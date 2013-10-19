package com.nabsys.nabeeplus.design.model;

import org.eclipse.swt.graphics.Image;

import com.nabsys.nabeeplus.views.model.TableModel;

public class BatchModel extends TableModel{
	private int min = 0;
	private int hur = 0;
	private int day = 0;
	private int mon = 0;
	private int yer	= 0;
	
	public BatchModel() {
		super();
	}
	
	public BatchModel(TableModel parent, String id) {
		super(parent, id);
	}
			
	@Override
	public String _getTxtValue(int columnIndex) {
		
		switch(columnIndex)
		{
		case 0:
			if(min > 1)
				return String.format("%02d", min - 2);
			else if(min == 1)
				return "*";
			else
				return "";
		case 1:
			if(hur > 1)
				return String.format("%02d", hur - 2);
			else if(hur == 1)
				return "*";
			else
				return "";
		case 2:
			if(day > 1)
				return String.format("%02d", day - 1);
			else if(day == 1)
				return "*";
			else
				return "";
		case 3:
			if(mon > 1)
				return String.format("%02d", mon - 1);
			else if(mon == 1)
				return "*";
			else
				return "";
		case 4:
			if(yer > 1)
				return Integer.toString(yer + 2010);
			else if(yer == 1)
				return "*";
			else
				return "";
		default:
		}
		return "";
	}
	
	public int getSendValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			if(min > 1)
				return min - 2;
			else
				return -1;
		case 1:
			if(hur > 1)
				return hur - 2;
			else
				return -1;
		case 2:
			if(day > 1)
				return day - 1;
			else
				return -1;
		case 3:
			if(mon > 1)
				return mon - 1;
			else
				return -1;
		case 4:
			if(yer > 1)
				return yer + 2010;
			else
				return -1;
		default:
		}
		
		return -1;
	}

	@Override
	public Object _getObjectValue(int columnIndex) {
		
		switch(columnIndex)
		{
		case 0:
			if(min <= 1) return 0;
			return min - 1;
		case 1:
			if(hur <= 1) return 0;
			return hur - 1;
		case 2:
			if(day <= 1) return 0;
			return day - 1;
		case 3:
			if(mon <= 1) return 0;
			return mon - 1;
		case 4:
			if(yer <= 1) return 0;
			return yer - 1;
		default:
		}
		return -1;
	}
	
	public Object getCurValue(int columnIndex) {
		
		switch(columnIndex)
		{
		case 0:
			return min;
		case 1:
			return hur;
		case 2:
			return day;
		case 3:
			return mon;
		case 4:
			return yer;
		default:
		}
		return -1;
	}

	public String _setObjectValue(int columnIndex, Object value) {

		switch(columnIndex)
		{
		case 0:
			min = (Integer) value+1;
			return "MIN";
		case 1:
			hur = (Integer) value+1;
			return "HUR";
		case 2:
			day = (Integer) value+1;
			return "DAY";
		case 3:
			mon = (Integer) value+1;
			return "MON";
		case 4:
			yer = (Integer) value+1;
			return "YER";
		default:
			return "__Non";
		}
	}

	@Override
	public Image _getImage(int columnIndex) {
		return null;
	}

}
