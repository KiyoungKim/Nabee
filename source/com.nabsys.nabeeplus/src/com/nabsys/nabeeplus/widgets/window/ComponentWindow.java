package com.nabsys.nabeeplus.widgets.window;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.widgets.BackBoard;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.nabeeplus.widgets.PopupWindow;
import com.nabsys.nabeeplus.widgets.SubTitleBoard;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public class ComponentWindow extends PopupWindow{

	private HashMap<String, String> 				params 					= null; 
	private	NStyledText 							txtID					= null;
	private	NStyledText 							txtName					= null;
	private NStyledText 							txtFilePath				= null;
	private List									listBox					= null;
	private Button									btnCancel				= null;
	private Button									btnOk					= null;
	private Button									btnFileFind				= null;
	private String									srcInstanceName			= null;
	private String									tgtInstanceName			= null;
	private String									componentID				= null;
	private IPCProtocol 							protocol				= null;
	
	public ComponentWindow(Shell parent)
	{
		super(parent);
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, String> open(IWorkbenchWindow window, String srcInstanceName, String tgtInstanceName, String componentID, IPCProtocol protocol) {
		params = new HashMap<String, String>();
		this.srcInstanceName = srcInstanceName;
		this.tgtInstanceName = tgtInstanceName;
		this.componentID = componentID;

		this.protocol = protocol; 
		
		setTitle(NBLabel.get(0x0201));
		setSize(new Point(500, 400));
		int width = window.getShell().getSize().x;
		int height = window.getShell().getSize().y;
		int x = window.getShell().getLocation().x;
		int y = window.getShell().getLocation().y;
		setLocation((width / 2) + x - (400 / 2), (height / 2) + y - (500 / 2));
		setLayout(new FillLayout());
		setImage(Activator.getImageDescriptor("/icons/resource_persp.gif").createImage(shell.getDisplay()));
		
		GridLayout backLayout = new GridLayout();
		backLayout.marginWidth = 0;
		backLayout.marginHeight = 0;
		backLayout.numColumns = 0;
		backLayout.horizontalSpacing = 0;
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 20;
		
		BackBoard board = new BackBoard(shell, SWT.NONE);
		board.setTitleIcon(Activator.getImageDescriptor("/icons/compare_view.gif").createImage(display), new Point(10, 8));
		board.setTitle(NBLabel.get(0x0202));
		board.setLayoutData(new FillLayout());
		board.setLayout(layout);
		
		
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 0;
		layout.verticalSpacing = 10;
		layout.numColumns = 5;
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 23;
		
		Composite btnBack = new Composite(board.getPanel(), SWT.NONE);
		btnBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		btnBack.setLayoutData(layoutData);
		btnBack.setLayout(layout);
		
		setButtonArea(btnBack);
		
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 0;
		layout.verticalSpacing = 10;
		layout.numColumns = 2;
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 80;
		
		SubTitleBoard infoBack = new SubTitleBoard(board.getPanel(), SWT.NONE, NBLabel.get(0x0204), NBLabel.get(0x0205), 1, 10);
		infoBack.setLayoutData(layoutData);
		infoBack.getPanel().setLayout(layout);
		
		setInfoArea(infoBack.getPanel());
		
		
		layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 0;
		layout.verticalSpacing = 10;

		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 150;
		
		SubTitleBoard pathBack = new SubTitleBoard(board.getPanel(), SWT.NONE, NBLabel.get(0x0206), NBLabel.get(0x0207), 1, 10);
		pathBack.setLayoutData(layoutData);
		pathBack.getPanel().setLayout(layout);
		
		setClassPathArea(pathBack.getPanel());
		
		openWindow();

		NBFields fields = new NBFields();
		if(srcInstanceName != null)
		{
			fields.put("SRC_INS_NAME", srcInstanceName);
		}
		if(componentID != null)
		{
			fields.put("ID", componentID);
			txtID.setEnabled(false);
			txtID.setBackground(new Color(display, 240, 240, 240));
			txtID.setForeground(new Color(display, 150, 150, 150));
		}
		
		fields.put("TGT_INS_NAME", tgtInstanceName);
		
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.document.ComponentConfig");
		fields.put("CMD_CODE", "R");
		
		try {
			fields = protocol.execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				shell.dispose();
				return null;
			}
			
			if(fields.containsKey("NAME"))
			{
				txtID.setText(componentID);
				txtName.setText((String)fields.get("NAME"));
			}
			
			ArrayList<NBFields> classPathList = (ArrayList<NBFields>)fields.get("CLS_PTH_LST");
			for(int i=0; i<classPathList.size(); i++)
			{
				listBox.add((String)classPathList.get(i).get("CLS_PTH"));
			}
			
			if(listBox.getItemCount() > 0)
				listBox.setSelection(0);
		} catch (SocketClosedException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			shell.dispose();
			return null;
		} catch (TimeoutException e) {
			IMessageBox.Error(shell, e.getMessage());
			shell.dispose();
			return null;
		} catch (NetException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			shell.dispose();
			return null;
		} catch (UnsupportedEncodingException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			shell.dispose();
			return null;
		} catch (NoSuchAlgorithmException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			shell.dispose();
			return null;
		} catch (DataTypeException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			shell.dispose();
			return null;
		} catch (ProtocolException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			shell.dispose();
			return null;
		}

		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }

		return params;
	}

	
	private void setInfoArea(Composite back)
	{
		FieldFrame firstFrame = new FieldFrame(back, SWT.NONE, 100);
		firstFrame.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		txtID = firstFrame.getTextField("ID", NBLabel.get(0x0164));
		
		txtID.setDefaultButton(btnOk);
		
		
		FieldFrame secondFrame = new FieldFrame(back, SWT.NONE, 120);
		secondFrame.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		txtName = secondFrame.getTextField("NAME", NBLabel.get(0x0165));
		
		txtName.setDefaultButton(btnOk);
	}

	private void setClassPathArea(Composite back)
	{
		listBox = new List(back, SWT.BORDER);
		listBox.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	}
	
	private void setButtonArea(Composite back)
	{
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 180;
		layoutData.heightHint = 15;
		
		txtFilePath = new NStyledText(back, SWT.SINGLE | SWT.BORDER);
		txtFilePath.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 80;
		layoutData.heightHint = 15;
		
		btnFileFind = new Button(back, SWT.NONE);
		btnFileFind.setText(NBLabel.get(0x0203));
		btnFileFind.setLayoutData(layoutData);
		
		txtFilePath.setDefaultButton(btnFileFind);
		
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
				if(e.widget == btnFileFind)
				{
					findComponentFile();
				}
				else if(e.widget == btnOk)
				{
					try {
						saveComponent();
					} catch (IOException ex) {
						IMessageBox.Error(shell, NBLabel.get(0x020B));
					}
				}
				else if(e.widget == btnCancel)
				{
					shell.dispose();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		};
		btnFileFind.addSelectionListener(listener);
		btnOk.addSelectionListener(listener);
		btnCancel.addSelectionListener(listener);
	}
	
	private void findComponentFile()
	{
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] {"*.class"});
		dialog.setFilterNames(new String[] {"Java class file"});
		String fileSelected = dialog.open();
		
		if(fileSelected != null)
		{

			txtFilePath.setText(fileSelected);
			
		}
	}
	
	private void saveComponent() throws IOException
	{
		if(txtID.getText().trim().equals(""))
		{
			IMessageBox.Warning(shell, NBLabel.get(0x0209));
			return;
		}
		
		NBFields fields = new NBFields();
		boolean needFile = true;
		if(componentID != null)
		{
			if(!srcInstanceName.equals(tgtInstanceName))
			{
				fields.put("CMD_CODE", "I");
			}
			else
			{
				fields.put("CMD_CODE", "U");
			}
			if(txtFilePath.getText().trim().equals("")) needFile = false;
		}
		else
		{
			fields.put("CMD_CODE", "I");
		}
		
		if(needFile && txtFilePath.getText().trim().equals(""))
		{
			IMessageBox.Warning(shell, NBLabel.get(0x020C));
			return;
		}
		
		if(!txtFilePath.getText().trim().equals("") && listBox.getSelectionIndex() < 0)
		{
			IMessageBox.Warning(shell, NBLabel.get(0x020A));
			return;
		}
		
		if(needFile || !txtFilePath.getText().trim().equals(""))
		{
			File file = new File(txtFilePath.getText());
			FileInputStream fis = new FileInputStream(file);
			byte classByte[] = new byte[(int)file.length()];
			fis.read(classByte);
			fis.close();
		
			int endPoint = 0;
			for(int i=16; i<classByte.length; i++)
			{
				if(classByte[i] == 0x07)
				{
					endPoint = i;
					break;
				}
			}
			
			String classPath = listBox.getItem(listBox.getSelectionIndex());
			
			if(!classPath.substring(classPath.length() -1).equals("/") && !classPath.substring(classPath.length() -1).equals("\\"))
			{
				classPath += "/";
			}
			
			byte pathArray[] = new byte[endPoint - 16];
			
			System.arraycopy(classByte, 16, pathArray, 0, pathArray.length);
			
			String subPath = new String(pathArray);
			String className = subPath.replace("/", ".");
			subPath = subPath + ".class";
			
			fields.put("RSC", classByte);
			fields.put("SAVE_PATH", classPath + subPath);
			fields.put("CLASS", className);
		}
		else //�����Է� �ʿ� ���, �����Է����� ���� ���
		{
			//�ٸ� �ν��Ͻ��� ������Ʈ ������
			if(!srcInstanceName.equals(tgtInstanceName))
			{
				fields.put("SHARE", "true");
				fields.put("SRC_INS_NAME", srcInstanceName);
			}
			
		}

		
		fields.put("ID"					, txtID.getText());
		fields.put("NAME"				, txtName.getText());
		fields.put(IPC.NB_INSTNCE_ID	, tgtInstanceName);
		
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.document.ComponentConfig");
		
		try {
			fields = protocol.execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			IMessageBox.Info(shell, NBLabel.get(0x0208));
			shell.dispose();
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
		}
	}

}
