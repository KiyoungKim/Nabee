package com.nabsys.process.instance.online.handler;

import com.nabsys.net.exception.KeyDuplicateException;
import com.nabsys.net.socket.channel.HandlerChain;
import com.nabsys.net.socket.channel.IHandlerChainFactory;
import com.nabsys.net.socket.channel.handler.util.LogHandler;
import com.nabsys.net.socket.channel.handler.util.TimeoutHandler;
import com.nabsys.net.socket.channel.handler.util.TimeoutSide;
import com.nabsys.process.ResourceFactory;
import com.nabsys.resource.InstanceContext;

public class OnlineHandlerChainFactory implements IHandlerChainFactory {

	private ResourceFactory resourceFactory = null;
	public OnlineHandlerChainFactory(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	
	public HandlerChain getHandlerChain() {
		HandlerChain handlerChain =  new HandlerChain();
		InstanceContext instanceContext = resourceFactory.getInstanceConfiguration();

		try{
			handlerChain.addLast("businessHandler"		, new BusinessHandler(resourceFactory));
			handlerChain.addLast("logHandler"			, new LogHandler());
			handlerChain.addLast("timeoutHandler"		, new TimeoutHandler(TimeoutSide.READ, 1000 * instanceContext.getReadTimeout())); //10sec
			handlerChain.addLast("frameAssignHandler"	, new FrameAssignHandler(resourceFactory));
		} catch (KeyDuplicateException e) {
			throw new IllegalStateException(e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		return handlerChain;
	}

	public void setHandlerChain(HandlerChain chain) {
	}

}
