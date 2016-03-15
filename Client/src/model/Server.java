package model;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Scanner;

import model.ServiceImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

public class Server {
	public static void main(String[] args) throws UnknownHostException {
		Scanner s = new Scanner(System.in);
		String str1 = null;
		String str2 = null;
		System.out.println("Please enter the IP address of the Server:");
		str1 = s.next();
		System.out.println("Please enter the port number of the Server:");
		str2 = s.next();
		s.close();
		File ADDRESS = new File("server_address.txt");
		ArrayList<String> address = new ArrayList<String>();
		File SERVER = new File("server.txt");
		try {
			Service service = new ServiceImpl();
			LocateRegistry.createRegistry(6600);
			Naming.rebind("rmi://" + str1 + ":" + str2 + "/Service", service);
			System.out.println("Service Start!");
			BufferedReader reader = new BufferedReader(new FileReader(ADDRESS));
			String str = "";
			while ((str = reader.readLine()) != null) {
				if (!str.equals(str1 + ":" + str2))
					address.add(str);
			}
			reader.close();
			if (address.size() > 0) {
				str = str1 + ":" + str2;
				Paxos runnable = new Paxos(str, address, SERVER.lastModified());
				while (true) {
					Thread paxos = new Thread(runnable);
					paxos.start();
					paxos.join();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class Paxos implements Runnable {
	private ArrayList<String> address = new ArrayList<String>();
	private File file_1 = new File("server.txt");
	private File file_2 = new File("number.txt");
	private File file_3 = new File("id.txt");
	private String line_1 = null;
	private String line_2 = null;
	private String line_3 = null;
	private String s = "";
	private static long lastModified = 0l;

	private static int ID = 0;
	private static int NUMBER = 1;
	private static int K = 0;

	public Paxos(String str, ArrayList<String> address, long lastModified) {
		this.address = address;
		Paxos.lastModified = lastModified;
	}

	public synchronized void run() {
		File SERVER = new File("server.txt");
		while (true) {
			if (SERVER.lastModified() > lastModified) {
				lastModified = SERVER.lastModified();
				break;
			}
		}
		try {
			BufferedReader reader_1 = new BufferedReader(new FileReader(file_1));
			while ((line_1 = reader_1.readLine()) != null) {
				s += line_1 + "\r\n";
			}
			reader_1.close();
			BufferedReader reader_2 = new BufferedReader(new FileReader(file_2));
			line_2 = reader_2.readLine();
			if (line_2 != null)
				NUMBER = Integer.valueOf(reader_2.readLine());
			reader_2.close();
			BufferedReader reader_3 = new BufferedReader(new FileReader(file_3));
			line_3 = reader_3.readLine();
			if (line_3 != null)
				ID = Integer.valueOf(reader_3.readLine());
			reader_3.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true) {
			K = 5 * NUMBER + ID;
			int yes = 0;
			int no = 0;
			int accept_n = 0;
			String accept_v = s;
			for (int i = 0; i < address.size(); i++) {
				try {
					Service service = (Service) Naming.lookup("rmi://" + address.get(i) + "/Service");
					String result = service.phase_1(K);
					if (result == null || result.equals("error")) {
						no++;
						continue;
					}
					String[] value = result.split("\\s+");
					if ((!value[1].equals("null")) && (!value[2].equals("null"))) {
						int temp = Integer.valueOf(value[1]);
						if (temp > accept_n) {
							accept_n = temp;
							for (int j = 2; j < value.length; j++) {
								accept_v += value[j];
							}
						}
					}
					yes++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (yes < no) {
				NUMBER++;
				K = 5 * NUMBER + ID;
			}
			yes = 0;
			no = 0;
			for (int i = 0; i < address.size(); i++) {
				try {
					Service service = (Service) Naming.lookup("rmi://" + address.get(i) + "/Service");
					String result = service.phase_2(K, accept_v);
					if (result.equals("error")) {
						no++;
					} else
						yes++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (yes < no) {
				NUMBER++;
				K = 5 * NUMBER + ID;
			} else {
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(file_2));
					out.write(String.valueOf(NUMBER));
					out.close();
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
