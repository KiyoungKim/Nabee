package com.nabsys.nabeeplus.design.model;

public class FieldTypeArray {
		public static final int CHAR = 0;
		public static final int INT = 1;
		public static final int DOUBLE = 2;
		public static final int FLOAT = 3;
		public static final int LONG = 4;
		public static final int BYTE = 5;
		public static final int BYTEARR = 6;
		
		public static final String[] TYPE = new String[] {
			"CHAR", 
			"INT", 
			"DOUBLE",
			"FLOAT",
			"LONG",
			"BYTE",
			"BYTEARR"
			};
		public static int getType(String type)
		{
			if(type.equals("CHAR")) return CHAR;
			else if(type.equals("INT")) return INT;
			else if(type.equals("DOUBLE")) return DOUBLE;
			else if(type.equals("FLOAT")) return FLOAT;
			else if(type.equals("LONG")) return LONG;
			else if(type.equals("BYTE")) return BYTE;
			else if(type.equals("BYTEARR")) return BYTEARR;
			else return -1; 
		}
}
