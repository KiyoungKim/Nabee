package com.nabsys.nabeeplus;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

import com.nabsys.common.label.NLabel;
import com.nabsys.nabeeplus.common.DOMConfigurator;
import com.nabsys.nabeeplus.common.ResourceFactory;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.model.Model;
import com.nabsys.nabeeplus.views.model.Server;
import com.nabsys.nabeeplus.views.model.ServerConnect;

/**
 * This class controls all aspects of the application's execution
 */
@SuppressWarnings("restriction")
public class Application implements IApplication {
	
	public static final String PLUGIN_ID = "com.nabsys.nabeeplus";
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
		Display display = PlatformUI.createDisplay();
		
		DOMConfigurator config = null;
		try {
			
			NLabel label = new NLabel();
			label.loadLabel();
			label.setLocale(NBLabel.getLocale());
			
			Bundle bundle = Platform.getBundle(Application.PLUGIN_ID);
			URL fileURL = FileLocator.toFileURL(BundleUtility.find(bundle, "configuration/nabee.xml"));
				
			config = new DOMConfigurator(fileURL.getPath());
				
			//getInitialize(config, display);
			ResourceFactory.setServerRoot(getInitialize(config, display));
			
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
		} catch (ParserConfigurationException e) {
		} catch (TransformerException e) {
		} catch (IOException e) {
		} catch (SAXException e) {
		} catch (NullPointerException e) {
		} catch (Exception e) {
		}  finally {
			display.dispose();
		}
		
		return IApplication.EXIT_OK;
	}
	
	private Model getInitialize(DOMConfigurator config, Display display)
	{
		ArrayList<String> serverList = config.getSubNodeIDList("monitor");
		Model root = new Model();
		
		for(int i=0; i<serverList.size(); i++)
		{
			Server server = new Server(root, serverList.get(i), Activator.getImageDescriptor("/icons/disconserver.gif").createImage(display));
			ServerConnect sc = new ServerConnect(NBLabel.get(0x0099)  + " : " + NBLabel.get(0x0098), Activator.getImageDescriptor("/icons/net_disconnect.gif").createImage(display));
			
			HashMap<String, String> serverSetting = config.getSubNodeMapByNodeID("monitor", serverList.get(i));
			
			sc.setUser(serverSetting.containsKey("user")?serverSetting.get("user"):"");
			sc.setPassword(serverSetting.containsKey("password")?serverSetting.get("password"):"");
			sc.setHostAddr(serverSetting.containsKey("ip")?serverSetting.get("ip"):"");
			sc.setHostPort(serverSetting.containsKey("port")?Integer.parseInt(serverSetting.get("port")):0);
			sc.setChannel(serverSetting.containsKey("channel")?serverSetting.get("channel"):"");
			sc.setChannelPort(serverSetting.containsKey("channel-port")?Integer.parseInt(serverSetting.get("channel-port").equals("")?"0":serverSetting.get("channel-port")):0);
			sc.setServerEncoding(serverSetting.containsKey("server-encoding")?serverSetting.get("server-encoding"):"UTF-8");
			
			server.setConnection(sc);
		}
		return root;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
