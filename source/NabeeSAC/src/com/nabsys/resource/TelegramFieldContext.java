package com.nabsys.resource;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.protocol.NBFields;


public class TelegramFieldContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id = null;
	private String name = null;
	private int index = 0; 
	private int length = 0; 
	private boolean mandatory = false; 
	private char padding = 0;
	private char align = 0;
	private char type = 0;
	private String remark = null;
	public TelegramFieldContext(String id, String name, int index, int length, boolean mandatory, char padding, char align, char type, String remark)
	{
		this.id = id;
		this.name = name;
		this.index = index; 
		this.length = length; 
		this.mandatory = mandatory; 
		this.padding = padding;
		this.align = align;
		this.type = type;
		this.remark = remark;
	}
	
	public String getID(){
		return id;
	}
	public String getName(){
		return name;
	}
	public int getIndex(){
		return index;
	}
	public int getLength(){
		return length;
	}
	public boolean isMandatory(){
		return mandatory;
	}
	public char getPadding(){
		return padding;
	}
	public char getAlign(){
		return align;
	}
	public char getType(){
		return type;
	}
	public String getRemark(){
		return remark;
	}
	private byte[] removePadding(byte[] buff)
	{
		byte paddingChar = (byte)getPadding();
		int start = 0;
		int length = 0;

		if(getAlign() == 'L')
		{
			length = buff.length;
			for(int i=buff.length - 1; i>=0; i--)
			{
				if(buff[i] != paddingChar)
				{
					break;
				}
				length--;
			}
		}
		else if(getAlign() == 'R')
		{
			for(int i=0; i<buff.length; i++)
			{
				if(buff[i] != paddingChar)
				{
					start = i;
					length = buff.length - start;
					break;
				}
			}
		}
		else if(getAlign() == 'C')
		{
			for(int i=0; i<buff.length; i++)
			{
				if(buff[i] != paddingChar)
				{
					start = i;
					break;
				}
			}
			
			length = buff.length - start;
			for(int i=buff.length - 1; i>=start; i--)
			{
				if(buff[i] != paddingChar)
				{
					break;
				}
				length--;
			}
		}
		
		if(getType() == 'N' && start == 0 && length == 0)
		{
			length = 1;
			start = buff.length -1;
		}
		
		byte[] rtnBuff = new byte[length];
		System.arraycopy(buff, start, rtnBuff, 0, length);
		return rtnBuff;
	}
	
	private byte[] setPadding(NBFields fields, byte paddingChar, int fieldLen, String encoding) throws UnsupportedEncodingException
	{
		
		byte tmp[] = new byte[fieldLen];
		Arrays.fill(tmp, paddingChar);
		byte[] src = null;
		int startPoint = 0;
		String tmpStr = "";
		
		switch(getType())
		{
		case('C') :
			if(fields.containsKey(getID()))
				tmpStr =(String)fields.get(getID());

			src = tmpStr.getBytes(encoding);
			
			break;
		case('N') :
			if(fields.containsKey(getID()))
				tmpStr =fields.get(getID())+"";
			
			src = tmpStr.getBytes(encoding);
			
			break;
		case('A') :
			src = (byte[])fields.get(getID());
			break;
		default:
		}

		if(getAlign() == 'L')
		{
		}
		else if(getAlign() == 'C')
		{
			if(fieldLen > src.length)
				startPoint = (fieldLen - src.length)/2;
		}
		else if(getAlign() == 'R')
		{
			startPoint = fieldLen - src.length;
		}
		
		int length = 0;
		if(src.length < fieldLen)
			length = src.length;
		else
			length = fieldLen;
		
		System.arraycopy(src, 0, tmp, startPoint, length);
		
		return tmp;
	}
	public void setConvertData(ByteBuffer buff, NBFields fields, String encoding) throws UnsupportedEncodingException, ProtocolException
	{
		byte paddingChar = (byte)getPadding();
		int fieldLen = (Integer)getLength();
		if(fieldLen <= 0) return;
		if(isMandatory() && !fields.containsKey(getID()))
			throw new ProtocolException(0x004D);
		switch(getType())
		{
		case('C') :
			buff.put(setPadding(fields, paddingChar, fieldLen, encoding));
			break;
		case('N') :
			buff.put(setPadding(fields, paddingChar, fieldLen, encoding));
			break;
		case('I') :
			buff.putInt((Integer)fields.get(getID()));
			break;
		case('D') :
			buff.putDouble((Double)fields.get(getID()));
			break;
		case('F') :
			buff.putFloat((Float)fields.get(getID()));
			break;
		case('L') :
			buff.putLong((Long)fields.get(getID()));
			break;
		case('B') :
			buff.put((Byte)fields.get(getID()));
			break;
		case('A') :
			buff.put(setPadding(fields, paddingChar, fieldLen, encoding));
			break;
		default :
		}
	}

	public Object getConvertData(ByteBuffer buff, String encoding) throws UnsupportedEncodingException
	{
		byte[] tmpByte = null;

		switch(getType())
		{
		case('C') :
			tmpByte = new byte[getLength()];
			buff.get(tmpByte);
			tmpByte = removePadding(tmpByte);
			return new String(tmpByte, encoding);
		case('N') :
			tmpByte = new byte[getLength()];
			buff.get(tmpByte);
			tmpByte = removePadding(tmpByte);
			return Integer.parseInt(new String(tmpByte, encoding));
		case('I') :
			return buff.getInt();
		case('D') :
			return buff.getDouble();
		case('F') :
			return buff.getFloat();
		case('L') :
			return buff.getLong();
		case('B') :
			return buff.get();
		case('A') :
			tmpByte = new byte[getLength()];
			buff.get(tmpByte);
			tmpByte = removePadding(tmpByte);
			return tmpByte;
		default :
		}
		
		return null;
	}
}
