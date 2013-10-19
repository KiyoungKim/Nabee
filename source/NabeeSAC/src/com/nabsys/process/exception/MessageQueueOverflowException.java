package com.nabsys.process.exception;

import com.nabsys.common.label.NLabel;

public class MessageQueueOverflowException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8986978891740450432L;

	public MessageQueueOverflowException(String serviceName)
	{
		super("[" + serviceName + "] " + NLabel.get(0x0067));
	}
}
