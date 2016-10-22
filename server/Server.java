package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

	private static int port = 18409;
	private static HashMap<String, String> map = null;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		map = new HashMap<String, String>();
		
		ServerSocket server = null;
		server = new ServerSocket(port);
		Socket socket = null;
		while (true) {
			try {
				socket = server.accept();
				// 使用accept()阻塞等待客户请求，有客户
				// 请求到来则产生一个Socket对象，并继续执行
				new TCPServerThread(socket, map);
			} catch (Exception e) {
				System.out.println("Error." + e);
				// 出错，打印出错信息
			} 
		}
	}

}
