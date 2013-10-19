package com.nabsys.nabeeplus.editors.input;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public class SqlEditorInput implements IEditorInput{
	
	private String name = "";
	private String sql = "";
	private String path = "";
	private String instanceName = "";
	private ArrayList<NBFields> pluginList = null;
	
	public SqlEditorInput(String name, IPCProtocol protocol, String sql, ArrayList<NBFields> pluginList, String path, String instanceName)
	{
		super();
		Assert.isNotNull(name);

		this.name = name;
		this.protocol = protocol;
		this.sql = sql;
		this.path = path;
		this.instanceName = instanceName;
		this.pluginList = pluginList;
	}
	
	private IPCProtocol protocol = null;
	
	public IPCProtocol getProtocol() {
		return protocol;
	}
	
	public String getPath()
	{
		return this.path;
	}
	
	public String getInstance()
	{
		return this.instanceName;
	}
	
	public String getSql()
	{
		return this.sql;
	}
	
	public ArrayList<NBFields> getPluginList()
	{
		return this.pluginList;
	}

	public String getToolTipText() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public boolean equals(Object obj)
	{
		if(super.equals(obj)) return true;
		
		if(!(obj instanceof SqlEditorInput)) return false;
		
		SqlEditorInput other = (SqlEditorInput) obj;
		
		return protocol.equals(other.protocol) && path.equals(other.path);
	}
	
	public int hashCode()
	{
		return name.hashCode();
	}
}
