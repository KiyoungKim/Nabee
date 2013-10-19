package com.nabsys.resource;

public class BatchServiceContext extends ServiceContext {

	public BatchServiceContext(String id, String name,
			String type, String remark, byte[] serviceData,
			boolean activate) {
		super(id, name, type, remark, serviceData, activate);
	}

}
