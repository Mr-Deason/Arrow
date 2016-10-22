package common;

import java.util.HashMap;

public class Operation {
	
	private String op = null;
	private String key = null;
	private String value = null;
	
	public Operation(String str) throws Exception {
		String args[] = str.split(" ");
		if (args.length < 2 || args.length > 3) {
			throw new Exception("malformed request!");
		}
		op = args[0];
		if (args.length == 2 ) {
			if (!op.toUpperCase().equals("GET") && !op.toUpperCase().equals("DELETE")) {
				throw new Exception("malformed request!");
			}
			key = args[1];
		} else {
			if (!op.toUpperCase().equals("PUT")) {
				throw new Exception("malformed request!");
			}
			key = args[1];
			value = args[2];
		}
	}
	
	public String exec(HashMap<String, String> map) throws Exception{
		if (op.toUpperCase().equals("GET")) {
			String res = map.get(key);
			if (res == null) {
				throw new Exception("not found!");
			}
			return "0 " + res;
		}
		if (op.toUpperCase().equals("PUT")) {
			map.put(key, value);
			return "0";
		}
		if (op.toUpperCase().equals("DELETE")) {
			String res = map.get(key);
			if (res == null) {
				throw new Exception("not found!");
			}
			map.remove(key);
			return "0";
		}
		return "0";
	}


	public String getOp() {
		return op;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
}
