package com.nabsys.nabeeplus.design.window;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NCheckBox;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class DatabaseUpdateConfig extends ConfigPopupWindow{

	private SearchPopupList 	popupList 					= null;
	
	public DatabaseUpdateConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Database Update", new Point(480, 300));

		Composite contentsBack = getContentsBack();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		contentsBack.setLayout(layout);
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 100;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 120);
		fieldFrame.setLayoutData(layoutData);
		
		final NStyledText txtURL = fieldFrame.getTextField("SU", NBLabel.get(0x0268));
		if(returnMap.get("SU") != null && !((String)returnMap.get("SU")).equals(""))
		{
			txtURL.setText((String)returnMap.get("SU"));
		}
		
		txtURL.addNBModifiedListener(new NBModifiedListener(){
			private boolean isInput = false;
			public void modified(String name, String value) {
				if(isInput == true)
				{
					isInput = false;
					return;
				}
				
				Rectangle rect = new Rectangle(getLocation().x + 115, 
						getLocation().y + 99,
						txtURL.getBounds().width,
						200);
				
				if(popupList == null || popupList.isShellDisposed()) 
				{
					popupList = new SearchPopupList(shell, SWT.NONE, txtURL);
				}
				
				setSquURLList(txtURL.getText());
				
				if(popupList.isShellDisposed()) 
				{
					String returnValue = popupList.open(rect);
					if(returnValue != null)
					{
						isInput = true;
						txtURL.setText(returnValue);
						returnMap.put("SU", returnValue);
					}
					else
					{
						returnMap.put("SU", txtURL.getText());
					}
				}
			}
		});
		
		NCheckBox chkSetReturn = fieldFrame.getCheckField("ISR", NBLabel.get(0x027A), NBLabel.get(0x0266), NBLabel.get(0x0267));
		final NStyledText txtReturnKeyName = fieldFrame.getTextField("RMK", NBLabel.get(0x0279));
		chkSetReturn.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				boolean isSetReturnValue =  value.equals("true");
				returnMap.put(name, isSetReturnValue);
				if(isSetReturnValue)
				{
					txtReturnKeyName.setEditable(true);
					txtReturnKeyName.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					txtReturnKeyName.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				}
				else
				{
					txtReturnKeyName.setText("");
					txtReturnKeyName.setEditable(false);
					txtReturnKeyName.setBackground(new Color(display, 240, 240, 240));
					txtReturnKeyName.setForeground(new Color(display, 150, 150, 150));
				}
			}
		});
		
		txtReturnKeyName.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		
		if(returnMap.get("RMK") != null && !((String)returnMap.get("RMK")).equals(""))
		{
			txtReturnKeyName.setText((String)returnMap.get("RMK"));
		}
		
		if(returnMap.get("ISR") != null)
		{
			chkSetReturn.setSelection((Boolean)returnMap.get("ISR"));
			if(!(Boolean)returnMap.get("ISR"))
			{
				txtReturnKeyName.setText("");
				txtReturnKeyName.setEditable(false);
				txtReturnKeyName.setBackground(new Color(display, 240, 240, 240));
				txtReturnKeyName.setForeground(new Color(display, 150, 150, 150));
			}
		}
		
		NCheckBox chkLogging = fieldFrame.getCheckField("IL", NBLabel.get(0x0269), NBLabel.get(0x0266), NBLabel.get(0x0267));
		chkLogging.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value.equals("true"));
			}
		});
		
		if(returnMap.get("IL") != null)
		{
			chkLogging.setSelection(((Boolean)returnMap.get("IL")));
		}
		
		StyledText label = new StyledText(contentsBack, SWT.MULTI|SWT.WRAP|SWT.READ_ONLY);
		label.setText(NBLabel.get(0x0271));
		label.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		label.setForeground(new Color(display, 49, 106, 197));
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
		layoutData.widthHint = 350;
		label.setLayoutData(layoutData);
		
		contentsBack.layout(true);
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return returnMap;
	}
	
	@SuppressWarnings("unchecked")
	private void setSquURLList(String key)
	{
		if(popupList.setItems(key)) return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
		fields.put(IPC.NB_INSTNCE_ID	, instanceID);
		fields.put("CMD_CODE"			, "SL");
		fields.put("PATH"				, key);
		try {
			fields = protocol.execute(fields);
					
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			ArrayList<NBFields> list = (ArrayList<NBFields>)fields.get("LIST");
			String[] sqlList = new String[list.size()];
			for(int i=0; i<sqlList.length; i++)
			{
				sqlList[i] = (String)list.get(i).get("ID");
			}
			
			popupList.setItems(sqlList);
		} catch (Exception e) {
			e.printStackTrace();
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		}
	}

	@Override
	protected void confirm() {
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}

}
