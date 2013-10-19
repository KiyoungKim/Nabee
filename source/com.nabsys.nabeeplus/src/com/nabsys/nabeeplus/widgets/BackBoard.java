package com.nabsys.nabeeplus.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.nabsys.nabeeplus.Activator;

public class BackBoard extends Canvas {
	
	private Display 	display 		= null;
	private Canvas 		titleBar 		= null;
	private Image 		icon			= null;
	private Point 		iconSize		= null;
	private String 		title			= null;
	private Composite 	contents		= null;
	private ToolBar 	toolBar 		= null;
	
	private PaintListener paintListener = null;
	
	public BackBoard(Composite parent, int style) {
		super(parent, SWT.NONE);
		
		this.display = parent.getDisplay();
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		super.setLayout(layout);
		
		Composite back = new Composite(this, SWT.NONE);
		back.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		back.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		GridLayout backLayout = new GridLayout();
		backLayout.numColumns = 1;
		backLayout.verticalSpacing = 10;
		backLayout.marginWidth = 0;
		backLayout.marginHeight = 0;
		back.setLayout(backLayout);
		
		GridData titleBarGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		titleBarGridData.heightHint = 33;
		
		titleBar = new Canvas(back, SWT.NONE);
		titleBar.setLayoutData(titleBarGridData);
		titleBar.setBackgroundImage(Activator.getImageDescriptor("/icons/titleback.gif").createImage(display));
		
		backLayout = new GridLayout();
		backLayout.horizontalSpacing = 10;
		backLayout.marginWidth = 0;
		backLayout.marginHeight = 3;
		backLayout.marginRight = 10;
		titleBar.setLayout(backLayout);
		
		paintListener = new PaintListener() {
			public void paintControl(PaintEvent e) {
				if(e.widget == titleBar)
				{
					drawTitle(e);
				}
			}
		};
		
		titleBar.addPaintListener(paintListener);
		
		contents = new Composite(back, SWT.NONE);
		GridData contentsGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		contents.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		contents.setLayoutData(contentsGridData);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalAlignment = SWT.RIGHT;
		
		toolBar = new ToolBar(titleBar, SWT.FLAT);
		toolBar.setLayoutData(gridData);
		toolBar.setBackgroundImage(Activator.getImageDescriptor("/icons/titleback.gif").createImage(display));
	}
	
	public BackBoard(Composite parent, int style, Point minScrollBack) {
		super(parent, SWT.NONE);
		this.display = parent.getDisplay();
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		super.setLayout(layout);
		
		ScrolledComposite scrollBack = new ScrolledComposite(this, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);

		scrollBack.setExpandHorizontal(true);
		scrollBack.setExpandVertical(true);
		scrollBack.getVerticalBar().setIncrement(5);
		scrollBack.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Composite back = new Composite(scrollBack, SWT.NONE);
		back.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		scrollBack.setContent(back);
		scrollBack.setMinSize(minScrollBack);
		
		GridLayout backLayout = new GridLayout();
		backLayout.numColumns = 1;
		backLayout.verticalSpacing = 10;
		backLayout.marginWidth = 0;
		backLayout.marginHeight = 0;
		back.setLayout(backLayout);
		
		GridData titleBarGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		titleBarGridData.heightHint = 33;
		
		titleBar = new Canvas(back, SWT.NONE);
		titleBar.setLayoutData(titleBarGridData);
		titleBar.setBackgroundImage(Activator.getImageDescriptor("/icons/titleback.gif").createImage(display));
		
		backLayout = new GridLayout();
		backLayout.horizontalSpacing = 10;
		backLayout.marginWidth = 0;
		backLayout.marginHeight = 3;
		backLayout.marginRight = 10;
		titleBar.setLayout(backLayout);
		
		paintListener = new PaintListener() {
			public void paintControl(PaintEvent e) {
				if(e.widget == titleBar)
				{
					drawTitle(e);
				}
			}
		};
		
		titleBar.addPaintListener(paintListener);
		
		contents = new Composite(back, SWT.NONE);
		GridData contentsGridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		contents.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		contents.setLayoutData(contentsGridData);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalAlignment = SWT.RIGHT;
		
		toolBar = new ToolBar(titleBar, SWT.FLAT);
		toolBar.setLayoutData(gridData);
		toolBar.setBackgroundImage(Activator.getImageDescriptor("/icons/titleback.gif").createImage(display));
	}
	
	public ToolItem addButton(String toolTipText, Image image)
	{
		ToolItem toolButton = new ToolItem(toolBar, SWT.PUSH);
		toolButton.setToolTipText(toolTipText);
		toolButton.setImage(image);
		
		return toolButton;
	}
	
	public Composite getPanel()
	{
		return contents;
	}
	
	public void setLayout(Layout layout)
	{
		contents.setLayout(layout);
	}
	
	public void setTitleIcon(Image icon, Point size)
	{
		this.icon= icon; 
		iconSize = size;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void dispose()
	{
		titleBar.removePaintListener(paintListener);
		super.dispose();
	}
	
	private void drawTitle(PaintEvent e)
	{
		e.gc.drawImage(icon, iconSize.x, iconSize.y);
		e.gc.setFont(new Font(e.display, "Tahoma", 11, SWT.BOLD));
		e.gc.setForeground(new Color(display, 49, 106, 197));
		e.gc.drawText(title==null?"":title, 35, 6, true);
	}
}
