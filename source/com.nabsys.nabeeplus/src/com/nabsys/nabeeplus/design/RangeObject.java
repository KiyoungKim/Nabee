package com.nabsys.nabeeplus.design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class RangeObject extends Canvas implements PaintListener, MouseMoveListener{
	
	private Point			start				= null;
	private PanelObject		parent				= null;
	public RangeObject(PanelObject parent, Point start) {
		super(parent, SWT.TRANSPARENT|SWT.DOUBLE_BUFFERED);
		this.parent = parent;
		this.start = start;

		addPaintListener(this);
		parent.addMouseMoveListener(this);
		moveAbove(null);
	}

	public void paintControl(PaintEvent e) {
		Rectangle rect = getBounds();
		
		e.gc.setLineStyle(SWT.LINE_DOT);
		e.gc.drawRectangle(0, 0, rect.width - 1, rect.height - 1);
		e.gc.setAlpha(50);
		e.gc.setBackground(new Color(e.display, 21, 16, 90));
		e.gc.fillRectangle(0, 0, rect.width, rect.height);
		
		e.gc.dispose();
	}

	public void mouseMove(MouseEvent e) {
		int startX 	= 0;
		int startY 	= 0;
		int width 	= 0;
		int height 	= 0;
		int x		= 0;
		int y		= 0;
		
		Rectangle rect = parent.getClientBoundary();
		if(e.x < rect.x) x = rect.x;
		else if(e.x > rect.x + rect.width) x = rect.x + rect.width;
		else x = e.x;
		if(e.y < rect.y) y = rect.y;
		else if(e.y > rect.y + rect.height) y = rect.y + rect.height;
		else y = e.y;
		
		if(start.x < x)
		{
			startX = start.x;
			width = x - start.x;
		}
		else
		{
			startX = x;
			width = start.x - x;
		}
		
		if(start.y < y)
		{
			startY = start.y;
			height = y - start.y;
		}
		else
		{
			startY = y;
			height = start.y - y;
		}
		setBounds(startX, startY, width, height);
		parent.notifyGroupSelect(getBounds());
	}

	public void mouseDoubleClick(MouseEvent e) {
		
	}
	
	public void dispose()
	{
		removePaintListener(this);
		parent.removeMouseMoveListener(this);
		super.dispose();
	}

}
