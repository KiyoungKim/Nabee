package com.nabsys.net.socket.channel.handler.util;

import org.apache.log4j.Level;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.ExceptionEvent;
import com.nabsys.net.socket.channel.IChannelEvent;
import com.nabsys.net.socket.channel.handler.ChannelHandler;
import com.nabsys.net.socket.channel.handler.util.exception.RequestIntervalTimeoutException;
import com.nabsys.net.socket.channel.handler.util.exception.ResponseIntervalTimeoutException;

public class LogHandler extends ChannelHandler{
	private final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	public void messageReceived(DataEvent e)
	{
		if(!(e.getMessage() instanceof NBFields 
				&& ((NBFields)e.getMessage()).containsKey("RSV_SERVICE_ID") 
				&& ((NBFields)e.getMessage()).get("RSV_SERVICE_ID").equals("RSV_KEEP_ALIVE_SVC"))
				&& logger.isEnabledFor(Level.DEBUG))
		{
			logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "]" + NLabel.get(0x009B));
			logger.debug(e.getMessage());
		}
		super.messageReceived(e);
	}
	
	public void connectRequested(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "] Connection requested.");
		super.connectRequested(e);
	}
	private boolean writeRequested = false;
	public void writeRequested(DataEvent e)
	{
		if(!(e.getMessage() instanceof NBFields 
				&& ((NBFields)e.getMessage()).containsKey("RSV_SERVICE_ID") 
				&& ((NBFields)e.getMessage()).get("RSV_SERVICE_ID").equals("RSV_KEEP_ALIVE_SVC")))
		{
			logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "] Write requested.");
			logger.debug(e.getMessage());
			writeRequested = true;
		}
		super.writeRequested(e);
	}
	
	public void bindRequested(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + "] Bind requested.");
		super.bindRequested(e);
	}

	public void closeRequested(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "] Close requested.");
		super.closeRequested(e);
	}
	
	public void disconnectRequested(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "] Disconnect requested.");
		super.disconnectRequested(e);
	}
	
	public void unbindRequested(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + "] Unbind requested.");
		super.unbindRequested(e);
	}
	
	public void channelConnected(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "] Channel connected.");
		super.channelConnected(e);
	}
	
	public void writeComplete(DataEvent e)
	{
		if(writeRequested)
		{
			logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "] Write complete.");
			writeRequested = false;
		}
		super.writeComplete(e);
	}
	
	public void channelBound(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + "] Channel bound.");
		super.channelBound(e);
	}
	
	public void channelOpen(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "] Channel opened.");
		super.channelOpen(e);
	}
	
	public void channelClosed(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "] Channel closed.");
		super.channelClosed(e);
	}
	
	public void channelDisconnected(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + ":" + e.getChannel().getRemoteAddress() + "] Channel disconnected.");
		super.channelDisconnected(e);
	}
	
	public void channelUnbound(IChannelEvent e)
	{
		logger.debug("[" + e.getChannel().getId() + "] Channel unbound.");
		super.channelUnbound(e);
	}

	public void exceptionCaught(ExceptionEvent e)
	{
		if(!(e.getCause() instanceof RequestIntervalTimeoutException) && !(e.getCause() instanceof ResponseIntervalTimeoutException))
		{
			logger.error(e.getCause(), e.getCause().getClass() + " : " + e.getCause().getMessage());
		}
		super.exceptionCaught(e);
	}
}
