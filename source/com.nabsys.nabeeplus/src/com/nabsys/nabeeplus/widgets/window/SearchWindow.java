package com.nabsys.nabeeplus.widgets.window;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.ResourceFactory;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.widgets.NCheckBox;
import com.nabsys.nabeeplus.widgets.NRadio;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.nabeeplus.widgets.PopupWindow;

public class SearchWindow extends PopupWindow{
	
	public SearchWindow(Shell parent)
	{
		super(parent);
	}
	
	public void setTitle(String title)
	{
		shell.setText(title);
	}
	
	public void setImage(String path)
	{
		shell.setImage(Activator.getImageDescriptor(path).createImage(shell.getDisplay()));
	}
	
	public HashMap<String, String> open(IWorkbenchWindow window)
	{
		shell.setSize(new Point(400, 350));
		
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
		
		shell.setLocation((width / 2) + x - (400 / 2), (height / 2) + y - (250 / 2));
		
		final HashMap<String, String> params = new HashMap<String, String>();
		
		NBModifiedListener mdfListener = new NBModifiedListener(){
			public void modified(String name, String value) {
				params.put(name, value);
			}
		};
		
		
		Composite schBack = new Composite(shell, SWT.NULL);
		schBack.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginLeft = 20;
		layout.marginRight = 20;
		layout.marginTop = 15;
		layout.marginBottom = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		schBack.setLayout(layout);
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 140;
		layoutData.verticalAlignment = SWT.TOP;
		
		Group schGroup = new Group(schBack, SWT.NONE);
		schGroup.setLayout(layout);
		schGroup.setLayoutData(layoutData);
		schGroup.setText("SQL Search");
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 18;
		
		CLabel label = new CLabel(schGroup, SWT.NONE);
		label.setLayoutData(layoutData);
		label.setText(NBLabel.get(0x0249));
		
		final NStyledText txtSch = new NStyledText(schGroup, "SCH_KEY", SWT.BORDER);
		txtSch.setLayoutData(layoutData);
		txtSch.addNBModifiedListener(mdfListener);
		txtSch.setText(ResourceFactory.getSearchKey());
		params.put("SCH_KEY", ResourceFactory.getSearchKey());
		
		
		
		NCheckBox chkFolder = new NCheckBox(schGroup, "FLDR", NBLabel.get(0x024A), NBLabel.get(0x024A), SWT.NONE);
		chkFolder.setLayoutData(layoutData);
		chkFolder.addNBModifiedListener(mdfListener);
		chkFolder.setSelection(ResourceFactory.isSearchFolder());
		params.put("FLDR", String.valueOf(ResourceFactory.isSearchFolder()));
		
		NCheckBox chkContents = new NCheckBox(schGroup, "CNTS", NBLabel.get(0x024B), NBLabel.get(0x024B), SWT.NONE);
		chkContents.setLayoutData(layoutData);
		chkContents.addNBModifiedListener(mdfListener);
		chkContents.setSelection(ResourceFactory.isSearchContents());
		params.put("CNTS", String.valueOf(ResourceFactory.isSearchContents()));
		
		NCheckBox chkCase = new NCheckBox(schGroup, "CASE", NBLabel.get(0x0251), NBLabel.get(0x0251), SWT.NONE);
		chkCase.setLayoutData(layoutData);
		chkCase.addNBModifiedListener(mdfListener);
		chkCase.setSelection(ResourceFactory.isCaseSensitive());
		params.put("CASE", String.valueOf(ResourceFactory.isCaseSensitive()));
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 60;
		layoutData.verticalAlignment = SWT.TOP;
		
		Group schemaGroup = new Group(schBack, SWT.NONE);
		schemaGroup.setLayout(layout);
		schemaGroup.setLayoutData(layoutData);
		schemaGroup.setText("Schema");
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 18;
		
		NRadio rdoFull = new NRadio(schemaGroup, "FUL", NBLabel.get(0x024C), SWT.NONE);
		rdoFull.setLayoutData(layoutData);
		rdoFull.addNBModifiedListener(mdfListener);
		rdoFull.setSelection(ResourceFactory.isSearchSchemaRoot());
		params.put("FUL", String.valueOf(ResourceFactory.isSearchSchemaRoot()));
		
		NRadio rdoSel = new NRadio(schemaGroup, "RSC", NBLabel.get(0x024D), SWT.NONE);
		rdoSel.setLayoutData(layoutData);
		rdoSel.addNBModifiedListener(mdfListener);
		rdoSel.setSelection(ResourceFactory.isSearchSchemaSelected());
		params.put("RSC", String.valueOf(ResourceFactory.isSearchSchemaSelected()));
		
		
		Composite btnBack = new Composite(shell, SWT.NULL);
		btnBack.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginRight = 20;
		layout.marginTop = 15;
		layout.marginBottom = 2;
		layout.marginWidth = 5;
		layout.marginHeight = 0;
		btnBack.setLayout(layout);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 25;
		
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 80;
		layoutData.heightHint = 25;
		
		final Button btnSearch = new Button(btnBack, SWT.NONE);
		btnSearch.setLayoutData(layoutData);
		btnSearch.setText(NBLabel.get(0x0046));
		
		final Button btnCancel = new Button(btnBack, SWT.NONE);
		btnCancel.setLayoutData(layoutData);
		btnCancel.setText(NBLabel.get(0x0086));

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
					else if(e.widget == btnSearch) 
					{
						
						if(!params.containsKey("SCH_KEY") || params.get("SCH_KEY").equals(""))
						{
							IMessageBox.Warning(shell, NBLabel.get(0x024E));
							txtSch.forceFocus();
							
							return;
						}

						ResourceFactory.setCaseSensitive(params.get("CASE").equals("true"));
						ResourceFactory.setSearchContents(params.get("CNTS").equals("true"));
						ResourceFactory.setSearchFolder(params.get("FLDR").equals("true"));
						ResourceFactory.setSearchKey(params.get("SCH_KEY"));
						ResourceFactory.setSearchSchemaRoot(params.get("FUL").equals("true"));
						ResourceFactory.setSearchSchemaSelected(params.get("RSC").equals("true"));

						params.put("EVENT", "SEARCH");
						shell.dispose();
					}
				break;
				}
			}
		};
		
		btnSearch.addListener(SWT.Selection, listener);
		btnCancel.addListener(SWT.Selection, listener);
		
		openWindow();
		shell.setDefaultButton(btnSearch);
		
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return params;
	}
}
