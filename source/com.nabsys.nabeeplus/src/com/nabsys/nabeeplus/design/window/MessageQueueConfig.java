package com.nabsys.nabeeplus.design.window;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NStyledText;

public class MessageQueueConfig  extends ConfigPopupWindow{

	public MessageQueueConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Message Queue", new Point(300, 170));
		
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
		layoutData.heightHint = 150;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 120);
		fieldFrame.setLayoutData(layoutData);
		

		final NStyledText txtQueueSize = fieldFrame.getTextField("QS", NBLabel.get(0x0223));
		txtQueueSize.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, Integer.parseInt(value));
			}
		});
		
		Listener listener = new Listener() {
			public void handleEvent(Event e){
				switch(e.type)
				{
				case SWT.Verify :
					if(e.widget == txtQueueSize)
					{
						e.doit = e.text.matches("[0-9]*");
					}
				}
			}
		};
		
		txtQueueSize.addListener(SWT.Verify, listener);
		
		if(returnMap.containsKey("QS"))
		{
			txtQueueSize.setText(returnMap.get("QS")+"");
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
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}
}
