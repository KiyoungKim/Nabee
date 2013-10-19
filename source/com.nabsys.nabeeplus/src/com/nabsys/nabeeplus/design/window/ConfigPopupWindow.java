package com.nabsys.nabeeplus.design.window;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.widgets.BackBoard;
import com.nabsys.nabeeplus.widgets.PopupWindow;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public abstract class ConfigPopupWindow extends PopupWindow{

	private Composite 					contentsBack 		= null;
	protected HashMap<String, Object> 	returnMap 			= null;
	protected IPCProtocol				protocol			= null;
	protected String					instanceID			= null;
	protected BackBoard 				board 				= null;
	public ConfigPopupWindow(Shell parent) {
		super(parent);
	}
	
	public void initMap(HashMap<String, Object> map)
	{
		this.returnMap = map;
	}
	
	public void setProtocol(IPCProtocol protocol){this.protocol = protocol;}
	public void setInstanceID(String instanceID){this.instanceID = instanceID;}
	
	public void open(IWorkbenchWindow window, Image icon, String title, Point size) {
		if(returnMap == null) returnMap = new HashMap<String, Object>();
		setTitle("Configuration");
		setSize(size);
		int width = window.getShell().getSize().x;
		int height = window.getShell().getSize().y;
		int x = window.getShell().getLocation().x;
		int y = window.getShell().getLocation().y;
		setLocation((width / 2) + x - (size.x / 2), (height / 2) + y - (size.y / 2));
		setLayout(new FillLayout());
		setImage(Activator.getImageDescriptor("/icons/resource_persp.gif").createImage(shell.getDisplay()));
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 20;
		
		board = new BackBoard(shell, SWT.NONE);
		board.setTitleIcon(icon, new Point(10, 8));
		board.setTitle(title + " Configuration");
		board.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		board.setLayout(layout);
		
		contentsBack = new Composite(board.getPanel(), SWT.NONE);
		contentsBack.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		contentsBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 40;
		
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.numColumns = 3;
		
		Composite btnBack = new Composite(board.getPanel(), SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayoutData(layoutData);
		btnBack.setLayout(layout);
		btnBack.setBackgroundImage(Activator.getImageDescriptor("/icons/btnback.gif").createImage(display));
		
		setButtonArea(btnBack);
		
		openWindow();
	}
	
	private void setButtonArea(Composite btnBack)
	{
		Composite fake = new Composite(btnBack, SWT.TRANSPARENT);
		fake.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 80;
		layoutData.heightHint = 15;
		
		Button btnConfirm = new Button(btnBack, SWT.NONE);
		btnConfirm.setText("Confirm");
		btnConfirm.setLayoutData(layoutData);
		btnConfirm.setSize(80, 15);
		shell.setDefaultButton(btnConfirm);
		btnConfirm.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				returnMap.put("RTN", "OK");
				confirm();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				returnMap.put("RTN", "OK");
				confirm();
			}
		});
		
		Button btnCancel = new Button(btnBack, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setSize(80, 15);
		btnCancel.setLayoutData(layoutData);
		btnCancel.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				returnMap.put("RTN", "CANCEL");
				cancel();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}
	
	protected Composite getContentsBack()
	{
		return contentsBack;
	}
	
	abstract protected void confirm();
	abstract protected void cancel();

}
