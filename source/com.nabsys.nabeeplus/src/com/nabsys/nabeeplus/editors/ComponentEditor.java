package com.nabsys.nabeeplus.editors;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.input.ComponentEditorInput;
import com.nabsys.nabeeplus.views.model.Component;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.TableModel;
import com.nabsys.nabeeplus.widgets.BackBoard;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.nabeeplus.widgets.window.ComponentWindow;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class ComponentEditor extends NabeeEditor {
	
	public final static String ID = "com.nabsys.nabeeplus.editors.componentEditor";
	
	private Display					display						= null;
	private Shell					shell						= null;
	
	private boolean 				isModified 					= false;
	private TableViewer				componentTable				= null;
	
	private SelectionListener 		btnSelection 				= null;
	private IDoubleClickListener	dbclListener				= null;
	
	private Combo 					cmbSchFlg					= null;
	private Combo 					cmbInstance					= null;
	private NStyledText				txtCmpSch					= null;
	private Button					btnCmpSch					= null;
	private Button					btnCmpAdd					= null;
	private Button					btnCmpDelete				= null;
	
	private NBFields				sendMsg						= null;
	
	public ComponentEditor(){
		
	}
	
	@Override
	public void createPartControl(Composite parent) {
		this.display = parent.getDisplay();
		this.shell = parent.getShell();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		
		genButtonSelectionListener();
		genDbClickListener();
		
		sendMsg = new NBFields();
		
		setSchTab(parent);
		searchComponentList();
	}
	
	private void setSchTab(Composite back)
	{
		BackBoard schBoard = new BackBoard(back, SWT.NONE);
		schBoard.setTitleIcon(Activator.getImageDescriptor("/icons/component.gif").createImage(display), new Point(10, 8));
		schBoard.setTitle(NBLabel.get(0x016A));
		schBoard.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		GridLayout boardLayout = new GridLayout();
		boardLayout.marginWidth = 0;
		boardLayout.marginHeight = 0;
		boardLayout.verticalSpacing = 20;
		schBoard.setLayout(boardLayout);
		
		setSchArea(schBoard.getPanel());
		setSchGridArea(schBoard.getPanel());
	}
	
	@SuppressWarnings("unchecked")
	private void setSchArea(Composite back)
	{
		GridLayout backLayout = new GridLayout();
		backLayout.marginWidth = 0;
		backLayout.marginHeight = 0;
		backLayout.numColumns = 8;
		backLayout.horizontalSpacing = 10;
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 23;
		
		Composite schBack = new Composite(back, SWT.NONE);
		schBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		schBack.setLayout(backLayout);
		schBack.setLayoutData(layoutData);
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 100;
		
		CLabel label = new CLabel(schBack, SWT.NONE);
		label.setText(NBLabel.get(0x0124) + " :");
		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		label.setAlignment(SWT.RIGHT);
		label.setForeground(new Color(display, 49, 106, 197));
		label.setLayoutData(layoutData);
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 100;
		
		cmbInstance = new Combo(schBack, SWT.BORDER | SWT.READ_ONLY);
		cmbInstance.setLayoutData(layoutData);
		
		cmbInstance.add("ALL");
		
		ArrayList<NBFields> insList = (ArrayList<NBFields>)((ComponentEditorInput)getEditorInput()).getFields().get("INSTANCE_LIST");
		for(int i=0; i<insList.size(); i++)
		{
			cmbInstance.add((String)insList.get(i).get("INSTANCE_NAME"));
		}
		cmbInstance.setText(((ComponentEditorInput)getEditorInput()).getName());
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 100;
		
		cmbSchFlg = new Combo(schBack, SWT.BORDER | SWT.READ_ONLY);
		cmbSchFlg.setLayoutData(layoutData);
		cmbSchFlg.add(NBLabel.get(0x0164));
		cmbSchFlg.add(NBLabel.get(0x0165));
		cmbSchFlg.setText(NBLabel.get(0x0164));
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 15;
		layoutData.widthHint = 100;
		
		txtCmpSch = new NStyledText(schBack, SWT.BORDER | SWT.SINGLE);
		txtCmpSch.setLayoutData(layoutData);

		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 80;
		
		btnCmpSch = new Button(schBack, SWT.NONE);
		btnCmpSch.setText(NBLabel.get(0x0121));
		btnCmpSch.setLayoutData(layoutData);
		btnCmpSch.addSelectionListener(btnSelection);
		
		txtCmpSch.setDefaultButton(btnCmpSch);
		
		
		
		Composite dum = new Composite(schBack, SWT.NONE);
		dum.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 80;
		
		btnCmpAdd = new Button(schBack, SWT.NONE);
		btnCmpAdd.setText(NBLabel.get(0x011F));
		btnCmpAdd.setLayoutData(layoutData);
		btnCmpAdd.addSelectionListener(btnSelection);
		
		btnCmpDelete = new Button(schBack, SWT.NONE);
		btnCmpDelete.setText(NBLabel.get(0x0120));
		btnCmpDelete.setLayoutData(layoutData);
		btnCmpDelete.addSelectionListener(btnSelection);
		
		
		
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
		layoutData.widthHint = 20;
		dum = new Composite(schBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 20;
		CLabel lblExp = new CLabel(back, SWT.NONE);
		lblExp.setLayoutData(layoutData);
		lblExp.setText(NBLabel.get(0x016C));
		lblExp.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
	}
	
	private void setSchGridArea(Composite back)
	{
		GridData tableAreaLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		componentTable = new TableViewer(back, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		componentTable.getTable().setLayoutData(tableAreaLayoutData);
		componentTable.addDoubleClickListener(dbclListener);
		componentTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(componentTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0124) + "               ");
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(componentTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0164));
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(componentTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0165));
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(componentTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0166));
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(componentTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0044));
		column.getColumn().setWidth(350);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		componentTable.getTable().setHeaderVisible(true);
		componentTable.getTable().setLinesVisible(true);
		
		componentTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		componentTable.getTable().getColumn(0).pack();
		
		componentTable.setContentProvider(new NBTableContentProvider());
		componentTable.setLabelProvider(new NBTableLabelProvider(display));
		
		Component root = new Component();
		componentTable.setInput(root);
		
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		
		sendMsg.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.document.ComponentConfig");
		sendMsg.put("CMD_CODE", "D");
		
		try {
			NBFields fields = ((ComponentEditorInput)getEditorInput()).getProtocol().execute(sendMsg);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			sendMsg = new NBFields();
		} catch (SocketClosedException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (TimeoutException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
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
		
		isModified = false;
		firePropertyChange(PROP_DIRTY);
	}
	
	@SuppressWarnings("unchecked")
	private void searchComponentList()
	{
		//if(txtCmpSch.getText().trim().equals(""))
		//	return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ComponentConfig");
		fields.put("CMD_CODE"			, "L");
		fields.put("SCH"				, txtCmpSch.getText().trim());
		fields.put(IPC.NB_INSTNCE_ID	, cmbInstance.getText());
		
		switch(cmbSchFlg.getSelectionIndex())
		{
		case 0:
			fields.put("SCH_TYPE", "SCH_ID");
			break;
		case 1:
			fields.put("SCH_TYPE", "SCH_NAME");
			break;
		default:
		}
		
		try {
			fields = ((ComponentEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			((TableModel)componentTable.getInput()).getChildren().removeAll(((TableModel)componentTable.getInput()).getChildren());
			componentTable.refresh();

			ArrayList<NBFields> componentList = (ArrayList<NBFields>)fields.get("CMP_LST");
			
			if(componentList != null)
			{
				for(int i=0; i<componentList.size(); i++)
				{
					NBFields tmp = componentList.get(i);
					
					Component component = new Component(((TableModel)componentTable.getInput()), (String)tmp.get("ID"));
					component.setComponentName((String)tmp.get("NAME"));
					component.setInstanceName((String)tmp.get(IPC.NB_INSTNCE_ID));
					component.setComponentClass((String)tmp.get("CLASS"));
					component.setComponentFile((String)tmp.get("SAVE_PATH"));
					
					component.setModified(false);
				}
	
				componentTable.refresh();
			}
		} catch (SocketClosedException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (TimeoutException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
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
	private void removeComponent()
	{
		IStructuredSelection selection = (IStructuredSelection)componentTable.getSelection();
		
		if(selection.isEmpty()) return;

		if(selection.getFirstElement() instanceof Component)
		{
			Component component = ((Component)selection.getFirstElement());

			if(!((ComponentEditorInput)getEditorInput()).getName().equals(component.getInstanceName()))
			{
				IMessageBox.Info(shell, NBLabel.get(0x019F));
				return;
			}
			
			if(!sendMsg.containsKey(IPC.NB_INSTNCE_ID)) sendMsg.put(IPC.NB_INSTNCE_ID, ((ComponentEditorInput)getEditorInput()).getName());
			
			ArrayList<NBFields> delList = null;
			if(sendMsg.containsKey("DEL_LST"))
			{
				delList = (ArrayList<NBFields>)sendMsg.get("DEL_LST");
			}
			else
			{
				delList = new ArrayList<NBFields>();
			}
			NBFields tmp = new NBFields();
			tmp.put("ID", component.getComponentID());
			delList.add(tmp);
			
			sendMsg.put("DEL_LST", delList);
			
			component.remove();
			
			componentTable.refresh();
			
			isModified = true;
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	private void genButtonSelectionListener()
	{
		btnSelection = new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {

				if(e.widget == btnCmpSch)
				{
					searchComponentList();
				}
				else if(e.widget == btnCmpAdd)
				{
					ComponentWindow cmpWindow = new ComponentWindow(shell);
					cmpWindow.open(getSite().getWorkbenchWindow(), null, ((ComponentEditorInput)getEditorInput()).getName(), null, ((ComponentEditorInput)getEditorInput()).getProtocol());
				}
				else if(e.widget == btnCmpDelete)
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) == SWT.CANCEL)
					{
						return;
					}
					
					removeComponent();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
	}

	private void genDbClickListener()
	{
		dbclListener = new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event) {
				if(event.getSelection() instanceof IStructuredSelection) 
				{

					IStructuredSelection selection = (IStructuredSelection)event.getSelection();

					if(selection.getFirstElement() instanceof Component)
					{
						Component component = (Component)selection.getFirstElement();
						ComponentWindow cmpWindow = new ComponentWindow(shell);
						cmpWindow.open(getSite().getWorkbenchWindow(), component.getInstanceName(), ((ComponentEditorInput)getEditorInput()).getName(), component.getComponentID(), ((ComponentEditorInput)getEditorInput()).getProtocol());
					}
				}
			}
		};
	}
	
	public void dispose()
	{
		if(!btnCmpSch.isDisposed())
			btnCmpSch.removeSelectionListener(btnSelection);
		if(!btnCmpDelete.isDisposed())
			btnCmpDelete.removeSelectionListener(btnSelection);
		if(!btnCmpDelete.isDisposed())
			btnCmpDelete.removeSelectionListener(btnSelection);
		
		componentTable.removeDoubleClickListener(dbclListener);
		
		super.dispose();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName() + " " + ((ComponentEditorInput)input).getWork());
	}

	@Override
	public boolean isDirty() {
		return isModified;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {
		txtCmpSch.forceFocus();
	}
	

	@Override
	public void doSaveAs() {
	}

}
