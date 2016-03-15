package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import model.Service;

public class Client {

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String str1 = null;
		String str2 = null;
		System.out.println("Please enter the target IP address:");
		str1 = s.next();
		System.out.println("Please enter the target port number:");
		str2 = s.next();
		s.close();
		File file_1 = new File("input.txt");
		File file_2 = new File("client_log.txt");
		try {
			Service service = (Service) Naming.lookup("rmi://" + str1 + ":" + str2 + "/Service");
			BufferedReader reader = new BufferedReader(new FileReader(file_1)); 
			String line = null;
			String str = "";
			while ((line = reader.readLine()) != null) {
				str += line + "\r\n";
			}
			reader.close();
			String receive = service.communication(str);
			FileWriter fileWritter = new FileWriter(file_2.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			handle(receive, bufferWritter);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void handle(String receive, BufferedWriter bufferWritter) throws IOException {
		String[] arr = receive.split("\r\n");
		for (int i = 0; i < arr.length; i++) {
			String str2 = arr[i];
			if (str2.equals("END"))
				break;
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = format.format(date);
			bufferWritter.write("Feedback: " + str2 + "  " + time + "\r\n");
		}
		bufferWritter.close();
	}

}
