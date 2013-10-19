package com.nabsys.nabeeplus.design.window;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.design.model.ProcedureParamModel;
import com.nabsys.nabeeplus.design.model.SqlTypeArray;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.listener.NBTableModifiedListener;
import com.nabsys.nabeeplus.views.model.NBEditingSupport;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.views.model.TableModel;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NCheckBox;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.resource.service.ProcedureArgumentContext;

public class DatabaseProcedureConfig extends ConfigPopupWindow{

	private TableViewer paramTable = null;
	private NBEditingSupport directionEditing = null;
	private NBEditingSupport typeEditing = null;
	private NBEditingSupport isMappingEditing = null;
	private NBEditingSupport valueEditing = null;
	
	public DatabaseProcedureConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Procedure Call", new Point(700, 400));

		ToolItem btnDelete = board.addButton("Delete Parameter", Activator.getImageDescriptor("/icons/trash.gif").createImage(shell.getDisplay()));
		
		
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
		layoutData.heightHint = 50;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 120);
		fieldFrame.setLayoutData(layoutData);
		
		final NStyledText txtProcedureName = fieldFrame.getTextField("PN", NBLabel.get(0x0297));
		if(returnMap.get("PN") != null && !((String)returnMap.get("PN")).equals(""))
		{
			txtProcedureName.setText((String)returnMap.get("PN"));
		}
		
		txtProcedureName.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		
		NCheckBox chkLogging = fieldFrame.getCheckField("IL", NBLabel.get(0x029B), NBLabel.get(0x0266), NBLabel.get(0x0267));
		chkLogging.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value.equals("true"));
			}
		});
		
		if(returnMap.get("IL") != null)
		{
			chkLogging.setSelection(((Boolean)returnMap.get("IL")));
		}
		
		StyledText label = new StyledText(contentsBack, SWT.MULTI|SWT.WRAP|SWT.READ_ONLY);
		label.setText(NBLabel.get(0x029C));
		label.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		label.setForeground(new Color(display, 49, 106, 197));
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.widthHint = 350;
		layoutData.heightHint = 30;
		label.setLayoutData(layoutData);
		
		paramTable = new TableViewer(contentsBack, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		paramTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		//Index
		TableViewerColumn column = new TableViewerColumn(paramTable, SWT.NONE);
		column.getColumn().setText("          "+NBLabel.get(0x0133));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(false);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.RIGHT);
		
		//In or Out
		column = new TableViewerColumn(paramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0298));
		column.getColumn().setWidth(70);
		column.getColumn().setResizable(false);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.CENTER);
		column.setEditingSupport(directionEditing = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), new String[]{"IN", "OUT"}, SWT.READ_ONLY),1));
		
		//Sql Type
		column = new TableViewerColumn(paramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0299));
		column.getColumn().setWidth(120);
		column.getColumn().setResizable(false);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(typeEditing = new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), SqlTypeArray.TYPE, SWT.READ_ONLY),2));
		
		
		//Is Mapping Value
		column = new TableViewerColumn(paramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x029A));
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(false);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(isMappingEditing = new NBEditingSupport(column.getViewer(), 
				new CheckboxCellEditor((Composite)column.getViewer().getControl()), 3));
		
		//value
		column = new TableViewerColumn(paramTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x027E));
		column.getColumn().setWidth(220);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(valueEditing = new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()),4));
		
		paramTable.getTable().setHeaderVisible(true);
		paramTable.getTable().setLinesVisible(true);
		
		paramTable.setContentProvider(new NBTableContentProvider());
		paramTable.setLabelProvider(new NBTableLabelProvider(shell.getDisplay()));
		
		ProcedureParamModel root = new ProcedureParamModel();
		
		if(returnMap.containsKey("PRM"))
		{
			ProcedureArgumentContext procedureArgumentContext = (ProcedureArgumentContext)returnMap.get("PRM");
			if(procedureArgumentContext != null)
			{
				for(int i=0; i<procedureArgumentContext.size(); i++)
				{
					ProcedureParamModel model = new ProcedureParamModel(root, "", shell);
					model.setDirection(procedureArgumentContext.isInput(i)?ProcedureParamModel.IN:ProcedureParamModel.OUT);
					if(procedureArgumentContext.isMapValue(i)) model.setMapping();
					model.setType(procedureArgumentContext.getSqlType(i));
					model.setValue(procedureArgumentContext.getArgument(i));
					model.getIndex();
				}
			}
		}
		
		paramTable.setInput(root);
		paramTable.refresh();
		
		paramTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		paramTable.getTable().getColumn(0).pack();
		
		btnDelete.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)paramTable.getSelection();
				
				if(selection.isEmpty()) return;
				
				if(selection.getFirstElement() instanceof ProcedureParamModel)
				{
					ProcedureParamModel model = ((ProcedureParamModel)selection.getFirstElement());
					if(model.getParent().getChildren().get(model.getParent().getChildren().size() - 1) == model
							&& model.getValue().equals("")) return;
					model.remove();
					paramTable.refresh();
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		DragSourceAdapter dragSourceListener = new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) 
		    {
				IStructuredSelection selection = (IStructuredSelection)paramTable.getSelection();
				if(selection.isEmpty()) return;
				
				if(selection.getFirstElement() instanceof ProcedureParamModel)
				{
					ProcedureParamModel model = (ProcedureParamModel)selection.getFirstElement();
					if(model._getTxtValue(0).equals("")) return;
					event.data = model.getIndex() +"";
				}
		    }
		};
		
		DropTargetAdapter dropTargetListener = new DropTargetAdapter()
		{
			public void dragEnter(DropTargetEvent event)
	    	{
	    		if (event.detail == DND.DROP_DEFAULT) 
	    		{
	    			event.detail = (event.operations & DND.DROP_COPY) != 0 ? DND.DROP_COPY : DND.DROP_NONE;
	    		}

	    		for (int i = 0; i < event.dataTypes.length; i++)
	    		{
	    			if (TextTransfer.getInstance().isSupportedType(event.dataTypes[i]))
	    			{
	    				event.currentDataType = event.dataTypes[i];
	    			}
	    		}
	    	}
			
			public void dragOver(DropTargetEvent event)
	    	{
	    		event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
	    	}
			
			public void drop(DropTargetEvent event)
	    	{
				if(((TableItem)event.item).getText().equals("")) return;
	    		if (TextTransfer.getInstance().isSupportedType(event.currentDataType))
	    		{
	    			// Get the dropped data
	    			int fromIndex = Integer.parseInt((String)event.data) - 1;
	    			int toIndex = 0;
	    			
	    			try{
	    				toIndex = Integer.parseInt(((TableItem)event.item).getText()) -1;
	    			}catch(NullPointerException e){ return; }
	    			
	    			if(fromIndex == toIndex) return;
	    			
	    			ProcedureParamModel root = (ProcedureParamModel)paramTable.getInput();
	    			
	    			if(toIndex > root.getChildren().size() - 1) return;
	    			
	    			ProcedureParamModel tmpProcedureParamModel = null;
	    			ProcedureParamModel fromProcedureParamModel = (ProcedureParamModel)root.getChildren().get(fromIndex);
	    			if(fromIndex > toIndex)
	    			{
	    				for(int i=toIndex; i<=fromIndex; i++)
	    				{
	    					tmpProcedureParamModel = (ProcedureParamModel)root.getChildren().get(i);
	    					root.getChildren().set(i, fromProcedureParamModel);
	    					fromProcedureParamModel = tmpProcedureParamModel;
	    				}
	    			}
	    			else if(fromIndex < toIndex)
	    			{
	    				for(int i=toIndex; i>=fromIndex; i--)
	    				{
	    					tmpProcedureParamModel = (ProcedureParamModel)root.getChildren().get(i);
	    					root.getChildren().set(i, fromProcedureParamModel);
	    					fromProcedureParamModel = tmpProcedureParamModel;
	    				}
	    			}
	    			
	    			paramTable.refresh();
	    		}
	    	}
		};
		
		NBTableModifiedListener tml = new NBTableModifiedListener(){
			public void modified(TableModel model, String id, String fieldName, int fieldIndex) 
			{
				if(fieldName.equals("DR") && ((ProcedureParamModel)model).getDirection() == ProcedureParamModel.OUT)
				{
					((ProcedureParamModel)model).setMapping();
				}
				
				if(model.getParent().getChildren().get(model.getParent().getChildren().size() - 1) == model)
				{
					((ProcedureParamModel)model).getIndex();
					ProcedureParamModel root = (ProcedureParamModel)paramTable.getInput();
					new ProcedureParamModel(root, "", shell);
				}
				
				paramTable.refresh();
			}
		};
		
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		
		paramTable.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, types, dragSourceListener);
		paramTable.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT, types, dropTargetListener);
		directionEditing.addNBTableModifiedListener(tml);
		typeEditing.addNBTableModifiedListener(tml);
		isMappingEditing.addNBTableModifiedListener(tml);
		valueEditing.addNBTableModifiedListener(tml);

		new ProcedureParamModel(root, "", shell);
		paramTable.refresh();
		
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
		ProcedureParamModel root = (ProcedureParamModel)paramTable.getInput();
		ArrayList<TableModel> children = root.getChildren();
		
		boolean[] isInput = new boolean[children.size() - 1];
		boolean[] isMapValue = new boolean[children.size() - 1];
		int[] sqlTypes = new int[children.size() - 1];
		Object[] parameter = new Object[children.size() -1];
		for(int i=0; i<children.size(); i++)
		{
			ProcedureParamModel model = (ProcedureParamModel)children.get(i);
			if(model._getTxtValue(0).equals("")) break;
			
			isInput[i] = model.getDirection() == ProcedureParamModel.IN;
			isMapValue[i] = model.isMapping();
			sqlTypes[i] = model.getType();
			parameter[i] = model.getConvertValue();
			if(parameter[i] == null)
			{
				IMessageBox.Warning(shell, NBLabel.get(0x029D));
				return;
			}
			
			if(isMapValue[i] == false)
			{
				if(sqlTypes[i] != Types.BIGINT 
					&& sqlTypes[i] != Types.BOOLEAN 
					&& sqlTypes[i] != Types.CHAR 
					&& sqlTypes[i] != Types.DATE 
					&& sqlTypes[i] != Types.DECIMAL
					&& sqlTypes[i] != Types.DOUBLE
					&& sqlTypes[i] != Types.FLOAT
					&& sqlTypes[i] != Types.INTEGER
					&& sqlTypes[i] != Types.LONGVARCHAR
					&& sqlTypes[i] != Types.NUMERIC
					&& sqlTypes[i] != Types.SMALLINT
					&& sqlTypes[i] != Types.TIME
					&& sqlTypes[i] != Types.TIMESTAMP
					&& sqlTypes[i] != Types.TINYINT
					&& sqlTypes[i] != Types.VARCHAR)
				{
					IMessageBox.Warning(shell, NBLabel.get(0x029E));
					return;
				}
			}
		}
		
		returnMap.put("PRM", new ProcedureArgumentContext(parameter, isInput, isMapValue, sqlTypes));
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}

}
