package com.nabsys.net.socket.client;

import java.nio.ByteBuffer;

import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;

public class MessageEvent {
	private ByteBuffer message = null;
	private TimeoutException timeoutException = null;
	private NetException netException = null;
	private SocketClosedException socketClosedException = null;
	
	protected ByteBuffer getMessage() throws TimeoutException, NetException, SocketClosedException {
		if(timeoutException != null) throw timeoutException;
		if(netException != null) throw netException;
		if(socketClosedException != null) throw socketClosedException;
		
		return message;
	}
	protected MessageEvent setMessage(ByteBuffer message) {
		this.message = message;
		return this;
	}
	protected MessageEvent setTimeoutException(TimeoutException timeoutException) {
		this.timeoutException = timeoutException;
		return this;
	}
	protected MessageEvent setNetException(NetException netException) {
		this.netException = netException;
		return this;
	}
	protected MessageEvent setSocketClosedException(SocketClosedException socketClosedException) {
		this.socketClosedException = socketClosedException;
		return this;
	}

	
	
}
