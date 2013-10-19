package com.nabsys.nabeeplus.widgets.window;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.model.MethodModel;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.widgets.BackBoard;
import com.nabsys.nabeeplus.widgets.PopupWindow;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public class MethodWindow extends PopupWindow{

	private HashMap<String, String> 				params 					= null; 
	private Button									btnCancel				= null;
	private Button									btnOk					= null;
	private String									componentID				= null;
	private String									instanceName			= null;
	private IPCProtocol 							protocol				= null;
	private TableViewer 							methodTable				= null;
	private IDoubleClickListener					dbclListener			= null;
	
	public MethodWindow(Shell parent)
	{
		super(parent);
	}

	public HashMap<String, String> open(IWorkbenchWindow window, String instanceName, String componentID, IPCProtocol protocol) {
		params = new HashMap<String, String>();
		params.put("EVENT", "CANCEL");
		
		this.componentID = componentID;
		this.instanceName = instanceName;
		this.protocol = protocol; 
		
		setTitle(NBLabel.get(0x021C));
		setSize(new Point(450, 300));
		int width = window.getShell().getSize().x;
		int height = window.getShell().getSize().y;
		int x = window.getShell().getLocation().x;
		int y = window.getShell().getLocation().y;
		setLocation((width / 2) + x - (400 / 2), (height / 2) + y - (500 / 2));
		setLayout(new FillLayout());
		setImage(Activator.getImageDescriptor("/icons/resource_persp.gif").createImage(shell.getDisplay()));
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 20;
		
		BackBoard board = new BackBoard(shell, SWT.NONE);
		board.setTitleIcon(Activator.getImageDescriptor("/icons/compare_view.gif").createImage(display), new Point(10, 8));
		board.setTitle(NBLabel.get(0x021D));
		board.setLayoutData(new FillLayout());
		board.setLayout(layout);
		
		
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 0;
		layout.verticalSpacing = 10;
		layout.numColumns = 3;
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 23;
		
		Composite btnBack = new Composite(board.getPanel(), SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayoutData(layoutData);
		btnBack.setLayout(layout);
		
		setButtonArea(btnBack);
		setGridArea(board.getPanel());

		openWindow();
		
		setMethodData();
		
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }

		return params;
	}
	
	@SuppressWarnings("unchecked")
	private void setMethodData()
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ComponentConfig");
		fields.put("CMD_CODE"			, "ML");
		fields.put("ID"					, componentID);
		fields.put(IPC.NB_INSTNCE_ID	, instanceName);
		
		try {
			fields = protocol.execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			MethodModel root = (MethodModel)methodTable.getInput();
			
			ArrayList<NBFields> methodList = (ArrayList<NBFields>)fields.get("MTH_LST");
			for(int i=0; i<methodList.size(); i++)
			{
				MethodModel method = new MethodModel(root, Integer.toString(i));
				method.setType((String)methodList.get(i).get("RTN"));
				method.setMethod((String)methodList.get(i).get("NAME"));
				method.setParams((String)methodList.get(i).get("PRMS"));
			}
			
			methodTable.refresh();

		} catch (SocketClosedException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		} catch (TimeoutException e) {
			IMessageBox.Error(shell, e.getMessage());
		} catch (NetException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		} catch (UnsupportedEncodingException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		} catch (NoSuchAlgorithmException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		} catch (DataTypeException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		} catch (ProtocolException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		}
	}

	private void setGridArea(Composite back)
	{
		dbclListener = new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event) {
				if(event.getSelection() instanceof IStructuredSelection) 
				{

					IStructuredSelection selection = (IStructuredSelection)event.getSelection();

					if(selection.getFirstElement() instanceof MethodModel)
					{
						selectMethod();
					}
				}
			}
		};
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		
		Composite tableBack = new Composite(back, SWT.NONE);
		tableBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		tableBack.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tableBack.setLayout(layout);
		
		methodTable = new TableViewer(tableBack, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		methodTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		methodTable.addDoubleClickListener(dbclListener);

		
		TableViewerColumn column = new TableViewerColumn(methodTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x021A) + "               ");//Return type
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(methodTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0167));//Method
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(methodTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x021B));//Parameters
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
	
		methodTable.getTable().setHeaderVisible(true);
		methodTable.getTable().setLinesVisible(true);
		
		methodTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		methodTable.getTable().getColumn(0).pack();
		
		methodTable.setContentProvider(new NBTableContentProvider());
		methodTable.setLabelProvider(new NBTableLabelProvider(display));
		
		MethodModel root = new MethodModel();
		methodTable.setInput(root);
	}
	
	private void selectMethod()
	{
		IStructuredSelection selection = (IStructuredSelection)methodTable.getSelection();
		
		if(selection.isEmpty()) return;

		if(selection.getFirstElement() instanceof MethodModel)
		{
			params.put("METHOD", ((MethodModel)selection.getFirstElement()).getMethod());
			params.put("PARAMS", ((MethodModel)selection.getFirstElement()).getParams());
			params.put("RTN", ((MethodModel)selection.getFirstElement()).getType());
			methodTable.removeDoubleClickListener(dbclListener);
			params.put("EVENT", "OK");
			shell.dispose();
		}
	}
	
	private void setButtonArea(Composite back)
	{
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 80;
		layoutData.heightHint = 15;
		
		Composite dum = new Composite(back, SWT.NONE);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		dum.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		btnOk = new Button(back, SWT.NONE);
		btnOk.setText(NBLabel.get(0x009C));
		btnOk.setLayoutData(layoutData);
		
		
		btnCancel = new Button(back, SWT.NONE);
		btnCancel.setText(NBLabel.get(0x0086));
		btnCancel.setLayoutData(layoutData);
		
		
		SelectionListener listener = new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(e.widget == btnOk)
				{
					selectMethod();
				}
				else if(e.widget == btnCancel)
				{
					methodTable.removeDoubleClickListener(dbclListener);
					shell.dispose();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		};
		btnOk.addSelectionListener(listener);
		btnCancel.addSelectionListener(listener);
	}

}
