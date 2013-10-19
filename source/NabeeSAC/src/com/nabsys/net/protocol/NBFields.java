package com.nabsys.net.protocol;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Set;

public class NBFields extends LinkedHashMap<String, Object> implements Serializable{
	/**
	 * Thanks to Miss. Hong
	 */
	private static final long serialVersionUID = 1299141827889799112L;
	
	public NBFields()
	{
		super();
	}
	
	/*
	 * Returns sum of each fields' length
	 */
	public int getCLen(String encoding)
	{
		int len = 0;
		
		Set<String> keySet = super.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
			len += getObjLen(super.get(key), encoding);
			
		}
		
		return len;
	}
	
	@SuppressWarnings("unchecked")
	public int getObjCnt()
	{
		int contentCount = 0;
		Iterator<String> itr = super.keySet().iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
			Object obj = super.get(key);
			if(obj instanceof ArrayList)
			{
				ArrayList<NBFields> list = (ArrayList<NBFields>)obj;
				for(int i=0; i<list.size(); i++)
				{
					contentCount += ((NBFields)list.get(i)).getObjCnt();
				}
			}
			else
			{
				contentCount++;
			}
		}
		
		return contentCount;
	}
	
	public void clear()
	{
		super.clear();
	}
	
	
	
	public Object remove(String key)
	{
		return super.remove(key);
	}
	
	public Object get(String key)
	{
		if(!super.containsKey(key)) throw new NoSuchElementException(key);
			
		return super.get(key);
	}
	
	@SuppressWarnings("rawtypes")
	public Object put(String key, Object value)
	{
		if(value == null) throw new RuntimeException("Null is not acceptable.");
		if(value instanceof ArrayList)
		{
			if(((ArrayList)value).size() > 0 && !(((ArrayList)value).get(0) instanceof NBFields)) return null;
		}
		
		super.put(key, value);
		return this;
	}

	public void putAll(NBFields m)
	{
		super.putAll(m);
	}
	

	@SuppressWarnings("rawtypes")
	private int getObjLen(Object obj, String encoding)
	{
		if(obj == null) return 0;
		if(obj instanceof ArrayList)
		{
			int tmpLen = 0;

			ArrayList tmp = (ArrayList)obj;
			for(int i=0; i<tmp.size(); i++)
			{
				tmpLen += ((NBFields)tmp.get(i)).getCLen(encoding);
			}
			
			return tmpLen;
		}
		else if(obj.getClass() == String.class)
		{
			try {
				return ((String)obj).getBytes(encoding).length;
			} catch (UnsupportedEncodingException e) {
				return ((String)obj).getBytes().length;
			}
		}
		else if(obj.getClass() == Integer.class)
		{
			return Integer.SIZE/Byte.SIZE;
		}
		else if(obj.getClass() == Long.class)
		{
			return Long.SIZE/Byte.SIZE;
		}
		else if(obj.getClass() == Float.class)
		{
			return Float.SIZE/Byte.SIZE;
		}
		else if(obj.getClass() == Double.class)
		{
			return Double.SIZE/Byte.SIZE;
		}
		else if(obj.getClass() == byte[].class)
		{
			return ((byte[])obj).length;
		}
		else if(obj.getClass() == Byte.class)
		{
			return Byte.SIZE/Byte.SIZE;
		}
		else
		{
			return 0;
		}
	}
}
