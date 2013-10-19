package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.nabsys.nabeeplus.Activator;


public class Field extends TableModel {
	private int no = 0;
	private String name = "";
	private int type = 0;
	private int length = 0;
	private int align = 0;
	private boolean mandatory = false;
	private String padding = "";
	private String remark = "";
	private static Image 	CHECKED = null;
	private static Image 	UNCHECKED = null;
	
	public Field() {
		super();
	}
	
	public Field(TableModel parent, String id) {
		super(parent, id);
	}
	
	public Field(TableModel parent, String id, Display display) {
		super(parent, id);
		CHECKED = Activator.getImageDescriptor("/icons/checked.gif").createImage(display);
		UNCHECKED = Activator.getImageDescriptor("/icons/unchecked.gif").createImage(display);
	}

	public Image _getImage(int columnIndex)
	{
		if(columnIndex == 6)
		{
			if(mandatory) return CHECKED;
			else return UNCHECKED;
		}
		
		return null;
	}
	
	public String _getTxtValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return getFieldNo();
		case 1:
			return getFieldID();
		case 2:
			return getFieldName();
		case 3:
			return getFieldType();
		case 4:
			return getFieldLength();
		case 5:
			return getFieldAlign();
		case 6:
			return "";
		case 7:
			return getFieldPadding();
		case 8:
			return getFieldRemark();
		default:
		}
		return "";
	}
	
	public Object _getObjectValue(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return no;
		case 1:
			return getFieldID();
		case 2:
			return name;
		case 3:
			return type;
		case 4:
			return Integer.toString(length);
		case 5:
			return align;
		case 6:
			return mandatory;
		case 7:
			return padding;
		case 8:
			return remark;
		default:
		}
		return "";
	}
	
	private int setLengthField(int type, int value)
	{
		switch(type)
		{
		case FieldTypeArray.BYTE:
			return Byte.SIZE / Byte.SIZE;
		case FieldTypeArray.DOUBLE:
			return Double.SIZE / Byte.SIZE;
		case FieldTypeArray.FLOAT:
			return Float.SIZE / Byte.SIZE;
		case FieldTypeArray.INT:
			return Integer.SIZE / Byte.SIZE;
		case FieldTypeArray.LONG:
			return Long.SIZE / Byte.SIZE;
		default:
			return value;
		}
	}
	
	public String _setObjectValue(int columnIndex, Object value)
	{
		
		switch(columnIndex)
		{
		case 0:
			if(no != (Integer)value) isModified = true;
			no = (Integer)value;
			break;
		case 1:
			if(!super.getID().equals((String)value)) isModified = true;
			super.setID((String)value);
			break;
		case 2:
			if(!name.equals((String)value)) isModified = true;
			setFieldName((String)value);
			break;
		case 3:
			if(type != (Integer)value) isModified = true;
			type = (Integer)value;
			length = setLengthField(type, length);
			break;
		case 4:
			if(length != Integer.parseInt((String)value)) isModified = true;
			length = setLengthField(type, Integer.parseInt((String)value));
			break;
		case 5:
			if(align != (Integer)value) isModified = true;
			align = (Integer)value;
			break;
		case 6:
			if(mandatory != (Boolean)value) isModified = true;
			mandatory = (Boolean)value;
			break;
		case 7:
			if(padding != (String)value) isModified = true;
			setFieldPadding((String)value);
			break;
		case 8:
			if(remark != (String)value) isModified = true;
			setFieldRemark((String)value);
			break;
		default:
		}
		
		if(isModified)
		{
			switch(columnIndex)
			{
			case 0:
				return "NO";
			case 1:
				return "ID";
			case 2:
				return "NAME";
			case 3:
				return "TYPE";
			case 4:
				return "LENGTH";
			case 5:
				return "ALIGN";
			case 6:
				return "MANDATORY";
			case 7:
				return "PADDING";
			case 8:
				return "REMARK";
			default:
				return "__Non";
			}
		}
		else
		{
			return "__Non";
		}
		
	}
	
	
	public String getFieldID() {
		return super.getID();
	}
	
	public String getFieldNo()
	{
		no =  this.getParent().getChildren().indexOf(this) + 1;
		return Integer.toString(no);
	}
	
	public String getFieldName() {
		return name;
	}
	
	public void setFieldName(String name) {
		this.name = name;
	}
	
	public String getFieldType() {
		return FieldTypeArray.TYPE[type];
	}

	public void setFieldType(int type) {
		this.type = type;
	}
	
	public String getFieldLength() {
		return Integer.toString(length);
	}

	public void setFieldLength(int length) {
		this.length = length;
	}
	
	public String getFieldAlign() {
		return AlignArray.ALIGN[align];
	}

	public void setFieldAlign(int align) {
		this.align = align;
	}
	
	public Boolean isFieldMandatory() {
		return mandatory;
	}

	public void setFieldMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public String getFieldPadding() {
		return this.padding;
	}

	public void setFieldPadding(String padding) {
		this.padding = padding;
	}
	
	public String getFieldRemark() {
		return this.remark;
	}

	public void setFieldRemark(String remark) {
		this.remark = remark;
	}

	public void setModified(boolean isModified) {
		super.isModified = isModified;
	}
	
	
}
