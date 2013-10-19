package com.nabsys.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MemTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int seconds = 400;
		String serverAddr = "localhost";
		int serverPort = 6001;
		String msg = "0067PERN_TEST                           kykim     new1234!  ET001ET001 ";
		String encoding = "UTF-8";
		
		
		byte[] b = null;
		Socket socket = null;
		OutputStream out = null;
        InputStream in = null;
		try {
			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();
			System.out.println("Test started.");
			int num = 0;
			
			while((end - start) <= 1000 * seconds )
			{
				socket = new Socket();
				socket.setReuseAddress(true);
				socket.setSoLinger(true, 0);
				socket.connect(new InetSocketAddress(serverAddr, serverPort));
				
				System.out.println("Connection : " + (num++));
				out = socket.getOutputStream();
		        in = socket.getInputStream();
		            
		        b = msg.getBytes(encoding);
		            
		        out.write(b);
				out.flush();
				end = System.currentTimeMillis();
			    in.read(b);
			    socket.shutdownInput();
			    socket.shutdownOutput();
			    socket.close();
			}
            
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Test finished.");
	}

}
