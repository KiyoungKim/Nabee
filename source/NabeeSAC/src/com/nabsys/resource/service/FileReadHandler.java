package com.nabsys.resource.service;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.nabsys.process.Context;
import com.nabsys.resource.InstanceContext;

public class FileReadHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedHashMap<String, Object[]> fileMap = null;
	private int capacity = 0;
	private int SIZE = 0;
	private int TYPE = 1;
	private int PADDING = 2;
	private static final int READ_ONCE = 0;
	private static final int READ_BY_COUNT = 1;
	private static final int READ_TL_END = 2;
	private int readType = READ_ONCE;
	private String countFieldID = "";
	public FileReadHandler(ServiceHandler parent, int x, int y, int width,
			int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(LinkedHashMap<String, Object[]> fileMap, int readType, String countFieldID)
	{
		this.fileMap = fileMap;
		this.readType = readType;
		this.countFieldID = countFieldID;
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
	
	public int getReadType()
	{
		return readType;
	}
	
	public String getCountFieldID()
	{
		return countFieldID;
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
			
			FileChannel channel = fileFrameHandler.getFileReadChannel();
			if(channel == null)
			{
				FileInputStream fileInputStream = new FileInputStream(fileFrameHandler.getFile());
				channel = fileInputStream.getChannel();
				
				fileFrameHandler.setFileInputStream(fileInputStream);
				fileFrameHandler.setFileReadChannel(channel);
			}
			
			InstanceContext instance = ctx.getResourceFactory().getInstanceConfiguration();
			if(ctx.isTest())
			{
				ctx.offerTestMessage(getHandlerID(), "File Read : " + map + "", false, false);
			}
			switch(readType)
			{
			case READ_ONCE : 
				readFileLine(channel, instance, map);
				super.moveToInside(ctx, map);
				break;
			case READ_BY_COUNT : 
				int loopCnt = 0;
				if(map.get(countFieldID) instanceof String) loopCnt = Integer.parseInt((String)map.get(countFieldID));
				else loopCnt = (Integer)map.get(countFieldID);
				for(int i=0; i<loopCnt; i++)
				{
					readFileLine(channel, instance, map);
					super.moveToInside(ctx, map);
				}
				break;
			case READ_TL_END : 
				while(readFileLine(channel, instance, map))
				{
					super.moveToInside(ctx, map);
				}
				break;
			}
			
		}
		
		super.execute(ctx, map);
	}
	
	private boolean readFileLine(FileChannel channel, InstanceContext instance, HashMap<String, Object> map) throws Exception
	{
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity + 1);
		int rtn = channel.read(byteBuffer);
		byteBuffer.rewind();
		if(rtn <= 0)
		{
			return false;
		}
		else
		{
			Iterator<String> itr = fileMap.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				Object value = null;
				
				byte padding = (byte) ((String)fileMap.get(key)[PADDING]).toCharArray()[0];
				
				if(((Class<?>)fileMap.get(key)[TYPE]) == String.class)
				{
					byte[] dest = new byte[(Integer)fileMap.get(key)[SIZE]];
					byteBuffer.get(dest);
					value = new String(dest, instance.getFileEncoding());
					((String)value).replaceAll(new String(new byte[]{padding})+"+$", "");
				}
				else if(((Class<?>)fileMap.get(key)[TYPE]) == Integer.class)
				{
					int size = (Integer)fileMap.get(key)[SIZE];
					if(size >= Integer.SIZE / Byte.SIZE)
					{
						value = byteBuffer.getInt();
						byteBuffer.position(byteBuffer.position() + (size - Integer.SIZE / Byte.SIZE));
					}
					else
					{
						throw new Exception("Type converting to integer error.");
					}
				}
				else if(((Class<?>)fileMap.get(key)[TYPE]) == Double.class)
				{
					int size = (Integer)fileMap.get(key)[SIZE];
					if(size >= Double.SIZE / Byte.SIZE)
					{
						value = byteBuffer.getDouble();
						byteBuffer.position(byteBuffer.position() + (size - Double.SIZE / Byte.SIZE));
					}
					else
					{
						throw new Exception("Type converting to double error.");
					}
				}
				else if(((Class<?>)fileMap.get(key)[TYPE]) == Float.class)
				{
					int size = (Integer)fileMap.get(key)[SIZE];
					if(size >= Float.SIZE / Byte.SIZE)
					{
						value = byteBuffer.getFloat();
						byteBuffer.position(byteBuffer.position() + (size - Float.SIZE / Byte.SIZE));
					}
					else
					{
						throw new Exception("Type converting to double error.");
					}
				}
				else if(((Class<?>)fileMap.get(key)[TYPE]) == Long.class)
				{
					int size = (Integer)fileMap.get(key)[SIZE];
					if(size >= Long.SIZE / Byte.SIZE)
					{
						value = byteBuffer.getLong();
						byteBuffer.position(byteBuffer.position() + (size - Long.SIZE / Byte.SIZE));
					}
					else
					{
						throw new Exception("Type converting to double error.");
					}
				}
				else if(((Class<?>)fileMap.get(key)[TYPE]) == byte[].class)
				{
					byte[] dest = new byte[(Integer)fileMap.get(key)[SIZE]];
					byteBuffer.get(dest);
					int index = 0;
					for(int i=dest.length -1; i>=0; i--)
					{
						if(dest[i] != padding)
						{
							index = i;
							break;
						}
					}
					
					if(index != dest.length - 1)
					{
						ByteBuffer newBuffer = ByteBuffer.wrap(dest, 0, index);
						dest = new byte[index + 1];
						newBuffer.get(dest);
					}
					value = dest;
				}
				else if(((Class<?>)fileMap.get(key)[TYPE]) == Byte.class)
				{
					int size = (Integer)fileMap.get(key)[SIZE];
					if(size >= 1)
					{
						value = byteBuffer.get();
						byteBuffer.position(byteBuffer.position() + (size - 1));
					}
					else
					{
						throw new Exception("Type converting to double error.");
					}
				}
				
				map.put(key, value);
			}
		}
		
		return true;
	}
}
