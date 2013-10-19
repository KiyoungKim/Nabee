package com.nabsys.nabeeplus.design.window;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
import com.nabsys.nabeeplus.widgets.NCombo;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class DatabaseConfig extends ConfigPopupWindow{

	public DatabaseConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Database", new Point(350, 250));
		
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
		layoutData.heightHint = 60;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 120);
		fieldFrame.setLayoutData(layoutData);
		
		NCombo cmbDatabase = fieldFrame.getComboField("DN", NBLabel.get(0x0264));
		setDBList(cmbDatabase);
		cmbDatabase.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		
		if(returnMap.get("DN") != null && !((String)returnMap.get("DN")).equals(""))
		{
			String dn = (String)returnMap.get("DN");
			for(int i=0; i<cmbDatabase.getItemCount(); i++)
			{
				if(cmbDatabase.getItem(i).contains(dn))
				{
					cmbDatabase.setText(cmbDatabase.getItem(i));
				}
			}
		}
		else if(cmbDatabase.getItemCount() > 0)
		{
			cmbDatabase.setText(cmbDatabase.getItem(0));
		}
		
		NCheckBox chkAutoCommit = fieldFrame.getCheckField("AC", NBLabel.get(0x0265), NBLabel.get(0x0266), NBLabel.get(0x0267));
		chkAutoCommit.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value.equals("true"));
			}
		});
		
		if(returnMap.get("AC") != null)
		{
			chkAutoCommit.setSelection(((Boolean)returnMap.get("AC")));
		}
		
		contentsBack.layout(true);
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return returnMap;
	}
	
	private void setDBList(NCombo cmbDatabase)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
		fields.put(IPC.NB_INSTNCE_ID	, instanceID);
		fields.put("CMD_CODE"			, "DL");
		try {
			
			fields = protocol.execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			@SuppressWarnings("unchecked")
			ArrayList<NBFields> list = (ArrayList<NBFields>) fields.get("LIST");
			for(int i=0; i<list.size(); i++)
			{
				cmbDatabase.add((String)list.get(i).get("NAME"));
			}
		} catch (Exception e) {
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
