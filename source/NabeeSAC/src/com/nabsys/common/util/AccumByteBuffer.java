package com.nabsys.common.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.nabsys.common.logger.NLogger;

class ReturnValue {
	protected boolean isEnd = false;
	protected ByteBuffer buffer = null;
}
public class AccumByteBuffer extends ArrayList<ByteBuffer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5691010890663163453L;

	private int length = 0;
	public AccumByteBuffer()
	{
		super();
	}
	
	public synchronized int getLength()
	{
		return this.length;
	}
	NLogger logger = NLogger.getLogger(this.getClass());
	public synchronized void write(ByteBuffer buffer)
	{
		ByteBuffer tmp = ByteBuffer.allocateDirect(buffer.remaining()); 
		while(buffer.remaining() > 0) tmp.put(buffer.get());
		buffer.clear();
		tmp.flip();
		super.add(tmp);
		length += tmp.limit();
	}
	
	public synchronized ByteBuffer copy(int length)
	{
		if(this.length < length)
		{
			throw new IllegalStateException("Buffer underflow exception.");
		}
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(length);
		int index = 0;
		
		while(buffer.position() < length)
		{
			ByteBuffer tmp = get(index);
			tmp.mark();
			index++;
			
			if(tmp.remaining()  > length - buffer.position())
			{
				for(int i=0; i<length; i++) buffer.put(tmp.get());
				tmp.reset();
			}
			else
			{
				buffer.put(tmp);
				tmp.reset();
			}
		}
		
		buffer.flip();
		return buffer;
	}
	
	public synchronized void write(ByteBuffer buffer, int length)
	{
		ByteBuffer tmp = ByteBuffer.allocateDirect(length); 
		for(int i=0; i<length; i++) tmp.put(buffer.get());
		tmp.flip();
		super.add(tmp);
		this.length += length;
	}
	
	public synchronized void write(ByteBuffer buffer, byte delimiter)
	{
		int bufferSize = 0;
		buffer.mark();
		while(buffer.remaining() > 0)
		{
			bufferSize++;
			if(buffer.get() == delimiter) break;
		}
		buffer.reset();
			
		ByteBuffer tmp = ByteBuffer.allocateDirect(bufferSize); 
		while(buffer.remaining() > 0) tmp.put(buffer.get());
		buffer.clear();
		tmp.flip();
		super.add(tmp);
		length += tmp.limit();
	}
	
	public synchronized boolean write(AccumByteBuffer buffer, byte delimiter, int maxFrameSize)
	{
		ReturnValue returnValue = buffer.getBuffer(delimiter, maxFrameSize);
		write(returnValue.buffer);
		return returnValue.isEnd;
	}
	
	private synchronized ReturnValue getBuffer(byte delimiter, int maxFrameSize)
	{
		int buffSize 				= 0;
		int index 					= 0;
		int startPosition 			= 0;
		int endPosition 			= 0;
		
		boolean containDelimiter 	= false;

		int accumByteSize = 0;
		for(int i=0; i<size(); i++)
		{
			ByteBuffer tmp = get(i);
			startPosition = tmp.position();
			tmp.mark();
			index = i;


			while(accumByteSize <= maxFrameSize && tmp.remaining() > 0)
			{
				if(tmp.get() == delimiter)
				{
					i = size();
					containDelimiter = true;
					break;
				}
				
				accumByteSize++;
			}

			endPosition = tmp.position();

			buffSize += (endPosition - startPosition);
			tmp.reset();
		}
		
		ReturnValue returnValue = new ReturnValue();
		returnValue.buffer = ByteBuffer.allocateDirect(buffSize);
		returnValue.isEnd = containDelimiter;
		
		for(int i=0; i<=index; i++)
		{
			ByteBuffer tmp = get(0);
			
			if(i == index && tmp.limit() == endPosition)
			{
				returnValue.buffer.put(tmp);
				this.length -= tmp.limit();
				tmp.clear();
				remove(0);
			}
			else if(i == index)
			{
				for(int j=0; j<(endPosition - startPosition); j++) returnValue.buffer.put(tmp.get());
				this.length -= (endPosition - startPosition);
			}
			else
			{
				returnValue.buffer.put(tmp);
				this.length -= tmp.limit();
				tmp.clear();
				remove(0);
			}
			
			
		}
		returnValue.buffer.flip();
		
		return returnValue;
	}
	
	public synchronized boolean write(AccumByteBuffer buffer, byte delimiter)
	{
		ReturnValue returnValue = buffer.getBuffer(delimiter);
		write(returnValue.buffer);
		return returnValue.isEnd;
	}
	
	private synchronized ReturnValue getBuffer(byte delimiter)
	{
		int buffSize 				= 0;
		int index 					= 0;
		int startPosition 			= 0;
		int endPosition 			= 0;
		
		boolean containDelimiter 	= false;

		for(int i=0; i<size(); i++)
		{
			ByteBuffer tmp = get(i);
			startPosition = tmp.position();
			tmp.mark();
			index = i;


			while(tmp.remaining() > 0)
			{
				if(tmp.get() == delimiter)
				{
					i = size();
					containDelimiter = true;
					break;
				}
			}

			endPosition = tmp.position();

			buffSize += (endPosition - startPosition);
			tmp.reset();
		}
		
		ReturnValue returnValue = new ReturnValue();
		returnValue.buffer = ByteBuffer.allocateDirect(buffSize);
		returnValue.isEnd = containDelimiter;
		
		for(int i=0; i<=index; i++)
		{
			ByteBuffer tmp = get(0);
			
			if(i == index && tmp.limit() == endPosition)
			{
				returnValue.buffer.put(tmp);
				this.length -= tmp.limit();
				tmp.clear();
				remove(0);
			}
			else if(i == index)
			{
				for(int j=0; j<(endPosition - startPosition); j++) returnValue.buffer.put(tmp.get());
				this.length -= (endPosition - startPosition);
			}
			else
			{
				returnValue.buffer.put(tmp);
				this.length -= tmp.limit();
				tmp.clear();
				remove(0);
			}
			
			
		}
		returnValue.buffer.flip();
		
		return returnValue;
	}
	
	public synchronized ByteBuffer getBuffer(int length)
	{
		if(length > this.length)
		{
			return getBuffer();
		}
		else
		{
			ByteBuffer buffer = ByteBuffer.allocateDirect(length);
			while(buffer.position() < length)
			{
				ByteBuffer tmp = get(0);
				
				if(tmp.remaining()  > length - buffer.position()) //���°��
				{
					for(int i=0; i<length; i++) buffer.put(tmp.get());
					this.length -= length;
				}
				else
				{
					int ramaining = tmp.remaining();
					buffer.put(tmp);

					this.length -= ramaining;

					tmp.clear();
					remove(0);
				}
			}
			buffer.flip();
			return buffer;
		}
	}
	
	public synchronized ByteBuffer getBuffer()
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect(length);
		while(size() > 0)
		{
			buffer.put(get(0));
			get(0).clear();
			remove(0);
		}
		
		length = 0;
		buffer.flip();
		return buffer;
	}
}
