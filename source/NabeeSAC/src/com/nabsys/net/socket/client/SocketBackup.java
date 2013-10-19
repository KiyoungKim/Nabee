package com.nabsys.net.socket.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.nabsys.common.label.NLabel;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;


public class SocketBackup {
	private SocketChannel 		interfaceChannel 	= null;
	private Selector 			readSelector 		= null;
	private long 				readtimeout 		= 0L;
	private boolean 			isConnect 			= false;
	
	
	public SocketBackup(String address, int port, int maxbuff, long timeoutsec) throws NetException, SocketClosedException
	{
		try {
			this.readSelector = Selector.open();
		} catch (IOException e) {
			throw new NetException(0x0000);
		}

		try {
			this.interfaceChannel = SocketChannel.open();
		} catch (IOException e) {
			throw new NetException(0x0001);
		}
		
		try {
			this.interfaceChannel.connect(new InetSocketAddress(address, port));
		} catch (IOException e) {
			throw new NetException(NLabel.get(0x0009) + "[" + address + ":" + port +"]");
		}
		
		try {
			this.interfaceChannel.configureBlocking(false);
		} catch (IOException e) {
			throw new NetException(0x0002);
		}
		
		try {
			this.interfaceChannel.socket().setSoLinger(false, 0);
			this.interfaceChannel.socket().setReuseAddress(true);
			this.interfaceChannel.socket().setReceiveBufferSize(maxbuff);
			this.interfaceChannel.socket().setSendBufferSize(maxbuff);
		} catch(SocketException e) {
			throw new NetException(0x001B);
		}
		
		try {
			this.interfaceChannel.register(readSelector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			throw new SocketClosedException(0x000A);
		}
		
		this.readtimeout = timeoutsec * 1000;
		isConnect = true;
	}
	
	public SocketBackup(SocketChannel inboundChannel, long timeoutsec, int maxbuff) throws NetException, SocketClosedException
	{
		try{
			this.readSelector = Selector.open();
		} catch (IOException e) {
			throw new NetException(0x0000);
		}
		
		this.interfaceChannel = inboundChannel;
		
		try{
			this.interfaceChannel.configureBlocking(false);
		} catch (IOException e) {
			throw new NetException(0x0002);
		}
		
		try {
			this.interfaceChannel.register(readSelector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			throw new SocketClosedException(0x000A);
		}
		
		try{
			this.interfaceChannel.socket().setSoLinger(false, 0);
			this.interfaceChannel.socket().setReuseAddress(true);
			this.interfaceChannel.socket().setReceiveBufferSize(maxbuff);
			this.interfaceChannel.socket().setSendBufferSize(maxbuff);
		} catch(SocketException e) {
			throw new NetException(0x000B);
		}
		
		this.readtimeout = timeoutsec * 1000;
		isConnect = true;
	}
	
	public void setSockReadTimeout(int timeoutsec)
	{
		this.readtimeout = timeoutsec * 1000;
	}
	
	public String getSockInfo()
	{
		return this.interfaceChannel.socket().getInetAddress().toString().replace("/", "");
	}
	
	public int getPortInfo()
	{
		return this.interfaceChannel.socket().getPort();
	}
	
	public ByteBuffer read(int size) throws NetException, TimeoutException, SocketClosedException
	{
		int numKeys;
		try {
			numKeys = readSelector.select(readtimeout);
		} catch (IOException e) {
			throw new NetException(0x0005);
		}

		if(numKeys > 0)
		{
			Iterator<SelectionKey> itr = readSelector.selectedKeys().iterator();
			while(itr.hasNext())
			{
				SelectionKey key = itr.next();
				itr.remove();
				
				SocketChannel socketChannel = (SocketChannel)key.channel();
				ByteBuffer readBuffer = ByteBuffer.allocateDirect(size);
				read(socketChannel, readBuffer);
				readBuffer.rewind();
				
				return readBuffer;
			}
		}
		else if(numKeys == 0)
		{
			throw new SocketClosedException(0x000C);
		}
		else
		{
			throw new TimeoutException(0x000D);
		}
		
		return null;
	}
	
	private void read(SocketChannel socketChannel, ByteBuffer buffer) throws SocketClosedException, NetException, TimeoutException
	{
		buffer.rewind();
		
		int readSocketBufferSize;
		try {
			readSocketBufferSize = socketChannel.socket().getReceiveBufferSize();
		} catch (SocketException e) {
			throw new NetException(0x000B);
		}
		
		int receiveRepeat = 1;
		
		if(buffer.capacity() > readSocketBufferSize)
		{
			receiveRepeat = buffer.capacity() / readSocketBufferSize;
			
			if((buffer.capacity() % readSocketBufferSize) > 0 ) receiveRepeat++;
		}
		
		int readLen = 0;
		int readAccumLen = 0;
		int tryCnt = 0;
		int additionalTry = 0;
		
		if(readtimeout > 10)
		{
			if(readtimeout/10 > receiveRepeat)
			{
				additionalTry = (int)readtimeout / 10 - receiveRepeat;
			}
			else
			{
				additionalTry = 0;
			}
		}
		else
		{
			if(readtimeout <= 0)
				additionalTry = 500;
			else
				additionalTry = 0;
		}
		
		boolean tryFirst = false;

		while(true){
			try {

				try {
					readLen = socketChannel.read(buffer);
				} catch (IOException e) {
					throw new SocketClosedException(0x000F);
				}

				if(readLen > 0) tryFirst = true;
				if(readLen < 0) throw new SocketClosedException(0x000C);

				readAccumLen += readLen;
				
				if(readAccumLen >= buffer.capacity()) break;
				
				if(tryFirst && receiveRepeat > tryCnt)
				{
					tryCnt++;
					continue;
				}
				
				if(tryFirst){
					try {
						Thread.sleep(10); //1초
					} catch (InterruptedException e1) {
						
					}
	
					tryCnt ++;
					
					if(tryCnt > receiveRepeat + additionalTry) 
					{
						throw new TimeoutException(0x000D);
					}
					
					continue;
				}
			} catch (SocketClosedException e) {
				isConnect = false;
				throw e;
			} catch (TimeoutException e) {
				isConnect = false;
				throw e;
			}
		}
	}
	
	public void write(ByteBuffer writeBuffer) throws SocketClosedException, TimeoutException, NetException
	{
		writeBuffer.rewind();

		int readSocketBufferSize;
		try {
			readSocketBufferSize = interfaceChannel.socket().getReceiveBufferSize();
		} catch (SocketException e2) {
			throw new NetException(0x000B);
		}
		int sendRepeat = 1;
		
		int accumLen = 0;
		int tryCnt = 0;
		boolean tryFirst = false;
		
		if(writeBuffer.capacity() > readSocketBufferSize)
		{
			sendRepeat = writeBuffer.capacity() / readSocketBufferSize;
			
			if((writeBuffer.capacity() % readSocketBufferSize) > 0)
			{
				sendRepeat++;
			}
		}
		
		int additionalTry = 0;
		
		if(readtimeout > 10)
		{
			if(readtimeout/10 > sendRepeat)
			{
				additionalTry = (int)readtimeout / 10 - sendRepeat;
			}
			else
			{
				additionalTry = 0;
			}
		}
		else
		{
			if(readtimeout <= 0)
				additionalTry = 500;
			else
				additionalTry = 0;
		}
		
		while(true){
			try {
				int writeLen = 0;
				try {
					writeLen = interfaceChannel.write(writeBuffer);
				} catch (IOException e) {
					throw new SocketClosedException(0x0010);
				}

				if(writeLen != 0) tryFirst = true; //최초 한번의 데이터가 전송되면 TRUE
				if(writeLen < 0)
				{
					isConnect = false;
					throw new SocketClosedException(0x000C);
				}
				
				accumLen += writeLen;
				
				if(accumLen >= writeBuffer.capacity()) break;
				
				if(tryFirst && sendRepeat > tryCnt)
				{
					tryCnt++;
					continue;
				}

				if(tryFirst){
					try {
						Thread.sleep(10); //0.01초
					} catch (InterruptedException e1) {
						
					}
					
					tryCnt ++;
					
					if(tryCnt > sendRepeat + additionalTry) 
					{
						isConnect = false;
						throw new TimeoutException(0x000E);
					}
					
					continue;
				}
			} catch (SocketClosedException e) {
				isConnect = false;
				throw e;
			} catch (TimeoutException e) {
				isConnect = false;
				throw e;
			}
		}
	}
	
	public boolean isConnect() 
	{
		if(!isConnect || !interfaceChannel.isConnected())
			return false;
		else
			return true;
	}
	
	public void close() throws IOException
	{
		if(interfaceChannel != null)
			interfaceChannel.close();
		if(readSelector != null)
			readSelector.close();
		
		isConnect = false;
	}
}
