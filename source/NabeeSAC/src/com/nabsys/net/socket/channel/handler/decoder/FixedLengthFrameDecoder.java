package com.nabsys.net.socket.channel.handler.decoder;

import com.nabsys.common.util.AccumByteBuffer;

public class FixedLengthFrameDecoder extends FrameDecoder{

	protected int length;
	protected FixedLengthFrameDecoder(int length)
	{
		this.length = length;
	}
	
	@Override
	protected Object decode(AccumByteBuffer buffer) {
		return buffer.getBuffer(length);
	}

}
