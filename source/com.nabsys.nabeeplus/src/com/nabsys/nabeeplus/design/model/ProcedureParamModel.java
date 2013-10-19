package com.nabsys.nabeeplus.design.model;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.views.model.TableModel;

public class ProcedureParamModel  extends TableModel{
	public static final int IN 					= 0;
	public static final int OUT 				= 1;
	private int				index				= 0;
	private int 			direction 			= IN;
	private int 			type 				= Types.ARRAY;
	private static Image 	CHECKED 			= null;
	private static Image 	UNCHECKED 			= null;
	private boolean 		isMapping 			= false;
	private String 			value 				= "";

	public ProcedureParamModel() {
		super();
	}
	
	public ProcedureParamModel(TableModel parent, String id, Shell shell) {
		super(parent, id);
		CHECKED = Activator.getImageDescriptor("/icons/checked.gif").createImage(shell.getDisplay());
		UNCHECKED = Activator.getImageDescriptor("/icons/unchecked.gif").createImage(shell.getDisplay());
	}

	public Image _getImage(int columnIndex)
	{
		if(columnIndex == 3)
		{
			if(isMapping) return CHECKED;
			else return UNCHECKED;
		}
		return null;
	}
	
	public String _getTxtValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			if(index == 0) return "";
			else return getIndex() + "";
		case 1:
			if(direction == IN) return "IN";
			else return "OUT";
		case 2:
			return SqlTypeArray.toString(type);
		case 3:
			return "";
		case 4:
			return value;
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getIndex();
		case 1:
			return direction;
		case 2:
			return SqlTypeArray.getIndex(type);
		case 3:
			return isMapping;
		case 4:
			return value;
		default:
		}
		return null;
	}
	
	public String _setObjectValue(int columnIndex, Object value)
	{
		switch(columnIndex)
		{
		case 0:
			if(index != (Integer)value) isModified = true;
			index = (Integer)value;
			break;
		case 1:
			if((Integer)value == OUT)
			{
				isModified = true;
			}
			if(direction != (Integer)value) isModified = true;
			direction = (Integer)value;
			break;
		case 2:
			if(type != SqlTypeArray.getType((Integer)value)) isModified = true;
			type = SqlTypeArray.getType((Integer)value);
			break;
		case 3:
			if(direction == OUT)
			{
				isModified = true;
			}
			else
			{
				if(isMapping != (Boolean)value) isModified = true;
				isMapping = (Boolean)value;
			}
			break;
		case 4:
			if(!this.value.equals((String)value)) isModified = true;
			this.value = (String)value;
			break;
		default:
		}
		
		if(isModified)
		{
			switch(columnIndex)
			{
			case 0:
				return "ID";
			case 1:
				return "DR";
			case 2:
				return "TP";
			case 3:
				return "IM";
			case 4:
				return "VL";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
	}
	
	public int getIndex()
	{
		index =  this.getParent().getChildren().indexOf(this) + 1;
		return index;
	}
	
	public void setDirection(int direction)
	{
		this.direction = direction;
	}
	
	public int getDirection()
	{
		return direction;
	}
	
	
	public int getType() {
		return type;
	}
		
	public void setType(int type) {
		this.type = type;
	}
	
	public void setMapping(){
		isMapping = true;
	}
	public boolean isMapping(){
		return isMapping;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public Object getConvertValue()
	{
		if(value.equals("")) return null;
		
		if(isMapping)
		{
			return value;
		}
		else
		{
			if(type == Types.BIGINT) return new BigInteger(value);
			else if(type == Types.BOOLEAN) return value.toUpperCase().equals("TRUE");
			else if(type == Types.CHAR) return value.substring(0, 1);
			else if(type == Types.DATE) return new Date(Long.parseLong(value));
			else if(type == Types.DECIMAL) return Double.parseDouble(value);
			else if(type == Types.DOUBLE) return Double.parseDouble(value);
			else if(type == Types.FLOAT) return Float.parseFloat(value);
			else if(type == Types.INTEGER) return Integer.parseInt(value);
			else if(type == Types.LONGVARCHAR) return value;
			else if(type == Types.NUMERIC) return Double.parseDouble(value);
			else if(type == Types.SMALLINT) return Integer.parseInt(value);
			else if(type == Types.TIME) return new Time(Long.parseLong(value));
			else if(type == Types.TIMESTAMP) return new Timestamp(Long.parseLong(value));
			else if(type == Types.TINYINT) return Integer.parseInt(value);
			else if(type == Types.VARCHAR) return value;
			else return null;
		}
	}
}
