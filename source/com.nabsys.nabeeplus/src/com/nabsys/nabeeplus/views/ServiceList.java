package com.nabsys.nabeeplus.views;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.actions.SearchAction;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.ResourceFactory;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.NabeeEditor;
import com.nabsys.nabeeplus.editors.ServiceDesigner;
import com.nabsys.nabeeplus.editors.input.ServiceDesignerInput;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.Server;
import com.nabsys.nabeeplus.views.model.ServiceModel;
import com.nabsys.nabeeplus.widgets.window.KeywordSearch;
import com.nabsys.nabeeplus.widgets.window.SimpleInputBox;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public class ServiceList extends NabeeView{
	
	public final static String ID = "com.nabsys.nabeeplus.views.serviceList";
	private Composite 							parent 					= null;
	private String 								instance 				= null;
	private IPCProtocol							protocol				= null;
	private ISelectionChangedListener 			selectChangedListener 	= null;
	private TableViewer 						serviceTable 			= null;
	private IAction								newServiceAction		= null;
	private IAction								deleteAction			= null;
	private IAction								copyAction				= null;
	private IAction								pasteAction				= null;
	private IAction								searchAction			= null;
	private IDoubleClickListener				dbcListener				= null;
	private Shell								shell					= null;
	private String								cpyServiceID			= null;
	private String								cpyInstance				= null;
	private Server								cpyServer				= null;
	public ServiceList() {
	}

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent;
		this.shell = parent.getShell();
	
		Composite searchBack = new Composite(parent, SWT.NONE);
		searchBack.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 10;
		searchBack.setLayout(layout);
		
		initServiceListObject(searchBack);
		
		newServiceAction = new Action(){
			public void run(){
				ServiceDesignerInput input = new ServiceDesignerInput(instance, "New Service", protocol, null);
				try {
					NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(input, ServiceDesigner.ID);
					editor.setServer(getServer());
				} catch (PartInitException e) {
					IMessageBox.Error(parent.getShell(), e.getMessage());
				}
			}
		};
		newServiceAction.setEnabled(false);
		
		copyAction = new Action() {
			public void run(){
				IStructuredSelection selection = (IStructuredSelection)serviceTable.getSelection();
				if(selection.isEmpty()) return;
				if(selection.getFirstElement() instanceof ServiceModel)
				{
					ServiceModel service = ((ServiceModel)selection.getFirstElement());
					//Get Instance Name and Service Name
					cpyServiceID = service.getID();
					cpyInstance = instance;
					cpyServer = getServer();
					pasteAction.setEnabled(true);
				}
			}
		};
		
		pasteAction = new Action() {
			public void run(){
				if(cpyServiceID == null || cpyInstance == null) return;
				//Open new Service Name
				IWorkbenchWindow window = getSite().getWorkbenchWindow();
				HashMap<String, String> params = new HashMap<String, String>();
				SimpleInputBox newServiceObject = new SimpleInputBox(shell, params);
				newServiceObject.setTitle(NBLabel.get(0x0296));
				newServiceObject.setImage("/icons/service_obj.gif");
				newServiceObject.setLabel(NBLabel.get(0x009B));
				newServiceObject.setText("Copy of " + cpyServiceID);
				newServiceObject.open(window, false);
				if(params.get("EVENT").equals("CANCEL")) return;
				
				//Open New Service Editor
				String newServiceID = params.get("TEXT");
				searchServiceCofig(instance, cpyInstance, cpyServiceID, newServiceID);
			}
			
			private void searchServiceCofig(String instance, String cpyInstance, String cpyServiceID, String newServiceID)
			{
				NBFields fields = new NBFields();
				fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
				fields.put("CMD_CODE"			, "R");
				fields.put(IPC.NB_INSTNCE_ID	, cpyInstance);
				fields.put("ID"					, cpyServiceID);
				
				try {

					fields = protocol.execute(fields);

					if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
					{
						IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
					}
					try {
						ServiceDesignerInput input = new ServiceDesignerInput(instance, newServiceID, protocol, fields);
						
						NabeeEditor editor = (NabeeEditor)getSite().getPage().openEditor(input, ServiceDesigner.ID);
						editor.setServer(getServer());
						((ServiceDesigner)editor).copyDesign(newServiceID, fields);
						
					} catch (PartInitException e) {
						IMessageBox.Error(shell, NBLabel.get(0x009E));
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
		};
		pasteAction.setEnabled(false);
		
		deleteAction = new Action() {
			public void run(){
				if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) == SWT.CANCEL)
				{
					return;
				}
				IStructuredSelection selection = (IStructuredSelection)serviceTable.getSelection();
				if(selection.isEmpty()) return;
				if(selection.getFirstElement() instanceof ServiceModel)
				{
					ServiceModel service = ((ServiceModel)selection.getFirstElement());
					deleteService(service);
				}
			}
		};
		deleteAction.setEnabled(false);
		
		searchAction = new SearchAction(getSite().getWorkbenchWindow()) {
			public void run(){
				KeywordSearch search = new KeywordSearch(shell);
				HashMap<String, Object> param = search.open(getSite().getWorkbenchWindow(), Activator.getImageDescriptor("/icons/search_src.gif").createImage(shell.getDisplay()));
				if(param.containsKey("RTN") && ((String)param.get("RTN")).equals("OK"))
				{
					String keyword = "";
					if(param.containsKey("SCH")) keyword = (String)param.get("SCH");
					searchServiceList(keyword);
				}
			}
		};
		searchAction.setEnabled(false);
		
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ResourceFactory.NEW_SERVICE.getId()	, newServiceAction);
		actionBars.setGlobalActionHandler(ResourceFactory.SEARCH.getId()		, searchAction);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId()			, deleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId()			, copyAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId()			, pasteAction);
		
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		MenuManager newMgr = new MenuManager("&New                    ", "file.mnuNew");
		newMgr.add(ResourceFactory.NEW_SERVICE.create(window));
		
		MenuManager mgr = new MenuManager();
		mgr.add(newMgr);
		mgr.add(new Separator());
		mgr.add(ActionFactory.COPY.create(window));
		mgr.add(ActionFactory.PASTE.create(window));
		mgr.add(new Separator());
		mgr.add(ActionFactory.DELETE.create(window));
		
		Menu menu = mgr.createContextMenu(parent);
		serviceTable.getTable().setMenu(menu);

	}
	
	public void init(String instance, IPCProtocol protocol)
	{
		this.instance = instance;
		this.protocol = protocol;
		searchServiceList("");
		setFocus();
	}
	
	private void deleteService(ServiceModel service)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
		fields.put("CMD_CODE"			, "D");
		fields.put(IPC.NB_INSTNCE_ID	, instance);
		fields.put("ID"					, service.getID());
		
		try {
			fields = protocol.execute(fields);
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			ServiceModel root = (ServiceModel)serviceTable.getInput();
			root.getChildren().remove(service);
			serviceTable.refresh();
			
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
	
	@SuppressWarnings("unchecked")
	private void searchServiceList(String keyword)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
		fields.put("CMD_CODE"			, "L");
		fields.put(IPC.NB_INSTNCE_ID	, instance);
		fields.put("SCH"				, keyword);
		
		try {

			fields = protocol.execute(fields);

			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
			}
			
			ArrayList<NBFields> list = (ArrayList<NBFields>)fields.get("LIST");
			ServiceModel root = new ServiceModel();
			serviceTable.setInput(root);
			for(int i=0; i<list.size(); i++)
			{
				NBFields tmp = list.get(i);
				
				ServiceModel service = new ServiceModel(root, (String)tmp.get("ID"), shell.getDisplay());
				service.setServiceName((String)tmp.get("NAME"));
				service.setServiceType((String)tmp.get("TYPE"));
				service.setRemark((String)tmp.get("REMARK"));
			}
			serviceTable.refresh();
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
	
	private void initServiceListObject(Composite searchBack)
	{
		serviceTable = new TableViewer(searchBack, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		serviceTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		TableViewerColumn column = new TableViewerColumn(serviceTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0153) + "                           ");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(serviceTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0154));
		column.getColumn().setWidth(140);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(serviceTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x015A));
		column.getColumn().setWidth(80);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(serviceTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012E));
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		serviceTable.getTable().setHeaderVisible(true);
		serviceTable.getTable().setLinesVisible(true);
		
		serviceTable.setContentProvider(new NBTableContentProvider());
		serviceTable.setLabelProvider(new NBTableLabelProvider(shell.getDisplay()));
		
		serviceTable.getTable().setFont(new Font(parent.getDisplay(), "Arial", 9, SWT.NONE));
		serviceTable.getTable().getColumn(0).pack();
		
		
		
		
		serviceTable.addSelectionChangedListener(genSelectChangedListener());
		serviceTable.addDoubleClickListener(dbcListener = new IDoubleClickListener (){
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				if(selection.isEmpty()) return;
				if(selection.getFirstElement() instanceof ServiceModel)
				{
					ServiceModel service = ((ServiceModel)selection.getFirstElement());
					searchServiceCofig(service.getID());
				}
			}
		});
		
		serviceTable.setInput(new ServiceModel());
	}
	
	public void refreshList(boolean isNew, String id, String name, String type, String remark)
	{
		ServiceModel root = (ServiceModel)serviceTable.getInput();
		if(isNew)
		{
			ServiceModel service = new ServiceModel(root, id, shell.getDisplay());
			service.setServiceName(name);
			service.setServiceType(type);
			service.setRemark(remark);
		}
		else
		{
			for(int i=0; i<root.getChildren().size(); i++)
			{
				ServiceModel service = (ServiceModel)root.getChildren().get(i);
				if(service.getID().equals(id))
				{
					service.setServiceName(name);
					service.setServiceType(type);
					service.setRemark(remark);
				}
			}
		}
		serviceTable.refresh();
	}
	
	private void searchServiceCofig(String serviceID)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
		fields.put("CMD_CODE"			, "R");
		fields.put(IPC.NB_INSTNCE_ID	, instance);
		fields.put("ID"					, serviceID);
		
		try {

			fields = protocol.execute(fields);

			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
			}
			try {
				ServiceDesignerInput input = new ServiceDesignerInput(instance, (String)fields.get("ID"), protocol, fields);
				
				IEditorPart editor = getSite().getPage().findEditor(input);
				if(editor == null)
				{
					NabeeEditor nEditor = (NabeeEditor)getSite().getPage().openEditor(input, ServiceDesigner.ID);
					nEditor.setServer(getServer());
					((ServiceDesigner)nEditor).loadDesign(fields);
				}
				else
				{
					getSite().getPage().openEditor(input, ServiceDesigner.ID);
				}
			} catch (PartInitException e) {
				IMessageBox.Error(shell, NBLabel.get(0x009E));
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
	
	public void dispose()
	{
		serviceTable.removeSelectionChangedListener(selectChangedListener);
		serviceTable.removeDoubleClickListener(dbcListener);
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
						deleteAction.setEnabled(protocol.isConnected());
						copyAction.setEnabled(true);
					}
					return;
				}
				deleteAction.setEnabled(false);
				copyAction.setEnabled(false);
			}
		};
		
		return selectChangedListener;
	}

	@Override
	public void setFocus() {
		if(newServiceAction != null && protocol != null)
		{
			newServiceAction.setEnabled(protocol.isConnected());
			searchAction.setEnabled(protocol.isConnected());
		}
		
		pasteAction.setEnabled(cpyServiceID != null && cpyServer == getServer());
	}
}
