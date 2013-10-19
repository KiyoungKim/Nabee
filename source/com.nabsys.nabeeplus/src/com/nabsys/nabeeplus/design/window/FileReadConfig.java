package com.nabsys.nabeeplus.design.window;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
import com.nabsys.nabeeplus.design.model.FileMappingModel;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.listener.NBTableModifiedListener;
import com.nabsys.nabeeplus.views.model.NBEditingSupport;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.TableModel;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NCombo;
import com.nabsys.nabeeplus.widgets.NStyledText;

public class FileReadConfig  extends ConfigPopupWindow{
	private TableViewer 			fieldsTable = null;
	private NBEditingSupport 		idEditingSupport 	= null;
	private NBEditingSupport		sizeEditingSupport = null;
	private NBEditingSupport		paddingEditingSupport	= null;
	private NBEditingSupport 		typeEditingSupport 	= null;
	private int	SIZE = 0;
	private int TYPE = 1;
	private int PADDING = 2;
	public FileReadConfig(Shell parent) {
		super(parent);
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "File Read", new Point(550, 600));
		
		ToolItem btnDelete = board.addButton("Delete mapping", Activator.getImageDescriptor("/icons/trash.gif").createImage(shell.getDisplay()));
		btnDelete.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)fieldsTable.getSelection();
				
				if(selection.isEmpty()) return;
				
				if(selection.getFirstElement() instanceof FileMappingModel)
				{
					FileMappingModel mappingModel = ((FileMappingModel)selection.getFirstElement());
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
		
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 70;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 150);
		fieldFrame.setLayoutData(layoutData);
		
		NCombo cmbReadType = fieldFrame.getComboField("RT", NBLabel.get(0x028F));
		cmbReadType.add(NBLabel.get(0x0290));
		cmbReadType.add(NBLabel.get(0x0291));
		cmbReadType.add(NBLabel.get(0x0292));
		final NStyledText txtCountFieldID = fieldFrame.getTextField("CFI", NBLabel.get(0x0293));
		txtCountFieldID.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put("CFI", value);
			}
		});
		cmbReadType.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				if(value.equals(NBLabel.get(0x0290)))
				{
					txtCountFieldID.setDisableEditing(true, display);
					returnMap.put("RT", 0);
				}
				else if(value.equals(NBLabel.get(0x0291)))
				{
					txtCountFieldID.setDisableEditing(false, display);
					returnMap.put("RT", 1);
				}
				else if(value.equals(NBLabel.get(0x0292)))
				{
					txtCountFieldID.setDisableEditing(true, display);
					returnMap.put("RT", 2);
				}
			}
		});
		
		if(returnMap.get("CFI") == null)
		{
			txtCountFieldID.setText("");
			returnMap.put("CFI", "");
		}
		else
		{
			txtCountFieldID.setText((String)returnMap.get("CFI"));
		}
		
		int type = (Integer)returnMap.get("RT");
		switch(type)
		{
		case 0 :
			txtCountFieldID.setDisableEditing(true, display);
			break;
		case 1 :
			txtCountFieldID.setDisableEditing(false, display);
			break;
		case 2 :
			txtCountFieldID.setDisableEditing(true, display);
			break;
		}
		cmbReadType.setText(cmbReadType.getItem(type));
		
		
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
		column.getColumn().setText(NBLabel.get(0x0137));
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(sizeEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 2, true));
		
		column = new TableViewerColumn(fieldsTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x013A));
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(paddingEditingSupport = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()), 3, 1));
		
		
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
					FileMappingModel root = (FileMappingModel)fieldsTable.getInput();
					new FileMappingModel(root, "");
					fieldsTable.refresh();
				}
			}
		});
		
		typeEditingSupport.addNBTableModifiedListener(tml);
		sizeEditingSupport.addNBTableModifiedListener(tml);
		paddingEditingSupport.addNBTableModifiedListener(tml);
		
		FileMappingModel root = null;
		fieldsTable.setInput(root = new FileMappingModel());
		
		if(returnMap.containsKey("FM") && returnMap.get("FM") != null 
				&& ((LinkedHashMap<String, Object[]>)returnMap.get("FM")).size() > 0)
		{
			LinkedHashMap<String, Object[]> mapping = (LinkedHashMap<String, Object[]>) returnMap.get("FM");
			Iterator<String> itr = mapping.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				FileMappingModel mm = new FileMappingModel(root, key);
				
				Object[] value = mapping.get(key);
				
				if(value[TYPE] == String.class){mm.setType(FieldTypeArray.CHAR);}
				else if(value[TYPE] == Integer.class){mm.setType(FieldTypeArray.INT);}
				else if(value[TYPE] == Long.class){mm.setType(FieldTypeArray.LONG);}
				else if(value[TYPE] == Float.class){mm.setType(FieldTypeArray.FLOAT);}
				else if(value[TYPE] == Double.class){mm.setType(FieldTypeArray.DOUBLE);}
				else if(value[TYPE] == Byte.class){mm.setType(FieldTypeArray.BYTE);}
				else if(value[TYPE] == byte[].class){mm.setType(FieldTypeArray.BYTEARR);}
				mm.setSize((Integer)value[SIZE]);
				mm.setPadding((String)value[PADDING]);
			}
		}
		
		new FileMappingModel(root, "");
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
		FileMappingModel model = (FileMappingModel)fieldsTable.getInput();
		LinkedHashMap<String, Object[]> tmp = new LinkedHashMap<String, Object[]>();
		for(int i=0; i<model.getChildren().size(); i++)
		{
			FileMappingModel child = (FileMappingModel)model.getChildren().get(i);
			
			if(child.getID().equals("")) continue;

			try{
				String padding = child.getPadding().equals("")?" ":child.getPadding();
				switch(FieldTypeArray.getType(child.getType())){
				case FieldTypeArray.CHAR:
					tmp.put(child.getID(), new Object[]{child.getSize(), String.class, padding});
					break;
				case FieldTypeArray.INT:
					tmp.put(child.getID(), new Object[]{child.getSize(), Integer.class, padding});
					break;
				case FieldTypeArray.DOUBLE:
					tmp.put(child.getID(), new Object[]{child.getSize(), Double.class, padding});
					break;
				case FieldTypeArray.FLOAT:
					tmp.put(child.getID(), new Object[]{child.getSize(), Float.class, padding});
					break;
				case FieldTypeArray.LONG:
					tmp.put(child.getID(), new Object[]{child.getSize(), Long.class, padding});
					break;
				case FieldTypeArray.BYTE:
					tmp.put(child.getID(), new Object[]{child.getSize(), Byte.class, padding});
					break;
				case FieldTypeArray.BYTEARR:
					tmp.put(child.getID(), new Object[]{child.getSize(), byte[].class, child.getPadding().toCharArray()[0]});
					break;
				}
			}catch(Exception e){
				IMessageBox.Error(shell, "Field [" + child.getID() + "] " +  e.getMessage());
				return;
			}
		}
		
		returnMap.put("FM", tmp);
		
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}
}
