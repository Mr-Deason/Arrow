package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import common.Logger;

public class UDPClient {

	private String hostname;
	private int port;
	
	private Scanner in;
	
	private Logger logger;
	
	public UDPClient(String hostname, int port, Logger logger, Scanner in) {
		this.hostname = hostname;
		this.port = port;
		this.logger = logger;
		this.in = in;
		
		start();
	}
	public void start() {
		
		try {

			DatagramSocket socket = null;
			socket = new DatagramSocket();
			while (in.hasNext()) {
				String message = in.nextLine();
				byte[] m = message.getBytes();
				InetAddress hostname = InetAddress.getByName("127.0.0.1");
				int port = 18409;

				// Send
				DatagramPacket request = new DatagramPacket(m,
						message.length(), hostname, port);
				socket.send(request);

				// Reply
				byte[] buffer = new byte[1024];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				socket.receive(reply);
				//System.out.println("reply:" + new String(reply.getData()).trim());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
