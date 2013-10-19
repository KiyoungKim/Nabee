package com.nabsys.nabeeplus.design.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.views.model.TableModel;

public class ComponentArgumentModel  extends TableModel{
	private String			type 				= null;
	private String			argumentName		= "";
	private boolean 		isUseSystemValue	= false;
	private static Image 	CHECKED 			= null;
	private static Image 	UNCHECKED 			= null;
	private String			argumentValue		= "";
	private int				systemValue			= 0;
	private String[] 		items				= new String[]{};
	private Shell 			shell 				= null;
	public ComponentArgumentModel() {
		super();
	}
	
	public ComponentArgumentModel(TableModel parent, String id, Shell shell) {
		super(parent, id);
		this.shell = shell;
		CHECKED = Activator.getImageDescriptor("/icons/checked.gif").createImage(shell.getDisplay());
		UNCHECKED = Activator.getImageDescriptor("/icons/unchecked.gif").createImage(shell.getDisplay());
	}

	public Image _getImage(int columnIndex)
	{
		if(columnIndex == 2)
		{
			if(isUseSystemValue) return CHECKED;
			else return UNCHECKED;
		}
		
		return null;
	}
	
	public String _getTxtValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			String str = "";
			if(type.equals("I")){str = "int";}//int
			else if(type.equals("J")){str = "long";}//long
			else if(type.equals("D")){str = "double";}//double
			else if(type.equals("F")){str = "float";}//float
			else if(type.equals("B")){str = "byte";}//byte
			else if(type.equals("C")){str = "char";}//char
			else if(type.equals("Z")){str = "boolean";}//boolean
			else if(type.substring(0, 1).equals("L")){str = type.substring(1).substring(type.lastIndexOf("/")).replace(";", "");}
			else if(type.substring(0, 1).equals("["))
			{
				String chkType = type.substring(1, 2);
				if(chkType.equals("I")){str = "int[ ]";}//int
				else if(chkType.equals("J")){str = "long[ ]";}//long
				else if(chkType.equals("D")){str = "double[ ]";}//double
				else if(chkType.equals("F")){str = "float[ ]";}//float
				else if(chkType.equals("B")){str = "byte[ ]";}//byte
				else if(chkType.equals("C")){str = "char[ ]";}//char
				else if(chkType.equals("Z")){str = "boolean[ ]";}//boolean
				else if(chkType.equals("L")){str = type.substring(1).substring(type.lastIndexOf("/")).replace(";", "") + "[ ]";}
			}
			return str;
		case 1:
			return argumentName;
		case 2:
			return "";
		case 3:
			return items[systemValue];
		case 4:
			return argumentValue;
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 2:
			return isUseSystemValue;
		case 3:
			return systemValue;
		case 4:
			return argumentValue;
		default:
		}
		return "";
	}
	
	public String _setObjectValue(int columnIndex, Object value)
	{
		switch(columnIndex)
		{
		case 2:
			isUseSystemValue = (Boolean)value;
			if(!isUseSystemValue) systemValue = 0;
			break;
		case 3:
			systemValue = (Integer)value;
			if(systemValue != 0) argumentValue = "";
			break;
		case 4:
			if(systemValue != 0) argumentValue = "";
			else argumentValue = (String)value;
			break;
		}
		switch(columnIndex)
		{
		case 2:
			return "SYS_VAL_USE";
		case 3:
			return "SYS_VAL";
		case 4:
			return "ARG_VAL";
		default:
			return "__Non";
		}
	}
	
	public void setArgumentComboList(String[] items)
	{
		this.items = items;
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getArgumentName()
	{
		return argumentName;
	}
	
	public void setArgumentName(String argumentName)
	{
		this.argumentName = argumentName;
	}
	
	public boolean isUseSystemValue()
	{
		return isUseSystemValue;
	}
	
	public void setUseSystemValue(boolean isUseSystemValue)
	{
		this.isUseSystemValue = isUseSystemValue;
	}
	
	public Object getArgumentValue()
	{
		if(isUseSystemValue)
		{
			if(systemValue == 0)
			{
				return argumentValue;
			}
			else
			{
				return items[systemValue];
			}
		}
		else
		{
			if(type.equals("Ljava/lang/Integer;") || type.equals("I")
				||	type.equals("Ljava/lang/Long;") || type.equals("J")
				||	type.equals("Ljava/lang/Float;") || type.equals("F")
				||	type.equals("Ljava/lang/Double;") || type.equals("D"))
			{
				try{
					Double.parseDouble(argumentValue);
				}catch(NumberFormatException e){
					IMessageBox.Error(shell, "Number format error.");
					return null;
				}
			}
				
			if(type.equals("Ljava/lang/String;"))return argumentValue;
			else if(type.equals("Ljava/lang/Integer;") || type.equals("I"))return Integer.parseInt(argumentValue);
			else if(type.equals("Ljava/lang/Long;") || type.equals("J"))return Long.parseLong(argumentValue);
			else if(type.equals("Ljava/lang/Float;") || type.equals("F"))return Float.parseFloat(argumentValue);
			else if(type.equals("Ljava/lang/Double;") || type.equals("D"))return Double.parseDouble(argumentValue);
			else if(type.equals("Z")) return argumentValue.equals("true");
			else{
				IMessageBox.Error(shell, "Data format error.");
				return null;
			}
		}
	}
	
	public void setArgumentValue(Object argumentValue)
	{
		if(isUseSystemValue)
		{
			for(int i=0; i<items.length; i++)
			{
				if(items[i].equals(argumentValue+""))
				{
					systemValue = i;
					return;
				}
			}
			
			this.systemValue = 0;
			this.argumentValue = argumentValue + "";
		}
		else
		{
			this.argumentValue = argumentValue + "";
		}
	}

}
