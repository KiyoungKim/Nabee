package com.nabsys.net.socket.channel.handler.decoder;

import java.nio.ByteBuffer;

import com.nabsys.common.util.AccumByteBuffer;
import com.nabsys.net.socket.channel.handler.FieldType;

public class LengthFieldFrameDecoder extends FrameDecoder {

	private int lengthFieldOffset;
	private int lengthFieldLength;
	private int lengthAdjustment;
	private FieldType lengthFieldType;
	
	public LengthFieldFrameDecoder(
			int lengthFieldOffset, 
			int lengthFieldLength, 
			int lengthAdjustment,
			FieldType lengthFieldType)
	{
		if(lengthFieldOffset < 0)
		{
			throw new IllegalStateException("lengthFieldOffset must be a non-negative integer: " + lengthFieldOffset);
		}
		
		if(lengthFieldLength <= 0)
		{
			throw new IllegalStateException("lengthFieldLength must be a positive integer: " + lengthFieldLength);
		}
		
		this.lengthFieldOffset = lengthFieldOffset;
		this.lengthFieldLength = lengthFieldLength;
		this.lengthAdjustment = lengthAdjustment;
		this.lengthFieldType = lengthFieldType;
	}
	
	@Override
	protected Object decode(AccumByteBuffer accumBuffer) 
	{
		int firstBytesToRead = lengthFieldOffset + lengthFieldLength;

		ByteBuffer firstReadBuffer = accumBuffer.getBuffer(firstBytesToRead);

		AccumByteBuffer frameAccume = new AccumByteBuffer();
		frameAccume.write(firstReadBuffer);
		
		int lengthFieldData = 0;
		byte[] byteLengthField = new byte[lengthFieldLength];
		
		if(this.lengthFieldType == FieldType.HEX_LENGTH_FIELD)
		{
			lengthFieldData = firstReadBuffer.getInt(lengthFieldOffset);
		}
		else if(this.lengthFieldType == FieldType.STR_LENGTH_FIELD)
		{
			firstReadBuffer.get(byteLengthField, lengthFieldOffset, lengthFieldLength);
			lengthFieldData = Integer.parseInt(new String(byteLengthField));
		}

		frameAccume.write(accumBuffer.getBuffer(lengthFieldData + lengthAdjustment));
		
		ByteBuffer frame = frameAccume.getBuffer();

		return frame;
	}

}
