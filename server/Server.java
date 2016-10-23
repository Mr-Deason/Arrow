package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import common.Logger;

public class Server {

	private static int port = 18409;
	private static HashMap<String, String> map = null;

	private static Logger logger = null;
	
	public static void main(String[] args) throws IOException {

		map = new HashMap<String, String>();
		logger = new Logger("./server/server.log");
		
		new TCPServer(port, map, logger);
		new UDPServer(port, map, logger);
		new RPCServer(port+1, map, logger);
	}

}
