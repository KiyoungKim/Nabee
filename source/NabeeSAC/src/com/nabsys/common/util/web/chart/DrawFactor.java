package com.nabsys.common.util.web.chart;

import java.awt.Color;

public class DrawFactor {
	private Color color;
	private int value;
	private String label;
	
	public DrawFactor()
	{
	}
	
	public DrawFactor(String label)
	{
		this.label = label;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public String getLabel()
	{
		return this.label;
	}
}
