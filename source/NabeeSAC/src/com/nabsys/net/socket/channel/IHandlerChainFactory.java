package com.nabsys.net.socket.channel;

public interface IHandlerChainFactory {
	HandlerChain getHandlerChain();
	void setHandlerChain(HandlerChain chain);
}
