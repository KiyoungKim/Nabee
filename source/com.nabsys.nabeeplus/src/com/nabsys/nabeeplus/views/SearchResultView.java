package com.nabsys.nabeeplus.views;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.ResourceFactory;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.editors.SqlEditor;
import com.nabsys.nabeeplus.editors.input.SqlEditorInput;
import com.nabsys.nabeeplus.views.model.Model;
import com.nabsys.nabeeplus.views.model.NBContentProvider;
import com.nabsys.nabeeplus.views.model.NBStyledLabelProvider;
import com.nabsys.nabeeplus.views.model.QueryStorageList;
import com.nabsys.nabeeplus.views.model.SearchContents;
import com.nabsys.nabeeplus.views.model.SqlDocument;
import com.nabsys.nabeeplus.views.model.SqlFolder;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public class SearchResultView extends NabeeView {
	
	public final static String ID = "com.nabsys.nabeeplus.views.searchResultView";
	private TreeViewer 							treeViewer 			= null;
	private Display 							display 			= null;
	private Label								label				= null;
	private IDoubleClickListener				dbcListener 		= null;
	private int 								findCnt 			= 0;
	private Pattern 							pattern 			= null;
	private String 								instanceName 		= "";
	private Shell 								shell 				= null;
	private IPCProtocol 						protocol 			= null;
	private String								selectedResource	= "";
	private String								path				= "";
	private HashMap<String, String> 			searchParam			= null;
	
	private Action 								clearAction			= null;
	private Action 								expandAction		= null;
	private Action 								collapseAction		= null;
	private Action 								refreshAction		= null;
	
	private final Styler 						decoStyle;
	
	public SearchResultView() {
		decoStyle = new Styler() {
			public void applyStyles(TextStyle textStyle) {
				textStyle.background = new Color(display, 206, 204, 247);
			}
		};
	}

	@Override
	public void createPartControl(Composite parent) {
		setPartName(NBLabel.get(0x0245));
		
		this.display 	= parent.getDisplay();
		
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
		
		treeViewer = new TreeViewer(back, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		treeViewer.setContentProvider(new NBContentProvider(display, false));
		treeViewer.setLabelProvider(new NBStyledLabelProvider(parent.getDisplay()));
		treeViewer.setUseHashlookup(true);
		treeViewer.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {

				if(o1 instanceof SearchContents && o2 instanceof SearchContents)
				{
					return -1;
				}
				
		    	if (o1 instanceof QueryStorageList && o2 instanceof QueryStorageList)
		        {
		    		if(o1 instanceof SqlFolder && o2 instanceof SqlDocument) return -1;
		    		else if(o1 instanceof SqlDocument && o2 instanceof SqlFolder) return 1;
		    		else return ((QueryStorageList)o1).getName().compareToIgnoreCase(((QueryStorageList)o2).getName());
		        }
		        else
		        {
		        	return -1;
		        }
		    }
		});
		
		treeViewer.addDoubleClickListener(genDbClickListener());
		
		clearAction = new Action() {
			public void run(){
				Model root = (Model)treeViewer.getInput();
				if(root.getChildren().size() > 0)
				{
					removeTree();
				}
			}
		};
		
		expandAction = new Action() {
			public void run(){
				Model root = (Model)treeViewer.getInput();
				if(root.getChildren().size() > 0)
				{
					treeViewer.expandAll();
				}
			}
		};
		
		collapseAction = new Action() {
			public void run(){
				Model root = (Model)treeViewer.getInput();
				if(root.getChildren().size() > 0)
				{
					treeViewer.collapseAll();
				}
			}
		};
		
		refreshAction = new Action() {
			public void run(){
				if(protocol != null & protocol.isConnected())
					refreshSearch();
			}
		};
		
		clearAction.setText(NBLabel.get(0x0252));
		clearAction.setImageDescriptor(Activator.getImageDescriptor("/icons/delete.gif"));
        
		expandAction.setText(NBLabel.get(0x0253));
		expandAction.setImageDescriptor(Activator.getImageDescriptor("/icons/expandall.gif"));
        
		collapseAction.setText(NBLabel.get(0x0254));
		collapseAction.setImageDescriptor(Activator.getImageDescriptor("/icons/collapseall.gif"));
        
		refreshAction.setText(NBLabel.get(0x0255));
		refreshAction.setImageDescriptor(Activator.getImageDescriptor("/icons/refresh.gif"));
		refreshAction.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/refresh.gif"));
		
		getViewSite().getActionBars().getToolBarManager().add(clearAction);
		getViewSite().getActionBars().getToolBarManager().add(new Separator());
		getViewSite().getActionBars().getToolBarManager().add(expandAction);
		getViewSite().getActionBars().getToolBarManager().add(collapseAction);
		getViewSite().getActionBars().getToolBarManager().add(new Separator());
		getViewSite().getActionBars().getToolBarManager().add(refreshAction);
		
		if(ResourceFactory.getSearchRoot() != null)
		{
			treeViewer.setInput(ResourceFactory.getSearchRoot());
			expandTree(ResourceFactory.getSearchRoot());
			collapseAction.setEnabled(true);
			expandAction.setEnabled(true);
			clearAction.setEnabled(true);
		}
		else
		{
			if(ResourceFactory.getSearchRoot() != null)
			{
				ResourceFactory.removeSearchRoot();
				ResourceFactory.removeSearchString();
			}
			
			Model root = new Model();
			new QueryStorageList(root, NBLabel.get(0x0250), null);
			treeViewer.setInput(root);

			collapseAction.setEnabled(false);
			expandAction.setEnabled(false);
			clearAction.setEnabled(false);
		}
		
		refreshAction.setEnabled(false);
		
		if(!ResourceFactory.getSearchString().equals(""))
		{
			this.label.setText(ResourceFactory.getSearchString());
		}
	}
	
	private void refreshSearch()
	{
		removeTree();
		final String keyWord = searchParam.get("SCH_KEY");
		final String caseSensitive = searchParam.get("CASE");
		
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(getSite().getWorkbenchWindow().getShell());
		try {
			
			pmd.run(true, true, new IRunnableWithProgress()
			{
				@SuppressWarnings("unchecked")
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException 
				{
					NBFields fields = new NBFields();
					
					fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.sql.QueryConfig");
					fields.put("CMD_CODE"			, "H");
					fields.put("PATH"				, path);
					fields.put(IPC.NB_INSTNCE_ID	, instanceName);
					fields.put("SCH_KEY"			, keyWord);
					fields.put("CASE"				, caseSensitive);
					fields.put("CNTS"				, (String)searchParam.get("CNTS"));
					fields.put("FLDR"				, (String)searchParam.get("FLDR"));
					
					try{
						
						fields = protocol.execute(fields);
						
						if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
						{
							IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
							return;
						}
						
						int tot = (Integer)fields.get("CNT");
						monitor.beginTask("Search", tot);	
						
						ArrayList<NBFields> list = (ArrayList<NBFields>)fields.get("SCH_LIST");

						for(int i=0; i<list.size(); i++)
						{
							fields = list.get(i);
							String treeType = (String)fields.get("TYPE");
							String path = (String)fields.get("PATH");
							
							if(((String)searchParam.get("FLDR")).equals("true"))
							{
								if(caseSensitive.equals("true"))
								{
									if(path.contains(keyWord))
									{
										setResult(keyWord, caseSensitive, path, null, treeType, searchParam.get("FLDR").equals("true"));
									}
								}
								else
								{
									if(path.toUpperCase().contains(keyWord.toUpperCase()))
									{
										setResult(keyWord, caseSensitive, path, null, treeType, searchParam.get("FLDR").equals("true"));
									}
								}
							}
							
							if(((String)searchParam.get("CNTS")).equals("true"))
							{
								fields = new NBFields();
								
								fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.sql.QueryConfig");
								fields.put("CMD_CODE"			, "C");
								fields.put("PATH"				, path);
								fields.put(IPC.NB_INSTNCE_ID	, instanceName);
								fields.put("SCH_KEY"			, keyWord);
								fields.put("CASE"				, caseSensitive);
								
								fields = protocol.execute(fields);

								if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
								{
									IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
									return;
								}
								
								ArrayList<NBFields> cntsList = (ArrayList<NBFields>)fields.get("STR_LIST");
								setResult(keyWord, caseSensitive, path, cntsList, treeType, searchParam.get("FLDR").equals("true"));
							}
							
							monitor.worked(i);
							monitor.subTask(path);
						}
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
					} catch (ProtocolException e) {
						IMessageBox.Error(shell, NBLabel.get(0x0090));
					} catch (DataTypeException e) {
						IMessageBox.Error(shell, NBLabel.get(0x0090));
					} catch (NullPointerException e) {
						IMessageBox.Error(shell, NBLabel.get(0x0090));
					} catch (Exception e){
						IMessageBox.Error(shell, NBLabel.get(0x0090));
					}finally {
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			IMessageBox.Error(shell, e.getMessage());
		} catch (InterruptedException e) {
			IMessageBox.Error(shell, e.getMessage());
		}finally{
			finishSetResult(selectedResource, keyWord);
		}
	}
	
	protected void finishSetResult(String selectedResource, String keyWord)
	{
		this.selectedResource = selectedResource;
		
		String resultString = "'" + keyWord + "' - " + findCnt + " matches in '" + selectedResource + "'";
		this.label.setText(resultString);
		Model root = (Model)treeViewer.getInput();
		ResourceFactory.setSearchRoot(root);
		ResourceFactory.setSearchString(resultString);
		
		if(findCnt > 0)
		{
			expandTree(root);
			collapseAction.setEnabled(true);
			expandAction.setEnabled(true);
			clearAction.setEnabled(true);
		}
		else
		{
			collapseAction.setEnabled(false);
			expandAction.setEnabled(false);
			clearAction.setEnabled(false);
		}
		
		findCnt = 0;
		pattern = null;
	}
	
	private void expandTree(Model parent)
	{
		if(parent.getChildren().size() > 0)
		{
			treeViewer.expandToLevel(parent.getChildren().get(0), 0);
			expandTree(parent.getChildren().get(0));
		}
		else
		{
			return;
		}
	}
	
	private void removeTree()
	{
		Model root = (Model)treeViewer.getInput();
		if(root.getChildren().size() > 0)
		{
			root.getChildren().remove(root.getChildren().get(0));
			treeViewer.refresh();
		}
	}
	
	protected void initTree(Shell shell, String instanceName, IPCProtocol protocol, String path, HashMap<String, String> searchParam)
	{
		this.shell 				= shell;
		this.instanceName 		= instanceName;
		this.protocol 			= protocol;
		this.path				= path;
		this.searchParam		= searchParam;
		
		removeTree();
		refreshAction.setEnabled(true);
	}
	
	protected void setResult(final String keyWord, final String caseSensitive, final String searchPath, final ArrayList<NBFields> lineStringList, final String treeType, final boolean searchFolder)
	{
		getSite().getWorkbenchWindow().getShell().getDisplay().syncExec(new Runnable(){
			public void run(){
				
				if(pattern == null)
				{
					pattern = Pattern.compile(caseSensitive.equals("true")?keyWord:keyWord.toUpperCase());
				}
				if(treeType.equals("FOLDER"))
				{
					setTreeFolder(keyWord, caseSensitive, searchPath, searchFolder);
				}
				else if(treeType.equals("DOCUMENT"))
				{
					setTreeDoc(keyWord, caseSensitive, searchPath, searchFolder);
					if(lineStringList != null && lineStringList.size() > 0)
					{
						setContents(searchPath, lineStringList, pattern, caseSensitive);
					}
				}
			}
		});
	}

	private void setContents(String path, ArrayList<NBFields> lineStringList, Pattern pattern, String caseSensitive)
	{
		String[] pathArray = path.split("\\.");
		Model root = (Model)treeViewer.getInput();
		Model parent = root.getChildren().get(0);
		
		int start = 0;
		if(pathArray[0].equals("")) start = 1;
		else start = 0;
		
		for(int i=start; i<pathArray.length; i++)
		{
			for(int j=0; j<parent.getChildren().size(); j++)
			{
				if(parent.getChildren().get(j).getName().equals(pathArray[i]))
				{
					parent = parent.getChildren().get(j);
					break;
				}
			}
		}
		
		for(int j=0; j<lineStringList.size(); j++)
		{
			NBFields tmp = lineStringList.get(j);
			String string = (String)tmp.get("FIND_STR");
			int matchStart = (Integer)tmp.get("MATCH_START");
			Matcher matcher = pattern.matcher(caseSensitive.equals("true")?string:string.toUpperCase());
			StyledString styledString = new StyledString();
			int fs = 0;
			while(matcher.find(fs))
			{
				findCnt++;
				StyledString tmpString = new StyledString(string.substring(matcher.start(), matcher.end()), decoStyle);
				styledString.append(string.substring(fs, matcher.start()));
				styledString.append(tmpString);
				fs = matcher.end();
			}
			
			if(string.length() > styledString.getString().length())
				styledString.append(string.substring(fs, string.length() - 1));

			new SearchContents(parent, matchStart, string, styledString.getStyleRanges(), Activator.getImageDescriptor("/icons/searchm_obj.gif").createImage(display));
		}
			

		treeViewer.refresh();
	}
	
	private void setTreeDoc(String keyWord, String caseSensitive, String path, boolean searchFolder)
	{
		String[] pathArray = path.split("\\.");
		Model root = (Model)treeViewer.getInput();
		Model parent = null;
		if(root.getChildren().size() <=0)
		{
			parent = new QueryStorageList(root, NBLabel.get(0x024F), Activator.getImageDescriptor("/icons/repo_rep.gif").createImage(display));
		}
		else
		{
			parent = root.getChildren().get(0);
		}
		int start = 0;
		if(pathArray[0].equals("")) start = 1;
		else start = 0;
		for(int i=start; i<pathArray.length; i++)
		{
			boolean find = false;
			for(int j=0; j<parent.getChildren().size(); j++)
			{
				if(parent.getChildren().get(j).getName().equals(pathArray[i]))
				{
					parent = parent.getChildren().get(j);
					find = true;
					break;
				}
			}
			if(find)
			{
				continue;
			}

			if(i == pathArray.length - 1)
			{
				parent = new SqlDocument(parent, pathArray[i], Activator.getImageDescriptor("/icons/sqldoc.gif").createImage(display));
			}
			else
			{
				parent = new SqlFolder(parent, pathArray[i], Activator.getImageDescriptor("/icons/fldr_obj.gif").createImage(display));
			}
			
			if(caseSensitive.equals("true"))
			{
				if(pathArray[i].contains(keyWord) && searchFolder)
				{
					findCnt++;
				}
			}
			else
			{
				if(pathArray[i].toUpperCase().contains(keyWord.toUpperCase()) && searchFolder)
				{
					findCnt++;
				}
			}

		}
		treeViewer.refresh();
	}
	
	private void setTreeFolder(String keyWord, String caseSensitive, String path, boolean searchFolder)
	{
		String[] pathArray = path.split("\\.");
		
		Model root = (Model)treeViewer.getInput();
		Model parent = null;
		if(root.getChildren().size() <=0)
		{
			parent = new QueryStorageList(root, NBLabel.get(0x024F), Activator.getImageDescriptor("/icons/repo_rep.gif").createImage(display));
		}
		else
		{
			parent = root.getChildren().get(0);
		}
		int start = 0;
		if(pathArray[0].equals("")) start = 1;
		else start = 0;
		for(int i=start; i<pathArray.length; i++)
		{
			boolean find = false;
			for(int j=0; j<parent.getChildren().size(); j++)
			{
				if(parent.getChildren().get(j).getName().equals(pathArray[i]))
				{
					parent = parent.getChildren().get(j);
					find = true;
					break;
				}
			}
			if(find) continue;

			parent = new SqlFolder(parent, pathArray[i], Activator.getImageDescriptor("/icons/fldr_obj.gif").createImage(display));

			if(caseSensitive.equals("true"))
			{
				if(pathArray[i].contains(keyWord) && searchFolder)
				{
					findCnt++;
				}
			}
			else
			{
				if(pathArray[i].toUpperCase().contains(keyWord.toUpperCase()) && searchFolder)
				{
					findCnt++;
				}
			}
		}
		treeViewer.refresh();
	}
	
	private IDoubleClickListener genDbClickListener()
	{
		dbcListener = new IDoubleClickListener()
		{
			@SuppressWarnings("unchecked")
			public void doubleClick(DoubleClickEvent event) {
				
				if(event.getSelection() instanceof IStructuredSelection) 
				{
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();

					if(selection.getFirstElement() instanceof SqlFolder)
					{
						Object domain = (Model) selection.iterator().next();
						if(treeViewer.getExpandedState(domain))
						{
							treeViewer.collapseToLevel(domain, 1);
						}
						else
						{
							treeViewer.expandToLevel(domain, 1);
						}
					}
					else if(selection.getFirstElement() instanceof SqlDocument)
					{
						try {
							NBFields fields = getSqlDocument((SqlDocument)selection.getFirstElement());
							getSite().getPage().openEditor(new SqlEditorInput(((SqlDocument)selection.getFirstElement()).getName(),
									protocol,
									(String)fields.get("SQL"),
									(ArrayList<NBFields>)fields.get("PLG_LST"),
									getPath((SqlDocument)selection.getFirstElement()), 
									instanceName)
									, SqlEditor.ID);
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
					}
					if(selection.getFirstElement() instanceof SearchContents)
					{
						SqlEditor sqlEditor = null;
						try {
							NBFields fields = getSqlDocument((SqlDocument)((SearchContents)selection.getFirstElement()).getParent());
							sqlEditor = (SqlEditor)getSite().getPage().openEditor(new SqlEditorInput(((SearchContents)selection.getFirstElement()).getParent().getName(),
									protocol,
									(String)fields.get("SQL"),
									(ArrayList<NBFields>)fields.get("PLG_LST"),
									getPath((SqlDocument)((SearchContents)selection.getFirstElement()).getParent()), 
									instanceName)
									, SqlEditor.ID);
						} catch (PartInitException e) {
							IMessageBox.Error(shell, NBLabel.get(0x009E));
						}
						
						SearchContents searchContents = (SearchContents)selection.getFirstElement();
						sqlEditor.setCursorPosition(searchContents.getName(), searchContents.getSearchFrom(), searchParam.get("SCH_KEY"), searchParam.get("CASE").equals("true"));
					}
				}
			}
		};
		
		return dbcListener;
	}
	
	private NBFields getSqlDocument(SqlDocument sqlObject)
	{
		NBFields fields = new NBFields();

		fields.put(IPC.NB_LOAD_CLASS, "com.nabsys.management.sql.QueryConfig");
		fields.put(IPC.NB_INSTNCE_ID, instanceName);
		fields.put("CMD_CODE", "L");
		fields.put("PATH", getPath(sqlObject));

		try {
			fields = protocol.execute(fields);

			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
			}
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
		} catch (ProtocolException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		}
		
		return fields;
	}
	
	private String getPath(QueryStorageList parent)
	{
		String path = parent.getPath().replace("/", ".");

		String[] pathArray = path.split("\\.");
		
		if(pathArray.length < 3)
		{
			path = "";
		}
		else
		{
			path = "";
			for(int i=2; i<pathArray.length; i++)
			{
				path += pathArray[i];
				if(i != pathArray.length - 1) path += ".";
			}
		}
		
		return path;
	}
	
	public void dispose()
	{
		treeViewer.removeDoubleClickListener(dbcListener);
		super.dispose();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
