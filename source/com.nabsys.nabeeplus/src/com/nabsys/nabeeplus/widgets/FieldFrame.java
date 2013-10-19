package com.nabsys.nabeeplus.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class FieldFrame extends Composite {
	
	private Display 		display 		= null;
	private int				nameWidth		= 0;
	private CLabel 			lblLabel 		= null;
	
	public FieldFrame(Composite parent, int style, int nameWidth) {
		super(parent, style);
		
		this.display = parent.getDisplay();
		this.nameWidth = nameWidth;
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		layout.numColumns = 2;
		layout.verticalSpacing = 3;
		layout.horizontalSpacing = 2;
		super.setLayout(layout);
		this.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
	}
	
	public NStyledText getTextField(String field, String label)
	{
		GridData labelGridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		labelGridData.widthHint = nameWidth;
		labelGridData.heightHint = 18;
		
		lblLabel = new CLabel(this, SWT.NONE);
		lblLabel.setText(label + " :");
		lblLabel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		lblLabel.setAlignment(SWT.RIGHT);
		lblLabel.setForeground(new Color(display, 49, 106, 197));
		lblLabel.setLayoutData(labelGridData);
		
		GridData textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		textGridData.heightHint = 15;
		
		NStyledText txtField = new NStyledText(this, field, SWT.BORDER | SWT.SINGLE);
		txtField.setLayoutData(textGridData);
		txtField.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		return txtField;
	}
	
	public void setLabelBackground(Color color)
	{
		lblLabel.setBackground(color);
	}
	
	public void setLabelForeground(Color color)
	{
		lblLabel.setForeground(color);
	}
	
	public Composite getPanel(String label)
	{
		GridData labelGridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		labelGridData.widthHint = nameWidth;
		labelGridData.heightHint = 20;
		
		lblLabel = new CLabel(this, SWT.NONE);
		lblLabel.setText(label + " :");
		lblLabel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		lblLabel.setAlignment(SWT.RIGHT);
		lblLabel.setForeground(new Color(display, 49, 106, 197));
		lblLabel.setLayoutData(labelGridData);
		
		GridData panerGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		panerGridData.heightHint = 20;
		
		Composite panel = new Composite(this, SWT.NONE);
		panel.setLayoutData(panerGridData);
		panel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		return panel;
	}
	
	public NStyledText getTextField(String field, String label, int style)
	{
		GridData labelGridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		labelGridData.widthHint = nameWidth;
		labelGridData.heightHint = 18;
		
		lblLabel = new CLabel(this, SWT.NONE);
		lblLabel.setText(label + " :");
		lblLabel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		lblLabel.setAlignment(SWT.RIGHT);
		lblLabel.setForeground(new Color(display, 49, 106, 197));
		lblLabel.setLayoutData(labelGridData);
		
		GridData textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		textGridData.heightHint = 15;
		
		NStyledText txtField = new NStyledText(this, field, style | SWT.SINGLE);
		txtField.setLayoutData(textGridData);
		txtField.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		return txtField;
	}
	
	public NCombo getComboField(String field, String label)
	{
		GridData labelGridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		labelGridData.widthHint = nameWidth;
		labelGridData.heightHint = 18;
		
		lblLabel = new CLabel(this, SWT.NONE);
		lblLabel.setText(label + " :");
		lblLabel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		lblLabel.setAlignment(SWT.RIGHT);
		lblLabel.setForeground(new Color(display, 49, 106, 197));
		lblLabel.setLayoutData(labelGridData);
		
		GridData textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		textGridData.heightHint = 18;
		
		NCombo cmbField = new NCombo(this, field, SWT.BORDER | SWT.READ_ONLY);
		cmbField.setLayoutData(textGridData);
		cmbField.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		return cmbField;
	}
	
	public NCheckBox getCheckField(String field, String label, String truetext, String falsetext)
	{
		GridData labelGridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		labelGridData.widthHint = nameWidth;
		labelGridData.heightHint = 18;
		
		lblLabel = new CLabel(this, SWT.NONE);
		lblLabel.setText(label + " :");
		lblLabel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		lblLabel.setAlignment(SWT.RIGHT);
		lblLabel.setForeground(new Color(display, 49, 106, 197));
		lblLabel.setLayoutData(labelGridData);
		
		GridData textGridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		textGridData.heightHint = 18;
		
		NCheckBox cbField = new NCheckBox(this, field, truetext, falsetext, SWT.NONE | SWT.CHECK);
		cbField.setLayoutData(textGridData);
		cbField.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		return cbField;
	}

}
