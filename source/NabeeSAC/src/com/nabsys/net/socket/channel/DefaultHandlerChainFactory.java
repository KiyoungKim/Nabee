package com.nabsys.net.socket.channel;

public class DefaultHandlerChainFactory implements IHandlerChainFactory {

	private HandlerChain chain;
	public HandlerChain getHandlerChain() {
		return chain;
	}

	public void setHandlerChain(HandlerChain chain) {
		this.chain = chain;
	}

}
