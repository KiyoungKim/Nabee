package com.nabsys.net.socket.channel;

import java.net.SocketAddress;

public interface IChannelEvent extends IEvent {
	EventState getState();
	void setValue(Object value);
	Object getValue();
	SocketAddress getRemoteAddress();
}
