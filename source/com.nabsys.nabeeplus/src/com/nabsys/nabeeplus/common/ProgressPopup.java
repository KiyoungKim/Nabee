package com.nabsys.nabeeplus.common;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ProgressPopup extends Thread{
	private Shell shell = null;
	private boolean isClose = false;
	private Display display = null;
	private String title = null;
	public ProgressPopup(Shell shell, Display display)
	{
		this.shell = shell;
		this.display = display;
	}
	
	public void run()
	{
		display.asyncExec(new Runnable(){ public void run() {
			try {
				ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
				pmd.run(true, false, new IRunnableWithProgress(){
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException 
						{
							monitor.beginTask(title, 100);
							
							for(int i=0; i<100; i++)
							{
								if(!isClose)
								{
									if(i < 50)
										Thread.sleep(10);
									else if(i < 80)
										Thread.sleep(300);
									else
										Thread.sleep(1000);
								}
								monitor.worked(1);
							}
						}
				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}});
	}
	
	public void open(final String title)
	{
		this.title = title;
		start();
	}
	
	public void close()
	{
		isClose = true;
	}
}
