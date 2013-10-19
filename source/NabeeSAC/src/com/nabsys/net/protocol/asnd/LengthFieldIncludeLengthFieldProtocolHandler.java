package com.nabsys.net.protocol.asnd;

import com.nabsys.resource.NetworkContext;
import com.nabsys.resource.TelegramFieldContextList;

public class LengthFieldIncludeLengthFieldProtocolHandler extends
		AssignedProtocolHandler {
	//길이필드의 데이터에 길이필드의 길이가 포함된 경우 경우 길이필드의 길이만큼 빼 주어야 됨....
	public LengthFieldIncludeLengthFieldProtocolHandler(
			NetworkContext networkContext,
			TelegramFieldContextList telegramFieldArrayContextList,
			int lengthFieldOffset, int lengthFieldLength) 
	{
		super(networkContext, telegramFieldArrayContextList, lengthFieldOffset,
				lengthFieldLength, -(lengthFieldOffset + lengthFieldLength));
	}

}
