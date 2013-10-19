package com.nabsys.nabeeplus.editors;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.input.InstanceConfigEditorInput;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.views.model.AuthArray;
import com.nabsys.nabeeplus.views.model.Header;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.Plugin;
import com.nabsys.nabeeplus.views.model.PluginTypeArray;
import com.nabsys.nabeeplus.views.model.TableModel;
import com.nabsys.nabeeplus.views.model.User;
import com.nabsys.nabeeplus.views.model.UserAccess;
import com.nabsys.nabeeplus.widgets.BackBoard;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NCheckBox;
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

public class InstanceConfigEditor extends NabeeEditor {

	public final static String ID = "com.nabsys.nabeeplus.editors.instanceConfigEditor";
	private boolean 				isModified 				= false;
	private Display 				display					= null;
	private Shell					shell					= null;
	
	private SelectionListener 		btnSelection 			= null;
	private IDoubleClickListener	dbclListener			= null;
	
	private Button 					btnPlgSch				= null;
	private NStyledText 			txtPlgSch				= null;
	private NStyledText				txtHdrSch				= null;
	private Button					btnHdrSch				= null;
	private Button 					btnPlgRemove			= null;
	private Button 					btnUserSch				= null;
	private NStyledText 			txtUserSch				= null;
	private Button 					btnUserRemove			= null;
	private NStyledText				txtSysHdr				= null;
	private NCombo					txtLenFldHdr			= null;
	private NCombo					txtIDFldHdr				= null;
	
	
	private TableViewer				headerSchTable			= null;
	private TableViewer 			plgSchTable				= null; 
	private TableViewer 			plgLstTable				= null;
	private TableViewer 			userTable				= null; 
	private TableViewer 			accTable				= null;
	//private ToolItem 				importConfig 			= null;
	//private ToolItem 				exportConfig 			= null;
	private ArrayList<Object>		compositeList			= null;
	
	private HashMap<String, Object>	tmpSendMsg = null;
	
	public InstanceConfigEditor(){
		
	}
	
	@Override
	public void createPartControl(Composite parent) {
		this.display = parent.getDisplay();
		this.shell = parent.getShell();
		
		compositeList = new ArrayList<Object>();
		
		BackBoard board = new BackBoard(parent, SWT.NONE, new Point(880,950));
		board.setTitleIcon(Activator.getImageDescriptor("/icons/config_obj.gif").createImage(display), new Point(10, 8));
		board.setTitle(NBLabel.get(0x009F));
		
		GridLayout boardLayout = new GridLayout();
		boardLayout.marginWidth = 0;
		boardLayout.marginHeight = 0;
		board.setLayout(boardLayout);
		
		
		genButtonSelectionListener();
		tmpSendMsg = new HashMap<String, Object>();
		
		//exportConfig = board.addButton(NBLabel.get(0x0257), Activator.getImageDescriptor("/icons/exportconfig.gif").createImage(display));
		//importConfig = board.addButton(NBLabel.get(0x0256), Activator.getImageDescriptor("/icons/importconfig.gif").createImage(display));
		
		//exportConfig.addSelectionListener(btnSelection);
		//importConfig.addSelectionListener(btnSelection);
		
		////////////CONFIG FIELDS DIVIDER///////////////////////
		GridData dividerLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		SashForm divider = new SashForm(board.getPanel(), SWT.HORIZONTAL);
		divider.setLayoutData(dividerLayoutData);
		divider.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		GridLayout divideLayout = new GridLayout();
		divideLayout.verticalSpacing = 5;
		divideLayout.horizontalSpacing = 0;
		divideLayout.marginWidth = 10;
		divideLayout.marginHeight = 0;
		
		Composite firstField = new Composite(divider, SWT.NONE);
		firstField.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		firstField.setLayout(divideLayout);
		
		Composite secondField = new Composite(divider, SWT.NONE);
		secondField.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		secondField.setLayout(divideLayout);
		
		
		////////////CONFIG FIELDS DIVIDER///////////////////////
		
		setFirstField(firstField);
		setSecondField(secondField);
		
		/////////////////////////////////////////////////////////////


	}
	
	@SuppressWarnings("unchecked")
	private void setFirstField(Composite back)
	{
		NBModifiedListener mdfListener = new NBModifiedListener(){

			public void modified(String name, String value) {
				if(name.equals("SYS_HDR"))
				{
					NBFields hdrFields = new NBFields();
					hdrFields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.instance.InstanceConfig");
					hdrFields.put("CMD_CODE"		, "F");
					hdrFields.put("SYS_HDR"			, value);
					hdrFields.put(IPC.NB_INSTNCE_ID	, ((InstanceConfigEditorInput)getEditorInput()).getName());

					try {
						hdrFields = ((InstanceConfigEditorInput)getEditorInput()).getProtocol().execute(hdrFields);
						if((Integer)hdrFields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
						{
							IMessageBox.Error(shell, (String)hdrFields.get("RTN_MSG"));
							return;
						}

						ArrayList<NBFields> fieldList = (ArrayList<NBFields>)hdrFields.get("FLD_LST");
						txtLenFldHdr.removeAll();
						txtIDFldHdr.removeAll();
						for(int i=0; i<fieldList.size(); i++)
						{
							txtLenFldHdr.add((String)fieldList.get(i).get("HDR_FLD"));
							txtIDFldHdr.add((String)fieldList.get(i).get("HDR_FLD"));
							
							if(i == 0)
							{
								txtLenFldHdr.setText((String)fieldList.get(i).get("HDR_FLD"));
								txtIDFldHdr.setText((String)fieldList.get(i).get("HDR_FLD"));
								tmpSendMsg.put("LEN_FLD", (String)fieldList.get(0).get("HDR_FLD"));
								tmpSendMsg.put("ID_FLD", (String)fieldList.get(0).get("HDR_FLD"));
							}
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

				tmpSendMsg.put(name, value);

				isModified = true;
				firePropertyChange(PROP_DIRTY);
			}
		};
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 285;
		
		SubTitleBoard sysConfig = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x0125), NBLabel.get(0x0126), 1);
		sysConfig.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		FieldFrame sysFrame = new FieldFrame(sysConfig.getPanel(), SWT.NONE, 150);
		sysFrame.setLayoutData(layoutData);
		
		txtSysHdr 	= sysFrame.getTextField("SYS_HDR"					, NBLabel.get(0x018B));
		txtLenFldHdr= sysFrame.getComboField("LEN_FLD"					, NBLabel.get(0x020D));
		txtIDFldHdr	= sysFrame.getComboField("ID_FLD"					, NBLabel.get(0x020E));
		NCombo 		cmbLocale 			= sysFrame.getComboField("LOCALE"		, NBLabel.get(0x0111));
		NStyledText txtLogFile 			= sysFrame.getTextField("LOG_CFG_FILE"	, NBLabel.get(0x010B));
		NCheckBox 	chkTelegramCache	= sysFrame.getCheckField("TLGM_CACHE"	, NBLabel.get(0X019B), "use", "off");
		NCheckBox 	chkSQLCache 		= sysFrame.getCheckField("SQL_CACHE"	, NBLabel.get(0x0114), "use", "off");
		NCheckBox 	chkSVCCache 		= sysFrame.getCheckField("SVC_CACHE"	, NBLabel.get(0x0295), "use", "off");
		NCheckBox 	chkLoadStUp			= sysFrame.getCheckField("LOAD_STUP"	, NBLabel.get(0x018C), "start", "off");
		
		txtSysHdr.setEnabled(false);
		txtSysHdr.setBackground(new Color(display, 240, 240, 240));
		txtSysHdr.setForeground(new Color(display, 150, 150, 150));
		
		cmbLocale.add("KOREA");
		cmbLocale.add("CANADA");
		cmbLocale.add("CHINA");
		cmbLocale.add("UK");
		cmbLocale.add("FRANCE");
		cmbLocale.add("GERMAN");
		cmbLocale.add("ITALY");
		cmbLocale.add("JAPAN");
		cmbLocale.add("PRC");
		cmbLocale.add("TAIWAN");
		cmbLocale.add("US");
		cmbLocale.setText("KOREA");
		
		if(((InstanceConfigEditorInput)getEditorInput()).getFields().containsKey("FLD_LST"))
		{
			ArrayList<NBFields> fieldList = (ArrayList<NBFields>)((InstanceConfigEditorInput)getEditorInput()).getFields().get("FLD_LST");
			txtLenFldHdr.add("-Undefined-");
			txtIDFldHdr.add("-Undefined-");
			for(int i=0; i<fieldList.size(); i++)
			{
				txtLenFldHdr.add((String)fieldList.get(i).get("HDR_FLD"));
				txtIDFldHdr.add((String)fieldList.get(i).get("HDR_FLD"));
			}
			txtLenFldHdr.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("LEN_FLD"));
			txtIDFldHdr.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("ID_FLD"));
		}
		else
		{
			txtLenFldHdr.add("-Undefined-");
			txtIDFldHdr.add("-Undefined-");
		}
		
		if(((InstanceConfigEditorInput)getEditorInput()).getFields().containsKey("SYS_HDR"))
		{
			txtSysHdr.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("SYS_HDR"));
		}
		else
		{
			txtSysHdr.setText("");
		}
		
		if(!((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("LOCALE")).equals(""))
			cmbLocale.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("LOCALE"));
		txtLogFile.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("LOG_CFG_FILE"));
		chkTelegramCache.setSelection(((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("TLGM_CACHE")).equals("true"));
		chkSQLCache.setSelection(((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("SQL_CACHE")).equals("true"));
		chkSVCCache.setSelection(((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("SVC_CACHE")).equals("true"));
		chkLoadStUp.setSelection(((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("LOAD_STUP")).equals("true"));
		
		
		txtSysHdr.addNBModifiedListener(mdfListener);
		txtLenFldHdr.addNBModifiedListener(mdfListener);
		txtIDFldHdr.addNBModifiedListener(mdfListener);
		cmbLocale.addNBModifiedListener(mdfListener);
		txtLogFile.addNBModifiedListener(mdfListener);
		chkTelegramCache.addNBModifiedListener(mdfListener);
		chkSQLCache.addNBModifiedListener(mdfListener);
		chkSVCCache.addNBModifiedListener(mdfListener);
		chkLoadStUp.addNBModifiedListener(mdfListener);
		

		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 220;
		
		SubTitleBoard loadConfig = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x0127), NBLabel.get(0x0128), 1);
		loadConfig.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		FieldFrame loadFrame = new FieldFrame(loadConfig.getPanel(), SWT.NONE, 150);
		loadFrame.setLayoutData(layoutData);
		
		NStyledText txtJavaHome	= loadFrame.getTextField("JAVA_HOME"		, NBLabel.get(0x018D));
		NStyledText txtSysPath 	= loadFrame.getTextField("PATH"			, NBLabel.get(0x018E));
		NStyledText txtComPath	= loadFrame.getTextField("COMP_PATH"		, NBLabel.get(0x018F));
		NCombo 		cmbSysEncod	= loadFrame.getComboField("SYS_ENCODING"	, NBLabel.get(0x0190));
		NCombo 		cmbFileEncod= loadFrame.getComboField("FILE_ENCODING"	, NBLabel.get(0x0191));
		NStyledText txtArgs		= loadFrame.getTextField("ARGS"			, NBLabel.get(0x0192));
		
		cmbSysEncod.add("EUC-KR");
		cmbSysEncod.add("ISO-8859-1");
		cmbSysEncod.add("US-ASCII");
		cmbSysEncod.add("UTF-8");
		cmbSysEncod.add("UTF-16");
		cmbSysEncod.setText("EUC-KR");
		
		cmbFileEncod.add("EUC-KR");
		cmbFileEncod.add("ISO-8859-1");
		cmbFileEncod.add("US-ASCII");
		cmbFileEncod.add("UTF-8");
		cmbFileEncod.add("UTF-16");
		cmbFileEncod.setText("EUC-KR");
		
		txtJavaHome.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("JAVA_HOME"));
		txtSysPath.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("PATH"));
		txtComPath.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("COMP_PATH"));
		if(!((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("SYS_ENCODING")).equals(""))
			cmbSysEncod.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("SYS_ENCODING"));
		if(!((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("FILE_ENCODING")).equals(""))
			cmbFileEncod.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("FILE_ENCODING"));
		txtArgs.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("ARGS"));
		
		txtJavaHome.addNBModifiedListener(mdfListener);
		txtSysPath.addNBModifiedListener(mdfListener);
		txtComPath.addNBModifiedListener(mdfListener);
		cmbSysEncod.addNBModifiedListener(mdfListener);
		cmbFileEncod.addNBModifiedListener(mdfListener);
		txtArgs.addNBModifiedListener(mdfListener);
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 180;
		
		SubTitleBoard networkConfig = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x0160), NBLabel.get(0x0194), 1);
		networkConfig.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		FieldFrame networkFrame = new FieldFrame(networkConfig.getPanel(), SWT.NONE, 150);
		networkFrame.setLayoutData(layoutData);
	
		NStyledText txtPort 		= networkFrame.getTextField("PORT"			, NBLabel.get(0x007E));
		NStyledText txtBuffSize 	= networkFrame.getTextField("BUFF_SIZE"		, NBLabel.get(0x0119));
		NStyledText txtTimeOut		= networkFrame.getTextField("TIME_OUT"		, NBLabel.get(0x011A));
		NStyledText txtCltNum		= networkFrame.getTextField("MAX_CLT_NUM"		, NBLabel.get(0x0117));
		NCombo 		cmbRemoteEnc	= networkFrame.getComboField("REMOTE_ENCODING", NBLabel.get(0x0112));
		
		txtPort.setNumberStyle(true);
		txtBuffSize.setNumberStyle(true);
		txtTimeOut.setNumberStyle(true);
		txtCltNum.setNumberStyle(true);
		
		cmbRemoteEnc.add("EUC-KR");
		cmbRemoteEnc.add("ISO-8859-1");
		cmbRemoteEnc.add("US-ASCII");
		cmbRemoteEnc.add("UTF-8");
		cmbRemoteEnc.add("UTF-16");
		cmbRemoteEnc.setText("EUC-KR");
		
		
		txtPort.setText(Integer.toString((Integer)((InstanceConfigEditorInput)getEditorInput()).getFields().get("PORT")));
		txtBuffSize.setText(Integer.toString((Integer)((InstanceConfigEditorInput)getEditorInput()).getFields().get("BUFF_SIZE")));
		txtTimeOut.setText(Integer.toString((Integer)((InstanceConfigEditorInput)getEditorInput()).getFields().get("TIME_OUT")));
		txtCltNum.setText(Integer.toString((Integer)((InstanceConfigEditorInput)getEditorInput()).getFields().get("MAX_CLT_NUM")));
		if(!((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("REMOTE_ENCODING")).equals(""))
			cmbRemoteEnc.setText((String)((InstanceConfigEditorInput)getEditorInput()).getFields().get("REMOTE_ENCODING"));
		
		txtPort.addNBModifiedListener(mdfListener);
		txtBuffSize.addNBModifiedListener(mdfListener);
		txtTimeOut.addNBModifiedListener(mdfListener);
		txtCltNum.addNBModifiedListener(mdfListener);
		cmbRemoteEnc.addNBModifiedListener(mdfListener);
	
		compositeList.add(txtSysHdr);
		compositeList.add(txtLenFldHdr);
		compositeList.add(txtIDFldHdr);
		compositeList.add(cmbLocale);
		compositeList.add(txtLogFile);
		compositeList.add(chkTelegramCache);
		compositeList.add(chkSQLCache);
		compositeList.add(chkSVCCache);
		compositeList.add(chkLoadStUp);
		compositeList.add(txtJavaHome);
		compositeList.add(txtSysPath);
		compositeList.add(txtComPath);
		compositeList.add(cmbSysEncod);
		compositeList.add(cmbFileEncod);
		compositeList.add(txtArgs);
		compositeList.add(txtPort);
		compositeList.add(txtBuffSize);
		compositeList.add(txtTimeOut);
		compositeList.add(txtCltNum);
		compositeList.add(cmbRemoteEnc);

	}
	
	private void setSecondField(Composite back)
	{
		dbclListener = new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event) {
				if(event.getSelection() instanceof IStructuredSelection) 
				{

					IStructuredSelection selection = (IStructuredSelection)event.getSelection();

					if(selection.getFirstElement() instanceof UserAccess)
					{
						UserAccess schUser = ((UserAccess)selection.getFirstElement());
						addUser(schUser);
					}
					else if(selection.getFirstElement() instanceof Plugin)
					{
						Plugin plugin = ((Plugin)selection.getFirstElement());
						addPlugin(plugin);
					}
					else if(selection.getFirstElement() instanceof Header)
					{
						Header header = ((Header)selection.getFirstElement());
						txtSysHdr.setText(header.getHeaderID());
					}
				}
			}
		};
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 5;
		
		SubTitleBoard schHeader = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x014C), NBLabel.get(0x014D), 1);
		schHeader.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		schHeader.getPanel().setLayout(gridLayout);
		
		setHeaderSearch(schHeader.getPanel());
		
		SubTitleBoard plgConfig = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x017D), NBLabel.get(0x0196), 1);
		plgConfig.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		plgConfig.getPanel().setLayout(gridLayout);
		
		setPluginSch(plgConfig.getPanel());
		setPluginList(plgConfig.getPanel());
		
		SubTitleBoard accConfig = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x0197), NBLabel.get(0x0198), 1);
		accConfig.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		accConfig.getPanel().setLayout(gridLayout);
		
		setSearchUser(accConfig.getPanel());
		setAccessList(accConfig.getPanel());
	}
	
	private void setHeaderSearch(Composite back)
	{
		//////////////////////////////����ȸ////////////////////////
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 33;
		
		Composite btnBack = new Composite(back, SWT.NONE);
		btnBack.setLayoutData(layoutData);
		btnBack.setLayout(gridLayout);
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
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 80;
		
		headerSchTable = new TableViewer(back, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		headerSchTable.getTable().setLayoutData(layoutData);
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
	}
	
	private void setPluginSch(Composite back)
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 33;
		
		Composite btnBack = new Composite(back, SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayout(gridLayout);
		btnBack.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 100;
		layoutData.heightHint = 15;
		txtPlgSch = new NStyledText(btnBack, SWT.BORDER | SWT.SINGLE);
		txtPlgSch.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 70;
		layoutData.heightHint = 18;
		btnPlgSch = new Button(btnBack, SWT.NONE);
		btnPlgSch.setText(NBLabel.get(0x0121));
		btnPlgSch.setLayoutData(layoutData);
		btnPlgSch.addSelectionListener(btnSelection);
		
		txtPlgSch.setDefaultButton(btnPlgSch);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 80;
		
		plgSchTable = new TableViewer(back, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		plgSchTable.getTable().setLayoutData(layoutData);
		plgSchTable.addDoubleClickListener(dbclListener);
		plgSchTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(plgSchTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x017F) + "                   ");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(plgSchTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0180));
		column.getColumn().setWidth(180);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(plgSchTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0181));
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		


		
		plgSchTable.getTable().setHeaderVisible(true);
		plgSchTable.getTable().setLinesVisible(true);
		
		plgSchTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		plgSchTable.getTable().getColumn(0).pack();

		plgSchTable.setContentProvider(new NBTableContentProvider());
		plgSchTable.setLabelProvider(new NBTableLabelProvider(display));
		
		Plugin root = new Plugin();
		plgSchTable.setInput(root);
	}
	
	@SuppressWarnings("unchecked")
	private void setPluginList(Composite back)
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 33;
		
		Composite btnBack = new Composite(back, SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayout(gridLayout);
		btnBack.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 70;
		layoutData.heightHint = 18;
		
		btnPlgRemove = new Button(btnBack, SWT.NONE);
		btnPlgRemove.setText(NBLabel.get(0x0120));
		btnPlgRemove.setLayoutData(layoutData);
		btnPlgRemove.addSelectionListener(btnSelection);

		
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 80;
		
		plgLstTable = new TableViewer(back, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		plgLstTable.getTable().setLayoutData(layoutData);
		plgLstTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(plgLstTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x017F) + "                   ");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(plgLstTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0180));
		column.getColumn().setWidth(180);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(plgLstTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0181));
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		


		
		plgLstTable.getTable().setHeaderVisible(true);
		plgLstTable.getTable().setLinesVisible(true);
		
		plgLstTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		plgLstTable.getTable().getColumn(0).pack();

		plgLstTable.setContentProvider(new NBTableContentProvider());
		plgLstTable.setLabelProvider(new NBTableLabelProvider(display));
		
		Plugin root = new Plugin();
		plgLstTable.setInput(root);
		
		ArrayList<NBFields> plgList = (ArrayList<NBFields>)((InstanceConfigEditorInput)getEditorInput()).getFields().get("PLG_LST");

		for(int i=0; i<plgList.size(); i++)
		{
			Plugin plugin = new Plugin(root, (String)plgList.get(i).get("ID"));
			plugin.setPluginName((String)plgList.get(i).get("NAME"));
			plugin.setPluginType(PluginTypeArray.getType((String)plgList.get(i).get("TYPE")));
			plugin.setModified(false);
		}
		
		plgLstTable.refresh();
	}
	
	private void setSearchUser(Composite back)
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 33;
		
		Composite btnBack = new Composite(back, SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayout(gridLayout);
		btnBack.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 100;
		layoutData.heightHint = 15;
		txtUserSch = new NStyledText(btnBack, SWT.BORDER | SWT.SINGLE);
		txtUserSch.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 70;
		layoutData.heightHint = 18;
		btnUserSch = new Button(btnBack, SWT.NONE);
		btnUserSch.setText(NBLabel.get(0x0121));
		btnUserSch.setLayoutData(layoutData);
		btnUserSch.addSelectionListener(btnSelection);
		
		txtUserSch.setDefaultButton(btnUserSch);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 80;
		
		userTable = new TableViewer(back, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		userTable.getTable().setLayoutData(layoutData);
		userTable.addDoubleClickListener(dbclListener);
		userTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0185) + "                   ");
		column.getColumn().setWidth(130);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0187));
		column.getColumn().setWidth(120);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0188));
		column.getColumn().setWidth(120);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0189));
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		userTable.getTable().setHeaderVisible(true);
		userTable.getTable().setLinesVisible(true);
		
		userTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		userTable.getTable().getColumn(0).pack();

		userTable.setContentProvider(new NBTableContentProvider());
		userTable.setLabelProvider(new NBTableLabelProvider(display));
		
		User root = new User();
		userTable.setInput(root);
	}
	
	@SuppressWarnings("unchecked")
	private void setAccessList(Composite back)
	{
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 33;
		
		Composite btnBack = new Composite(back, SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayout(gridLayout);
		btnBack.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 70;
		layoutData.heightHint = 18;
		
		btnUserRemove = new Button(btnBack, SWT.NONE);
		btnUserRemove.setText(NBLabel.get(0x0120));
		btnUserRemove.setLayoutData(layoutData);
		btnUserRemove.addSelectionListener(btnSelection);
		
		
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 80;
		
		accTable = new TableViewer(back, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		accTable.getTable().setLayoutData(layoutData);
		accTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(accTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0185) + "                   ");
		column.getColumn().setWidth(130);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(accTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0187));
		column.getColumn().setWidth(120);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		column = new TableViewerColumn(accTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0188));
		column.getColumn().setWidth(120);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(accTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0189));
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		
		accTable.getTable().setHeaderVisible(true);
		accTable.getTable().setLinesVisible(true);
		
		accTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		accTable.getTable().getColumn(0).pack();

		accTable.setContentProvider(new NBTableContentProvider());
		accTable.setLabelProvider(new NBTableLabelProvider(display));
		
		UserAccess root = new UserAccess();
		accTable.setInput(root);
		

		ArrayList<NBFields> accList = (ArrayList<NBFields>)((InstanceConfigEditorInput)getEditorInput()).getFields().get("ACC_LST");

		for(int i=0; i<accList.size(); i++)
		{
			UserAccess userAccess = new UserAccess(root, (String)accList.get(i).get("ID"));
			userAccess.setAuth(AuthArray.getAuth((String)accList.get(i).get("ROLE")));
			userAccess.setName((String)accList.get(i).get("NAME"));
			userAccess.setPhone((String)accList.get(i).get("PHONE"));
			userAccess.setModified(false);
		}
		
		accTable.refresh();
	}
	
	private void genButtonSelectionListener()
	{
		btnSelection = new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(e.widget == btnPlgSch)
				{
					searchPlugins();
				}
				else if(e.widget == btnPlgRemove)
				{
					deletePlugins();
				}
				else if(e.widget == btnUserSch)
				{
					searchUser();
				}
				else if(e.widget == btnUserRemove)
				{
					deleteUser();
				}
				else if(e.widget == btnHdrSch)
				{
					searchHeader();
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
	
	/*private void exportConfigFile()
	{
	}
	
	private void importConfigFile()
	{
	}*/
	
	@SuppressWarnings("unchecked")
	private void addPlugin(Plugin schPlugin)
	{
		Plugin plugin = new Plugin(((TableModel)plgLstTable.getInput()), schPlugin.getID());
		plugin.setPluginName(schPlugin.getPluginName());
		plugin.setPluginType(PluginTypeArray.getType(schPlugin.getPluginType()));
		plugin.setModified(true);
		
		//////////////SEND MESSAGE///////////////
		HashMap<String, String> tmpAct = null;
		if(tmpSendMsg.containsKey("__" + schPlugin.getID()))
		{
			tmpAct = (HashMap<String, String>)tmpSendMsg.get("__" + schPlugin.getID());
			if(tmpAct.get("ACT").equals("I"))
			{
				plugin.getParent().getChildren().remove(plugin);
				return;
			}
			else
			{
				tmpSendMsg.remove("__" + schPlugin.getID());
			}
		}
		else
		{
			tmpAct = new HashMap<String, String>();
			
			tmpAct.put("ACT", "I");
			tmpAct.put("CTG", "PLG");
			tmpSendMsg.put("__" + schPlugin.getID(), tmpAct);
		}
		//////////////SEND MESSAGE///////////////
		
		plgLstTable.refresh();
		
		isModified = true;
		firePropertyChange(PROP_DIRTY);
	}
	
	@SuppressWarnings("unchecked")
	private void addUser(UserAccess schUser)
	{
		UserAccess userAccess = new UserAccess(((TableModel)accTable.getInput()), schUser.getID());
		userAccess.setAuth(AuthArray.getAuth(schUser.getAuth()));
		userAccess.setName(schUser.getName());
		userAccess.setPhone(schUser.getPhone());
		userAccess.setModified(true);
		
		//////////////SEND MESSAGE///////////////
		HashMap<String, String> tmpAct = null;
		if(tmpSendMsg.containsKey("__" + schUser.getID()))
		{
			tmpAct = (HashMap<String, String>)tmpSendMsg.get("__" + schUser.getID());
			if(tmpAct.get("ACT").equals("I"))
			{
				userAccess.getParent().getChildren().remove(userAccess);
				return;
			}
			else
			{
				tmpSendMsg.remove("__" + schUser.getID());
			}
		}
		else
		{
			tmpAct = new HashMap<String, String>();
			
			tmpAct.put("ACT", "I");
			tmpAct.put("CTG", "USR");
			tmpSendMsg.put("__" + schUser.getID(), tmpAct);
		}
		//////////////SEND MESSAGE///////////////
		
		accTable.refresh();
		
		isModified = true;
		firePropertyChange(PROP_DIRTY);
	}
	
	@SuppressWarnings("unchecked")
	private void deleteUser()
	{
		IStructuredSelection selection = (IStructuredSelection)accTable.getSelection();
		
		if(selection.isEmpty()) return;
		
		if(selection.getFirstElement() instanceof UserAccess)
		{
			UserAccess userAccess = ((UserAccess)selection.getFirstElement());
			
			userAccess.remove();
			
			accTable.refresh();
			
			//////////////SEND MESSAGE///////////////
			HashMap<String, String> tmpAct = null;
			if(tmpSendMsg.containsKey("__" + userAccess.getID()))
			{
				tmpAct = (HashMap<String, String>)tmpSendMsg.get("__" + userAccess.getID());
				if(tmpAct.get("ACT").equals("I"))
				{
					tmpSendMsg.remove("__" + userAccess.getID());
				}
			}
			else
			{
				tmpAct = new HashMap<String, String>();
				
				tmpAct.put("ACT", "D");
				tmpAct.put("CTG", "USR");
				tmpSendMsg.put("__" + userAccess.getID(), tmpAct);
			}
			//////////////SEND MESSAGE///////////////
			
			
			isModified = true;
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deletePlugins()
	{
		IStructuredSelection selection = (IStructuredSelection)plgLstTable.getSelection();
		
		if(selection.isEmpty()) return;
		
		if(selection.getFirstElement() instanceof Plugin)
		{
			Plugin plugin = ((Plugin)selection.getFirstElement());
			
			plugin.remove();
			
			plgLstTable.refresh();
			
			//////////////SEND MESSAGE///////////////
			HashMap<String, String> tmpAct = null;
			if(tmpSendMsg.containsKey("__" + plugin.getID()))
			{
				tmpAct = (HashMap<String, String>)tmpSendMsg.get("__" + plugin.getID());
				if(tmpAct.get("ACT").equals("I"))
				{
					tmpSendMsg.remove("__" + plugin.getID());
				}
			}
			else
			{
				tmpAct = new HashMap<String, String>();
				
				tmpAct.put("ACT", "D");
				tmpAct.put("CTG", "PLG");
				tmpSendMsg.put("__" + plugin.getID(), tmpAct);
			}
			//////////////SEND MESSAGE///////////////
			
			
			isModified = true;
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	private void searchHeader()
	{
		if(txtHdrSch.getText().trim().equals(""))
			return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TelegramConfig");
		fields.put("CMD_CODE"			, "L");
		fields.put("SCH"				, txtHdrSch.getText().trim());
		fields.put("SCH_TYPE"			, "SCH_ID");
		fields.put(IPC.NB_INSTNCE_ID	, ((InstanceConfigEditorInput)getEditorInput()).getName());
		
		try {
			fields = ((InstanceConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			((TableModel)headerSchTable.getInput()).getChildren().removeAll(((TableModel)headerSchTable.getInput()).getChildren());
			headerSchTable.refresh();

			@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	private void searchPlugins()
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.server.ServerConfig");
		fields.put("CMD_CODE", "P");
		fields.put("SCH", txtPlgSch.getText().trim());
		
		try {
			fields = ((InstanceConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			((TableModel)plgSchTable.getInput()).getChildren().removeAll(((TableModel)plgSchTable.getInput()).getChildren());
			plgSchTable.refresh();

			ArrayList<NBFields> pluginList = (ArrayList<NBFields>)fields.get("PLG_LST");
			
			if(pluginList != null)
			{
				for(int i=0; i<pluginList.size(); i++)
				{
					NBFields tmp = pluginList.get(i);
					
					Plugin plugin = new Plugin(((TableModel)plgSchTable.getInput()), (String)tmp.get("ID"), display);
					plugin.setPluginName((String)tmp.get("NAME"));
					plugin.setPluginType(PluginTypeArray.getType((String)tmp.get("TYPE")));
				}
	
				plgSchTable.refresh();
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
	private void searchUser()
	{

		if(txtUserSch.getText().trim().equals(""))
		{
			IMessageBox.Info(shell, NBLabel.get(0x0195));
			return;
		}
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.user.UserConfig");
		fields.put("CMD_CODE", "R");
		fields.put("SCH", txtUserSch.getText().trim());
		
		try {
			fields = ((InstanceConfigEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			((TableModel)userTable.getInput()).getChildren().removeAll(((TableModel)userTable.getInput()).getChildren());
			userTable.refresh();

			ArrayList<NBFields> userList = (ArrayList<NBFields>)fields.get("USR_LST");
			
			if(userList != null)
			{
				for(int i=0; i<userList.size(); i++)
				{
					NBFields tmp = userList.get(i);
					
					if(((String)tmp.get("ROLE")).equals("Admin")) continue;
					
					UserAccess userAcc = new UserAccess(((TableModel)userTable.getInput()), (String)tmp.get("ID"), display);
					userAcc.setAuth(AuthArray.getAuth((String)tmp.get("ROLE")));
					userAcc.setName((String)tmp.get("NAME"));
					userAcc.setPhone((String)tmp.get("PHONE"));
				}
	
				userTable.refresh();
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
		if(!btnUserSch.isDisposed())
			btnUserSch.removeSelectionListener(btnSelection);
		if(!btnUserRemove.isDisposed())
			btnUserRemove.removeSelectionListener(btnSelection);
		if(!btnPlgSch.isDisposed())
			btnPlgSch.removeSelectionListener(btnSelection);
		if(!btnPlgRemove.isDisposed())
			btnPlgRemove.removeSelectionListener(btnSelection);
		//if(!importConfig.isDisposed())
		//	importConfig.removeSelectionListener(btnSelection);
		//if(!exportConfig.isDisposed())
		//	exportConfig.removeSelectionListener(btnSelection);
		
		userTable.removeDoubleClickListener(dbclListener);
		plgLstTable.removeDoubleClickListener(dbclListener);
		
		super.dispose();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doSave(IProgressMonitor monitor) {
		NBFields sendMsg = new NBFields();
		
		if(tmpSendMsg.containsKey("LEN_FLD") && tmpSendMsg.containsKey("ID_FLD"))
		{
			if(tmpSendMsg.get("LEN_FLD").equals(tmpSendMsg.get("ID_FLD")))
			{
				IMessageBox.Warning(shell, NBLabel.get(0x020F));
				return;
			}
		}
		
		if(tmpSendMsg.containsKey("SYS_HDR")) 			sendMsg.put("SYS_HDR"			, tmpSendMsg.get("SYS_HDR"));
		if(tmpSendMsg.containsKey("LEN_FLD")) 			sendMsg.put("LEN_FLD"			, tmpSendMsg.get("LEN_FLD"));
		if(tmpSendMsg.containsKey("ID_FLD")) 			sendMsg.put("ID_FLD"			, tmpSendMsg.get("ID_FLD"));
		if(tmpSendMsg.containsKey("LOCALE")) 			sendMsg.put("LOCALE"			, tmpSendMsg.get("LOCALE"));
		if(tmpSendMsg.containsKey("LOG_CFG_FILE")) 		sendMsg.put("LOG_CFG_FILE"		, tmpSendMsg.get("LOG_CFG_FILE"));
		if(tmpSendMsg.containsKey("TLGM_CACHE")) 		sendMsg.put("TLGM_CACHE"			, tmpSendMsg.get("TLGM_CACHE"));
		if(tmpSendMsg.containsKey("SQL_CACHE")) 		sendMsg.put("SQL_CACHE"			, tmpSendMsg.get("SQL_CACHE"));
		if(tmpSendMsg.containsKey("SVC_CACHE")) 		sendMsg.put("SVC_CACHE"			, tmpSendMsg.get("SVC_CACHE"));
		if(tmpSendMsg.containsKey("LOAD_STUP")) 		sendMsg.put("LOAD_STUP"			, tmpSendMsg.get("LOAD_STUP"));
		if(tmpSendMsg.containsKey("JAVA_HOME")) 		sendMsg.put("JAVA_HOME"			, tmpSendMsg.get("JAVA_HOME"));
		if(tmpSendMsg.containsKey("PATH")) 				sendMsg.put("PATH"				, tmpSendMsg.get("PATH"));
		if(tmpSendMsg.containsKey("COMP_PATH")) 		sendMsg.put("COMP_PATH"			, tmpSendMsg.get("COMP_PATH"));
		if(tmpSendMsg.containsKey("SYS_ENCODING")) 		sendMsg.put("SYS_ENCODING"		, tmpSendMsg.get("SYS_ENCODING"));
		if(tmpSendMsg.containsKey("FILE_ENCODING")) 	sendMsg.put("FILE_ENCODING"		, tmpSendMsg.get("FILE_ENCODING"));
		if(tmpSendMsg.containsKey("ARGS")) 				sendMsg.put("ARGS"				, tmpSendMsg.get("ARGS"));
		if(tmpSendMsg.containsKey("PORT")) 				sendMsg.put("PORT"				, Integer.parseInt((String)tmpSendMsg.get("PORT")));
		if(tmpSendMsg.containsKey("BUFF_SIZE")) 		sendMsg.put("BUFF_SIZE"			, Integer.parseInt((String)tmpSendMsg.get("BUFF_SIZE")));
		if(tmpSendMsg.containsKey("TIME_OUT")) 			sendMsg.put("TIME_OUT"			, Integer.parseInt((String)tmpSendMsg.get("TIME_OUT")));
		if(tmpSendMsg.containsKey("MAX_CLT_NUM")) 		sendMsg.put("MAX_CLT_NUM"		, Integer.parseInt((String)tmpSendMsg.get("MAX_CLT_NUM")));
		if(tmpSendMsg.containsKey("REMOTE_ENCODING")) 	sendMsg.put("REMOTE_ENCODING"	, tmpSendMsg.get("REMOTE_ENCODING"));
		
		ArrayList<NBFields> msgArray = new ArrayList<NBFields>();
		
		Set<String> keySet = tmpSendMsg.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext())
		{
			String keyString = itr.next();
			if(!keyString.substring(0, 2).equals("__")) continue;
			
			HashMap<String, String> tmpMap = (HashMap<String, String>)tmpSendMsg.get(keyString);
			
			NBFields tmpFields = new NBFields();
			
			tmpFields.put("ID"		, keyString.substring(2));
			tmpFields.put("ACT"		, tmpMap.get("ACT"));
			tmpFields.put("CTG"		, tmpMap.get("CTG"));

			msgArray.add(tmpFields);
		}
		
		sendMsg.put("ACT_LST"			, msgArray);
		sendMsg.put(IPC.NB_INSTNCE_ID	, ((InstanceConfigEditorInput)getEditorInput()).getName());
		
		tmpSendMsg = new HashMap<String, Object>();
		
		sendMsg.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.instance.InstanceConfig");
		sendMsg.put("CMD_CODE", "S");
		
		try {
			NBFields fields = ((InstanceConfigEditorInput)getEditorInput()).getProtocol().execute(sendMsg);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
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
		
		isModified = false;
		TableModel root = (TableModel)accTable.getInput();
		
		for(int i=0; i<root.getChildren().size(); i++)
		{
			if(root.getChildren().get(i).isModified()) root.getChildren().get(i).setModified(false);
		}
		
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName() + " " + ((InstanceConfigEditorInput)input).getWork());
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
		txtPlgSch.forceFocus();
	}

}
