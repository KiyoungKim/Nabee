package com.nabsys.net.socket.channel;

public class SocketConstructor {
	
	private volatile IChannelFactory factory;
	private volatile IHandlerChainFactory handlerChainFactory;
	
	public SocketConstructor(IChannelFactory channelFactory)
	{
		setFactory(channelFactory);
	}
	
	public IChannelFactory getFactory() 
	{
		IChannelFactory factory = this.factory;
        if (factory == null) {
            throw new IllegalStateException("factory is not set yet.");
        }
        return factory;
    }
	
	private void setFactory(IChannelFactory factory)
	{
		if(factory == null) throw new NullPointerException("factory");
		if (this.factory != null) {
            throw new IllegalStateException(
                    "factory can't change once set.");
        }
		
		this.factory = factory;
	}
	
	public void setHandlerChainFactory(IHandlerChainFactory handlerChainFactory)
	{
		if(handlerChainFactory == null) throw new NullPointerException("handlerChainFactory");
		
		this.handlerChainFactory = handlerChainFactory;
	}
	
	public IHandlerChainFactory getHandlerChainFactory()
	{
		return this.handlerChainFactory;
	}
}
