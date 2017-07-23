import java.io.*;
import java.net.*;
import java.util.*;

public class Bot04{

	private static String host="10.0.187.59";
	private static Socket connection;
	private static int port=20000;
	private static int version = 0;
	private static volatile int id = 0;
	private static volatile int fillcount = 0;
	private static volatile int profits = 0;
	private static int BOND_FAIR_PRICE = 1000;
	private static int BEST_BID_NOKUS;
	private static int BEST_OFFER_NOKUS;
	private static int BEST_BID_NOKFH;
	private static int BEST_OFFER_NOKFH;
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
							else if (info[0].equals("BOOK") && (info[1].equals("NOKFH") || info[1].equals("NOKUS")))
							{
								if(info[1].equals("NOKFH"))
								{
									Pair pair = best(info);
									System.out.println(pair);
									if(pair.getKey()!=0)
										BEST_BID_NOKFH = pair.getKey();
									if(pair.getValue()!=0)
										BEST_OFFER_NOKFH = pair.getValue();
								}
								else{
									Pair pair = best(info);
									System.out.println(pair);
									if(pair.getKey()!=0)
										BEST_BID_NOKUS = pair.getKey();
									if(pair.getValue()!=0)
										BEST_OFFER_NOKUS = pair.getValue();
								}
								if(BEST_BID_NOKFH!=0&&BEST_OFFER_NOKFH!=0&&BEST_BID_NOKUS!=0&&BEST_OFFER_NOKUS!=0){
									if(BEST_OFFER_NOKUS<BEST_BID_NOKFH){
										output.println("ADD " + id + " NOKUS BUY " + BEST_OFFER_NOKUS + " 1");
										id++;
										output.println("CONVERT " + id + " NOKUS SELL 1");
										id++;
										output.println("ADD " + id + " NOKFH SELL " + BEST_BID_NOKFH + " 1");
										id++;
									}
									if(BEST_OFFER_NOKFH<BEST_BID_NOKUS) {
										output.println("ADD " + id + " NOKFH BUY " + BEST_OFFER_NOKFH + " 1");
										id++;
										output.println("CONVERT " + id + " NOKUS BUY 1");
										id++;
										output.println("ADD " + id + " NOKUS SELL " + BEST_BID_NOKUS + " 1");
										id++;
									}
									if((BEST_BID_NOKFH+BEST_OFFER_NOKFH)>(BEST_BID_NOKUS+BEST_OFFER_NOKUS)){
										output.println("ADD " + id + " NOKFH BUY " + ((BEST_BID_NOKUS+BEST_OFFER_NOKUS)/2) + " 1");
										id++;
										output.println("ADD " + id + " NOKUS SELL " + ((BEST_BID_NOKFH+BEST_OFFER_NOKFH)/2) + " 1");
										id++;
										System.out.println("Justin");
									}
									if((BEST_BID_NOKFH+BEST_OFFER_NOKFH)<(BEST_BID_NOKUS+BEST_OFFER_NOKUS)){
										output.println("ADD " + id + " NOKUS BUY " + ((BEST_BID_NOKFH+BEST_OFFER_NOKFH)/2) + " 1");
										id++;
										output.println("ADD " + id + " NOKFH SELL " + ((BEST_BID_NOKUS+BEST_OFFER_NOKUS)/2) + " 1");
										id++;
										System.out.println("Jonathan");
									}
								}
							}
							else if (info[0].equals("BOOKS")) {
								String[]buypair = info[3].split(":");
								if(buypair.length == 2) {
									int buyprice = Integer.parseInt(buypair[0]);
									output.println("ADD " + id + " " + info[1] +" BUY " + (buyprice - 1) + " 1");
									id++;
									int sellIndex = findSell(info, 4);
									if(info.length>sellIndex+1)
									{
										int sellprice = Integer.parseInt(info[sellIndex+1].split(":")[0]);
										output.println("ADD " + id + " " + info[1] +" SELL " + (sellprice + 1) + " 1");
										id++;
									}
								}
								else{
									if(info.length > 4)
									{
										int sellprice = Integer.parseInt(info[4].split(":")[0]);
										output.println("ADD " + id + " " + info[1] +" SELL " + (sellprice + 1) + " 1");
										id++;
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
		/*
		while(true)
		{
			output.println("ADD " + id + " BOND BUY 998 1");
			Thread.sleep(2000);
			id++;
			output.println("ADD " + id + " BOND SELL 1002 1");
			id++;
			Thread.sleep(2000);
		}
		*/
	}
	
	private static int findSell(String[] info, int start){
		for(int i = start; i<info.length;i++)
		{
			if(info[i].equals("SELL"))
				return i;
		}
		return 0;
	}
	
	private static Pair best (String[] info) {
		int sellIndex = findSell(info, 3);
		if(sellIndex == 3 && info.length > 4)
		{
			return new Pair(0, Integer.parseInt(info[4].split(":")[0]));
		}
		else if (sellIndex > 3 && info.length > sellIndex + 1){
			return new Pair(Integer.parseInt(info[3].split(":")[0]), Integer.parseInt(info[sellIndex+1].split(":")[0]));
		}
		else if (sellIndex>3 && info.length == sellIndex + 1){
			return new Pair(Integer.parseInt(info[3].split(":")[0]),0);
		}
		return new Pair(0,0);
	}
}	
