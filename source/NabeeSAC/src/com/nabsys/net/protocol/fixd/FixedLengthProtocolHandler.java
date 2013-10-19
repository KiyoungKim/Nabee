package com.nabsys.net.protocol.fixd;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.nabsys.common.util.AccumByteBuffer;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.handler.decoder.FixedLengthFrameDecoder;
import com.nabsys.resource.NetworkContext;
import com.nabsys.resource.TelegramFieldContext;
import com.nabsys.resource.TelegramFieldContextList;

public class FixedLengthProtocolHandler extends FixedLengthFrameDecoder{
	
	private TelegramFieldContext[] telegramFieldArray;
	private NetworkContext networkContext;
	
	public FixedLengthProtocolHandler(NetworkContext networkContext, TelegramFieldContextList telegramFieldArrayContextList) 
	{
		super(telegramFieldArrayContextList.getTelegramLength());
		this.telegramFieldArray = telegramFieldArrayContextList.toArray();
		this.networkContext = networkContext;
	}
	
	public void writeRequested(DataEvent e)
	{
		Object d = e.getMessage();
		if(!(d instanceof NBFields))
		{
			super.writeRequested(e);
			return;
		}

		Object frame = null;
		try {
			frame = encode((NBFields)d);
			e.setData(frame);

			super.writeRequested(e);
		} catch (UnsupportedEncodingException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (ProtocolException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	protected Object encode(NBFields fields) throws UnsupportedEncodingException, ProtocolException
	{
		int packetLength = super.length;
		ByteBuffer buffer = ByteBuffer.allocateDirect(packetLength);
		for(int i=0; i<telegramFieldArray.length; i++)
		{
			telegramFieldArray[i].setConvertData(buffer, fields, networkContext.getServerEncoding());
		}
		
		return buffer;
	}
	
	protected Object decode(AccumByteBuffer accumBuffer)
	{
		ByteBuffer buffer = (ByteBuffer)super.decode(accumBuffer);
		
		NBFields rtnFields = null;
		
		try {
			rtnFields = getNBFieldsFromBuffer(buffer);
		} catch (UnsupportedEncodingException e) {
		}
		
		return rtnFields;
	}
	
	private NBFields getNBFieldsFromBuffer(ByteBuffer buffer) throws UnsupportedEncodingException
	{
		NBFields rtnFields = new NBFields();
		
		for(int i=0; i<telegramFieldArray.length; i++)
		{
			rtnFields.put(telegramFieldArray[i].getID(), 
					telegramFieldArray[i].getConvertData(buffer, networkContext.getServerEncoding()));
		}
		
		return rtnFields;
	}
	
}
