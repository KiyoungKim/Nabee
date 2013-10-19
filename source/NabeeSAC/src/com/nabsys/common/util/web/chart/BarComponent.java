package com.nabsys.common.util.web.chart;

import java.awt.Color;
import java.awt.Graphics2D;

public class BarComponent {
	private Graphics2D g;
	private int width = 0;
	private int height = 0;
	private int x = 0;
	private int y = 0;
	protected static int VERTICAL = 0;
	protected static int HORIZONTAL = 1;
	private int style = -1;
	protected BarComponent(Graphics2D g, int style)
	{
		this.g = g;
		this.style = style;
	}
	
	protected void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	protected void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	protected void draw(Color color)
	{
		int ext = 0;
		if(style == VERTICAL)
		{
			ext = width / 3;
			
			g.setColor(color);
			g.fillRect(x, y + ext, width, height);
			
			int xPoints[] = new int[4];
			int yPoints[] = new int[4];
			xPoints[0] = x;
			xPoints[1] = x + width;
			xPoints[2] = x + width + ext;
			xPoints[3] = x + ext;
			
			yPoints[0] = y + ext;
			yPoints[1] = y + ext;
			yPoints[2] = y;
			yPoints[3] = y;
			
			g.setColor(color.brighter());
			g.fillPolygon(xPoints, yPoints, 4);
			
			xPoints[0] = x + width;
			xPoints[1] = x + width + ext;
			xPoints[2] = x + width + ext;
			xPoints[3] = x + width;
			
			yPoints[0] = y + ext;
			yPoints[1] = y;
			yPoints[2] = y + height;
			yPoints[3] = y + height + ext;
			
			g.setColor(color.darker());
			g.fillPolygon(xPoints, yPoints, 4);
		}
		else if(style == HORIZONTAL)
		{ 
			ext = height / 3;
			
			width = width - ext;
			
			g.setColor(color);
			g.fillRect(x, y, width, height);
			
			int xPoints[] = new int[4];
			int yPoints[] = new int[4];
			xPoints[0] = x;
			xPoints[1] = x + width;
			xPoints[2] = x + width + ext;
			xPoints[3] = x + ext;
			
			yPoints[0] = y;
			yPoints[1] = y;
			yPoints[2] = y - ext;
			yPoints[3] = y - ext;
			
			g.setColor(color.brighter());
			g.fillPolygon(xPoints, yPoints, 4);
			
			xPoints[0] = x + width;
			xPoints[1] = x + width + ext;
			xPoints[2] = x + width + ext;
			xPoints[3] = x + width;
			
			yPoints[0] = y;
			yPoints[1] = y - ext;
			yPoints[2] = y + height - ext;
			yPoints[3] = y + height;
			
			g.setColor(color.darker());
			g.fillPolygon(xPoints, yPoints, 4);
		}
		
		
	}
}
