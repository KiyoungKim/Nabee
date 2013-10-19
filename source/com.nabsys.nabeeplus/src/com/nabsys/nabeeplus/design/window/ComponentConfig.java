package com.nabsys.nabeeplus.design.window;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.design.model.ComponentArgumentModel;
import com.nabsys.nabeeplus.listener.NBModifiedListener;
import com.nabsys.nabeeplus.views.model.NBEditingSupport;
import com.nabsys.nabeeplus.views.model.NBTableContentProvider;
import com.nabsys.nabeeplus.views.model.NBTableLabelProvider;
import com.nabsys.nabeeplus.widgets.FieldFrame;
import com.nabsys.nabeeplus.widgets.NCheckBox;
import com.nabsys.nabeeplus.widgets.NCombo;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.resource.service.ComponentArgumentContext;

public class ComponentConfig  extends ConfigPopupWindow{
	private SearchPopupList popupList = null;
	private TableViewer constTable = null;
	private TableViewer methodTable = null;
	private HashMap<String, String> componentMap = null;
	private ArrayList<org.apache.bcel.classfile.Method> constructArray = null;
	private ArrayList<org.apache.bcel.classfile.Method> methodArray = null;
	private boolean isInit = true;
	public ComponentConfig(Shell parent) {
		super(parent);
	}
	
	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Component", new Point(770, 500));
		
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
		layoutData.heightHint = 120;
		FieldFrame fieldFrame = new FieldFrame(contentsBack, SWT.NONE, 120);
		fieldFrame.setLayoutData(layoutData);
		
		final String[] listItems = new String[]{"", "Context", "Connector Map"};
		
		final NStyledText txtComponentName = fieldFrame.getTextField("CPN", NBLabel.get(0x0164));
		
		final NStyledText txtClassName = fieldFrame.getTextField("CN", NBLabel.get(0x0166));
		txtClassName.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		
		txtClassName.setEditable(false);
		
		NCheckBox chkAlwaysNew = fieldFrame.getCheckField("IAN", NBLabel.get(0x0284), NBLabel.get(0x0266), NBLabel.get(0x0267));
		chkAlwaysNew.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value.equals("true"));
			}
		});
		
		NCheckBox chkSetReturn = fieldFrame.getCheckField("ISR", NBLabel.get(0x027B), NBLabel.get(0x0266), NBLabel.get(0x0267));
		final NStyledText txtReturnKeyName = fieldFrame.getTextField("RMK", NBLabel.get(0x0279));
		chkSetReturn.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				boolean isSetReturnValue =  value.equals("true");
				returnMap.put(name, isSetReturnValue);
				if(isSetReturnValue)
				{
					txtReturnKeyName.setEditable(true);
					txtReturnKeyName.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					txtReturnKeyName.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				}
				else
				{
					txtReturnKeyName.setText("");
					txtReturnKeyName.setEditable(false);
					txtReturnKeyName.setBackground(new Color(display, 240, 240, 240));
					txtReturnKeyName.setForeground(new Color(display, 150, 150, 150));
				}
			}
		});
		
		txtReturnKeyName.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				returnMap.put(name, value);
			}
		});
		
		if(returnMap.get("RMK") != null && !((String)returnMap.get("RMK")).equals(""))
		{
			txtReturnKeyName.setText((String)returnMap.get("RMK"));
		}
		
		if(returnMap.get("ISR") != null)
		{
			chkSetReturn.setSelection((Boolean)returnMap.get("ISR"));
			if(!(Boolean)returnMap.get("ISR"))
			{
				txtReturnKeyName.setText("");
				txtReturnKeyName.setEditable(false);
				txtReturnKeyName.setBackground(new Color(display, 240, 240, 240));
				txtReturnKeyName.setForeground(new Color(display, 150, 150, 150));
			}
		}
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		
		CTabFolder tabFolder = new CTabFolder(contentsBack, SWT.BORDER);
		tabFolder.setTabHeight(20);
		tabFolder.setLayout(gridLayout);
		tabFolder.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Composite constBack = new Composite(tabFolder, SWT.NONE);
		constBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		constBack.setLayout(gridLayout);
		
		CTabItem constTab = new CTabItem(tabFolder, SWT.NONE);
		constTab.setText(NBLabel.get(0x027C));
		constTab.setControl(constBack);
		
		Composite methodBack = new Composite(tabFolder, SWT.NONE);
		methodBack.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		methodBack.setLayout(gridLayout);
		
		CTabItem methodTab = new CTabItem(tabFolder, SWT.NONE);
		methodTab.setText(NBLabel.get(0x027D));
		methodTab.setControl(methodBack);

		tabFolder.setSelection(constTab);
		
		
		/////////////CONSTRUCTION TAB DESIGN
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 25;
		fieldFrame = new FieldFrame(constBack, SWT.NONE, 120);
		fieldFrame.setLayoutData(layoutData);
		
		final NCombo cmbConstruct = fieldFrame.getComboField("", NBLabel.get(0x0281));
		cmbConstruct.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				ComponentArgumentModel root = new ComponentArgumentModel();
				
				int index = cmbConstruct.getSelectionIndex();
				if(index < 0) return;
				org.apache.bcel.classfile.Method method = constructArray.get(index);
				
				if(!isInit)
				{
					LocalVariable[] lvt = method.getLocalVariableTable().getLocalVariableTable();
					org.apache.bcel.generic.Type[] types = method.getArgumentTypes();
					
					for(int i=0; i<types.length; i++)
					{
						ComponentArgumentModel model = new ComponentArgumentModel(root, i+"", shell);
						model.setArgumentComboList(listItems);
						model.setArgumentName(lvt[i+1].getName());
						model.setType(types[i].getSignature());
					}
					
					constTable.setInput(root);
					constTable.refresh();
					constTable.getTable().getColumn(0).pack();
				}
			}
		});
		
		constTable = new TableViewer(constBack, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		constTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		//Class Type
		TableViewerColumn column = new TableViewerColumn(constTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0169));
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
			
		//Argument Name
		column = new TableViewerColumn(constTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0168));
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		
		//IS Using system value
		column = new TableViewerColumn(constTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x027F));
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(false);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.CENTER);
		column.setEditingSupport(new NBEditingSupport(column.getViewer(), 
				new CheckboxCellEditor((Composite)column.getViewer().getControl()), 2));
		
		//System Argument value
		column = new TableViewerColumn(constTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0283));
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), listItems, SWT.READ_ONLY),3));
		//Argument value
		column = new TableViewerColumn(constTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x027E));
		column.getColumn().setWidth(230);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(new NBEditingSupport(column.getViewer(), 
		new TextCellEditor((Composite)column.getViewer().getControl()),4));
				
		constTable.getTable().setHeaderVisible(true);
		constTable.getTable().setLinesVisible(true);
		
		constTable.setContentProvider(new NBTableContentProvider());
		constTable.setLabelProvider(new NBTableLabelProvider(shell.getDisplay()));
		
		constTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		constTable.getTable().getColumn(0).pack();
		
		/////////////METHOD TAB DESIGN
		//
		fieldFrame = new FieldFrame(methodBack, SWT.NONE, 120);
		fieldFrame.setLayoutData(layoutData);
		final NCombo cmbMethod = fieldFrame.getComboField("", NBLabel.get(0x0282));
		cmbMethod.addNBModifiedListener(new NBModifiedListener(){
			public void modified(String name, String value) {
				ComponentArgumentModel root = new ComponentArgumentModel();
				
				int index = cmbMethod.getSelectionIndex();
				if(index < 0) return;
				org.apache.bcel.classfile.Method method = methodArray.get(index);
				
				if(!isInit)
				{
					returnMap.put("MN", method.getName());
					
					LocalVariable[] lvt = method.getLocalVariableTable().getLocalVariableTable();
					org.apache.bcel.generic.Type[] types = method.getArgumentTypes();
					
					for(int i=0; i<types.length; i++)
					{
						ComponentArgumentModel model = new ComponentArgumentModel(root, i+"", shell);
						//맵핑키 리스트 존재유무에 따라 변경
						model.setArgumentComboList(listItems);
						model.setArgumentName(lvt[i+1].getName());
						model.setType(types[i].getSignature());
					}
				
				
					methodTable.setInput(root);
					methodTable.refresh();
					methodTable.getTable().getColumn(0).pack();
				}
			}
		});
		
		methodTable = new TableViewer(methodBack, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		methodTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		//Class Type
		column = new TableViewerColumn(methodTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0169));
		column.getColumn().setWidth(170);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
			
		//Argument Name
		column = new TableViewerColumn(methodTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0168));
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		
		//IS Using system value
		column = new TableViewerColumn(methodTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x027F));
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(false);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.CENTER);
		column.setEditingSupport(new NBEditingSupport(column.getViewer(), 
				new CheckboxCellEditor((Composite)column.getViewer().getControl()), 2));

		//System Argument value
		column = new TableViewerColumn(methodTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x0283));
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(new NBEditingSupport(column.getViewer(), 
				new ComboBoxCellEditor((Composite)column.getViewer().getControl(), listItems, SWT.READ_ONLY),3));

		//Argument value
		column = new TableViewerColumn(methodTable, SWT.NONE);
		column.getColumn().setText(NBLabel.get(0x027E));
		column.getColumn().setWidth(230);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setAlignment(SWT.LEFT);
		column.setEditingSupport(new NBEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl()),4));
		
		methodTable.getTable().setHeaderVisible(true);
		methodTable.getTable().setLinesVisible(true);
		
		methodTable.setContentProvider(new NBTableContentProvider());
		methodTable.setLabelProvider(new NBTableLabelProvider(shell.getDisplay()));
		
		methodTable.getTable().setFont(new Font(display, "Arial", 9, SWT.NONE));
		methodTable.getTable().getColumn(0).pack();
		////////////////////////////////////
		
		txtComponentName.addModifyListener(new ModifyListener(){
			private boolean isInput = false;
			public void modifyText(ModifyEvent e) {
				if(isInit)
				{
					return;
				}
				if(isInput == true)
				{
					//Init everything except component name
					txtClassName.setText("");
					isInput = false;
					return;
				}
				
				Rectangle rect = new Rectangle(getLocation().x + 135, 
						getLocation().y + 98,
						txtComponentName.getBounds().width,
						200);
				
				if(popupList == null || popupList.isShellDisposed()) 
				{
					popupList = new SearchPopupList(shell, SWT.NONE, txtComponentName);
				}

				setComponentList(txtComponentName.getText());
				
				if(popupList.isShellDisposed()) 
				{
					String returnValue = popupList.open(rect);
					if(returnValue != null)
					{
						isInput = true;
						setComponentInfo(txtComponentName, txtClassName, cmbConstruct, cmbMethod, returnValue, null);
					}
					else
					{
						txtComponentName.setText(returnValue);
					}
				}
			}
		});
		
		MouseListener mouseListener = new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				if(popupList != null && !popupList.isShellDisposed())popupList.close();
			}

			public void mouseUp(MouseEvent e) {
			}
		};
		
		txtComponentName.setText((String)returnMap.get("CPN")==null?"":(String)returnMap.get("CPN"));
		txtClassName.setText((String)returnMap.get("CN")==null?"":(String)returnMap.get("CN"));
		chkAlwaysNew.setSelection((Boolean)returnMap.get("IAN"));
		chkSetReturn.setSelection((Boolean)returnMap.get("ISR"));
		txtReturnKeyName.setText((String)returnMap.get("RMK")==null?"":(String)returnMap.get("RMK"));

		if(!txtComponentName.getText().equals("") && !txtClassName.getText().equals(""))
		{
			setComponentInfo(txtComponentName, txtClassName, cmbConstruct, cmbMethod, txtComponentName.getText(), txtClassName.getText());
		}
		
		if(returnMap.containsKey("CA") && returnMap.get("CA") != null)
		{
			ComponentArgumentModel root = new ComponentArgumentModel();
			ComponentArgumentContext cac = (ComponentArgumentContext)returnMap.get("CA");
			for(int i=0; i<cac.getSize(); i++)
			{
				ComponentArgumentModel model = new ComponentArgumentModel(root, i+"", shell);
				//맵핑키 리스트 존재유무에 따라 변경
				model.setArgumentComboList(listItems);
				model.setArgumentName(cac.getArgumentName()[i]);
				model.setUseSystemValue(cac.isSystemValue()[i]);
				model.setType(cac.getSignature()[i]);
				model.setArgumentValue(cac.getArgument()[i]);
			}
			
			constTable.setInput(root);
			constTable.refresh();
			constTable.getTable().getColumn(0).pack();
		}
		
		if(returnMap.containsKey("MA") && returnMap.get("MA") != null)
		{
			ComponentArgumentModel root = new ComponentArgumentModel();
			ComponentArgumentContext cac = (ComponentArgumentContext)returnMap.get("MA");
			for(int i=0; i<cac.getSize(); i++)
			{
				ComponentArgumentModel model = new ComponentArgumentModel(root, i+"", shell);
				//맵핑키 리스트 존재유무에 따라 변경
				model.setArgumentComboList(listItems);
				model.setArgumentName(cac.getArgumentName()[i]);
				model.setUseSystemValue(cac.isSystemValue()[i]);
				model.setType(cac.getSignature()[i]);
				model.setArgumentValue(cac.getArgument()[i]);
			}
			
			methodTable.setInput(root);
			methodTable.refresh();
			methodTable.getTable().getColumn(0).pack();
		}
		
		txtComponentName.addMouseListener(mouseListener);
		tabFolder.addMouseListener(mouseListener);
		constTable.getTable().addMouseListener(mouseListener);
		contentsBack.layout(true);
		
		isInit = false;
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		
		return returnMap;
	}
	
	private void setComponentInfo(NStyledText txtComponentName, NStyledText txtClassName, NCombo cmbConstruct, NCombo cmbMethod, String componentName, String className)
	{
		if(className == null) className = componentMap.get(componentName);
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ComponentConfig");
		fields.put(IPC.NB_INSTNCE_ID	, instanceID);
		fields.put("CMD_CODE"			, "B");
		fields.put("ID"					, componentName);
		
		String cStr = "";
		String mStr = "";
		if(isInit)
		{
			if(returnMap.containsKey("CA") && returnMap.get("CA") != null)
			{
				ComponentArgumentContext cac = (ComponentArgumentContext)returnMap.get("CA");
				cStr = className.substring(className.lastIndexOf(".") + 1)+" (";
				
				for(int i=0; i<cac.getSize(); i++)
				{
					cStr += getClassFromSignature(cac.getSignature()[i]);
					if(i < cac.getSize() - 1) cStr += " ,";
				}
				
				cStr += ")";
			}	
			if(returnMap.containsKey("MA") && returnMap.get("MA") != null)
			{
				ComponentArgumentContext cac = (ComponentArgumentContext)returnMap.get("MA");
				if(returnMap.containsKey("MN") && returnMap.get("MN") != null)
				{
					String methodName = (String)returnMap.get("MN");
					mStr = methodName+" (";
				}
				
				for(int i=0; i<cac.getSize(); i++)
				{
					mStr += getClassFromSignature(cac.getSignature()[i]);
					if(i < cac.getSize() - 1) mStr += " ,";
				}
				
				mStr += ")";
			}	
		}
		
		try {
			fields = protocol.execute(fields);
					
			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			byte[] binData = (byte[])fields.get("BIN_DATA");
			ClassParser parser = new ClassParser(new ByteArrayInputStream(binData), className);
			JavaClass jClazz = parser.parse();
			org.apache.bcel.classfile.Method[] methods = jClazz.getMethods();

			constructArray = new ArrayList<org.apache.bcel.classfile.Method>();
			methodArray = new ArrayList<org.apache.bcel.classfile.Method>();
			cmbConstruct.removeAll();
			cmbMethod.removeAll();
			for(int i=0; i<methods.length; i++)
			{
				org.apache.bcel.classfile.Method method = methods[i];
				String str = "";
				if(method.getModifiers() != Modifier.PUBLIC) continue;
				
				if(method.getName().equals("<init>"))
				{
					String cln = jClazz.getClassName();
					str = method.getReturnType() + " " +  cln.substring(cln.lastIndexOf(".") + 1)+" (";
					String chkStr = cln.substring(cln.lastIndexOf(".") + 1)+" (";
					org.apache.bcel.generic.Type[] types = method.getArgumentTypes();
					
					for(int j=0; j<types.length; j++)
					{
						String typeStr = types[j].toString();
						str += typeStr.substring(typeStr.lastIndexOf(".") + 1);
						chkStr += typeStr.substring(typeStr.lastIndexOf(".") + 1);
						if(j < types.length - 1)
						{
							str += " ,";
							chkStr += " ,";
						}
					}
					str += ")";
					chkStr += ")";
					
					cmbConstruct.add(str);
					constructArray.add(method);
					
					if(!cStr.equals(""))
					{
						if(chkStr.equals(cStr)) cmbConstruct.setText(str);
					}
				}
				else
				{
					String rType = method.getReturnType().toString();
					str = rType.substring(rType.lastIndexOf(".") + 1) + " " +  method.getName()+" (";
					String chkStr = method.getName()+" (";
					org.apache.bcel.generic.Type[] types = method.getArgumentTypes();
					
					for(int j=0; j<types.length; j++)
					{
						String typeStr = types[j].toString();
						str += typeStr.substring(typeStr.lastIndexOf(".") + 1);
						chkStr += typeStr.substring(typeStr.lastIndexOf(".") + 1);
						if(j < types.length - 1)
						{
							str += " ,";
							chkStr += " ,";
						}
					}
					str += ")";
					chkStr += ")";
					
					cmbMethod.add(str);
					methodArray.add(method);
					if(!mStr.equals(""))
					{
						if(chkStr.equals(mStr)) cmbMethod.setText(str);
					}
				}
			}
			
			if(!isInit)
			{
				if(cmbConstruct.getItemCount() > 0) cmbConstruct.setText(cmbConstruct.getItem(0));
				if(cmbMethod.getItemCount() > 0) cmbMethod.setText(cmbMethod.getItem(0));
				
				txtComponentName.setText(componentName);
				txtClassName.setText(className);
				returnMap.put("CPN", componentName);
				returnMap.put("CN", className);
			}
			
		} catch (Exception e) {
			IMessageBox.Error(shell, NBLabel.get(0x0280));
		}
	}
	
	private String getClassFromSignature(String signature)
	{
		String str = "";
		if(signature.equals("I")){str = "int";}//int
		else if(signature.equals("J")){str = "long";}//long
		else if(signature.equals("D")){str = "double";}//double
		else if(signature.equals("F")){str = "float";}//float
		else if(signature.equals("B")){str = "byte";}//byte
		else if(signature.equals("C")){str = "char";}//char
		else if(signature.equals("Z")){str = "boolean";}//boolean
		else if(signature.substring(0, 1).equals("L")){str = signature.substring(1).substring(signature.lastIndexOf("/")).replace(";", "");}
		else if(signature.substring(0, 1).equals("["))
		{
			String chkType = signature.substring(1, 2);
			if(chkType.equals("I")){str = "int[ ]";}//int
			else if(chkType.equals("J")){str = "long[ ]";}//long
			else if(chkType.equals("D")){str = "double[ ]";}//double
			else if(chkType.equals("F")){str = "float[ ]";}//float
			else if(chkType.equals("B")){str = "byte[ ]";}//byte
			else if(chkType.equals("C")){str = "char[ ]";}//char
			else if(chkType.equals("Z")){str = "boolean[ ]";}//boolean
			else if(chkType.equals("L")){str = signature.substring(1).substring(signature.lastIndexOf("/")).replace(";", "") + "[ ]";}
		}
		return str;
	}
	
	@SuppressWarnings("unchecked")
	private void setComponentList(String key)
	{
		if(popupList.setItems(key)) return;
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ComponentConfig");
		fields.put(IPC.NB_INSTNCE_ID	, instanceID);
		fields.put("CMD_CODE"			, "L");
		fields.put("SCH_TYPE"			, "SCH_ID");
		fields.put("SMPL"				, "Y");
		fields.put("SCH"				, key);
		try {
			fields = protocol.execute(fields);

			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}

			ArrayList<NBFields> list = (ArrayList<NBFields>)fields.get("CMP_LST");
			componentMap = new HashMap<String, String>();
			String[] classList = new String[list.size()];
			for(int i=0; i<classList.length; i++)
			{
				classList[i] = (String)list.get(i).get("ID");
				componentMap.put((String)list.get(i).get("ID"), (String)list.get(i).get("CLASS"));
			}
			
			popupList.setItems(classList);
		} catch (Exception e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
		}
		
	}

	@Override
	protected void confirm() {
		if(returnMap.get("CPN") == null || returnMap.get("CPN").equals(""))
		{
			IMessageBox.Error(shell, "Input component ID.");
			return;
		}
		if(returnMap.get("CN") == null || returnMap.get("CN").equals(""))
		{
			IMessageBox.Error(shell, "Input class name.");
			return;
		}
		if(returnMap.get("MN") == null || returnMap.get("MN").equals(""))
		{
			IMessageBox.Error(shell, "Input method name.");
			return;
		}
		if(((Boolean)returnMap.get("ISR")) && (returnMap.get("RMK") == null || returnMap.get("RMK").equals("")))
		{
			IMessageBox.Error(shell, "Need mapping key.");
			return;
		}
		ComponentArgumentModel root = (ComponentArgumentModel)constTable.getInput();
		int size = root.getChildren().size();
		String[] typeArray = new String[size];
		String[] argNameArray = new String[size];
		Object[] argArray = new Object[size];
		boolean[] isSysValArray = new boolean[size];
		for(int i=0; i<size; i++)
		{
			ComponentArgumentModel model = (ComponentArgumentModel) root.getChildren().get(i);
			typeArray[i] = model.getType();
			argNameArray[i] = model.getArgumentName();
			argArray[i] = model.getArgumentValue();
			if(argArray[i] == null) return;
			isSysValArray[i] = model.isUseSystemValue();
		}
		
		returnMap.put("CA", new ComponentArgumentContext(typeArray, argNameArray, argArray, isSysValArray));
		
		root = (ComponentArgumentModel)methodTable.getInput();
		size = root.getChildren().size();
		typeArray = new String[size];
		argNameArray = new String[size];
		argArray = new Object[size];
		isSysValArray = new boolean[size];
		for(int i=0; i<size; i++)
		{
			ComponentArgumentModel model = (ComponentArgumentModel) root.getChildren().get(i);
			typeArray[i] = model.getType();
			argNameArray[i] = model.getArgumentName();
			argArray[i] = model.getArgumentValue();
			if(argArray[i] == null) return;
			isSysValArray[i] = model.isUseSystemValue();
		}
		returnMap.put("MA", new ComponentArgumentContext(typeArray, argNameArray, argArray, isSysValArray));
		shell.dispose();
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}
}
