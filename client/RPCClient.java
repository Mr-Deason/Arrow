package client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;


import common.Logger;
import common.Operation;
import server.RPCServerI;

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
			RPCServerI server = (RPCServerI) Naming.lookup("rmi://" + hostname + ':' + port + "/RPCServer");
			while (in.hasNextLine()) {
				String str = in.nextLine();
				if (str.trim().equals("")) {
					continue;
				}
				try {
					Operation op = new Operation(str);
					if (op.isGet()) {
						String res = server.get(op.getKey());
						if (res != null) {
							logger.append("[INFO] get value: \"" + res +'\"');
						}else {
							logger.append("[INFO] no such value!");
						}
					}else if (op.isDelete()) {
						int res = server.delete(op.getKey());
						logger.append("[INFO] operation result: " + res);
					}else {
						int res = server.put(op.getKey(), op.getValue());
						logger.append("[INFO] operation result: " + res);
					}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
