package com.nabsys.net.socket.channel;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabsys.net.exception.NetException;
import com.nabsys.net.socket.channel.handler.ChannelHandler;

public class ServerSocketConstructor extends SocketConstructor{
	public ServerSocketConstructor(IChannelFactory channelFactory)
	{
		super(channelFactory);
	}
	
	public void shutdownServer()
	{
		((AsyncServerChannelFactory)getFactory()).shutdownServer();
	}
	
	public Channel bind(final SocketAddress localAddress)
	{
		if (localAddress == null) {
            throw new NullPointerException("localAddress");
        }
		
		final BlockingQueue<IChannelResult> futureQueue = new LinkedBlockingQueue<IChannelResult>();
		
		ChannelHandler binder = new Binder(localAddress, futureQueue);
        HandlerChain acceptorHandlerChain = new HandlerChain();
        acceptorHandlerChain.addLast("binder", binder);
		
        Channel channel = getFactory().newChannel(acceptorHandlerChain);

        IChannelResult result = null;
        boolean interrupted = false;
        do {
            try {
            	result = futureQueue.poll(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        } while (result == null);
        
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        
        if(!result.isSuccess())
        {
        	result.getChannel().close();
        	throw new NetException(result.getCause().getMessage());
            //throw new NetException(0x0003);
        }
        
        channel.setHandlerChainFactory(getHandlerChainFactory());
		return channel;
	}
	

	private final class Binder extends ChannelHandler{
		private BlockingQueue<IChannelResult> futureQueue;
		private SocketAddress localAddress;
		
		protected Binder(SocketAddress localAddress, BlockingQueue<IChannelResult> futureQueue)
		{
			this.futureQueue = futureQueue;
			this.localAddress = localAddress;
		}
		
		public void channelOpen(IChannelEvent e)
		{
			e.getChannel().setHandlerChainFactory(getHandlerChainFactory());
			
			super.channelOpen(e);
			futureQueue.offer(e.getChannel().bind(localAddress));
		}
	}
}

