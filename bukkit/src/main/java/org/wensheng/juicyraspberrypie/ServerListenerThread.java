package org.wensheng.juicyraspberrypie;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;

public class ServerListenerThread implements Runnable {
    ServerSocket serverSocket;

    boolean running = true;

    private final JuicyRaspberryPie plugin;

    ServerListenerThread(final JuicyRaspberryPie plugin, final SocketAddress bindAddress) throws IOException {
        this.plugin = plugin;
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(bindAddress);
    }

    public void run() {
        while (running) {
            try {
                final Socket newConnection = serverSocket.accept();
                if (!running) return;
                plugin.handleConnection(new RemoteSession(plugin, newConnection));
            } catch (Exception e) {
                // if the server thread is still running, raise an error
                if (running) {
                    plugin.getLogger().log(Level.WARNING, "Error creating new connection", e);
                }
            }
        }
        try {
            serverSocket.close();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error closing server socket", e);
        }
    }
}
