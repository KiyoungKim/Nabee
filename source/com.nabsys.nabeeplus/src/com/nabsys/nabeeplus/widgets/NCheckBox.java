package com.nabsys.nabeeplus.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.nabsys.nabeeplus.listener.NBModifiedListener;

public class NCheckBox{
	
	private SelectionListener listener = null;
	private String field = "";
	private String truetext = "";
	private String falsetext = "";
	private Button button = null;
	
	public NCheckBox(Composite parent, String field, String truetext, String falsetext, int style) {
		this.button = new Button(parent, style | SWT.CHECK);
		
		this.field = field;
		this.truetext = truetext;
		this.falsetext = falsetext;
		this.button.setText(" " + falsetext);
		
		this.button.addDisposeListener(new DisposeListener(){

			public void widgetDisposed(DisposeEvent e) {
				if(listener != null)
				button.removeSelectionListener(listener);
			}
			
		});
	}
	
	public String getFieldName()
	{
		return field;
	}
	
	public void setLayoutData(GridData layout)
	{
		this.button.setLayoutData(layout);
	}
	
	public void setBackground(Color color)
	{
		this.button.setBackground(color);
	}
	
	public void setSelection(boolean selected)
	{
		this.button.setSelection(selected);
		
		if(selected)
			this.button.setText(" " + truetext);
		else
			this.button.setText(" " + falsetext);
	}
	
	public boolean getSelection()
	{
		return button.getSelection();
	}

	public void addNBModifiedListener(final NBModifiedListener mdfListener)
	{
		button.addSelectionListener(this.listener = new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(((Button)e.widget).getSelection())
				{
					((Button)e.widget).setText(" " + truetext);
					mdfListener.modified(field, "true");
				}
				else
				{
					((Button)e.widget).setText(" " + falsetext);
					mdfListener.modified(field, "false");
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
	}
}
