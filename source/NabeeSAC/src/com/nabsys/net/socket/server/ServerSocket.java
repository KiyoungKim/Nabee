package com.nabsys.net.socket.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.socket.client.Socket;


public class ServerSocket{

	private Selector selector = null;
	private ServerSocketChannel channel = null;
	private long readtimeout = 0L;
	private int maxbuff = 0;

	public ServerSocket(int port, int maxbuff, long readtimeout) throws NetException
	{
		try {
			selector = Selector.open();
		} catch (IOException e) {
			throw new NetException(0x0000);
		}
		
		try {
			channel = ServerSocketChannel.open();
		} catch (IOException e) {
			throw new NetException(0x0001);
		}
		
		try {
			channel.configureBlocking(false);
		} catch (IOException e) {
			throw new NetException(0x0002);
		}
		
		InetSocketAddress address = new InetSocketAddress(port);
		try {
			channel.socket().bind(address);
		} catch (IOException e) {
			throw new NetException(0x0003);
		}
		
		try {
			channel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (ClosedChannelException e) {
			throw new NetException(0x0004);
		}
		
		this.readtimeout = readtimeout;
		this.maxbuff = maxbuff;
	}
	
	public Socket accept() throws NetException, SocketClosedException
	{
		Socket socket = null;
		int numKeys;
		
		try{
			numKeys = selector.select();
		}catch (IOException e) {
			throw new NetException(0x0005);
		}

		if(numKeys> 0)
		{
			Iterator<SelectionKey> itr = selector.selectedKeys().iterator();
			
			while(itr.hasNext())
			{
				SelectionKey key = itr.next();
				itr.remove();
					
				ServerSocketChannel readyChannel = (ServerSocketChannel)key.channel();
				SocketChannel interfaceChannel = null;
				try {
					interfaceChannel = readyChannel.accept();
				} catch (IOException e) {
					throw new NetException(0x0006);
				}
				socket = new Socket(interfaceChannel, readtimeout, maxbuff);
				return socket;
			}
		}
		
		throw new NetException(0x0006);
	}
	
	public void close() throws NetException
	{
		if(selector != null)
		{
			try {
				selector.close();
			} catch (IOException e) {
				throw new NetException(0x0007);
			}
			//selector.notify();
		}
		if(channel != null)
		{
			try {
				channel.close();
			} catch (IOException e) {
				throw new NetException(0x0008);
			}
		}
	}
}
