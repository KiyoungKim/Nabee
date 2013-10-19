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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
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
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.input.TransactionConfigEditorInput;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.TableModel;
import com.nabsys.nabeeplus.views.model.Telegram;
import com.nabsys.nabeeplus.views.model.Transaction;
import com.nabsys.nabeeplus.widgets.BackBoard;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NCombo;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.nabeeplus.widgets.SubTitleBoard;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class TransactionConfigEditor extends NabeeEditor {

	public final static String ID = "com.nabsys.nabeeplus.editors.transactionConfigEditor";
	
	private Display					display						= null;
	private Shell					shell						= null;
	
	private TableViewer 			transactionTable			= null;
	private TableViewer 			telegramTable				= null;
	private CTabFolder 				tabFolder					= null;
	
	private SelectionListener 		btnSelection 				= null;
	private IDoubleClickListener	dbclListener				= null;
	
	private Combo 					cmbSchFlg					= null;
	private Combo 					cmbInstance					= null;
	private NStyledText				txtTelegramSch					= null;
	private NStyledText				txtTrxSch					= null;
	
	private NStyledText 			txtInstance					= null;
	private NStyledText 			txtID						= null;
	private NStyledText 			txtName						= null;
	private NCombo 					cmbActiveMode				= null;
	private NStyledText 			txtReqTelegram					= null;
	private NStyledText 			txtResTelegram					= null;
	private NStyledText 			txtRemark					= null;
	
	private Button					btnTrxSch					= null;
	private Button					btnTrxAdd					= null;
	private Button					btnTrxDelete				= null;
	private Button					btnTelegramSch					= null;
	private Button					btnSndRes					= null;
	private Button					btnSndReq					= null;
	
	private NBFields				sendMsg						= null;
	private boolean					isModified					= false;	
	private ToolItem 				importConfig 				= null;
	private ToolItem 				exportConfig 				= null;
	
	public TransactionConfigEditor(){
		
	}
	

	@Override
	public void createPartControl(Composite parent) {
		this.display = parent.getDisplay();
		this.shell = parent.getShell();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		
		tabFolder = new CTabFolder(parent, SWT.NONE);
		tabFolder.setTabHeight(20);
		tabFolder.setTabPosition(SWT.BOTTOM);
		tabFolder.setLayout(gridLayout);
		
		GridLayout boardLayout = new GridLayout();
		boardLayout.marginWidth = 0;
		boardLayout.marginHeight = 0;
		
		Composite schBack = new Composite(tabFolder, SWT.NONE);
		schBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		schBack.setLayout(boardLayout);
		
		CTabItem schTab = new CTabItem(tabFolder, SWT.NONE);
		schTab.setText(NBLabel.get(0x0140));
		schTab.setControl(schBack);
		tabFolder.setSelection(schTab);

		genButtonSelectionListener();
		getDbClickListener();
		sendMsg = new NBFields();
		
		
		Composite cfgBack = new Composite(tabFolder, SWT.NONE);
		cfgBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		cfgBack.setLayout(boardLayout);
		
		CTabItem cfgTab = new CTabItem(tabFolder, SWT.NONE);
		cfgTab.setText(NBLabel.get(0x0147));
		cfgTab.setControl(cfgBack);
		

		setSchTab(schBack);
		setCfgTab(cfgBack);
		
		searchTransactionList();
	}
	
	private void setSchTab(Composite back)
	{
		BackBoard schBoard = new BackBoard(back, SWT.NONE);
		schBoard.setTitleIcon(Activator.getImageDescriptor("/icons/message_storage.gif").createImage(display), new Point(10, 8));
		schBoard.setTitle(NBLabel.get(0x0140));
		schBoard.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		GridLayout boardLayout = new GridLayout();
		boardLayout.marginWidth = 0;
		boardLayout.marginHeight = 0;
		boardLayout.verticalSpacing = 20;
		schBoard.setLayout(boardLayout);
		
		setSchArea(schBoard.getPanel());
		setSchGridArea(schBoard.getPanel());
	}
	
	private void setCfgTab(Composite back)
	{
		NBModifiedListener mdfListener = new NBModifiedListener(){

			public void modified(String name, String value) {
				if(!sendMsg.containsKey("ACT_FLG"))
					sendMsg.put("ACT_FLG", "I");
					
				if(!sendMsg.containsKey("ACT_MODE")) sendMsg.put("ACT_MODE", "");
				if(name.equals("ACT_MODE")) 
					sendMsg.put(name, String.valueOf(value.equals("Active")));
				else
					sendMsg.put(name, value);
				
				isModified = true;
				firePropertyChange(PROP_DIRTY);
			}
		};
		
		BackBoard cfgBoard = new BackBoard(back, SWT.NONE, new Point(880,380));
		cfgBoard.setTitleIcon(Activator.getImageDescriptor("/icons/message_storage.gif").createImage(display), new Point(10, 8));
		cfgBoard.setTitle(NBLabel.get(0x0150));
		cfgBoard.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		GridLayout boardLayout = new GridLayout();
		boardLayout.marginWidth = 0;
		boardLayout.marginHeight = 0;
		boardLayout.verticalSpacing = 10;
		cfgBoard.setLayout(boardLayout);
		
		exportConfig = cfgBoard.addButton(NBLabel.get(0x0257), Activator.getImageDescriptor("/icons/exportconfig.gif").createImage(display));
		importConfig = cfgBoard.addButton(NBLabel.get(0x0256), Activator.getImageDescriptor("/icons/importconfig.gif").createImage(display));
		
		exportConfig.addSelectionListener(btnSelection);
		importConfig.addSelectionListener(btnSelection);
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 340;
		
		SashForm divider = new SashForm(cfgBoard.getPanel(), SWT.HORIZONTAL);
		divider.setLayoutData(layoutData);
		divider.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 10;
		layout.marginHeight = 0;
		
		SubTitleBoard gnrConfig = new SubTitleBoard(divider, SWT.NONE, NBLabel.get(0x014E), NBLabel.get(0x014F), 1, 10);
		gnrConfig.setLayoutData(layoutData);
		gnrConfig.getPanel().setLayout(layout);
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		FieldFrame firstFrame = new FieldFrame(gnrConfig.getPanel(), SWT.NONE, 100);
		firstFrame.setLayoutData(layoutData);
		
		txtInstance 	= firstFrame.getTextField(IPC.NB_INSTNCE_ID	, NBLabel.get(0x0124));
		txtID 			= firstFrame.getTextField("ID"				, NBLabel.get(0x0141));
		txtName 		= firstFrame.getTextField("NAME"			, NBLabel.get(0x0142));
		cmbActiveMode 	= firstFrame.getComboField("ACT_MODE"		, NBLabel.get(0x0144));
		txtReqTelegram 		= firstFrame.getTextField("REQ_TLGM"			, NBLabel.get(0x0148));
		txtResTelegram 		= firstFrame.getTextField("RES_TLGM"			, NBLabel.get(0x0149));
		txtRemark 		= firstFrame.getTextField("RMK"				, NBLabel.get(0x013B));
		
		txtInstance.setText(((TransactionConfigEditorInput)getEditorInput()).getName());
		txtInstance.setEnabled(false);
		txtInstance.setBackground(new Color(display, 240, 240, 240));
		txtInstance.setForeground(new Color(display, 150, 150, 150));
		
		txtReqTelegram.setEditable(false, true);
		txtReqTelegram.setBackground(new Color(display, 240, 240, 240));
		txtReqTelegram.setForeground(new Color(display, 150, 150, 150));
		
		txtResTelegram.setEditable(false, true);
		txtResTelegram.setBackground(new Color(display, 240, 240, 240));
		txtResTelegram.setForeground(new Color(display, 150, 150, 150));
		
		cmbActiveMode.add("Active");
		cmbActiveMode.add("Passive");
		cmbActiveMode.setText("Active");
		
		
		txtID.addNBModifiedListener(mdfListener);
		txtName.addNBModifiedListener(mdfListener);
		cmbActiveMode.addNBModifiedListener(mdfListener);
		txtReqTelegram.addNBModifiedListener(mdfListener);
		txtResTelegram.addNBModifiedListener(mdfListener);
		txtRemark.addNBModifiedListener(mdfListener);
		
		
		//////////////////////////////����ȸ////////////////////////
		SubTitleBoard schConfig = new SubTitleBoard(divider, SWT.NONE, NBLabel.get(0x0129), NBLabel.get(0x0151), 1, 10);
		schConfig.setLayoutData(layoutData);
		schConfig.getPanel().setLayout(layout);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 23;
		
		layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.numColumns = 3;
		layout.horizontalSpacing = 5;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		
		Composite btnBack = new Composite(schConfig.getPanel(), SWT.NONE);
		btnBack.setLayoutData(layoutData);
		btnBack.setLayout(layout);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 15;
		layoutData.widthHint = 100;
		
		txtTelegramSch = new NStyledText(btnBack, SWT.BORDER | SWT.SINGLE);
		txtTelegramSch.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 70;
		
		btnTelegramSch = new Button(btnBack, SWT.NONE);
		btnTelegramSch.setLayoutData(layoutData);
		btnTelegramSch.setText(NBLabel.get(0x0121));
		btnTelegramSch.addSelectionListener(btnSelection);
		
		txtTelegramSch.setDefaultButton(btnTelegramSch);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 18;
		
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		//////////////////////////////����ȸ////////////////////////
		
		
		/////////////////////////////����ȸ �׸���//////////////////
		telegramTable = new TableViewer(schConfig.getPanel(), SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		telegramTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		telegramTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(telegramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012B) + "                     ");
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(telegramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012C));
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(telegramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012E));
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		telegramTable.getTable().setHeaderVisible(true);
		telegramTable.getTable().setLinesVisible(true);
		
		telegramTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		telegramTable.getTable().getColumn(0).pack();
		
		telegramTable.setContentProvider(new NBTableContentProvider());
		telegramTable.setLabelProvider(new NBTableLabelProvider(display));
		
		Telegram root = new Telegram();
		telegramTable.setInput(root);
		/////////////////////////////����ȸ �׸���//////////////////
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 30;
		
		btnBack = new Composite(schConfig.getPanel(), SWT.NONE);
		btnBack.setLayoutData(layoutData);
		btnBack.setLayout(layout);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.numColumns = 3;
		layout.horizontalSpacing = 5;
		layout.marginWidth = 0;
		layout.marginHeight = 5;
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 23;
		layoutData.widthHint = 150;
		
		btnSndReq = new Button(btnBack, SWT.NONE);
		btnSndReq.setLayoutData(layoutData);
		btnSndReq.setText(NBLabel.get(0x014A));
		btnSndReq.addSelectionListener(btnSelection);
		
		btnSndRes = new Button(btnBack, SWT.NONE);
		btnSndRes.setLayoutData(layoutData);
		btnSndRes.setText(NBLabel.get(0x014B));
		btnSndRes.addSelectionListener(btnSelection);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 23;
		
		dum = new Composite(btnBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
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
		
		ArrayList<NBFields> insList = (ArrayList<NBFields>)((TransactionConfigEditorInput)getEditorInput()).getFields().get("INSTANCE_LIST");
		for(int i=0; i<insList.size(); i++)
		{
			cmbInstance.add((String)insList.get(i).get("INSTANCE_NAME"));
		}
		cmbInstance.setText(((TransactionConfigEditorInput)getEditorInput()).getName());
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 100;
		
		cmbSchFlg = new Combo(schBack, SWT.BORDER | SWT.READ_ONLY);
		cmbSchFlg.setLayoutData(layoutData);
		cmbSchFlg.add(NBLabel.get(0x0141));
		cmbSchFlg.add(NBLabel.get(0x0142));
		cmbSchFlg.setText(NBLabel.get(0x0141));
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 15;
		layoutData.widthHint = 100;
		
		txtTrxSch = new NStyledText(schBack, SWT.BORDER | SWT.SINGLE);
		txtTrxSch.setLayoutData(layoutData);

		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 80;
		
		btnTrxSch = new Button(schBack, SWT.NONE);
		btnTrxSch.setText(NBLabel.get(0x0121));
		btnTrxSch.setLayoutData(layoutData);
		btnTrxSch.addSelectionListener(btnSelection);
		
		txtTrxSch.setDefaultButton(btnTrxSch);
		
		
		Composite dum = new Composite(schBack, SWT.NONE);
		dum.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 80;
		
		btnTrxAdd = new Button(schBack, SWT.NONE);
		btnTrxAdd.setText(NBLabel.get(0x011F));
		btnTrxAdd.setLayoutData(layoutData);
		btnTrxAdd.addSelectionListener(btnSelection);
		
		btnTrxDelete = new Button(schBack, SWT.NONE);
		btnTrxDelete.setText(NBLabel.get(0x0120));
		btnTrxDelete.setLayoutData(layoutData);
		btnTrxDelete.addSelectionListener(btnSelection);
		
		
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
		layoutData.widthHint = 20;
		dum = new Composite(schBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 20;
		CLabel lblExp = new CLabel(back, SWT.NONE);
		lblExp.setLayoutData(layoutData);
		lblExp.setText(NBLabel.get(0x0143));
		lblExp.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
	}
	
	private void setSchGridArea(Composite back)
	{
		GridData tableAreaLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		transactionTable = new TableViewer(back, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		transactionTable.getTable().setLayoutData(tableAreaLayoutData);
		transactionTable.addDoubleClickListener(dbclListener);
		transactionTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(transactionTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0124) + "               ");
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(transactionTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0141));
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(transactionTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0142));
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(transactionTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0144));
		column.getColumn().setWidth(80);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.getColumn().setAlignment(SWT.CENTER);
		
		column = new TableViewerColumn(transactionTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012E));
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		transactionTable.getTable().setHeaderVisible(true);
		transactionTable.getTable().setLinesVisible(true);
		
		transactionTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		transactionTable.getTable().getColumn(0).pack();
		
		transactionTable.setContentProvider(new NBTableContentProvider());
		transactionTable.setLabelProvider(new NBTableLabelProvider(display));
		
		Transaction root = new Transaction();
		transactionTable.setInput(root);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doSave(IProgressMonitor monitor) {
		if(sendMsg.containsKey("ACT_FLG"))
		{
			if(txtID.getText().trim().equals(""))
			{
				IMessageBox.Error(shell, NBLabel.get(0x0199));
				return;
			}
			
			if(txtName.getText().trim().equals(""))
			{
				IMessageBox.Error(shell, NBLabel.get(0x019A));
				return;
			}
			
			//�űԵ�� Ʈ������� ��������Ʈ�� ���� ��� �������� Ȯ��.
			if(!sendMsg.containsKey("ID"))
			{
				sendMsg.remove("ACT_FLG");
			}
			ArrayList<NBFields> delList = null;
			if(sendMsg.containsKey("DEL_LST"))
			{
				delList = (ArrayList<NBFields>)sendMsg.get("DEL_LST");
				
				for(int i=0; i<delList.size(); i++)
				{
					NBFields tmp = delList.get(i);
					if(sendMsg.containsKey("ACT_FLG"))
					{
						if(sendMsg.containsKey("ID") && ((String)tmp.get("ID")).equals((String)sendMsg.get("ID")))
						{
							if(IMessageBox.Confirm(shell, NBLabel.get(0x0200)) != SWT.CANCEL)
							{
								delList.remove(i);
								sendMsg.put("DEL_LST", delList);
								break;
							}
						}
					}
				}
			}
		}
		
		if(!sendMsg.containsKey(IPC.NB_INSTNCE_ID)) 	sendMsg.put(IPC.NB_INSTNCE_ID, ((TransactionConfigEditorInput)getEditorInput()).getName());
		if(!sendMsg.containsKey("ID")) 					sendMsg.put("ID", txtID.getText());
		if(!sendMsg.containsKey("NAME")) 				sendMsg.put("NAME", txtName.getText());
		
		sendMsg.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.document.TransactionConfig");
		sendMsg.put("CMD_CODE", "S");
		
		try {
			NBFields fields = ((TransactionConfigEditorInput)getEditorInput()).getProtocol().execute(sendMsg);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			sendMsg = new NBFields();
			sendMsg.put("ACT_FLG", "U");
			
			txtID.setEnabled(false);
			txtID.setBackground(new Color(display, 240, 240, 240));
			txtID.setForeground(new Color(display, 150, 150, 150));
			
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
		

		
		TableModel telegramRoot = (TableModel)telegramTable.getInput();
		
		for(int i=0; i<telegramRoot.getChildren().size(); i++)
		{
			if(telegramRoot.getChildren().get(i).isModified()) telegramRoot.getChildren().get(i).setModified(false);
		}
		

		isModified = false;
		firePropertyChange(PROP_DIRTY);
	}
	
	@SuppressWarnings("unchecked")
	private void removeTransaction()
	{

		IStructuredSelection selection = (IStructuredSelection)transactionTable.getSelection();
		
		if(selection.isEmpty()) return;

		if(selection.getFirstElement() instanceof Transaction)
		{
			Transaction transaction = ((Transaction)selection.getFirstElement());

			if(!((TransactionConfigEditorInput)getEditorInput()).getName().equals(transaction.getInstanceName()))
			{
				IMessageBox.Info(shell, NBLabel.get(0x019F));
				return;
			}
			
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
			tmp.put("ID", transaction.getTransactionID());
			delList.add(tmp);
			
			sendMsg.put("DEL_LST", delList);
			
			transaction.remove();
			
			transactionTable.refresh();
			
			isModified = true;
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void searchTransactionList()
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TransactionConfig");
		fields.put("CMD_CODE"			, "L");
		fields.put("SCH"				, txtTrxSch.getText().trim());
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
			fields = ((TransactionConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			((TableModel)transactionTable.getInput()).getChildren().removeAll(((TableModel)transactionTable.getInput()).getChildren());
			transactionTable.refresh();

			ArrayList<NBFields> transactionList = (ArrayList<NBFields>)fields.get("TRX_LST");
			
			if(transactionList != null)
			{
				for(int i=0; i<transactionList.size(); i++)
				{
					NBFields tmp = transactionList.get(i);
					
					Transaction transaction = new Transaction(((TableModel)transactionTable.getInput()), (String)tmp.get("ID"), display);
					transaction.setTransactionName((String)tmp.get("NAME"));
					transaction.setInstanceName((String)tmp.get(IPC.NB_INSTNCE_ID));
					
					if(((String)tmp.get("ACT_MODE")).equals("true"))
						transaction.setTransactionActType("Active");
					else
						transaction.setTransactionActType("Passive");
					
					transaction.setTransactionRemark((String)tmp.get("RMK"));
					transaction.setModified(false);
				}
	
				transactionTable.refresh();
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
	
	private void genButtonSelectionListener()
	{
		btnSelection = new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {

				if(e.widget == btnTrxSch)
				{
					searchTransactionList();
				}
				else if(e.widget == btnTrxAdd)
				{
					txtID.setEnabled(true);
					txtID.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					txtID.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
					
					txtID.setText("");
					txtName.setText("");
					cmbActiveMode.setText("Active");
					txtReqTelegram.setText("");
					txtResTelegram.setText("");
					txtRemark.setText("");
					txtTelegramSch.setText("");
					
					((TableModel)telegramTable.getInput()).getChildren().removeAll(((TableModel)telegramTable.getInput()).getChildren());
					telegramTable.refresh();
					
					sendMsg.put("ACT_FLG", "I");
					
					isModified = false;
					firePropertyChange(PROP_DIRTY);
					
					tabFolder.setSelection(1);
				}
				else if(e.widget == btnTrxDelete)
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) == SWT.CANCEL)
					{
						return;
					}
					
					removeTransaction();
				}
				else if(e.widget == btnTelegramSch)
				{
					searchTelegramList();
				}
				else if(e.widget == btnSndRes)
				{
					IStructuredSelection selection = (IStructuredSelection)telegramTable.getSelection();
					
					if(selection.isEmpty()) return;
					
					if(selection.getFirstElement() instanceof Telegram)
					{
						Telegram telegram = ((Telegram)selection.getFirstElement());
						
						txtResTelegram.setText(telegram.getTelegramID());
					}
					
				}
				else if(e.widget == btnSndReq)
				{
					IStructuredSelection selection = (IStructuredSelection)telegramTable.getSelection();
					
					if(selection.isEmpty()) return;
					
					if(selection.getFirstElement() instanceof Telegram)
					{
						Telegram telegram = ((Telegram)selection.getFirstElement());
						
						txtReqTelegram.setText(telegram.getTelegramID());
					}
					
				}
				else if(e.widget == importConfig)
				{
					importConfigFile();
				}
				else if(e.widget == exportConfig)
				{
					exportConfigFile();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
	}
	
	private void exportConfigFile()
	{
		/*if(txtID.isEnabled() || txtID.getText().equals(""))
		{
			IMessageBox.Warning(shell, NBLabel.get(0x0258));
			return;
		}
		
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFileName(txtID.getText());
		dialog.setFilterExtensions(new String[] {"*.nbft"});
		dialog.setFilterNames(new String[] {"Nabee backup file"});
		String fileSelected = dialog.open();
		
		if(fileSelected == null) return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TransactionConfig");
		fields.put("CMD_CODE"			, "R");
		fields.put("ID"					, txtID.getText());
		fields.put(IPC.NB_INSTNCE_ID	, ((TransactionConfigEditorInput)getEditorInput()).getName());
		
		try {
			fields = ((TransactionConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}

			ElTransactionDefinition transactionConfig = new ElTransactionDefinition();
			
			transactionConfig.put("TRX_ID", txtID.getText());
			transactionConfig.NBsetTransactionName((String)fields.get("NAME"));
			transactionConfig.NBsetTransactionPassive(fields.get("ACT_MODE").equals("true"));
			transactionConfig.NBsetTransactionRemark((String)fields.get("RMK"));
			transactionConfig.NBsetTransactionRequestTelegramID((String)fields.get("REQ_TLGM"));
			transactionConfig.NBsetTransactionResponseTelegramID((String)fields.get("RES_TLGM"));
			
			ObjectFileIO objIO = new ObjectFileIO();
			objIO.saveObject(fileSelected, transactionConfig);
				
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
		} catch (IOException e) {
			IMessageBox.Error(shell, e.getMessage());
		}	*/
		
	}
	
	private void importConfigFile()
	{
		/*FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] {"*.nbft"});
		dialog.setFilterNames(new String[] {"Nabee backup file"});
		String fileSelected = dialog.open();
		
		if(fileSelected == null) return;
		
		try {
			ElTransactionDefinition transactionConfig = null;

			if(fileSelected != null)
			{
				ObjectFileIO objIO = new ObjectFileIO();
				transactionConfig = (ElTransactionDefinition)objIO.recoverObject(fileSelected);
			}
			
			if(transactionConfig == null)
			{
				IMessageBox.Error(shell, NBLabel.get(0x0259));
				return;
			}

			txtID.setText((String)transactionConfig.get("TRX_ID"));
			txtName.setText(transactionConfig.NBgetTransactionName());
			cmbActiveMode.setText(transactionConfig.NBisTransactionPassive()==true?"Active":"Passive");
			txtReqTelegram.setText(transactionConfig.NBgetTransactionRequestTelegramID());
			txtResTelegram.setText(transactionConfig.NBgetTransactionResponseTelegramID());
			txtRemark.setText(transactionConfig.NBgetTransactionRemark());
		} catch (IOException e) {
			IMessageBox.Error(shell, e.getMessage());
		} catch (ClassNotFoundException e) {
			IMessageBox.Error(shell, e.getMessage());
		}*/
	}
	
	private void getDbClickListener()
	{
		dbclListener = new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event) {
				if(event.getSelection() instanceof IStructuredSelection) 
				{

					IStructuredSelection selection = (IStructuredSelection)event.getSelection();

					if(selection.getFirstElement() instanceof Transaction)
					{
						sendMsg.put("ACT_FLG", "U");
						txtID.setEnabled(false);
						txtID.setBackground(new Color(display, 240, 240, 240));
						txtID.setForeground(new Color(display, 150, 150, 150));
						
						setTransactionConfig(((Transaction)selection.getFirstElement()).getInstanceName() ,((Transaction)selection.getFirstElement()).getID());
							
						isModified = false;
						firePropertyChange(PROP_DIRTY);
						
					}
				}
			}
		};
	}
	
	private void setTransactionConfig(String instance, String id)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TransactionConfig");
		fields.put("CMD_CODE"			, "R");
		fields.put("ID"					, id);
		fields.put(IPC.NB_INSTNCE_ID	, instance);
		
		try {
			fields = ((TransactionConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}

			txtID.setText((String)fields.get("ID"));
			txtName.setText((String)fields.get("NAME"));
			if(fields.get("ACT_MODE").equals("true"))
				cmbActiveMode.setText("Active");
			else
				cmbActiveMode.setText("Passive");
			txtReqTelegram.setText((String)fields.get("REQ_TLGM"));
			txtResTelegram.setText((String)fields.get("RES_TLGM"));
			txtRemark.setText((String)fields.get("RMK"));
				
			tabFolder.setSelection(1);
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
	private void searchTelegramList()
	{
		if(txtTelegramSch.getText().trim().equals(""))
			return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TelegramConfig");
		fields.put("CMD_CODE"			, "L");
		fields.put("SCH"				, txtTelegramSch.getText().trim());
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
			fields = ((TransactionConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			((TableModel)telegramTable.getInput()).getChildren().removeAll(((TableModel)telegramTable.getInput()).getChildren());
			telegramTable.refresh();

			ArrayList<NBFields> userList = (ArrayList<NBFields>)fields.get("TLGM_LST");
			
			if(userList != null)
			{
				for(int i=0; i<userList.size(); i++)
				{
					NBFields tmp = userList.get(i);
					
					Telegram telegram = new Telegram(((TableModel)telegramTable.getInput()), (String)tmp.get("ID"), display);
					telegram.setInstanceName((String)tmp.get(IPC.NB_INSTNCE_ID));
					telegram.setTelegramName((String)tmp.get("NAME"));
					telegram.setTelegramRemark((String)tmp.get("RMK"));
					telegram.setModified(false);
				}
	
				telegramTable.refresh();
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
	
	public void dispose()
	{
		if(!btnTrxSch.isDisposed())
			btnTrxSch.removeSelectionListener(btnSelection);
		if(!btnTrxAdd.isDisposed())
			btnTrxAdd.removeSelectionListener(btnSelection);
		if(!btnTrxDelete.isDisposed())
			btnTrxDelete.removeSelectionListener(btnSelection);
		if(!btnTelegramSch.isDisposed())
			btnTelegramSch.removeSelectionListener(btnSelection);
		if(!btnSndRes.isDisposed())
			btnSndRes.removeSelectionListener(btnSelection);
		if(!btnSndReq.isDisposed())
			btnSndReq.removeSelectionListener(btnSelection);
		if(!importConfig.isDisposed())
			importConfig.removeSelectionListener(btnSelection);
		if(!exportConfig.isDisposed())
			exportConfig.removeSelectionListener(btnSelection);
		
		transactionTable.removeDoubleClickListener(dbclListener);
		
		super.dispose();
	}

	@Override
	public void doSaveAs() {
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName() + " " + ((TransactionConfigEditorInput)input).getWork());
	}

	@Override
	public boolean isDirty() {
		return isModified;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
