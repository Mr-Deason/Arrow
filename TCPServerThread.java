import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

public class TCPServerThread extends Thread {

	Socket socket;
	HashMap<String, String> map;

	public TCPServerThread(Socket socket, HashMap<String, String> map) {
		this.socket = socket;
		this.map = map;
		this.start();
	}

	@Override
	public void run() {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			while (true) {
				String line = reader.readLine();
				if (line.trim().equals("")) {
					continue;
				}
				System.out.println("Client: " + line);
				String res = operation(line);
				System.out.println(res);
				writer.write(res + "\n");
				writer.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String operation(String str) {
		String[] args = str.split(" ");
		if (args.length == 2) {
			String key = args[1];
			String value = map.get(key);
			if (value == null) {
				return "not found";
			}
			if (args[0].equals("GET")) {
				return value;
			} else {
				map.remove(key);
				return "deleted";
			}
		} else {
			String key = args[1];
			String value = args[2];
			map.put(key, value);
			return "put";
		}
	}

}
