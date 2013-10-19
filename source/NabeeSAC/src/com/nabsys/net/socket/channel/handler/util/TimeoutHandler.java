package com.nabsys.net.socket.channel.handler.util;

import java.util.Timer;
import java.util.TimerTask;

import com.nabsys.net.socket.channel.Channel;
import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.IChannelEvent;
import com.nabsys.net.socket.channel.handler.ChannelHandler;
import com.nabsys.net.socket.channel.handler.util.exception.RequestIntervalTimeoutException;
import com.nabsys.net.socket.channel.handler.util.exception.ResponseIntervalTimeoutException;

public class TimeoutHandler extends ChannelHandler {
	
	private TimeoutSide side;
	private long timeout;
	private Timer timer;
	
	public TimeoutHandler(TimeoutSide side, long timeoutmilliseconds)
	{
		if(timeoutmilliseconds < 0) 
		{
			throw new IllegalStateException("timeoutmilliseconds must be non-negative integer : " + timeoutmilliseconds);
		}
		
		this.side = side;
		this.timeout = timeoutmilliseconds;
	}
	
	public void channelConnected(IChannelEvent e)
	{
		if(timeout != 0)
		{
			timer = new Timer();
			timer.schedule(new TimeoutExecutor(e.getChannel()), timeout);
		}
		
		super.channelConnected(e);
	}
	
	public void messageReceived(DataEvent e)
	{
		if(side == TimeoutSide.READ && timeout != 0)
		{
			timer.cancel();
			timer.purge();
			
			timer = new Timer();
			timer.schedule(new TimeoutExecutor(e.getChannel()), timeout);
		}
		
		super.messageReceived(e);
	}
	
	public void channelClosed(IChannelEvent e)
	{
		if(timeout != 0)
		{
			timer.cancel();
			timer.purge();
		}
		super.channelClosed(e);
	}
	
	public void writeComplete(DataEvent e)
	{
		if(side == TimeoutSide.WRITE && timeout != 0)
		{
			timer.cancel();
			timer.purge();

			timer = new Timer();
			timer.schedule(new TimeoutExecutor(e.getChannel()), timeout);
		}
		
		super.writeComplete(e);
	}

	private final class TimeoutExecutor extends TimerTask
	{
		Channel c;
		TimeoutExecutor(Channel c)
		{
			this.c = c;
		}
		
		@Override
		public void run() {
			if(side == TimeoutSide.WRITE)
			{
				fireExceptionStream(c, new RequestIntervalTimeoutException("Request interval timeout"), new ChannelResult(c));
			}
			else if(side == TimeoutSide.READ)
			{
				fireExceptionStream(c, new ResponseIntervalTimeoutException("Response interval timeout"), new ChannelResult(c));
			}
		}	
	}
}
