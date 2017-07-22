import java.io.*;
import java.net.*;
import java.util.*;

public class Bot01{
	private static String host="10.0.187.59";
	private static Socket connection;
	private static int port=20001;

	public static void main(String[]args) throws Exception{
		connection = new Socket(InetAddress.getByName(host), port);
		System.out.println("Socket created");
		PrintStream output = new PrintStream(connection.getOutputStream());
		output.println("HELLO BAYKARP");
		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while(true)
			System.out.println(input.readLine());
	}
}	
