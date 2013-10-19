package com.nabsys.resource;

import java.util.LinkedHashMap;
import java.util.Map;

public class TelegramFieldContextList extends LinkedHashMap<String, TelegramFieldContext>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int length = 0;
	
	public TelegramFieldContext put(String key, TelegramFieldContext value)
	{
		length += value.getLength();
		return super.put(key, value);
	}
	
	public void putAll(Map<? extends String, ? extends TelegramFieldContext> m)
	{
		length += ((TelegramFieldContextList)m).getTelegramLength();
		super.putAll(m);
	}
	
	public TelegramFieldContext remove(String key)
	{
		length -= super.get(key).getLength();
		return super.remove(key);
	}
	
	public void clear()
	{
		this.length = 0;
		super.clear();
	}
	
	 public Object clone() {
		 TelegramFieldContextList o = (TelegramFieldContextList)super.clone();
		 o.length = this.length;
		 return o;
	}
	 
	public TelegramFieldContext[] toArray()
	{
		return (TelegramFieldContext[])values().toArray(new TelegramFieldContext[size()]);
	}
	
	public int getTelegramLength()
	{
		return this.length;
	}
}
