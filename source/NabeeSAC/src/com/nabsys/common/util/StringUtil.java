package com.nabsys.common.util;

import java.io.UnsupportedEncodingException;

import com.nabsys.resource.ServerConfiguration;


public class StringUtil {
	public StringUtil()
	{
	}
	
	public String getNumber(byte[] b) throws UnsupportedEncodingException
	{
		String tmp = new String(b, ServerConfiguration.getServerEncoding());
		int num = Integer.parseInt(tmp);
		return Integer.toString(num);
	}
	
	public static String lpad(String str, int len, char padding)
	{
		if(len < str.length())
		{
			return str.substring(str.length() - len);
		}
		else
		{
			StringBuffer buf = new StringBuffer(len);
			int gap = len - str.length();
			
			for(int i=0; i<gap; i++)
			{
				buf.insert(i, padding);
			}
			
			buf.insert(gap, str);
			
			return buf.toString();
		}
	}
	
	public static String rpad(String str, int len, char padding)
	{
		if(len < str.length())
		{
			return str.substring(0, len);
		}
		else
		{
			StringBuffer buf = new StringBuffer(len);
			int gap = len - str.length();
			
			for(int i=0; i<gap; i++)
			{
				buf.insert(i, padding);
			}
			
			buf.insert(0, str);
			
			return buf.toString();
		}
	}
	
	public static byte[] intToByte(int value)
	{
		byte[] byteArray = new byte[Integer.SIZE/Byte.SIZE];
		
		byteArray[0] = (byte)((value >> 24)& 0xff);
		byteArray[1] = (byte)((value >> 16)& 0xff);
		byteArray[2] = (byte)((value >> 8)& 0xff);
		byteArray[3] = (byte)(value);
		
		return byteArray;
	}
	
	public static int byteToInt(byte[] byteArray)
	{
		return (int)((byteArray[0]&0XFF) << 24)|
		(int)((byteArray[1]&0XFF) << 16)|
		(int)((byteArray[2]&0XFF) << 8)|
		(int)((byteArray[3]&0XFF));
	}
	
	public static byte[] longToByte(long value)
	{
		byte[] byteArray = new byte[Long.SIZE/Byte.SIZE];

		byteArray[0] = ((byte)(value >> 56));
		byteArray[1] = ((byte)(value >> 48));
		byteArray[2] = ((byte)(value >> 40));
		byteArray[3] = ((byte)(value >> 32));
		byteArray[4] = ((byte)(value >> 24));
		byteArray[5] = ((byte)(value >> 16));
		byteArray[6] = ((byte)(value >> 8));
		byteArray[7] = ((byte)(value));
		
		return byteArray;
	}
	
	public static long byteToLong(byte[] byteArray)
	{
		 return (((long)(byteArray[0]) & 0xFF) << 56) |
		 (((long)(byteArray[1]) & 0xFF) << 48) |
		 (((long)(byteArray[2]) & 0xFF) << 40) |
		 (((long)(byteArray[3]) & 0xFF) << 32) |
		 (((long)(byteArray[4]) & 0xFF) << 24) |
		 (((long)(byteArray[5]) & 0xFF) << 16) |
		 (((long)(byteArray[6]) & 0xFF) << 8) |
		 ((long)(byteArray[7]) & 0xFF);
	}
	
}
