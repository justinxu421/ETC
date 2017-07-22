import java.io.*;
import java.net.*;
import java.util.*;

public class Bot03{
	private static String host="10.0.187.59";
	private static Socket connection;
	private static int port=20000;
	private static int version = 0;
	private static int id = 0;
	private static volatile int fillcount = 0;
	public static void main(String[]args) throws Exception{
		if(args.length == 2)
			host = "1.1.1.1";
		if(args.length >= 1)
			version = Integer.parseInt(args[1]);
		connection = new Socket(InetAddress.getByName(host), port + version);
		System.out.println("Socket created");
		PrintStream output = new PrintStream(connection.getOutputStream());
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
								System.out.println(line);
							}
							else if (info[0].equals("BOOK") && info[1].equals("BOND")) {
								boolean selling = false;
								for(int i = 3; i<info.length; i++) {
									if(info[i].equals("SELL"))
										selling = true;
									else if (!selling){
										
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
			Thread.sleep(1000);
			id++;
			output.println("ADD " + id + " BOND SELL 1002 1");
			id++;
			Thread.sleep(1000);
		}
	}
}	
