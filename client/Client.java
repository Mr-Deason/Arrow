package client;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TCPClient("127.0.0.1", 18409);
		
	}
	
	public static void TCPClient(String hostname, int port){
		
		Socket socket = null;
		BufferedReader reader = null;
		Scanner in = null;
		BufferedWriter writer = null;
		
		
		try{
			socket = new Socket(hostname, port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			in = new Scanner(System.in);
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			Scanner scan = new Scanner(new File("./script.txt"));
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
				if (isCmdValid(str)) {
					writer.write(str+"\n");
					writer.flush();
					System.out.println(reader.readLine());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static boolean isCmdValid(String cmd) {
		return true;
	}

}
