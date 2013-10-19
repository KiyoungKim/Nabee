package com.nabsys.nabeeplus.editors.input;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public class InstanceConfigEditorInput implements IEditorInput{
	
	String name = "";
	String work = "";
	
	public InstanceConfigEditorInput(String name, String work, IPCProtocol protocol, NBFields fields)
	{
		super();
		Assert.isNotNull(name);

		this.name = name;
		this.work = work;
		this.protocol = protocol;
		this.fields = fields;
	}
	
	private IPCProtocol protocol = null;
	private NBFields fields = null;
	
	public IPCProtocol getProtocol() {
		return protocol;
	}

	public String getToolTipText() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getWork(){
		return work;
	}
	
	public NBFields getFields()
	{
		return fields;
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
		
		if(!(obj instanceof InstanceConfigEditorInput)) return false;
		
		InstanceConfigEditorInput other = (InstanceConfigEditorInput) obj;
		
		return protocol.equals(other.protocol) && name.equals(other.name);
	}
	
	public int hashCode()
	{
		return name.hashCode();
	}
}
