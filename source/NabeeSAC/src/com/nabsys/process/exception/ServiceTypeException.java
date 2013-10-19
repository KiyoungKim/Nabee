package com.nabsys.process.exception;

import com.nabsys.common.label.NLabel;

public class ServiceTypeException  extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8143537767907604566L;

	public ServiceTypeException()
	{
		super(NLabel.get(0x0050));
	}
}
