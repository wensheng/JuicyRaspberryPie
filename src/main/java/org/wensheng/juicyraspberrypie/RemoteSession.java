package org.wensheng.juicyraspberrypie;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.LocationParser;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods"})
class RemoteSession {
	private static final int MAX_COMMANDS_PER_TICK = 9000;

	private boolean pendingRemoval;

	private final Socket socket;

	@SuppressWarnings("PMD.ShortVariable")
	private BufferedReader in;

	private BufferedWriter out;

	@SuppressWarnings("PMD.DoNotUseThreads")
	private Thread inThread;

	@SuppressWarnings("PMD.DoNotUseThreads")
	private Thread outThread;

	private final Deque<String> inQueue = new ArrayDeque<>();

	private final Deque<String> outQueue = new ArrayDeque<>();

	private boolean running = true;

	private final JuicyRaspberryPie plugin;

	private final Logger logger;

	private final LocationParser locationParser;

	private final SessionAttachment attachment;

	@SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
	public RemoteSession(final JuicyRaspberryPie plugin, final Socket socket) throws IOException {
		this.socket = socket;
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		init();

		attachment = new SessionAttachment(plugin.getServer());
		attachment.setPlayerAndOrigin();
		locationParser = new LocationParser(attachment);
		startEventQueues(attachment);
	}

	private void init() throws IOException {
		socket.setTcpNoDelay(true);
		socket.setKeepAlive(true);
		socket.setTrafficClass(0x10);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
		this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
		startThreads();
		logger.log(Level.INFO, "Opened connection to" + socket.getRemoteSocketAddress() + ".");

	}

	@SuppressWarnings("PMD.DoNotUseThreads")
	private void startThreads() {
		inThread = new Thread(new InputThread());
		inThread.start();
		outThread = new Thread(new OutputThread());
		outThread.start();
	}

	public Socket getSocket() {
		return socket;
	}

	/**
	 * called from the server main thread
	 */
	public void tick() {
		int processedCount = 0;
		while (!inQueue.isEmpty()) {
			handleLine(inQueue.poll());
			processedCount++;
			if (processedCount >= MAX_COMMANDS_PER_TICK) {
				logger.log(Level.WARNING, "Over " + MAX_COMMANDS_PER_TICK
						+ " commands were queued - deferring " + inQueue.size() + " to next tick");
				break;
			}
		}

		if (!running && inQueue.isEmpty()) {
			pendingRemoval = true;
		}
	}

	private void handleLine(final String line) {
		final String trimmedLine = line.trim();
		if (!trimmedLine.contains("(") || !trimmedLine.endsWith(")")) {
			send("Wrong format");
			return;
		}
		final String methodName = trimmedLine.substring(0, trimmedLine.indexOf('('));
		final String methodArgs = trimmedLine.substring(trimmedLine.indexOf('(') + 1, trimmedLine.length() - 1);
		String[] args = methodArgs.split(",", -1);
		args = ArrayUtils.remove(args, args.length - 1);
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];
			if (arg.isEmpty()) {
				args[i] = null;
			}
		}
		handleCommand(methodName, args);
	}

	private void handleCommand(final String command, final String... args) {
		final Handler handler = plugin.getRegistry().getHandler(command);
		if (handler != null) {
			send(handler.get(attachment, new Instruction(args, locationParser)));
			return;
		}
		plugin.getLogger().warning(command + " is not supported.");
		send("Fail");
	}

	private void send(final String message) {
		if (pendingRemoval) {
			return;
		}
		synchronized (outQueue) {
			outQueue.add(message);
		}
	}

	public void close() {
		running = false;
		pendingRemoval = true;

		stopEventQueues(attachment);

		//wait for threads to stop
		try {
			inThread.join(2000);
			outThread.join(2000);
		} catch (final InterruptedException e) {
			logger.log(Level.WARNING, "Failed to stop in/out thread", e);
		}

		try {
			socket.close();
		} catch (final IOException e) {
			logger.log(Level.WARNING, "Failed to close socket", e);
		}
		logger.log(Level.INFO, "Closed connection to" + socket.getRemoteSocketAddress() + ".");
	}

	public boolean isPendingRemoval() {
		return pendingRemoval;
	}

	public void kick(final String reason) {
		try {
			out.write(reason);
			out.flush();
		} catch (final IOException e) {
			logger.log(Level.FINE, "Failed to send kick reason", e);
		}
		close();
	}

	/**
	 * socket listening thread
	 */
	private class InputThread implements Runnable {
		public InputThread() {
		}

		@Override
		public void run() {
			logger.log(Level.INFO, "Starting input thread");
			while (running) {
				try {
					final String newLine = in.readLine();
					if (newLine == null) {
						running = false;
					} else {
						inQueue.add(newLine);
					}
				} catch (final IOException e) {
					if (running) {
						logger.log(Level.WARNING, "Error occurred in input thread", e);
						running = false;
					}
				}
			}
			try {
				in.close();
			} catch (final IOException e) {
				logger.log(Level.WARNING, "Failed to close in buffer", e);
			}
		}
	}

	private class OutputThread implements Runnable {
		public OutputThread() {
		}

		@SuppressWarnings("PMD.DoNotUseThreads")
		@Override
		public void run() {
			logger.log(Level.INFO, "Starting output thread!");
			while (running) {
				try {
					while (!outQueue.isEmpty()) {
						out.write(outQueue.poll());
						out.write('\n');
					}
					out.flush();
					Thread.yield();
					Thread.sleep(1L);
				} catch (final IOException | InterruptedException e) {
					if (running) {
						logger.log(Level.WARNING, "Error occurred in output thread", e);
						running = false;
					}
				}
			}
			//close out buffer
			try {
				out.close();
			} catch (final IOException e) {
				logger.log(Level.WARNING, "Failed to close out buffer", e);
			}
		}
	}

	private void startEventQueues(@NotNull final SessionAttachment sessionAttachment) {
		plugin.getRegistry().getHandlers().forEach(handler -> startEventQueue(sessionAttachment, handler));
	}

	private void startEventQueue(@NotNull final SessionAttachment sessionAttachment, @NotNull final Handler handler) {
		handler.createEventQueue().ifPresent(eventQueue -> {
			plugin.getServer().getPluginManager().registerEvents(eventQueue, plugin);
			sessionAttachment.setEventQueue(handler, eventQueue);
		});
	}

	private void stopEventQueues(@NotNull final SessionAttachment sessionAttachment) {
		plugin.getRegistry().getHandlers().forEach(handler -> stopEventQueue(sessionAttachment, handler));
	}

	private void stopEventQueue(@NotNull final SessionAttachment sessionAttachment, @NotNull final Handler handler) {
		sessionAttachment.getEventQueue(handler).ifPresent(HandlerList::unregisterAll);
	}
}
