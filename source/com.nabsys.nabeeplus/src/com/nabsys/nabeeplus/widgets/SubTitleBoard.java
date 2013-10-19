package com.nabsys.nabeeplus.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.nabsys.nabeeplus.Activator;

public class SubTitleBoard extends Canvas {
	
	private Display 		display 		= null;
	private Composite 		contents		= null;
	private int				orgHeight		= 0;


	public SubTitleBoard(Composite parent, int style, String title, String explain, int explainRows) {
		super(parent, style);

		this.display = parent.getDisplay();
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		layout.numColumns = 3;
		layout.verticalSpacing = 3;
		layout.horizontalSpacing = 0;
		super.setLayout(layout);
		this.setBackground(new Color(display, 255, 255, 255));
		
		GridData side = new GridData(GridData.FILL, GridData.FILL, false, false);
		side.widthHint = 5;
		side.heightHint = 18;
		
		GridData center = new GridData(GridData.FILL, GridData.FILL, true, false); 
		center.heightHint = 18;
		
		Composite left = new Composite(this, SWT.NONE);
		left.setBackgroundImage(Activator.getImageDescriptor("/icons/left_title_back.gif").createImage(display));
		left.setLayoutData(side);
		
		CLabel lbTitle = new CLabel(this, SWT.NONE);
		lbTitle.setText(title);
		lbTitle.setAlignment(SWT.LEFT);
		lbTitle.setFont(new Font(display, "Tahoma", 10, SWT.BOLD));
		lbTitle.setForeground(new Color(display, 49, 106, 197));
		lbTitle.setBackground(Activator.getImageDescriptor("/icons/center_title_back.gif").createImage(display));
		lbTitle.setLayoutData(center);
		
		Composite right = new Composite(this, SWT.NONE);
		right.setBackgroundImage(Activator.getImageDescriptor("/icons/right_title_back.gif").createImage(display));
		right.setLayoutData(side);
		
		
		side = new GridData(GridData.FILL, GridData.FILL, false, false);
		side.widthHint = 5;
		side.heightHint = 18 * explainRows;
		
		center = new GridData(GridData.FILL, GridData.FILL, true, false); 
		center.heightHint = 18 * explainRows;
		
		Composite sideDummy = new Composite(this, SWT.NONE);
		sideDummy.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		sideDummy.setLayoutData(side);
		
		Text txtExplain = new Text(this, SWT.BORDER_SOLID | SWT.MULTI | SWT.WRAP);
		txtExplain.setEditable(false);
		txtExplain.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		txtExplain.setLayoutData(center);
		txtExplain.setText(explain);
		txtExplain.setForeground(new Color(display, 129, 129, 129));
		
		sideDummy = new Composite(this, SWT.NONE);
		sideDummy.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		sideDummy.setLayoutData(side);
		
		side = new GridData(GridData.FILL, GridData.FILL, false, true);
		side.widthHint = 5;
		
		center = new GridData(GridData.FILL, GridData.FILL, true, true); 
		
		sideDummy = new Composite(this, SWT.NONE);
		sideDummy.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		sideDummy.setLayoutData(side);
		
		contents = new Composite(this, SWT.NONE);
		contents.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		contents.setLayoutData(center);
		
		sideDummy = new Composite(this, SWT.NONE);
		sideDummy.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		sideDummy.setLayoutData(side);
		
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		layout.verticalSpacing = 3;
		layout.horizontalSpacing = 0;
		
		contents.setLayout(layout);
	}
	
	public SubTitleBoard(Composite parent, int style, String title, String explain, int explainRows, int marginWidth) {
		super(parent, style);

		this.display = parent.getDisplay();
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = marginWidth;
		layout.marginHeight = 2;
		layout.numColumns = 3;
		layout.verticalSpacing = 3;
		layout.horizontalSpacing = 0;
		super.setLayout(layout);
		this.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		GridData side = new GridData(GridData.FILL, GridData.FILL, false, false);
		side.widthHint = 5;
		side.heightHint = 18;
		
		GridData center = new GridData(GridData.FILL, GridData.FILL, true, false); 
		center.heightHint = 18;
		
		Composite left = new Composite(this, SWT.NONE);
		left.setBackgroundImage(Activator.getImageDescriptor("/icons/left_title_back.gif").createImage(display));
		left.setLayoutData(side);
		
		CLabel lbTitle = new CLabel(this, SWT.NONE);
		lbTitle.setText(title);
		lbTitle.setAlignment(SWT.LEFT);
		lbTitle.setFont(new Font(display, "Tahoma", 10, SWT.BOLD));
		lbTitle.setForeground(new Color(display, 49, 106, 197));
		lbTitle.setBackground(Activator.getImageDescriptor("/icons/center_title_back.gif").createImage(display));
		lbTitle.setLayoutData(center);
		
		Composite right = new Composite(this, SWT.NONE);
		right.setBackgroundImage(Activator.getImageDescriptor("/icons/right_title_back.gif").createImage(display));
		right.setLayoutData(side);
		
		
		side = new GridData(GridData.FILL, GridData.FILL, false, false);
		side.widthHint = 5;
		side.heightHint = 18 * explainRows;
		
		center = new GridData(GridData.FILL, GridData.FILL, true, false); 
		center.heightHint = 18 * explainRows;
		
		Composite sideDummy = new Composite(this, SWT.NONE);
		sideDummy.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		sideDummy.setLayoutData(side);
		
		Text txtExplain = new Text(this, SWT.BORDER_SOLID | SWT.MULTI | SWT.WRAP);
		txtExplain.setEditable(false);
		txtExplain.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		txtExplain.setLayoutData(center);
		txtExplain.setText(explain);
		txtExplain.setForeground(new Color(display, 129, 129, 129));
		
		sideDummy = new Composite(this, SWT.NONE);
		sideDummy.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		sideDummy.setLayoutData(side);
		
		side = new GridData(GridData.FILL, GridData.FILL, false, true);
		side.widthHint = 5;
		
		center = new GridData(GridData.FILL, GridData.FILL, true, true); 
		
		sideDummy = new Composite(this, SWT.NONE);
		sideDummy.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		sideDummy.setLayoutData(side);
		
		contents = new Composite(this, SWT.NONE);
		contents.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		contents.setLayoutData(center);
		
		sideDummy = new Composite(this, SWT.NONE);
		sideDummy.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		sideDummy.setLayoutData(side);
		
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		layout.verticalSpacing = 3;
		layout.horizontalSpacing = 0;
		
		contents.setLayout(layout);
	}
	
	public void hide()
	{
		if(orgHeight == 0)
		{
			orgHeight = ((GridData)this.getLayoutData()).heightHint;
		}
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 20;
		
		this.setLayoutData(layoutData);
		this.getParent().layout(true);
	}
	
	public void show()
	{
		if(orgHeight != 0)
		{
			GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
			layoutData.heightHint = orgHeight;
			
			this.setLayoutData(layoutData);
			this.getParent().layout(true);
			orgHeight = 0;
		}
	}
	
	public Composite getPanel()
	{
		return contents;
	}

}
