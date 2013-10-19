package com.nabsys.common.util.web.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class PieChart extends Chart{
	
	private int angleGap = 2;
	private int sideMargin = 30;
	private int topMargin = 30;
	private int marginBtnImgLabel = 30;
	
	public PieChart(int width, int height)
	{
		super(width, height);
	}
	
	public void drawChart(ArrayList<DrawFactor> arcList, Font labelFont)
	{
		int listCnt = arcList.size();
		int sumValue = 0;
		int maxLabelWidth = 0;
		
		FontMetrics fontMetrics = g.getFontMetrics(labelFont);
		
		for(int i=0; i<listCnt; i++)
		{
			DrawFactor factor = arcList.get(i);
			sumValue += factor.getValue();
			
			String label = factor.getLabel() + " = " + Integer.toString(factor.getValue());
			
			int labelWidth = fontMetrics.stringWidth(label);
			labelWidth += (labelWidth * 0.1);
			if(maxLabelWidth < labelWidth) maxLabelWidth = labelWidth;
		}

		int x = maxLabelWidth + sideMargin + marginBtnImgLabel;
		int circleWidth = width - (maxLabelWidth * 2) - (sideMargin * 2) - (marginBtnImgLabel * 2);
		int circleHeight = circleWidth;
		int y = (height - circleWidth) / 2;

		if(y < topMargin)
		{
			y = topMargin;
			circleWidth = height - (topMargin * 2);
			circleHeight = circleWidth;
			x = (width / 2) - (circleWidth / 2);
		}

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHints(rh);
	    
		int totArc = 360 - (listCnt * angleGap);
		int startAngle = 0;
		LabelInfo[] labelInfos = new LabelInfo[listCnt];
		
		int imageCenterX = width / 2;
		int imageCenterY = height / 2;
		int boxHeight = fontMetrics.getAscent() - fontMetrics.getDescent() + 6;
		for(int i=0; i<listCnt; i++)
		{
			DrawFactor factor = arcList.get(i);
			
			int arcAngle = Math.round((float)factor.getValue() / (float)sumValue * (float)totArc);
			
			if(i + 1 == listCnt && (360 - startAngle + arcAngle) > angleGap)
			{
				arcAngle += (360 - (startAngle + arcAngle) - angleGap);
			}
			
			g.setColor(factor.getColor());
			g.fillArc(x, y, circleWidth, circleHeight, startAngle, arcAngle);

			int angleCetner = arcAngle / 2 + startAngle;
			double radius = circleWidth / 2d;
			double angle = (angleCetner * -1) * (Math.PI/180);
			
			radius = radius - 5d;
			int x1 = (int)(radius * Math.cos(angle));
			int y1 = (int)(radius * Math.sin(angle));
			
			radius = radius + 10d;
			int x2 = (int)(radius * Math.cos(angle));
			int y2 = (int)(radius * Math.sin(angle));
			
			g.setColor(Color.gray);
			g.drawLine(x1 + imageCenterX, y1 + imageCenterY ,x2 + imageCenterX, y2 + imageCenterY);

			String str = factor.getLabel() + " = " + Integer.toString(factor.getValue());
			
			LabelInfo labelInfo = new LabelInfo();
			int fontWidth = fontMetrics.stringWidth(str);
			labelInfo.fontWidth = fontWidth + (int)(fontWidth * 0.1) + 6;
			labelInfo.angleCetner = angleCetner;
			labelInfo.isLeft = labelInfo.angleCetner > 90 && labelInfo.angleCetner <270;
			labelInfo.x1 = x2 + imageCenterX; //라인 시작
			labelInfo.y1 = y2 + imageCenterY;
			labelInfo.x2 = labelInfo.isLeft?x - marginBtnImgLabel - labelInfo.fontWidth :width - sideMargin - maxLabelWidth; //박스 까지
			labelInfo.y2 = y2 + imageCenterY - (boxHeight / 2);
			labelInfo.label = factor.getLabel() + " = " + Integer.toString(factor.getValue());
			
			labelInfos[i] = labelInfo;
			
			startAngle = startAngle + arcAngle + angleGap;
		}
		
		boolean passLeft = false;
		for(int i=0; i<labelInfos.length; i++)
		{
			if(labelInfos[i].isLeft)
			{
				if(i > 0 && labelInfos[i - 1].isLeft)
				{
					if(labelInfos[i - 1].y2 >= labelInfos[i].y2 - boxHeight - 2)
					{
						labelInfos[i].y2 = labelInfos[i - 1].y2 + 2 + boxHeight;
					}
				}
				passLeft = true;
			}
			else
			{
				if(passLeft)
				{
					if(i != 0)
					{
						for(int j=listCnt - 1; j>= i; j--)
						{
							if(j == listCnt - 1 && !labelInfos[0].isLeft)
							{
								if(labelInfos[0].y2 >= labelInfos[j].y2 - boxHeight - 2)
								{
									labelInfos[j].y2 = labelInfos[0].y2 + 2 + boxHeight;
								}
							}
							else
							{
								if(labelInfos[j + 1].y2 >= labelInfos[j].y2 - boxHeight - 2)
								{
									labelInfos[j].y2 = labelInfos[j + 1].y2 + 2 + boxHeight;
								}
							}
						}
					}
					break;
				}
				else
				{
					if(i > 0)
					{
						if(labelInfos[i - 1].y2 - boxHeight - 2 <= labelInfos[i].y2)
						{
							labelInfos[i].y2 = labelInfos[i - 1].y2 - 2 - boxHeight;
						}
					}
				}
			}
		}
		
		for(int i=0; i<labelInfos.length; i++)
		{
			g.setColor(Color.BLACK);
			g.drawString(labelInfos[i].label, labelInfos[i].x2 + 3, labelInfos[i].y2 + boxHeight - 3);
			g.setColor(Color.gray);
			if(labelInfos[i].isLeft)
			{
				g.drawLine(labelInfos[i].x1, labelInfos[i].y1, labelInfos[i].x2 + labelInfos[i].fontWidth + 15, labelInfos[i].y1);
				g.drawLine(labelInfos[i].x2 + labelInfos[i].fontWidth + 15, labelInfos[i].y1, labelInfos[i].x2 + labelInfos[i].fontWidth, labelInfos[i].y2 + (boxHeight / 2));
			}
			else
			{
				g.drawLine(labelInfos[i].x1, labelInfos[i].y1, labelInfos[i].x2 - 15, labelInfos[i].y1);
				g.drawLine(labelInfos[i].x2 - 15, labelInfos[i].y1, labelInfos[i].x2, labelInfos[i].y2 + (boxHeight / 2));
			}
			g.setColor(new Color(228, 228, 228));
			g.drawRect(labelInfos[i].x2, labelInfos[i].y2, labelInfos[i].fontWidth, boxHeight);
		}
	}
}

class LabelInfo{
	boolean isLeft = false;
	String label = "";
	int angleCetner = 0;
	int x1 = 0;
	int y1 = 0;
	int x2 = 0;
	int y2 = 0;
	int fontWidth = 0;
}
