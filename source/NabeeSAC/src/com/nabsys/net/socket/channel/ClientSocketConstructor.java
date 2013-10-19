package com.nabsys.net.socket.channel;

import java.net.SocketAddress;

public class ClientSocketConstructor extends SocketConstructor{
	public ClientSocketConstructor(IChannelFactory channelFactory)
	{
		super(channelFactory);
	}
	
	public IChannelResult connect(SocketAddress remoteAddress)
	{
		if(remoteAddress == null) 
		{
            throw new NullPointerException("remoteAddress");
        }
		
		HandlerChain handlerChain = getHandlerChainFactory().getHandlerChain();
		
		if(handlerChain == null)
		{
			throw new NullPointerException("handlerChain");
		}
		
		Channel channel = getFactory().newChannel(handlerChain);
		channel.setHandlerChainFactory(getHandlerChainFactory());
		
		return channel.connect(remoteAddress);
	}
}
