package com.nabsys.resource.service;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;

import com.nabsys.net.protocol.ConnectionPool;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.AsyncChannelFactory;
import com.nabsys.net.socket.channel.Channel;
import com.nabsys.net.socket.channel.ClientSocketConstructor;
import com.nabsys.net.socket.channel.DefaultHandlerChainFactory;
import com.nabsys.net.socket.channel.HandlerChain;
import com.nabsys.net.socket.channel.IChannelFactory;
import com.nabsys.net.socket.channel.IChannelResult;
import com.nabsys.net.socket.channel.IHandlerChainFactory;
import com.nabsys.net.socket.channel.handler.MappingFrameAssignHandler;
import com.nabsys.net.socket.channel.handler.util.BlockingReadHandler;
import com.nabsys.net.socket.channel.handler.util.LogHandler;
import com.nabsys.process.Context;
import com.nabsys.process.ResourceFactory;
import com.nabsys.resource.TelegramContext;
import com.nabsys.resource.TelegramFieldContextList;

public class ClientNetworkHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String addr = null;
	private String protocolID = null;
	private String encoding = null;
	private int port = 0;
	private int lengthFieldOffset = 0;
	private int lengthFieldLength = 0;
	private int lengthFieldAdjustment = 0;
	private int maxBufferSize = 1024;
	private String lengthFieldID = null;
	private String idFieldID = null;
	private Channel channel = null;
	private String inboundTelegramID = null;
	private String outboundTelegramID = null;
	private boolean useConnectionPool = false;
	private String connectionPoolName = null;
	
	public ClientNetworkHandler(ServiceHandler parent, int x, int y, int width,
			int height) {
		super(parent, x, y, width, height);
	}
	public void setData(
			boolean useConnectionPool,
			String connectionPoolName,
			String protocolID, 
			String addr, 
			int port, 
			String encoding, 
			String idFieldID, 
			String lengthFieldID, 
			int lengthFieldOffset, 
			int lengthFieldLength, 
			int lengthFieldAdjustment,
			int maxBufferSize){
		this.useConnectionPool = useConnectionPool;
		this.connectionPoolName = connectionPoolName;
		this.addr = addr;
		this.port = port;
		this.encoding = encoding;
		this.lengthFieldOffset = lengthFieldOffset;
		this.lengthFieldLength = lengthFieldLength;
		this.lengthFieldAdjustment = lengthFieldAdjustment;
		this.protocolID = protocolID;
		this.maxBufferSize = maxBufferSize;
		this.idFieldID = idFieldID;
		this.lengthFieldID = lengthFieldID;
	}
	
	public boolean isUseConnectionPool(){return useConnectionPool;}
	public String getConnectionPoolName(){return connectionPoolName;}
	public String getAddr(){return addr;}
	public int getPort(){return port;}
	public String getEncoding(){return encoding;}
	public int getLengthFieldOffset(){return lengthFieldOffset;}
	public int getLengthFieldLength(){return lengthFieldLength;}
	public int getLengthFieldAdjustment(){return lengthFieldAdjustment;}
	public String getProtocolID(){return protocolID;}
	public int getMaxBufferSize(){return maxBufferSize;}
	public String getIdFieldID(){return idFieldID;}
	public String getLengthFieldID(){return lengthFieldID;}
	
	protected void setInboundTelegramID(String inboundTelegramID)
	{
		this.inboundTelegramID = inboundTelegramID;
	}
	protected void setOutboundTelegramID(String outboundTelegramID)
	{
		this.outboundTelegramID = outboundTelegramID;
	}
	protected Channel getChannel()
	{
		return channel;
	}
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		{
			ResourceFactory rf = ctx.getResourceFactory();
			TelegramFieldContextList inboundTelegramFieldContextList = null;
			TelegramFieldContextList outboundTelegramFieldContextList = null;
			
			if(inboundTelegramID != null && !inboundTelegramID.equals(""))
				inboundTelegramFieldContextList = getPacketFields(rf, rf.getTelegram(inboundTelegramID));
			
			if(ctx.getData("__CALL_BY_MANAGER") != null)
			{
				outboundTelegramID = (String)ctx.getData("__OUT_TLGM_ID");
			}
			
			if(outboundTelegramID != null && !outboundTelegramID.equals(""))
				outboundTelegramFieldContextList = getPacketFields(rf, rf.getTelegram(outboundTelegramID));
			if(useConnectionPool)
			{
				try{
					channel = ((ConnectionPool)ctx.getPlugins(connectionPoolName)).getChannel();
					if(channel.getHandlerChain().isHandlerExist("mappingFrameAssignHandler"))
					{
						MappingFrameAssignHandler mappingFrameAssignHandler = (MappingFrameAssignHandler)channel.getHandlerChain().getHandler("mappingFrameAssignHandler");
						mappingFrameAssignHandler.setOutboundTelegramFieldContextList(outboundTelegramFieldContextList);
						mappingFrameAssignHandler.setInboundTelegramFieldContextList(inboundTelegramFieldContextList);
					}
					if(!channel.getHandlerChain().isHandlerExist("blockingReadHandler"))
					{
						channel.getHandlerChain().addFirst("blockingReadHandler", new BlockingReadHandler<NBFields>());
					}
					if(!channel.getHandlerChain().isHandlerExist("logHandler"))
					{
						if(channel.getHandlerChain().isHandlerExist("keepAliveHandler"))
							channel.getHandlerChain().addNext("keepAliveHandler", "logHandler", new LogHandler());
						else
							channel.getHandlerChain().addNext("blockingReadHandler", "logHandler", new LogHandler());
					}
					
					if(ctx.isTest())
					{
						ctx.offerTestMessage(getHandlerID(), map + "", false, false);
					}
					
					super.moveToInside(ctx, map);
				}finally{
					channel.close();
				}
			}
			else
			{
				IChannelFactory channelFactory = new AsyncChannelFactory();
				HandlerChain handlerChain =  new HandlerChain();
				
				Class<?> protocol = ctx.getResourceFactory().getProtocol(protocolID);
				
				handlerChain.addFirst("blockingReadHandler", new BlockingReadHandler<NBFields>());
				handlerChain.addNext("blockingReadHandler", "logHandler", new LogHandler());
				MappingFrameAssignHandler mappingFrameAssignHandler = null;
				handlerChain.addLast("mappingFrameAssignHandler", mappingFrameAssignHandler = new MappingFrameAssignHandler(
																protocol,
																idFieldID, 
																lengthFieldID,
																lengthFieldOffset,
																lengthFieldLength,
																lengthFieldAdjustment,
																encoding));
				mappingFrameAssignHandler.setOutboundTelegramFieldContextList(outboundTelegramFieldContextList);
				mappingFrameAssignHandler.setInboundTelegramFieldContextList(inboundTelegramFieldContextList);
				
				IHandlerChainFactory chainFactory = new DefaultHandlerChainFactory();
				chainFactory.setHandlerChain(handlerChain);
				
				ClientSocketConstructor socketConstructor = new ClientSocketConstructor(channelFactory);
				socketConstructor.setHandlerChainFactory(chainFactory);
				
				InetSocketAddress remoteAddress = new InetSocketAddress(addr, port);
				
				try{
					IChannelResult result = socketConstructor.connect(remoteAddress);
					
					channel = result.getChannel();
					channel.setMaxBufferSize(maxBufferSize<1024?1024:maxBufferSize);
					
					if(ctx.isTest())
					{
						ctx.offerTestMessage(getHandlerID(), "Connect To Server : " + map + "", false, false);
					}
					
					super.moveToInside(ctx, map);
				}finally{
					channel.close();
				}
			}
		}
		super.execute(ctx, map);
	}
	
	private TelegramFieldContextList getPacketFields(ResourceFactory rf, TelegramContext telegramContext) throws SQLException
	{
		TelegramFieldContextList fields = null;
		if(telegramContext.getHeaderID() != null && !telegramContext.getHeaderID().equals(""))
		{
			fields = getPacketFields(rf, rf.getTelegram(telegramContext.getHeaderID()));
			fields.putAll(telegramContext.getTelegramFieldContextList());
			return fields;
		}
		else
		{
			fields = (TelegramFieldContextList)telegramContext.getTelegramFieldContextList().clone();
			return fields;
		}
	}
}
