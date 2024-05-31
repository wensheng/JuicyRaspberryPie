package org.wensheng.juicyraspberrypie;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.ArrayUtils;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.LocationParser;
import org.wensheng.juicyraspberrypie.command.Registry;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.entity.EntityByPlayerNameProvider;
import org.wensheng.juicyraspberrypie.command.entity.EntityByUUIDProvider;
import org.wensheng.juicyraspberrypie.command.handlers.GetPlayer;
import org.wensheng.juicyraspberrypie.command.handlers.SetPlayer;
import org.wensheng.juicyraspberrypie.command.handlers.chat.Post;
import org.wensheng.juicyraspberrypie.command.handlers.entity.DisableControl;
import org.wensheng.juicyraspberrypie.command.handlers.entity.EnableControl;
import org.wensheng.juicyraspberrypie.command.handlers.entity.GetDirection;
import org.wensheng.juicyraspberrypie.command.handlers.entity.GetPitch;
import org.wensheng.juicyraspberrypie.command.handlers.entity.GetPos;
import org.wensheng.juicyraspberrypie.command.handlers.entity.GetRotation;
import org.wensheng.juicyraspberrypie.command.handlers.entity.GetTile;
import org.wensheng.juicyraspberrypie.command.handlers.entity.Remove;
import org.wensheng.juicyraspberrypie.command.handlers.entity.SetDirection;
import org.wensheng.juicyraspberrypie.command.handlers.entity.SetPitch;
import org.wensheng.juicyraspberrypie.command.handlers.entity.SetPos;
import org.wensheng.juicyraspberrypie.command.handlers.entity.SetRotation;
import org.wensheng.juicyraspberrypie.command.handlers.entity.SetTile;
import org.wensheng.juicyraspberrypie.command.handlers.entity.WalkTo;
import org.wensheng.juicyraspberrypie.command.handlers.events.Clear;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;
import org.wensheng.juicyraspberrypie.command.handlers.events.chat.Posts;
import org.wensheng.juicyraspberrypie.command.handlers.world.GetBlock;
import org.wensheng.juicyraspberrypie.command.handlers.world.GetBlockWithData;
import org.wensheng.juicyraspberrypie.command.handlers.world.GetBlocks;
import org.wensheng.juicyraspberrypie.command.handlers.world.GetHeight;
import org.wensheng.juicyraspberrypie.command.handlers.world.GetNearbyEntities;
import org.wensheng.juicyraspberrypie.command.handlers.world.GetPlayerId;
import org.wensheng.juicyraspberrypie.command.handlers.world.GetPlayerIds;
import org.wensheng.juicyraspberrypie.command.handlers.world.IsBlockPassable;
import org.wensheng.juicyraspberrypie.command.handlers.world.SetBlock;
import org.wensheng.juicyraspberrypie.command.handlers.world.SetBlocks;
import org.wensheng.juicyraspberrypie.command.handlers.world.SetPowered;
import org.wensheng.juicyraspberrypie.command.handlers.world.SetSign;
import org.wensheng.juicyraspberrypie.command.handlers.world.SpawnEntity;
import org.wensheng.juicyraspberrypie.command.handlers.world.SpawnParticle;

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

@SuppressWarnings("PMD.CommentRequired")
class RemoteSession {
	private static final int MAX_COMMANDS_PER_TICK = 9000;

	private final Registry registry;

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

	@SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
	public RemoteSession(final JuicyRaspberryPie plugin, final Socket socket) throws IOException {
		this.socket = socket;
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		init();
		registry = new Registry();

		final SessionAttachment attachment = new SessionAttachment(plugin.getServer());
		attachment.setPlayerAndOrigin();
		locationParser = new LocationParser(attachment);
		setupRegistry(attachment);
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
		final Handler handler = registry.getHandler(command);
		if (handler != null) {
			send(handler.get(new Instruction(args, locationParser)));
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

		teardownRegistry();

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

	private void setupRegistry(final SessionAttachment attachment) {
		final EntityByPlayerNameProvider playerEntityProvider = new EntityByPlayerNameProvider(attachment);
		final EntityByUUIDProvider entityProvider = new EntityByUUIDProvider(plugin.getServer());

		registry.register("getPlayer", new GetPlayer(attachment));
		registry.register("setPlayer", new SetPlayer(attachment));
		registry.register("world.getBlock", new GetBlock());
		registry.register("world.getBlocks", new GetBlocks());
		registry.register("world.getBlockWithData", new GetBlockWithData());
		registry.register("world.setBlock", new SetBlock());
		registry.register("world.setBlocks", new SetBlocks());
		registry.register("world.isBlockPassable", new IsBlockPassable());
		registry.register("world.setPowered", new SetPowered());
		registry.register("world.getPlayerIds", new GetPlayerIds(plugin.getServer()));
		registry.register("world.getPlayerId", new GetPlayerId());
		registry.register("world.setSign", new SetSign());
		registry.register("world.getNearbyEntities", new GetNearbyEntities());
		registry.register("world.spawnEntity", new SpawnEntity());
		registry.register("world.spawnParticle", new SpawnParticle());
		registry.register("world.getHeight", new GetHeight());
		registry.register("chat.post", new Post(plugin.getServer()));
		registry.register("events.block.hits", new org.wensheng.juicyraspberrypie.command.handlers.events.block.Hits(plugin));
		registry.register("events.projectile.hits", new org.wensheng.juicyraspberrypie.command.handlers.events.projectile.Hits(plugin));
		registry.register("events.chat.posts", new Posts(plugin));
		registry.register("events.clear", new Clear(registry));
		registry.register("player.getTile", new GetTile(playerEntityProvider));
		registry.register("entity.getTile", new GetTile(entityProvider));
		registry.register("player.setTile", new SetTile(playerEntityProvider));
		registry.register("entity.setTile", new SetTile(entityProvider));
		registry.register("player.getPos", new GetPos(playerEntityProvider));
		registry.register("entity.getPos", new GetPos(entityProvider));
		registry.register("player.setPos", new SetPos(playerEntityProvider));
		registry.register("entity.setPos", new SetPos(entityProvider));
		registry.register("player.getDirection", new GetDirection(playerEntityProvider));
		registry.register("entity.getDirection", new GetDirection(entityProvider));
		registry.register("player.setDirection", new SetDirection(playerEntityProvider));
		registry.register("entity.setDirection", new SetDirection(entityProvider));
		registry.register("player.getRotation", new GetRotation(playerEntityProvider));
		registry.register("entity.getRotation", new GetRotation(entityProvider));
		registry.register("player.setRotation", new SetRotation(playerEntityProvider));
		registry.register("entity.setRotation", new SetRotation(entityProvider));
		registry.register("player.getPitch", new GetPitch(playerEntityProvider));
		registry.register("entity.getPitch", new GetPitch(entityProvider));
		registry.register("player.setPitch", new SetPitch(playerEntityProvider));
		registry.register("entity.setPitch", new SetPitch(entityProvider));
		registry.register("entity.enableControl", new EnableControl(plugin, entityProvider));
		registry.register("entity.disableControl", new DisableControl(plugin, entityProvider));
		registry.register("entity.walkTo", new WalkTo(entityProvider));
		registry.register("entity.remove", new Remove(entityProvider));
		registry.register("player.performCommand", new org.wensheng.juicyraspberrypie.command.handlers.player.PerformCommand(attachment));
		registry.register("console.performCommand", new org.wensheng.juicyraspberrypie.command.handlers.console.PerformCommand(
				plugin.getLogger(), plugin.getConfig().getStringList("console-command-whitelist")));

		registry.getHandlers().stream()
				.filter(handler -> handler instanceof EventQueue<?>)
				.map(handler -> (EventQueue<?>) handler)
				.forEach(EventQueue::start);
	}

	private void teardownRegistry() {
		registry.getHandlers().stream()
				.filter(handler -> handler instanceof EventQueue<?>)
				.map(handler -> (EventQueue<?>) handler)
				.forEach(EventQueue::stop);
	}
}
