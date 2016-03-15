package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import model.Service;

public class ServiceImpl extends UnicastRemoteObject implements Service {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4360731150443141723L;

	protected ServiceImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	File file_1 = new File("server_log.txt");
	File file_2 = new File("server.txt");
	File file_3 = new File("maxn.txt");
	File file_4 = new File("accept.txt");
	public static HashMap<String, Integer> set = new HashMap<String, Integer>();

	public synchronized String communication(String array) {
		String ret = "";
		try {
			String[] arr1 = array.split("\r\n");
			BufferedReader reader_1 = new BufferedReader(new FileReader(file_2));
			getstoredvalue(reader_1);
			FileWriter fileWritter_1 = new FileWriter(file_1.getName(), true);
			BufferedWriter bufferWritter_1 = new BufferedWriter(fileWritter_1);
			for (int i = 0; i < arr1.length; i++) {
				String str2 = arr1[i];
				if (str2.equals("END"))
					break;
				String[] arr2 = str2.split("\\s+");
				if ((arr2[0].equals("PUT") || arr2[0].equals("put")) && arr2.length == 3) {
					String key = arr2[1];
					int value = Integer.valueOf(arr2[2]);
					set.put(key, value);
					ret += "(" + key + "," + value + ") has been stored" + "\r\n";
					Date date = new Date();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(date);
					bufferWritter_1.write("(" + key + "," + value + ") has been stored in " + time + "\r\n");
				} else if ((arr2[0].equals("GET") || arr2[0].equals("get")) && arr2.length == 2) {
					String key = arr2[1];
					if (!set.containsKey(key)) {
						ret += "There is not a key " + key + "\r\n";
						continue;
					}
					int value = set.get(key);
					ret += "The value of key " + key + " is " + value + "\r\n";
					Date date = new Date();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(date);
					bufferWritter_1.write("There is a request of key " + key + " in " + time + "\r\n");
				} else if ((arr2[0].equals("DELETE") || arr2[0].equals("delete") && arr2.length == 2)) {
					String key = arr2[1];
					if (!set.containsKey(key)) {
						ret += "There is not a key " + key + "\r\n";
						continue;
					}
					set.remove(key);
					ret += "The key " + key + " has been deleted + \r\n";
					Date date = new Date();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(date);
					bufferWritter_1.write("The key " + key + " has been deleted in " + time + "\r\n");
				} else {
					ret += "Error of message format:" + str2 + "\r\n";
					Date date = new Date();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = format.format(date);
					bufferWritter_1.write("There is a request with wrong message format in " + time + "\r\n");
				}
			}
			ret += "END";
			bufferWritter_1.close();
			FileWriter fileWritter_2 = new FileWriter(file_2.getName());
			BufferedWriter bufferWritter_2 = new BufferedWriter(fileWritter_2);
			updatestoredvalue(bufferWritter_2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void getstoredvalue(BufferedReader reader) throws IOException {
		String line = null;
		while (true) {
			line = reader.readLine();
			if (line == null || line.equals("END"))
				break;
			String[] arr = line.split("\\s+");
			set.put(arr[0], Integer.valueOf(arr[1]));
		}
		reader.close();
	}

	public static void updatestoredvalue(BufferedWriter bufferWritter) throws IOException {
		for (String key : set.keySet()) {
			String value = set.get(key).toString();
			bufferWritter.write(key + " " + value + "\r\n");
		}
		bufferWritter.write("END");
		bufferWritter.close();
	}

	public synchronized String phase_1(int K) {
		String s = null;
		try {
			BufferedReader reader_1 = new BufferedReader(new FileReader(file_3));
			String line_1 = reader_1.readLine();
			if (line_1 == null) {
				s = "OK" + " null " + "null";
				reader_1.close();
				return s;
			}
			int max_n = Integer.valueOf(line_1);
			if (max_n > K) {
				reader_1.close();
				return "error";
			}
			reader_1.close();
			BufferedWriter out = new BufferedWriter(new FileWriter(file_3));
			out.write(String.valueOf(K));
			out.close();
			BufferedReader reader_2 = new BufferedReader(new FileReader(file_4));
			String line_2 = reader_2.readLine();
			if (line_2 == null) {
				s = "OK" + " null " + "null";
				reader_2.close();
				return s;
			}
			String[] arr = line_2.split("\\s+");
			String accept_n = arr[0];
			String accept_v = "";
			for (int i = 1; i < arr.length; i++) {
				accept_v += arr[i];
			}
			s = "OK " + accept_n + " " + accept_v;
			reader_2.close();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public synchronized String phase_2(int K, String accept_v) {
		String line_1 = null;
		try {
			BufferedReader reader_1 = new BufferedReader(new FileReader(file_3));
			line_1 = reader_1.readLine();
			if (line_1 != null) {
				int max_n = Integer.valueOf(line_1);
				if (max_n > K) {
					reader_1.close();
					return "error";
				}
			}
			reader_1.close();
			BufferedWriter out_1 = new BufferedWriter(new FileWriter(file_2));
			out_1.write(accept_v);
			out_1.close();
			BufferedWriter out_2 = new BufferedWriter(new FileWriter(file_3));
			out_2.write(String.valueOf(K));
			out_2.close();
			BufferedWriter out_3 = new BufferedWriter(new FileWriter(file_4));
			out_3.write(String.valueOf(K) + " " + accept_v);
			out_3.close();
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "OK";
	}

}
