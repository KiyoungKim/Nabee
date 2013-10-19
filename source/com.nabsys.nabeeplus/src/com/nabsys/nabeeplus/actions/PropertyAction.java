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

import com.nabsys.nabeeplus.Application;
import com.nabsys.nabeeplus.common.DOMConfigurator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.ServerView;
import com.nabsys.nabeeplus.views.model.Server;
import com.nabsys.nabeeplus.widgets.window.NewServerWindow;

@SuppressWarnings("restriction")
public class PropertyAction extends Action implements ISelectionListener{
	private final IWorkbenchWindow window;
	public final static String ID = "com.nabsys.nabeeplus.actions.propertyAction";
	private String serverID = "";
	
	public PropertyAction(IWorkbenchWindow window)
	{
		this.window = window;
		
		setId(ID);
		setActionDefinitionId(ID);

		setText(NBLabel.get(0x0070) + "                    ");
		setToolTipText(NBLabel.get(0x006F));
		window.getSelectionService().addSelectionListener(this);
		setEnabled(false);
	}
	
	public void run()
	{
		DOMConfigurator config = null;
		
		try{
			HashMap<String, String> params = new HashMap<String, String>();
			
			Bundle bundle = Platform.getBundle(Application.PLUGIN_ID);
			URL fileURL = FileLocator.toFileURL(BundleUtility.find(bundle, "configuration/nabee.xml"));
				
			config = new DOMConfigurator(fileURL.getPath());
			HashMap<String, String> serverSetting = config.getSubNodeMapByNodeID("monitor", serverID);
			
			params.put("SERVER_NAME", serverID);
			params.put("USER", serverSetting.containsKey("user")?serverSetting.get("user"):"");
			params.put("IP_ADDRESS", serverSetting.containsKey("ip")?serverSetting.get("ip"):"");
			params.put("PORT", serverSetting.containsKey("port")?serverSetting.get("port"):"0");
			params.put("IP_MONADDRESS", serverSetting.containsKey("channel")?serverSetting.get("channel"):"");
			params.put("MONPORT", serverSetting.containsKey("channel-port")?serverSetting.get("channel-port"):"0");
			params.put("ENCODING", serverSetting.containsKey("server-encoding")?serverSetting.get("server-encoding"):"UTF-8");
			
			
			NewServerWindow newServer = new NewServerWindow(this.window.getShell(), params);
			params = newServer.open(window);
			
			if(params.containsKey("EVENT") && params.get("EVENT").equals("CANCEL"))
			{
				return;
			}
			
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
			
			config.modifyConf("monitor/server", serverID, nodeMap);
			((ServerView)window.getActivePage().findView(ServerView.ID)).modifyServer(params);

		} catch (ParserConfigurationException e) {
		} catch (TransformerException e) {
		} catch (IOException e) {
		} catch (SAXException e) {
		} catch (NullPointerException e) {
		} catch (Exception e) {
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
				setEnabled(false);
			}
			else
			{
				setEnabled(selection.size() == 1 && selection.getFirstElement() instanceof Server);
				if(selection.getFirstElement() instanceof Server)
					serverID = ((Server)selection.getFirstElement()).getName();
			}
		}
		else
		{
			setEnabled(false);
		}
	}
}
