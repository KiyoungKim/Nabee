package com.nabsys.nabeeplus.editors;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.input.ServerConfigEditorInput;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.listener.NBTableModifiedListener;
import com.nabsys.nabeeplus.views.model.NBEditingSupport;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.Param;
import com.nabsys.nabeeplus.views.model.Plugin;
import com.nabsys.nabeeplus.views.model.PluginTypeArray;
import com.nabsys.nabeeplus.views.model.Protocol;
import com.nabsys.nabeeplus.views.model.TableModel;
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

public class ServerConfigEditor extends NabeeEditor implements ISelectionListener{
	
	public final static String ID = "com.nabsys.nabeeplus.editors.serverConfigEditor";
	private Display 			display 			= null;
	private Shell				shell				= null;
	private boolean				isModified			= false;
	
	private NStyledText 		txtLicenseKey		= null;
	private NBEditingSupport 	idEditingSupport 	= null;
	private NBEditingSupport 	nameEditingSupport 	= null;
	private NBEditingSupport 	classEditingSupport = null;
	private NBEditingSupport 	pidEditingSupport 	= null;
	private NBEditingSupport 	pnameEditingSupport = null;
	private NBEditingSupport 	ptypeEditingSupport = null;
	private NBEditingSupport 	pgidEditingSupport 	= null;
	private NBEditingSupport 	pgvalueEditingSupport = null;
	
	private Protocol 			protocolRoot 		= null;
	private Plugin				pluginRoot			= null;
	private Param				paramRoot			= null;
	
	private TableViewer 		protocolTable		= null;
	private TableViewer 		pluginTable			= null;
	private TableViewer 		paramTable			= null;

	private SelectionListener 	btnSelection 		= null;
	private Button 				btnAddProtocol		= null;
	private Button 				btnDelProtocol		= null;
	private Button 				btnAddPlugin		= null;
	private Button 				btnDelPlugin		= null;
	private Button 				btnAddParam			= null;
	private Button 				btnDelParam			= null;
	//private ToolItem 			importConfig 		= null;
	//private ToolItem 			exportConfig 		= null;
	
	private HashMap<String, HashMap<String, String>> paramMap = null;
	private NBFields			sendMsg				= null;
	
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName() + " " + ((ServerConfigEditorInput)input).getWork());
	}

	@Override
	public boolean isDirty() {
		return  isModified;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void setSendMessage(String action, String category, String field, String key, String value, String pid)
	{
		//GNR
		//PCL
		//PLG
		//PRM
		if(category.equals("GNR"))
		{
			sendMsg.put(field, value);
		}
		else
		{
			ArrayList<NBFields> actList = null;

			if(sendMsg.containsKey("ACT_LST"))
			{
				actList = (ArrayList<NBFields>)sendMsg.get("ACT_LST");
			}
			else
			{
				actList = new ArrayList<NBFields>();
			}
			NBFields actMap = new NBFields();
			actMap.put("CTG", category);
			actMap.put("ACT", action);
			actMap.put("KEY", key);
			actMap.put("FLD", field);
			actMap.put("VLU", value);
			if(category.equals("PRM")) actMap.put("PID", paramTable.getData("PID"));
			
			actList.add(actMap);
			sendMsg.put("ACT_LST", actList);
		}
	}
	
	private void genButtonSelectionListener()
	{
		btnSelection = new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(e.widget == btnAddProtocol)
				{
					if(((Protocol)protocolTable.getInput()).getChildren().size() > 0 &&
							((Protocol)((Protocol)protocolTable.getInput()).getChildren().get(((Protocol)protocolTable.getInput()).getChildren().size() - 1)).getID().equals("New") ) return;
					
					new Protocol(protocolRoot, "New");
					protocolTable.refresh();
					
					setSendMessage("U", "PCL", "ID", "New", "New", null);
					setSendMessage("U", "PCL", "NAME", "New", "", null);
					setSendMessage("U", "PCL", "CLASS", "New", "", null);
					
					isModified = true;
					firePropertyChange(PROP_DIRTY);
				}
				else if(e.widget == btnDelProtocol) 
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) == SWT.CANCEL)
					{
						return;
					}
					
					IStructuredSelection selection = (IStructuredSelection)protocolTable.getSelection();
					
					if(selection.isEmpty()) return;
					
					Protocol protocol = ((Protocol)selection.getFirstElement());
					setSendMessage("D", "PCL", "", protocol.getID(), "", null);
					protocol.remove();
					protocolTable.refresh();
					
					isModified = true;
					firePropertyChange(PROP_DIRTY);
				}
				else if(e.widget == btnAddPlugin) 
				{
					if(((Plugin)pluginTable.getInput()).getChildren().size() > 0 &&
							((Plugin)((Plugin)pluginTable.getInput()).getChildren().get(((Plugin)pluginTable.getInput()).getChildren().size() - 1)).getID().equals("New") ) return;
					
					
					Plugin tmp = new Plugin(pluginRoot, "New");
					tmp.setPluginType(PluginTypeArray.OTHER);
					pluginTable.refresh();
					
					setSendMessage("U", "PLG", "ID", "New", "New", null);
					setSendMessage("U", "PLG", "NAME", "New", "", null);
					setSendMessage("U", "PLG", "TYPE", "New", "OTHER", null);
					
					isModified = true;
					firePropertyChange(PROP_DIRTY);
				}
				else if(e.widget == btnDelPlugin) 
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) == SWT.CANCEL)
					{
						return;
					}
					
					IStructuredSelection selection = (IStructuredSelection)pluginTable.getSelection();
					
					if(selection.isEmpty()) return;
					
					Plugin plugin = ((Plugin)selection.getFirstElement());
					setSendMessage("D", "PLG", "", plugin.getID(), "", null);
					plugin.remove();
					pluginTable.refresh();
					
					paramRoot.getChildren().removeAll(paramRoot.getChildren());
					paramTable.refresh();
					
					isModified = true;
					firePropertyChange(PROP_DIRTY);
				}
				else if(e.widget == btnAddParam) 
				{
					if(paramTable.getData("PID") == null || ((String)paramTable.getData("PID")).equals(""))
					{
						IMessageBox.Error(shell, NBLabel.get(0x0184));
						return;
					}
					
					if(((Param)paramTable.getInput()).getChildren().size() > 0 &&
							((Param)((Param)paramTable.getInput()).getChildren().get(((Param)paramTable.getInput()).getChildren().size() - 1)).getID().equals("New") ) return;
					
					
					new Param(paramRoot, "New");
					paramTable.refresh();
					
					setSendMessage("U", "PRM", "ID", "New", "New", (String)paramTable.getData("PID"));
					setSendMessage("U", "PRM", "VALUE", "New", "", (String)paramTable.getData("PID"));
					
					HashMap<String, String> tmpParam = paramMap.get((String)paramTable.getData("PID"));
					tmpParam.put("New", "");
					
					
					isModified = true;
					firePropertyChange(PROP_DIRTY);
				}
				else if(e.widget == btnDelParam) 
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) == SWT.CANCEL)
					{
						return;
					}
					
					IStructuredSelection selection = (IStructuredSelection)paramTable.getSelection();
					
					if(selection.isEmpty()) return;
					
					Param param = ((Param)selection.getFirstElement());
					setSendMessage("D", "PRM", "", param.getID(), "", (String)paramTable.getData("PID"));
					param.remove();
					paramTable.refresh();
					
					HashMap<String, String> tmpParam = paramMap.get((String)paramTable.getData("PID"));
					tmpParam.remove(param.getID());
					
					isModified = true;
					firePropertyChange(PROP_DIRTY);
				}
				/*else if(e.widget == importConfig)
				{
					importConfigFile();
				}
				else if(e.widget == exportConfig)
				{
					exportConfigFile();
				}*/
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		};
	}
	
	/*private void exportConfigFile()
	{
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] {"*.nbfs"});
		dialog.setFilterNames(new String[] {"Nabee backup file"});
		String fileSelected = dialog.open();
		
		if(fileSelected != null)
		{
			
		}
	}
	
	private void importConfigFile()
	{
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] {"*.nbfs"});
		dialog.setFilterNames(new String[] {"Nabee backup file"});
		String fileSelected = dialog.open();
		
		if(fileSelected == null) return;
	}*/
	
	@Override
	public void createPartControl(Composite parent) {
		this.display = parent.getDisplay();
		this.shell = parent.getShell();
		
		sendMsg = new NBFields();
		
		BackBoard board = new BackBoard(parent, SWT.NONE, new Point(880,680));
		board.setTitleIcon(Activator.getImageDescriptor("/icons/config_obj.gif").createImage(display), new Point(10, 8));
		board.setTitle(NBLabel.get(0x0106));
		
		GridLayout boardLayout = new GridLayout();
		boardLayout.marginWidth = 0;
		boardLayout.marginHeight = 0;
		board.setLayout(boardLayout);
		
		genButtonSelectionListener();
		
		//exportConfig = board.addButton(NBLabel.get(0x0257), Activator.getImageDescriptor("/icons/exportconfig.gif").createImage(display));
		//importConfig = board.addButton(NBLabel.get(0x0256), Activator.getImageDescriptor("/icons/importconfig.gif").createImage(display));
		
		//exportConfig.addSelectionListener(btnSelection);
		//importConfig.addSelectionListener(btnSelection);
		
		/////////////LICENSEKEY PLACE DUMMY/////////////////////
		GridData licenseKeyLayoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		licenseKeyLayoutData.heightHint = 33;
		
		FieldFrame licenseKeyFrame = new FieldFrame(board.getPanel(), SWT.NONE, 100);
		licenseKeyFrame.setLayoutData(licenseKeyLayoutData);
		
		txtLicenseKey = licenseKeyFrame.getTextField("LICENSE", NBLabel.get(0x0107));
		txtLicenseKey.setEditable(false);
		txtLicenseKey.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		txtLicenseKey.setText((String)((ServerConfigEditorInput)getEditorInput()).getFields().get("LICENSE"));
		
		/////////////LICENSEKEY PLACE DUMMY/////////////////////
		
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
	}
	
	private void setFirstField(Composite back)
	{
		NBModifiedListener mdfListener = new NBModifiedListener(){

			public void modified(String name, String value) {
				
				setSendMessage("", "GNR", name, "", value, null);
				
				isModified = true;
				firePropertyChange(PROP_DIRTY);
			}
		};
		
		txtLicenseKey.addNBModifiedListener(mdfListener);
		
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 200;
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 180;
		
		SubTitleBoard gnrConfig = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x010F), NBLabel.get(0x0110), 1);
		gnrConfig.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		FieldFrame grnFrame = new FieldFrame(gnrConfig.getPanel(), SWT.NONE, 150);
		grnFrame.setLayoutData(layoutData);
		
		NStyledText txtLog4j 		= grnFrame.getTextField("LOG4J_PATH", NBLabel.get(0x010B));
		txtLog4j.setText((String)((ServerConfigEditorInput)getEditorInput()).getFields().get("LOG4J_PATH"));
		txtLog4j.addNBModifiedListener(mdfListener);
		
		NCombo cmbLocale 		= grnFrame.getComboField("LOCALE", NBLabel.get(0x0111));
		NCombo cmbEncoding 		= grnFrame.getComboField("ENCODING", NBLabel.get(0x0112));

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
		
		cmbEncoding.add("EUC-KR");
		cmbEncoding.add("ISO-8859-1");
		cmbEncoding.add("US-ASCII");
		cmbEncoding.add("UTF-8");
		cmbEncoding.add("UTF-16");
		
		cmbLocale.setText((String)((ServerConfigEditorInput)getEditorInput()).getFields().get("LOCALE"));
		cmbEncoding.setText((String)((ServerConfigEditorInput)getEditorInput()).getFields().get("ENCODING"));
	
		cmbLocale.addNBModifiedListener(mdfListener);
		cmbEncoding.addNBModifiedListener(mdfListener);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 170;
		
		SubTitleBoard sockConfig = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x0115), NBLabel.get(0x0116), 1);
		sockConfig.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		FieldFrame sockFrame = new FieldFrame(sockConfig.getPanel(), SWT.NONE, 150);
		sockFrame.setLayoutData(layoutData);
		
		NStyledText txtMaxClient 		= sockFrame.getTextField("MAX_CLIENT", NBLabel.get(0x0117));
		NStyledText txtServicePort 		= sockFrame.getTextField("SERVICE_PORT", NBLabel.get(0x0118));
		NStyledText txtMaxSockBuffer 	= sockFrame.getTextField("SOCK_BUFF_SIZE", NBLabel.get(0x0119));
		NStyledText txtSockTimeout 		= sockFrame.getTextField("SOCK_TIME_OUT", NBLabel.get(0x011A));
		
		txtMaxClient.setNumberStyle(true);
		txtServicePort.setNumberStyle(true);
		txtMaxSockBuffer.setNumberStyle(true);
		txtSockTimeout.setNumberStyle(true);
		
		txtMaxClient.setText((String)((ServerConfigEditorInput)getEditorInput()).getFields().get("MAX_CLIENT"));
		txtServicePort.setText((String)((ServerConfigEditorInput)getEditorInput()).getFields().get("SERVICE_PORT"));
		txtMaxSockBuffer.setText((String)((ServerConfigEditorInput)getEditorInput()).getFields().get("SOCK_BUFF_SIZE"));
		txtSockTimeout.setText((String)((ServerConfigEditorInput)getEditorInput()).getFields().get("SOCK_TIME_OUT"));

		
		txtMaxClient.addNBModifiedListener(mdfListener);
		txtServicePort.addNBModifiedListener(mdfListener);
		txtMaxSockBuffer.addNBModifiedListener(mdfListener);
		txtSockTimeout.addNBModifiedListener(mdfListener);
		
		setProtocolArea(back);
	}
	
	private void setSecondField(Composite back)
	{
		setPluginArea(back);
	}
	
	@SuppressWarnings("unchecked")
	private void setProtocolArea(Composite back)
	{
		NBTableModifiedListener mdfListener = new NBTableModifiedListener(){

			public void modified(TableModel model, String id, String fieldName, int fieldIndex) {
				setSendMessage("U", "PCL", fieldName, id, model.getValue(fieldIndex), null);
				isModified = true;
				firePropertyChange(PROP_DIRTY);
			}
			
		};
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = 190;
		
		SubTitleBoard protocolConfig = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x011B), NBLabel.get(0x011C), 1);
		protocolConfig.setLayoutData(layoutData);
		
		///////////////////////�߰�������ư///////////////
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 23;
		
		Composite btnBack = new Composite(protocolConfig.getPanel(), SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayout(layout);
		btnBack.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 70;
		
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		dum.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		btnAddProtocol = new Button(btnBack, SWT.NONE);
		btnAddProtocol.setLayoutData(layoutData);
		btnAddProtocol.setText(NBLabel.get(0x011F));
		btnAddProtocol.addSelectionListener(btnSelection);
		
		btnDelProtocol = new Button(btnBack, SWT.NONE);
		btnDelProtocol.setLayoutData(layoutData);
		btnDelProtocol.setText(NBLabel.get(0x0120));
		btnDelProtocol.addSelectionListener(btnSelection);
		
		///////////////////////�߰�������ư///////////////
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 80;
		
		protocolTable = new TableViewer(protocolConfig.getPanel(), SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		protocolTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		TableViewerColumn column = new TableViewerColumn(protocolTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x017B) + "      ");
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(idEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 0));
		
		
		column = new TableViewerColumn(protocolTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x017C));
		column.getColumn().setWidth(120);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(nameEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 1));
		
		
		column = new TableViewerColumn(protocolTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0166));
		column.getColumn().setWidth(240);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(classEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 2));
		
		protocolTable.getTable().setHeaderVisible(true);
		protocolTable.getTable().setLinesVisible(true);
		
		protocolTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		protocolTable.getTable().getColumn(0).pack();
		
		
		protocolTable.setContentProvider(new NBTableContentProvider());
		protocolTable.setLabelProvider(new NBTableLabelProvider(display));
		
		protocolRoot = new Protocol();
		protocolTable.setInput(protocolRoot);
		
		ArrayList<NBFields> list = (ArrayList<NBFields>)((ServerConfigEditorInput)getEditorInput()).getFields().get("PCL_LIST");
		for(int i=0; i<list.size(); i++)
		{
			Protocol protocol = new Protocol(protocolRoot, (String)list.get(i).get("ID"));
			protocol.setProtocolName((String)list.get(i).get("NAME"));
			protocol.setProtocolClass((String)list.get(i).get("CLASS"));
		}

		idEditingSupport.addNBTableModifiedListener(mdfListener);
		nameEditingSupport.addNBTableModifiedListener(mdfListener);
		classEditingSupport.addNBTableModifiedListener(mdfListener);
		
		protocolTable.refresh();
	}
	
	private void setPluginArea(Composite back)
	{
		NBTableModifiedListener mdfListener = new NBTableModifiedListener(){

			public void modified(TableModel model, String id, String fieldName, int fieldIndex) {
				if(model instanceof Plugin)
				{
					if(fieldName.equals("ID") && !id.equals(model.getValue(fieldIndex)))
					{
						model.setID(model.getValue(fieldIndex));
					}
					
					setSendMessage("U", "PLG", fieldName, id, model.getValue(fieldIndex), null);

					if(fieldName.equals("ID") && paramMap.containsKey(id))
					{
						//HashMap<String, String> tmpMap = (HashMap<String, String>) paramMap.get(id).clone();
						paramMap.put(model.getValue(fieldIndex), paramMap.get(id));
						paramMap.remove(id);
					}
				}
				else if(model instanceof Param)
				{
					setSendMessage("U", "PRM", fieldName, id, model.getValue(fieldIndex), (String)paramTable.getData("PID"));
					
					HashMap<String, String> tmpParam = paramMap.get((String)paramTable.getData("PID"));
					if(paramMap.containsKey((String)paramTable.getData("PID")))
					{
						tmpParam = paramMap.get((String)paramTable.getData("PID"));
					}
					else
					{
						tmpParam = new HashMap<String, String>();
						paramMap.put((String)paramTable.getData("PID"), tmpParam);
					}
					
					if(fieldName.equals("ID"))
					{
						tmpParam.put(((Param)model).getID(), model.getValue(2));
						tmpParam.remove(id);
					}
					else
					{
						tmpParam.put(((Param)model).getID(), model.getValue(2));
					}
				}

				isModified = true;
				firePropertyChange(PROP_DIRTY);
			}
			
		};
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = 390;
		
		SubTitleBoard pluginlConfig = new SubTitleBoard(back, SWT.NONE, NBLabel.get(0x017D), NBLabel.get(0x017E), 1);
		pluginlConfig.setLayoutData(layoutData);
		
		///////////////////////�߰�������ư///////////////
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 23;
		
		Composite btnBack = new Composite(pluginlConfig.getPanel(), SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayout(layout);
		btnBack.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 70;
		
		Composite dum = new Composite(btnBack, SWT.NONE);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		dum.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		btnAddPlugin = new Button(btnBack, SWT.NONE);
		btnAddPlugin.setLayoutData(layoutData);
		btnAddPlugin.setText(NBLabel.get(0x011F));
		btnAddPlugin.addSelectionListener(btnSelection);
		
		btnDelPlugin = new Button(btnBack, SWT.NONE);
		btnDelPlugin.setLayoutData(layoutData);
		btnDelPlugin.setText(NBLabel.get(0x0120));
		btnDelPlugin.addSelectionListener(btnSelection);
		
		///////////////////////�߰�������ư///////////////
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 100;
		
		pluginTable = new TableViewer(pluginlConfig.getPanel(), SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		pluginTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		pluginTable.setComparator(new ViewerComparator(){
			boolean isInit = false;
			public int compare(Viewer viewer, Object o1, Object o2) {
				if(isInit)
				{
					return 0;
				}
				else
				{
					isInit = true;
					return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
				}
		    }
		});
		
		TableViewerColumn column = new TableViewerColumn(pluginTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x017F) + "             ");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(pidEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 0));
		
		
		column = new TableViewerColumn(pluginTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0180));
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(pnameEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 1));
		
		
		column = new TableViewerColumn(pluginTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0181));
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(ptypeEditingSupport = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
						PluginTypeArray.TYPE, SWT.READ_ONLY), 2));
		
		
		
		pluginTable.getTable().setHeaderVisible(true);
		pluginTable.getTable().setLinesVisible(true);
		
		pluginTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		pluginTable.getTable().getColumn(0).pack();
		
		pluginTable.setContentProvider(new NBTableContentProvider());
		pluginTable.setLabelProvider(new NBTableLabelProvider(display));
		
		pluginRoot = new Plugin();
		pluginTable.setInput(pluginRoot);
		
		@SuppressWarnings("unchecked")
		ArrayList<NBFields> list = (ArrayList<NBFields>)((ServerConfigEditorInput)getEditorInput()).getFields().get("PLG_LIST");
		for(int i=0; i<list.size(); i++)
		{
			Plugin plugin = new Plugin(pluginRoot, (String)list.get(i).get("ID"));
			plugin.setPluginName((String)list.get(i).get("NAME"));
			
			if(((String)list.get(i).get("TYPE")).equals("DATABASE"))
				plugin.setPluginType(PluginTypeArray.DATABASE);
			else if(((String)list.get(i).get("TYPE")).equals("CONNECTION"))
				plugin.setPluginType(PluginTypeArray.CONNECTION);
			else
				plugin.setPluginType(PluginTypeArray.OTHER);
		}

		pidEditingSupport.addNBTableModifiedListener(mdfListener);
		pnameEditingSupport.addNBTableModifiedListener(mdfListener);
		ptypeEditingSupport.addNBTableModifiedListener(mdfListener);
		
		//�÷����� ���ý� param ����
		getSite().setSelectionProvider(pluginTable);
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		
		pluginTable.refresh();
		
		///////////////////////�߰�������ư///////////////
		layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 4;
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 23;
		
		btnBack = new Composite(pluginlConfig.getPanel(), SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayout(layout);
		btnBack.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 150;
		
		CLabel lblParam = new CLabel(btnBack, SWT.NONE);
		lblParam.setLayoutData(layoutData);
		lblParam.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		lblParam.setForeground(new Color(display, 49, 106, 197));
		lblParam.setText(NBLabel.get(0x017A));
		
		dum = new Composite(btnBack, SWT.NONE);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		dum.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 70;
		
		btnAddParam = new Button(btnBack, SWT.NONE);
		btnAddParam.setLayoutData(layoutData);
		btnAddParam.setText(NBLabel.get(0x011F));
		btnAddParam.addSelectionListener(btnSelection);
		
		btnDelParam = new Button(btnBack, SWT.NONE);
		btnDelParam.setLayoutData(layoutData);
		btnDelParam.setText(NBLabel.get(0x0120));
		btnDelParam.addSelectionListener(btnSelection);
		
		///////////////////////�߰�������ư///////////////
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 250;
		
		paramTable = new TableViewer(pluginlConfig.getPanel(), SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		paramTable.getTable().setLayoutData(layoutData);
		
		column = new TableViewerColumn(paramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0182) + "                              ");
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.setEditingSupport(pgidEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 0));
		
		column = new TableViewerColumn(paramTable, SWT.NONE);
		column.getColumn().setWidth(0);
		column.getColumn().setResizable(false);
		column.getColumn().setMoveable(false);
		
		column = new TableViewerColumn(paramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0183));
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.setEditingSupport(pgvalueEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 2));
		
		pgidEditingSupport.addNBTableModifiedListener(mdfListener);
		pgvalueEditingSupport.addNBTableModifiedListener(mdfListener);
		
		paramTable.getTable().setHeaderVisible(true);
		paramTable.getTable().setLinesVisible(true);
		
		paramTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		paramTable.getTable().getColumn(0).pack();
		
		paramTable.setContentProvider(new NBTableContentProvider());
		paramTable.setLabelProvider(new NBTableLabelProvider(display));
		
		paramRoot = new Param();
		paramTable.setInput(paramRoot);
		
		paramMap = new HashMap<String, HashMap<String, String>>();
		
		for(int i=0; i<list.size(); i++)
		{
			NBFields tmpPlugin = list.get(i);
			
			Set<String> keySet = tmpPlugin.keySet();
			Iterator<String> itr = keySet.iterator();
			
			HashMap<String, String> tmpParamMap = new HashMap<String, String>();
			
			while(itr.hasNext())
			{
				String key = itr.next();
				
				if(key.contains("PRM__"))
				{
					tmpParamMap.put(key.replace("PRM__", ""), (String)tmpPlugin.get(key));
				}
			}
			
			paramMap.put((String)tmpPlugin.get("ID"), tmpParamMap);
		}
	}
	

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
		if(incoming instanceof IStructuredSelection)
		{
			IStructuredSelection selection = (IStructuredSelection)incoming;
			
			if(selection.getFirstElement() != null && selection.getFirstElement() instanceof Plugin)
			{
				Plugin plugin = (Plugin)selection.getFirstElement();
				String pluginID = plugin.getID();
				
				paramRoot.getChildren().removeAll(paramRoot.getChildren());

				if(paramMap.containsKey(pluginID))
				{
					HashMap<String, String> tmpParamMap = paramMap.get(pluginID);
					Set<String> keySet = tmpParamMap.keySet();
					Iterator<String> itr = keySet.iterator();
					while(itr.hasNext())
					{
						String key = itr.next();
						Param param = new Param(paramRoot, key);
						param.setParamValue(tmpParamMap.get(key));
					}
				}
				
				paramTable.refresh();
				paramTable.setData("PID", pluginID);
			}
		}
	}

	@Override
	public void setFocus() {
		txtLicenseKey.forceFocus();
	}

	public ServerConfigEditor(){
		
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		
		
		sendMsg.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.server.ServerConfig");
		sendMsg.put("CMD_CODE", "S");
		
		try {
			NBFields fields = ((ServerConfigEditorInput)getEditorInput()).getProtocol().execute(sendMsg);
			
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
		
		
		sendMsg = new NBFields();
		
		isModified = false;
		
		for(int i=0; i<protocolRoot.getChildren().size(); i++)
		{
			if(protocolRoot.getChildren().get(i).isModified()) protocolRoot.getChildren().get(i).setModified(false);
		}
		
		for(int i=0; i<pluginRoot.getChildren().size(); i++)
		{
			if(pluginRoot.getChildren().get(i).isModified()) pluginRoot.getChildren().get(i).setModified(false);
		}
		
		for(int i=0; i<paramRoot.getChildren().size(); i++)
		{
			if(paramRoot.getChildren().get(i).isModified()) paramRoot.getChildren().get(i).setModified(false);
		}
		
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}
	
	public void dispose()
	{
		/*if(!importConfig.isDisposed())
			importConfig.removeSelectionListener(btnSelection);
		if(!exportConfig.isDisposed())
			exportConfig.removeSelectionListener(btnSelection);*/
		
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}

}
