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

public class NRadio{
	
	private SelectionListener listener = null;
	private String field = "";
	private Button button = null;
	
	public NRadio(Composite parent, String field, String text, int style) {
		this.button = new Button(parent, style | SWT.RADIO);
		
		this.field = field;
		this.button.setText(" " + text);
		
		this.button.addDisposeListener(new DisposeListener(){

			public void widgetDisposed(DisposeEvent e) {
				if(listener != null)
				button.removeSelectionListener(listener);
			}
			
		});
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
					mdfListener.modified(field, "true");
				}
				else
				{
					mdfListener.modified(field, "false");
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
	}
}
