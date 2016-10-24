package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import common.Logger;
import common.Operation;

public class UDPClient {

	private String hostname;
	private int port;

	private Scanner in;

	private Logger logger;

	private int retry = 3;

	public UDPClient(String hostname, int port, Logger logger, Scanner in) {
		this.hostname = hostname;
		this.port = port;
		this.logger = logger;
		this.in = in;

		start();
	}

	public void start() {

		try {

			logger.append("[INFO] UDP client started");
			DatagramSocket socket = null;
			InetAddress host = InetAddress.getByName(hostname);
			socket = new DatagramSocket();
			while (in.hasNext()) {
				String message = in.nextLine();
				try {
					Operation operation = new Operation(message);
				} catch (Exception e) {
					logger.append("[ERROR] " + e.getMessage());
					System.out.println(e.getMessage());
					continue;
				}
				byte[] m = message.getBytes();

				// Send
				DatagramPacket request = new DatagramPacket(m, message.length(), host, port);

				int retryCount = 0;
				socket.setSoTimeout(2000);
				while (retryCount < retry) {
					try {
						socket.send(request);
						logger.append("[INFO] send request \"" + message + "\" to " + hostname + ":" + port);
					} catch (Exception e) {
						++retryCount;
						logger.append("[ERROR] UDP send packet exception: " + e.getMessage() + ", retry " + retryCount + "...");
						System.out.println("send packet exception: " + e.getMessage() + ", retry " + retryCount + "...");
						continue;
					}

					// Reply
					byte[] buffer = new byte[1024];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					String response = null;
					
					try {
						socket.receive(reply);
						logger.append("[INFO] received response from " + hostname);
						response = new String(reply.getData()).trim();
						String[] data = response.split(" ", 2);
						if (data[0].equals("-1") && data.length == 2) {
							logger.append("[INFO] request \"" + message + "\" failed, message: " + data[1]);
							System.out.println(data[1]);
						}else if (data[0].equals("0") && data.length == 2){
							logger.append("[INFO] request \"" + message + "\" succeed, result: " + data[1]);
							System.out.println("succeed, result: " + data[1]);
						}else {
							++retryCount;
							logger.append("[ERROR] received malformed response from " + hostname + ", retry " + retryCount + "...");
							System.out.println("received malformed response from " + hostname + ", retry " + retryCount + "...");
							continue;
						}
						break;
					} catch (SocketTimeoutException e) {
						++retryCount;
						logger.append("[ERROR] UDP receive packet timeout, retry " + retryCount + "...");
						System.out.println("UDP receive packet timeout, retry " + retryCount + "...");
						continue;
					} catch (Exception e) {
						++retryCount;
						logger.append("[ERROR] UDP receive packet exception: " + e.getMessage() + ", retry " + retryCount + "...");
						System.out.println("UDP receive packet exception: " + e.getMessage() + ", retry " + retryCount + "...");
						continue;
					}
				}
				if (retryCount == retry) {
					logger.append("[ERROR] Reached max retry count, client is closing...");
					System.out.println("Reached max retry count, client is closing...");
					Thread.sleep(1000);
					System.exit(-1);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
