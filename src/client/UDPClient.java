package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.PrimitiveIterator.OfDouble;

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
			
			//generate datagram socket
			socket = new DatagramSocket();
			while (in.hasNext()) {
				
				//receive input
				String message = in.nextLine();
				try {
					Operation operation = new Operation(message);
					message = operation.toString();
				} catch (Exception e) {
					logger.append("[ERROR] " + e.getMessage());
					continue;
				}
				byte[] m = message.getBytes();

				// create datagram packet
				DatagramPacket request = new DatagramPacket(m, message.length(), host, port);

				int retryCount = 0;
				socket.setSoTimeout(2000);
				
				//retry loop
				while (retryCount < retry) {
					try {
						
						//send packet to server using UDP
						socket.send(request);
						logger.append("[INFO] send request \"" + message + "\" to " + hostname + ":" + port);
					} catch (Exception e) {
						++retryCount;
						logger.append("[ERROR] UDP send packet exception: " + e.getMessage() + ", retry " + retryCount
								+ "...");
						System.out
								.println("send packet exception: " + e.getMessage() + ", retry " + retryCount + "...");
						continue;
					}

					// receive reply
					byte[] buffer = new byte[1024];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					String response = null;

					try {
						socket.receive(reply);
						logger.append("[INFO] received response from " + hostname);
						
						// analyze response
						response = new String(reply.getData()).trim();
						String[] data = response.split(" ", 2);
						//System.out.println(response);
						if (data[0].equals("-1") && data.length == 2) {
							logger.append("[INFO] request \"" + message + "\" failed, message: " + data[1]);
							//System.out.println(data[1]);
						} else if (data[0].equals("0")) {
							if (data.length == 2) {
								logger.append("[INFO] request \"" + message + "\" succeed, result: " + data[1]);
								//System.out.println("succeed, result: " + data[1]);
							} else {
								logger.append("[INFO] request \"" + message + "\" succeed");
								//System.out.println("succeed");

							}
						} else {
							++retryCount;
							logger.append("[ERROR] received malformed response from " + hostname + ", retry "
									+ retryCount + "...");
							System.out.println(
									"received malformed response from " + hostname + ", retry " + retryCount + "...");
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
						logger.append("[ERROR] UDP receive packet exception: " + e.getMessage() + ", retry "
								+ retryCount + "...");
						// System.out.println("UDP receive packet exception: " +
						// e.getMessage() + ", retry " + retryCount + "...");
						e.printStackTrace();
						continue;
					}
				}
				if (retryCount == retry) {
					logger.append("[ERROR] Reached max retry count, client is closing...");
					System.out.println("Reached max retry count, client is closing...");
					System.exit(-1);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
