package com.nabsys.nabeeplus.design.window;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.design.model.TelegramModel;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class ServiceCallConfig  extends ConfigPopupWindow{
	private SearchPopupList 			popupList 					= null;
	private HashMap<String, Boolean>	typeList 					= new HashMap<String, Boolean>();
	private boolean 					isTlgInput 					= false;
	private NStyledText 				txtCallServiceID 			= null;
	private NStyledText 				txtTelegram 				= null;
	private boolean						isQs						= false;
	public ServiceCallConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Service Call", new Point(450, 400));
		
		Composite contentsBack = getContentsBack();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		contentsBack.setLayout(layout);
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 60;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 90);
		fieldFrame.setLayoutData(layoutData);
		
		MouseListener mouseListener = new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				if(popupList != null)popupList.close();
			}

			public void mouseUp(MouseEvent e) {
			}
		};
		
		txtCallServiceID = fieldFrame.getTextField("SI", NBLabel.get(0x0153));
		if(returnMap.get("SI") != null && !((String)returnMap.get("SI")).equals(""))
		{
			txtCallServiceID.setText((String)returnMap.get("SI"));
		}
		
		txtTelegram = fieldFrame.getTextField("TI", NBLabel.get(0x0145));
		if(returnMap.get("TI") != null && !((String)returnMap.get("TI")).equals(""))
		{
			isQs = true;
			txtTelegram.setText((String)returnMap.get("TI"));
		}
		else
		{
			isQs = false;
			txtTelegram.setDisableEditing(true, display);
		}
		
		final TableViewer fieldsTable = new TableViewer(contentsBack, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		fieldsTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		txtCallServiceID.addModifyListener(new ModifyListener(){
			private boolean isInput = false;
			public void modifyText(ModifyEvent e) {
				if(isInput == true)
				{
					isInput = false;
					return;
				}
				
				Rectangle rect = new Rectangle(getLocation().x + 105, 
						getLocation().y + 105,
						txtCallServiceID.getBounds().width,
						200);
				
				if(popupList == null || popupList.isShellDisposed()) 
				{
					popupList = new SearchPopupList(shell, SWT.NONE, txtCallServiceID);
				}

				setServiceList(txtCallServiceID.getText());
				
				if(popupList.isShellDisposed()) 
				{
					String returnValue = popupList.open(rect);
					if(returnValue != null)
					{
						isInput = true;
						txtCallServiceID.setText(returnValue);
						if(!typeList.containsKey(returnValue) || !typeList.get(returnValue))
						{
							isTlgInput = true;
							txtTelegram.setDisableEditing(true, display);
							isQs = false;
							isTlgInput = false;
							fieldsTable.setInput(new TelegramModel());
							fieldsTable.refresh();
						}
						else
						{
							isTlgInput = true;
							txtTelegram.setDisableEditing(false, display);
							isQs = true;
							isTlgInput = false;
						}
					}
					else
					{
						if(!typeList.containsKey(returnValue) || !typeList.get(returnValue))
						{
							isTlgInput = true;
							txtTelegram.setDisableEditing(true, display);
							isQs = false;
							isTlgInput = false;
							fieldsTable.setInput(new TelegramModel());
							fieldsTable.refresh();
						}
						else
						{
							isTlgInput = true;
							txtTelegram.setDisableEditing(false, display);
							isQs = true;
							isTlgInput = false;
						}
					}
				}
			}
		});
		
		txtTelegram.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				if(isTlgInput == true)
				{
					isTlgInput = false;
					return;
				}
				
				Rectangle rect = new Rectangle(getLocation().x + 105, 
						getLocation().y + 125,
						txtTelegram.getBounds().width,
						200);
				
				if(popupList == null || popupList.isShellDisposed()) 
				{
					popupList = new SearchPopupList(shell, SWT.NONE, txtTelegram);
				}

				setTelegramList(txtTelegram.getText());
				
				if(popupList.isShellDisposed()) 
				{
					String returnValue = popupList.open(rect);
					if(returnValue != null)
					{
						isTlgInput = true;
						txtTelegram.setText(returnValue);
						setTelegramFields(returnValue, fieldsTable);
					}
					else
					{
						setTelegramFields(txtTelegram.getText(), fieldsTable);
					}
				}
			}
		});
		
		TableViewerColumn column = new TableViewerColumn(fieldsTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0134) + "               ");
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(fieldsTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0135));
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(fieldsTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0136));
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		fieldsTable.getTable().setHeaderVisible(true);
		fieldsTable.getTable().setLinesVisible(true);
		
		fieldsTable.setContentProvider(new NBTableContentProvider());
		fieldsTable.setLabelProvider(new NBTableLabelProvider(shell.getDisplay()));
		
		fieldsTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		fieldsTable.getTable().getColumn(0).pack();

		if(!txtTelegram.getText().equals(""))
		{
			setTelegramFields(txtTelegram.getText(), fieldsTable);
		}
		
		fieldFrame.addMouseListener(mouseListener);
		fieldsTable.getTable().addMouseListener(mouseListener);
		
		contentsBack.layout(true);
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return returnMap;
	}
	
	private void setServiceList(String key)
	{
		if(popupList.setItems(key)) return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
		fields.put(IPC.NB_INSTNCE_ID	, instanceID);
		fields.put("CMD_CODE"			, "LC");
		fields.put("SCH"				, key);
		fields.put("CALLER" 			, (String)returnMap.get("CALLER"));
		try {
			fields = protocol.execute(fields);
					
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			@SuppressWarnings("unchecked")
			ArrayList<NBFields> list = (ArrayList<NBFields>)fields.get("LIST");
			String[] serviceList = new String[list.size()];
			typeList = new HashMap<String, Boolean>();
			for(int i=0; i<serviceList.length; i++)
			{
				serviceList[i] = (String)list.get(i).get("ID");
				typeList.put(serviceList[i], ((String)list.get(i).get("TYPE")).equals("MessageQueue"));
			}
			
			popupList.setItems(serviceList);
		} catch (Exception e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setTelegramList(String key)
	{
		if(popupList.setItems(key)) return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TelegramConfig");
		fields.put(IPC.NB_INSTNCE_ID	, instanceID);
		fields.put("CMD_CODE"			, "L");
		fields.put("SCH_TYPE"			, "SCH_ID");
		fields.put("SCH"				, key);
		try {
			fields = protocol.execute(fields);
					
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			ArrayList<NBFields> list = (ArrayList<NBFields>)fields.get("TLGM_LST");
			String[] telegramList = new String[list.size()];
			for(int i=0; i<telegramList.length; i++)
			{
				telegramList[i] = (String)list.get(i).get("ID");
			}
			
			popupList.setItems(telegramList);
		} catch (Exception e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void setTelegramFields(String key, TableViewer fieldsTable)
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.TelegramConfig");
		fields.put(IPC.NB_INSTNCE_ID	, instanceID);
		fields.put("CMD_CODE"			, "RP");
		fields.put("ID"					, key);
		try {
			fields = protocol.execute(fields);
					
			TelegramModel root = null;
			fieldsTable.setInput(root = new TelegramModel());
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			ArrayList<NBFields> list = (ArrayList<NBFields>) fields.get("FLD");
			for(int i=0; i<list.size(); i++)
			{
				NBFields tmp = list.get(i);
				TelegramModel tm = new TelegramModel(root, (String)tmp.get("ID"));
				tm.setName((String)tmp.get("NAME"));
				String type = "";
				switch(((String)tmp.get("TYPE")).toCharArray()[0])
				{
				case 'C': type = "CHAR";
					break;
				case 'N': type = "NUM";
					break;
				case 'I': type = "INT";
					break;
				case 'D': type = "DOUBLE";
					break;
				case 'F': type = "FLOAT";
					break;
				case 'L': type = "LONG";
					break;
				case 'B': type = "BYTE";
					break;
				case 'A': type = "BYTEARRAY";
					break;
				}
				tm.setType(type);
			}
			fieldsTable.refresh();
		} catch (Exception e) {
			e.printStackTrace();
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		}
	}

	@Override
	protected void confirm() {

		if(isQs && txtTelegram.getText().equals(""))
		{
			IMessageBox.Warning(shell, NBLabel.get(0x028C));
			return;
		}
		returnMap.put("SI", txtCallServiceID.getText());
		returnMap.put("TI", txtTelegram.getText());
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}
}
