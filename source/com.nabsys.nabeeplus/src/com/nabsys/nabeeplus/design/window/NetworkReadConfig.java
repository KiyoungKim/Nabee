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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.design.model.TelegramModel;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class NetworkReadConfig  extends ConfigPopupWindow{
	private SearchPopupList 	popupList 					= null;
	
	public NetworkReadConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Network Write", new Point(400, 400));
		
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
		
		MouseListener mouseListener = new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				if(popupList != null)popupList.close();
			}

			public void mouseUp(MouseEvent e) {
			}
		};
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 60;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 120);
		fieldFrame.setLayoutData(layoutData);
		
		final NStyledText txtTelegram = fieldFrame.getTextField("TI", NBLabel.get(0x0145));
		if(returnMap.get("TI") != null && !((String)returnMap.get("TI")).equals(""))
		{
			txtTelegram.setText((String)returnMap.get("TI"));
		}
		
		final NStyledText txtTimeout = fieldFrame.getTextField("TO", NBLabel.get(0x028B));
		txtTimeout.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, Long.parseLong(value));
			}
		});
		Listener listener = new Listener() {
			public void handleEvent(Event e){
				switch(e.type)
				{
				case SWT.Verify :
					if(e.widget == txtTimeout)
					{
						e.doit = e.text.matches("[0-9]*");
					}
				}
			}
		};
		
		txtTimeout.addListener(SWT.Verify, listener);
		
		if(returnMap.get("TO") != null)
		{
			txtTimeout.setText(String.valueOf((Long)returnMap.get("TO")));
		}
		
		final TableViewer fieldsTable = new TableViewer(contentsBack, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		fieldsTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		
		txtTelegram.addModifyListener(new ModifyListener(){
			private boolean isInput = false;
			public void modifyText(ModifyEvent e) {
				if(isInput == true)
				{
					isInput = false;
					return;
				}
				
				Rectangle rect = new Rectangle(getLocation().x + 135, 
						getLocation().y + 100,
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
						isInput = true;
						txtTelegram.setText(returnValue);
						returnMap.put("TI", returnValue);
						setTelegramFields(returnValue, fieldsTable);
					}
					else
					{
						returnMap.put("TI", txtTelegram.getText());
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
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}
}
