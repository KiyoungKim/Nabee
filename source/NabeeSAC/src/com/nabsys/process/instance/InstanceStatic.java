package com.nabsys.process.instance;


public class InstanceStatic {
	public InstanceStatic(){}
	
	private static String INSTANCE_NAME = "";
	private static String INSTANCE_PW = "";
	
	public static void setInstanceName(String name)
	{
		INSTANCE_NAME = name;
	}
	
	public static String getInstanceName()
	{
		return INSTANCE_NAME;
	}
	
	public static void setServerLoginPW(String pw) {
		INSTANCE_PW = pw;
	}

	public static String getServerLoginPW() {
		return INSTANCE_PW;
	}
}
