package com.nabsys.resource;

public class UserContext {
	private String 	id			= "";
	private String 	name 		= "";
	private String 	password 	= "";
	private String 	auth 		= "";
	private String 	phone 		= "";
	private boolean isActivate 	= false;

	public UserContext(String id, String name, String password, String auth, String phone, boolean isActivate)
	{
		this.id			= id;
		this.name 		= name;
		this.password 	= password;
		this.auth 		= auth;
		this.phone 		= phone;
		this.isActivate = isActivate;
	}
	
	public String getID()
	{
		return id;
	}
	
	public String getUserName()
	{
		return name;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getAuthorization()
	{
		//Admin, Operator, Developer, Application, (Instance : )
		return auth;
	}
	
	public String getPhone()
	{
		return phone;
	}
	
	public boolean isActivate()
	{
		return isActivate;
	}
}
