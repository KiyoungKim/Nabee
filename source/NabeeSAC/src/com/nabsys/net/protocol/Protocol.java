package com.nabsys.net.protocol;

import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;

public abstract class Protocol {
	protected abstract void _sendAliveMessage() throws SocketClosedException, TimeoutException, NetException;
}
