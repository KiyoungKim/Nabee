package com.nabsys.nabeeplus.views.model;

public class FieldTypeArray {
		public static final int CHAR = 0;
		public static final int NUM = 1;
		public static final int INT = 2;
		public static final int DOUBLE = 3;
		public static final int FLOAT = 4;
		public static final int LONG = 5;
		public static final int BYTE = 6;
		public static final int BYTEARR = 7;
		
		public static final String[] TYPE = new String[] {
			"CHAR", 
			"NUM",  
			"INT", 
			"DOUBLE",
			"FLOAT",
			"LONG",
			"BYTE",
			"BYTEARR"
		};
		public static final String[] SPL_TYPE = new String[] {
			"CHAR", 
			"INT", 
			"DOUBLE",
			"FLOAT",
			"LONG"
		};
		public static int getType(String type)
		{
			if(type.equals("CHAR")) return CHAR;
			else if(type.equals("NUM")) return NUM;
			else if(type.equals("INT")) return INT;
			else if(type.equals("DOUBLE")) return DOUBLE;
			else if(type.equals("FLOAT")) return FLOAT;
			else if(type.equals("LONG")) return LONG;
			else if(type.equals("BYTE")) return BYTE;
			else if(type.equals("BYTEARR")) return BYTEARR;
			else return -1; 
		}
		public static int getSplType(String type)
		{
			if(type.equals("CHAR")) return CHAR;
			else if(type.equals("INT")) return INT - 1;
			else if(type.equals("DOUBLE")) return DOUBLE - 1;
			else if(type.equals("FLOAT")) return FLOAT - 1;
			else if(type.equals("LONG")) return LONG - 1;
			else return -1; 
		}
}
