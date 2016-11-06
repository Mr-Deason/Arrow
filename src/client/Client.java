package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.crypto.Data;

import common.Logger;
import common.Operation;

public class Client {

	private String hostname;
	private int port;
	private static HashMap<String, String> map = null;

	private Logger logger = null;

	private Scanner in;
	
	private long beginTime;
	private long endTime;

	public static void main(String[] args) throws IOException {

		String hostname = "127.0.0.1";
		int port = 18409;

		//verify arguments
		if (args.length != 0 && args.length != 2) {
			System.out.println("client can only accept 0 or 2 arguments !");
			System.exit(-1);
		}
		if (args.length == 2) {
			hostname = args[0];
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.out.println("arguments format error !");
				System.exit(-1);
			}
		}
		
		Client client = new Client(hostname, port);
		client.begin();
	}

	public Client(String hostname, int port) {

		this.hostname = hostname;
		this.port = port;

		try {
			logger = new Logger("./client.log");
		} catch (IOException e) {
			e.printStackTrace();
		}
		doBeforeQuit();
	}

	public void begin() {

		int pChoice, iChoice;
		Scanner in = new Scanner(System.in);

		//let user select input source
		System.out.println("Enter a number to select the source of input:");
		System.out.println("[1]Console");
		System.out.println("[2]File");

		while (true) {
			iChoice = in.nextInt();
			if (iChoice < 1 || iChoice > 2) {
				System.out.println("Wrrong input !");
				continue;
			}
			break;
		}

		in.nextLine();
		if (iChoice == 2) {
			
			//user select to use file
			System.out.println("Enter the file name (default file \"./kvp-operations.csv\"):");
			while (true) {
				String filename = in.nextLine().trim();
				if (filename.equals("")) {
					filename = "./kvp-operations.csv";
				}
				System.out.println(filename);
				try {
					in = new Scanner(new File(filename));
					break;
				} catch (Exception e) {
					System.out.println("Open file failed, re-enter the file name:");
				}
			}
		}else {
			System.out.println("Enter operation:");
		}
		
		//record begin time
		beginTime = System.currentTimeMillis();

		this.in = in;
		try {
			start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() throws IOException {

		Socket socket = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;

		try {
			logger.append("[INFO] TCP client started");
			logger.append("[INFO] connect to " + hostname + ':' + port + "...");

			// connect to server using TCP socket
			socket = new Socket(hostname, port);
			logger.append("[INFO] connect successfully!");

			// get socket I/O stream
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			writer.write("operation\n");
			writer.flush();
			while (in.hasNextLine()) {
				// receive input and send to server
				String str = in.nextLine();
				try {
					Operation op = new Operation(str);
					str = op.toString();
				} catch (Exception e) {
					logger.append("[ERROR] " + e.getMessage());
					continue;
				}
				writer.write(str + "\n");
				writer.flush();
				logger.append("[INFO] send request \"" + str + "\" to server");

				// read response from server
				String res = reader.readLine();
				logger.append("[INFO] receive response \"" + res + "\" from server");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void doBeforeQuit() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				endTime = System.currentTimeMillis();
				try {
					logger.append("[INFO] client quit.");
					long usedTime = endTime - beginTime;
					logger.append("[INFO] this transaction totally used " + usedTime + " ms");
					System.out.println("client quit.");
					System.out.println("this transaction totally used " + usedTime + " ms");
					logger.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
