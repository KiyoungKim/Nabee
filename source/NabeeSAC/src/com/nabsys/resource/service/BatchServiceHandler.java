package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;




public class BatchServiceHandler extends GateServiceHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BatchServiceHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(null, x, y, width, height);
	}
	public void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		super.execute(ctx, map);
	}

}
