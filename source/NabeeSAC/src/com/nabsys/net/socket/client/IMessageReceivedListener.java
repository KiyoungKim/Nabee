package com.nabsys.net.socket.client;

import java.nio.ByteBuffer;

public interface IMessageReceivedListener {
	public void messageReceived(ByteBuffer message);
}
