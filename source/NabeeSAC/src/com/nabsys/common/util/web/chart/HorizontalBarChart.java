package com.nabsys.common.util.web.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class HorizontalBarChart extends Chart{
	private int[] barPoint;
	private int barHeight = 10;
	private int rightSpace = 50;
	private int topSpace = 0;
	private int bottomSpace = 0;
	private int imgStartX = 0;
	
	public HorizontalBarChart(int width, int height)
	{
		super(width, height);
	}
	
	public void setBarHeight(int height)
	{
		this.barHeight = height;
	}
	
	public void drawChart(ArrayList<DrawFactor> barList, Font labelFont)
	{
		FontMetrics fontMetrics = g.getFontMetrics(labelFont);
		int adj = (fontMetrics.getAscent() - fontMetrics.getDescent())/2;
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g.setRenderingHints(rh);
	    
		drawValueLabel(barList, labelFont, adj);
		drawImage(barList, labelFont, adj);
	}
	
	private void drawImage(ArrayList<DrawFactor> barList, Font labelFont, int adj)
	{
		int listCnt = barList.size();
		
		int sumValue = 0;
		for(int i=0; i< listCnt; i++)
		{
			sumValue += barList.get(i).getValue();
		}
		
		int index = 0;
		for(int i=0; i< listCnt; i++)
		{
			DrawFactor factor = barList.get(i);
			int imageWidth = width - imgStartX - rightSpace;
			int barWidth = getWidth(imageWidth, sumValue, factor.getValue());
			
			int x = imgStartX;
			int y = barPoint[index] - (barHeight/2);

			
			BarComponent bar = new BarComponent(g, BarComponent.HORIZONTAL);
			bar.setPosition(x, y);
			bar.setSize(barWidth, barHeight);
			bar.draw(barList.get(i).getColor());
			
			
			int ratio = Math.round((float)factor.getValue() / (float)sumValue * 100f);
			g.setColor(Color.BLACK);
			g.drawString(Integer.toString(ratio) + " %", x + barWidth + 5, barPoint[index] + adj);
			
			index++;
		}
	}
	
	private void drawValueLabel(ArrayList<DrawFactor> barList, Font labelFont, int adj)
	{
		FontMetrics fontMetrics = g.getFontMetrics(labelFont);
		
		int maxLabelWidth = 0;
		
		int labelCnt = barList.size();
		barPoint = new int[labelCnt];
		
		for(int i=0; i< labelCnt; i++)
		{
			DrawFactor factor = barList.get(i);
			int newWidth = fontMetrics.stringWidth(Integer.toString(factor.getValue()));
			if(maxLabelWidth < newWidth)
			{
				maxLabelWidth = newWidth;
			}
		}
		
		maxLabelWidth += 3;
		
		imgStartX = maxLabelWidth + 15;
		
		int horizontalGap = (height - topSpace - bottomSpace) / (labelCnt + 1);

		for(int i=0; i<labelCnt; i++)
		{
			DrawFactor factor = barList.get(i);
			
			int pointY = (horizontalGap * (i + 1)) + topSpace;
			barPoint[i] = pointY;

			g.setColor(new Color(0,0,0));
			
			String string = Integer.toString(factor.getValue());
			g.drawString(string, maxLabelWidth - fontMetrics.stringWidth(string), pointY + adj);
		}
	}
	
	private int getWidth(int imgWidth, int maxValue, int value)
	{
		return super.getRatio(imgWidth, maxValue, value);
	}
}
