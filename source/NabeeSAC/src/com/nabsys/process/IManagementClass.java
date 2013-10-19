package com.nabsys.process;

import com.nabsys.net.protocol.NBFields;

public interface IManagementClass {
	public NBFields execute(ManagementContext context, long clientSequence);
}
