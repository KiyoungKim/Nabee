package com.nabsys.nabeeplus.editors;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import com.nabsys.common.cipher.hash.Hash;
import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.input.UserListEditorInput;
import com.nabsys.nabeeplus.listener.NBTableModifiedListener;
import com.nabsys.nabeeplus.views.model.AuthArray;
import com.nabsys.nabeeplus.views.model.NBEditingSupport;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.TableModel;
import com.nabsys.nabeeplus.views.model.User;
import com.nabsys.nabeeplus.widgets.BackBoard;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class UserListEditor extends NabeeEditor {

	public final static String ID = "com.nabsys.nabeeplus.editors.userListEditor";
	private Display 				display 			= null;
	private Shell					shell				= null;
	private BackBoard 				board 				= null;
	private Button 					btnSearch 			= null;
	private Button 					btnAdd 				= null;
	private Button 					btnRemove 			= null;
	private NStyledText 			txtSearch 			= null;
	
	private NBTableModifiedListener	nmListener 			= null;
	private SelectionListener		btnSelection		= null;

	private User 					root 				= null;
	private TableViewer 			userTable 			= null;
	private boolean 				isModified 			= false;
	
	private NBEditingSupport 		idEditingSupport 	= null;
	private NBEditingSupport 		pwEditingSupport 	= null;
	private NBEditingSupport 		authEditingSupport 	= null;
	private NBEditingSupport 		nameEditingSupport 	= null;
	private NBEditingSupport 		phoneEditingSupport = null;
	private NBEditingSupport 		activeEditingSupport = null;
	private String					userAuthority		= "";
	
	private HashMap<String, HashMap<String, String>>	tmpSendMsg = null;
	
	public UserListEditor(){
	}
	
	@Override
	public void createPartControl(Composite parent) {
		this.display = parent.getDisplay();
		this.shell = parent.getShell();
		
		tmpSendMsg = new HashMap<String, HashMap<String, String>>();
		
		board = new BackBoard(parent, SWT.NONE);
		board.setTitleIcon(Activator.getImageDescriptor("/icons/user_view.gif").createImage(display), new Point(10, 8));
		board.setTitle(NBLabel.get(0x011D));
		
		GridLayout boardLayout = new GridLayout();
		boardLayout.marginWidth = 0;
		boardLayout.marginHeight = 0;
		board.setLayout(boardLayout);
		
		/////////////BUTTON AREA/////////////////////
		genButtonSelectionListener();
		
		GridData btnAreaLayoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		btnAreaLayoutData.heightHint = 30;
		
		Composite btnCps = new Composite(board.getPanel(), SWT.NONE);
		btnCps.setLayoutData(btnAreaLayoutData);
		btnCps.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		GridLayout btnArea = new GridLayout();
		btnArea.marginWidth = 0;
		btnArea.marginHeight = 2;
		btnArea.numColumns = 5;
		btnArea.verticalSpacing = 3;
		btnArea.horizontalSpacing = 2;
		btnCps.setLayout(btnArea);
		
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);

		FieldFrame schFrame = new FieldFrame(btnCps, SWT.NONE, 100);
		schFrame.setLayoutData(layoutData);
		txtSearch = schFrame.getTextField("SCH", NBLabel.get(0x011E));
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 80;
		
		btnSearch = new Button(btnCps, SWT.NONE);
		btnSearch.setText(NBLabel.get(0x0121));
		btnSearch.setLayoutData(layoutData);

		txtSearch.setDefaultButton(btnSearch);
		
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 18;
		Composite dum = new Composite(btnCps, SWT.NONE);
		dum.setLayoutData(layoutData);
		dum.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 18;
		layoutData.widthHint = 80;
		
		btnAdd = new Button(btnCps, SWT.NONE);
		btnAdd.setText(NBLabel.get(0x011F));
		btnAdd.setLayoutData(layoutData);
		
		btnRemove = new Button(btnCps, SWT.NONE);
		btnRemove.setText(NBLabel.get(0x0120));
		btnRemove.setLayoutData(layoutData);
		
		/////////////BUTTON AREA/////////////////////
		
		////////////TABLE VIEWER AREA///////////////
		GridData tableAreaLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		userTable = new TableViewer(board.getPanel(), SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		userTable.getTable().setLayoutData(tableAreaLayoutData);
		/*userTable.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				if(((TableModel)o2).getID().equals("New")) return 1;
				return ((TableModel)o1).getID().compareToIgnoreCase(((TableModel)o2).getID());
		    }
		});*/
		
		
		TableViewerColumn column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0185) + "                   ");
		column.getColumn().setWidth(130);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(idEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 0));
		
		
		
		column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0186));
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(pwEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 1));
		
		userAuthority = ((UserListEditorInput)getEditorInput()).getProtocol().getUserAuthority();
		
		column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0187));
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		if(userAuthority.equals("Admin"))
		{
			column.setEditingSupport(authEditingSupport = new NBEditingSupport(column.getViewer(), 
					new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
							AuthArray.AUTH, SWT.READ_ONLY), 2));
		}
		
		
		column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0188));
		column.getColumn().setWidth(120);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(nameEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 3));
		
		column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0189));
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(phoneEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 4));
		
		column = new TableViewerColumn(userTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x018A));
		column.getColumn().setAlignment(SWT.CENTER);
		column.getColumn().setWidth(60);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(activeEditingSupport = new NBEditingSupport(column.getViewer(), 
				new CheckboxCellEditor((Composite)column.getViewer().getControl()), 5));
		
		userTable.getTable().setHeaderVisible(true);
		userTable.getTable().setLinesVisible(true);
		
		userTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		userTable.getTable().getColumn(0).pack();

		userTable.setContentProvider(new NBTableContentProvider());
		userTable.setLabelProvider(new NBTableLabelProvider(display));
		
		root = new User();
		userTable.setInput(root);
		
		
		nmListener = new NBTableModifiedListener(){

			public void modified(TableModel model, String id, String fieldName,
					int fieldIndex) {
				setSendMessage("U", id, (User)model);
				
				isModified = true;
				firePropertyChange(PROP_DIRTY);
			}
			
		};
		
		
		btnSearch.addSelectionListener(btnSelection);
		btnAdd.addSelectionListener(btnSelection);
		btnRemove.addSelectionListener(btnSelection);
		
		idEditingSupport.addNBTableModifiedListener(nmListener);
		pwEditingSupport.addNBTableModifiedListener(nmListener);
		
		if(userAuthority.equals("Admin"))
			authEditingSupport.addNBTableModifiedListener(nmListener);
		
		nameEditingSupport.addNBTableModifiedListener(nmListener);
		phoneEditingSupport.addNBTableModifiedListener(nmListener);
		activeEditingSupport.addNBTableModifiedListener(nmListener);
		
		////////////TABLE VIEWER AREA///////////////
		
		searchUser();
	}
	
	private void addNewUser()
	{
		User user = new User(root, "New", display);
		user.setModified(true);
		user.setActive(false);

		setSendMessage("I", "New", user);
		
		userTable.refresh();
		isModified = true;
		firePropertyChange(PROP_DIRTY);
	}
	
	private void deleteUser()
	{
		IStructuredSelection selection = (IStructuredSelection)userTable.getSelection();
		
		if(selection.isEmpty()) return;
		
		if(selection.getFirstElement() instanceof User)
		{
			User user = ((User)selection.getFirstElement());
			setSendMessage("D", user.getUserId(), user);
			
			user.remove();
			
			userTable.refresh();
			isModified = true;
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	public void dispose()
	{
		if(!btnSearch.isDisposed())
			btnSearch.removeSelectionListener(btnSelection);
		if(!btnAdd.isDisposed())
			btnAdd.removeSelectionListener(btnSelection);
		if(!btnRemove.isDisposed())
			btnRemove.removeSelectionListener(btnSelection);
		
		idEditingSupport.removeNBTableModifiedListener(nmListener);
		pwEditingSupport.removeNBTableModifiedListener(nmListener);
		
		if(userAuthority.equals("Admin"))
			authEditingSupport.removeNBTableModifiedListener(nmListener);
		
		nameEditingSupport.removeNBTableModifiedListener(nmListener);
		phoneEditingSupport.removeNBTableModifiedListener(nmListener);
		activeEditingSupport.removeNBTableModifiedListener(nmListener);
		
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		NBFields sendMsg = new NBFields();
		ArrayList<NBFields> msgArray = new ArrayList<NBFields>();
		
		Set<String> keySet = tmpSendMsg.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext())
		{
			String keyString = itr.next();
			HashMap<String, String> tmpMap = tmpSendMsg.get(keyString);
			
			NBFields tmpFields = new NBFields();
			
			tmpFields.put("ID"		, keyString);
			tmpFields.put("ACT"		, tmpMap.get("ACT"));

			if(tmpMap.containsKey("PW"))
			{
				try {
					tmpFields.put("PW"		, Hash.getMD5Hash(tmpMap.get("PW")));
				} catch (NoSuchAlgorithmException e) {
					tmpFields.put("PW"		, "1234");
				}
			}
			if(tmpMap.containsKey("ROLE")) 	tmpFields.put("ROLE"	, tmpMap.get("ROLE"));
			if(tmpMap.containsKey("NAME")) 	tmpFields.put("NAME"	, tmpMap.get("NAME"));
			if(tmpMap.containsKey("PHONE")) tmpFields.put("PHONE"	, tmpMap.get("PHONE"));
			if(tmpMap.containsKey("ATV")) 	tmpFields.put("ATV"		, tmpMap.get("ATV"));

			msgArray.add(tmpFields);
		}
		
		sendMsg.put("ACT_LST", msgArray);
		
		tmpSendMsg = new HashMap<String, HashMap<String, String>>();

		sendMsg.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.user.UserConfig");
		sendMsg.put("CMD_CODE", "S");

		try {
			NBFields fields = ((UserListEditorInput)getEditorInput()).getProtocol().execute(sendMsg);
			
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
		
		for(int i=0; i<root.getChildren().size(); i++)
		{
			if(root.getChildren().get(i).isModified()) root.getChildren().get(i).setModified(false);
		}
		
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName() + " " + ((UserListEditorInput)input).getWork());
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
		txtSearch.forceFocus();
	}
	
	@SuppressWarnings("unchecked")
	private void searchUser()
	{
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.user.UserConfig");
		fields.put("CMD_CODE", "R");
		fields.put("SCH", txtSearch.getText().trim());
		
		try {
			fields = ((UserListEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			root.getChildren().removeAll(root.getChildren());
			userTable.refresh();

			ArrayList<NBFields> userList = (ArrayList<NBFields>)fields.get("USR_LST");
			
			if(userList != null)
			{
				for(int i=0; i<userList.size(); i++)
				{
					NBFields tmp = userList.get(i);
					
					User user = new User(root, (String)tmp.get("ID"), display);
					user.setAuth(AuthArray.getAuth((String)tmp.get("ROLE")));
					user.setName((String)tmp.get("NAME"));
					user.setPassword((String)tmp.get("PW"));
					user.setPhone((String)tmp.get("PHONE"));
					user.setActive(((String)tmp.get("ATV")).equals("true"));
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
	
	private void setSendMessage(String action, String key, User user)
	{
		HashMap<String, String> actMap = null;

		if(tmpSendMsg.containsKey(key))
		{
			actMap = tmpSendMsg.get(key);

			if(action.equals("I"))
			{
				if(actMap.get("ACT").equals("I"))
				{
					user.getParent().getChildren().remove(user);
					return;
				}
				
				actMap.put("ACT", action);
			}
			else if(action.equals("U"))
			{
				if(!actMap.get("ACT").equals("I"))
				{
					if(!key.equals(user.getUserId()))
					{
						actMap.put("ACT", "D");
						
						actMap = new HashMap<String, String>();
						actMap.put("ACT", action);

					}
					else
					{
						actMap.put("ACT", action);
					}
				}
				else
				{
					if(!key.equals(user.getUserId()))
					{
						tmpSendMsg.remove(key);
						
						actMap = new HashMap<String, String>();
						actMap.put("ACT", "I");
					}
				}
					
			}
			else if(action.equals("D"))
			{
				if(actMap.get("ACT").equals("I"))
				{
					tmpSendMsg.remove(key);
				}
				else
				{
					actMap.put("ACT", action);
				}
			}
		}
		else
		{
			if(!key.equals(user.getUserId()))
			{
				actMap = new HashMap<String, String>();
				actMap.put("ACT", "D");
				tmpSendMsg.put(key, actMap);
				
				actMap = new HashMap<String, String>();
				actMap.put("ACT", action);
			}
			else
			{
				actMap = new HashMap<String, String>();
				
				actMap.put("ACT", action);
			}
		}
		
		
		
		if(action.equals("I") || action.equals("U"))
		{
			actMap.put("PW"		, user.getPassword());
			actMap.put("ROLE"	, user.getAuth());
			actMap.put("NAME"	, user.getName());
			actMap.put("PHONE"	, user.getPhone());
			actMap.put("ATV"	, user.isActive().toString());
		}
		
		tmpSendMsg.put(user.getUserId()		, actMap);
	}
	
	private void genButtonSelectionListener()
	{
		btnSelection = new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				if(e.widget == btnSearch)
				{
					searchUser();
				}
				else if(e.widget == btnAdd)
				{
					addNewUser();
				}
				else if(e.widget == btnRemove)
				{
					if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) == SWT.CANCEL)
					{
						return;
					}
					
					deleteUser();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
	}

}
