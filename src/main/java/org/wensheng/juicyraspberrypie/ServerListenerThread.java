package org.wensheng.juicyraspberrypie;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;

/**
 * A thread that listens for incoming connections and creates new sessions.
 */
public class ServerListenerThread implements Runnable {
	/**
	 * The server socket.
	 */
	private final ServerSocket serverSocket;

	/**
	 * The plugin.
	 */
	private final JuicyRaspberryPie plugin;

	/**
	 * Create a new server listener thread.
	 *
	 * @param plugin      The plugin.
	 * @param bindAddress The address to bind to.
	 * @throws IOException If the server socket cannot be created.
	 */
	@SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
	public ServerListenerThread(final JuicyRaspberryPie plugin, final SocketAddress bindAddress) throws IOException {
		this.plugin = plugin;
		serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(bindAddress);
	}

	/**
	 * Close the server socket.
	 */
	public void close() {
		try {
			serverSocket.close();
		} catch (final IOException e) {
			plugin.getLogger().log(Level.WARNING, "Error closing server socket", e);
		}
	}

	@Override
	@SuppressWarnings("PMD.PrematureDeclaration")
	public void run() {
		while (!serverSocket.isClosed()) {
			try (Socket newConnection = serverSocket.accept()) {
				if (serverSocket.isClosed()) {
					return;
				}
				plugin.handleConnection(new RemoteSession(plugin, newConnection));
			} catch (final IOException e) {
				if (!serverSocket.isClosed()) {
					plugin.getLogger().log(Level.WARNING, "Error creating new connection", e);
				}
			}
		}
		try {
			serverSocket.close();
		} catch (final IOException e) {
			plugin.getLogger().log(Level.WARNING, "Error closing server socket", e);
		}
	}
}
