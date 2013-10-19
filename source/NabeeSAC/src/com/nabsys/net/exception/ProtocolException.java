package com.nabsys.net.exception;

import com.nabsys.common.label.NLabel;

public class ProtocolException  extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1750439579766674870L;

	public ProtocolException(int code) {
		// TODO Auto-generated constructor stub
		super(NLabel.get(code));
	}
}
