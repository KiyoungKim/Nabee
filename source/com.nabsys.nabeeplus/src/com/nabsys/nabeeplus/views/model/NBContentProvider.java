package com.nabsys.nabeeplus.views.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public class NBContentProvider implements ITreeContentProvider{//, INBListener{
	
	private static 	Object[] 			EMPTY_ARRAY 	= new Object[0];
	protected 		TreeViewer 			viewer			= null;
	private 		IPCProtocol 		protocol 		= null;
	private			Display				display			= null;
	private			Shell				shell			= null;
	private final 	int 				DOCUMENT 		= 0;
	private final 	int 				FOLDER 			= 1;
	private			boolean				activeSearch	= false;
	
	public NBContentProvider()
	{
	}
	
	public NBContentProvider(Display display, boolean activeSearch)
	{
		this.display = display;
		this.shell = display.getActiveShell();
		this.activeSearch = activeSearch;
	}
	
	public void setProtocol(IPCProtocol protocol)
	{
		this.protocol = protocol;
	}
	
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object parentElement) {

		if(parentElement instanceof QueryStorageList && activeSearch)
		{
			if(parentElement instanceof SqlDocument) return EMPTY_ARRAY;
				
			if(((QueryStorageList) parentElement).isVisited())
			{
				return ((Model)parentElement).getChildren().toArray();
			}
			
			NBFields fields = new NBFields();

			String path = ((QueryStorageList)parentElement).getPath().replace("/", ".");
			String[] pathArray = path.split("\\.");
			
			if(pathArray.length < 4)
			{
				path = "";
			}
			else
			{
				path = "";
				for(int i=3; i<pathArray.length; i++)
				{
					path += pathArray[i];
					if(i != pathArray.length - 1) path += ".";
				}
			}

			String instanceName = pathArray[1];

			fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.sql.QueryConfig");
			fields.put("CMD_CODE"			, "L");
			fields.put("PATH"				, path);
			fields.put(IPC.NB_INSTNCE_ID	, instanceName);
			
			try {
				fields = protocol.execute(fields);

				if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
				{
					IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				}
				else
				{
					setSubSqlPath(fields, (QueryStorageList)parentElement);
					
					Model model = (Model)parentElement;
					model.setVisited(true);
					return model.getChildren().toArray();
				}
			} catch (SocketClosedException e) {
				IMessageBox.Error(shell, NBLabel.get(0x022C));
			} catch (TimeoutException e) {
				IMessageBox.Error(shell, e.getMessage());
			} catch (NetException e) {
				IMessageBox.Error(shell, NBLabel.get(0x022C));
			} catch (UnsupportedEncodingException e) {
				IMessageBox.Error(shell, NBLabel.get(0x022C));
			} catch (NoSuchAlgorithmException e) {
				IMessageBox.Error(shell, NBLabel.get(0x022C));
			} catch (ProtocolException e) {
				IMessageBox.Error(shell, NBLabel.get(0x022C));
			} catch (DataTypeException e) {
				IMessageBox.Error(shell, NBLabel.get(0x022C));
			} catch (NullPointerException e){
				IMessageBox.Error(shell, NBLabel.get(0x022C));
			}
		}
		else if(parentElement instanceof Model)
		{
			Model model = (Model)parentElement;
			return model.getChildren().toArray();
	    }
	    return EMPTY_ARRAY;
	}
	
	@SuppressWarnings("unchecked")
	private void setSubSqlPath(NBFields fields, QueryStorageList parent)
	{
		ArrayList<NBFields> list = (ArrayList<NBFields>)fields.get("LIST");

		for(int i=0; i<list.size(); i++)
		{
			NBFields tmp = list.get(i);
			if((Integer)tmp.get("TYPE") == DOCUMENT)
			{
				new SqlDocument(parent, (String)tmp.get("ID"), Activator.getImageDescriptor("/icons/sqldoc.gif").createImage(display));
			}
			else if((Integer)tmp.get("TYPE") == FOLDER)
			{
				new SqlFolder(parent, (String)tmp.get("ID"), Activator.getImageDescriptor("/icons/fldr_obj.gif").createImage(display));
			}
		}
	}

	public Object getParent(Object element) {
		if(element instanceof Model)
		{
			return ((Model)element).getParent();
		}
		
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof QueryStorageList)
		{
			if(element instanceof SqlDocument && activeSearch)
			{
				return false;
			}
			else if(element instanceof SearchContents)
			{
				return false;
			}
			else
			{
				if(((QueryStorageList) element).isVisited() || !activeSearch)
				{
					return getChildren(element).length > 0;
				}
				else
				{
					return true;
				}
			}
		}
		
		return getChildren(element).length > 0;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		
		/*if(oldInput != null)
		{
			removeListenerFrom((Model)oldInput);
		}
		
		if(newInput != null)
		{
			addListenerTo((Model)newInput);
		}*/
	}
	
	/*protected void removeListenerFrom(Model model) {
		
		model.removeListener(this);
		for(Iterator<Model> iterator = model.getChildren().iterator(); iterator.hasNext();)
		{
			Model aModel = (Model) iterator.next();
			removeListenerFrom(aModel);
		}
	}
	
	protected void addListenerTo(Model model) {
		model.addListener(this);
		for (Iterator<Model> iterator = model.getChildren().iterator(); iterator.hasNext();)
		{
			Model aModel = (Model) iterator.next();
			addListenerTo(aModel);
		}
	}

	public void add(NBEvent event) {
		Object model = ((Model)event.receiver()).getParent();
		viewer.refresh(model, false);
	}

	public void remove(NBEvent event) {
		add(event);
	}*/

}
