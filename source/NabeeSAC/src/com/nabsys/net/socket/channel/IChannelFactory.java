package com.nabsys.net.socket.channel;


public interface IChannelFactory {
	Channel newChannel(HandlerChain handlerChain);
}
