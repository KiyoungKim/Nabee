package com.nabsys.common.exception;

import com.nabsys.common.label.NLabel;

public class NotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2537231521409691646L;

	public NotFoundException() {
		super(NLabel.get(0x0044));
	}
	
	public NotFoundException(String message)
	{
		super(message);
	}
}
