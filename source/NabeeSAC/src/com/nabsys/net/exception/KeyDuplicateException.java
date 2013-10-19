package com.nabsys.net.exception;

import com.nabsys.common.label.NLabel;

public class KeyDuplicateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5700959462901734120L;

	public KeyDuplicateException(int code) {
		super(NLabel.get(code));
	}

	public KeyDuplicateException(String message) {
		super(message);
	}
}
