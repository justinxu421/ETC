import java.io.*;
import java.net.*;
import java.util.*;

public class Bot01{
	private static String host="10.0.187.59";
	private static Socket connection;
	private static int port=20001;
	private static int id = 0;
	public static void main(String[]args) throws Exception{
		connection = new Socket(InetAddress.getByName(host), port);
		System.out.println("Socket created");
		PrintStream output = new PrintStream(connection.getOutputStream());
		output.println("HELLO BAYKARP");
		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		Thread t=new Thread(new Runnable(){
			public void run()
			{
				while(true)
				{
						try {
							System.out.println(input.readLine());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		});
		t.start();
		while(true)
		{
			output.println("ADD " + id + " BOND BUY 999 1");
			id++;
			output.println("ADD " + id + " BOND SELL 1001 1");
			id++;
		}
	}
}	
