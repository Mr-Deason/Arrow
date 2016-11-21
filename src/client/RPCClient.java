package client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import javax.swing.LookAndFeel;

import common.Logger;
import common.Operation;
import interfaces.RPCInterf;

public class RPCClient {

	private String hostname;
	private int port;
	
	private Logger logger;
	
	private Scanner in;
	
	public RPCClient(String hostname, int port, Logger logger, Scanner in) {
		this.hostname = hostname;
		this.port = port;
		this.logger = logger;
		this.in = in;
		
		start();
	}


	public void start() { 
		try {

			logger.append("[INFO] RPC client started");
			logger.append("[INFO] look for RPC interface in \"rmi://" + hostname + ':' + port + "/RPCServer\"...");
			
			//look for RPC interface
			RPCInterf server = (RPCInterf) Naming.lookup("rmi://" + hostname + ':' + port + "/RPCServer");
			
			logger.append("[INFO] get interface successfully");
			
			while (in.hasNextLine()) {
				String str = in.nextLine();
				if (str.trim().equals("")) {
					continue;
				}
				try {
					//generate operation
					Operation op = new Operation(str);
					
					//invoke remote method
					String res = server.request(op.toString());
//					logger.append("[INFO] server respond: " + res);
				}catch (Exception e) {
					try {
						logger.append("[ERROR] " + e.getMessage());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					continue;
				}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			try {
				logger.append("[ERROR] " + e.getMessage());
			} catch (IOException e1) {
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
}
