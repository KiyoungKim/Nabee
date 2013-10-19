package com.nabsys.nabeeplus.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.nabsys.nabeeplus.listener.NBModifiedListener;

public class NStyledText extends StyledText {

	private ModifyListener 	modifyListener 		= null;
	private FocusListener	focusListener		= null;
	private VerifyListener	verifyListener		= null;
	private String 			field 				= "";
	private Button 			defaultButton 		= null;
	private boolean			isNumber			= false;
	
	public NStyledText(Composite parent, int style) {
		super(parent, style);
		genFocusListener();
	}
	
	public NStyledText(Composite parent, String field, int style) {
		super(parent, style);
		this.field = field;
		genFocusListener();
	}
	
	public void setNumberStyle(boolean isNumber)
	{
		if(!this.isNumber && isNumber)
		{
			this.addVerifyListener(verifyListener = new VerifyListener(){
				public void verifyText(VerifyEvent e) {
					e.doit = e.text.matches("[0-9]*");
				}
			});
		}
		else if(this.isNumber && !isNumber)
		{
			if(verifyListener != null)
			{
				this.removeVerifyListener(verifyListener);
				verifyListener = null;
			}
		}
		
		this.isNumber = isNumber;
	}
	
	public String getFieldName()
	{
		return field;
	}
	
	public void setDefaultButton(Button button)
	{
		this.defaultButton = button;
	}
	
	public void setEditable(boolean editable, boolean delKeyEanble)
	{
		super.setEditable(editable);
		if(delKeyEanble)
		{
			keyLIstener = genDeleteListener(this);
			this.addKeyListener(keyLIstener);
		}
	}
	
	private KeyListener keyLIstener = null;
	
	private KeyListener genDeleteListener(final Composite obj)
	{
		return new KeyListener(){

			public void keyPressed(KeyEvent e) {
				if(e.widget == obj && e.keyCode == SWT.DEL)
				{
					((NStyledText)e.widget).setText("");
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		};
	}
	
	private void genFocusListener()
	{
		this.addFocusListener(this.focusListener = new FocusListener(){
			public void focusGained(FocusEvent e) {
				getShell().setDefaultButton(defaultButton);
			}

			public void focusLost(FocusEvent e) {
				getShell().setDefaultButton(null);
			}
		});
	}
	
	public void addNBModifiedListener(final NBModifiedListener mdfListener)
	{
		this.addModifyListener(this.modifyListener = new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				mdfListener.modified(field, ((NStyledText)e.widget).getText());
			}
			
		});
	}
	
	public void setDisableEditing(boolean disable, Display display)
	{
		if(disable)
		{
			setText("");
			setEditable(false);
			setBackground(new Color(display, 240, 240, 240));
			setForeground(new Color(display, 150, 150, 150));
		}
		else
		{
			setEditable(true);
			setBackground(display.getSystemColor(SWT.COLOR_WHITE));
			setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		}
	}
	
	public void dispose()
	{
		if(verifyListener != null)
			this.removeVerifyListener(verifyListener);
		if(modifyListener != null)
			this.removeModifyListener(modifyListener);
		if(focusListener != null)
			this.removeFocusListener(focusListener);
		if(keyLIstener != null) 
			this.removeKeyListener(keyLIstener);
		super.dispose();
	}

}
