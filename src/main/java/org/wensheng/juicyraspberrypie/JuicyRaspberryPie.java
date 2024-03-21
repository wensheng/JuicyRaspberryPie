package org.wensheng.juicyraspberrypie;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("PMD.CommentRequired")
public class JuicyRaspberryPie extends JavaPlugin implements Listener {
	private final Logger logger = Logger.getLogger("Minecraft");

	private ServerListenerThread serverThread;

	private final List<RemoteSession> sessions = new ArrayList<>();

	public JuicyRaspberryPie() {
		super();
	}

	private void saveResources() {
		final File pyInitFile = new File(getDataFolder(), "config.yml");
		if (!pyInitFile.exists()) {
			this.saveResource("config.yml", false);
		}

		final File svrFolder = new File(getDataFolder(), "cmdsvr");
		if (svrFolder.exists() || svrFolder.mkdir()) {
			this.saveResource("cmdsvr/pycmdsvr.py", true);
		} else {
			logger.warning("Could not create cmdsvr directory in plugin.");
		}

		final File mcpiFolder = new File(getDataFolder(), "mcpi");
		if (mcpiFolder.exists() || mcpiFolder.mkdir()) {
			this.saveResource("mcpi/__init__.py", true);
			this.saveResource("mcpi/connection.py", true);
			this.saveResource("mcpi/event.py", true);
			this.saveResource("mcpi/minecraft.py", true);
			this.saveResource("mcpi/util.py", true);
			this.saveResource("mcpi/vec3.py", true);
		} else {
			logger.warning("Could not create mcpi directory in plugin.");
		}

		final File ppluginsFolder = new File(getDataFolder(), "pplugins");
		if (ppluginsFolder.exists() || ppluginsFolder.mkdir()) {
			this.saveResource("pplugins/README.txt", true);
			this.saveResource("pplugins/examples.py", true);
		} else {
			logger.warning("Could not create pplugins directory in plugin.");
		}
	}

	@Override
	@SuppressWarnings("PMD.DoNotUseThreads")
	public void onEnable() {
		this.saveDefaultConfig();
		final int port = this.getConfig().getInt("api_port");

		//create new tcp listener thread
		try {
			serverThread = new ServerListenerThread(this, new InetSocketAddress(port));
			new Thread(serverThread).start();
			logger.info("ThreadListener Started");
		} catch (final IOException e) {
			logger.log(Level.WARNING, "Failed to start ThreadListener", e);
			return;
		}
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(), 1, 1);
		final boolean startPyserver = this.getConfig().getBoolean("start_cmdsvr");

		this.saveResources();

		if (startPyserver) {
			final String pyexe = getConfig().getString("pyexe", "python.exe");
			logger.info("Starting Python command server using " + pyexe);
			final ProcessBuilder processBuilder = new ProcessBuilder(pyexe, "cmdsvr/pycmdsvr.py");
			try {
				processBuilder.redirectErrorStream(true);
				processBuilder.directory(this.getDataFolder());
				processBuilder.start();
			} catch (final IOException e) {
				logger.warning("******************************************************************************");
				logger.warning("Could not start python command server! Please check your `pyexe` in config.yml");
				logger.warning("******************************************************************************");
			}
		}

	}

	@Override
	public void onDisable() {
		int port = this.getConfig().getInt("cmdsvr_port");
		// cmdsvr_host is not used, always "localhost" for now
		final boolean startPyserver = this.getConfig().getBoolean("start_cmdsvr");
		if (port == 0) {
			port = 32_123;
		}

		if (startPyserver) {
			try (
					Socket socket = new Socket("localhost", port);
					DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream())) {
				toPyServer.writeUTF("BYE");
				logger.info("ask py server to shut itself down");
			} catch (final IOException e) {
				logger.warning("Could not send shutdown signal to python command server, please shutdown manually.");
			}
		}

		getServer().getScheduler().cancelTasks(this);
		for (final RemoteSession session : sessions) {
			session.close();
		}
		serverThread.close();

		serverThread = null;
	}

	@Override
	@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, final String[] args) {
		if (args.length == 0) {
			return false;
		}

		int port = this.getConfig().getInt("cmdsvr_port");
		if (port == 0) {
			port = 4731;
		}

		final String cmdString;
		try (
				Socket socket = new Socket("localhost", port);
				DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream());
				BufferedReader fromPyServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
			final String cmdLine = String.join(" ", args);
			toPyServer.writeUTF(cmdLine);
			cmdString = fromPyServer.readLine();
			logger.info("the py server send back:|" + cmdString + "|");
			if (!"ok".equals(cmdString)) {
				sender.sendMessage(cmdString);
			}
		} catch (final IOException e) {
			sender.sendMessage("command server not available.");
		}
		return true;
	}

	private class TickHandler implements Runnable {
		public TickHandler() {
		}

		@Override
		public void run() {
			final Iterator<RemoteSession> remoteSessionIterator = sessions.iterator();
			while (remoteSessionIterator.hasNext()) {
				final RemoteSession session = remoteSessionIterator.next();
				if (session.isPendingRemoval()) {
					session.close();
					remoteSessionIterator.remove();
				} else {
					session.tick();
				}
			}
		}
	}

	/**
	 * called when a new session is established.
	 */
	public void handleConnection(final RemoteSession newSession) {
		if (checkBanned(newSession)) {
			logger.warning("Kicking " + newSession.getSocket().getRemoteSocketAddress() + " because the IP address has been banned.");
			newSession.kick("You've been banned from this server!");
			return;
		}
		synchronized (sessions) {
			sessions.add(newSession);
		}
	}

	private boolean checkBanned(final RemoteSession session) {
		final Set<String> ipBans = getServer().getIPBans();
		final String sessionIp = session.getSocket().getInetAddress().getHostAddress();
		return ipBans.contains(sessionIp);
	}

}
