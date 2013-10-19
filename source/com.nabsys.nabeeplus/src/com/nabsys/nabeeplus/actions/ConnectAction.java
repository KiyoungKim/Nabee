package com.nabsys.nabeeplus.actions;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

import com.nabsys.nabeeplus.Application;
import com.nabsys.nabeeplus.common.DOMConfigurator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.views.ServerView;
import com.nabsys.nabeeplus.widgets.window.LoginWindow;

@SuppressWarnings("restriction")
public class ConnectAction extends Action{
	private final IWorkbenchWindow window;

	public ConnectAction(IWorkbenchWindow window)
	{
		super();
		this.window = window;
	}

	public void run()
	{
		
		HashMap<String, String> params = ((ServerView)window.getActivePage().findView(ServerView.ID)).getUserInfo();
		if(!params.get("PASSWORD").equals(""))
		{
			if(((ServerView)window.getActivePage().findView(ServerView.ID)).connectServer(params))
			{
				return;
			}
			else
			{
				params.put("PASSWORD", "");
			}
		}
		
		boolean loop = true;

		while(loop)
		{
			LoginWindow login = new LoginWindow(this.window.getShell(), params);
			
			params = login.open(window);
			
			if(params.containsKey("EVENT") && params.get("EVENT").equals("FINISH"))
			{
				if(((ServerView)window.getActivePage().findView(ServerView.ID)).connectServer(params))
				{
					break;
				}

				params.put("PASSWORD", "");
			}
			else
			{
				return;
			}
		}
		
		DOMConfigurator config = null;
		
		try{
			Bundle bundle = Platform.getBundle(Application.PLUGIN_ID);
			URL fileURL = FileLocator.toFileURL(BundleUtility.find(bundle, "configuration/nabee.xml"));
			
			config = new DOMConfigurator(fileURL.getPath());
			
			String id = params.get("SERVER_NAME");
			HashMap<String, String> nodeMap = new HashMap<String, String>();

			if(params.get("SAVE_FLAG").equals("true"))
			{
				nodeMap.put("password"		, params.get("PASSWORD"));
			}
			else
			{
				nodeMap.put("password"		, "");
			}

			nodeMap.put("user"			, params.get("USER"));
			config.modifyConf("monitor/server", id, nodeMap);
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
}
