package com.nabsys.resource;

import java.io.IOException;

import com.nabsys.common.fileio.ObjectFileIO;

public class ServiceContext {
	private String 			id 				= null;
	private String 			name 			= null;
	private String			type 			= null;
	private String 			remark 			= null;
	private byte[]			serviceData		= null;
	private ServiceDesign	design			= null;
	private boolean 		activate 		= false;
	
	public ServiceContext(
			String id,
			String name,
			String type,
			String remark,
			byte[] serviceData,
			boolean activate)
	{
		this.id 			= id;
		this.name 			= name;
		this.type			= type;
		this.remark 		= remark;
		this.serviceData 	= serviceData;
		this.activate 		= activate;
	}
	
	public String getID(){
		return id;
	}
	public String getName(){
		return name;
	}
	public String getType(){
		return type;
	}
	public String getRemark(){
		return remark;
	}
	public ServiceDesign getServiceDesign(){
		if(this.design != null) return this.design;
		if(serviceData != null)
		{
			ObjectFileIO ofio = new ObjectFileIO();
			try {
				this.design = (ServiceDesign)ofio.recoverObject(serviceData);
				serviceData = null;
				return this.design;
			} catch (IOException e) {
				return null;
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}
	public boolean isActivate(){
		return activate;
	}
}
