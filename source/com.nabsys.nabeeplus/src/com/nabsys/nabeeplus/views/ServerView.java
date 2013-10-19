package com.nabsys.nabeeplus.views;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.Application;
import com.nabsys.nabeeplus.actions.ConnectAction;
import com.nabsys.nabeeplus.actions.DisconnectAction;
import com.nabsys.nabeeplus.actions.NewServerAction;
import com.nabsys.nabeeplus.actions.PropertyAction;
import com.nabsys.nabeeplus.actions.TerminateAction;
import com.nabsys.nabeeplus.common.DOMConfigurator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.ResourceFactory;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.NabeeEditor;
import com.nabsys.nabeeplus.editors.ServerConfigEditor;
import com.nabsys.nabeeplus.editors.UserListEditor;
import com.nabsys.nabeeplus.editors.input.ServerConfigEditorInput;
import com.nabsys.nabeeplus.editors.input.UserListEditorInput;
import com.nabsys.nabeeplus.views.model.InstanceList;
import com.nabsys.nabeeplus.views.model.Model;
import com.nabsys.nabeeplus.views.model.NBContentProvider;
import com.nabsys.nabeeplus.views.model.NBLabelProvider;
import com.nabsys.nabeeplus.views.model.Server;
import com.nabsys.nabeeplus.views.model.ServerConfig;
import com.nabsys.nabeeplus.views.model.ServerConnect;
import com.nabsys.nabeeplus.views.model.UserList;
import com.nabsys.nabeeplus.widgets.window.SimpleInputBox;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.net.protocol.IPC.IPCProtocol;
import com.nabsys.net.socket.client.Socket;
import com.nabsys.process.InstanceHeaderFields;
import com.nabsys.resource.TelegramFieldContext;

@SuppressWarnings("restriction")
public class ServerView extends NabeeView{

	public static final String ID = "com.nabsys.nabeeplus.views.serverView";

	private TreeViewer 					treeViewer 				= null; 
	private IDoubleClickListener 		dbcListener 			= null;
	private ISelectionChangedListener 	selectChangedListener 	= null;
	private IAction 					deleteAction 			= null;
	private IAction						renameAction			= null;
	private IAction						refreshAction			= null;
	private IAction 					connectAction 			= null;
	private IAction 					disconnectAction 		= null;
	private TerminateAction 			terminateAction 		= null;
	private Display 					display 				= null;
	private Shell 						shell 					= null;
			
	public ServerView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		setPartName(NBLabel.get(0x0050));
		this.display = parent.getDisplay();
		this.shell = parent.getShell();

		treeViewer = new TreeViewer(parent, SWT.NONE);
		getSite().setSelectionProvider(treeViewer);

		treeViewer.setContentProvider(new NBContentProvider());
		treeViewer.setLabelProvider(new NBLabelProvider(parent.getDisplay()));
		treeViewer.setUseHashlookup(true);
		
		registContextMenu(parent);
		addRetargetableAction(parent);
		
		treeViewer.addDoubleClickListener(genDbClickListener());
		treeViewer.addSelectionChangedListener(genSelectChangedListener());
		
		treeViewer.setInput(ResourceFactory.getServerRoot());
	}
	
	public void closeInstanceView(String serverName)
	{
		InstanceView instanceView = ((InstanceView)getSite().getWorkbenchWindow().getActivePage().findView(InstanceView.ID));
		instanceView.initInstance(serverName);
	}
	
	public void openInstanceView(String serverName, InstanceList instanceList, NBFields netFields, Server server)
	{
		try {
			ArrayList<Boolean> runStateList = new ArrayList<Boolean>();
			if(netFields != null)
			{
				@SuppressWarnings("unchecked")
				ArrayList<NBFields> nList = (ArrayList<NBFields>) netFields.get("INSTANCE_LIST");
				ArrayList<String> iList = new ArrayList<String>();

				try{
					for(int i=0; i<nList.size(); i++)
					{
						iList.add((String)nList.get(i).get("INSTANCE_NAME"));
						runStateList.add(((String)nList.get(i).get("RUNNING")).equals("true"));
					}
					instanceList.setList(iList);
				} catch (NullPointerException e) {
				}
			}
			
			IPCProtocol protocol = server.getConnect().getProtocol();

			InstanceView instanceView = (InstanceView)getSite().getPage().showView(InstanceView.ID);
			instanceView.setProtocol(protocol);
			instanceView.setPartName(serverName);
			instanceView.setInstance(serverName, instanceList.getList(), runStateList);
			instanceView.setPartProperty("SERVER_NAME", serverName);
			instanceView.setServer(server);
		} catch (PartInitException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0096));
		}

	}
	
	public void closeConnection(Server server)
	{
		try {
			server.getConnect().getProtocol()._close();
		} catch (IOException e) {
			IMessageBox.Error(shell, e.getMessage());
		}
		
		server.setImage(Activator.getImageDescriptor("/icons/disconserver.gif").createImage(display));
		server.removeConfig();
		server.removeUserList();
		server.removeInstance();
		treeViewer.refresh();
	
		connectAction.setEnabled(true);
		disconnectAction.setEnabled(false);
		refreshAction.setEnabled(false);
		terminateAction.setEnabled(false);
		
		closeInstanceView(server.getName());
	}
	
	public void disconnectServer(boolean needAlert)
	{
		if(needAlert && IMessageBox.Confirm(shell, NBLabel.get(0x0094)) != SWT.OK) return;
		
		Server server = null;
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		
		if(selection.getFirstElement() instanceof Server)
		{
			server = (Server)selection.getFirstElement();
		}
		else
		{
			IMessageBox.Error(shell, NBLabel.get(0x0091));
			return;
		}
		
		closeConnection(server);
	}
	
	public HashMap<String, String> getUserInfo()
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		ServerConnect connect = null;
		if(!treeViewer.getSelection().isEmpty())
		{
			IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
			
			if(selection.getFirstElement() instanceof Server)
			{
				connect = ((Server)selection.getFirstElement()).getConnect();
				map.put("SERVER_NAME", ((Server)selection.getFirstElement()).getName());
			}else
			{
				map.put("USER", "");
				map.put("PASSWORD", "");
				map.put("SERVER_NAME", "");
				return map;
			}
			
			map.put("USER", connect.getUser());
			map.put("PASSWORD", connect.getPassword());
		}
		
		return map;
	}
	
	public boolean connectServer(HashMap<String, String> params)
	{
		ServerConnect connect = null;
		Server server = null;

		if(!treeViewer.getSelection().isEmpty())
		{
			IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
			
			if(selection.getFirstElement() instanceof Server)
			{
				server = (Server)selection.getFirstElement();
				connect = server.getConnect();
			}
			else
			{
				IMessageBox.Error(shell, NBLabel.get(0x0090));
				return true;
			}
			
			if(params.containsKey("SAVE_FLAG") && params.get("SAVE_FLAG").equals("true")) connect.setPassword(params.get("PASSWORD"));
			
			params.put("IP_ADDRESS", connect.getHostAddr());
			params.put("PORT", Integer.toString(connect.getHostPort()));
			params.put("ENCODING", connect.getServerEncoding());
		}
		else if(params.containsKey("SERVER_NAME"))
		{
			String serverName = params.get("SERVER_NAME");

			ArrayList<Model> children = ((Model)treeViewer.getInput()).getChildren();
			for(int i=0; i<children.size(); i++)
			{
				if(serverName.equals(children.get(i).getName()))
				{
					server = (Server)children.get(i);
					connect = ((Server)children.get(i)).getConnect();
				}
			}
			
		}
		else
		{
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			return true;
		}
		
		try{
			Socket socket = new Socket(params.get("IP_ADDRESS"), 
					Integer.parseInt(params.get("PORT")), 
					4096, 
					30);

			InstanceHeaderFields fields = new InstanceHeaderFields();
			fields.put("LENGTH", new TelegramFieldContext("LENGTH", "LENGTH", 0, 4, true, ' ', ' ', 'I', ""));
			fields.put("MSG_TYPE", new TelegramFieldContext("MSG_TYPE", "MSG_TYPE", 1, 4, true, ' ', ' ', 'I', ""));
			fields.put("RETURN", new TelegramFieldContext("RETURN", "RETURN", 2, 4, true, ' ', ' ', 'I', ""));
			fields.setLengthFieldIndex("LENGTH");
			fields.setMsgTypeFieldIndex("MSG_TYPE");
			fields.setReturnFieldIndex("RETURN");
			
			IPCProtocol protocol = new IPCProtocol(socket, params.get("USER"), params.get("PASSWORD"), fields, params.get("ENCODING"));
			connect.setProtocol(protocol);

			NBFields netFields = new NBFields();
			netFields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.instance.InstanceConfig");
			netFields.put("CMD_CODE", "L");

			netFields = protocol.execute(netFields);

			if(protocol.getUserAuthority().equals("Admin"))
			{
				new ServerConfig(server, NBLabel.get(0x0049), Activator.getImageDescriptor("/icons/config_obj.gif").createImage(display));
				new UserList(server, NBLabel.get(0x009D), Activator.getImageDescriptor("/icons/user_view.gif").createImage(display));
				new InstanceList(server, NBLabel.get(0x0093), Activator.getImageDescriptor("/icons/pview.gif").createImage(display));
				terminateAction.setEnabled(true);
			}
			else if(protocol.getUserAuthority().equals("Operator"))
			{
				new UserList(server, NBLabel.get(0x009D), Activator.getImageDescriptor("/icons/user_view.gif").createImage(display));
				new InstanceList(server, NBLabel.get(0x0093), Activator.getImageDescriptor("/icons/pview.gif").createImage(display));
			}
			else if(protocol.getUserAuthority().equals("Developer"))
			{
				new InstanceList(server, NBLabel.get(0x0093), Activator.getImageDescriptor("/icons/pview.gif").createImage(display));				
			}
			
			
			server.setImage(Activator.getImageDescriptor("/icons/conserver.gif").createImage(display));
			treeViewer.refresh();
			
			connectAction.setEnabled(false);
			disconnectAction.setEnabled(true);
			
			treeViewer.expandToLevel(server, 1);
			
			openInstanceView(server.getName(), server.getInstance(), netFields, server);
			
		} catch (NumberFormatException e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return true;
		} catch (NetException e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return false;
		} catch (SocketClosedException e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return true;
		} catch (ClassNotFoundException e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return true;
		} catch (TimeoutException e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return false;
		} catch (UnsupportedEncodingException e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return true;
		} catch (NoSuchAlgorithmException e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return true;
		} catch (DataTypeException e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return true;
		} catch (ProtocolException e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return true;
		} catch (Exception e) {
			try {
				if(connect.getProtocol() != null)
				connect.getProtocol()._close();
			} catch (IOException e1) {
			}
			IMessageBox.Error(shell, e.getMessage());
			return true;
		}
		return true;
	}
	
	public void modifyServer(HashMap<String, String> params)
	{
		Model root = (Model) treeViewer.getInput();
		String serverID = params.get("SERVER_NAME");
		ServerConnect serverConnect = null;
		for(int i=0; i<root.getChildren().size(); i++)
		{
			Server server = (Server)root.getChildren().get(i);
			if(server.getName().equals(serverID))
			{
				serverConnect = server.getConnect();
				serverConnect.setChannel(params.get("IP_MONADDRESS"));
				serverConnect.setChannelPort(Integer.parseInt(params.get("MONPORT").equals("")?"0":params.get("MONPORT")));
				serverConnect.setHostAddr(params.get("IP_ADDRESS"));
				serverConnect.setHostPort(Integer.parseInt(params.get("PORT").equals("")?"0":params.get("PORT")));
				serverConnect.setServerEncoding(params.get("ENCODING"));
				serverConnect.setUser(params.get("USER"));
				
				if(!params.get("SAVE_FLAG").equals(""))
					serverConnect.setPassword(params.get("PASSWORD"));
			}
		}
		
		if(params.containsKey("EVENT") && params.get("EVENT").equals("CONNECT"))
		{
			if(serverConnect.getProtocol() != null && serverConnect.getProtocol().isConnected())
			{
				disconnectServer(false);
			}

			connectServer(params);
		}
	}
	
	public void addServer(HashMap<String, String> params)
	{
		Model root = (Model) treeViewer.getInput();
		Server server = new Server(root, params.get("SERVER_NAME"), Activator.getImageDescriptor("/icons/disconserver.gif").createImage(display));
		ServerConnect serverConnect = new ServerConnect(NBLabel.get(0x0099)  + " : " + NBLabel.get(0x0098), Activator.getImageDescriptor("/icons/net_connect.gif").createImage(display));
		
		serverConnect.setChannel(params.get("IP_MONADDRESS"));
		serverConnect.setChannelPort(Integer.parseInt(params.get("MONPORT").equals("")?"0":params.get("MONPORT")));
		serverConnect.setHostAddr(params.get("IP_ADDRESS"));
		serverConnect.setHostPort(Integer.parseInt(params.get("PORT").equals("")?"0":params.get("PORT")));
		serverConnect.setServerEncoding(params.get("ENCODING"));
		serverConnect.setUser(params.get("USER"));
		
		server.setConnection(serverConnect);
		
		if(!params.get("SAVE_FLAG").equals(""))
			serverConnect.setPassword(params.get("PASSWORD"));
		
		
		treeViewer.insert(root, server, -1);
		treeViewer.refresh();
	}
	
	public void deleteServer(Object server)
	{
		String id = ((Server)server).getName();
		Server serverIns = (Server)server;
		DOMConfigurator config = null;
		try {
			Bundle bundle = Platform.getBundle(Application.PLUGIN_ID);
			URL fileURL = FileLocator.toFileURL(BundleUtility.find(bundle, "configuration/nabee.xml"));
			
			config = new DOMConfigurator(fileURL.getPath());
			
			config.deleteConf("monitor/server", id);
			
			if(serverIns.getConnect().isConnect())
				serverIns.getConnect().getProtocol()._close();
			
		} catch (ParserConfigurationException e) {
			IMessageBox.Error(shell, NBLabel.get(0x008F));
			return;
		} catch (TransformerException e) {
			IMessageBox.Error(shell, NBLabel.get(0x008F));
			return;
		} catch (IOException e) {
			IMessageBox.Error(shell, NBLabel.get(0x008F));
			return;
		} catch (SAXException e) {
			IMessageBox.Error(shell, NBLabel.get(0x008F));
			return;
		} catch (Exception e) {
			IMessageBox.Error(shell, NBLabel.get(0x008F));
			return;
		}

		serverIns.getParent().removeChild(id);
		treeViewer.refresh();
	}

	private ISelectionChangedListener genSelectChangedListener()
	{
		selectChangedListener = new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event) {
				
				ISelection incoming = event.getSelection();
				
				if(incoming instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection)incoming;
					
					
					if(selection.size() == 1)
					{
						if(selection.getFirstElement() instanceof Server)
						{
							connectAction.setEnabled(!((Server)selection.getFirstElement()).getConnect().isConnect());
							disconnectAction.setEnabled(((Server)selection.getFirstElement()).getConnect().isConnect());
							refreshAction.setEnabled(((Server)selection.getFirstElement()).getConnect().isConnect());
						}else
						{
							connectAction.setEnabled(false);
							disconnectAction.setEnabled(false);
							refreshAction.setEnabled(false);
						}
						
						deleteAction.setEnabled(selection.getFirstElement() instanceof Server);
						renameAction.setEnabled(selection.getFirstElement() instanceof Server);
					}
				}
				else
				{
					deleteAction.setEnabled(false);
				}
			}
			
		};

		return selectChangedListener;
	}

	private IDoubleClickListener genDbClickListener()
	{
		dbcListener = new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event) {
				if(event.getSelection() instanceof IStructuredSelection) 
				{
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					Object domain = (Model) selection.iterator().next();
					if(treeViewer.getExpandedState(domain))
					{
						treeViewer.collapseToLevel(domain, 1);
					}
					else
					{
						treeViewer.expandToLevel(domain, 1);
					}
					
					if(selection.getFirstElement() instanceof InstanceList)
					{
						openInstanceView(((InstanceList)selection.getFirstElement()).getParent().getName(), 
								((InstanceList)selection.getFirstElement()), null, 
								(Server)((InstanceList)selection.getFirstElement()).getParent());
					}
					else if(selection.getFirstElement() instanceof ServerConfig)
					{
						try {
							NBFields fields = getServerConfigInfo(((Server)((ServerConfig)selection.getFirstElement()).getParent()).getConnect().getProtocol());
							if(fields == null) return;
							NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(new ServerConfigEditorInput(((ServerConfig)selection.getFirstElement()).getParent().getName(), 
																						((ServerConfig)selection.getFirstElement()).getName(),
																						((Server)((ServerConfig)selection.getFirstElement()).getParent()).getConnect().getProtocol(),
																						fields),
																						ServerConfigEditor.ID);
							editor.setServer((Server)((ServerConfig)selection.getFirstElement()).getParent());
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
						
					}
					else if(selection.getFirstElement() instanceof UserList)
					{
						try {
							NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(new UserListEditorInput(((UserList)selection.getFirstElement()).getParent().getName(),
																					((UserList)selection.getFirstElement()).getName(),
																					((Server)((UserList)selection.getFirstElement()).getParent()).getConnect().getProtocol()), UserListEditor.ID);
							editor.setServer((Server)((UserList)selection.getFirstElement()).getParent());
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
					}
				}
			}
		};
		
		return dbcListener;
	}
	
	public void dispose() {
		treeViewer.removeDoubleClickListener(dbcListener);
		treeViewer.removeSelectionChangedListener(selectChangedListener);
		
		super.dispose();
	}
	
	private void addRetargetableAction(Composite parent)
	{
		deleteAction = new Action(){
			public void run(){

				ISelection incomming = treeViewer.getSelection();
				if(incomming instanceof IStructuredSelection)
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) != SWT.OK) return;
					
					IStructuredSelection selection = (IStructuredSelection)incomming;
					deleteServer((Server)selection.getFirstElement());
				}
			}
		};
		
		refreshAction = new Action() {
			public void run(){
				Server server = null;
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				
				if(selection.getFirstElement() instanceof Server)
				{
					server = (Server)selection.getFirstElement();
				}
				else
				{
					IMessageBox.Error(shell, NBLabel.get(0x0091));
					return;
				}
				
				server.setImage(Activator.getImageDescriptor("/icons/disconserver.gif").createImage(display));
				
				server.removeConfig();
				server.removeUserList();
				server.removeInstance();
				treeViewer.refresh();
			
				connectAction.setEnabled(true);
				disconnectAction.setEnabled(false);
				refreshAction.setEnabled(false);
				terminateAction.setEnabled(false);
				
				closeInstanceView(server.getName());
				
				IPCProtocol protocol = ((ServerConnect)((Server)selection.getFirstElement()).getConnect()).getProtocol();
				
				NBFields netFields = new NBFields();
				netFields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.instance.InstanceConfig");
				netFields.put("CMD_CODE", "L");

				try {
					netFields = protocol.execute(netFields);
					
					if(protocol.getUserAuthority().equals("Admin"))
					{
						new ServerConfig(server, NBLabel.get(0x0049), Activator.getImageDescriptor("/icons/config_obj.gif").createImage(display));
						new UserList(server, NBLabel.get(0x009D), Activator.getImageDescriptor("/icons/user_view.gif").createImage(display));
						new InstanceList(server, NBLabel.get(0x0093), Activator.getImageDescriptor("/icons/pview.gif").createImage(display));
						terminateAction.setEnabled(true);
					}
					else if(protocol.getUserAuthority().equals("Operator"))
					{
						new UserList(server, NBLabel.get(0x009D), Activator.getImageDescriptor("/icons/user_view.gif").createImage(display));
						new InstanceList(server, NBLabel.get(0x0093), Activator.getImageDescriptor("/icons/pview.gif").createImage(display));
					}
					else if(protocol.getUserAuthority().equals("Developer"))
					{
						new InstanceList(server, NBLabel.get(0x0093), Activator.getImageDescriptor("/icons/pview.gif").createImage(display));				
					}
					
					
					server.setImage(Activator.getImageDescriptor("/icons/conserver.gif").createImage(display));
					treeViewer.refresh();
					
					connectAction.setEnabled(false);
					disconnectAction.setEnabled(true);
					
					treeViewer.expandToLevel(server, 1);
					
					openInstanceView(server.getName(), server.getInstance(), netFields, server);
				} catch (SocketClosedException e) {
					IMessageBox.Error(shell, NBLabel.get(0x0090));
					closeConnection();
				} catch (TimeoutException e) {
					IMessageBox.Error(shell, e.getMessage());
				} catch (NetException e) {
					IMessageBox.Error(shell, NBLabel.get(0x0090));
					closeConnection();
				} catch (UnsupportedEncodingException e) {
					IMessageBox.Error(shell, NBLabel.get(0x0090));
					closeConnection();
				} catch (NoSuchAlgorithmException e) {
					IMessageBox.Error(shell, NBLabel.get(0x0090));
					closeConnection();
				} catch (DataTypeException e) {
					IMessageBox.Error(shell, NBLabel.get(0x0090));
					closeConnection();
				} catch (ProtocolException e) {
					IMessageBox.Error(shell, NBLabel.get(0x0090));
					closeConnection();
				}
			}
		};
		
		renameAction = new Action() {
			public void run(){
				final HashMap<String, String> params = new HashMap<String, String>();
				SimpleInputBox inputBox = new SimpleInputBox(shell, params);
				inputBox.setTitle(NBLabel.get(0x009A));
				inputBox.setImage("/icons/topic.gif");
				inputBox.setLabel(NBLabel.get(0x009B));
				inputBox.open(getSite().getWorkbenchWindow(), false);
				
				if(params.containsKey("EVENT") && params.get("EVENT").equals("CANCEL"))
				{
					return;
				}
				
				ISelection incomming = treeViewer.getSelection();
				if(incomming instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection)incomming;
					Server server = (Server)selection.getFirstElement();
					DOMConfigurator config = null;
					try {
						Bundle bundle = Platform.getBundle(Application.PLUGIN_ID);
						URL fileURL = FileLocator.toFileURL(BundleUtility.find(bundle, "configuration/nabee.xml"));
						
						config = new DOMConfigurator(fileURL.getPath());
						
						config.modifyConf("monitor/server", server.getName(), params.get("TEXT"));
						
						if(server.getConnect().isConnect())
						{
							InstanceView instanceView = ((InstanceView)getSite().getWorkbenchWindow().getActivePage().findView(InstanceView.ID));
							if(instanceView.getPartProperty("SERVER_NAME").equals(server.getName()))
							{
								instanceView.serverNameChanged(params.get("TEXT"));
							}
						}
						
						
						server.setName(params.get("TEXT"));
						treeViewer.refresh();
						
					} catch (ParserConfigurationException e) {
						IMessageBox.Error(shell, NBLabel.get(0x008F));
						return;
					} catch (TransformerException e) {
						IMessageBox.Error(shell, NBLabel.get(0x008F));
						return;
					} catch (IOException e) {
						IMessageBox.Error(shell, NBLabel.get(0x008F));
						return;
					} catch (SAXException e) {
						IMessageBox.Error(shell, NBLabel.get(0x008F));
						return;
					} catch (Exception e) {
						IMessageBox.Error(shell, NBLabel.get(0x008F));
						return;
					}
					
					
				}
			}
		};
		
		terminateAction = new TerminateAction(getSite().getWorkbenchWindow()){
			public void run(){

				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();

				if(selection.getFirstElement() instanceof Server)
				{
					Server server = (Server) selection.getFirstElement();
					
					if(IMessageBox.Confirm(shell, "[" + server.getName() + "] " + NBLabel.get(0x022B)) != SWT.OK) return;
					
					IPCProtocol protocol = ((ServerConnect)((Server)selection.getFirstElement()).getConnect()).getProtocol();
					boolean rtn = controlServer(IPC.CMD_SHUTDOWN, protocol);

					if(rtn)
					{
						disconnectServer(false);
						setEnabled(false);
					}
					
				}
			}
			
			public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
				if(incoming instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection)incoming;
					
					if(selection.getFirstElement() != null && selection.getFirstElement() instanceof Server)
					{
						IPCProtocol protocol = ((Server)selection.getFirstElement()).getConnect().getProtocol();
						if(selection.size() == 1 && selection.getFirstElement() instanceof Server && protocol != null)
						{
							setEnabled(protocol.getUserAuthority().equals("Admin") && protocol.isConnected());
						}
						else
						{
							setEnabled(false);
						}
					}
					else
					{
						setEnabled(false);
					}
				}
				else
				{
					setEnabled(false);
				}
			}
		};
		
		connectAction = new ConnectAction(getSite().getWorkbenchWindow());
		disconnectAction = new DisconnectAction(getSite().getWorkbenchWindow());
		
		IActionBars actionBars = getViewSite().getActionBars();
	
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), renameAction);
		actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAction);
		actionBars.setGlobalActionHandler("connect", connectAction);
		actionBars.setGlobalActionHandler("disconnect", disconnectAction);
		actionBars.setGlobalActionHandler(ResourceFactory.TERMINATE.getId(), terminateAction);
	}
	
	private boolean controlServer(int messageType, IPCProtocol protocol)
	{
		NBFields fields = new NBFields();

		fields.put(IPC.NB_MSG_TYPE, messageType);
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.server.ControlServer");
		
		try {

			fields = protocol.execute(fields);

			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return false;
			}
			else
			{
				return true;
			}
		} catch (SocketClosedException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			return false;
		} catch (TimeoutException e) {
			IMessageBox.Error(shell, e.getMessage());
			return false;
		} catch (NetException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			return false;
		} catch (UnsupportedEncodingException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			return false;
		} catch (NoSuchAlgorithmException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			return false;
		} catch (DataTypeException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			return false;
		} catch (ProtocolException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			return false;
		}
	}
	
	
	private void registContextMenu(Composite parent)
	{
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		
		IWorkbenchAction renameAction = ActionFactory.RENAME.create(window);
		renameAction.setToolTipText(NBLabel.get(0x006B));
		renameAction.setText(NBLabel.get(0x006E));
		
		IWorkbenchAction refreshAction = ActionFactory.REFRESH.create(window);
		refreshAction.setToolTipText(NBLabel.get(0x025F));
		refreshAction.setText(NBLabel.get(0x0260));
		
		IWorkbenchAction deleteAction = ActionFactory.DELETE.create(window);
		deleteAction.setToolTipText(NBLabel.get(0x0062));
		deleteAction.setText(NBLabel.get(0x0068));
		
		
		MenuManager newMgr = new MenuManager("&New                    ", "file.mnuNew");
		newMgr.add(new NewServerAction(window));
		newMgr.add(new Separator());
		newMgr.add(new GroupMarker("file.new.OTHERS"));
		
		MenuManager mgr = new MenuManager();
		mgr.add(newMgr);
		mgr.add(new Separator());
		mgr.add(ResourceFactory.CONNECT.create(window));
		mgr.add(ResourceFactory.DISCONNECT.create(window));
		mgr.add(new Separator());
		mgr.add(refreshAction);
		mgr.add(new Separator());
		mgr.add(deleteAction);
		mgr.add(renameAction);
		mgr.add(new Separator());
		mgr.add(new PropertyAction(window));
		
		Menu menu = mgr.createContextMenu(parent);
		treeViewer.getTree().setMenu(menu);
	}

	@Override
	public void setFocus() {
	}
	
	private NBFields getServerConfigInfo(IPCProtocol protocol)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.server.ServerConfig");
		fields.put("CMD_CODE", "R");
		
		try {
			fields = protocol.execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
			}
		} catch (SocketClosedException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			fields = null;
		} catch (TimeoutException e) {
			IMessageBox.Error(shell, e.getMessage());
			fields = null;
		} catch (NetException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			fields = null;
		} catch (UnsupportedEncodingException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			fields = null;
		} catch (NoSuchAlgorithmException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			fields = null;
		} catch (DataTypeException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			fields = null;
		} catch (ProtocolException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
			fields = null;
		}
		
		return fields;
	}
}
