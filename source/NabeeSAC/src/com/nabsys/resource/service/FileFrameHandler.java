package com.nabsys.resource.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nabsys.common.util.DateUtil;
import com.nabsys.process.Context;

public class FileFrameHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fileNamePattern = null;
	private FileChannel fileReadChannel = null;
	private FileChannel fileWriteChannel = null;
	private FileOutputStream fileOutputStream = null;
	private FileInputStream fileInputStream = null;
	private File frameFile = null;
	public FileFrameHandler(ServiceHandler parent, int x, int y, int width,
			int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String fileNamePattern)
	{
		this.fileNamePattern = fileNamePattern;
	}
	
	public String getFileNamePattern()
	{
		return this.fileNamePattern;
	}
	
	protected FileChannel getFileReadChannel(){
		return fileReadChannel;
	}
	
	protected FileChannel getFileWriteChannel(){
		return fileWriteChannel;
	}
	
	protected void setFileReadChannel(FileChannel channel){
		this.fileReadChannel = channel;
	}
	
	protected void setFileWriteChannel(FileChannel channel){
		this.fileWriteChannel = channel;
	}
	
	
	
	protected void setFileOutputStream(FileOutputStream fileOutputStream){
		this.fileOutputStream = fileOutputStream;
	}
	
	protected void setFileInputStream(FileInputStream fileInputStream){
		this.fileInputStream = fileInputStream;
	}
	
	protected File getFile()
	{
		return frameFile;
	}
	
	protected void deleteFile() throws IOException
	{
		if(fileReadChannel!=null)
		{
			fileReadChannel.close();
			fileReadChannel = null;
		}
		if(fileWriteChannel!=null)
		{
			fileWriteChannel.close();
			fileWriteChannel = null;
		}
		if(fileInputStream != null)
		{
			fileInputStream.close();
			fileInputStream = null;
		}
		if(fileOutputStream != null)
		{
			fileOutputStream.close();
			fileOutputStream = null;
		}
		frameFile.delete();
	}

	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		{
			DateUtil du = new DateUtil();
			Pattern ptrn = Pattern.compile("\\[.*\\]");
			Matcher matcher = ptrn.matcher(fileNamePattern);
			String dateString = "";
			if(matcher.find())
			{
				 dateString = du.getCurrentDate(fileNamePattern.substring(matcher.start() + 1, matcher.end() -1));
			}
			String filePath = fileNamePattern.replaceAll("\\[.*\\]", dateString);
			File file = new File(filePath);
			if(file.isFile() || !file.exists())
			{
				frameFile = file;
				try{
					if(ctx.isTest())
					{
						ctx.offerTestMessage(getHandlerID(), "File : " + map + "", false, false);
					}
					moveToInside(ctx, map);
				}finally{
					if(fileReadChannel!=null)
					{
						fileReadChannel.close();
						fileReadChannel = null;
					}
					if(fileWriteChannel!=null)
					{
						fileWriteChannel.close();
						fileWriteChannel = null;
					}
					if(fileInputStream != null)
					{
						fileInputStream.close();
						fileInputStream = null;
					}
					if(fileOutputStream != null)
					{
						fileOutputStream.close();
						fileOutputStream = null;
					}
				}
			}
			else if(file.isDirectory())
			{
				File[] files = file.listFiles();
				if(ctx.isTest())
				{
					ctx.offerTestMessage(getHandlerID(), "File : " + map + "", false, false);
				}
				for(int i=0; i<files.length; i++)
				{
					frameFile = files[i];
			
					try{
						moveToInside(ctx, map);
					}finally{
						if(fileReadChannel!=null)
						{
							fileReadChannel.close();
							fileReadChannel = null;
						}
						if(fileWriteChannel!=null)
						{
							fileWriteChannel.close();
							fileWriteChannel = null;
						}
						if(fileInputStream != null)
						{
							fileInputStream.close();
							fileInputStream = null;
						}
						if(fileOutputStream != null)
						{
							fileOutputStream.close();
							fileOutputStream = null;
						}
					}
				}
			}
		}
		
		super.execute(ctx, map);
	}
}
