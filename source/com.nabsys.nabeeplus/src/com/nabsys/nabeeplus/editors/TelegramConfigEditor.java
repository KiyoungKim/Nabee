package com.nabsys.nabeeplus.editors;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.input.TelegramConfigEditorInput;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.listener.NBTableModifiedListener;
import com.nabsys.nabeeplus.views.model.AlignArray;
import com.nabsys.nabeeplus.views.model.Field;
import com.nabsys.nabeeplus.views.model.FieldTypeArray;
import com.nabsys.nabeeplus.views.model.Header;
import com.nabsys.nabeeplus.views.model.NBEditingSupport;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.TableModel;
import com.nabsys.nabeeplus.views.model.Telegram;
import com.nabsys.nabeeplus.widgets.BackBoard;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.nabeeplus.widgets.SubTitleBoard;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class TelegramConfigEditor extends NabeeEditor {

	public final static String ID = "com.nabsys.nabeeplus.editors.telegramConfigEditor";
	
	private Display					display						= null;
	private Shell					shell						= null;
	private TableViewer 			telegramTable 				= null;
	private TableViewer 			headerSchTable				= null;
	private TableViewer 			fieldTable					= null;
	private CTabFolder 				tabFolder					= null;
	
	private SelectionListener 		btnSelection 				= null;
	private IDoubleClickListener	dbclListener				= null;
	private NBTableModifiedListener nmListener					= null;
	
	private Combo 					cmbSchFlg					= null;
	private Combo 					cmbInstance					= null;
	private NStyledText				txtTelegramSch					= null;
	private NStyledText				txtHdrSch					= null;
	
	private NStyledText				txtInstance					= null;
	private NStyledText				txtID						= null;
	private NStyledText				txtName						= null;
	private NStyledText				txtHeader					= null;
	//private NCombo					cmbLogging					= null;
	private NStyledText				txtRemark					= null;
	
	private NBEditingSupport		idEditingSupport			= null;
	private NBEditingSupport		nameEditingSupport			= null;
	private NBEditingSupport		typeEditingSupport			= null;
	private NBEditingSupport		lengthEditingSupport		= null;
	private NBEditingSupport		alignEditingSupport			= null;
	private NBEditingSupport		mandatoryEditingSupport		= null;
	private NBEditingSupport		paddingEditingSupport		= null;
	private NBEditingSupport		remarkEditingSupport		= null;
	
	private Button					btnTelegramSch					= null;
	private Button					btnTelegramAdd					= null;
	private Button					btnTelegramDelete				= null;
	private Button					btnHdrSch					= null;
	private Button					btnFldAdd					= null;
	private Button					btnFldDelete				= null;
	//private ToolItem 				importConfig 				= null;
	//private ToolItem 				exportConfig 				= null;
	
	private NBFields				sendMsg						= null;
	private boolean					isModified					= false;			
			
	
	public TelegramConfigEditor(){
		
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
		schTab.setText(NBLabel.get(0x0129));
		schTab.setControl(schBack);
		tabFolder.setSelection(schTab);

		
		
		Composite cfgBack = new Composite(tabFolder, SWT.NONE);
		cfgBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		cfgBack.setLayout(boardLayout);
		
		CTabItem cfgTab = new CTabItem(tabFolder, SWT.NONE);
		cfgTab.setText(NBLabel.get(0x012A));
		cfgTab.setControl(cfgBack);
		

		
		setSchTab(schBack);
		setCfgTab(cfgBack);
		
		searchTelegramList();
	}
	
	private void setCfgTab(Composite back)
	{
		NBModifiedListener mdfListener = new NBModifiedListener(){

			public void modified(String name, String value) {
				if(!sendMsg.containsKey("ACT_FLG"))
					sendMsg.put("ACT_FLG", "I");
					
				sendMsg.put(name, value);
				isModified = true;
				firePropertyChange(PROP_DIRTY);
			}
		};
		
		BackBoard cfgBoard = new BackBoard(back, SWT.NONE, new Point(880,620));
		cfgBoard.setTitleIcon(Activator.getImageDescriptor("/icons/message_storage.gif").createImage(display), new Point(10, 8));
		cfgBoard.setTitle(NBLabel.get(0x012A));
		cfgBoard.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		GridLayout boardLayout = new GridLayout();
		boardLayout.marginWidth = 0;
		boardLayout.marginHeight = 0;
		boardLayout.verticalSpacing = 10;
		cfgBoard.setLayout(boardLayout);
		
		//exportConfig = cfgBoard.addButton(NBLabel.get(0x0257), Activator.getImageDescriptor("/icons/exportconfig.gif").createImage(display));
		//importConfig = cfgBoard.addButton(NBLabel.get(0x0256), Activator.getImageDescriptor("/icons/importconfig.gif").createImage(display));
		
		//exportConfig.addSelectionListener(btnSelection);
		//importConfig.addSelectionListener(btnSelection);
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 200;
		
		SashForm divider = new SashForm(cfgBoard.getPanel(), SWT.HORIZONTAL);
		divider.setLayoutData(layoutData);
		divider.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 10;
		layout.marginHeight = 0;
		
		SubTitleBoard gnrConfig = new SubTitleBoard(divider, SWT.NONE, NBLabel.get(0x012F), NBLabel.get(0x0130), 1, 10);
		gnrConfig.setLayoutData(layoutData);
		gnrConfig.getPanel().setLayout(layout);

		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		FieldFrame firstFrame = new FieldFrame(gnrConfig.getPanel(), SWT.NONE, 100);
		firstFrame.setLayoutData(layoutData);
		
		txtInstance = firstFrame.getTextField(IPC.NB_INSTNCE_ID	, NBLabel.get(0x0124));
		txtID 		= firstFrame.getTextField("ID"				, NBLabel.get(0x013C));
		txtName 	= firstFrame.getTextField("NAME"			, NBLabel.get(0x013D));
		txtHeader 	= firstFrame.getTextField("HDR"				, NBLabel.get(0x013F));
		//cmbLogging 	= firstFrame.getComboField("LGNG"			, NBLabel.get(0x013E));
		txtRemark 	= firstFrame.getTextField("RMK"				, NBLabel.get(0x013B));
		
		txtInstance.setText(((TelegramConfigEditorInput)getEditorInput()).getName());
		txtInstance.setEnabled(false);
		txtInstance.setBackground(new Color(display, 240, 240, 240));
		txtInstance.setForeground(new Color(display, 150, 150, 150));
		
		txtHeader.setEditable(false, true);
		txtHeader.setBackground(new Color(display, 240, 240, 240));
		txtHeader.setForeground(new Color(display, 150, 150, 150));
		
		//cmbLogging.add("ALL");
		//cmbLogging.add("Header only");
		//cmbLogging.add("None");
		
		//cmbLogging.setText("None");
		sendMsg.put("LGNG", "ALL");
		
		
		
		txtID.addNBModifiedListener(mdfListener);
		txtName.addNBModifiedListener(mdfListener);
		txtHeader.addNBModifiedListener(mdfListener);
		//cmbLogging.addNBModifiedListener(mdfListener);
		txtRemark.addNBModifiedListener(mdfListener);
		
		//////////////////////////////����ȸ////////////////////////
		SubTitleBoard schConfig = new SubTitleBoard(divider, SWT.NONE, NBLabel.get(0x014C), NBLabel.get(0x014D), 1, 10);
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
		
		txtHdrSch = new NStyledText(btnBack, SWT.BORDER | SWT.SINGLE);
		txtHdrSch.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 70;
		
		btnHdrSch = new Button(btnBack, SWT.NONE);
		btnHdrSch.setLayoutData(layoutData);
		btnHdrSch.setText(NBLabel.get(0x0121));
		btnHdrSch.addSelectionListener(btnSelection);
		
		txtHdrSch.setDefaultButton(btnHdrSch);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 18;
		
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		//////////////////////////////����ȸ////////////////////////
		
		
		/////////////////////////////����ȸ �׸���//////////////////
		headerSchTable = new TableViewer(schConfig.getPanel(), SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		headerSchTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		headerSchTable.addDoubleClickListener(dbclListener);
		headerSchTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(headerSchTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012B) + "                     ");
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(headerSchTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012C));
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(headerSchTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012E));
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		headerSchTable.getTable().setHeaderVisible(true);
		headerSchTable.getTable().setLinesVisible(true);
		
		headerSchTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		headerSchTable.getTable().getColumn(0).pack();
		
		headerSchTable.setContentProvider(new NBTableContentProvider());
		headerSchTable.setLabelProvider(new NBTableLabelProvider(display));
		
		Header root = new Header();
		headerSchTable.setInput(root);
		/////////////////////////////����ȸ �׸���//////////////////
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		
		SubTitleBoard fldConfig = new SubTitleBoard(cfgBoard.getPanel(), SWT.NONE, NBLabel.get(0x0131), NBLabel.get(0x0132), 1);
		fldConfig.setLayoutData(layoutData);
		
		setFieldArea(fldConfig.getPanel());
	}
	
	private void setFieldArea(Composite back)
	{
		//////////////////BUTTON///////////////////////
		GridLayout backLayout = new GridLayout();
		backLayout.marginWidth = 0;
		backLayout.marginHeight = 0;
		backLayout.numColumns = 3;
		backLayout.horizontalSpacing = 10;
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 23;
		
		Composite btnBack = new Composite(back, SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayout(backLayout);
		btnBack.setLayoutData(layoutData);
		
		
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 80;

		btnFldAdd = new Button(btnBack, SWT.NONE);
		btnFldAdd.setText(NBLabel.get(0x011F));
		btnFldAdd.setLayoutData(layoutData);
		btnFldAdd.addSelectionListener(btnSelection);

		btnFldDelete = new Button(btnBack, SWT.NONE);
		btnFldDelete.setText(NBLabel.get(0x0120));
		btnFldDelete.setLayoutData(layoutData);
		btnFldDelete.addSelectionListener(btnSelection);
		//////////////////BUTTON///////////////////////
		
		
		GridData tableAreaLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		fieldTable = new TableViewer(back, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		fieldTable.getTable().setLayoutData(tableAreaLayoutData);
		
		TableViewerColumn column = new TableViewerColumn(fieldTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0133) + "    ");
		column.getColumn().setWidth(50);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.getColumn().setAlignment(SWT.RIGHT);
		
		column = new TableViewerColumn(fieldTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0134));
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(idEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 1));

		column = new TableViewerColumn(fieldTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0135));
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(nameEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 2));
		
		column = new TableViewerColumn(fieldTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0136));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.getColumn().setAlignment(SWT.CENTER);
		column.setEditingSupport(typeEditingSupport = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
						FieldTypeArray.TYPE, SWT.READ_ONLY),3));
		
		column = new TableViewerColumn(fieldTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0137));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.getColumn().setAlignment(SWT.RIGHT);
		column.setEditingSupport(lengthEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 4, true));
		
		column = new TableViewerColumn(fieldTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0138));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.getColumn().setAlignment(SWT.CENTER);
		column.setEditingSupport(alignEditingSupport = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
						AlignArray.ALIGN, SWT.READ_ONLY),5));
		
		column = new TableViewerColumn(fieldTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0139));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.getColumn().setAlignment(SWT.CENTER);
		column.setEditingSupport(mandatoryEditingSupport = new NBEditingSupport(column.getViewer(), 
				new CheckboxCellEditor((Composite)column.getViewer().getControl()), 6));
		
		
		column = new TableViewerColumn(fieldTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x013A));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.getColumn().setAlignment(SWT.CENTER);
		column.setEditingSupport(paddingEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 7, 1));
		
		column = new TableViewerColumn(fieldTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x013B));
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(remarkEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 8));

		nmListener = new NBTableModifiedListener(){

			public void modified(TableModel model, String id, String fieldName,
					int fieldIndex) {
				if(fieldName.equals("ID") && !model.getID().matches("^[1-9a-zA-Z_]+$"))
				{
					IMessageBox.Warning(shell, "Field key format error.");
					model.setID(id);
					fieldTable.refresh();
					return;
				}
				isModified = true;
				firePropertyChange(PROP_DIRTY);
			}
			
		};
		
		idEditingSupport.addNBTableModifiedListener(nmListener);
		nameEditingSupport.addNBTableModifiedListener(nmListener);
		typeEditingSupport.addNBTableModifiedListener(nmListener);
		lengthEditingSupport.addNBTableModifiedListener(nmListener);
		alignEditingSupport.addNBTableModifiedListener(nmListener);
		mandatoryEditingSupport.addNBTableModifiedListener(nmListener);
		paddingEditingSupport.addNBTableModifiedListener(nmListener);
		remarkEditingSupport.addNBTableModifiedListener(nmListener);
		

		fieldTable.getTable().setHeaderVisible(true);
		fieldTable.getTable().setLinesVisible(true);
		
		fieldTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		fieldTable.getTable().getColumn(0).pack();
		
		fieldTable.setContentProvider(new NBTableContentProvider());
		fieldTable.setLabelProvider(new NBTableLabelProvider(display));
		
		Field root = new Field();
		fieldTable.setInput(root);
		
		DragSourceAdapter dragSourceListener = new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) 
		    {
				IStructuredSelection selection = (IStructuredSelection)fieldTable.getSelection();
				
				if(selection.isEmpty()) return;
				
				if(selection.getFirstElement() instanceof Field)
				{
					Field field = ((Field)selection.getFirstElement());
					event.data = field.getFieldNo();
				}
		    }
		};
		
		DropTargetAdapter dropTargetListener = new DropTargetAdapter()
		{
	    	public void dragEnter(DropTargetEvent event)
	    	{
	    		if (event.detail == DND.DROP_DEFAULT) 
	    		{
	    			event.detail = (event.operations & DND.DROP_COPY) != 0 ? DND.DROP_COPY : DND.DROP_NONE;
	    		}

	    		for (int i = 0; i < event.dataTypes.length; i++)
	    		{
	    			if (TextTransfer.getInstance().isSupportedType(event.dataTypes[i]))
	    			{
	    				event.currentDataType = event.dataTypes[i];
	    			}
	    		}
	    	}

	    	public void dragOver(DropTargetEvent event)
	    	{
	    		event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
	    	}
	    	
	    	public void drop(DropTargetEvent event)
	    	{
	    		if (TextTransfer.getInstance().isSupportedType(event.currentDataType))
	    		{
	    			// Get the dropped data
	    			int fromIndex = Integer.parseInt((String)event.data) - 1;
	    			int toIndex = 0;
	    			
	    			try{
	    				toIndex = Integer.parseInt(((TableItem)event.item).getText()) -1;
	    			}catch(NullPointerException e){ return; }
	    			
	    			if(fromIndex == toIndex) return;
	    			
	    			Field field = (Field)fieldTable.getInput();
	    			
	    			if(toIndex > field.getChildren().size() - 1) return;
	    			
	    			Field tmpField = null;
	    			Field fromField = (Field)field.getChildren().get(fromIndex);
	    			if(fromIndex > toIndex)
	    			{
	    				for(int i=toIndex; i<=fromIndex; i++)
	    				{
	    					tmpField = (Field)field.getChildren().get(i);
	    					field.getChildren().set(i, fromField);
	    					fromField = tmpField;
	    				}
	    			}
	    			else if(fromIndex < toIndex)
	    			{
	    				for(int i=toIndex; i>=fromIndex; i--)
	    				{
	    					tmpField = (Field)field.getChildren().get(i);
	    					field.getChildren().set(i, fromField);
	    					fromField = tmpField;
	    				}
	    			}
	    			
	    			fieldTable.refresh();
	    			
	    			isModified = true;
	    			firePropertyChange(PROP_DIRTY);
	    		}
	    	}
		};
		
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};

		fieldTable.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, types, dragSourceListener);
		fieldTable.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT, types, dropTargetListener);
	}
	
	private void setSchTab(Composite back)
	{
		BackBoard schBoard = new BackBoard(back, SWT.NONE);
		schBoard.setTitleIcon(Activator.getImageDescriptor("/icons/message_storage.gif").createImage(display), new Point(10, 8));
		schBoard.setTitle(NBLabel.get(0x0129));
		schBoard.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		GridLayout boardLayout = new GridLayout();
		boardLayout.marginWidth = 0;
		boardLayout.marginHeight = 0;
		boardLayout.verticalSpacing = 20;
		schBoard.setLayout(boardLayout);
		
		setSchArea(schBoard.getPanel());
		setSchGridArea(schBoard.getPanel());
	}
	
	private void setSchGridArea(Composite back)
	{
		telegramTable = new TableViewer(back, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		telegramTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		telegramTable.addDoubleClickListener(dbclListener);
		telegramTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(telegramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0124) + "               ");
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(telegramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012B));
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(telegramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x012C));
		column.getColumn().setWidth(250);
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
		
		ArrayList<NBFields> insList = (ArrayList<NBFields>)((TelegramConfigEditorInput)getEditorInput()).getFields().get("INSTANCE_LIST");
		for(int i=0; i<insList.size(); i++)
		{
			cmbInstance.add((String)insList.get(i).get("INSTANCE_NAME"));
		}
		cmbInstance.setText(((TelegramConfigEditorInput)getEditorInput()).getName());
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 100;
		
		cmbSchFlg = new Combo(schBack, SWT.BORDER | SWT.READ_ONLY);
		cmbSchFlg.setLayoutData(layoutData);
		cmbSchFlg.add(NBLabel.get(0x012B));
		cmbSchFlg.add(NBLabel.get(0x012C));
		cmbSchFlg.setText(NBLabel.get(0x012B));
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 15;
		layoutData.widthHint = 100;
		
		txtTelegramSch = new NStyledText(schBack, SWT.BORDER | SWT.SINGLE);
		txtTelegramSch.setLayoutData(layoutData);

		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 80;
		
		btnTelegramSch = new Button(schBack, SWT.NONE);
		btnTelegramSch.setText(NBLabel.get(0x0121));
		btnTelegramSch.setLayoutData(layoutData);
		btnTelegramSch.addSelectionListener(btnSelection);
		
		txtTelegramSch.setDefaultButton(btnTelegramSch);
		
		
		Composite dum = new Composite(schBack, SWT.NONE);
		dum.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 80;
		
		btnTelegramAdd = new Button(schBack, SWT.NONE);
		btnTelegramAdd.setText(NBLabel.get(0x011F));
		btnTelegramAdd.setLayoutData(layoutData);
		btnTelegramAdd.addSelectionListener(btnSelection);
		
		btnTelegramDelete = new Button(schBack, SWT.NONE);
		btnTelegramDelete.setText(NBLabel.get(0x0120));
		btnTelegramDelete.setLayoutData(layoutData);
		btnTelegramDelete.addSelectionListener(btnSelection);
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
		layoutData.widthHint = 20;
		dum = new Composite(schBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 20;
		CLabel lblExp = new CLabel(back, SWT.NONE);
		lblExp.setLayoutData(layoutData);
		lblExp.setText(NBLabel.get(0x012D));
		lblExp.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doSave(IProgressMonitor monitor) {
		if(!txtID.getText().equals("") && txtID.getText().equals(txtHeader.getText()))
		{
			IMessageBox.Error(shell, NBLabel.get(0x0210));
			return;
		}
		
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
		
		if(!sendMsg.containsKey(IPC.NB_INSTNCE_ID)) 	sendMsg.put(IPC.NB_INSTNCE_ID	, ((TelegramConfigEditorInput)getEditorInput()).getName());
		if(!sendMsg.containsKey("ID")) 					sendMsg.put("ID"				, txtID.getText());
		if(!sendMsg.containsKey("NAME")) 				sendMsg.put("NAME"				, txtName.getText());

		
		//FIELD SETTING
		ArrayList<NBFields> fieldList = new ArrayList<NBFields>();
		Field root = (Field)fieldTable.getInput();
		ArrayList<TableModel> tableFieldList = root.getChildren();
		for(int i=0; i<tableFieldList.size(); i++)
		{
			Field field = (Field)tableFieldList.get(i);
			NBFields tmpField = new NBFields();

			tmpField.put("ID", field.getFieldID());
			tmpField.put("NAME", field.getFieldName());
			tmpField.put("ALIGN", field.getFieldAlign().substring(0, 1));
			
			if(field.getFieldType().equals("BYTEARR"))
				tmpField.put("TYPE", "BA");
			else
				tmpField.put("TYPE", field.getFieldType().substring(0, 1));

			tmpField.put("LENGTH", Integer.parseInt(field.getFieldLength()));
			tmpField.put("MANDATORY", field.isFieldMandatory().toString());
			
			if(field.getFieldPadding().length() > 0)
				tmpField.put("PADDING", field.getFieldPadding().subSequence(0, 1));
			else
				tmpField.put("PADDING", "");

			tmpField.put("REMARK", field.getFieldRemark());
			
			fieldList.add(tmpField);
		}
		
		sendMsg.put("FLD", fieldList);

		
		sendMsg.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.document.TelegramConfig");
		sendMsg.put("CMD_CODE", "S");
		
		try {
			NBFields fields = ((TelegramConfigEditorInput)getEditorInput()).getProtocol().execute(sendMsg);
			
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
		
		
		TableModel fieldRoot = (TableModel)fieldTable.getInput();
		
		for(int i=0; i<fieldRoot.getChildren().size(); i++)
		{
			if(fieldRoot.getChildren().get(i).isModified()) fieldRoot.getChildren().get(i).setModified(false);
		}
		
		TableModel telegramRoot = (TableModel)telegramTable.getInput();
		
		for(int i=0; i<telegramRoot.getChildren().size(); i++)
		{
			if(telegramRoot.getChildren().get(i).isModified()) telegramRoot.getChildren().get(i).setModified(false);
		}
		

		isModified = false;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName() + " " + ((TelegramConfigEditorInput)input).getWork());
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
	
	private void genDbClickListener()
	{
		dbclListener = new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event) {
				if(event.getSelection() instanceof IStructuredSelection) 
				{

					IStructuredSelection selection = (IStructuredSelection)event.getSelection();

					if(selection.getFirstElement() instanceof Header)
					{
						txtHeader.setText(((Header)selection.getFirstElement()).getHeaderID());
					}
					else if(selection.getFirstElement() instanceof Telegram)
					{
						String selInstanceName = ((Telegram)selection.getFirstElement()).getInstanceName();
						if(selInstanceName.equals(((TelegramConfigEditorInput)getEditorInput()).getName()))
						{
							sendMsg.put("ACT_FLG", "U");
							txtID.setEnabled(false);
							txtID.setBackground(new Color(display, 240, 240, 240));
							txtID.setForeground(new Color(display, 150, 150, 150));
							
							setTelegramConfig(selInstanceName ,((Telegram)selection.getFirstElement()).getID());
								
							isModified = false;
							firePropertyChange(PROP_DIRTY);
						}
						else
						{
							sendMsg.put("ACT_FLG", "I");
							txtID.setEnabled(true);
							txtID.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
							txtID.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
							
							setTelegramConfig(selInstanceName ,((Telegram)selection.getFirstElement()).getID());
								
							isModified = true;
							firePropertyChange(PROP_DIRTY);
						}
						
					}
				}
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	private void setTelegramConfig(String instance, String id)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TelegramConfig");
		fields.put("CMD_CODE"			, "R");
		fields.put("ID"					, id);
		fields.put(IPC.NB_INSTNCE_ID	, instance);
		
		try {
			fields = ((TelegramConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			txtID.setText((String)fields.get("ID"));
			txtName.setText((String)fields.get("NAME"));
			txtHeader.setText((String)fields.get("HDR"));
			//cmbLogging.setText((String)fields.get("LGNG"));
			//switch(((String)fields.get("LGNG")).toCharArray()[0])
			//{
			//case 'A': cmbLogging.setText("ALL");
			//	break;
			//case 'H': cmbLogging.setText("Header only");
			//	break;
			//case 'N': cmbLogging.setText("None");
			//	break;
			//}
			txtRemark.setText((String)fields.get("RMK"));
			
			((Field)fieldTable.getInput()).getChildren().removeAll(((Field)fieldTable.getInput()).getChildren());
			fieldTable.refresh();
			
			ArrayList<NBFields> fieldList = (ArrayList<NBFields>)fields.get("FLD");
			if(fieldList != null)
			{
				Field root = (Field)fieldTable.getInput();
				
				for(int i=0; i<fieldList.size(); i++)
				{
					Field field = new Field(root, (String)fieldList.get(i).get("ID"), display);
					field.setFieldName((String)fieldList.get(i).get("NAME"));
					field.setFieldLength((Integer)fieldList.get(i).get("LENGTH"));
					field.setFieldMandatory(((String)fieldList.get(i).get("MANDATORY")).equals("true"));
					field.setFieldPadding(((String)fieldList.get(i).get("PADDING")));
					field.setFieldRemark((String)fieldList.get(i).get("REMARK"));
					
					switch(((String)fieldList.get(i).get("ALIGN")).toCharArray()[0])
					{
					case 'L': field.setFieldAlign(0);
						break;
					case 'C': field.setFieldAlign(1);
						break;
					case 'R': field.setFieldAlign(2);
						break;
					}
					
					switch(((String)fieldList.get(i).get("TYPE")).toCharArray()[0])
					{
					case 'C': field.setFieldType(0);
						break;
					case 'N': field.setFieldType(1);
						break;
					case 'I': field.setFieldType(2);
						break;
					case 'D': field.setFieldType(3);
						break;
					case 'F': field.setFieldType(4);
						break;
					case 'L': field.setFieldType(5);
						break;
					case 'B': field.setFieldType(6);
						break;
					case 'A': field.setFieldType(7);
						break;
					}
				}
				
				fieldTable.refresh();
			}
			
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
	private void removeTelegram()
	{
		IStructuredSelection selection = (IStructuredSelection)telegramTable.getSelection();
		
		if(selection.isEmpty()) return;
		
		if(selection.getFirstElement() instanceof Telegram)
		{
			Telegram telegram = ((Telegram)selection.getFirstElement());

			if(!((TelegramConfigEditorInput)getEditorInput()).getName().equals(telegram.getInstanceName()))
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
			tmp.put("ID", telegram.getTelegramID());
			delList.add(tmp);
			
			sendMsg.put("DEL_LST", delList);
			
			telegram.remove();
			
			telegramTable.refresh();
			
			isModified = true;
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void searchTelegramList()
	{
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
			fields = ((TelegramConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
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
	
	@SuppressWarnings("unchecked")
	private void searchHeader()
	{
		if(txtHdrSch.getText().trim().equals(""))
			return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TelegramConfig");
		fields.put("CMD_CODE"			, "L");
		fields.put("SCH"				, txtHdrSch.getText().trim());
		fields.put("SCH_TYPE"			, "SCH_ID");
		fields.put(IPC.NB_INSTNCE_ID	, ((TelegramConfigEditorInput)getEditorInput()).getName());
		
		try {
			fields = ((TelegramConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			((TableModel)headerSchTable.getInput()).getChildren().removeAll(((TableModel)headerSchTable.getInput()).getChildren());
			headerSchTable.refresh();

			ArrayList<NBFields> userList = (ArrayList<NBFields>)fields.get("TLGM_LST");
			
			if(userList != null)
			{
				for(int i=0; i<userList.size(); i++)
				{
					NBFields tmp = userList.get(i);
					
					Header header = new Header(((TableModel)headerSchTable.getInput()), (String)tmp.get("ID"), display);
					header.setHeaderName((String)tmp.get("NAME"));
					header.setHeaderRemark((String)tmp.get("RMK"));
				}
	
				headerSchTable.refresh();
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

				if(e.widget == btnTelegramSch)
				{
					searchTelegramList();
				}
				else if(e.widget == btnTelegramAdd)
				{
					txtID.setEnabled(true);
					txtID.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					txtID.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
					
					txtID.setText("");
					txtName.setText("");
					txtHeader.setText("");
					//cmbLogging.setText("ALL");
					//cmbLogging.setText("None");
					txtRemark.setText("");
					txtHdrSch.setText("");
					
					((Field)fieldTable.getInput()).getChildren().removeAll(((Field)fieldTable.getInput()).getChildren());
					fieldTable.refresh();
					
					((TableModel)headerSchTable.getInput()).getChildren().removeAll(((TableModel)headerSchTable.getInput()).getChildren());
					headerSchTable.refresh();
					
					sendMsg.put("ACT_FLG", "I");
					
					isModified = false;
					firePropertyChange(PROP_DIRTY);
					
					tabFolder.setSelection(1);
				}
				else if(e.widget == btnTelegramDelete)
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) == SWT.CANCEL)
					{
						return;
					}
					
					removeTelegram();
				}
				else if(e.widget == btnHdrSch)
				{
					searchHeader();
				}
				else if(e.widget == btnFldAdd)
				{
					Field root = (Field)fieldTable.getInput();

					new Field(root, "New", display);
					fieldTable.refresh();
					isModified = true;
					firePropertyChange(PROP_DIRTY);
				}
				else if(e.widget == btnFldDelete)
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) == SWT.CANCEL)
					{
						return;
					}
					
					IStructuredSelection selection = (IStructuredSelection)fieldTable.getSelection();
					
					if(selection.isEmpty()) return;
					
					if(selection.getFirstElement() instanceof Field)
					{
						Field field = ((Field)selection.getFirstElement());
						
						field.remove();
						
						fieldTable.refresh();
						isModified = true;
						firePropertyChange(PROP_DIRTY);
					}
				}
				//else if(e.widget == importConfig)
				//{
				//	importConfigFile();
				//}
				//else if(e.widget == exportConfig)
				//{
				//	exportConfigFile();
				//}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
	}
	
	//private void exportConfigFile()
	//{
		/*if(txtID.isEnabled() || txtID.getText().equals(""))
		{
			IMessageBox.Warning(shell, NBLabel.get(0x0258));
			return;
		}
		
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFileName(txtID.getText());
		dialog.setFilterExtensions(new String[] {"*.nbfd"});
		dialog.setFilterNames(new String[] {"Nabee backup file"});
		String fileSelected = dialog.open();
		
		if(fileSelected == null) return;

		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TelegramConfig");
		fields.put("CMD_CODE"			, "R");
		fields.put("ID"					, txtID.getText());
		fields.put(IPC.NB_INSTNCE_ID	, ((TelegramConfigEditorInput)getEditorInput()).getName());
		
		try {
			fields = ((TelegramConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			TelegramFactor telegramFactor = new TelegramFactor();
			telegramFactor.put("TLGM_ID", txtID.getText());
			telegramFactor.NBsetTelegramHeaderName((String)fields.get("HDR"));
			telegramFactor.NBsetTelegramLogLevel(((String)fields.get("LGNG")).toCharArray()[0]);
			telegramFactor.NBsetTelegramName((String)fields.get("NAME"));
			telegramFactor.NBsetTelegramRemark((String)fields.get("RMK"));
			
			
			ArrayList<NBFields> fieldList = (ArrayList<NBFields>)fields.get("FLD");
			
			if(fieldList != null)
			{
				ElFieldDefinition fieldDefinition = new ElFieldDefinition(fieldList.size());
					for(int i=0; i<fieldList.size(); i++)
				{
					fieldDefinition.NBsetAlign(i		, ((String)fieldList.get(i).get("ALIGN")).toCharArray()[0]);
					fieldDefinition.NBsetDataLength(i	, (Integer)fieldList.get(i).get("LENGTH"));
					fieldDefinition.NBsetDataType(i		, ((String)fieldList.get(i).get("TYPE")).toCharArray()[0]);
					fieldDefinition.NBsetFieldID(i		, fieldList.get(i).get("ID")==null?"":(String)fieldList.get(i).get("ID"));
					fieldDefinition.NBsetFieldName(i	, fieldList.get(i).get("NAME")==null?"":(String)fieldList.get(i).get("NAME"));
					fieldDefinition.NBsetMandatory(i	, ((String)fieldList.get(i).get("MANDATORY")).equals("true"));
					fieldDefinition.NBsetPaddingChar(i	, ((String)fieldList.get(i).get("PADDING")).length()==0?' ':((String)fieldList.get(i).get("PADDING")).toCharArray()[0]);
					fieldDefinition.NBsetRemark(i		, fieldList.get(i).get("REMARK")==null?"":(String)fieldList.get(i).get("REMARK"));
				}
				
				telegramFactor.NBsetField(fieldDefinition);
			}
			
			ObjectFileIO objIO = new ObjectFileIO();
			objIO.saveObject(fileSelected, telegramFactor);
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
		} catch (KeyException e) {
			IMessageBox.Error(shell, e.getMessage());
		} catch (IOException e) {
			IMessageBox.Error(shell, e.getMessage());
		}*/
	//}
	
	//private void importConfigFile()
	//{
		/*FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] {"*.nbfd"});
		dialog.setFilterNames(new String[] {"Nabee backup file"});
		String fileSelected = dialog.open();
		
		if(fileSelected == null) return;
		
		try {
			TelegramFactor telegramFactor = null;
			if(fileSelected != null)
			{
				ObjectFileIO objIO = new ObjectFileIO();
				telegramFactor = (TelegramFactor)objIO.recoverObject(fileSelected);
			}
			
			if(telegramFactor == null)
			{
				IMessageBox.Error(shell, NBLabel.get(0x0259));
				return;
			}
			
			txtID.setText((String)telegramFactor.get("TLGM_ID"));
			txtName.setText(telegramFactor.NBgetTelegramName());
			txtHeader.setText(telegramFactor.NBgetTelegramHeaderName());
			switch(telegramFactor.NBgetTelegramLogLevel())
			{
			case 'A': cmbLogging.setText("ALL");
				break;
			case 'H': cmbLogging.setText("Header only");
				break;
			case 'N': cmbLogging.setText("None");
				break;
			}
			txtRemark.setText(telegramFactor.NBgetTelegramRemark());
			
			((Field)fieldTable.getInput()).getChildren().removeAll(((Field)fieldTable.getInput()).getChildren());
			fieldTable.refresh();
			
			ElFieldDefinition fieldDefinition = telegramFactor.NBgetField();
			if(fieldDefinition != null)
			{
				Field root = (Field)fieldTable.getInput();
				
				for(int i=0; i<fieldDefinition.size(); i++)
				{
					Field field = new Field(root, fieldDefinition.NBgetFieldID(i), display);
					field.setFieldName(fieldDefinition.NBgetFieldName(i));
					field.setFieldLength(fieldDefinition.NBgetDataLength(i));
					field.setFieldMandatory(fieldDefinition.NBgetMandatory(i));
					field.setFieldPadding(String.valueOf(fieldDefinition.NBgetPaddingChar(i)));
					field.setFieldRemark(fieldDefinition.NBgetRemark(i));
					
					switch(fieldDefinition.NBgetAlign(i))
					{
					case 'L': field.setFieldAlign(0);
						break;
					case 'C': field.setFieldAlign(1);
						break;
					case 'R': field.setFieldAlign(2);
						break;
					}
					
					switch(fieldDefinition.NBgetDataType(i))
					{
					case 'C': field.setFieldType(0);
						break;
					case 'N': field.setFieldType(1);
						break;
					case 'I': field.setFieldType(2);
						break;
					case 'D': field.setFieldType(3);
						break;
					case 'F': field.setFieldType(4);
						break;
					case 'L': field.setFieldType(5);
						break;
					case 'B': field.setFieldType(6);
						break;
					case 'A': field.setFieldType(7);
						break;
					}
				}
				
				fieldTable.refresh();
			}
			
		} catch (IOException e) {
			IMessageBox.Error(shell, e.getMessage());
		} catch (ClassNotFoundException e) {
			IMessageBox.Error(shell, e.getMessage());
		}*/
	//}

	public void dispose()
	{
		if(!btnTelegramSch.isDisposed())
			btnTelegramSch.removeSelectionListener(btnSelection);
		if(!btnTelegramAdd.isDisposed())
			btnTelegramAdd.removeSelectionListener(btnSelection);
		if(!btnTelegramDelete.isDisposed())
			btnTelegramDelete.removeSelectionListener(btnSelection);
		if(!btnHdrSch.isDisposed())
			btnHdrSch.removeSelectionListener(btnSelection);
		if(!btnFldAdd.isDisposed())
			btnFldAdd.removeSelectionListener(btnSelection);
		if(!btnFldDelete.isDisposed())
			btnFldDelete.removeSelectionListener(btnSelection);
		//if(!importConfig.isDisposed())
		//	importConfig.removeSelectionListener(btnSelection);
		//if(!exportConfig.isDisposed())
		//	exportConfig.removeSelectionListener(btnSelection);
		
		headerSchTable.removeDoubleClickListener(dbclListener);
		telegramTable.removeDoubleClickListener(dbclListener);
		
		idEditingSupport.removeNBTableModifiedListener(nmListener);
		nameEditingSupport.removeNBTableModifiedListener(nmListener);
		typeEditingSupport.removeNBTableModifiedListener(nmListener);
		lengthEditingSupport.removeNBTableModifiedListener(nmListener);
		alignEditingSupport.removeNBTableModifiedListener(nmListener);
		mandatoryEditingSupport.removeNBTableModifiedListener(nmListener);
		paddingEditingSupport.removeNBTableModifiedListener(nmListener);
		remarkEditingSupport.removeNBTableModifiedListener(nmListener);
		
		
		super.dispose();
	}

	@Override
	public void setFocus() {
	}

}
