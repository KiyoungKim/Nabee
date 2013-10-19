package com.nabsys.nabeeplus.design.window;

import java.util.HashMap;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.design.model.BatchModel;
import com.nabsys.nabeeplus.listener.NBTableModifiedListener;
import com.nabsys.nabeeplus.views.model.NBEditingSupport;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.TableModel;
import com.nabsys.resource.BatchTimeList;
import com.nabsys.resource.service.TimeSector;

public class BatchConfig  extends ConfigPopupWindow{
	private TableViewer 			batchListTable				= null;
	private NBEditingSupport		minEditingSupport			= null;
	private NBEditingSupport		hurEditingSupport			= null;
	private NBEditingSupport		dayEditingSupport			= null;
	private NBEditingSupport		monEditingSupport			= null;
	private NBEditingSupport		yerEditingSupport			= null;
	private BatchTimeList 			timeList 					= null;
	public BatchConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Batch", new Point(400, 350));
		
		ToolItem btnDelete = board.addButton("Delete schedule", Activator.getImageDescriptor("/icons/trash.gif").createImage(shell.getDisplay()));
		btnDelete.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)batchListTable.getSelection();
				if(selection.isEmpty()) return;
				if(selection.getFirstElement() instanceof BatchModel)
				{
					BatchModel batchModel = ((BatchModel)selection.getFirstElement());
					for(int i=0; i<5; i++)
					{
						int value = (Integer)batchModel.getCurValue(i);
						if(value < 1) return;
					}
					timeList.remove(Integer.parseInt(batchModel.getID()));
					batchModel.remove();
					batchListTable.refresh();
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		Composite contentsBack = getContentsBack();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		contentsBack.setLayout(layout);
		
		
		batchListTable = new TableViewer(contentsBack, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		batchListTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		TableViewerColumn column = new TableViewerColumn(batchListTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0171) + "             ");
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(minEditingSupport = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
						new String[]{"*", "00", "01", "02", "03", "04", "05", "06", "07", "08"
						, "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
						, "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32"
						, "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44"
						, "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56"
						, "57", "58", "59"},
						SWT.READ_ONLY), 0));
		
		
		column = new TableViewerColumn(batchListTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0172));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(hurEditingSupport = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
						new String[]{"*", "00", "01", "02", "03", "04", "05", "06", "07", "08"
						, "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
						, "21", "22", "23"},
						SWT.READ_ONLY), 1));
		
		
		column = new TableViewerColumn(batchListTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0173));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(dayEditingSupport = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
						new String[]{"*", "01", "02", "03", "04", "05", "06", "07", "08"
						, "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
						, "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"},
						SWT.READ_ONLY), 2));
		
		column = new TableViewerColumn(batchListTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0174));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(monEditingSupport = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
						new String[]{"*", "01", "02", "03", "04", "05", "06", "07", "08"
						, "09", "10", "11", "12"},
						SWT.READ_ONLY), 3));
		
		column = new TableViewerColumn(batchListTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0175));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.setEditingSupport(yerEditingSupport = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
						new String[]{"*", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019"
						, "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029"},
						SWT.READ_ONLY), 4));

		
		batchListTable.getTable().setHeaderVisible(true);
		batchListTable.getTable().setLinesVisible(true);
		
		batchListTable.getTable().setFont(new Font(display, "Arial", 10, SWT.NONE));
		batchListTable.getTable().getColumn(0).pack();
		
		
		batchListTable.setContentProvider(new NBTableContentProvider());
		batchListTable.setLabelProvider(new NBTableLabelProvider(display));
		
		BatchModel root = new BatchModel();
		batchListTable.setInput(root);
		
		minEditingSupport.addNBTableModifiedListener(new NBTableModifiedListener(){
			public void modified(TableModel model, String id, String fieldName,
					int fieldIndex) {
				setTimeValue((BatchModel)model, Integer.parseInt(id));
			}
		});
		hurEditingSupport.addNBTableModifiedListener(new NBTableModifiedListener(){
			public void modified(TableModel model, String id, String fieldName,
					int fieldIndex) {
				setTimeValue((BatchModel)model, Integer.parseInt(id));
			}
		});
		dayEditingSupport.addNBTableModifiedListener(new NBTableModifiedListener(){
			public void modified(TableModel model, String id, String fieldName,
					int fieldIndex) {
				setTimeValue((BatchModel)model, Integer.parseInt(id));
			}
		});
		monEditingSupport.addNBTableModifiedListener(new NBTableModifiedListener(){
			public void modified(TableModel model, String id, String fieldName,
					int fieldIndex) {
				setTimeValue((BatchModel)model, Integer.parseInt(id));
			}
		});
		yerEditingSupport.addNBTableModifiedListener(new NBTableModifiedListener(){
			public void modified(TableModel model, String id, String fieldName,
					int fieldIndex) {
				setTimeValue((BatchModel)model, Integer.parseInt(id));
			}
		});
		
		timeList = (BatchTimeList)returnMap.get("TL");
		if(timeList == null || timeList.size() == 0)
		{
			timeList = new BatchTimeList();
			new BatchModel(root, "0"); 
		}
		else
		{
			for(int i=0; i<timeList.size(); i++)
			{
				HashMap<TimeSector, Integer> time = timeList.get(i);
				BatchModel batchModel = new BatchModel(root, i + "");
				batchModel._setObjectValue(0, time.get(TimeSector.MIN) + 1);
				batchModel._setObjectValue(1, time.get(TimeSector.HUR) + 1);
				batchModel._setObjectValue(2, time.get(TimeSector.DAY)==-1?time.get(TimeSector.DAY) + 1:time.get(TimeSector.DAY));
				batchModel._setObjectValue(3, time.get(TimeSector.MON)==-1?time.get(TimeSector.MON) + 1:time.get(TimeSector.MON));
				batchModel._setObjectValue(4, time.get(TimeSector.YER)==-1?time.get(TimeSector.YER) + 1:time.get(TimeSector.YER) - 2011);
			}
			new BatchModel(root, timeList.size()+"");
		}
		
		batchListTable.refresh();
		
		contentsBack.layout(true);
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return returnMap;
	}
	
	private void setTimeValue(BatchModel model, int index)
	{
		for(int i=0; i<5; i++)
		{
			int value = (Integer)model.getCurValue(i);
			if(value < 1) return;
		}

		if(timeList.size() <= index)
		{
			HashMap<TimeSector, Integer> time = new HashMap<TimeSector, Integer>();
			time.put(TimeSector.MIN, model.getSendValue(0));
			time.put(TimeSector.HUR, model.getSendValue(1));
			time.put(TimeSector.DAY, model.getSendValue(2));
			time.put(TimeSector.MON, model.getSendValue(3));
			time.put(TimeSector.YER, model.getSendValue(4));
			timeList.add(time);
			new BatchModel(model.getParent(), timeList.size() + "");
			batchListTable.refresh();
		}
		else
		{
			HashMap<TimeSector, Integer> time = timeList.get(index);
			time.put(TimeSector.MIN, model.getSendValue(0));
			time.put(TimeSector.HUR, model.getSendValue(1));
			time.put(TimeSector.DAY, model.getSendValue(2));
			time.put(TimeSector.MON, model.getSendValue(3));
			time.put(TimeSector.YER, model.getSendValue(4));
		}
		
		
	}

	@Override
	protected void confirm() {
		returnMap.put("TL", timeList);
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}
}
