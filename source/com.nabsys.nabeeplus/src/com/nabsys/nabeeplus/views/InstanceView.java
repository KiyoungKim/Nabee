package com.nabsys.nabeeplus.views;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.actions.NewInstanceAction;
import com.nabsys.nabeeplus.actions.NewSQLDocument;
import com.nabsys.nabeeplus.actions.NewSQLFolder;
import com.nabsys.nabeeplus.actions.PropertyAction;
import com.nabsys.nabeeplus.actions.ResumeAction;
import com.nabsys.nabeeplus.actions.RunAction;
import com.nabsys.nabeeplus.actions.SearchAction;
import com.nabsys.nabeeplus.actions.TerminateAction;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.ResourceFactory;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.common.paramdata.ParameterFileControler;
import com.nabsys.nabeeplus.common.paramdata.ParameterMap;
import com.nabsys.nabeeplus.editors.ComponentEditor;
import com.nabsys.nabeeplus.editors.InstanceConfigEditor;
import com.nabsys.nabeeplus.editors.NabeeEditor;
import com.nabsys.nabeeplus.editors.ServiceDesigner;
import com.nabsys.nabeeplus.editors.SqlEditor;
import com.nabsys.nabeeplus.editors.TelegramConfigEditor;
import com.nabsys.nabeeplus.editors.TransactionConfigEditor;
import com.nabsys.nabeeplus.editors.input.ComponentEditorInput;
import com.nabsys.nabeeplus.editors.input.InstanceConfigEditorInput;
import com.nabsys.nabeeplus.editors.input.ServiceDesignerInput;
import com.nabsys.nabeeplus.editors.input.SqlEditorInput;
import com.nabsys.nabeeplus.editors.input.TelegramConfigEditorInput;
import com.nabsys.nabeeplus.editors.input.TransactionConfigEditorInput;
import com.nabsys.nabeeplus.views.model.ComponentConfig;
import com.nabsys.nabeeplus.views.model.Instance;
import com.nabsys.nabeeplus.views.model.InstanceConfig;
import com.nabsys.nabeeplus.views.model.Model;
import com.nabsys.nabeeplus.views.model.NBContentProvider;
import com.nabsys.nabeeplus.views.model.NBLabelProvider;
import com.nabsys.nabeeplus.views.model.QueryStorageList;
import com.nabsys.nabeeplus.views.model.ServiceConfig;
import com.nabsys.nabeeplus.views.model.SqlDocument;
import com.nabsys.nabeeplus.views.model.SqlFolder;
import com.nabsys.nabeeplus.views.model.TelegramConfig;
import com.nabsys.nabeeplus.views.model.TransactionConfig;
import com.nabsys.nabeeplus.widgets.window.InstanceListSelect;
import com.nabsys.nabeeplus.widgets.window.SearchWindow;
import com.nabsys.nabeeplus.widgets.window.SimpleInputBox;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public class InstanceView extends NabeeView{
	
	public final static String ID = "com.nabsys.nabeeplus.views.instanceView";
	
				
	private TreeViewer 							treeViewer 				= null;
	private Display 							display 				= null;
	private Shell								shell					= null;
	private Composite 							parent 					= null;
	private IPCProtocol 						protocol 				= null;
	
	private IAction		 						deleteAction 			= null;
	private TerminateAction 					terminateAction 		= null;
	private ResumeAction 						resumeAction 			= null;
	private RunAction 							runAction 				= null;
	private NewSQLFolder						newSQLFolderAction		= null;
	private NewSQLDocument		 				newSQLDocumentAction	= null;
	private NewInstanceAction					newInstanceAction		= null;
	private IAction								newServiceAction		= null;
	private IAction								renameAction			= null;
	private SearchAction						searchAction			= null;

	private HashMap<String, Model> 				mMap 					= new HashMap<String, Model>();
	private HashMap<String, TreePath[]>			expMap					= new HashMap<String, TreePath[]>();
	
	private IDoubleClickListener				dbcListener 			= null;
	private ISelectionChangedListener 			selectChangedListener 	= null;
	
	
	private final int DOCUMENT = 0;
	private final int FOLDER = 1;
	
	public InstanceView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		this.shell = parent.getShell();
		
		setPartName(NBLabel.get(0x0093));
		this.display = parent.getDisplay();
		
		treeViewer = new TreeViewer(parent, SWT.NONE);
		treeViewer.setContentProvider(new NBContentProvider(display, true));
		treeViewer.setLabelProvider(new NBLabelProvider(parent.getDisplay()));
		treeViewer.setUseHashlookup(true);
		treeViewer.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {

		    	if (o1 instanceof QueryStorageList && o2 instanceof QueryStorageList)
		        {
		    		if(o1 instanceof SqlFolder && o2 instanceof SqlDocument) return -1;
		    		else if(o1 instanceof SqlDocument && o2 instanceof SqlFolder) return 1;
		    		else return ((QueryStorageList)o1).getName().compareToIgnoreCase(((QueryStorageList)o2).getName());
		        }
		        else
		        {
		        	return 0;
		        }
		    }
		});
		
		getSite().setSelectionProvider(treeViewer);
		
		registContextMenu(parent);
		addRetargetableAction(parent);
		
		treeViewer.addDoubleClickListener(genDbClickListener());
		treeViewer.addSelectionChangedListener(genSelectChangedListener());
	}
	
	public void initInstance(String serverName)
	{
		mMap.remove(serverName);
		expMap.remove(serverName);
		
		if(serverName.equals(getPartProperty("SERVER_NAME")))
		{
			treeViewer.setInput(new Model());
			protocol = null;
			setPartName(NBLabel.get(0x0093));
		}
	}
	
	public void serverNameChanged(String newName)
	{
		String oldName = getPartProperty("SERVER_NAME");
		mMap.put(newName, mMap.get(oldName));
		expMap.put(newName, expMap.get(oldName));
		setPartName(newName);
		setPartProperty("SERVER_NAME", newName);
	}
	
	public void addInstance(String name)
	{
		Model root = (Model)treeViewer.getInput();
		for(int i=0; i<root.getChildren().size(); i++)
		{
			if(root.getChildren().get(i).getName().equals(name))
			{
				IMessageBox.Error(shell, NBLabel.get(0x0024));
				return;
			}
		}
		
		NBFields sendMsg = new NBFields();
		sendMsg.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.instance.InstanceConfig");
		sendMsg.put("CMD_CODE"			, "I");
		sendMsg.put(IPC.NB_INSTNCE_ID	, name);
		
		try {
			NBFields fields = protocol.execute(sendMsg);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
			}
			
			Instance instance = new Instance(root, name, Activator.getImageDescriptor("/icons/pview_con.gif").createImage(display)
					, Activator.getImageDescriptor("/icons/pview_discon.gif").createImage(display));
			if(instance.hasAuthority())
				new InstanceConfig(instance, NBLabel.get(0x009F), Activator.getImageDescriptor("/icons/config_obj.gif").createImage(display));
			new TelegramConfig(instance, NBLabel.get(0x0102), Activator.getImageDescriptor("/icons/message_storage.gif").createImage(display));
			new ComponentConfig(instance, NBLabel.get(0x0163), Activator.getImageDescriptor("/icons/component.gif").createImage(display));
			new ServiceConfig(instance, NBLabel.get(0x0104), Activator.getImageDescriptor("/icons/service_obj.gif").createImage(display));
			new QueryStorageList(instance, NBLabel.get(0x0105), Activator.getImageDescriptor("/icons/repo_rep.gif").createImage(display));
			
			treeViewer.insert(root, instance, -1);
			treeViewer.refresh();
			
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
	
	public void setInstance(String serverName, ArrayList<String> instanceList, ArrayList<Boolean> runStateList)
	{
		if(getPartProperty("SERVER_NAME") != null)
		{
			expMap.put(getPartProperty("SERVER_NAME"), treeViewer.getExpandedTreePaths());
		}
		
		if(mMap.containsKey(serverName))
		{
			treeViewer.setInput(mMap.get(serverName));
			treeViewer.setExpandedTreePaths(expMap.get(serverName));
		}
		else
		{
			Model root = new Model();
			
			for(int i=0; i<instanceList.size(); i++)
			{
				Instance instance = new Instance(root, (String)instanceList.get(i), Activator.getImageDescriptor("/icons/pview_con.gif").createImage(display)
										, Activator.getImageDescriptor("/icons/pview_discon.gif").createImage(display));
				
				instance.setRunning(runStateList.get(i));
				instance.setAuthority(!protocol.getUserAuthority().equals("Developer"));
				
				if(instance.hasAuthority())
					new InstanceConfig(instance, NBLabel.get(0x009F), Activator.getImageDescriptor("/icons/config_obj.gif").createImage(display));
				new TelegramConfig(instance, NBLabel.get(0x0102), Activator.getImageDescriptor("/icons/message_storage.gif").createImage(display));
				new ComponentConfig(instance, NBLabel.get(0x0163), Activator.getImageDescriptor("/icons/component.gif").createImage(display));
				new ServiceConfig(instance, NBLabel.get(0x0104), Activator.getImageDescriptor("/icons/service_obj.gif").createImage(display));
				new QueryStorageList(instance, NBLabel.get(0x0105), Activator.getImageDescriptor("/icons/repo_rep.gif").createImage(display));
			}
			
			treeViewer.setInput(root);
			mMap.put(serverName, root);
		}
		
		parent.forceFocus();
	}
	
	private void deleteInstance(String name, Instance instance)
	{
		if(instance.isRunning())
		{
			if(IMessageBox.Confirm(shell, "[" + instance.getName() + "] " + NBLabel.get(0x0246)) != SWT.OK)
			{
				return;
			}
			boolean rtn = controlInstance(instance.getName(), "com.nabsys.management.instance.TerminateInstance");
			
			if(!rtn)
			{
				IMessageBox.Error(shell, NBLabel.get(0x0043));
				return;
			}
		}
		else
		{
			controlInstance(instance.getName(), "com.nabsys.management.instance.TerminateInstance");
		}
		
		Model root = (Model)treeViewer.getInput();
		
		NBFields sendMsg = new NBFields();
		sendMsg.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.instance.InstanceConfig");
		sendMsg.put("CMD_CODE"			, "D");
		sendMsg.put(IPC.NB_INSTNCE_ID	, name);
		
		try {
			NBFields fields = protocol.execute(sendMsg);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
			}
			
			root.getChildren().remove(instance);
			treeViewer.refresh();
			
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
	
	private void addRetargetableAction(Composite parent)
	{
		deleteAction = new Action() {
			public void run(){

				ISelection incoming = treeViewer.getSelection();
				if(incoming instanceof IStructuredSelection)
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) != SWT.OK) return;
					
					IStructuredSelection selection = (IStructuredSelection)incoming;
					
					if(selection.getFirstElement() instanceof Instance)
					{
						deleteInstance(((Instance)selection.getFirstElement()).getName(), (Instance)selection.getFirstElement());
					}
					else if(selection.getFirstElement() instanceof SqlFolder || selection.getFirstElement() instanceof SqlDocument)
					{
						deleteSQLObject((QueryStorageList)selection.getFirstElement());
					}
				}
			}
		};
		

		
		runAction = new RunAction(getSite().getWorkbenchWindow(), new Instance()){
			public void run(){
				
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				
				if(selection.getFirstElement() instanceof Instance)
				{
					
					Instance instance = (Instance) selection.getFirstElement();
					
					boolean rtn = controlInstance(instance.getName(), "com.nabsys.management.instance.LoadInstance");
					if(rtn)
					{
						instance.setRunning(true);
						treeViewer.refresh();
						setEnabled(false);
						resumeAction.setEnabled(true);
						terminateAction.setEnabled(true);
					}
				}
			}
		};
		
		resumeAction = new ResumeAction(getSite().getWorkbenchWindow(), new Instance()){
			public void run(){

				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();

				if(selection.getFirstElement() instanceof Instance)
				{
					Instance instance = (Instance) selection.getFirstElement();
					if(IMessageBox.Confirm(shell, "[" + instance.getName() + "] " + NBLabel.get(0x0229)) != SWT.OK) return;
					
					boolean rtn = controlInstance(instance.getName(), "com.nabsys.management.instance.TerminateInstance");
					
					if(rtn)
					{
						instance.setRunning(false);
						treeViewer.refresh();
						setEnabled(false);
						terminateAction.setEnabled(false);
						runAction.setEnabled(true);
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					rtn = controlInstance(instance.getName(), "com.nabsys.management.instance.LoadInstance");
					if(rtn)
					{
						instance.setRunning(true);
						treeViewer.refresh();
						setEnabled(true);
						terminateAction.setEnabled(true);
						runAction.setEnabled(false);
					}
					
					
				}
			}
		};
		
		terminateAction = new TerminateAction(getSite().getWorkbenchWindow()){
			public void run(){

				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();

				if(selection.getFirstElement() instanceof Instance)
				{
					Instance instance = (Instance) selection.getFirstElement();
					
					if(IMessageBox.Confirm(shell, "[" + instance.getName() + "] " + NBLabel.get(0x022A)) != SWT.OK) return;
					
					boolean rtn = controlInstance(instance.getName(), "com.nabsys.management.instance.TerminateInstance");

					if(rtn)
					{
						instance.setRunning(false);
						treeViewer.refresh();
						setEnabled(false);
						resumeAction.setEnabled(false);
						runAction.setEnabled(true);
					}
				}
			}
		};
		
		newSQLFolderAction = new NewSQLFolder(getSite().getWorkbenchWindow()){
			public void run(){

				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();

				if(selection.getFirstElement() instanceof QueryStorageList)
				{
					if(selection.getFirstElement() instanceof SqlDocument) return;
					createNewSQLObject((QueryStorageList)selection.getFirstElement(), FOLDER);
				}
			}
		};
		
		newSQLDocumentAction = new NewSQLDocument(getSite().getWorkbenchWindow()){
			public void run(){

				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();

				if(selection.getFirstElement() instanceof QueryStorageList)
				{
					if(selection.getFirstElement() instanceof SqlDocument) return;
					createNewSQLObject((QueryStorageList)selection.getFirstElement(), DOCUMENT);
				}
			}
		};
		
		renameAction = new Action() {
			public void run(){
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();

				if(selection.getFirstElement() instanceof QueryStorageList)
				{
					if(selection.getFirstElement() instanceof SqlDocument) 
						renameSQLObject((QueryStorageList)selection.getFirstElement(), DOCUMENT);
					else if(selection.getFirstElement() instanceof SqlFolder) 
						renameSQLObject((QueryStorageList)selection.getFirstElement(), FOLDER);
				}
			}
		};
		
		newServiceAction = new Action(){
			public void run(){
				Model root = (Model)treeViewer.getInput();
				HashMap<String, Object> param = new HashMap<String, Object>();
				ArrayList<String> list = new ArrayList<String>();
				for(int i=0; i<root.getChildren().size(); i++)
				{
					list.add(root.getChildren().get(i).getName());
				}
				param.put("LIST", list);

				InstanceListSelect instanceList = new InstanceListSelect(shell);
				instanceList.initMap(param);
				param = instanceList.open(getSite().getWorkbenchWindow(), Activator.getImageDescriptor("/icons/pview_con.gif").createImage(display));
				if(param.containsKey("RTN") && ((String)param.get("RTN")).equals("OK"))
				{
					ServiceDesignerInput input = new ServiceDesignerInput((String)param.get("INSTANCE"), "New Service", protocol, null);
					try {
						NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(input, ServiceDesigner.ID);
						editor.setServer(getServer());
					} catch (PartInitException e) {
						IMessageBox.Error(shell, e.getMessage());
					}
				}
			}
		};
		newServiceAction.setEnabled(false);
		
		searchAction = new SearchAction(getSite().getWorkbenchWindow()) {
			public void run(){
				SearchWindow searchWindow = new SearchWindow(shell);
				
				searchWindow.setTitle("Search");
				searchWindow.setImage("icons/search_src.gif");
				HashMap<String, String> searchParam = searchWindow.open(getSite().getWorkbenchWindow());
				if(searchParam.get("EVENT").equals("CANCEL")) return;
				
				searchSql(searchParam, selectObject);
			}
		};
		
		newInstanceAction = new NewInstanceAction(getSite().getWorkbenchWindow());
		
		IActionBars actionBars = getViewSite().getActionBars();
	
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId()			, deleteAction);
		actionBars.setGlobalActionHandler(ResourceFactory.RUN.getId()			, runAction);
		actionBars.setGlobalActionHandler(ResourceFactory.RESUME.getId()		, resumeAction);
		actionBars.setGlobalActionHandler(ResourceFactory.TERMINATE.getId()		, terminateAction);
		actionBars.setGlobalActionHandler(ResourceFactory.NEW_INSTANCE.getId()	, newInstanceAction);
		actionBars.setGlobalActionHandler(ResourceFactory.NEW_SQL_DIR.getId()	, newSQLFolderAction);
		actionBars.setGlobalActionHandler(ResourceFactory.NEW_SQL_DOC.getId()	, newSQLDocumentAction);
		actionBars.setGlobalActionHandler(ResourceFactory.SEARCH.getId()		, searchAction);
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId()			, renameAction);
		actionBars.setGlobalActionHandler(ResourceFactory.NEW_SERVICE.getId()	, newServiceAction);
	}
	
	private String getPath(QueryStorageList parent)
	{
		String path = parent.getPath().replace("/", ".");
		
		String[] pathArray = path.split("\\.");
		
		if(pathArray.length < 4)
		{
			path = "";
		}
		else
		{
			path = "";
			for(int i=3; i<pathArray.length; i++)
			{
				path += pathArray[i];
				if(i != pathArray.length - 1) path += ".";
			}
		}
		
		return path;
	}
	
	private String getInstanceName(QueryStorageList parent)
	{
		String path = parent.getPath().replace("/", ".");
		String[] pathArray = path.split("\\.");
		return pathArray[1];
	}
	
	private void renameSQLObject(QueryStorageList sqlObject, int type)
	{
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		String objectName = sqlObject.getName();
		HashMap<String, String> params = new HashMap<String, String>();
		SimpleInputBox newSQLObject = new SimpleInputBox(shell, params);

		switch(type)
		{
		case FOLDER:
			newSQLObject.setTitle(NBLabel.get(0x009A));
			newSQLObject.setImage("/icons/query_folder.gif");
			newSQLObject.setLabel(NBLabel.get(0x009B));
			break;
		case DOCUMENT:
			newSQLObject.setTitle(NBLabel.get(0x009A));
			newSQLObject.setImage("/icons/sqldoc.gif");
			newSQLObject.setLabel(NBLabel.get(0x009B));
			break;
		}
		newSQLObject.setText(objectName);
		newSQLObject.open(window, false);

		if(params.get("EVENT").equals("CANCEL")) return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.sql.QueryConfig");
		fields.put(IPC.NB_INSTNCE_ID, getInstanceName(sqlObject));
		fields.put("CMD_CODE", "R");
		fields.put("PATH", getPath(sqlObject));
		fields.put("ID", params.get("TEXT"));
		
		try {
			fields = protocol.execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			sqlObject.setName(params.get("TEXT"));
			treeViewer.refresh();
			
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
		} catch (ProtocolException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (DataTypeException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		}
	}
	
	private void deleteSQLObject(QueryStorageList sqlObject)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.sql.QueryConfig");
		fields.put(IPC.NB_INSTNCE_ID, getInstanceName(sqlObject));
		fields.put("CMD_CODE", "D");
		fields.put("PATH", getPath(sqlObject));
		
		try {
			fields = protocol.execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			sqlObject.getParent().getChildren().remove(sqlObject);
			treeViewer.refresh();
			
			ParameterFileControler paramControl = new ParameterFileControler();
			ParameterMap paramMap = paramControl.getObject();
			paramMap.remove(getPath(sqlObject));
			paramControl.save();
			
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
		} catch (ProtocolException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (DataTypeException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}

		
	}
	
	@SuppressWarnings("unchecked")
	private void createNewSQLObject(QueryStorageList parent, int type)
	{
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		
		HashMap<String, String> params = new HashMap<String, String>();
		SimpleInputBox newSQLObject = new SimpleInputBox(shell, params);

		switch(type)
		{
		case FOLDER:
			newSQLObject.setTitle(NBLabel.get(0x022E));
			newSQLObject.setImage("/icons/query_folder.gif");
			newSQLObject.setLabel(NBLabel.get(0x022D));
			break;
		case DOCUMENT:
			newSQLObject.setTitle(NBLabel.get(0x0230));
			newSQLObject.setImage("/icons/sqldoc.gif");
			newSQLObject.setLabel(NBLabel.get(0x022F));
			break;
		}
		
		newSQLObject.open(window, false);
		
		if(params.get("EVENT").equals("CANCEL")) return;
		
		String path = getPath(parent);
		String instanceName = getInstanceName(parent);
		
		NBFields fields = new NBFields();

		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.sql.QueryConfig");
		fields.put(IPC.NB_INSTNCE_ID, instanceName);
		fields.put("CMD_CODE", "N");
		fields.put("TYPE", type);
		fields.put("PATH", path);
		fields.put("ID", params.get("TEXT"));
		
		try {
			fields = protocol.execute(fields);

			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			if(parent.isVisited())
			{
				Model obj = null;
				switch(type)
				{
				case FOLDER:
					obj = new SqlFolder(parent, params.get("TEXT"), Activator.getImageDescriptor("/icons/fldr_obj.gif").createImage(display));
					break;
				case DOCUMENT:
					obj = new SqlDocument(parent, params.get("TEXT"), Activator.getImageDescriptor("/icons/sqldoc.gif").createImage(display));
					NBFields tmpFields = getSqlDocument((SqlDocument)obj);
					try {
						NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(new SqlEditorInput(obj.getName(),
																		protocol,
																		"",
																		(ArrayList<NBFields>)tmpFields.get("PLG_LST"),
																		getPath((SqlDocument)obj), 
																		getInstanceName((SqlDocument)obj))
																		, SqlEditor.ID);
						editor.setServer(getServer());
					} catch (PartInitException e) {
						IMessageBox.Error(shell, e.getMessage());
					}
					break;
				}
				treeViewer.insert(parent, obj, -1);
				treeViewer.refresh();
			}

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
	
	private boolean controlInstance(String instanceName, String actionClass)
	{
		NBFields fields = new NBFields();

		fields.put(IPC.NB_LOAD_CLASS, actionClass);
		fields.put(IPC.NB_INSTNCE_ID, instanceName);
		
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
	
	private IDoubleClickListener genDbClickListener()
	{
		dbcListener = new IDoubleClickListener()
		{
			@SuppressWarnings("unchecked")
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
					
					if(selection.getFirstElement() instanceof InstanceConfig)
					{
						try {
							NBFields fields = getInstanceConfigInfo(protocol, ((InstanceConfig)selection.getFirstElement()).getParent().getName());
							if(fields == null) return;
							NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(new InstanceConfigEditorInput(((InstanceConfig)selection.getFirstElement()).getParent().getName(),
																						((InstanceConfig)selection.getFirstElement()).getName(),
																						protocol,
																						fields), InstanceConfigEditor.ID);
							editor.setServer(getServer());
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
					}
					else if(selection.getFirstElement() instanceof TelegramConfig)
					{
						try {
							NBFields fields = getInstanceList(protocol);
							if(fields == null) return;
							NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(new TelegramConfigEditorInput(((TelegramConfig)selection.getFirstElement()).getParent().getName(),
																						((TelegramConfig)selection.getFirstElement()).getName(),
																						protocol,
																						fields), TelegramConfigEditor.ID);
							editor.setServer(getServer());
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
					}
					else if(selection.getFirstElement() instanceof TransactionConfig)
					{
						try {
							NBFields fields = getInstanceList(protocol);
							if(fields == null) return;
							NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(new TransactionConfigEditorInput(((TransactionConfig)selection.getFirstElement()).getParent().getName(),
																						((TransactionConfig)selection.getFirstElement()).getName(),
																						protocol,
																						fields), TransactionConfigEditor.ID);
							editor.setServer(getServer());
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
					}
					else if(selection.getFirstElement() instanceof ServiceConfig)
					{
						try {
							ServiceList serviceView = (ServiceList)getSite().getWorkbenchWindow().getActivePage().showView(ServiceList.ID);
							serviceView.setServer(getServer());
							serviceView.init(((ServiceConfig)selection.getFirstElement()).getParent().getName(), protocol);
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
					}
					else if(selection.getFirstElement() instanceof ComponentConfig)
					{
						try {
							NBFields fields = getInstanceList(protocol);
							if(fields == null) return;
							NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(new ComponentEditorInput(((ComponentConfig)selection.getFirstElement()).getParent().getName(),
																						((ComponentConfig)selection.getFirstElement()).getName(),
																						protocol,
																						fields), ComponentEditor.ID);
							editor.setServer(getServer());
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
					}
					else if(selection.getFirstElement() instanceof SqlDocument)
					{
						try {
							NBFields fields = getSqlDocument((SqlDocument)selection.getFirstElement());
							if(fields == null) return;
							NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(new SqlEditorInput(((SqlDocument)selection.getFirstElement()).getName(),
																				protocol,
																				(String)fields.get("SQL"),
																				(ArrayList<NBFields>)fields.get("PLG_LST"),
																				getPath((SqlDocument)selection.getFirstElement()), 
																				getInstanceName((SqlDocument)selection.getFirstElement()))
																				, SqlEditor.ID);
							editor.setServer(getServer());
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
					}
				}
			}
		};
		
		return dbcListener;
	}
	
	private NBFields getSqlDocument(SqlDocument sqlObject)
	{
		NBFields fields = new NBFields();

		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.sql.QueryConfig");
		fields.put(IPC.NB_INSTNCE_ID, getInstanceName(sqlObject));
		fields.put("CMD_CODE", "L");
		fields.put("PATH", getPath(sqlObject));

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
	
	private NBFields getInstanceConfigInfo(IPCProtocol protocol, String name)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.instance.InstanceConfig");
		fields.put("CMD_CODE"			, "R");
		fields.put(IPC.NB_INSTNCE_ID	, name);
		
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
	
	private NBFields getInstanceList(IPCProtocol protocol)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.instance.InstanceConfig");
		fields.put("CMD_CODE", "L");
		fields.put("ISALL", "true");
		
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
	
	public void setProtocol(IPCProtocol protocol)
	{
		this.protocol = protocol;
		((NBContentProvider)treeViewer.getContentProvider()).setProtocol(protocol);
	}
	
	public void setPartName(String partName)
	{
		super.setPartName(partName);
	}
	
	public boolean hasAuthority()
	{
		if(protocol == null) return false;
		return !protocol.getUserAuthority().equals("Developer");
	}
	
	private void registContextMenu(Composite parent)
	{
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		
		IWorkbenchAction renameAction = ActionFactory.RENAME.create(window);
		renameAction.setToolTipText(NBLabel.get(0x006B));
		renameAction.setText(NBLabel.get(0x006E));
		
		IWorkbenchAction deleteAction = ActionFactory.DELETE.create(window);
		deleteAction.setToolTipText(NBLabel.get(0x0062));
		deleteAction.setText(NBLabel.get(0x0068));
		
		MenuManager newMgr = new MenuManager("&New                    ", "file.mnuNew");
		newMgr.add(ResourceFactory.NEW_INSTANCE.create(window));
		newMgr.add(new Separator());
		newMgr.add(ResourceFactory.NEW_SQL_DIR.create(window));
		newMgr.add(ResourceFactory.NEW_SQL_DOC.create(window));
		newMgr.add(new GroupMarker("file.new.OTHERS"));
		
		MenuManager mgr = new MenuManager();
		mgr.add(newMgr);
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
	
	private void searchSql(final HashMap<String, String> searchParam, final QueryStorageList selectObject)
	{
		SearchResultView tmpView = null;
		try {
			tmpView = (SearchResultView)getSite().getWorkbenchWindow().getActivePage().showView(SearchResultView.ID);
		} catch (PartInitException e) {
			IMessageBox.Error(shell, e.getMessage());
			return;
		} 
		
		final SearchResultView searchResultView = tmpView;
		
		final String instanceName = getInstanceName(selectObject);
		final String path;
		
		final String keyWord 		= searchParam.get("SCH_KEY").replace("/", "").replace("\\", "").replace("\\.", "");
		final String caseSensitive 	= searchParam.get("CASE");
		String selectedResource = null;
		if(searchParam.get("FUL").equals("true"))
		{
			path = "";
			selectedResource = "root";
		}
		else
		{
			path = getPath(selectObject);
			if(path.equals(""))
			{
				selectedResource = "root";
			}
			else
			{
				selectedResource = selectObject.getName();
			}
		}
	
		searchResultView.initTree(shell, instanceName, protocol, path, searchParam);
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(getSite().getWorkbenchWindow().getShell());
		try {

			pmd.run(true, true, new IRunnableWithProgress()
			{
				@SuppressWarnings("unchecked")
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException 
				{
					NBFields fields = new NBFields();

					fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.sql.QueryConfig");
					fields.put("CMD_CODE"			, "H");
					fields.put("PATH"				, path);
					fields.put(IPC.NB_INSTNCE_ID	, instanceName);
					fields.put("SCH_KEY"			, keyWord);
					fields.put("CASE"				, caseSensitive);
					fields.put("CNTS"				, (String)searchParam.get("CNTS"));
					fields.put("FLDR"				, (String)searchParam.get("FLDR"));

					try{
						fields = protocol.execute(fields);

						if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
						{
							IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
							return;
						}
						
						int tot = (Integer)fields.get("CNT");
						monitor.beginTask("Search", tot);	
						
						ArrayList<NBFields> list = (ArrayList<NBFields>)fields.get("SCH_LIST");

						for(int i=0; i<list.size(); i++)
						{
							fields = list.get(i);
							String treeType = (String)fields.get("TYPE");
							String path = (String)fields.get("PATH");
							
							if(((String)searchParam.get("FLDR")).equals("true"))
							{
								if(caseSensitive.equals("true"))
								{
									if(path.contains(keyWord))
									{
										searchResultView.setResult(keyWord, caseSensitive, path, null, treeType, searchParam.get("FLDR").equals("true"));
									}
								}
								else
								{
									if(path.toUpperCase().contains(keyWord.toUpperCase()))
									{
										searchResultView.setResult(keyWord, caseSensitive, path, null, treeType, searchParam.get("FLDR").equals("true"));
									}
								}
							}
							
							if(((String)searchParam.get("CNTS")).equals("true"))
							{
								fields = new NBFields();
								
								fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.sql.QueryConfig");
								fields.put("CMD_CODE"			, "C");
								fields.put("PATH"				, path);
								fields.put(IPC.NB_INSTNCE_ID	, instanceName);
								fields.put("SCH_KEY"			, keyWord);
								fields.put("CASE"				, caseSensitive);
								
								fields = protocol.execute(fields);

								if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
								{
									IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
									return;
								}
								
								ArrayList<NBFields> cntsList = (ArrayList<NBFields>)fields.get("STR_LIST");
								searchResultView.setResult(keyWord, caseSensitive, path, cntsList, treeType, searchParam.get("FLDR").equals("true"));
							}
							
							
							monitor.worked(i);
							monitor.subTask(path);
						}
						
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
					} catch (ProtocolException e) {
						IMessageBox.Error(shell, NBLabel.get(0x0090));
						closeConnection();
					} catch (DataTypeException e) {
						IMessageBox.Error(shell, NBLabel.get(0x0090));
						closeConnection();
					} catch (NullPointerException e) {
						IMessageBox.Error(shell, NBLabel.get(0x0090));
						closeConnection();
					} catch (Exception e){
						IMessageBox.Error(shell, NBLabel.get(0x0090));
						closeConnection();
					}finally {
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			IMessageBox.Error(shell, e.getMessage());
		} catch (InterruptedException e) {
			IMessageBox.Error(shell, e.getMessage());
		} finally {
			if(searchResultView != null)
				searchResultView.finishSetResult(selectedResource, keyWord);
		}
	}
	
	public void dispose()
	{
		treeViewer.removeSelectionChangedListener(selectChangedListener);
		treeViewer.removeDoubleClickListener(dbcListener);
		searchAction.dispose();
		newInstanceAction.dispose();
		terminateAction.dispose();
		resumeAction.dispose();
		runAction.dispose();
		newSQLFolderAction.dispose();
		newSQLDocumentAction.dispose();
	}
	
	private ISelectionChangedListener genSelectChangedListener()
	{
		selectChangedListener = new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event) {
				if(protocol.isConnected() && ((Model)treeViewer.getInput()).getChildren().size() > 0)newServiceAction.setEnabled(true);
				else newServiceAction.setEnabled(false);
				ISelection incoming = event.getSelection();
				if(incoming instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection)incoming;
					if(selection.size() == 1)
					{
						if((!protocol.getUserAuthority().equals("Developer") && selection.getFirstElement() instanceof Instance) ||
								selection.getFirstElement() instanceof SqlFolder || selection.getFirstElement() instanceof SqlDocument)
						{
							deleteAction.setEnabled(true);
							
							if(selection.getFirstElement() instanceof SqlFolder || selection.getFirstElement() instanceof SqlDocument)
								renameAction.setEnabled(true);
							else
								renameAction.setEnabled(false);
							
							return;
						}
					}
					return;
				}
				deleteAction.setEnabled(false);
				renameAction.setEnabled(false);
			}
		};
		
		return selectChangedListener;
	}

}
