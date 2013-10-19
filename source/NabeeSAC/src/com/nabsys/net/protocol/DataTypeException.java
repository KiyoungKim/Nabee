package com.nabsys.net.protocol;

import com.nabsys.common.label.NLabel;

public class DataTypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5882959212756614262L;

	public DataTypeException(int code, String message) {
		// TODO Auto-generated constructor stub
		super(NLabel.get(code) + " : " + message);
	}
}
