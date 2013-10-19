package com.nabsys.nabeeplus.views;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IConsoleConstants;

import com.nabsys.common.util.DateUtil;
import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.actions.SelectSupportAction;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.common.paramdata.ParameterContext;
import com.nabsys.nabeeplus.common.paramdata.ParameterFileControler;
import com.nabsys.nabeeplus.common.paramdata.ParameterMap;
import com.nabsys.nabeeplus.editors.ServiceDesigner;
import com.nabsys.nabeeplus.editors.input.ServiceDesignerInput;
import com.nabsys.nabeeplus.views.model.FieldTypeArray;
import com.nabsys.nabeeplus.views.model.Model;
import com.nabsys.nabeeplus.views.model.NBContentProvider;
import com.nabsys.nabeeplus.views.model.NBLabelProvider;
import com.nabsys.nabeeplus.views.model.TreeFieldModel;
import com.nabsys.nabeeplus.views.model.TreeListModel;
import com.nabsys.nabeeplus.views.model.TreeTableEditingSupport;
import com.nabsys.nabeeplus.views.model.TreeTableModel;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.NabeeProtocol;
import com.nabsys.net.protocol.IPC.IPC;

public class ServiceRequestView extends NabeeView {

	public final static String ID = "com.nabsys.nabeeplus.views.serviceRequestView";
	private TreeViewer				tree			= null;
	private Display					display			= null;
	private Shell					shell			= null;
	private NabeeProtocol			protocol		= null;
	private Label					label			= null;
	private Action					newFieldAction 	= null;
	private Action					newListAction 	= null;
	private Action					deleteAction 	= null;
	private ParameterFileControler	paramControler	= null;
	private SelectSupportAction		executeAction	= null;
	private String					instanceName	= null;
	private String					serviceName		= null;
	private IEditorPart 			editor 			= null;
	private NBFields 				testFields 		= null; 
	private boolean					isBreak			= false;
	public ServiceRequestView() {
	}
	
	@SuppressWarnings("unchecked")
	private void executeService()
	{
		DateUtil du = new DateUtil(NBLabel.getLocale());
		String curTime = du.getCurrentDate("yyyy. MM. dd. HH:mm:ss");
		this.label.setText(instanceName + " : " + serviceName + " (" + curTime + ")");
		
		executeAction.setEnabled(false);
		ParameterMap paramMap = paramControler.getObject();
		ParameterContext parameterContext = null;
		
		paramMap.put(instanceName+"."+serviceName, parameterContext = (new ParameterContext()));
		
		System.out.println("##################################################################");
		System.out.println("Start testing service " + serviceName + " [" + curTime+ "]");
		System.out.println("##################################################################");
		
		TreeTableModel root = (TreeTableModel)tree.getInput();
		setParam(root, parameterContext);
		try {
			paramControler.save();
		} catch (IOException e) {
			IMessageBox.Error(shell, e.getMessage());
		} catch (ClassNotFoundException e) {
			IMessageBox.Error(shell, e.getMessage());
		}
		
		try {
			getSite().getWorkbenchWindow().getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW);
		} catch (PartInitException e) {
		}
		
		testFields = new NBFields();
		testFields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.tester.ServiceTester");
		testFields.put("CMD_CODE"			, "E");
		testFields.put("_SERVICE_ID"		, serviceName);
		testFields.put(IPC.NB_INSTNCE_ID	, instanceName);
		testFields.put("_STATUS_"			, "S");
		
		if(!parameterContext.isEmpty())
		{
			Iterator<String> itr = parameterContext.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				
				if(parameterContext.get(key) instanceof ArrayList)
				{
					ArrayList<ParameterContext> list = (ArrayList<ParameterContext>)parameterContext.get(key);
					ArrayList<NBFields> fieldList = new ArrayList<NBFields>();
					for(int i=0; i<list.size(); i++)
					{
						ParameterContext subCtx = list.get(i);
						Iterator<String> subItr = subCtx.keySet().iterator();
						NBFields tmp = new NBFields();
						while(subItr.hasNext()){
							String subKey = subItr.next();
							tmp.put(subKey, subCtx.get(subKey));
						}
						
						fieldList.add(tmp);
					}
					
					testFields.put(key, fieldList);
				}
				else
				{
					testFields.put(key, parameterContext.get(key));
				}
			}
		}
		
		final ServiceDesigner testEditor = (ServiceDesigner)editor;
		testEditor.initTestStatus();
		isBreak = false;
		Thread tester = new Thread(){
			public void run(){
				while(true){
					try {
						final NBFields rtnField = protocol.execute(testFields);
						if((Integer)rtnField.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
						{
							display.syncExec(new Runnable(){
								public void run(){
									IMessageBox.Error(shell, (String)rtnField.get("RTN_MSG"));
									executeAction.setEnabled(true);
									isBreak = true;
									return;
								}
							});
						}
						
						if(isBreak) return;
						
						if(rtnField.containsKey("_STD_OUT_"))
						{
							System.out.println(rtnField.get("_STD_OUT_"));
						}
						else if(rtnField.containsKey("_STD_ERR_"))
						{
							System.err.print(rtnField.get("_STD_ERR_"));
							System.out.println("");
						}

						display.syncExec(new Runnable(){
							public void run(){
								if(testEditor != null && !testEditor.isDisposed())
								{
									if(rtnField.containsKey("_OBJ_ID_"))
										testEditor.setTestStatus((Integer)rtnField.get("_OBJ_ID_"));
								}
								else
								{
									executeAction.setEnabled(true);
									return;
								}
							}
						});

						if(rtnField.containsKey("_STATUS_") && ((String)rtnField.get("_STATUS_")).equals("E"))
						{
							executeAction.setEnabled(true);
							return;
						}
						
						testFields = new NBFields();
						testFields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.tester.ServiceTester");
						testFields.put("CMD_CODE"			, "E");
						testFields.put("ID"					, serviceName);
						testFields.put(IPC.NB_INSTNCE_ID	, instanceName);
						testFields.put("_STATUS_"			, "N");
					} catch (SocketClosedException e) {
						e.printStackTrace();
						executeAction.setEnabled(true);
						return;
					} catch (TimeoutException e) {
						e.printStackTrace();
						executeAction.setEnabled(true);
						return;
					} catch (NetException e) {
						e.printStackTrace();
						executeAction.setEnabled(true);
						return;
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						executeAction.setEnabled(true);
						return;
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
						executeAction.setEnabled(true);
						return;
					} catch (ProtocolException e) {
						e.printStackTrace();
						executeAction.setEnabled(true);
						return;
					} catch (DataTypeException e) {
						e.printStackTrace();
						executeAction.setEnabled(true);
						return;
					}
				}
			}
		};
		tester.start();
		
	}
	
	@SuppressWarnings("unchecked")
	private void setParam(TreeTableModel parent, ParameterContext ctx)
	{
		for(int i=0; i<parent.getChildren().size(); i++)
		{
			TreeTableModel child = (TreeTableModel)parent.getChildren().get(i);
			if(child instanceof TreeListModel)
			{
				if(child.getChildren().size() <= 0)
				{
					IMessageBox.Warning(shell, child.getName() + " " + NBLabel.get(0x0301));
					return;
				}
				
				ArrayList<ParameterContext> list = null;
				
				if(ctx.containsKey(child.getName()))
					list = (ArrayList<ParameterContext>)ctx.get(child.getName());
				else
					list = new ArrayList<ParameterContext>();
					
				ParameterContext tmp = new ParameterContext();
				for(int j=0; j<child.getChildren().size(); j++)
				{
					TreeTableModel listChild = (TreeTableModel)child.getChildren().get(j);
					switch(((TreeFieldModel) listChild).getType()){
					case 0:
						tmp.put(listChild.getName(), ((TreeFieldModel) listChild).getValue());
						break;
					case 1:
						tmp.put(listChild.getName(), Integer.parseInt(((TreeFieldModel) listChild).getValue()));
						break;
					case 2: 
						tmp.put(listChild.getName(), Double.parseDouble(((TreeFieldModel) listChild).getValue()));
						break;
					case 3:
						tmp.put(listChild.getName(), Float.parseFloat(((TreeFieldModel) listChild).getValue()));
						break;
					case 4:
						tmp.put(listChild.getName(), Long.parseLong(((TreeFieldModel) listChild).getValue()));
						break;
					}
				}
				list.add(tmp);
				ctx.put(child.getName(), list);
			}
			else if(child instanceof TreeFieldModel)
			{
				switch(((TreeFieldModel) child).getType()){
				case 0:
					ctx.put(child.getName(), ((TreeFieldModel) child).getValue());
					break;
				case 1:
					ctx.put(child.getName(), Integer.parseInt(((TreeFieldModel) child).getValue()));
					break;
				case 2: 
					ctx.put(child.getName(), Double.parseDouble(((TreeFieldModel) child).getValue()));
					break;
				case 3:
					ctx.put(child.getName(), Float.parseFloat(((TreeFieldModel) child).getValue()));
					break;
				case 4:
					ctx.put(child.getName(), Long.parseLong(((TreeFieldModel) child).getValue()));
					break;
				}
			}
		}
	}
	
	private void parseSubParams(TreeTableModel parent, ParameterContext ctx)
	{
		Iterator<String> itr = ctx.keySet().iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
				
			TreeFieldModel model = new TreeFieldModel(parent, key, Activator.getImageDescriptor("/icons/topic.gif").createImage());
			if(ctx.get(key) instanceof String)
				model.setType(0);
			else if(ctx.get(key) instanceof Integer)
				model.setType(1);
			else if(ctx.get(key) instanceof Double)
				model.setType(2);
			else if(ctx.get(key) instanceof Float)
				model.setType(3);
			else if(ctx.get(key) instanceof Long)
				model.setType(4);
			
			model.setValue(ctx.get(key));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void parseParams(String instanceName, String serviceName)
	{

		if(this.instanceName != null && this.serviceName != null)
		{
			TreeTableModel root = (TreeTableModel)tree.getInput();
			if(root.getChildren().size() > 0)
			{
				ParameterMap paramMap = paramControler.getObject();
				ParameterContext parameterContext = null;
				paramMap.put(this.instanceName+"."+this.serviceName, parameterContext = (new ParameterContext()));
				setParam(root, parameterContext);
				try {
					paramControler.save();
				} catch (IOException e) {
					IMessageBox.Error(shell, e.getMessage());
				} catch (ClassNotFoundException e) {
					IMessageBox.Error(shell, e.getMessage());
				}
			}
		}
		
		this.instanceName = instanceName;
		this.serviceName = serviceName;

		((TreeTableModel)tree.getInput()).getChildren().removeAll(((TreeTableModel)tree.getInput()).getChildren());
		tree.refresh();
		
		if(serviceName == null) return;
		if(!paramControler.getObject().containsKey(instanceName + "." + serviceName)) return;
		
		ParameterContext parameterContext = paramControler.getObject().get(instanceName + "." + serviceName);

		Iterator<String> itr = parameterContext.keySet().iterator();
		TreeTableModel root = (TreeTableModel)tree.getInput();
		boolean isExist = false;
		while(itr.hasNext())
		{
			String key = itr.next();
			isExist = true;
			if(parameterContext.get(key) instanceof ArrayList)
			{
				ArrayList<ParameterContext> list = (ArrayList<ParameterContext>)parameterContext.get(key);
				for(int i=0; i<list.size(); i++)
				{
					TreeListModel listModel = new TreeListModel(root, key, Activator.getImageDescriptor("/icons/list_obj.gif").createImage());
					parseSubParams(listModel, list.get(i));
				}
				
			}
			else
			{
				TreeFieldModel model = new TreeFieldModel(root, key, Activator.getImageDescriptor("/icons/topic.gif").createImage());
				if(parameterContext.get(key) instanceof String)
					model.setType(0);
				else if(parameterContext.get(key) instanceof Integer)
					model.setType(1);
				else if(parameterContext.get(key) instanceof Double)
					model.setType(2);
				else if(parameterContext.get(key) instanceof Float)
					model.setType(3);
				else if(parameterContext.get(key) instanceof Long)
					model.setType(4);
				
				model.setValue(parameterContext.get(key));
			}
		}
		
		if(!isExist)
		{
			paramControler.getObject().remove(instanceName + "." + serviceName);
			try {
				paramControler.save();
			} catch (IOException e) {
				IMessageBox.Error(shell, e.getMessage());
			} catch (ClassNotFoundException e) {
				IMessageBox.Error(shell, e.getMessage());
			}
		}
		else
		{
			tree.refresh();
		}
	}

	@Override
	public void createPartControl(final Composite parent) {
		setPartName(NBLabel.get(0x0243));
		display = parent.getDisplay();
		shell = parent.getShell();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.heightHint = 12;
		
		label = new Label(parent, SWT.NONE);
		label.setLayoutData(gridData);
		
		tree = new TreeViewer(parent, SWT.BORDER| SWT.H_SCROLL | SWT.V_SCROLL|SWT.FULL_SELECTION);
		tree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tree.setContentProvider(new NBContentProvider(display, true));
		tree.setLabelProvider(new NBLabelProvider(display));
		tree.setUseHashlookup(true);
		
		tree.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				int rtn = 0;
				if(o1 instanceof TreeFieldModel)
				{
					if(o2 instanceof TreeListModel) rtn = -1;
				}
				else if(o1 instanceof TreeListModel)
				{
					if(o2 instanceof TreeFieldModel) rtn = 1;
				}
				return rtn;
		    }
		});
		
		TreeViewerColumn column = new TreeViewerColumn(tree, SWT.NONE);
		column.getColumn().setWidth(300);
		column.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				return ((TreeTableModel)element).getName();
			}
			public Image getImage(Object element) {
				return ((TreeTableModel)element).getImage();
			}
		});
		column.setEditingSupport(new TreeTableEditingSupport(column.getViewer(), new TextCellEditor((Composite)column.getViewer().getControl()), 0));
		
		
		column = new TreeViewerColumn(tree, SWT.NONE);
		column.getColumn().setWidth(70);
		column.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof TreeFieldModel)
				{
					return FieldTypeArray.SPL_TYPE[((TreeFieldModel)element).getType()];
				}
				else 
				{
					return "";
				}
			}
			public Image getImage(Object element) {
				return null;
			}
		});
		column.setEditingSupport(new TreeTableEditingSupport(column.getViewer(), new ComboBoxCellEditor((Composite)column.getViewer().getControl(), FieldTypeArray.SPL_TYPE, SWT.READ_ONLY), 1));
		
		
		column = new TreeViewerColumn(tree, SWT.NONE);
		column.getColumn().setWidth(300);
		column.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if(element instanceof TreeFieldModel)
					return ((TreeFieldModel)element).getValue();
				else 
					return "";
			}
			public Image getImage(Object element) {
				return null;
			}
		});
		column.setEditingSupport(new TreeTableEditingSupport(column.getViewer(), new TextCellEditor((Composite)column.getViewer().getControl()), 2));
		
		
		MenuManager mgr = new MenuManager();
		mgr.add(newFieldAction = new Action(){
			public void run()
			{
				ISelection incoming = tree.getSelection();
				if(incoming.isEmpty())
				{
					new TreeFieldModel((TreeTableModel)tree.getInput(), "New Field ID", Activator.getImageDescriptor("/icons/topic.gif").createImage());
					tree.refresh();
				}
				else
				{
					if(incoming instanceof IStructuredSelection)
					{
						IStructuredSelection selection = (IStructuredSelection)incoming;
						if(selection.getFirstElement() instanceof TreeListModel)
						{
							new TreeFieldModel((TreeListModel)selection.getFirstElement(), "New Field ID", Activator.getImageDescriptor("/icons/topic.gif").createImage());
							tree.refresh();
						}
						else
						{
							new TreeFieldModel(((TreeFieldModel)selection.getFirstElement()).getParent(), "New Field ID", Activator.getImageDescriptor("/icons/topic.gif").createImage());
							tree.refresh();
						}
					}
				}
			}
		});
		newFieldAction.setText("Add New Field");
		newFieldAction.setToolTipText("Add new field");
		newFieldAction.setImageDescriptor(Activator.getImageDescriptor("/icons/topic.gif"));
		mgr.add(newListAction = new Action(){
			public void run()
			{
				new TreeListModel((TreeTableModel)tree.getInput(), "New List ID", Activator.getImageDescriptor("/icons/list_obj.gif").createImage());
				tree.refresh();
				
			}
		});
		newListAction.setText("Add New List");
		newListAction.setToolTipText("Add new list");
		newListAction.setImageDescriptor(Activator.getImageDescriptor("/icons/list_obj.gif"));
		
		mgr.add(deleteAction = new Action(){
			public void run()
			{
				if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) != SWT.CANCEL)
				{
					ISelection incoming = tree.getSelection();
					if(!incoming.isEmpty())
					{
						if(incoming instanceof IStructuredSelection)
						{
							IStructuredSelection selection = (IStructuredSelection)incoming;
							if(selection.getFirstElement() instanceof TreeTableModel)
							{
								TreeTableModel model = (TreeTableModel)selection.getFirstElement();
								model.getParent().getChildren().remove(model);
								tree.refresh();
							}
						}
					}
				}
			}
		});
		deleteAction.setText("Delete");
		deleteAction.setToolTipText("Delete");
		deleteAction.setImageDescriptor(Activator.getImageDescriptor("/icons/delete_obj.gif"));
		deleteAction.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/delete_obj.gif"));
		deleteAction.setEnabled(false);
		
		tree.setInput(new TreeTableModel());
		tree.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection incoming = event.getSelection();
				if(incoming instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection)incoming;
					if(selection.getFirstElement() instanceof Model) deleteAction.setEnabled(true);
					else deleteAction.setEnabled(false);
				}
			}
		});
		
		Menu menu = mgr.createContextMenu(tree.getTree());
		tree.getTree().setMenu(menu);
		tree.getTree().setLinesVisible(true);
		
		try {
			paramControler = new ParameterFileControler();
		} catch (IOException e) {
			IMessageBox.Error(shell, e.getMessage());
			return;
		} catch (ClassNotFoundException e) {
			IMessageBox.Error(shell, e.getMessage());
			return;
		}
		
		executeAction = new SelectSupportAction() {
			public void run(){
				executeService();
			}

			@Override
			public void _selectionChanged(Object obj) {
			}
		};
		executeAction.setText(NBLabel.get(0x0047));
		executeAction.setImageDescriptor(Activator.getImageDescriptor("/icons/exec.gif"));
		executeAction.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/exec.gif"));
        executeAction.setEnabled(false);
        getViewSite().getActionBars().getToolBarManager().add(executeAction);
		
		if(getSite().getWorkbenchWindow().getActivePage() != null)
		{
			editor = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
			if(editor != null && editor instanceof ServiceDesigner)
			{
				String serviceID = ((ServiceDesigner)editor).getPartName();
				if(serviceID != null && !serviceID.equals(""))
				{
					ServiceDesignerInput input = (ServiceDesignerInput)editor.getEditorInput();
					this.protocol = input.getProtocol();
					executeAction.setEnabled(true);
					setServer(((ServiceDesigner)editor).getServer());
					parseParams(input.getInstance(), input.getName());
				}
			}
		}
	}


	@Override
	public void setFocus() {
	}
	
	public void setProtocol(NabeeProtocol protocol)
	{
		this.protocol = protocol;
		executeAction.setEnabled(true);
	}
	
	public NabeeProtocol getProtocol()
	{
		return this.protocol;
	}
	public void setEditor(IEditorPart editor)
	{
		this.editor = editor;
	}
	
	private boolean isDispose = false;
	public boolean isDispose()
	{
		return this.isDispose;
	}
	
	public void dispose()
	{
		isDispose = true;
		super.dispose();
	}
}
