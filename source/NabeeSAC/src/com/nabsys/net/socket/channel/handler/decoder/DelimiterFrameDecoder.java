package com.nabsys.net.socket.channel.handler.decoder;

import com.nabsys.common.util.AccumByteBuffer;

public class DelimiterFrameDecoder extends FrameDecoder{

	private byte delimiter;
	private int maxFrameSize;
	
	public DelimiterFrameDecoder(byte delimiter, int maxFrameSize)
	{
		if(maxFrameSize < 0)
		{
			throw new IllegalStateException("maxFrameSize must be a non-negative integer: " + maxFrameSize);
		}
		
		this.delimiter = delimiter;
		this.maxFrameSize = maxFrameSize;
	}
	
	@Override
	protected Object decode(AccumByteBuffer buffer) {
		AccumByteBuffer frameAccume = new AccumByteBuffer();
		frameAccume.write(buffer, delimiter, maxFrameSize);
		return frameAccume.getBuffer();
	}

}
