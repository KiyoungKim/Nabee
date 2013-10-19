package com.nabsys.resource.script;

import java.io.Serializable;

public class BigDecimal implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Class<?> type = null;
	java.math.BigDecimal v = null;
	
	public BigDecimal(java.math.BigDecimal val) {
		v = val;

		if(val.toPlainString().contains("."))
		{
			if((floatValue() + "").equals(doubleValue() + ""))
			{
				type = Float.class;
			}
			else
			{
				type = Double.class;
			}
		}
		else
		{
			try{
				intValueExact();
				type = Integer.class;
			} catch (ArithmeticException e){
				type = Long.class;
			}
		}
	}
	
	public int compareTo(BigDecimal val)
	{
		return v.compareTo(val.getRealBigDecimal());
	}
	
	protected java.math.BigDecimal getRealBigDecimal()
	{
		return this.v;
	}
	
	public BigDecimal(int val) {
		v = new java.math.BigDecimal(val);
		type = Integer.class;
	}
	public BigDecimal(long val) {
		v = new java.math.BigDecimal(val);
		type = Long.class;
	}
	public BigDecimal(float val) {
		v = new java.math.BigDecimal(val);
		type = Float.class;
	}
	public BigDecimal(double val) {
		v = new java.math.BigDecimal(val);
		type = Double.class;
	}
	public BigDecimal(String val) {
		v = new java.math.BigDecimal(val);
		
		if(val.contains("."))
		{
			if((floatValue() + "").equals(doubleValue() + ""))
			{
				type = Float.class;
			}
			else
			{
				type = Double.class;
			}
		}
		else
		{
			try{
				intValueExact();
				type = Integer.class;
			} catch (ArithmeticException e){
				type = Long.class;
			}
		}
	}
	
	public Object getValue(){
		if(type == Integer.class)
			return v.intValue();
		else if(type == Long.class)
			return v.longValue();
		else if(type == Float.class)
			return v.floatValue();
		else if(type == Double.class)
			return v.doubleValue();
		else return null;
	}
	
	public Class<?> getValueClass(){
		return type;
	}
	
	public BigDecimal toInt()
	{
		return new BigDecimal(v.intValue());
	}
	public BigDecimal toDouble()
	{
		return new BigDecimal(v.doubleValue());
	}
	public BigDecimal toFloat()
	{
		return new BigDecimal(v.floatValue());
	}
	public BigDecimal toLong()
	{
		return new BigDecimal(v.longValue());
	}
	public StringBuilder stringValue()
	{
		return new StringBuilder(toPlainString());
	}
	
	public BigDecimal abs()
	{
		return new BigDecimal(v.abs());
	}
	public BigDecimal add(BigDecimal augend)
	{
		return new BigDecimal(v.add(new java.math.BigDecimal(augend.toPlainString())));
	}
	public BigDecimal divide(BigDecimal divisor)
	{
		return new BigDecimal(v.divide(new java.math.BigDecimal(divisor.toPlainString())));
	}
	public BigDecimal remainder(BigDecimal divisor)
	{
		return new BigDecimal(v.remainder(new java.math.BigDecimal(divisor.toPlainString())));
	}
	public BigDecimal subtract(BigDecimal subtrahend)
	{
		return new BigDecimal(v.subtract(new java.math.BigDecimal(subtrahend.toPlainString())));
	}
	public BigDecimal multiply(BigDecimal multiplicand)
	{
		return new BigDecimal(v.multiply(new java.math.BigDecimal(multiplicand.toPlainString())));
	}
	
	
	public int intValue()
	{
		return v.intValue();
	}
	public int intValueExact()
	{
		return v.intValueExact();
	}
	public double doubleValue()
	{
		return v.doubleValue();
	}
	public float floatValue()
	{
		return v.floatValue();
	}
	public long longValue()
	{
		return v.longValue();
	}
	public long longValueExact()
	{
		return v.longValueExact();
	}
	public String toPlainString()
	{
		return v.toPlainString();
	}
	public String toString()
	{
		return v.toString();
	}
}
