package com.nabsys.net.exception;

import java.io.IOException;

import com.nabsys.common.label.NLabel;

public class SocketClosedException extends IOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7811935602345937935L;

	public SocketClosedException(int code) {
		// TODO Auto-generated constructor stub
		super(NLabel.get(code));
	}
}
