package com.nabsys.resource.script;

import java.io.Serializable;

public class StringBuilder  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.lang.StringBuilder v = null;
	
	public StringBuilder()
	{
		v = new java.lang.StringBuilder();
	}
	
	public StringBuilder(String str)
	{
		v = new java.lang.StringBuilder(str);
	}
	
	public boolean equals(StringBuilder str)
	{
		return v.toString().equals(str.toString());
	}
	
	public StringBuilder append(StringBuilder str)
	{
		v.append(str.toString());
		return this;
	}
	
	public StringBuilder insert(BigDecimal offset, StringBuilder str)
	{
		v.insert(offset.intValue(), str.toString());
		return this;
	}
	
	public StringBuilder substring(BigDecimal start)
	{
		return new StringBuilder(v.substring(start.intValue()));
	}
	
	public StringBuilder substring(BigDecimal start, BigDecimal end)
	{
		return new StringBuilder(v.substring(start.intValue(), end.intValue()));
	}
	
	public BigDecimal length()
	{
		return new BigDecimal(v.length());
	}
	
	public StringBuilder charAt(BigDecimal index)
	{
		return new StringBuilder(v.charAt(index.intValue())+"");
	}
	
	public StringBuilder delete(BigDecimal start, BigDecimal end)
	{
		v.delete(start.intValue(), end.intValue());
		return this;
	}

	public StringBuilder deleteCharAt(BigDecimal index)
	{
		v.deleteCharAt(index.intValue());
		return this;
	}
	
	public BigDecimal indexOf(StringBuilder str)
	{
		return new BigDecimal(v.indexOf(str.toString()));
	}
	
	public BigDecimal lastIndexOf(StringBuilder str)
	{
		return new BigDecimal(v.lastIndexOf(str.toString()));
	}
	
	public BigDecimal toNumber()
	{
		return new BigDecimal(v.toString());
	}
	
	public StringBuilder replace(BigDecimal start, BigDecimal end, StringBuilder str)
	{
		v.replace(start.intValue(), end.intValue(), str.toString());
		return this;
	}
	
	public StringBuilder format(StringBuilder format, Object ... args)
	{
		for(int i=0; i<args.length; i++)
		{
			if(args[i] instanceof BigDecimal)
			{
				args[i] = ((BigDecimal)args[i]).getValue();
			}
			else if(args[i] instanceof StringBuilder)
			{
				args[i] = ((StringBuilder)args[i]).toString();
			}
		}
		return new StringBuilder(String.format(format.toString(), args));
	}
	
	public String toString()
	{
		return v.toString();
	}
}
