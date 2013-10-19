package com.nabsys.nabeeplus.editors.input;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public class ServiceDesignerInput implements IEditorInput{
	
	private String id = "";
	private String instanceName = "";
	private IPCProtocol protocol = null;
	private NBFields fields = null;
	public ServiceDesignerInput(String instanceName, String id, IPCProtocol protocol, NBFields fields)
	{
		super();
		Assert.isNotNull(id);
		this.instanceName = instanceName;
		this.id = id;
		this.protocol = protocol;
		this.fields = fields;
	}
	
	public NBFields getFields()
	{
		return this.fields;
	}
	
	public IPCProtocol getProtocol() {
		return protocol;
	}

	public String getToolTipText() {
		return id;
	}
	
	public String getName() {
		return id;
	}
	
	public String getID() {
		return id;
	}

	public String getInstance(){
		return instanceName;
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

		if(!(obj instanceof ServiceDesignerInput)) return false;

		ServiceDesignerInput other = (ServiceDesignerInput) obj;
		return protocol.equals(other.protocol) && id.equals(other.id) && instanceName.equals(other.instanceName);
	}
	
	public int hashCode()
	{
		return id.hashCode();
	}
}
