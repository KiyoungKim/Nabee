package com.nabsys.net.socket.channel.handler.decoder;

import java.nio.BufferUnderflowException;

import com.nabsys.common.util.AccumByteBuffer;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.handler.ChannelHandler;

public abstract class FrameDecoder extends ChannelHandler{
	
	protected abstract Object decode(AccumByteBuffer buffer);
	
	public synchronized void messageReceived(DataEvent e)
	{
		Object d = e.getMessage();
		if(!(d instanceof AccumByteBuffer))
		{
			super.messageReceived(e);
			return;
		}

		try{
			Object frame = decode((AccumByteBuffer)d);
			e.setData(frame);
		}catch(BufferUnderflowException ex){
			e.getChannelResult().setFailure(ex);
			return;
		}
		e.getChannelResult().setSuccess();
		super.messageReceived(e);
	}
			
}
