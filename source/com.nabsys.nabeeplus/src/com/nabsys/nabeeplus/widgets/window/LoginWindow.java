package com.nabsys.nabeeplus.widgets.window;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.common.cipher.hash.Hash;
import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.widgets.PopupWindow;

public class LoginWindow extends PopupWindow{
	
	private HashMap<String, String> params = null;
	
	public LoginWindow(Shell parent, HashMap<String, String> params)
	{
		super(parent);
		this.params = params;
	}
	
	public HashMap<String, String> open(IWorkbenchWindow window)
	{
		setTitle(NBLabel.get(0x008C));
		setImage(Activator.getImageDescriptor("/icons/keylock.gif").createImage(shell.getDisplay()));
		setSize(new Point(330, 300));
		setLayout(new FillLayout());
		setTitleImage(Activator.getImageDescriptor("/icons/login_big.gif").createImage(shell.getDisplay()), 
				shell.getSize().x - 81,
				0,
				NBLabel.get(0x0088),
				NBLabel.get(0x0089));

		int width = window.getShell().getSize().x;
		int height = window.getShell().getSize().y;
		int x = window.getShell().getLocation().x;
		int y = window.getShell().getLocation().y;
		
		setLocation((width / 2) + x - (330 / 2), (height / 2) + y - (300 / 2));
		
		Canvas canvas = getCanvas(SWT.NULL);
		
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				drawTitle(e);
				drawDevider(e, shell.getSize().y - 80);
			}
		});
		
		openWindow();
		
		CLabel lblUser = new CLabel(canvas, SWT.NONE);
		lblUser.setText(NBLabel.get(0x0082) + ":");
		lblUser.setAlignment(SWT.RIGHT);
		lblUser.setLocation(35, 100);
		lblUser.setSize(75, 15);
		
		final Text txtUser = new Text(canvas, SWT.BORDER);
		txtUser.setLocation(123, 98);
		txtUser.setSize(150, 18);
		txtUser.setTextLimit(20);
		if(params.containsKey("USER"))
			txtUser.setText(params.get("USER"));
		
		CLabel lblPassword = new CLabel(canvas, SWT.NONE);
		lblPassword.setText(NBLabel.get(0x0083) + ":");
		lblPassword.setAlignment(SWT.RIGHT);
		lblPassword.setLocation(35, 140);
		lblPassword.setSize(75, 15);
		
		final Text txtPassword = new Text(canvas, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLocation(123, 140);
		txtPassword.setSize(150, 18);
		txtPassword.setTextLimit(20);
		if(params.containsKey("PASSWORD"))
			txtPassword.setText(params.get("PASSWORD"));
		
		if(txtUser.getText().equals(""))
			txtUser.setFocus();
		else
			txtPassword.setFocus();
		
		CLabel lblSave = new CLabel(canvas, SWT.NONE);
		lblSave.setText(NBLabel.get(0x0084) + ":");
		lblSave.setAlignment(SWT.RIGHT);
		lblSave.setLocation(20, 180);
		lblSave.setSize(90, 15);
		
		final Button btnSave = new Button(canvas, SWT.CHECK);
		btnSave.setLocation(123, 180);
		btnSave.setSize(20, 18);
		
		
		final Button btnFinish = new Button(canvas, SWT.PUSH);
		btnFinish.setText(NBLabel.get(0x008C));
		btnFinish.setSize(80, 23);
		btnFinish.setLocation(shell.getSize().x - 190, shell.getSize().y - 67);
		
		if(!params.get("USER").equals("") && !params.get("PASSWORD").equals(""))
			btnFinish.setEnabled(true);
		else
			btnFinish.setEnabled(false);
		
		final Button btnCancel = new Button(canvas, SWT.PUSH);
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
					else if(e.widget == btnFinish) 
					{
						params.put("EVENT", "FINISH");
						params.put("USER", txtUser.getText());
						params.put("SAVE_FLAG", String.valueOf(btnSave.getSelection()));
						try {
							params.put("PASSWORD", Hash.getMD5Hash(txtPassword.getText()));
						} catch (NoSuchAlgorithmException e1) {
						}

						shell.dispose();
					}
				case SWT.Modify :
					if(e.widget == txtPassword)
					{
						if(txtPassword.getText().length() > 0 
							&& txtUser.getText().length() > 0 ) btnFinish.setEnabled(true);
							else btnFinish.setEnabled(false);
					}
					else if(e.widget == txtUser)
					{
						if(txtPassword.getText().length() > 0 
							&& txtUser.getText().length() > 0 ) btnFinish.setEnabled(true);
							else btnFinish.setEnabled(false);
					}
				}
			}
		};
		shell.setDefaultButton(btnFinish);
		txtUser.addListener(SWT.Modify, listener);
		txtPassword.addListener(SWT.Modify, listener);
		btnFinish.addListener(SWT.Selection, listener);
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
