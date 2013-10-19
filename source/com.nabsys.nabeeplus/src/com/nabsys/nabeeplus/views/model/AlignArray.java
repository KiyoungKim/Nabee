package com.nabsys.nabeeplus.views.model;

public class AlignArray {
		public static final int LEFT = 0;
		public static final int CENTER = 1;
		public static final int RIGHT = 2;
		
		public static final String[] ALIGN = new String[] {
			"LEFT", 
			"CENTER",  
			"RIGHT"
			};
		public static int getType(String type)
		{
			if(type.equals("LEFT")) return LEFT;
			else if(type.equals("CENTER")) return CENTER;
			else if(type.equals("RIGHT")) return RIGHT;
			else return -1; 
		}
}
