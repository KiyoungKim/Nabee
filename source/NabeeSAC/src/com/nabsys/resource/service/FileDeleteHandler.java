package com.nabsys.resource.service;

import java.util.HashMap;

import com.nabsys.process.Context;

public class FileDeleteHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileDeleteHandler(ServiceHandler parent, int x, int y, int width,
			int height) {
		super(parent, x, y, width, height);
	}

	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		{
			ServiceHandler parentHandler = getParent();
			FileFrameHandler fileFrameHandler = null;
			while(true)
			{
				if(parentHandler instanceof FileFrameHandler)
				{
					fileFrameHandler = (FileFrameHandler)parentHandler;
					break;
				}
				else if(parentHandler == null)
				{
					throw new Exception("Can't find file frame handler");
				}
				else
				{
					parentHandler = parentHandler.getParent();
				}
			}
			if(fileFrameHandler.getFile().exists())fileFrameHandler.deleteFile();
		}
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "File Delete : " + map + "", false, false);
		}
		super.moveToNext(ctx, map);
	}
}
