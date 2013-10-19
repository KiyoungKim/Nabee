package com.nabsys.process.instance.online.handler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.sql.SQLException;

import com.nabsys.common.util.AccumByteBuffer;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.handler.ChannelHandler;
import com.nabsys.net.socket.channel.handler.decoder.DelimiterFrameDecoder;
import com.nabsys.net.socket.channel.handler.decoder.FixedLengthFrameDecoder;
import com.nabsys.net.socket.channel.handler.decoder.FullFrameDecoder;
import com.nabsys.net.socket.channel.handler.decoder.LengthFieldFrameDecoder;
import com.nabsys.process.ResourceFactory;
import com.nabsys.resource.InstanceContext;
import com.nabsys.resource.NetworkContext;
import com.nabsys.resource.OnlineServiceContext;
import com.nabsys.resource.TelegramContext;
import com.nabsys.resource.TelegramFieldContext;
import com.nabsys.resource.TelegramFieldContextList;

public class FrameAssignHandler extends ChannelHandler{
	private String systemIdField = null;
	private String systemLengthField = null;
	private TelegramFieldContextList sysHeaderFields = null;
	private ResourceFactory resourceFactory = null;
	private String networkEncoding = null;
	private NetworkContext networkContext = null;
	
	public FrameAssignHandler(ResourceFactory resourceFactory) throws SQLException
	{
		super();
		this.resourceFactory = resourceFactory;
		InstanceContext instanceContext = resourceFactory.getInstanceConfiguration();
		networkContext = instanceContext;
		this.networkEncoding = instanceContext.getServerEncoding();
		this.systemIdField = instanceContext.getIDFieldID();
		this.systemLengthField = instanceContext.getLengthFieldID();
		this.sysHeaderFields = resourceFactory.getTelegram(instanceContext.getSysHeaderID()).getTelegramFieldContextList();
	}
	
	public void writeRequested(DataEvent e)
	{
		Object d = e.getMessage();
		if(!(d instanceof NBFields))
		{
			super.writeRequested(e);
			return;
		}
		
		int lenLength = sysHeaderFields.get(systemLengthField).getLength();
		int loop = sysHeaderFields.get(systemLengthField).getIndex() + 1;
		int lenOffset = 0;
		TelegramFieldContext[] list = sysHeaderFields.toArray();
		for(int i=0; i<loop; i++)
		{
			lenOffset += list[i].getLength();
		}
		
		try{
			OnlineServiceContext service = null;
			if(e.getService() == null)
			{
				NBFields field = (NBFields)d;
				String serviceId = ((String)field.get(systemIdField)).trim();
				service =  resourceFactory.getOnlineService(serviceId);
				if(service == null) throw new IllegalStateException("Protocol type.");
				service.setServiceIDFieldID(systemIdField);
				service.setServiceID(serviceId);
				e.setService(service);
			}
			else
			{
				service = e.getService();
				e.setService(null);
			}
			Class<?> frameHandler = service.getOnlineProtocol();
			
			if(frameHandler.getSuperclass() == FullFrameDecoder.class)
			{
				ChannelHandler handler = generateDecodingHandler(
											frameHandler,
											null, 
											lenOffset, 
											lenLength);
				
				handler.writeRequested(e);
			}
			else
			{
				TelegramFieldContextList telegramFieldContextList = getPacketFields(service.getOutboundTelegramContext());
				
				ChannelHandler handler = generateDecodingHandler(
											frameHandler,
											telegramFieldContextList, 
											lenOffset, 
											lenLength);
				
				handler.writeRequested(e);
			}
			
			super.writeRequested(e);
			
		}catch (IllegalArgumentException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (InstantiationException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (IllegalAccessException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (InvocationTargetException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (SecurityException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (NoSuchMethodException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (SQLException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (IOException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (ClassNotFoundException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	public void messageReceived(DataEvent e)
	{
		Object d = e.getMessage();
		if(!(d instanceof AccumByteBuffer))
		{
			super.messageReceived(e);
			return;
		}
		
		AccumByteBuffer accumBuffer = (AccumByteBuffer)d;
		
		try {
			ByteBuffer headerBuffer = accumBuffer.copy(sysHeaderFields.getTelegramLength());
			
			int idOffset = 0;
			int idLength = 0;
			int lenOffset = 0;
			int lenLength = 0;
			boolean findId = false;
			boolean findLen = false;
			TelegramFieldContext[] list = sysHeaderFields.toArray();
			
			for(int i=0; i<sysHeaderFields.size(); i++)
			{
				if(list[i].getID().equals(systemIdField))
				{
					idLength = list[i].getLength();
					findId = true;
				}
				
				if(list[i].getID().equals(systemLengthField))
				{
					lenLength = list[i].getLength();
					findLen = true;
				}
				
				if(!findId) idOffset += list[i].getLength();
				if(!findLen) lenOffset += list[i].getLength();
				if(findId && findLen) break;
			}
			
			byte[] byteServiceId = new byte[idLength];
			headerBuffer.position(idOffset);
			headerBuffer.get(byteServiceId);
			OnlineServiceContext service =  null;
			if(e.getService() == null)
			{
				String serviceId = (new String(byteServiceId)).trim();
				
				service =  resourceFactory.getOnlineService(serviceId);
				if(service == null) throw new IllegalStateException("Can't find service id : " + serviceId);
				service.setServiceIDFieldID(systemIdField);
				service.setServiceID(serviceId);
				e.setService(service);
			}
			else
			{
				service = e.getService();
				e.setService(null);
			}
			
			Class<?> frameHandler = service.getOnlineProtocol();
			
			if(frameHandler.getSuperclass() == FullFrameDecoder.class)
			{
				ChannelHandler handler = generateDecodingHandler(
											frameHandler,
											null, 
											lenOffset, 
											lenLength);
				handler.messageReceived(e);
			}
			else
			{

				
				TelegramFieldContextList telegramFieldContextList = getPacketFields(service.getInboundTelegramContext());
				
				ChannelHandler handler = generateDecodingHandler(
											frameHandler,
											telegramFieldContextList, 
											lenOffset, 
											lenLength);
				handler.messageReceived(e);
			}
			
			if(e.getChannelResult().getCause() instanceof BufferUnderflowException)
			{
				return;
			}
			
			super.messageReceived(e);
			
		} catch (IllegalArgumentException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (InstantiationException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (IllegalAccessException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (InvocationTargetException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (SecurityException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (NoSuchMethodException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (SQLException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (IOException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (ClassNotFoundException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	private boolean checkSubclassing(Class<?> parent, Class<?> children)
	{
		if(children.getSuperclass() == null) return false;
		
		if(children.getSuperclass() != parent)
		{
			return checkSubclassing(parent, children.getSuperclass());
		}
		else
		{
			return true;
		}
	}
	
	private ChannelHandler generateDecodingHandler(Class<?> frameHandler, TelegramFieldContextList telegramFieldList, int lenOffset, int lenLength) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, KeyDuplicateException
	{
		ChannelHandler handler = null;
		if(checkSubclassing(LengthFieldFrameDecoder.class, frameHandler))
		{
			Constructor<?> constructor = frameHandler.getConstructor(
					new Class[]{NetworkContext.class, TelegramFieldContextList.class, Integer.TYPE, Integer.TYPE});
			
			Object protocolObj = constructor.newInstance(networkContext, telegramFieldList, lenOffset, lenLength);
			
			handler = (LengthFieldFrameDecoder)protocolObj;
		}
		else if(checkSubclassing(FixedLengthFrameDecoder.class, frameHandler))
		{
			Constructor<?> constructor = frameHandler.getConstructor(
					new Class[]{NetworkContext.class, TelegramFieldContextList.class});
			
			Object protocolObj = constructor.newInstance(networkContext, telegramFieldList);
			
			handler = (FixedLengthFrameDecoder)protocolObj;
		}
		else if(checkSubclassing(DelimiterFrameDecoder.class, frameHandler))
		{
			Constructor<?> constructor = frameHandler.getConstructor(
					new Class[]{NetworkContext.class, TelegramFieldContextList.class});
			
			Object protocolObj = constructor.newInstance(networkContext, telegramFieldList);
			
			handler = (DelimiterFrameDecoder)protocolObj;
		}
		else if(checkSubclassing(FullFrameDecoder.class, frameHandler))
		{
			Constructor<?> constructor = frameHandler.getConstructor(new Class[]{String.class});
			
			Object protocolObj = constructor.newInstance(networkEncoding);
			
			handler = (FullFrameDecoder)protocolObj;
		}
		else
		{
			throw new IllegalStateException("Protocol type.");
		}
		
		return handler;
	}
	
	private TelegramFieldContextList getPacketFields(TelegramContext telegramContext) throws SQLException
	{
		TelegramFieldContextList fields = null;
		if(telegramContext.getHeaderID() != null && !telegramContext.getHeaderID().equals(""))
		{
			fields = getPacketFields(resourceFactory.getTelegram(telegramContext.getHeaderID()));
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
