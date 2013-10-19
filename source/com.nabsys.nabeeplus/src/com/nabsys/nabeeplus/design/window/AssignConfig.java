package com.nabsys.nabeeplus.design.window;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
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
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.design.model.FieldTypeArray;
import com.nabsys.nabeeplus.design.model.MappingModel;
import com.nabsys.nabeeplus.listener.NBTableModifiedListener;
import com.nabsys.nabeeplus.views.model.NBEditingSupport;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.TableModel;

public class AssignConfig  extends ConfigPopupWindow{
	private TableViewer 			fieldsTable = null;
	private NBEditingSupport 		idEditingSupport 	= null;
	private NBEditingSupport		valueEditingSupport	= null;
	private NBEditingSupport 		typeEditingSupport 	= null;
	public AssignConfig(Shell parent) {
		super(parent);
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Assign", new Point(720, 350));
		
		
		ToolItem btnDelete = board.addButton("Delete Assign", Activator.getImageDescriptor("/icons/trash.gif").createImage(shell.getDisplay()));
		btnDelete.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)fieldsTable.getSelection();
				
				if(selection.isEmpty()) return;
				
				if(selection.getFirstElement() instanceof MappingModel)
				{
					MappingModel mappingModel = ((MappingModel)selection.getFirstElement());
					if(mappingModel.getID().equals("")) return;
					mappingModel.remove();
					fieldsTable.refresh();
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
		
		fieldsTable = new TableViewer(contentsBack, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		fieldsTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		TableViewerColumn column = new TableViewerColumn(fieldsTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0273) + "                                      ");
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(idEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 0));
		
		column = new TableViewerColumn(fieldsTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0136));
		column.getColumn().setWidth(90);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.CENTER);
		column.setEditingSupport(typeEditingSupport = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), 
						FieldTypeArray.TYPE, SWT.READ_ONLY),1));
		
		
		column = new TableViewerColumn(fieldsTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0274));
		column.getColumn().setWidth(350);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(valueEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 2));
		
		
		fieldsTable.getTable().setHeaderVisible(true);
		fieldsTable.getTable().setLinesVisible(true);
		
		fieldsTable.setContentProvider(new NBTableContentProvider());
		fieldsTable.setLabelProvider(new NBTableLabelProvider(shell.getDisplay()));
		
		fieldsTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		fieldsTable.getTable().getColumn(0).pack();
		
		
		NBTableModifiedListener tml = null;
		idEditingSupport.addNBTableModifiedListener(tml = new NBTableModifiedListener(){
			public void modified(TableModel model, String id, String fieldName, int fieldIndex) {
				if(model.getParent().getChildren().get(model.getParent().getChildren().size() - 1) == model && id.equals("") && !model.getID().equals(""))
				{
					MappingModel root = (MappingModel)fieldsTable.getInput();
					new MappingModel(root, "");
					fieldsTable.refresh();
				}
				
			}
		});
		
		typeEditingSupport.addNBTableModifiedListener(tml);
		valueEditingSupport.addNBTableModifiedListener(tml);
		
		MappingModel root = null;
		fieldsTable.setInput(root = new MappingModel());
		
		if(returnMap.containsKey("MD") && returnMap.get("MD") != null 
				&& ((HashMap<String, Object>)returnMap.get("MD")).size() > 0)
		{
			HashMap<String, Object> mapping = (HashMap<String, Object>) returnMap.get("MD");
			Iterator<String> itr = mapping.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				MappingModel mm = new MappingModel(root, key);
				
				Object value = mapping.get(key);
				if(value instanceof String)
				{
					mm.setMappingData((String)mapping.get(key));
				}
				else if(value instanceof Integer)
				{
					mm.setMappingData(String.valueOf(mapping.get(key)));
					mm.setType(FieldTypeArray.INT);
				}
				else if(value instanceof Float)
				{
					mm.setMappingData(String.valueOf(mapping.get(key)));
					mm.setType(FieldTypeArray.FLOAT);
				}
				else if(value instanceof Long)
				{
					mm.setMappingData(String.valueOf(mapping.get(key)));
					mm.setType(FieldTypeArray.LONG);
				}
				else if(value instanceof Double)
				{
					mm.setMappingData(String.valueOf(mapping.get(key)));
					mm.setType(FieldTypeArray.DOUBLE);
				}
				else if(value instanceof Byte)
				{
					mm.setMappingData((String)mapping.get(key));
					mm.setType(FieldTypeArray.BYTE);
				}
				else if(value instanceof byte[])
				{
					mm.setMappingData((String)mapping.get(key));
					mm.setType(FieldTypeArray.BYTEARR);
				}
			}
		}
		
		new MappingModel(root, "");
		fieldsTable.refresh();
		
		
		contentsBack.layout(true);
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return returnMap;
	}

	@Override
	protected void confirm() {
		MappingModel model = (MappingModel)fieldsTable.getInput();
		HashMap<String, Object> tmp = new HashMap<String, Object>();
		for(int i=0; i<model.getChildren().size(); i++)
		{
			MappingModel child = (MappingModel)model.getChildren().get(i);
			
			if(child.getID().equals("")) continue;

			try{
				switch(FieldTypeArray.getType(child.getType())){
				case FieldTypeArray.CHAR:
					tmp.put(child.getID(), child.getMappingData());
					break;
				case FieldTypeArray.INT:
					tmp.put(child.getID(), Integer.parseInt(child.getMappingData()));
					break;
				case FieldTypeArray.DOUBLE:
					tmp.put(child.getID(), Double.parseDouble(child.getMappingData()));
					break;
				case FieldTypeArray.FLOAT:
					tmp.put(child.getID(), Float.parseFloat(child.getMappingData()));
					break;
				case FieldTypeArray.LONG:
					tmp.put(child.getID(), Long.parseLong(child.getMappingData()));
					break;
				case FieldTypeArray.BYTE:
					if(!((String)child.getMappingData()).equals("Return Value"))
					{
						IMessageBox.Error(shell, NBLabel.get(0x0278));
						return;
					}
					tmp.put(child.getID(), child.getMappingData());
					break;
				case FieldTypeArray.BYTEARR:
					if(!((String)child.getMappingData()).equals("Return Value"))
					{
						IMessageBox.Error(shell, NBLabel.get(0x0279));
						return;
					}
					tmp.put(child.getID(), child.getMappingData());
					break;
				}
			}catch(Exception e){
				IMessageBox.Error(shell, "Field [" + child.getID() + "] " +  e.getMessage());
				return;
			}
		}
		
		returnMap.put("MD", tmp);
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}
}
