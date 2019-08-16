package org.wensheng.juicyraspberrypie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListenerThread implements Runnable {
	public ServerSocket serverSocket;
	private ApiHandler apiHandler;
	public SocketAddress bindAddress;
	public boolean running = true;
	private static final Logger LOGGER = LogManager.getLogger();

	public ServerListenerThread(ApiHandler apiHandler, SocketAddress bindAddress) throws IOException {
		this.apiHandler = apiHandler;
		this.bindAddress = bindAddress;
		serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(bindAddress);
	}

	public void run() {
		BufferedReader reader = null;
		PrintWriter writer = null;
		String recvStr;
		while (running) {
			try {
				Socket newConnection = serverSocket.accept();
				if (!running) return;
				LOGGER.info("got new connection");
				reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
				writer = new PrintWriter(newConnection.getOutputStream());
				while((recvStr = reader.readLine()) != null){
					apiHandler.process(writer, recvStr);
				}
			} catch (IOException  e) {
				//e.printStackTrace();
			}
		}
		try {
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
