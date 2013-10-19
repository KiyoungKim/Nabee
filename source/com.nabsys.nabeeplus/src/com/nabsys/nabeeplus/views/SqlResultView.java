package com.nabsys.nabeeplus.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.nabsys.common.util.DateUtil;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.net.protocol.NBFields;

public class SqlResultView extends NabeeView {

	public final static String ID = "com.nabsys.nabeeplus.views.sqlResultView";
	private Table 			table 			= null;
	private Display 		display 		= null;
	private Label			label			= null;
	private DateUtil 		du 				= null;
	
	public SqlResultView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		setPartName(NBLabel.get(0x023D));
		
		this.display 	= parent.getDisplay();
		this.du			= new DateUtil(NBLabel.getLocale());
		
		Composite back = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		back.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.heightHint = 12;
		
		label = new Label(back, SWT.NONE);
		label.setLayoutData(gridData);
		
		table = new Table(back, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(new Font(display, "Arial", 9, SWT.NONE));
	}
	
	public void setUpdateResult(String result, String url)
	{
		this.label.setText(url + " (" + du.getCurrentDate("yyyy. MM. dd. HH:mm:ss") + ")");
		table.removeAll();
		
		int cnt = table.getColumnCount();
		for(int i=0; i<cnt; i++)
			table.getColumn(0).dispose();
		
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("");
		column.setWidth(0);
		column.setAlignment(SWT.CENTER);
		table.getColumn(0).pack();
		
		column = new TableColumn(table, SWT.NONE);
		column.setText("Result");
		column.setWidth(500);
		column.setAlignment(SWT.CENTER);
		
		String[] resultArray = new String[2];
		resultArray[0] = "";
		resultArray[1] = result;
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(resultArray);

		table.getColumn(0).dispose();
	}
	
	public void setResult(ArrayList<NBFields> list, String url)
	{
		this.label.setText(url + " (" + du.getCurrentDate("yyyy. MM. dd. HH:mm:ss") + ")");
		table.removeAll();
		
		int cnt = table.getColumnCount();
		for(int i=0; i<cnt; i++)
			table.getColumn(0).dispose();
		
		if(list.size() > 0)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText("");
			column.setWidth(0);
			table.getColumn(0).pack();
			
			NBFields colInfo = list.get(0);
			Set<String> keySet = colInfo.keySet();
			Iterator<String> itr = keySet.iterator();
			int colSize = 0;
			while(itr.hasNext())
			{
				String colName = itr.next();
				column = new TableColumn(table, SWT.NONE);
				column.setText(colName);
				column.setWidth(100);
				column.setAlignment(SWT.LEFT);
				colSize++;
			}

			for(int i=0; i<list.size(); i++)
			{
				NBFields itemField = list.get(i);
				String[] itemData = new String[colSize + 1];
				itemData[0] = "";
				for(int j=0; j<colSize; j++)
				{
					String value = "";
					if(itemField.get(table.getColumn(j+1).getText()) instanceof byte[])
					{
						value = "Binary data";
					}
					else
					{
						value = (String)itemField.get(table.getColumn(j+1).getText());
					}
					itemData[j+1] = value.equals("null")?"":value;
				}
				
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(itemData);
			}
			table.getColumn(0).dispose();
		}
	}

	@Override
	public void setFocus() {
	}

}
