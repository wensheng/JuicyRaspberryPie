package org.wensheng.juicyraspberrypie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerListenerThread implements Runnable {
	private ServerSocket serverSocket;
	private ApiHandler apiHandler;
	final AtomicBoolean running = new AtomicBoolean(true);

	ServerListenerThread(ApiHandler apiHandler, ServerSocket serverSocket) {
		this.apiHandler = apiHandler;
        this.serverSocket = serverSocket;
	}

	public void run() {
		BufferedReader reader;
		PrintWriter writer;
		String recvStr;
		while (running.get()) {
			try {
				Socket newConnection = serverSocket.accept();
				if (!running.get()) return;
				reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
				writer = new PrintWriter(newConnection.getOutputStream());
				while((recvStr = reader.readLine()) != null){
					apiHandler.process(writer, recvStr);
				}
			} catch (IOException  e) {
				//e.printStackTrace();
			}
		}
	}
}
