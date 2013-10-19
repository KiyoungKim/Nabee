package com.nabsys.net.socket.channel.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.BufferUnderflowException;

import com.nabsys.common.util.AccumByteBuffer;
import com.nabsys.net.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.handler.decoder.DelimiterFrameDecoder;
import com.nabsys.net.socket.channel.handler.decoder.FixedLengthFrameDecoder;
import com.nabsys.net.socket.channel.handler.decoder.FullFrameDecoder;
import com.nabsys.net.socket.channel.handler.decoder.LengthFieldFrameDecoder;
import com.nabsys.resource.NetworkContext;
import com.nabsys.resource.TelegramFieldContextList;

public class MappingFrameAssignHandler extends ChannelHandler{
	
	private int lengthFieldOffset;
	private int lengthFieldLength;
	private int lengthFieldAdjustment;
	private TelegramFieldContextList inboundTelegramFieldContextList = null;
	private TelegramFieldContextList outboundTelegramFieldContextList = null;
	private String encoding;
	private NetworkContext networkContext = null;
	Class<?> frameHandler = null;
	
	public MappingFrameAssignHandler(Class<?> frameHandler, String idFieldID, String lengthFieldID, int lengthFieldOffset, int lengthFieldLength, int lengthFieldAdjustment, String encoding)
	{
		super();
		this.lengthFieldOffset = lengthFieldOffset;
		this.lengthFieldLength = lengthFieldLength;
		this.lengthFieldAdjustment = lengthFieldAdjustment;
		this.frameHandler = frameHandler;
		this.encoding = encoding;
		//MappingFrameAssignHandler 에서 NetworkContext의 BufferSize와 ReadTimeout 은 사용하지 않음.. 기본으로 등록
		//ConnectionPool에서 외부 설정으로 부터 해당 정보가 등록 됨.
		networkContext = new NetworkContext(1024, 3000, encoding, lengthFieldID, idFieldID);
	}
	
	public void setInboundTelegramFieldContextList(TelegramFieldContextList inboundTelegramFieldContextList)
	{
		this.inboundTelegramFieldContextList = inboundTelegramFieldContextList;
	}
	
	public void setOutboundTelegramFieldContextList(TelegramFieldContextList outboundTelegramFieldContextList)
	{
		this.outboundTelegramFieldContextList = outboundTelegramFieldContextList;
	}
	
	public void writeRequested(DataEvent e)
	{
		Object d = e.getMessage();
		if(!(d instanceof NBFields))
		{
			super.messageReceived(e);
			return;
		}
		
		try{
			if(outboundTelegramFieldContextList != null)
			{
				ChannelHandler handler = generateDecodingHandler(
												frameHandler,
												outboundTelegramFieldContextList, 
												lengthFieldOffset, 
												lengthFieldLength,
												lengthFieldAdjustment);
				handler.writeRequested(e);
				super.writeRequested(e);
			}
			else
			{
				ChannelHandler handler = generateDecodingHandler(
												frameHandler,
												null, 
												lengthFieldOffset, 
												lengthFieldLength,
												lengthFieldAdjustment);
				handler.writeRequested(e);
				super.writeRequested(e);
			}
			
			
			
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
		
		try {
			if(inboundTelegramFieldContextList != null)
			{
				ChannelHandler handler = generateDecodingHandler(
											frameHandler,
											inboundTelegramFieldContextList, 
											lengthFieldOffset, 
											lengthFieldLength,
											lengthFieldAdjustment);
				
				handler.messageReceived(e);
			}
			else
			{
				ChannelHandler handler = generateDecodingHandler(
										frameHandler,
										null, 
										lengthFieldOffset, 
										lengthFieldLength,
										lengthFieldAdjustment);
				
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
	
	private ChannelHandler generateDecodingHandler(Class<?> frameHandler, TelegramFieldContextList telegramFieldList, int lenOffset, int lenLength, int adjustment) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, KeyDuplicateException
	{
		ChannelHandler handler = null;
		if(checkSubclassing(LengthFieldFrameDecoder.class, frameHandler))
		{
			Constructor<?> constructor = frameHandler.getConstructor(
					new Class[]{NetworkContext.class, TelegramFieldContextList.class, Integer.TYPE, Integer.TYPE, Integer.TYPE});
			
			Object protocolObj = constructor.newInstance(networkContext, telegramFieldList, lenOffset, lenLength, adjustment);
			
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
			
			Object protocolObj = constructor.newInstance(encoding);
			
			handler = (FullFrameDecoder)protocolObj;
		}
		else
		{
			throw new IllegalStateException("Protocol type.");
		}
		
		return handler;
	}
}
