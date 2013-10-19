package com.nabsys.nabeeplus.views;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import com.nabsys.common.util.DateUtil;
import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.common.paramdata.ParameterContext;
import com.nabsys.nabeeplus.common.paramdata.ParameterFileControler;
import com.nabsys.nabeeplus.common.paramdata.ParameterMap;
import com.nabsys.nabeeplus.editors.SqlEditor;
import com.nabsys.nabeeplus.editors.input.SqlEditorInput;
import com.nabsys.nabeeplus.views.model.NBEditingSupport;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.Param;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.NabeeProtocol;
import com.nabsys.net.protocol.IPC.IPC;

public class SqlParameterView extends NabeeView {

	public final static String ID = "com.nabsys.nabeeplus.views.sqlParameterView";
	private Display					display					= null;
	private TableViewer				tableViewer				= null;
	private NabeeProtocol			protocol				= null;
	private Action 					executeAction			= null;
	private Shell					shell					= null;
	private Pattern					commentPattern			= null;
	private Pattern					commentClosePattern		= null;
	private ParameterFileControler	paramControler			= null;
	private Label					label					= null;
	private DateUtil 				du 						= null;
	
	public SqlParameterView(){
		commentPattern		= Pattern.compile("((?s)/\\*.*)|(\\-\\-.*)");
		commentClosePattern	= Pattern.compile("\\*/");
	}

	@Override
	public void createPartControl(Composite parent) {
		setPartName(NBLabel.get(0x023C));
		
		
		this.display 	= parent.getDisplay();
		this.shell		= parent.getShell();
		this.du			= new DateUtil(NBLabel.getLocale());

		Composite back = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		back.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.heightHint = 12;
		
		label = new Label(back, SWT.NONE);
		label.setLayoutData(gridData);

		try {
			paramControler = new ParameterFileControler();
		} catch (IOException e) {
			IMessageBox.Error(shell, e.getMessage());
			return;
		} catch (ClassNotFoundException e) {
			IMessageBox.Error(shell, e.getMessage());
			return;
		}
	
		tableViewer = new TableViewer(back, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x017A) + "                                     ");
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0169));
		column.getColumn().setWidth(90);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), new String[] {"CHAR", "NUMBER"}, SWT.READ_ONLY), 1));
		

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0183));
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 2));
		
		tableViewer.getTable().getColumn(0).pack();
		tableViewer.setContentProvider(new NBTableContentProvider());
		tableViewer.setLabelProvider(new NBTableLabelProvider(display));
		
		Param root = new Param();
		tableViewer.setInput(root);
		
		executeAction = new Action() {
			public void run(){
				if(!protocol.isConnected()) return;
				
				if(pluginName.equals(""))
				{
					IMessageBox.Warning(shell, "Select database plugin");
					return;
				}
				
				NBFields fields = new NBFields();
				fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.sql.QueryConfig");
				fields.put("CMD_CODE"			, "E");
				fields.put("PLUGIN"				, pluginName);
				fields.put("URL"				, url);
				fields.put(IPC.NB_INSTNCE_ID	, instanceName);

				ParameterMap paramMap = paramControler.getObject();
				ParameterContext parameterContext = null;
				if(!paramMap.containsKey(url)) paramMap.put(url, parameterContext = (new ParameterContext()));
				else parameterContext = paramMap.get(url);
				
				Param root = (Param)tableViewer.getInput();
				ArrayList<NBFields> list = new ArrayList<NBFields>();
				NBFields param = new NBFields();
				for(int i=0; i<root.getChildren().size(); i++)
				{
					Param child = (Param)root.getChildren().get(i);
					
					if(child.getType().equals("CHAR"))
					{
						param.put(child.getID(), child.getParamValue());
						parameterContext.put(child.getID(), child.getParamValue());
					}
					else if(child.getType().equals("NUMBER"))
					{
						param.put(child.getID(), Integer.parseInt(child.getParamValue()));
						parameterContext.put(child.getID(), Integer.parseInt(child.getParamValue()));
					}
				}
				parameterContext.put("__DATABASE_CONNECTION_PLUGIN__", pluginName);
				
				if(param.size() > 0)
					list.add(param);
				fields.put("PARAM", list);

				try {

					fields = protocol.execute(fields);

					if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
					{
						IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
						return;
					}
					openResultView(fields);
				} catch (SocketClosedException e) {
					IMessageBox.Error(shell, NBLabel.get(0x0090));
					closeConnection();
				} catch (TimeoutException e) {
					IMessageBox.Error(shell, e.getMessage());
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
				
				try {
					paramControler.save();
				} catch (IOException e) {
					IMessageBox.Error(shell, e.getMessage());
				} catch (ClassNotFoundException e) {
					IMessageBox.Error(shell, e.getMessage());
				}
			}
		};
		
		executeAction.setText(NBLabel.get(0x0047));
		executeAction.setImageDescriptor(Activator.getImageDescriptor("/icons/exec.gif"));
		executeAction.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/exec.gif"));
        executeAction.setEnabled(false);
        
		getViewSite().getActionBars().getToolBarManager().add(executeAction);
		
		IEditorPart editor = null;
		if(getSite().getWorkbenchWindow().getActivePage() != null)
			editor = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();

		if(editor != null && editor instanceof SqlEditor)
		{
			SqlEditorInput input = (SqlEditorInput)editor.getEditorInput();
			this.protocol = input.getProtocol();
			this.pluginName = ((SqlEditor)editor).getPluginName();
			setServer(((SqlEditor)editor).getServer());
			parseParams(input.getSql(), input.getInstance(), input.getPath());
		}
	}

	public void openResultView(NBFields fields)
	{
		try {
			SqlResultView resultView = (SqlResultView)getSite().getWorkbenchWindow().getActivePage().showView(SqlResultView.ID);

			if(fields.containsKey("LIST"))
			{
				@SuppressWarnings("unchecked")
				ArrayList<NBFields> list = (ArrayList<NBFields>)fields.get("LIST");
				if(list.size() > 0)
				{
					resultView.setResult(list, url);
				}
				else
				{
					resultView.setUpdateResult("0 rows retrieved.", url);
				}
			}
			else
			{
				if(fields.containsKey("RESULT"))
				{
					resultView.setUpdateResult((String)fields.get("RESULT"), url);
				}
				else
				{
					resultView.setUpdateResult("Requested sql failed.", url);
				}
			}
			
		} catch (PartInitException e) {
			IMessageBox.Error(shell, e.getMessage());
		}
	}
	
	public void setProtocol(NabeeProtocol protocol)
	{
		this.protocol = protocol;
	}
	
	public NabeeProtocol getProtocol()
	{
		return this.protocol;
	}

	@Override
	public void setFocus() {
		if(protocol != null)
		executeAction.setEnabled(protocol.isConnected());
	}
	
	private Pattern 	pattern 		= Pattern.compile(":\\w+");
	private String 		instanceName 	= "";
	private String 		pluginName 		= "";
	private String 		url 			= "";
	private ArrayList<int[]> commentLocation = null;
	private int			START			= 0;
	private int			END				= 1;
	
	public void setPlugin(String pluginName)
	{
		this.pluginName = pluginName;
	}
	
	public String getPlugin()
	{
		if(paramControler.getObject().get(url) != null)
			return (String) paramControler.getObject().get(url).get("__DATABASE_CONNECTION_PLUGIN__");
		else
			return null;
	}
	
	private boolean checkInComment(int start, int end)
	{
		int[] cl = null;
		for(int i=0; i<commentLocation.size(); i++)
		{
			cl = commentLocation.get(i);
			
			if(cl[START] < start && cl[END] > start)
			{
				return true;
			}
			
			if(cl[START] < end && cl[END] > end)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void parseParams(String sql, String instanceName, String url)
	{
		this.label.setText(url + " (" + du.getCurrentDate("yyyy. MM. dd. HH:mm:ss") + ")");
		this.instanceName = instanceName;
		this.url = url;
		
		commentLocation = new ArrayList<int[]>();
		Matcher commentMatcher = commentPattern.matcher(sql);
		Matcher commentCloseMatcher = commentClosePattern.matcher(sql);
		
		int offset = 0;

		while(commentMatcher.find(offset))
		{
			int[] location	 = new int[2];
			location[START] = commentMatcher.start();
			location[END]	= commentMatcher.end();
			offset = location[END];

			if(sql.substring(location[START], location[START]+2).equals("/*"))
			{
				if(commentCloseMatcher.find(location[START]))
				{
					location[END] = commentCloseMatcher.end();
					offset = location[END];
				}
			}
			
			commentLocation.add(location);
		}
		
		Matcher matcher = pattern.matcher(sql);
		
		Param root = (Param)tableViewer.getInput();
		ArrayList<Param> delList = new ArrayList<Param>();
		for(int i=0; i<root.getChildren().size(); i++)
		{
			Param param = (Param) root.getChildren().get(i);
			Pattern delPattern = Pattern.compile(":" + param.getID() + "[\\p{javaWhitespace}\\(\\{\\[\\)\\}\\]]");
			Matcher delMatcher = delPattern.matcher(sql);
			
			if(!delMatcher.find())
			{
				delList.add(param);
			}
			else if(checkInComment(delMatcher.start(), delMatcher.end()))
			{
				delList.add(param);
			}
		}
		
		for(int i=0; i<delList.size(); i++)
		{
			root.getChildren().remove(delList.get(i));
		}
		
		ParameterMap paramMap = paramControler.getObject();
		ParameterContext parameterContext = null;
		if(paramMap.containsKey(url)) parameterContext = paramMap.get(url);
		else parameterContext = new ParameterContext();
		
		while(matcher.find())
		{
			String key = sql.substring(matcher.start()+1, matcher.end());
			
			if(checkInComment(matcher.start(), matcher.end()))
			{
				continue;
			}

			boolean isExist = false;
			for(int i=0; i<root.getChildren().size(); i++)
			{
				Param param = (Param)root.getChildren().get(i);
				
				if(key.equals(param.getID()))
				{
					isExist = true;
					
					if(parameterContext.containsKey(key))
					{
						param.setParamValue(parameterContext.get(key) + "");
						param.setType(parameterContext.get(key) instanceof String?0:1);
					}
					
					break;
				}
			}
			
			if(!isExist)
			{
				Param param = new Param(root, key);
				if(parameterContext.containsKey(key))
				{
					param.setParamValue(parameterContext.get(key) + "");
					param.setType(parameterContext.get(key) instanceof String?0:1);
				}
			}
		}
		
		tableViewer.refresh();
	}
	
	private boolean isDispose = false;
	public boolean isDispose()
	{
		return this.isDispose;
	}
	
	public void dispose()
	{
		isDispose = true;
		super.dispose();
	}

}
