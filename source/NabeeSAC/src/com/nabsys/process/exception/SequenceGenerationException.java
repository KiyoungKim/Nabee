package com.nabsys.process.exception;

import com.nabsys.common.label.NLabel;

public class SequenceGenerationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8234239969403390808L;

	public SequenceGenerationException(int code)
	{
		super(NLabel.get(code));
	}
}
