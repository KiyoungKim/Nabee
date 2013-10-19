package com.nabsys.nabeeplus.views.model;

public class PluginTypeArray {
		public static final int DATABASE = 0;
		public static final int CONNECTION = 1;
		public static final int OTHER = 2;
		
		public static final String[] TYPE = new String[] {"DATABASE", "CONNECTION",  "OTHER"};
		public static int getType(String type)
		{
			if(type.equals("DATABASE")) return DATABASE;
			else if(type.equals("CONNECTION")) return CONNECTION;
			else if(type.equals("OTHER")) return OTHER;
			else return -1; 
		}
}
