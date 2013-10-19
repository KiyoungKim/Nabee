package com.nabsys.nabeeplus.widgets.window;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.widgets.PopupWindow;

public class SelectWindow extends PopupWindow{
	private ArrayList<String> labelList = null;
	private HashMap<String, String> params;
	private Button[] btnArray = null;
	
	public SelectWindow(Shell parent, HashMap<String, String> params)
	{
		super(parent);
		this.params = params;
	}
	
	public void setTitle(String title)
	{
		shell.setText(title);
	}
	
	public void setImage(String path)
	{
		shell.setImage(Activator.getImageDescriptor(path).createImage(shell.getDisplay()));
	}
	
	public void setLabel(ArrayList<String> labelList)
	{
		this.labelList = labelList;
	}
	
	public HashMap<String, String> open(IWorkbenchWindow window)
	{
		shell.setSize(new Point(330, 150));
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		
		shell.setLayout(layout);
		
		int width = window.getShell().getSize().x;
		int height = window.getShell().getSize().y;
		int x = window.getShell().getLocation().x;
		int y = window.getShell().getLocation().y;
		
		shell.setLocation((width / 2) + x - (400 / 2), (height / 2) + y - (500 / 2));

		Composite selBack = new Composite(shell, SWT.NULL);
		selBack.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginLeft = 40;
		layout.marginTop = 15;
		layout.marginBottom = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		selBack.setLayout(layout);
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 25;
		layoutData.verticalAlignment = SWT.TOP;
		
		btnArray = new Button[labelList.size()];
		
		for(int i=0; i<labelList.size(); i++)
		{
			btnArray[i] = new Button(selBack, SWT.RADIO);
			btnArray[i].setText(labelList.get(i));
			btnArray[i].setLayoutData(layoutData);
		}
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 50;
		
		Composite btnBack = new Composite(shell, SWT.NULL);
		btnBack.setLayoutData(layoutData);
		
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		btnBack.setLayout(layout);

		//dummy
		Composite dummy = new Composite(btnBack, SWT.NONE);
		dummy.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		//confirm
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 7;
		layoutData.widthHint = 70;
		
		final Button btnConfirm = new Button(btnBack, SWT.NONE);
		btnConfirm.setLayoutData(layoutData);
		btnConfirm.setText(NBLabel.get(0x009C));
		
		//cancel
		final Button btnCancel = new Button(btnBack, SWT.NONE);
		btnCancel.setLayoutData(layoutData);
		btnCancel.setText(NBLabel.get(0x0086));
		
		
		openWindow();
		
		
		
		Listener listener = new Listener() {
			public void handleEvent(Event e){
				switch(e.type)
				{
				case SWT.Selection :
					if(e.widget == btnCancel) 
					{
						params.put("EVENT", "CANCEL");
						shell.dispose();
					}
					else if(e.widget == btnConfirm) 
					{
						params.put("EVENT", "OK");
						int index = 0;
						for(int i=0; i<btnArray.length; i++)
						{
							if(btnArray[i].getSelection())
							{
								index = i;
								break;
							}
						}
						
						params.put("INDEX", Integer.toString(index));
						shell.dispose();
					}
					break;
				}
			}
		};
		shell.setDefaultButton(btnConfirm);
		btnConfirm.addListener(SWT.Selection, listener);
		btnCancel.addListener(SWT.Selection, listener);
		
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return params;
	}
}
