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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.common.cipher.hash.Hash;
import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.widgets.PopupWindow;

public class NewServerWindow extends PopupWindow{

	private HashMap<String, String> params = null; 
			
	public NewServerWindow(Shell parent)
	{
		super(parent);
		params = new HashMap<String, String>();
	}
	
	public NewServerWindow(Shell shell, HashMap<String, String> params)
	{
		super(shell);
		this.params = params;
	}
	
	public HashMap<String, String> open(IWorkbenchWindow window)
	{
		setTitle(NBLabel.get(0x007A));
		setImage(Activator.getImageDescriptor("/icons/resource_persp.gif").createImage(shell.getDisplay()));
		setSize(new Point(430, 500));
		setLayout(new FillLayout());
		setTitleImage(Activator.getImageDescriptor("/icons/workset_wiz.png").createImage(shell.getDisplay()), 
				shell.getSize().x - 81,
				0,
				NBLabel.get(0x007B),
				NBLabel.get(0x007C));
		
		int width = window.getShell().getSize().x;
		int height = window.getShell().getSize().y;
		int x = window.getShell().getLocation().x;
		int y = window.getShell().getLocation().y;
		
		setLocation((width / 2) + x - (400 / 2), (height / 2) + y - (500 / 2));
		
		Canvas canvas = getCanvas(SWT.NULL);
		
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				drawTitle(e);
				drawDevider(e, shell.getSize().y - 80);
			}
		});
		
		openWindow();
		
		CLabel lblServerName = new CLabel(canvas, SWT.NONE);
		lblServerName.setText(NBLabel.get(0x0087) + ":");
		lblServerName.setAlignment(SWT.RIGHT);
		lblServerName.setLocation(20, 90);
		lblServerName.setSize(90, 15);
		
		
		final Text txtServerName = new Text(canvas, SWT.BORDER);
		txtServerName.setLocation(115, 88);
		txtServerName.setSize(shell.getSize().x - 140, 18);
		txtServerName.setTextLimit(46);
		if(params.containsKey("SERVER_NAME"))
		{
			txtServerName.setText(params.get("SERVER_NAME"));
			txtServerName.setEditable(false);
		}
		
		Group addrGroup = new Group(canvas, SWT.NONE);
		addrGroup.setText(NBLabel.get(0x0048));
		addrGroup.setSize(shell.getSize().x - 45, 95);
		addrGroup.setLocation(20, 130);
		
		CLabel lblAddress = new CLabel(addrGroup, SWT.NONE);
		lblAddress.setText(NBLabel.get(0x007D) + ":");
		lblAddress.setAlignment(SWT.RIGHT);
		lblAddress.setLocation(15, 20);
		lblAddress.setSize(75, 15);
		
		final Text txtAddress = new Text(addrGroup, SWT.BORDER);
		txtAddress.setLocation(93, 18);
		txtAddress.setSize(100, 18);
		txtAddress.setTextLimit(15);
		if(params.containsKey("IP_ADDRESS")) txtAddress.setText(params.get("IP_ADDRESS"));
		
		CLabel lblPort = new CLabel(addrGroup, SWT.NONE);
		lblPort.setText(NBLabel.get(0x007E) + ":");
		lblPort.setAlignment(SWT.RIGHT);
		lblPort.setLocation(15, 45);
		lblPort.setSize(75, 15);
		
		final Text txtPort = new Text(addrGroup, SWT.BORDER);
		txtPort.setLocation(93, 43);
		txtPort.setSize(50, 18);
		txtPort.setTextLimit(5);
		if(params.containsKey("PORT")) txtPort.setText(params.get("PORT"));
		
		CLabel lblEncoding = new CLabel(addrGroup, SWT.NONE);
		lblEncoding.setText(NBLabel.get(0x0095) + ":");
		lblEncoding.setAlignment(SWT.RIGHT);
		lblEncoding.setLocation(15, 70);
		lblEncoding.setSize(75, 15);
		
		final Combo cbEncoding = new Combo(addrGroup, SWT.BORDER|SWT.READ_ONLY);
		cbEncoding.setLocation(93, 68);
		cbEncoding.setSize(100, 18);
		cbEncoding.add("EUC-KR");
		cbEncoding.add("ISO-8859-1");
		cbEncoding.add("US-ASCII");
		cbEncoding.add("UTF-8");
		cbEncoding.add("UTF-16");
		
		if(params.containsKey("ENCODING")) cbEncoding.setText(params.get("ENCODING"));
		else cbEncoding.setText("EUC-KR");


		
		
		Group monGroup = new Group(canvas, SWT.NONE);
		monGroup.setText(NBLabel.get(0x007F));
		monGroup.setSize(shell.getSize().x - 45, 70);
		monGroup.setLocation(20, 240);
		monGroup.setVisible(false);
		
		CLabel lblMonAddress = new CLabel(monGroup, SWT.NONE);
		lblMonAddress.setText(NBLabel.get(0x0080) + ":");
		lblMonAddress.setAlignment(SWT.RIGHT);
		lblMonAddress.setLocation(15, 20);
		lblMonAddress.setSize(75, 15);
		
		final Text txtMonAddress = new Text(monGroup, SWT.BORDER);
		txtMonAddress.setLocation(93, 18);
		txtMonAddress.setSize(100, 18);
		txtMonAddress.setTextLimit(15);
		if(params.containsKey("IP_MONADDRESS")) txtMonAddress.setText(params.get("IP_MONADDRESS"));
		
		CLabel lblMonPort = new CLabel(monGroup, SWT.NONE);
		lblMonPort.setText(NBLabel.get(0x007E) + ":");
		lblMonPort.setAlignment(SWT.RIGHT);
		lblMonPort.setLocation(15, 45);
		lblMonPort.setSize(75, 15);
		
		final Text txtMonPort = new Text(monGroup, SWT.BORDER);
		txtMonPort.setLocation(93, 43);
		txtMonPort.setSize(50, 18);
		txtMonPort.setTextLimit(5);
		if(params.containsKey("MONPORT")) txtMonPort.setText(params.get("MONPORT"));
		
		
		
		
		
		Group userGroup = new Group(canvas, SWT.NONE);
		userGroup.setText(NBLabel.get(0x0081));
		userGroup.setSize(shell.getSize().x - 45, 70);
		//userGroup.setLocation(20, 330);
		userGroup.setLocation(20, 240);
		
		CLabel lblUser = new CLabel(userGroup, SWT.NONE);
		lblUser.setText(NBLabel.get(0x0082) + ":");
		lblUser.setAlignment(SWT.RIGHT);
		lblUser.setLocation(15, 20);
		lblUser.setSize(75, 15);
		
		final Text txtUser = new Text(userGroup, SWT.BORDER);
		txtUser.setLocation(93, 18);
		txtUser.setSize(150, 18);
		txtUser.setTextLimit(20);
		if(params.containsKey("USER")) txtUser.setText(params.get("USER"));
		
		CLabel lblPassword = new CLabel(userGroup, SWT.NONE);
		lblPassword.setText(NBLabel.get(0x0083) + ":");
		lblPassword.setAlignment(SWT.RIGHT);
		lblPassword.setLocation(15, 45);
		lblPassword.setSize(75, 15);
		
		final Text txtPassword = new Text(userGroup, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLocation(93, 45);
		txtPassword.setSize(150, 18);
		txtPassword.setTextLimit(20);

		
		final Button btnSavePassword = new Button(userGroup, SWT.CHECK);
		btnSavePassword.setText(NBLabel.get(0x0084));
		btnSavePassword.setLocation(93 + 150 + 10, 47);
		btnSavePassword.setSize(110, 18);

		final Button btnConnect = new Button(canvas, SWT.PUSH);
		btnConnect.setText(NBLabel.get(0x004B));
		btnConnect.setSize(80, 23);
		btnConnect.setLocation(shell.getSize().x - 280, shell.getSize().y - 67);
		btnConnect.setEnabled(false);
		
		final Button btnFinish = new Button(canvas, SWT.PUSH);
		btnFinish.setText(NBLabel.get(0x0085));
		btnFinish.setSize(80, 23);
		btnFinish.setLocation(shell.getSize().x - 190, shell.getSize().y - 67);
		if(params.containsKey("SERVER_NAME"))
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
						if(txtServerName.getText().equals("")) return;
						
						params.put("EVENT", "FINISH");
						params.put("SERVER_NAME", txtServerName.getText());
						params.put("IP_ADDRESS", txtAddress.getText().replace(" ", ""));
						params.put("PORT", txtPort.getText());
						params.put("IP_MONADDRESS", txtMonAddress.getText().replace(" ", ""));
						params.put("MONPORT", txtMonPort.getText());
						params.put("USER", txtUser.getText());
						params.put("ENCODING", cbEncoding.getText());
						try {
							if(txtPassword.getText().equals(""))
								params.put("PASSWORD", "");
							else
								params.put("PASSWORD", Hash.getMD5Hash(txtPassword.getText()));
						} catch (NoSuchAlgorithmException e1) {
						}
						params.put("SAVE_FLAG", String.valueOf(btnSavePassword.getSelection()));
						shell.dispose();
					}
					else if(e.widget == btnConnect)
					{
						if(txtServerName.getText().equals("")) return;
						if(txtAddress.getText().equals("")) return;
						if(txtPort.getText().equals("")) return;
						if(txtUser.getText().equals("")) return;
						if(txtPassword.getText().equals("")) return;
						
						params.put("EVENT", "CONNECT");
						params.put("SERVER_NAME", txtServerName.getText());
						params.put("IP_ADDRESS", txtAddress.getText().replace(" ", ""));
						params.put("PORT", txtPort.getText());
						params.put("IP_MONADDRESS", txtMonAddress.getText().replace(" ", ""));
						params.put("MONPORT", txtMonPort.getText());
						params.put("USER", txtUser.getText());
						params.put("ENCODING", cbEncoding.getText());
						try {
							if(txtPassword.getText().equals(""))
								params.put("PASSWORD", "");
							else
								params.put("PASSWORD", Hash.getMD5Hash(txtPassword.getText()));
						} catch (NoSuchAlgorithmException e1) {
						}
						params.put("SAVE_FLAG", String.valueOf(btnSavePassword.getSelection()));
						shell.dispose();
					}
				case SWT.Verify :
					if(e.widget == txtPort)
					{
						e.doit = e.text.matches("[0-9]*");
					}
					else if(e.widget == txtAddress)
					{
						e.doit = (e.text.matches("[0-9]*") ||
									e.text.matches("[.]*") ||
									e.text.matches("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)." +
									"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)." +
									"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)." +
									"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"));
					}
					else if(e.widget == txtMonPort)
					{
						e.doit = e.text.matches("[0-9]*");
					}
					else if(e.widget == txtMonAddress)
					{
						e.doit = (e.text.matches("[0-9]*") ||
									e.text.matches("[.]*") ||
									e.text.matches("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)." +
									"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)." +
									"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)." +
									"(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"));
					}
				case SWT.Modify :
					if(e.widget == txtServerName)
					{
						if(txtServerName.getText().length() > 0 
								&& txtAddress.getText().length() > 0 
								&& txtPort.getText().length() > 0 
								&& txtUser.getText().length() > 0 ) btnFinish.setEnabled(true);
						else btnFinish.setEnabled(false);
						
						if(txtServerName.getText().length() > 0 
							&& txtAddress.getText().length() > 0 
							&& txtPort.getText().length() > 0 
							&& txtPassword.getText().length() > 0 
							&& txtUser.getText().length() > 0 ) btnConnect.setEnabled(true);
						else btnConnect.setEnabled(false);
					}
					else if(e.widget == txtAddress)
					{
						if(txtServerName.getText().length() > 0 
								&& txtAddress.getText().length() > 0 
								&& txtPort.getText().length() > 0 
								&& txtUser.getText().length() > 0 ) btnFinish.setEnabled(true);
						else btnFinish.setEnabled(false);
						
						if(txtServerName.getText().length() > 0 
								&& txtAddress.getText().length() > 0 
								&& txtPort.getText().length() > 0 
								&& txtUser.getText().length() > 0 ) btnConnect.setEnabled(true);
							else btnConnect.setEnabled(false);
					}
					else if(e.widget == txtPort)
					{
						if(txtServerName.getText().length() > 0 
								&& txtAddress.getText().length() > 0 
								&& txtPort.getText().length() > 0 
								&& txtUser.getText().length() > 0 ) btnFinish.setEnabled(true);
						else btnFinish.setEnabled(false);
						
						if(txtServerName.getText().length() > 0 
								&& txtAddress.getText().length() > 0 
								&& txtPort.getText().length() > 0 
								&& txtUser.getText().length() > 0 ) btnConnect.setEnabled(true);
							else btnConnect.setEnabled(false);
					}
					else if(e.widget == txtPassword)
					{
						if(txtServerName.getText().length() > 0 
								&& txtAddress.getText().length() > 0 
								&& txtPort.getText().length() > 0 
								&& txtUser.getText().length() > 0 ) btnFinish.setEnabled(true);
						else btnFinish.setEnabled(false);
						
						if(txtServerName.getText().length() > 0 
								&& txtAddress.getText().length() > 0 
								&& txtPort.getText().length() > 0 
								&& txtUser.getText().length() > 0 ) btnConnect.setEnabled(true);
							else btnConnect.setEnabled(false);
					}
					else if(e.widget == txtUser)
					{
						if(txtServerName.getText().length() > 0 
								&& txtAddress.getText().length() > 0 
								&& txtPort.getText().length() > 0 
								&& txtUser.getText().length() > 0 ) btnFinish.setEnabled(true);
						else btnFinish.setEnabled(false);
						
						if(txtServerName.getText().length() > 0 
								&& txtAddress.getText().length() > 0 
								&& txtPort.getText().length() > 0 
								&& txtPassword.getText().length() > 0 
								&& txtUser.getText().length() > 0 ) btnConnect.setEnabled(true);
							else btnConnect.setEnabled(false);
					}
				}
			}
		};
		shell.setDefaultButton(btnFinish);
		txtServerName.addListener(SWT.Modify, listener);
		txtAddress.addListener(SWT.Verify, listener);
		txtAddress.addListener(SWT.Modify, listener);
		txtPort.addListener(SWT.Verify, listener);
		txtPort.addListener(SWT.Modify, listener);
		txtMonAddress.addListener(SWT.Verify, listener);
		txtMonPort.addListener(SWT.Verify, listener);
		txtPassword.addListener(SWT.Modify, listener);
		txtUser.addListener(SWT.Modify, listener);
		btnConnect.addListener(SWT.Selection, listener);
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
