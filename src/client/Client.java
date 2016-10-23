package client;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import common.Logger;

public class Client {

	private String hostname;
	private int port;
	private static HashMap<String, String> map = null;

	private Logger logger = null;

	public static void main(String[] args) throws IOException {

		String hostname = "127.0.0.1";
		int port = 18409;

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

		System.out.println("Enter a number to select protocol:");
		System.out.println("[1]TPC");
		System.out.println("[2]UDP");
		System.out.println("[3]RPC");
		while (true) {
			pChoice = in.nextInt();
			if (pChoice < 1 || pChoice > 3) {
				System.out.println("Wrrong input !");
				continue;
			}
			break;
		}

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
		
		if (pChoice == 1) {
			new TCPClient(hostname, port, logger, in);
		}else if (pChoice == 2) {
			new UDPClient(hostname, port, logger, in);
		}else {
			new RPCClient(hostname, port, logger, in);
		}

	}

	public void doBeforeQuit() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					logger.append("client quit.");
					logger.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
