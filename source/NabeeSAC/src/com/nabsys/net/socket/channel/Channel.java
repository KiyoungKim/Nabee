package com.nabsys.net.socket.channel;

import java.net.SocketAddress;

public interface Channel {
	Integer getId();
	SocketAddress getRemoteAddress();
	IChannelResult bind(SocketAddress localAddress);
	IChannelResult unbind();
	IChannelResult disconnect();
	IChannelResult close();
	IChannelResult connect(SocketAddress remoteAddress);
	IChannelResult write(Object message);
	void setMaxBufferSize(int size);
	int getMaxBufferSize();
	IChannelFactory getChannelFactory();
	HandlerChain getHandlerChain();
	void setHandlerChainFactory(IHandlerChainFactory handlerChainFactory);
	IHandlerChainFactory getHandlerChainFactory();
	boolean addChild(Channel channel);
	ChannelList getChildren();
	Channel getParent();
	void setDisconnected();
	void setUnbound();
	boolean isConnected();
	boolean isBound();
	void setReadable(boolean readable);
	boolean isReadable();
}
