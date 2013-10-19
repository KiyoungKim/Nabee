package com.nabsys.net.socket.channel;

public class AsyncChannelFactory implements IChannelFactory {

	public Channel newChannel(HandlerChain handlerChain) {
		return new AsyncChannel(this, handlerChain);
	}

}
