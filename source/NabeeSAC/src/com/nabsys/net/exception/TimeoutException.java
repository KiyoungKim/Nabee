package com.nabsys.net.exception;

import com.nabsys.common.label.NLabel;

public class TimeoutException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1741211846718192410L;

	public TimeoutException(int code) {
		// TODO Auto-generated constructor stub
		super(NLabel.get(code));
	}
}
