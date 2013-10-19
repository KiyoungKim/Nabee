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

import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NStyledText;

public class LoopConfig  extends ConfigPopupWindow{

	public LoopConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Loop", new Point(350, 250));
		
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
		layoutData.heightHint = 200;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 140);
		fieldFrame.setLayoutData(layoutData);
		
		NStyledText txtKeyID = fieldFrame.getTextField("MK", NBLabel.get(0x028E));
		txtKeyID.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		
		
		if(returnMap.containsKey("MK") && returnMap.get("MK") != null)
		{
			txtKeyID.setText(returnMap.get("MK")+"");
		}
		else
		{
			txtKeyID.setText("");
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

	@Override
	protected void confirm() {
		if(!returnMap.containsKey("MK") || returnMap.get("MK") == null || ((String)returnMap.get("MK")).equals(""))
		{
			IMessageBox.Warning(shell, "Need mapping list ID.");
			return;
		}
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}

}
