package com.nabsys.web;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.flxb.FLXB;
import com.nabsys.net.socket.channel.handler.util.IKeepAlive;

public class KeepAliveManager implements IKeepAlive
{
	public Object generateKeepAliveMessage() {
		NBFields keepMessage = new NBFields();
		keepMessage.put(FLXB.SERVICE_ID_FIELD_NAME, FLXB.KEEP_ALIVE_SERVICE);
		keepMessage.put(FLXB.RETURN_FIELD_NAME, FLXB.SUCCESS);
		keepMessage.put(FLXB.REPLY_YN, FLXB.REP_N);
		
		return keepMessage;
	}
}
