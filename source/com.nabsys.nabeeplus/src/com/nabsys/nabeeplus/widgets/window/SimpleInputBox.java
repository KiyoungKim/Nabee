package com.nabsys.nabeeplus.widgets.window;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;

public class SimpleInputBox{
	
	private Shell shell = null;
	private String label = "";
	private String text = "";
	private HashMap<String, String> params;
	
	public SimpleInputBox(Shell parent, HashMap<String, String> params)
	{
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.TITLE);
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
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void open(IWorkbenchWindow window, boolean secure)
	{
		shell.setSize(new Point(330, 150));
		shell.setLayout(new FillLayout());
		
		int width = window.getShell().getSize().x;
		int height = window.getShell().getSize().y;
		int x = window.getShell().getLocation().x;
		int y = window.getShell().getLocation().y;
		
		shell.setLocation((width / 2) + x - (400 / 2), (height / 2) + y - (500 / 2));

		Canvas canvas = new Canvas(shell, SWT.NULL);
		
		shell.open();
		
		CLabel lblInput = new CLabel(canvas, SWT.NONE);
		lblInput.setText(label + ":");
		lblInput.setAlignment(SWT.RIGHT);
		lblInput.setLocation(10, 30);
		lblInput.setSize(110, 15);
		
		int style;
		if(secure)
		{
			style = SWT.BORDER | SWT.PASSWORD;
		}
		else
		{
			style = SWT.BORDER;
		}
		
		final Text txtInput = new Text(canvas, style);
		
		txtInput.setLocation(120, 28);
		txtInput.setSize(shell.getSize().x - 145, 18);
		txtInput.setTextLimit(35);
		txtInput.setText(text);
		txtInput.forceFocus();
		txtInput.setSelection(0, text.length());
		
		final Button btnOk = new Button(canvas, SWT.PUSH | SWT.OK);
		btnOk.setText(NBLabel.get(0x009C));
		btnOk.setSize(80, 23);
		btnOk.setLocation(shell.getSize().x - 190, shell.getSize().y - 67);
		btnOk.setEnabled(false);
		
		final Button btnCancel = new Button(canvas, SWT.PUSH | SWT.CANCEL);
		btnCancel.setText(NBLabel.get(0x0086));
		btnCancel.setSize(80, 23);
		btnCancel.setLocation(shell.getSize().x - 100, shell.getSize().y - 67);
		
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
					else if(e.widget == btnOk) 
					{
						params.put("EVENT", "OK");
						params.put("TEXT", txtInput.getText());
						shell.dispose();
					}
					break;
				case SWT.Modify :
					if(e.widget == txtInput)
					{
						if(txtInput.getText().length() > 0)
						{
							btnOk.setEnabled(true);
						}
					}
				}
			}
		};
		shell.setDefaultButton(btnOk);
		txtInput.addListener(SWT.Modify, listener);
		btnOk.addListener(SWT.Selection, listener);
		btnCancel.addListener(SWT.Selection, listener);
		
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
	}
}
