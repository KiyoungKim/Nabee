package com.nabsys.net.socket.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.util.AccumByteBuffer;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;


public class Socket {
	private SocketChannel 					interfaceChannel 	= null;
	private Selector 						readSelector 		= null;
	private long 							readtimeout 		= 0L;
	private boolean 						isConnect 			= false;
	private boolean							isServer			= false;
	private IMessageReceivedListener 		recvListener		= null;
	private BlockingQueue<MessageEvent>		replyQueue			= null;
	
	public static byte						NULL_DELIMITER		= 0x00;
	public static byte						LF_DELIMITER		= 0x0A;
	public static byte						CR_DELIMITER		= 0x0D;
	
	public Socket(String address, int port, int maxbuff, long timeoutsec) throws NetException, SocketClosedException
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
		
		this.readtimeout 	= timeoutsec;
		this.replyQueue 	= new LinkedBlockingQueue<MessageEvent>();
		isConnect 			= true;
		
		invokeMessageListener();
	}
	
	public Socket(SocketChannel inboundChannel, long timeoutsec, int maxbuff) throws NetException, SocketClosedException
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
		
		this.readtimeout 	= timeoutsec;
		this.replyQueue 	= new LinkedBlockingQueue<MessageEvent>();
		isConnect 			= true;
		isServer			= true;
		
		invokeMessageListener();
	}
	
	public void addMessageReceivedListener(IMessageReceivedListener recvListener)
	{
		this.recvListener = recvListener;
	}
	
	public void setSockReadTimeout(long timeoutsec)
	{
		this.readtimeout = timeoutsec;
	}
	
	public String getSockInfo()
	{
		return this.interfaceChannel.socket().getInetAddress().toString().replace("/", "");
	}
	
	public int getPortInfo()
	{
		return this.interfaceChannel.socket().getPort();
	}
	
	public void clearBuffer()
	{
		synchronized(nextBuffer)
		{
			nextBuffer = new AccumByteBuffer();
		}
	}
	
	
	public ByteBuffer read(byte delimiter) throws TimeoutException, NetException, SocketClosedException
	{
		if(this.recvListener != null) throw new UnsupportedOperationException("Asynchronized socket can't use read method.");

		try {
			MessageEvent e = null;
			AccumByteBuffer buffer = new AccumByteBuffer();
			
			boolean chkEnd = false;
			if(nextBuffer.getLength() > 0)
			{
				synchronized(nextBuffer)
				{
					chkEnd = buffer.write(nextBuffer, delimiter);
				}
			}
			
			if(!chkEnd)
			{
				if(this.readtimeout == 0L)
				{
					if(buffer.getLength() > 0)
					{
						e = replyQueue.poll(30, TimeUnit.SECONDS);
						if(e == null)
						{
							e = new MessageEvent();
							e.setTimeoutException(new TimeoutException(0x000D));
						}
					}
					else
					{
						while((e = replyQueue.poll(60, TimeUnit.SECONDS))== null)
						{
						}
					}
				}
				else
				{
					e = replyQueue.poll(this.readtimeout, TimeUnit.SECONDS);
					if(e == null)
					{
						//throw new TimeoutException(0x000D);
						if(isServer)//원래 타임아웃 이지만, 지정된 시간 안에 반응이 없다면(Alive message 가 없다면) 연결이 끊어진 것으로 판단.
						{
							e = new MessageEvent();
							replyQueue.offer(e.setSocketClosedException(new SocketClosedException(0x000C)));
						}
						else
						{
							e = new MessageEvent();
							replyQueue.offer(e.setTimeoutException(new TimeoutException(0x000D)));
						}
					}
				}
				
				ByteBuffer tmp = e.getMessage();
				buffer.write(tmp, delimiter);
				synchronized(nextBuffer)
				{
					nextBuffer.write(tmp);
				}
			}
			
			return buffer.getBuffer();
		} catch (InterruptedException ex){
			throw new SocketClosedException(0x000C);
		}
	}
	
	private AccumByteBuffer nextBuffer = new AccumByteBuffer(); 
	
	public ByteBuffer read(int size) throws TimeoutException, NetException, SocketClosedException
	{
		if(this.recvListener != null) throw new UnsupportedOperationException("Asynchronized socket can't use read method.");
		
		try {
			MessageEvent e = null;
			AccumByteBuffer buffer = new AccumByteBuffer();
			if(nextBuffer.getLength() > 0)
			{
				synchronized(nextBuffer)
				{
					buffer.write(nextBuffer.getBuffer(size));
				}
			}
			
			if(buffer.getLength() < size)
			{
				if(this.readtimeout == 0L)
				{
					if(buffer.getLength() > 0)
					{
						e = replyQueue.poll(30, TimeUnit.SECONDS);
						if(e == null)
						{
							e = new MessageEvent();
							e.setTimeoutException(new TimeoutException(0x000D));
						}
					}
					else
					{
						while((e = replyQueue.poll(60, TimeUnit.SECONDS))== null)
						{
						}
					}
				}
				else
				{
					e = replyQueue.poll(this.readtimeout, TimeUnit.SECONDS);
					if(e == null)
					{
						//throw new TimeoutException(0x000D);
						if(isServer)//원래 타임아웃 이지만, 지정된 시간 안에 반응이 없다면(Alive message 가 없다면) 연결이 끊어진 것으로 판단.
						{
							e = new MessageEvent();
							replyQueue.offer(e.setSocketClosedException(new SocketClosedException(0x000C)));
						}
						else
						{
							e = new MessageEvent();
							replyQueue.offer(e.setTimeoutException(new TimeoutException(0x000D)));
						}
					}
				}
				
				ByteBuffer tmp = e.getMessage();
				buffer.write(tmp, size - buffer.getLength());
				
				synchronized(nextBuffer)
				{
					nextBuffer.write(tmp);
				}
			}
			
			return buffer.getBuffer();
		} catch (InterruptedException ex){
			throw new SocketClosedException(0x000C);
		}
	}
	
	private void invokeMessageListener()
	{
		Thread listener = new Thread(new Runnable(){
			
			public void run() {
				try {
					int numKeys;

					while(true)
					{
						try {
							numKeys = readSelector.select(0L);
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
								ByteBuffer buffer = read(socketChannel);
								
								if(recvListener != null)
								{
									recvListener.messageReceived(buffer);
								}
								else
								{
									MessageEvent event = new MessageEvent();
									replyQueue.offer(event.setMessage(buffer));
								}
								
							}
						}
						else if(numKeys == 0)
						{
							//throw new TimeoutException(0x000D);
							if(isServer)//원래 타임아웃 이지만, 지정된 시간 안에 반응이 없다면(Alive message 가 없다면) 연결이 끊어진 것으로 판단.
							{
								throw new SocketClosedException(0x000C);
							}
							else
							{
								MessageEvent event = new MessageEvent();
								replyQueue.offer(event.setTimeoutException(new TimeoutException(0x000D)));
							}
						}
					}
				} catch(NetException e) {
					MessageEvent event = new MessageEvent();
					replyQueue.offer(event.setNetException(e));
				} catch (SocketClosedException e) {
					MessageEvent event = new MessageEvent();
					replyQueue.offer(event.setSocketClosedException(e));
				} catch (ClosedSelectorException e) {
					MessageEvent event = new MessageEvent();
					replyQueue.offer(event.setSocketClosedException(new SocketClosedException(0x000C)));
				}
			}
			
			private ByteBuffer read(SocketChannel socketChannel) throws NetException, SocketClosedException
			{
				int readSocketBufferSize;
				try {
					readSocketBufferSize = socketChannel.socket().getReceiveBufferSize();
				} catch (SocketException e) {
					throw new NetException(0x000B);
				}
				
				AccumByteBuffer accumByteBuffer = new AccumByteBuffer();
				boolean tryFirst = true;
				
				while(true){
					try {
						ByteBuffer tmpReadBuffer = ByteBuffer.allocateDirect(readSocketBufferSize);
						int readLen = socketChannel.read(tmpReadBuffer);

						if(readLen < 0)
						{
							throw new SocketClosedException(0x000C);
						}
						else if(readLen == 0)
						{
							if(tryFirst) accumByteBuffer.write(ByteBuffer.allocateDirect(0));
							break;
						}
						else
						{
							tryFirst = false;
							tmpReadBuffer.flip();
							accumByteBuffer.write(tmpReadBuffer);
						}
					} catch (SocketClosedException e) {
						throw e;
					} catch (IOException e) {
						throw new SocketClosedException(0x000C);
					}
				}
				return accumByteBuffer.getBuffer();
			}
		});
		
		listener.start();
	}
	
	
	public void write(ByteBuffer writeBuffer) throws SocketClosedException, TimeoutException, NetException
	{
		writeBuffer.rewind();

		int socketBufferSize;
		try {
			socketBufferSize = interfaceChannel.socket().getReceiveBufferSize();
		} catch (SocketException e2) {
			throw new NetException(0x000B);
		}
		int sendRepeat = 1;
		
		int accumLen = 0;
		int tryCnt = 0;
		boolean tryFirst = false;
		
		if(writeBuffer.capacity() > socketBufferSize)
		{
			sendRepeat = writeBuffer.capacity() / socketBufferSize;
			
			if((writeBuffer.capacity() % socketBufferSize) > 0)
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
					throw new SocketClosedException(0x000C);
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
