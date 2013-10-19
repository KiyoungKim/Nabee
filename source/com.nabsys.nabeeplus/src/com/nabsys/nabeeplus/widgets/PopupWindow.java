package com.nabsys.nabeeplus.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PopupWindow {
	protected Shell shell = null;
	protected Display display = null;
	private Image titleImage = null;
	private int titleX = 0;
	private int titleY = 0;
	private String titleText;
	private String titleDetail;
	
	public PopupWindow(Shell parent)
	{
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.TITLE);
		this.display = shell.getDisplay();
	}
	
	public void setLocation(int x, int y)
	{
		shell.setLocation(x, y);
	}
	
	public Point getLocation()
	{
		return shell.getLocation();
	}
	
	protected void setTitle(String title)
	{
		shell.setText(title);
	}
	
	protected void setImage(Image image)
	{
		shell.setImage(image);
	}
	
	protected void setTitleImage(Image image, int x, int y, String text, String detail)
	{
		this.titleImage = image;

		this.titleX = x;
		this.titleY = y;
		this.titleText = text;
		this.titleDetail = detail;
	}
	
	protected void setSize(Point size)
	{
		shell.setSize(size);
	}
	
	protected void setLayout(FillLayout layout)
	{
		shell.setLayout(layout);
	}
	
	protected Canvas getCanvas(int style)
	{
		return new Canvas(shell, style);
	}
	
	protected void openWindow()
	{
		shell.open();
	}
	
	protected void drawTitle(PaintEvent e)
	{
		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		e.gc.fillRectangle(0, 0, shell.getSize().x, 66);
		drawDevider(e, 66);
		
		e.gc.drawImage(titleImage, titleX, titleY);
		
		e.gc.setFont(new Font(e.display, "Tahoma", 10, SWT.BOLD));
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		e.gc.drawText(titleText, 5, 8);
		
		e.gc.setFont(new Font(e.display, "Tahoma", 9, SWT.NONE));
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		e.gc.drawText(titleDetail, 15, 25);
	}
	
	protected void drawDevider(PaintEvent e, int y)
	{
		e.gc.setLineWidth(1);
		e.gc.setForeground(new Color(shell.getDisplay(), 172,168,153));
		e.gc.drawLine(0, y, shell.getSize().x, y);
		e.gc.setForeground(new Color(shell.getDisplay(), 255,255,255));
		e.gc.drawLine(0, y+1, shell.getSize().x, y+1);
	}
}
