package com.nabsys.net.protocol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.socket.client.IMessageReceivedListener;
import com.nabsys.net.socket.client.Socket;

public abstract class NabeeProtocol extends Protocol{
	
	private Socket 					socket 					= null;
	private AliveManager 			alive 					= null;
	private Thread 					aliveThread 			= null;
	protected String				user					= null;
	protected String				userAuthority			= null;
	private final Lock 				readLock 				= new ReentrantLock();
    private final Lock 				writeLock 				= new ReentrantLock();
	
	//KEEP ALIVE 메시지 전송.. ALIVE MANAGER 전용
	abstract protected void sendAliveMessage() throws SocketClosedException, TimeoutException, NetException;
	abstract protected void close();
	abstract protected void writePacket(NBFields params) throws SocketClosedException, TimeoutException, NetException, DataTypeException, UnsupportedEncodingException, ProtocolException;
	abstract protected NBFields readPacket() throws NetException, TimeoutException, SocketClosedException, UnsupportedEncodingException, DataTypeException, NoSuchAlgorithmException, ProtocolException;
	abstract protected ByteBuffer getWritePacket();
	abstract protected ByteBuffer getReadPacket();
	abstract protected String getLoadClass(NBFields fields);
	
	public NabeeProtocol(Socket socket) throws ClassNotFoundException
	{
		this.socket = socket;
	}
	
	protected void addMessageReceivedListener(IMessageReceivedListener recvListener)
	{
		this.socket.addMessageReceivedListener(recvListener);
	}
	
	public void setReadTimeout(int timeoutsec)
	{
		this.socket.setSockReadTimeout(timeoutsec);
	}
	
	public void _sendAliveMessage() throws SocketClosedException, TimeoutException, NetException
	{
		sendAliveMessage();
	}
	
	//KEEP ALIVE 유지를 위한 쓰레드 생성. 
	//CLIENT 에서만 필요.
	public void invokeKeepAlive(int second)
	{
		alive = new AliveManager(this, second);
		aliveThread = new Thread(alive);
		aliveThread.start();
	}

	//KEEP ALIVE 쓰레드 종료
	private void killKeepAlive()
	{
		if(alive != null)
		{
			alive.exit();
			aliveThread.interrupt();
			while(!alive.isExit())
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			
			alive = null;
			aliveThread = null;
		}
	}
	
	public boolean isConnected()
	{
		return socket.isConnect();
	}
	
	public void _close() throws IOException
	{
		killKeepAlive();
		close();
		socket.close();
	}

	public NBFields execute(NBFields params) throws SocketClosedException, TimeoutException, NetException, DataTypeException, UnsupportedEncodingException, NoSuchAlgorithmException, ProtocolException 
	{
		writeLock.lock();
		readLock.lock();
		try{
			_writePacket(params);
			return _readPacket();
		}finally{
			readLock.unlock();
			writeLock.unlock();
		}
	}
	
	public void _writePacket(NBFields params) throws SocketClosedException, TimeoutException, NetException, DataTypeException, UnsupportedEncodingException, ProtocolException
	{
		writeLock.lock();
		try{
			writePacket(params);
		}finally{
			writeLock.unlock();
		}
	}
	
	public NBFields _readPacket() throws NetException, TimeoutException, SocketClosedException, UnsupportedEncodingException, DataTypeException, NoSuchAlgorithmException, ProtocolException
	{
		readLock.lock();
		try{
			NBFields params = readPacket();
			return params;
		}finally{
			readLock.unlock();
		}
	}
	
	protected void write(ByteBuffer buff) throws SocketClosedException, TimeoutException, NetException
	{
		writeLock.lock();
		try{
			socket.write(buff);
		}finally{
			writeLock.unlock();
		}
	}
	
	protected ByteBuffer read(int size) throws NetException, TimeoutException, SocketClosedException
	{
		readLock.lock();
		try{
			return socket.read(size);
		}finally{
			readLock.unlock();
		}
	}
	
	public String _getLoadClass(NBFields fields)
	{
		readLock.lock();
		try{
			return getLoadClass(fields);
		}finally{
			readLock.unlock();
		}
	}

	public String getPeerAddress()
	{
		return socket.getSockInfo();
	}
	
	public int getPeerPort()
	{
		return socket.getPortInfo();
	}

	public String getUserAuthority()
	{
		return userAuthority;
	}
	
	public String getUser()
	{
		return user;
	}
}
