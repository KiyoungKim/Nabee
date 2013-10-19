package com.nabsys.net.socket.channel.handler;

public enum Delimiter {
	NEW_LINE,
	NULL,
	CARRIAGE_RETURN;
	
	public byte getDelimiter()
	{
		byte rtn = 0x00;
		
		switch(this)
		{
		case NEW_LINE :
			rtn = 0x0A;
			break;
		case NULL :
			rtn = 0x00;
			break;
		case CARRIAGE_RETURN :
			rtn = 0x0D;
			break;
		default:
		}
		
		return rtn;
	}
}
