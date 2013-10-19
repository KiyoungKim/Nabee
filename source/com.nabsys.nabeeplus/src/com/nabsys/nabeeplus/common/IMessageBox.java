package com.nabsys.nabeeplus.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class IMessageBox{
	
	private static MessageBox box = null;
	
	public static void Info(Shell shell, String message)
	{
		if(message == null) message = "null message";
		box = new MessageBox(shell, SWT.ICON_INFORMATION);
		box.setText("Info");
		box.setMessage(message);
		box.open();
	}
	
	public static int Confirm(Shell shell, String message)
	{
		if(message == null) message = "null message";
		box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK | SWT.CANCEL);
		box.setText("Confirm");
		box.setMessage(message);
		int rtn = box.open();
		return rtn;
	}
	
	public static void Warning(Shell shell, String message)
	{
		if(message == null) message = "null message";
		box = new MessageBox(shell, SWT.ICON_WARNING);
		box.setText("Warning");
		box.setMessage(message);
		box.open();
	}
	
	public static void Error(Shell shell, String message)
	{
		if(message == null) message = "null message";
		box = new MessageBox(shell, SWT.ICON_ERROR);
		box.setText("Error");
		box.setMessage(message);
		box.open();
	}
}
