package com.nabsys.net.socket.channel.handler.decoder;

import com.nabsys.common.util.AccumByteBuffer;

public class FullFrameDecoder extends FrameDecoder{

	@Override
	protected Object decode(AccumByteBuffer buffer) {
		return buffer.getBuffer();
	}

}
