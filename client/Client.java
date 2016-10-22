package client;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;



import common.Logger;
import common.Operation;

public class Client {

	Logger logger = null;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//System.out.println(args[0] + "," + args[1]);
		Client client = new Client();
		client.TCPClient("127.0.0.1", 18409);
		
	}
	
	public void TCPClient(String hostname, int port) throws IOException{
		
		Socket socket = null;
		BufferedReader reader = null;
		Scanner in = null;
		BufferedWriter writer = null;

		logger = new Logger("./client/client.log");
		doBeforeQuit();
		try{
			logger.append(new Date(), "[INFO] connect to " + hostname + ':' + port + "...");
			socket = new Socket(hostname, port);
			logger.append(new Date(), "[INFO] connect successfully!");
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new Scanner(System.in);
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
//			Scanner scan = new Scanner(new File("./script.txt"));
//			while (scan.hasNextLine()) {
//				String str = scan.nextLine();
//				if (isCmdValid(str)) {
//					writer.write(str);
//					writer.flush();
//					System.out.println(reader.readLine());
//				}
//			}
			while (in.hasNextLine()) {
				String str = in.nextLine();
				if (str.trim().equals("")) {
					continue;
				}
				try {
					Operation op = new Operation(str);
				}catch (Exception e) {
					logger.append(new Date(), "[ERROR] " + e.getMessage());
					continue;
				}
				writer.write(str+"\n");
				writer.flush();
				logger.append(new Date(), "[INFO] send request \"" + str + "\" to server");
				String res = reader.readLine();
				logger.append(new Date(), "[INFO] receive response \"" + res + "\" from server");
				System.out.println(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void doBeforeQuit() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		@Override
		public void run() {
			try {
				logger.append(new Date(), "client quit.");
				logger.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		});
	}

}
