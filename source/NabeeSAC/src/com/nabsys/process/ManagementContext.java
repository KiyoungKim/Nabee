package com.nabsys.process;

import java.util.HashMap;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.nabee.ResourceParameter;

public class ManagementContext {
	
	private InstanceManager instances = null;
	private String instanceID = "";
	private ResourceParameter rscPrm = null;
	private NBFields fields = null;
	private String userAuthority = "";
	private String user = "";
	private String clientAddress = "";
	private HashMap<Object, Object> sessionData = new HashMap<Object, Object>();
	
	public void setFields(NBFields fields)
	{
		this.fields = fields;
	}
	
	public void setInstances(InstanceManager instances)
	{
		this.instances = instances;
	}
	
	public void setInstanceID(String instanceID)
	{
		this.instanceID = instanceID;
	}
	
	public void setResourceParameters(ResourceParameter rscPrm)
	{
		this.rscPrm = rscPrm;
	}
	
	
	public NBFields getFields()
	{
		return this.fields;
	}
	
	public InstanceManager getInstances()
	{
		return this.instances;
	}
	
	public String getInstanceID()
	{
		return this.instanceID;
	}
	
	public ResourceParameter getResourceParameters()
	{
		return this.rscPrm;
	}

	public String getUserAuthority() {
		return userAuthority;
	}

	public void setUserAuthority(String userAuthority) {
		this.userAuthority = userAuthority;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
	
	public void setSessionData(Object key, Object value) {
		this.sessionData.put(key, value);
	}
	
	public Object getSessionData(Object key) {
		return this.sessionData.get(key);
	}
	
	public void removeSessionData(Object key) {
		this.sessionData.remove(key);
	}
	
	public void clearSessionData()
	{
		this.sessionData = new HashMap<Object, Object>();
	}
}
