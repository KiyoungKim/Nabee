package com.nabsys.nabeeplus.design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import com.nabsys.nabeeplus.Activator;

public class LineConnectorObject extends Canvas implements PaintListener, MouseMoveListener{
	private static final int	NONE				= 0;
	private static final int	TOP_LSIDE			= 1;
	private static final int	TOP_RSIDE			= 2;
	private static final int	BOTTOM_LSIDE		= 3;
	private static final int	BOTTOM_RSIDE		= 4;
	private static final int	LEFT_TSIDE			= 5;
	private static final int	LEFT_BSIDE			= 6;
	private static final int	RIGHT_TSIDE			= 7;
	private static final int	RIGHT_BSIDE			= 8;
	private static final int	STRSIDE				= 9;
	private static final int	STLSIDE				= 10;
	private static final int	SRBSIDE				= 11;
	private static final int	SLBSIDE				= 12;
	private static final int	VTLINE				= 13;
	private static final int	VBLINE				= 14;
	private static final int	HLLINE				= 15;
	private static final int	HRLINE				= 16;
	private int					LINE_TYPE			= NONE;	
	private DesignObject 		startObject 		= null;
	private DesignObject 		endObject 			= null;
	private PanelObject			parent				= null;
	public LineConnectorObject(PanelObject parent, DesignObject startObject) {
		super(parent, SWT.TRANSPARENT|SWT.DOUBLE_BUFFERED);
		this.parent = parent;
		this.startObject = startObject;
		addPaintListener(this);
		addMouseMoveListener(this);
		parent.addMouseMoveListener(this);
		moveAbove(null);
	}
	
	public void paintControl(PaintEvent e) {
		Display display = e.display;
		GC gc = e.gc;

		Rectangle rect = getBounds();
		int half = 0;
		String arType = "_n.png";
		gc.setForeground(new Color(display, 147, 147, 147));
		
		Image arImg = null;

		switch(LINE_TYPE){
		case TOP_LSIDE:
			half = rect.height / 2;
			gc.drawLine(3, 0, 3, half);
			gc.drawLine(3 , half, rect.width - 2, half);
			gc.drawLine(rect.width -2, half, rect.width -2, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_t" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, 0, 0);
			break;
		case TOP_RSIDE:
			half = rect.height / 2;
			gc.drawLine(rect.width -4, 0, rect.width -4, half);
			gc.drawLine(1 , half, rect.width - 4, half);
			gc.drawLine(1, half, 1, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_t" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, rect.width - 7, 0);
			break;
		case BOTTOM_LSIDE:
			half = rect.height / 2;
			gc.drawLine(rect.width -2, 0, rect.width -2, half);
			gc.drawLine(3 , half, rect.width - 2, half);
			gc.drawLine(3, half, 3, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_b" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, 0, rect.height - 7);
			break;
		case BOTTOM_RSIDE:
			half = rect.height / 2;
			gc.drawLine(1, 0, 1, half);
			gc.drawLine(1 , half, rect.width - 4, half);
			gc.drawLine(rect.width -4, half, rect.width -4, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_b" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, rect.width - 7, rect.height - 7);
			break;
		case LEFT_TSIDE:
			half = rect.width / 2;
			gc.drawLine(0, 3, half, 3);
			gc.drawLine(half, 3, half, rect.height - 2);
			gc.drawLine(half, rect.height -2, rect.width, rect.height -2);
			arImg = Activator.getImageDescriptor("/icons/ar_l" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, 0, 0);
			break;
		case LEFT_BSIDE:
			half = rect.width / 2;
			gc.drawLine(0, rect.height -4, half, rect.height -4);
			gc.drawLine(half, 1, half, rect.height - 4);
			gc.drawLine(half, 1, rect.width, 1);
			arImg = Activator.getImageDescriptor("/icons/ar_l" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, 0, rect.height - 7);
			break;
		case RIGHT_TSIDE:
			half = rect.width / 2;
			gc.drawLine(0, rect.height -2, half, rect.height -2);
			gc.drawLine(half, 3, half, rect.height - 2);
			gc.drawLine(half, 3, rect.width, 3);
			arImg = Activator.getImageDescriptor("/icons/ar_r" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, rect.width - 7, 0);
			break;
		case RIGHT_BSIDE:
			half = rect.width / 2;
			gc.drawLine(0, 1, half, 1);
			gc.drawLine(half, 1, half, rect.height - 4);
			gc.drawLine(half, rect.height -4, rect.width, rect.height -4);
			arImg = Activator.getImageDescriptor("/icons/ar_r" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, rect.width - 7, rect.height - 7);
			break;
		case STRSIDE:
			gc.drawLine(1, 3, rect.width, 3);
			gc.drawLine(1, 3, 1, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_r" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, rect.width - 7, 0);
			break;
		case STLSIDE:
			gc.drawLine(0, 3, rect.width -2, 3);
			gc.drawLine(rect.width -2, 3, rect.width -2, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_l" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, 0, 0);
			break;
		case SLBSIDE:
			gc.drawLine(3, 1, rect.width, 1);
			gc.drawLine(3, 1, 3, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_b" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, 0, rect.height - 7);
			break;
		case SRBSIDE:
			gc.drawLine(0, 1, rect.width -4, 1);
			gc.drawLine(rect.width -4, 1, rect.width -4, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_b" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, rect.width - 7, rect.height - 7);
			break;
		case VTLINE:
			gc.drawLine(3, 0, 3, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_t" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, 0, 0);
			break;
		case HLLINE:
			gc.drawLine(0, 3, rect.width, 3);
			arImg = Activator.getImageDescriptor("/icons/ar_l" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, 0, 0);
			break;
		case VBLINE:
			gc.drawLine(3, 0, 3, rect.height);
			arImg = Activator.getImageDescriptor("/icons/ar_b" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, 0, rect.height - 7);
			break;
		case HRLINE:
			gc.drawLine(0, 3, rect.width, 3);
			arImg = Activator.getImageDescriptor("/icons/ar_r" + arType).createImage(parent.getDisplay());
			gc.drawImage(arImg, rect.width - 7, 0);
			break;
		default:
			break;
		}
		
		gc.dispose();
	}
	
	public DesignObject getStartObject()
	{
		return this.startObject;
	}
	
	public void mouseMove(MouseEvent e) {
		if(e.widget == this)
		{
			pointDecision(new Rectangle(getBounds().x + e.x, getBounds().y + e.y,1, 1));
		}
		else
		{
			if(this.parent instanceof BlockObject)
			{
				Rectangle rect = ((BlockObject)this.parent).getClientBoundary();
				if(!rect.contains(e.x, e.y)) return;
				else pointDecision(new Rectangle(e.x, e.y,1, 1));
			}
			else
			{
				pointDecision(new Rectangle(e.x, e.y,1, 1));
			}
		}
	}
	
	public void setEndObject(DesignObject endObject){
		this.endObject = endObject;
		pointDecision(endObject.getLinkPointRectangle(this));
	}
	
	public void breakeEndObject()
	{
		if(this.endObject != null) this.endObject = null;
	}
	
	public void pointDecision(Rectangle endRect)
	{
		if(startObject == null) return;
		int x = 0, y = 0, width = 0, height = 0;
		Rectangle startRect = startObject.getLinkPointRectangle(this);
		Point startCenter 	= new Point(startRect.x + startRect.width / 2, startRect.y + startRect.height /2);
		Point endCenter 	= new Point(endRect.x + endRect.width / 2, endRect.y + endRect.height /2);
		
		boolean isStartBlockInsideObject = (startObject instanceof BlockObject) && this.parent == startObject;
		boolean isEndBlockInsideObject = false;

		Rectangle insideChkRect = new Rectangle(startRect.x - 30, startRect.y - 30, startRect.width + 60, startRect.height + 60);
		//inside check
		if(!isEndBlockInsideObject && !isStartBlockInsideObject && insideChkRect.intersects(endRect))
		{
			if(startCenter.x > endCenter.x && Math.abs(startCenter.y - endCenter.y) < 5)
			{
				//left
				x = endRect.x + endRect.width;
				y = endCenter.y - 1;
				width = startRect.x - endRect.x - endRect.width;
				height = 7;
				LINE_TYPE = HLLINE;
			}
			else if(startCenter.x < endCenter.x && Math.abs(endCenter.y - startCenter.y) <5 )
			{
				//right
				x = startRect.x + startRect.width;
				y = startCenter.y - 1;
				width = endRect.x - startRect.x - startRect.width;
				height = 7;
				LINE_TYPE = HRLINE;
			}
			else if(Math.abs(startCenter.x - endCenter.x) < 5 && startCenter.y > endCenter.y)
			{
				//top
				x = endCenter.x - 1;
				y = endRect.y + endRect.height;
				width = 7;
				height = startRect.y - endRect.y - endRect.height;
				LINE_TYPE = VTLINE;
			}
			else if(Math.abs(endCenter.x - startCenter.x) < 5  && startCenter.y < endCenter.y)
			{
				//bottom
				x = startCenter.x - 1;
				y = startRect.y + startRect.height;
				width = 7;
				height = endRect.y - startRect.y - startRect.height;
				LINE_TYPE = VBLINE;
			}
			else if(startCenter.x > endCenter.x && startCenter.y > endCenter.y)
			{
				//left top inside
				if(startCenter.x - endRect.x - endRect.width <= 15)
				{
					x = endCenter.x;
					y = endRect.y + endRect.height;
					width = startCenter.x - endCenter.x < 10?10:startCenter.x - endCenter.x;
					height = startRect.y - endRect.y - endRect.height;
					LINE_TYPE = TOP_LSIDE;
				}
				else if(startRect.y - endCenter.y <= 15)
				{
					x = endRect.x + endRect.width;
					y = endCenter.y;
					width = startRect.x - endRect.x - endRect.width;
					height = startCenter.y - endCenter.y < 10?10:startCenter.y - endCenter.y;
					LINE_TYPE = LEFT_TSIDE;
				}
				else
				{
					x = endRect.x + endRect.width;
					y = endCenter.y;
					width = startCenter.x - endRect.x - endRect.width;
					height = startRect.y - endCenter.y;
					LINE_TYPE = STLSIDE;
				}
			}
			else if(startCenter.x < endCenter.x && startCenter.y > endCenter.y)
			{
				//right top inside
				if(endRect.x - startCenter.x <= 15)
				{
					x = startCenter.x;
					y = endRect.y + endRect.height;
					width = endCenter.x - startCenter.x < 10?10:endCenter.x - startCenter.x;
					height = startRect.y - endRect.y - endRect.height;
					LINE_TYPE = TOP_RSIDE;
					
				}
				else if(startRect.y - endCenter.y <=15)
				{
					x = startRect.x + startRect.width;
					y = endCenter.y;
					width = endRect.x - startRect.x - startRect.width;
					height = startCenter.y - endCenter.y < 10?10:startCenter.y - endCenter.y;
					LINE_TYPE = RIGHT_TSIDE;
				}
				else
				{
					x = startCenter.x;
					y = endCenter.y;
					width = endRect.x - startCenter.x;
					height = startRect.y - endCenter.y;
					LINE_TYPE = STRSIDE;
				}
			}
			else if(startCenter.x < endCenter.x && startCenter.y < endCenter.y)
			{
				//right bottom inside
				if(endCenter.x >= startCenter.x && endCenter.x <= startRect.x + startRect.width + 10)
				{
					x = startCenter.x;
					y = startRect.y + startRect.height;
					width = endCenter.x - startCenter.x<10?10:endCenter.x - startCenter.x;
					height = endRect.y - startRect.y - startRect.height;
					LINE_TYPE = BOTTOM_RSIDE;
				}
				else if(startCenter.y <= endCenter.y && startCenter.y >= endRect.y - 10 )
				{
					x = startRect.x + startRect.width;
					y = startCenter.y;
					width = endRect.x - startRect.x - startRect.width;
					height = endCenter.y - startCenter.y < 10?10:endCenter.y - startCenter.y;
					LINE_TYPE = RIGHT_BSIDE;
				}
				else
				{
					x = startRect.x + startRect.width;
					y = startCenter.y;
					width = endCenter.x - startRect.x - startRect.width;
					height = endRect.y - startCenter.y;
					LINE_TYPE = SRBSIDE;
				}
			}
			else if(startCenter.x > endCenter.x && startCenter.y < endCenter.y)
			{
				//left bottom inside
				if(endCenter.x <= startCenter.x && endCenter.x >= startRect.x - 10)
				{
					x = endCenter.x;
					y = startRect.y + startRect.height;
					width = startCenter.x - endCenter.x<10?10:startCenter.x - endCenter.x;
					height = endRect.y - startRect.y - startRect.height;
					LINE_TYPE = BOTTOM_LSIDE;
				}
				else if(endRect.y - startCenter.y <= 15)
				{
					x = endRect.x + endRect.width;
					y = startCenter.y;
					width = startRect.x - endRect.x - endRect.width;
					height = endCenter.y - startCenter.y < 10?10:endCenter.y - startCenter.y;
					LINE_TYPE = LEFT_BSIDE;
				}
				else
				{
					x = endCenter.x;
					y = startCenter.y;
					width = startRect.x - endCenter.x;
					height = endRect.y - startCenter.y;
					LINE_TYPE = SLBSIDE;
				}
			}
			else
			{
				//center same
				setBounds(0, 0, 0, 0);
				return;
			}
		}
		else
		{
			if(startCenter.x > endCenter.x && Math.abs(startCenter.y - endCenter.y) < 5)
			{
				//left outside
				x = endRect.x + endRect.width;
				y = endCenter.y - 1;
				width = startRect.x - endRect.x - endRect.width;
				height = 7;
				LINE_TYPE = HLLINE;
			}
			else if(startCenter.x < endCenter.x && Math.abs(startCenter.y - endCenter.y) < 5)
			{
				//right outside
				x = startRect.x + startRect.width;
				y = startCenter.y - 1;
				width = endRect.x - startRect.x - startRect.width;
				height = 7;
				LINE_TYPE = HRLINE;
			}
			else if(Math.abs(startCenter.x - endCenter.x) < 5 && startCenter.y > endCenter.y)
			{
				//top outside
				x = endCenter.x - 1;
				y = endRect.y + endRect.height;
				width = 7;
				height = startRect.y - endRect.y - endRect.height;
				LINE_TYPE = VTLINE;
			}
			else if(Math.abs(startCenter.x - endCenter.x) < 5 && startCenter.y < endCenter.y)
			{
				//bottom outside
				x = startCenter.x - 1;
				y = startRect.y + startRect.height;
				width = 7;
				height = endRect.y - startRect.y - startRect.height;
				LINE_TYPE = VBLINE;
			}
			else if(startCenter.x > endCenter.x && startCenter.y > endCenter.y)
			{
				//left top outside
				if(endRect.x + endRect.width + 15 > startRect.x)
				{
					x = endCenter.x;
					y = endRect.y + endRect.height;
					width = startCenter.x - endCenter.x < 10?10:startCenter.x - endCenter.x;
					height = startRect.y - endRect.y - endRect.height;
					LINE_TYPE = TOP_LSIDE;
				}
				else
				{
					x = endRect.x + endRect.width;
					y = endCenter.y;
					width = startRect.x - endRect.x - endRect.width;
					height = startCenter.y - endCenter.y < 10?10:startCenter.y - endCenter.y;
					LINE_TYPE = LEFT_TSIDE;
				}
			}
			else if(startCenter.x < endCenter.x && startCenter.y > endCenter.y)
			{
				//right top outside
				if(endRect.x < startRect.x + startRect.width + 15)
				{
					x = startCenter.x;
					y = endRect.y + endRect.height;
					width = endCenter.x - startCenter.x < 10?10:endCenter.x - startCenter.x;
					height = startRect.y - endRect.y - endRect.height;
					LINE_TYPE = TOP_RSIDE;
				}
				else
				{
					x = startRect.x + startRect.width;
					y = endCenter.y;
					width = endRect.x - startRect.x - startRect.width;
					height = startCenter.y - endCenter.y < 10?10:startCenter.y - endCenter.y;
					LINE_TYPE = RIGHT_TSIDE;
				}
			}
			else if(startCenter.x < endCenter.x && startCenter.y < endCenter.y)
			{
				//right bottom outside
				if(endRect.x < startRect.x + startRect.width + 15)
				{
					x = startCenter.x;
					y = startRect.y + startRect.height;
					width = endCenter.x - startCenter.x<10?10:endCenter.x - startCenter.x;
					height = endRect.y - startRect.y - startRect.height;
					LINE_TYPE = BOTTOM_RSIDE;
				}
				else
				{
					x = startRect.x + startRect.width;
					y = startCenter.y;
					width = endRect.x - startRect.x - startRect.width;
					height = endCenter.y - startCenter.y < 10?10:endCenter.y - startCenter.y;
					LINE_TYPE = RIGHT_BSIDE;
				}
			}
			else if(startCenter.x > endCenter.x && startCenter.y < endCenter.y)
			{
				//left bottom outside
				if(endRect.x + endRect.width + 15 > startRect.x)
				{
					x = endCenter.x;
					y = startRect.y + startRect.height;
					width = startCenter.x - endCenter.x<10?10:startCenter.x - endCenter.x;
					height = endRect.y - startRect.y - startRect.height;
					LINE_TYPE = BOTTOM_LSIDE;
				}
				else
				{
					x = endRect.x + endRect.width;
					y = startCenter.y;
					width = startRect.x - endRect.x - endRect.width;
					height = endCenter.y - startCenter.y < 10?10:endCenter.y - startCenter.y;
					LINE_TYPE = LEFT_BSIDE;
				}
			}
			else
			{
				//center same
				setBounds(0, 0, 0, 0);
				return;
			}
		}
		
		setBounds(x, y, width, height);
	}
	
	public void dispose()
	{
		removePaintListener(this);
		parent.removeMouseMoveListener(this);
		super.dispose();
	}
}
