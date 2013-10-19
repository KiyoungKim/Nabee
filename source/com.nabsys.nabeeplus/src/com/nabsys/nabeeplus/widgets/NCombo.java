package com.nabsys.nabeeplus.widgets;

import java.util.ArrayList;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.nabsys.nabeeplus.listener.NBModifiedListener;

public class NCombo{
	private ModifyListener listener = null;
	private String field = "";
	private Combo combo = null;
	
	public NCombo(Composite parent, String field, int style) {
		
		this.combo = new Combo(parent, style);
		this.field = field;
		
		combo.addDisposeListener(new DisposeListener(){

			public void widgetDisposed(DisposeEvent e) {
				if(listener != null)
				combo.removeModifyListener(listener);
			}
			
		});
		
	}
	
	public String getFieldName()
	{
		return field;
	}
	
	public int getSelectionIndex()
	{
		return combo.getSelectionIndex();
	}
	
	public void setLayoutData(GridData layout)
	{
		this.combo.setLayoutData(layout);
	}
	
	public void setBackground(Color color)
	{
		this.combo.setBackground(color);
	}

	public void addNBModifiedListener(final NBModifiedListener mdfListener)
	{
		combo.addModifyListener(this.listener = new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				mdfListener.modified(field, getValue());
			}
			
		});
	}
	
	public void addMouseListener(MouseListener listener){
		combo.addMouseListener(listener);
	}
	
	public void removeMouseListener(MouseListener listener){
		combo.removeMouseListener(listener);
	}
	
	public void add(String string)
	{
		combo.add(string);
	}
	
	private ArrayList<String> valueList = new ArrayList<String>();
	public void add(String key, String value)
	{
		combo.add(key);
		valueList.add(value);
	}
	
	public void add(String string, int index)
	{
		combo.add(string, index);
	}
	
	public void setTextByValue(String value)
	{
		for(int i=0; i<valueList.size(); i++)
		{
			if(valueList.get(i).equals(value))
			{
				combo.setText(combo.getItem(i));
				return;
			}
		}
		
		combo.setText(combo.getItem(0));
	}
	
	public void setText(String string)
	{
		combo.setText(string);
	}
	
	public void clearSelection()
	{
		combo.clearSelection();
	}
	
	public void removeAll()
	{
		combo.removeAll();
	}
	
	public String getItem(int index)
	{
		return combo.getItem(index);
	}
	
	public int getItemCount()
	{
		return combo.getItemCount();
	}
	
	public String[] getItems()
	{
		return combo.getItems();
	}
	
	public void setItem(int index, String string)
	{
		combo.setItem(index, string);
	}
	
	public void setItems(String[] items)
	{
		combo.setItems(items);
	}
	
	public String getText()
	{
		return combo.getText();
	}
	
	public String getValue()
	{
		if(valueList.size() > 0)
			return valueList.get(combo.getSelectionIndex());
		else
			return getText();
	}
	
	public Control[] getChildren()
	{
		return combo.getChildren();
	}
	
	public Object getData()
	{
		return combo.getData();
	}
	
	public void setData(Object data)
	{
		combo.setData(data);
	}

	
	public Object getData(String key)
	{
		return combo.getData(key);
	}
	
	public void getData(String key, Object data)
	{
		combo.setData(key, data);
	}
	
	public void setEnabled(boolean enabled)
	{
		combo.setEnabled(enabled);
	}
	
	public boolean getEnabled()
	{
		return combo.getEnabled();
	}
}
