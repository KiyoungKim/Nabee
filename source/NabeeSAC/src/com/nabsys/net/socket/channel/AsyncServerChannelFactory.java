package com.nabsys.net.socket.channel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncServerChannelFactory implements IChannelFactory{
	
	private ExecutorService server;
	private int maxClientNumber = 0;
	
	public AsyncServerChannelFactory(int maxClientNumber)
	{
		server = Executors.newCachedThreadPool();
		this.maxClientNumber = maxClientNumber;
	}
	
	
	public void shutdownServer()
	{
		server.shutdown();
		try {
			server.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		
		server.shutdownNow();
	}
	
	public Channel newChannel(HandlerChain handlerChain){
		return new AsyncServerChannel(this, handlerChain, server, maxClientNumber);
	}
}
