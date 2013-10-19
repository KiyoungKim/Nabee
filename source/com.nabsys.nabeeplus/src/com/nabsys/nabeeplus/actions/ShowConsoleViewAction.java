package com.nabsys.nabeeplus.actions;

import java.io.PrintStream;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;

public class ShowConsoleViewAction extends Action{
	private final IWorkbenchWindow window;
	public final static String ID = "com.nabsys.nabeeplus.actions.showConsolViewAction";
	
	public ShowConsoleViewAction(IWorkbenchWindow window)
	{
		super();
		this.window = window;
		setId(ID);

		setText(NBLabel.get(0x0294) + "                   ");
		setImageDescriptor(Activator.getImageDescriptor("/icons/console_view.gif"));
		
		MessageConsole console = new MessageConsole("Console" , null);
		console.clearConsole();
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
        MessageConsoleStream stdStream = console.newMessageStream();
        MessageConsoleStream errStream = console.newMessageStream();
        errStream.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        
        PrintStream stdPStream = new PrintStream(stdStream);
        PrintStream errPStream = new PrintStream(errStream);
        System.setOut(stdPStream); 
        System.setErr(errPStream);
	}
	
	public void run()
	{
		try {
			window.getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW);
		} catch (PartInitException e) {
		}
	}
}
