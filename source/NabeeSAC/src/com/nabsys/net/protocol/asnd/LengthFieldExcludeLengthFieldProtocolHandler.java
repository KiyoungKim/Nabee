package com.nabsys.net.protocol.asnd;

import com.nabsys.resource.NetworkContext;
import com.nabsys.resource.TelegramFieldContextList;

public class LengthFieldExcludeLengthFieldProtocolHandler extends
		AssignedProtocolHandler {
//길이필드의 데이터에 길이필드의 길이는 포함되지 않은 경우 따로 조정할 필요가없음
	public LengthFieldExcludeLengthFieldProtocolHandler(
			NetworkContext networkContext,
			TelegramFieldContextList telegramFieldArrayContextList,
			int lengthFieldOffset, int lengthFieldLength) 
	{
		super(networkContext, telegramFieldArrayContextList, lengthFieldOffset,
				lengthFieldLength, 0);
	}

}
