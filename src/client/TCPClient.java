package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import common.Logger;
import common.Operation;

public class TCPClient {

	private String hostname;
	private int port;
	
	private Scanner in;
	
	private Logger logger;
	
	public TCPClient(String hostname, int port, Logger logger, Scanner in) {
		this.hostname = hostname;
		this.port = port;
		this.logger = logger;
		this.in = in;
		
		try {
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() throws IOException{
		
		Socket socket = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;

		try{
			logger.append("[INFO] TCP client started");
			logger.append("[INFO] connect to " + hostname + ':' + port + "...");
			socket = new Socket(hostname, port);
			logger.append("[INFO] connect successfully!");
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			while (in.hasNextLine()) {
				String str = in.nextLine();
				try {
					Operation op = new Operation(str);
					str = op.toString();
				}catch (Exception e) {
					logger.append("[ERROR] " + e.getMessage());
					continue;
				}
				writer.write(str+"\n");
				writer.flush();
				logger.append("[INFO] send request \"" + str + "\" to server");
				String res = reader.readLine();
				logger.append("[INFO] receive response \"" + res + "\" from server");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
