package com.nabsys.nabeeplus.editors;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.common.EditActionManager;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.StyleManager;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.input.SqlEditorInput;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.views.SqlParameterView;
import com.nabsys.nabeeplus.views.model.Server;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NCombo;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;

public class SqlEditor extends NabeeEditor{

	public final static String ID = "com.nabsys.nabeeplus.editors.sqlEditor";
	
	private Shell						shell				= null;
	private EditActionManager 			editActionManager 	= null;
	private StyledText 					editor				= null;
	private boolean						isModified			= false;
	private ModifyListener				modifyListener		= null;
	private SqlParameterView			paramView			= null;
	private NCombo 						dbList				= null;
	private Styler 						sqlStyle 			= null;
	private Styler						quoteStyle			= null;
	private Styler						dQuoteStyle			= null;
	private Styler 						commentStyle		= null;
	private Pattern 					pattern 			= null;
	private SelectionListener			selectionListener	= null;
	private SelectionListener			eSListener			= null;
	
	public SqlEditor(){
		
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.sql.QueryConfig");
		fields.put(IPC.NB_INSTNCE_ID	, ((SqlEditorInput)getEditorInput()).getInstance());
		fields.put("CMD_CODE"			, "S");
		fields.put("PATH"				, ((SqlEditorInput)getEditorInput()).getPath());
		fields.put("SQL"				, editor.getText());

		try {
			fields = ((SqlEditorInput)getEditorInput()).getProtocol().execute(fields);
			
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			setParams(editor.getText());
			isModified = false;
			firePropertyChange(PROP_DIRTY);
		} catch (SocketClosedException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (TimeoutException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		} catch (NetException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		} catch (UnsupportedEncodingException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (NoSuchAlgorithmException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (ProtocolException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (DataTypeException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		}
	}
	
	public String getPluginName()
	{
		return dbList.getText();
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPartControl(Composite parent) {
		this.shell = parent.getShell();
		
		sqlStyle = new Styler() {
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = new Color(shell.getDisplay(), 127,0,1);
				textStyle.font = new Font(editor.getDisplay(), "Courier New", 10, SWT.BOLD);
			}
		};
		
		quoteStyle = new Styler() {
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_BLUE);
			}
		};
		
		dQuoteStyle = new Styler() {
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_BLUE);
			}
		};
		
		commentStyle = new Styler() {
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = new Color(shell.getDisplay(), 63,127,95);
			}
		};
		
		StyleManager styleManager = new StyleManager(parent.getDisplay());
		styleManager.start();
		
		Composite top = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		top.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gridData.widthHint = 10;
		gridData.heightHint = 35;
		Composite dumy = new Composite(top, SWT.NONE);
		dumy.setLayoutData(gridData);
		
		
		GridLayout dbBackLayout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 10;
		layout.marginHeight = 0;
		
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.heightHint = 35;
		Composite dbBack = new Composite(top, SWT.NONE);
		dbBack.setLayoutData(gridData);
		dbBack.setLayout(dbBackLayout);
		dbBack.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		FieldFrame dbFrame = new FieldFrame(dbBack, SWT.NONE, 150);
		dbFrame.setLayoutData(layoutData);
		dbFrame.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		
		dbList = dbFrame.getComboField("DB", "Select Database Plugin");
		dbFrame.setLabelBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		dbFrame.setLabelForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		//DATABASE PLUGIN LIST
		ArrayList<NBFields> pluginList = ((SqlEditorInput)getEditorInput()).getPluginList();
		for(int i=0; i<pluginList.size(); i++)
		{
			dbList.add((String)pluginList.get(i).get("PLG_NM"));
		}
		
		gridData = new GridData(GridData.FILL, GridData.FILL, false, true);
		gridData.widthHint = 10;
		CLabel space = new CLabel(top, SWT.NONE);
		space.setLayoutData(gridData);

		editor = new StyledText(top, SWT.NONE | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		editor.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		editor.setSize(parent.getSize().x - 20, parent.getSize().y - 20);
		editor.setLocation(20, 20);
		editor.setFont(new Font(parent.getDisplay(), "Courier New", 10, SWT.NONE));
		editor.setText(((SqlEditorInput)getEditorInput()).getSql());
		
		StyleManager.styleChanger.changeStyle(editor, commentStyle, sqlStyle, quoteStyle, dQuoteStyle, pattern);
		
		editor.addSelectionListener(eSListener = new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				editActionManager.setFocus();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		editor.getVerticalBar().addSelectionListener(selectionListener = new SelectionListener(){
			private int prevLine = 0;
			public void widgetSelected(SelectionEvent e) {
				int line = editor.getVerticalBar().getSelection() / editor.getVerticalBar().getIncrement();
				if(prevLine != line)
				{
					StyleManager.styleChanger.changeStyle(editor, commentStyle, sqlStyle, quoteStyle, dQuoteStyle, pattern);
				}
				prevLine = line;
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		modifyListener = new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				StyleManager.styleChanger.changeStyle(editor, commentStyle, sqlStyle, quoteStyle, dQuoteStyle, pattern);
				isModified = true;
				firePropertyChange(PROP_DIRTY);
			}
			
		};

		editor.addModifyListener(modifyListener);
		
		editActionManager = new EditActionManager(parent, 
				getSite().getWorkbenchWindow(), 
				getEditorSite().getActionBars(), 
				editor);

		try {
			paramView = (SqlParameterView)getSite().getWorkbenchWindow().getActivePage().showView(SqlParameterView.ID);
			paramView.setProtocol(((SqlEditorInput)getEditorInput()).getProtocol());
			
			dbList.addNBModifiedListener(new NBModifiedListener(){
				public void modified(String name, String value) {
					paramView.setPlugin(dbList.getText());
				}
			});
			
			if(dbList.getItemCount() > 0)
				dbList.setText((String)pluginList.get(0).get("PLG_NM"));

		} catch (PartInitException e1) {
		}
		
	}
	
	public void setServer(Server server)
	{
		super.setServer(server);
		paramView.setServer(server);
	}
	
	private void initStylerInfo()
	{
		ArrayList<String> cmdList = new ArrayList<String>();
		
		cmdList.add("\\bSELECT\\b");
		cmdList.add("\\bFROM\\b");
		cmdList.add("\\bWHERE\\b");
		cmdList.add("\\bAND\\b");
		cmdList.add("\\bOR\\b");
		cmdList.add("\\bGROUP\\b");
		cmdList.add("\\bORDER\\b");
		cmdList.add("\\bBY\\b");
		cmdList.add("\\bINSERT\\b");
		cmdList.add("\\bINTO\\b");
		cmdList.add("\\bUPDATE\\b");
		cmdList.add("\\bSET\\b");
		cmdList.add("\\bVALUES\\b");
		cmdList.add("\\bDELETE\\b");
		cmdList.add("\\bUNION\\b");
		cmdList.add("\\bALL\\b");
		cmdList.add("\\bJOIN\\b");
		cmdList.add("\\bON\\b");
		cmdList.add("\\bLIMIT\\b");
		cmdList.add("\\bLEFT\\b");
		cmdList.add("\\bRIGHT\\b");
		cmdList.add("\\bOUTER\\b");
		cmdList.add("\\bINNER\\b");
		
		String regxString = "((?s)/\\*.*)|(\\-\\-.*)|(\'.*)|(\".*)|(";
		for(int i=0; i<cmdList.size(); i++)
		{
			if(i == cmdList.size() - 1) regxString += cmdList.get(i);
			else regxString += cmdList.get(i) + "|";
		}
		regxString += ")";
		pattern = Pattern.compile(regxString);
	}
	
	private void setParams(String sql)
	{
		if(paramView != null && !paramView.isDispose())
		{
			paramView.parseParams(sql, ((SqlEditorInput)getEditorInput()).getInstance(), ((SqlEditorInput)getEditorInput()).getPath());
			
			if(paramView.getPlugin() != null)
			{
				for(int i=0; i<dbList.getItemCount(); i++)
				{
					if(dbList.getItem(i).equals(paramView.getPlugin()))
					{
						dbList.setText(paramView.getPlugin());
						break;
					}
				}
			}
		}
	}
	
	public void setCursorPosition(String rangeBlock, int matchStart, String keyWord, boolean caseSensitive)
	{
		rangeBlock = rangeBlock.replaceAll("\\*", "\\\\\\\\*");
		
		Pattern pattern = Pattern.compile(rangeBlock);
		Matcher matcher = pattern.matcher(editor.getText());
		boolean exist = matcher.find(matchStart);
		if(exist)
		{
			int start = matcher.start();
			pattern = Pattern.compile(caseSensitive?keyWord:keyWord.toUpperCase());
			matcher = pattern.matcher(caseSensitive?editor.getText():editor.getText().toUpperCase());
			exist = matcher.find(start);
			if(exist)
			{
				editor.setSelection(matcher.start(), matcher.end());
			}
		}
	}
	
	@Override
	public void setFocus() {
		editor.forceFocus();
		editActionManager.setFocus();
		
		if(paramView.isDispose())
		{
			try {
				paramView = (SqlParameterView)getSite().getWorkbenchWindow().getActivePage().showView(SqlParameterView.ID);
				paramView.setServer(getServer());
				paramView.setProtocol(((SqlEditorInput)getEditorInput()).getProtocol());
				paramView.setFocus();
			} catch (PartInitException e1) {
			}
		}
		paramView.setPlugin(dbList.getText());
		setParams(editor.getText());
	}
	
	public void dispose()
	{
		StyleManager.styleChanger.exit();
		editor.removeModifyListener(modifyListener);
		editor.removeSelectionListener(eSListener);
		editor.getVerticalBar().removeSelectionListener(selectionListener);
		editActionManager.dispose();
		super.dispose();
	}
	

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		initStylerInfo();
	}
	
	@Override
	public boolean isDirty() {
		return isModified;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
