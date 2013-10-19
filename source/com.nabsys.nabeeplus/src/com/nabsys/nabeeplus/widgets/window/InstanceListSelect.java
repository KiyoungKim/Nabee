package com.nabsys.nabeeplus.widgets.window;

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
import com.nabsys.nabeeplus.widgets.NCombo;

public class InstanceListSelect  extends PopupWindow{
	public InstanceListSelect(Shell parent) {
		super(parent);
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Select an instance", new Point(350, 250));
		
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
		layoutData.heightHint = 120;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 120);
		fieldFrame.setLayoutData(layoutData);
		
		NCombo cmbList = fieldFrame.getComboField("INSTANCE", NBLabel.get(0x0093));
		ArrayList<String> list = (ArrayList<String>) returnMap.get("LIST");
		for(int i=0; i<list.size();i++)
		{
			cmbList.add(list.get(i));
		}
		cmbList.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		cmbList.setText(list.get(0));

		
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
		if(!returnMap.containsKey("INSTANCE") || ((String)returnMap.get("INSTANCE")).equals(""))
		{
			IMessageBox.Warning(shell, "Select instance");
			return;
		}
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}
}
