package com.nabsys.net.socket.client;

import java.nio.ByteBuffer;

public class SocketTest {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(10);
		
		ByteBuffer initBuffer = ByteBuffer.allocateDirect(10);
		initBuffer.put("ABCDEFGHIJ".getBytes());
		initBuffer.flip();
		
		System.out.println(initBuffer);
		
		for(int i=0; i<2; i++) {buffer.put(initBuffer.get());}
		buffer.flip();
		
		byte[] prnt = new byte[2];
		buffer.get(prnt);
		System.out.println(new String(prnt));
	}

}
