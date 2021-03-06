import java.io.*;
import java.net.*;
import java.util.*;

public class Bot03{
	private static String host="10.0.187.59";
	private static Socket connection;
	private static int port=20000;
	private static int version = 0;
	private static volatile int id = 0;
	private static volatile int fillcount = 0;
	private static volatile int profits = 0;
	private static int BOND_FAIR_PRICE = 1000;
	public static void main(String[]args) throws Exception{
		if(args.length == 2)
			host = "1.1.1.1";
		if(args.length >= 1)
			version = Integer.parseInt(args[1]);
		connection = new Socket(InetAddress.getByName(host), port + version);
		System.out.println("Socket created");
		final PrintStream output = new PrintStream(connection.getOutputStream());
		output.println("HELLO BAYKARP");
		final BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		Thread t=new Thread(new Runnable(){
			public void run()
			{
				while(true)
				{
						try {
							String line = input.readLine();
							if(line == null)
							{
								System.out.println("\n\n\n\n\n\n\n"+fillcount+"\n\n\n\n\n\n\n");
								System.exit(0);
							}
							String[] info = line.split(" ");
							if(info[0].equals("FILL"))
							{
								fillcount++;
								if(info[3].equals("SELL")) {
									int profit = Integer.parseInt(info[4]) - 1000;
									int count = Integer.parseInt(info[5]);
									profits += profit*count;
									System.out.println(profits);
								}
								else if(info[3].equals("BUY")) {
									int profit = 1000 - Integer.parseInt(info[4]);
									int count = Integer.parseInt(info[5]);
									profits += profit*count;
									System.out.println(profits);
								}
								System.out.println(line);
							}
							else if (info[0].equals("BOOK") && info[1].equals("NOKFH")) {
								
							
							}

							else if (info[0].equals("BOOK") && info[1].equals("BOND")) {
								boolean selling = false;
								for(int i = 3; i<info.length; i++) {
									if(info[i].equals("SELL"))
										selling = true;
									else if (!selling){
										String[]pair = info[i].split(":");
										int price = Integer.parseInt(pair[0]);
										int count = Integer.parseInt(pair[1]);
										if(price > BOND_FAIR_PRICE) {
											output.println("ADD " + id + " BOND SELL " + price + " " + count);
											id++;
										}
									}
									else {
										String[]pair = info[i].split(":");
										int price = Integer.parseInt(pair[0]);
										int count = Integer.parseInt(pair[1]);
										if(price < BOND_FAIR_PRICE) {
											output.println("ADD " + id + " BOND BUY " + price + " " + count);
											id++;
										}
									}
								}
							}
							//System.out.println(line);
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
			output.println("ADD " + id + " BOND BUY 998 1");
			Thread.sleep(2000);
			id++;
			output.println("ADD " + id + " BOND SELL 1002 1");
			id++;
			Thread.sleep(2000);
		}
	}
}	
