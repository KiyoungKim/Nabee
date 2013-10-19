package com.nabsys.nabeeplus.design.window;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NCheckBox;
import com.nabsys.nabeeplus.widgets.NCombo;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class ClientNetworkConfig  extends ConfigPopupWindow{
	private NCombo cmbPoolName = null;
	private NCombo cmbProtocol = null;
	private NStyledText txtAddr = null;
	private NStyledText txtPort = null;
	private NCombo cmbEncoding = null;
	private NStyledText txtLengthFieldID = null;
	private NStyledText txtOffset = null;
	private NStyledText txtLength = null;
	private NStyledText txtAdjustment = null;
	private NStyledText txtIDFieldID = null;
	private NStyledText txtMaxBuffer = null;
	
	public ClientNetworkConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Connect To Server", new Point(450, 470));
		
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
		layoutData.heightHint = 500;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 150);
		fieldFrame.setLayoutData(layoutData);
		
		NCheckBox chkUsePool =  fieldFrame.getCheckField("UCP", NBLabel.get(0x0285), NBLabel.get(0x0266), NBLabel.get(0x0267));
		
		cmbPoolName = fieldFrame.getComboField("CPN", NBLabel.get(0x0286));
		cmbProtocol = fieldFrame.getComboField("PI", NBLabel.get(0x017B));
		txtAddr = fieldFrame.getTextField("AD", NBLabel.get(0x007D));
		txtPort = fieldFrame.getTextField("PT", NBLabel.get(0x007E));
		cmbEncoding = fieldFrame.getComboField("SED", NBLabel.get(0x0112));
		cmbEncoding.add("EUC-KR");
		cmbEncoding.add("ISO-8859-1");
		cmbEncoding.add("US-ASCII");
		cmbEncoding.add("UTF-8");
		cmbEncoding.add("UTF-16");
		cmbEncoding.setText("EUC-KR");
		txtLengthFieldID = fieldFrame.getTextField("LFI", NBLabel.get(0x020D));
		txtOffset = fieldFrame.getTextField("LFO", NBLabel.get(0x0287));
		txtLength = fieldFrame.getTextField("LFL", NBLabel.get(0x0288));
		txtAdjustment = fieldFrame.getTextField("LFA", NBLabel.get(0x0289));
		txtIDFieldID = fieldFrame.getTextField("IFI", NBLabel.get(0x020E));
		txtMaxBuffer = fieldFrame.getTextField("MB", NBLabel.get(0x0119));
		
		Listener listener = new Listener() {
			public void handleEvent(Event e){
				switch(e.type)
				{
				case SWT.Verify :
					if(e.widget == txtPort ||
						e.widget == txtOffset ||
						e.widget == txtLength ||
						e.widget == txtAdjustment ||
						e.widget == txtMaxBuffer)
					{
						e.doit = e.text.matches("-?[0-9]*");
					}
				}
			}
		};
		
		txtPort.addListener(SWT.Verify, listener);
		txtOffset.addListener(SWT.Verify, listener);
		txtLength.addListener(SWT.Verify, listener);
		txtAdjustment.addListener(SWT.Verify, listener);
		txtMaxBuffer.addListener(SWT.Verify, listener);
		
		chkUsePool.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				boolean isPool = ((String)value).equals("true");
				if(isPool)
				{
					cmbPoolName.setEnabled(true);
					cmbProtocol.setEnabled(false);
					cmbProtocol.setText(cmbProtocol.getItemCount() > 0?cmbProtocol.getItem(0):"");
					txtAddr.setDisableEditing(true, display);
					txtPort.setDisableEditing(true, display);
					txtPort.setText("0");
					cmbEncoding.setEnabled(false);
					cmbEncoding.setText(cmbEncoding.getItemCount() > 0?cmbEncoding.getItem(0):"");
					txtLengthFieldID.setDisableEditing(true, display);
					txtOffset.setDisableEditing(true, display);
					txtOffset.setText("0");
					txtLength.setDisableEditing(true, display);
					txtLength.setText("0");
					txtAdjustment.setDisableEditing(true, display);
					txtAdjustment.setText("0");
					txtIDFieldID.setDisableEditing(true, display);
					txtMaxBuffer.setDisableEditing(true, display);
					txtMaxBuffer.setText("0");
				}
				else
				{
					cmbPoolName.setEnabled(false);
					cmbPoolName.setText(cmbPoolName.getItemCount() > 0?cmbPoolName.getItem(0):"");
					cmbProtocol.setEnabled(true);
					txtAddr.setDisableEditing(false, display);
					txtPort.setDisableEditing(false, display);
					cmbEncoding.setEnabled(true);
					txtLengthFieldID.setDisableEditing(false, display);
					txtOffset.setDisableEditing(false, display);
					txtLength.setDisableEditing(false, display);
					txtAdjustment.setDisableEditing(false, display);
					txtIDFieldID.setDisableEditing(false, display);
					txtMaxBuffer.setDisableEditing(false, display);
				}
				returnMap.put(name, ((String)value).equals("true"));
			}
		});
		
		setProtocolList();
		setConnectionPoolList();
		
		boolean isPool = ((Boolean)returnMap.get("UCP"));
		
		chkUsePool.setSelection((Boolean)returnMap.get("UCP"));
		cmbPoolName.setText((String)returnMap.get("CPN"));
		String pid = (String)returnMap.get("PI");
		if(pid.equals(""))
		{
			cmbProtocol.setText(cmbProtocol.getItem(0));
		}
		else
		{
			for(int i=0; i<cmbProtocol.getItemCount(); i++)
			{
				if(cmbProtocol.getItem(i).contains("["+pid+"]"))
				{
					cmbProtocol.setText(cmbProtocol.getItem(i));
				}
			}
		}
		txtAddr.setText((String)returnMap.get("AD"));
		txtPort.setText((Integer)returnMap.get("PT")+"");
		cmbEncoding.setText((String)returnMap.get("SED"));
		txtLengthFieldID.setText((String)returnMap.get("LFI"));
		txtOffset.setText((Integer)returnMap.get("LFO")+"");
		txtLength.setText((Integer)returnMap.get("LFL")+"");
		txtAdjustment.setText((Integer)returnMap.get("LFA")+"");
		txtIDFieldID.setText((String)returnMap.get("IFI"));
		txtMaxBuffer.setText((Integer)returnMap.get("MB")+"");
		
		if(isPool)
		{
			cmbPoolName.setEnabled(true);
			cmbProtocol.setEnabled(false);
			cmbProtocol.setText(cmbProtocol.getItemCount() > 0?cmbProtocol.getItem(0):"");
			txtAddr.setDisableEditing(true, display);
			txtPort.setDisableEditing(true, display);
			txtPort.setText("0");
			cmbEncoding.setEnabled(false);
			cmbEncoding.setText(cmbEncoding.getItemCount() > 0?cmbEncoding.getItem(0):"");
			txtLengthFieldID.setDisableEditing(true, display);
			txtOffset.setDisableEditing(true, display);
			txtOffset.setText("0");
			txtLength.setDisableEditing(true, display);
			txtLength.setText("0");
			txtAdjustment.setDisableEditing(true, display);
			txtAdjustment.setText("0");
			txtIDFieldID.setDisableEditing(true, display);
			txtMaxBuffer.setDisableEditing(true, display);
			txtMaxBuffer.setText("0");
		}
		else
		{
			cmbPoolName.setEnabled(false);
			cmbPoolName.setText(cmbPoolName.getItemCount() > 0?cmbPoolName.getItem(0):"");
			cmbProtocol.setEnabled(true);
			txtAddr.setDisableEditing(false, display);
			txtPort.setDisableEditing(false, display);
			cmbEncoding.setEnabled(true);
			txtLengthFieldID.setDisableEditing(false, display);
			txtOffset.setDisableEditing(false, display);
			txtLength.setDisableEditing(false, display);
			txtAdjustment.setDisableEditing(false, display);
			txtIDFieldID.setDisableEditing(false, display);
			txtMaxBuffer.setDisableEditing(false, display);
		}
		returnMap.put("UCP", isPool);
		
		contentsBack.layout(true);
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return returnMap;
	}
	
	private void setConnectionPoolList()
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
		fields.put(IPC.NB_INSTNCE_ID	, instanceID);
		fields.put("CMD_CODE"			, "CPL");
		try {
			
			fields = protocol.execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			@SuppressWarnings("unchecked")
			ArrayList<NBFields> list = (ArrayList<NBFields>) fields.get("LIST");
			for(int i=0; i<list.size(); i++)
			{
				cmbPoolName.add((String)list.get(i).get("NAME"));
			}
			
		} catch (Exception e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		}
	}
	
	private void setProtocolList()
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
		fields.put("CMD_CODE"			, "PL");
		try {
			
			fields = protocol.execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			@SuppressWarnings("unchecked")
			ArrayList<NBFields> list = (ArrayList<NBFields>) fields.get("LIST");
			for(int i=0; i<list.size(); i++)
			{
				cmbProtocol.add("[" + (String)list.get(i).get("ID") + "]" + (String)list.get(i).get("NAME"));
			}
		} catch (Exception e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		}
	}
	
	@Override
	protected void confirm() {
		try{
		returnMap.put("CPN", cmbPoolName.getText());
		returnMap.put("PI", cmbProtocol.getText().replaceAll("\\].*", "").replaceAll("\\[", ""));
		returnMap.put("AD", txtAddr.getText());
		returnMap.put("PT", Integer.parseInt(txtPort.getText()));
		returnMap.put("SED", cmbEncoding.getText());
		returnMap.put("LFI", txtLengthFieldID.getText());
		returnMap.put("LFO", Integer.parseInt(txtOffset.getText()));
		returnMap.put("LFL", Integer.parseInt(txtLength.getText()));
		returnMap.put("LFA", Integer.parseInt(txtAdjustment.getText()));
		returnMap.put("IFI", txtIDFieldID.getText());
		returnMap.put("MB", Integer.parseInt(txtMaxBuffer.getText()));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}
}
