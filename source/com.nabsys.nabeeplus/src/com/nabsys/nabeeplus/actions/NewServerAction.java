package com.nabsys.nabeeplus.actions;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.Application;
import com.nabsys.nabeeplus.common.DOMConfigurator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.ServerView;
import com.nabsys.nabeeplus.views.model.Server;
import com.nabsys.nabeeplus.widgets.window.NewServerWindow;

@SuppressWarnings("restriction")
public class NewServerAction extends Action implements ISelectionListener{
	private final IWorkbenchWindow window;
	public final static String ID = "com.nabsys.nabeeplus.actions.newServerAction";
	
	public NewServerAction(IWorkbenchWindow window)
	{
		this.window = window;

		setId(ID);

		setText(NBLabel.get(0x005D) + "                    ");
		setToolTipText(NBLabel.get(0x0050));
		setImageDescriptor(Activator.getImageDescriptor("/icons/new_server.gif"));
		setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/new_server.gif"));
		window.getSelectionService().addSelectionListener(this);
		setEnabled(false);
	}
	
	public void run()
	{
		NewServerWindow newServer = new NewServerWindow(this.window.getShell());
		
		HashMap<String, String> params = newServer.open(window);

		if(params.containsKey("EVENT") && params.get("EVENT").equals("FINISH"))
		{
			addNewServer(params);
		}
		else if(params.containsKey("EVENT") && params.get("EVENT").equals("CONNECT"))
		{
			addNewServer(params);
			((ServerView)window.getActivePage().findView(ServerView.ID)).connectServer(params);
		}
	}
	
	private void addNewServer(HashMap<String, String> params)
	{
		DOMConfigurator config = null;
		try {
			//�������� ����
			Bundle bundle = Platform.getBundle(Application.PLUGIN_ID);
			URL fileURL = FileLocator.toFileURL(BundleUtility.find(bundle, "configuration/nabee.xml"));
			
			config = new DOMConfigurator(fileURL.getPath());
			
			String id = params.get("SERVER_NAME");
			HashMap<String, String> nodeMap = new HashMap<String, String>();
			if(params.get("SAVE_FLAG").equals("true"))
				nodeMap.put("password"		, params.get("PASSWORD"));
			else
				nodeMap.put("password"		, "");
			
			nodeMap.put("user"			, params.get("USER"));
			nodeMap.put("channel-port"	, params.get("MONPORT"));
			nodeMap.put("channel"		, params.get("IP_MONADDRESS"));
			nodeMap.put("port"			, params.get("PORT"));
			nodeMap.put("ip"			, params.get("IP_ADDRESS"));
			nodeMap.put("server-encoding", params.get("ENCODING"));
			
			config.setConf("monitor/server", id, nodeMap);
			

			((ServerView)window.getActivePage().findView(ServerView.ID)).addServer(params);

		} catch (ParserConfigurationException e) {
			IMessageBox.Error(window.getShell(), e.getMessage());
			return;
		} catch (TransformerException e) {
			IMessageBox.Error(window.getShell(), e.getMessage());
			return;
		} catch (IOException e) {
			IMessageBox.Error(window.getShell(), e.getMessage());
			return;
		} catch (SAXException e) {
			IMessageBox.Error(window.getShell(), e.getMessage());
			return;
		} catch (Exception e) {
			IMessageBox.Error(window.getShell(), e.getMessage());
			return;
		}
		
		
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}


	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {

		if(incoming instanceof IStructuredSelection)
		{
			IStructuredSelection selection = (IStructuredSelection)incoming;
			
			if(selection.getFirstElement() == null)
			{
				setEnabled(part.getSite().getId().equals(ServerView.ID));
			}
			else
			{
				setEnabled(selection.size() == 1 && selection.getFirstElement() instanceof Server);
			}
		}
		else
		{
			setEnabled(false);
		}
		
	}

}
