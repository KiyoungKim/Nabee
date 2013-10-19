package com.nabsys.resource.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.nabsys.process.Context;
import com.nabsys.resource.InstanceContext;

public class FileWriteHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedHashMap<String, Object[]> fileMap = null;
	private int capacity = 0;
	private int SIZE = 0;
	private int TYPE = 1;
	private int PADDING = 2;
	public FileWriteHandler(ServiceHandler parent, int x, int y, int width,
			int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(LinkedHashMap<String, Object[]> fileMap)
	{
		this.fileMap = fileMap;
		capacity = 0;
		if(fileMap != null)
		{
			Iterator<String> itr = fileMap.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				capacity += (Integer)fileMap.get(key)[SIZE];
			}
		}
	}
	
	public LinkedHashMap<String, Object[]> getFileMap()
	{
		return this.fileMap;
	}
	
	private void checkDir(File dir)
	{
		if(!dir.exists())
		{
			if(dir.getParentFile() != null) checkDir(dir.getParentFile());
			else return;
		}
		else
		{
			return;
		}
		dir.mkdir();
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
			
			FileChannel channel = fileFrameHandler.getFileWriteChannel();
			if(channel == null)
			{
				if(!fileFrameHandler.getFile().exists())
				{
					checkDir(fileFrameHandler.getFile().getParentFile());
					fileFrameHandler.getFile().createNewFile();
				}
				
				FileOutputStream fileOutputStream = new FileOutputStream(fileFrameHandler.getFile(), true);
				channel = fileOutputStream.getChannel();
				
				fileFrameHandler.setFileOutputStream(fileOutputStream);
				fileFrameHandler.setFileWriteChannel(channel);
			}
			
			Iterator<String> itr = fileMap.keySet().iterator();
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity + 1);
			int offset = 0;
			InstanceContext instance = ctx.getResourceFactory().getInstanceConfiguration();
			String encoding = instance.getFileEncoding();
			while(itr.hasNext()){
				String key = itr.next();
				Object src = map.get(key);
				
				setBuffer(src, byteBuffer, fileMap.get(key), encoding);
					
				offset += (Integer)fileMap.get(key)[SIZE];
				int position = byteBuffer.position();
				
				if(offset > position)
				{
					byte padding = (byte) ((String)fileMap.get(key)[PADDING]).toCharArray()[0];
					for(int i=0; i<offset - position; i++)byteBuffer.put(padding);
				}
				else
				{
					byteBuffer.position(offset);
				}
			}
			byteBuffer.put("\n".getBytes(encoding)[0]);
			byteBuffer.rewind();
			channel.write(byteBuffer);
		}
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "File Write : " + map + "", false, false);
		}
		super.execute(ctx, map);
	}
	
	private void setBuffer(Object src, ByteBuffer buffer, Object[] info, String encoding) throws UnsupportedEncodingException
	{
		if(info[TYPE] == String.class)
		{
			if(src instanceof String)
			{
				byte[] byteSrc = ((String)src).getBytes(encoding);
				buffer.put(byteSrc);
			}
			else if(src instanceof byte[] || src instanceof Byte)
			{
				
			}
			else
			{
				byte[] byteSrc = (src+"").getBytes(encoding);
				buffer.put(byteSrc);
			}
			
		}
		else if(info[TYPE] == Integer.class)
		{
			if(src instanceof String)
			{
				buffer.putInt(Integer.parseInt(((String)src)));
			}
			else if(src instanceof byte[] || src instanceof Byte)
			{
				
			}
			else
			{
				buffer.putInt((Integer)src);
			}
		}
		else if(info[TYPE] == Long.class)
		{
			if(src instanceof String)
			{
				buffer.putLong(Long.parseLong(((String)src)));
			}
			else if(src instanceof byte[] || src instanceof Byte)
			{
				
			}
			else
			{
				buffer.putLong((Long)src);
			}
		}
		else if(info[TYPE] == Float.class)
		{
			if(src instanceof String)
			{
				buffer.putFloat(Float.parseFloat(((String)src)));
			}
			else if(src instanceof byte[] || src instanceof Byte)
			{
				
			}
			else
			{
				buffer.putFloat((Float)src);
			}
		}
		else if(info[TYPE] == Double.class)
		{
			if(src instanceof String)
			{
				buffer.putDouble(Double.parseDouble(((String)src)));
			}
			else if(src instanceof byte[] || src instanceof Byte)
			{
				
			}
			else
			{
				buffer.putDouble((Double)src);
			}
		}
		else if(info[TYPE] == Byte.class)
		{
			if(src instanceof Byte)
			{
				buffer.put((Byte)src);
			}
			else
			{
				
			}
		}
		else if(info[TYPE] == byte[].class)
		{
			if(src instanceof byte[])
			{
				buffer.put((byte[])src);
			}
			else
			{
			
			}
			
		}
	}
}
