package com.nabsys.nabeeplus.design.window;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NCheckBox;
import com.nabsys.nabeeplus.widgets.NCombo;
import com.nabsys.nabeeplus.widgets.NStyledText;

public class ServiceConfig  extends ConfigPopupWindow{
	private final int ONLINE			= 1;
	private final int MESSAGE_QUEUE 	= 2;
	private final int BATCH 			= 3;
	private final int GENERAL			= 4;
	public ServiceConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Service", new Point(350, 260));
		
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
		layoutData.heightHint = 140;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 90);
		fieldFrame.setLayoutData(layoutData);
		
		NCombo cmbType = fieldFrame.getComboField("TYPE", NBLabel.get(0x015A));
		cmbType.add("General");
		cmbType.add("Online");
		cmbType.add("Batch");
		cmbType.add("Message Queue");
		if(returnMap != null && returnMap.containsKey("TYPE"))
			cmbType.setText((String)returnMap.get("TYPE"));
		else
			cmbType.setText("General");
		returnMap.remove("TYPE");
		cmbType.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				int type = 0;
				if(value.equals("General")) type = GENERAL;
				else if(value.equals("Online")) type = ONLINE;
				else if(value.equals("Batch")) type = BATCH;
				else if(value.equals("Message Queue")) type = MESSAGE_QUEUE;
				returnMap.put(name, type);
			}
		});
		NStyledText txtID = fieldFrame.getTextField("ID", NBLabel.get(0x0153));
		if(returnMap != null && returnMap.containsKey("ID"))
			txtID.setText((String)returnMap.get("ID"));
		returnMap.remove("ID");
		txtID.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		NStyledText txtName = fieldFrame.getTextField("NAME", NBLabel.get(0x0154));
		if(returnMap != null && returnMap.containsKey("NAME"))
			txtName.setText((String)returnMap.get("NAME"));
		returnMap.remove("NAME");
		txtName.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		
		NStyledText txtRemark = fieldFrame.getTextField("REMARK", NBLabel.get(0x012E));
		if(returnMap != null && returnMap.containsKey("REMARK"))
			txtRemark.setText((String)returnMap.get("REMARK"));
		returnMap.remove("REMARK");
		txtRemark.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		NCheckBox cbActivate = fieldFrame.getCheckField("ACTIVATE", NBLabel.get(0x0146), NBLabel.get(0x0266), NBLabel.get(0x0267));
		if(returnMap != null && returnMap.containsKey("ACTIVATE"))
			cbActivate.setSelection((Boolean)returnMap.get("ACTIVATE"));
		
		cbActivate.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value.equals("true"));
			}
		});
		
		contentsBack.layout(true);
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return returnMap;
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
