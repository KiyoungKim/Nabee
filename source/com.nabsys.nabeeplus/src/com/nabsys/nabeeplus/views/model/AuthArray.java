package com.nabsys.nabeeplus.views.model;

public class AuthArray {
		public static final int ADMIN = 0;
		public static final int OPERATOR = 1;
		public static final int DEVELOPER = 2;
		
		public static final String[] AUTH = new String[] {"Admin", "Operator",  "Developer"};
		public static int getAuth(String auth)
		{
			if(auth.equals("Admin")) return ADMIN;
			else if(auth.equals("Operator")) return OPERATOR;
			else if(auth.equals("Developer")) return DEVELOPER;
			else return -1; 
		}
}
