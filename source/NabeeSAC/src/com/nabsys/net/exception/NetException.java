package com.nabsys.net.exception;

import com.nabsys.common.label.NLabel;

public class NetException extends RuntimeException {

	public NetException(int code) {
		// TODO Auto-generated constructor stub
		super(NLabel.get(code));
	}
	
	public NetException(String message) {
		// TODO Auto-generated constructor stub
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7315354850109588281L;

}
