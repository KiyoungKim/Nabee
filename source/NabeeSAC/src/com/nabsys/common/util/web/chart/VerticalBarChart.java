package com.nabsys.common.util.web.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class VerticalBarChart extends Chart{
	
	private int gmaxValue = 0;
	private int bottomLabelHeight = 20;
	private int topLabelHeight = 15;
	private int rightSpace = 10;
	private int imgStartX = 0;
	private int maxBarCount = 0;
	private int[] barPoint;
	private int barWidth = 10;
	
	public VerticalBarChart(int width, int height, int maxBarCount)
	{
		super(width, height);
		this.maxBarCount = maxBarCount;
		barPoint = new int[maxBarCount];
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g.setRenderingHints(rh);
	}
	
	public void setMaxValue(int value)
	{
		this.gmaxValue = value;
	}
	
	public void setBarWidth(int width)
	{
		this.barWidth = width;
		bottomLabelHeight = bottomLabelHeight + (barWidth / 3);
	}
	
	public void drawChart(ArrayList<DrawFactor> barList)
	{
		int imgHeight = height - (bottomLabelHeight - barWidth /3) - topLabelHeight;
		
		int startIndex = 0;
		if(barList.size() > maxBarCount) startIndex = barList.size() - maxBarCount;
		int index = 0;
		for(int i=startIndex; i< barList.size(); i++)
		{
			DrawFactor factor = barList.get(i);
			int barHeight = getHeight(imgHeight, gmaxValue, factor.getValue());

			int x = barPoint[index] - (barWidth / 2);
			int y = height - barHeight - bottomLabelHeight;

			index++;
			
			BarComponent bar = new BarComponent(g, BarComponent.VERTICAL);
			bar.setPosition(x, y);
			bar.setSize(barWidth, barHeight);
			bar.draw(barList.get(i).getColor());
		}
	}
	
	public void setBottomLabel(ArrayList<String> bottomLabels, Font labelFont)
	{
		int imageWidth = width - imgStartX - rightSpace;
		int leftSpace = imageWidth / maxBarCount;
		int labelGap = (imageWidth - leftSpace) / maxBarCount;
		int lastX = imgStartX + leftSpace;
		int labelJump = maxBarCount / bottomLabels.size();
		int index = 0;
		
		FontMetrics fontMetrics = g.getFontMetrics(labelFont);
		
		g.setFont(labelFont);
		for(int i=0; i<maxBarCount; i++)
		{
			g.setColor(new Color(193,193,193));
			g.drawLine(lastX, height - bottomLabelHeight, lastX, height - bottomLabelHeight + 5);
			barPoint[i] = lastX;
			if(i%labelJump == 0)
			{
				g.setColor(new Color(0,0,0));
				int half = fontMetrics.stringWidth(bottomLabels.get(index)) / 2;
				g.drawString(bottomLabels.get(index), lastX - half, height - 5);
				index++;
			}
			lastX += labelGap;
		}
	}
	
	public void drawHorizontalLine(ArrayList<String> lineLabels, Font labelFont)
	{
		FontMetrics fontMetrics = g.getFontMetrics(labelFont);
		
		int maxLabelWidth = 0;
		int labelCnt = lineLabels.size();
		for(int i=0; i< labelCnt; i++)
		{
			int newWidth = fontMetrics.stringWidth(lineLabels.get(i));
			if(maxLabelWidth < newWidth)
			{
				maxLabelWidth = newWidth;
			}
		}
		
		imgStartX = maxLabelWidth + 2;
		
		int imgHeight = height - (bottomLabelHeight - barWidth /3) - topLabelHeight; 
		
		
		for(int i=0; i<labelCnt; i++)
		{
			int lineHeight = getHeight(imgHeight, (labelCnt-1), i);
			int y = height - lineHeight - bottomLabelHeight;
			
			g.setColor(Color.BLACK);
			g.setFont(labelFont);
			g.drawString(lineLabels.get(i), maxLabelWidth - fontMetrics.stringWidth(lineLabels.get(i)), y);
			
			g.setColor(new Color(193,193,193));
			g.drawLine(imgStartX, y, width  - rightSpace, y);
		}
	}
	
	private int getHeight(int imgHeight, int maxValue, int value)
	{
		return super.getRatio(imgHeight, maxValue, value);
	}
}
