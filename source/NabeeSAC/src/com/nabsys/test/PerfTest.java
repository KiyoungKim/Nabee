package com.nabsys.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class Transactions implements Callable<Integer>{
	private int id = 0;
	private String serverAddr = null;
	private int serverPort = 0;
	private String msg = null;
	private String encoding = null;
	private int seconds = 0;
	
	public Transactions(int id, String serverAddr, int serverPort, String msg, String encoding, int seconds){
		this.id = id;
		this.serverAddr = serverAddr;
		this.serverPort = serverPort;
		this.msg = msg;
		this.encoding = encoding;
		this.seconds = seconds;
	}
	
	public Integer call()throws Exception {
		byte[] b = null;
		OutputStream out = null;
        InputStream in = null;
		try {
			Socket socket = new Socket(serverAddr, serverPort);
			out = socket.getOutputStream();
            in = socket.getInputStream();
            
            b = msg.getBytes(encoding);
            
            Thread.sleep(1000);
            
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		
		System.out.println("["+ id + "]>> Starts");

		int num = 0;
		try {
			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();
			
			while((end - start) <= 1000 * seconds )
			{
				out.write(b);
				out.flush();
				end = System.currentTimeMillis();
		        in.read(b);
		        num++;
			}
			
			System.out.println("["+ id + "]>> Finished. Executed "+num+" transactions in "+seconds+" seconds.");
		} catch (IOException e) {
			System.out.println("["+ id + "]>> " + e.getMessage());
			e.printStackTrace();
		}	finally {
			in.close();
			out.close();
		}
		
		return num;
	}
}

public class PerfTest {

	/**
	 * @param args
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		int totalRunners = 5;
		int seconds = 10;
		String serverAddr = "127.0.0.1";
		int serverPort = 6001;
		String msg = "0067PERN_TEST                           kykim     new1234!  ET001ET001 ";
		String encoding = "UTF-8";
		
		
		final ExecutorService runners = Executors.newFixedThreadPool(totalRunners);
		final List<Transactions> tl = new ArrayList<Transactions>();
		
		for(int i=0; i<totalRunners; i++)
		{
			tl.add(new Transactions(i, serverAddr, serverPort, msg, encoding, seconds));
		}
		
		try {
			List<Future<Integer>> futureList = runners.invokeAll((Collection)tl);
			
			int totNum = 0;
			
			try{
				for(Future future : futureList ) {
					totNum += (Integer)future.get();
				}
			}
			catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			int transaction = totNum / seconds;
			int bytes = 0;
			try {
				bytes = ((msg.getBytes(encoding)).length * 2) * transaction;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			System.out.println("Transactions in "+seconds+" seconds : " + totNum);
			System.out.println("Transactions per a second : " + transaction);
			System.out.println(bytes + " byte transfered per a second.");
			
			
			runners.shutdown();
			runners.awaitTermination(120, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
