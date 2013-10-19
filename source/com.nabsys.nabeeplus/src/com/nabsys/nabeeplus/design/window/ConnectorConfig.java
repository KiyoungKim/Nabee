package com.nabsys.nabeeplus.design.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.design.model.MapFieldModel;
import com.nabsys.nabeeplus.design.model.MapListModel;
import com.nabsys.nabeeplus.design.model.TreeEditingSupport;
import com.nabsys.nabeeplus.views.model.Model;
import com.nabsys.nabeeplus.views.model.NBContentProvider;
import com.nabsys.nabeeplus.views.model.NBLabelProvider;
import com.nabsys.nabeeplus.widgets.NStyledText;
import com.nabsys.resource.script.FuncParser;
import com.nabsys.resource.script.Function;
import com.nabsys.resource.script.FunctionDescription;
import com.nabsys.resource.script.FunctionExecutor;
import com.nabsys.resource.service.MappingObject;

public class ConnectorConfig extends ConfigPopupWindow implements PaintListener{
	private Action 						newFieldSrcAction 	= null;
	private Action 						newListSrcAction 	= null;
	private Action 						deleteSrcAction 	= null;
	private Action 						newFieldTgtAction 	= null;
	private Action 						newListTgtAction 	= null;
	private Action 						deleteTgtAction 	= null;
	private Composite 					mappingPanel 		= null;
	
	private CLabel 						lblMapKey 			= null;
	private StyledText 					txtScript 			= null;
	private StyledText					txtCompair			= null;
			
	private Transfer[] 					types 				= new Transfer[] {TextTransfer.getInstance()};
	private LinkedHashMap<Model, Model> mapping 			= new LinkedHashMap<Model, Model>();
	private Model						selectModel			= null;
	private Model						srcModel			= null;
	
	private Styler 						funcStyler 			= null;
	private Styler 						dQuoteStyle 		= null;
	private Styler 						paramStyle 			= null;
	private Styler						nullStyle			= null;
	private Pattern						paramPtrn 			= null;
	private Pattern 					funcPtrn 			= null;
	private Pattern 					dQuotePtrn 			= null;
	private Pattern						nullPtrn			= null;
	private int							tgtListIndex		= 0;
	
	public ConnectorConfig(Shell parent) {
		super(parent);
	}

	public HashMap<String, Object> open(IWorkbenchWindow window, Image icon) {
		super.open(window, icon, "Relation Mapping", new Point(900, 700));
		Composite contentsBack = getContentsBack();
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		contentsBack.setLayout(layout);
		
		funcStyler = new Styler() {
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = new Color(display, 242,105,154);
				textStyle.font = new Font(display, "Arial", 9, SWT.BOLD);
			}
		};
		
		nullStyle = new Styler() {
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = new Color(shell.getDisplay(), 127,0,1);
				textStyle.font = new Font(display, "Courier New", 10, SWT.BOLD);
			}
		};
		
		dQuoteStyle = new Styler() {
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = display.getSystemColor(SWT.COLOR_BLUE);
			}
		};
		
		paramStyle = new Styler() {
			public void applyStyles(TextStyle textStyle) {
				textStyle.foreground = new Color(shell.getDisplay(), 127,0,1);
				textStyle.font = new Font(display, "Courier New", 10, SWT.BOLD);
			}
		};
		
		String funcPtrnStr = "";
		Function[] enmFnc = Function.values();
		for(int i=0; i< enmFnc.length; i++)
		{
			funcPtrnStr += enmFnc[i].pattern().replaceAll("\\\\b", "");
			if(i < enmFnc.length - 1)
			{
				funcPtrnStr += "|";
			}
		}
		
		paramPtrn = Pattern.compile(":[a-zA-Z0-9_]+(\\[:[a-zA-Z0-9_]+\\])?");
		funcPtrn = Pattern.compile("("+funcPtrnStr+")+\\p{javaWhitespace}*\\(");
		//따옴표 묶음 검색 패턴
		dQuotePtrn = Pattern.compile("\"[^\"]*\"");
		nullPtrn = Pattern.compile("(NULL)|(null)");
		
	
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 25;
		
		Composite top = new Composite(contentsBack, SWT.NONE);
		top.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		top.setLayoutData(layoutData);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 0;
		top.setLayout(layout);
		setTopArea(top);
		
		
		Composite middle = new Composite(contentsBack, SWT.NONE);
		middle.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		middle.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		middle.setLayout(layout);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, false);
		layoutData.heightHint = 200;
		Composite bottom = new Composite(contentsBack, SWT.NONE);
		bottom.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		bottom.setLayoutData(layoutData);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 5;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		bottom.setLayout(layout);
		setBottomArea(bottom);
		
		TreeViewer srcTree = new TreeViewer(middle, SWT.BORDER| SWT.H_SCROLL | SWT.V_SCROLL);
		srcTree.setContentProvider(new NBContentProvider(display, true));
		srcTree.setLabelProvider(new NBLabelProvider(display));
		srcTree.setUseHashlookup(true);
		srcTree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		srcTree.getTree().setLinesVisible(true);
		
		TreeViewerColumn column = new TreeViewerColumn(srcTree, SWT.NONE);
		column.setLabelProvider(new ColumnLabelProvider(){
			public void update(ViewerCell cell) {
				((Model)cell.getElement()).setViewerRow(cell.getViewerRow());
				super.update(cell);
			}
			
			public String getText(Object element) {
	            return ((Model)element).getName();
	         }
			public Image getImage(Object element) {
				return ((Model)element).getImage();
			}
		});
		column.setEditingSupport(new TreeEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl())));
		column.getColumn().setWidth(300);
		
		
		setSrcTreeFunction(srcTree, window);
		
		

		
		
		mappingPanel = new Composite(middle, SWT.NONE | SWT.DOUBLE_BUFFERED);
		mappingPanel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
		layoutData.widthHint = 150;
		mappingPanel.setLayoutData(layoutData);
		mappingPanel.addPaintListener(this);
		
		
		
		
		
		
		
		
		TreeViewer tgtTree = new TreeViewer(middle, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tgtTree.setContentProvider(new NBContentProvider(display, true));
		tgtTree.setLabelProvider(new NBLabelProvider(middle.getDisplay()));
		tgtTree.setUseHashlookup(true);
		tgtTree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tgtTree.getTree().setLinesVisible(true);
		
		
		column = new TreeViewerColumn(tgtTree, SWT.NONE);
		column.setLabelProvider(new ColumnLabelProvider(){
			public void update(ViewerCell cell) {
				((Model)cell.getElement()).setViewerRow(cell.getViewerRow());
				super.update(cell);
			}
			
			public String getText(Object element) {
	            return ((Model)element).getName();
	         }
			public Image getImage(Object element) {
				return ((Model)element).getImage();
			}
		});
		column.setEditingSupport(new TreeEditingSupport(column.getViewer(), 
				new TextCellEditor((Composite)column.getViewer().getControl())));
		column.getColumn().setWidth(300);
		
		setTgtTreeFunction(tgtTree, window);
		
		
		tgtListIndex = setMappingData(srcTree, tgtTree) + 10;

		returnMap.put("TGT_MODEL", tgtTree.getInput());
		contentsBack.layout(true);
		while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
            {
            	shell.getDisplay().sleep();
            }
        }
		returnMap.remove("TGT_MODEL");
		return returnMap;
	}
	
	@SuppressWarnings("unchecked")
	private int setMappingData(TreeViewer srcTree, TreeViewer tgtTree)
	{
		int rtnIndex = 0;
		if(returnMap.get("MD") == null) return rtnIndex;
		LinkedHashMap<String, Object> tmpMap = (LinkedHashMap<String, Object>)returnMap.get("MD");
		
		Model srcRoot = (Model)srcTree.getInput();
		Model tgtRoot = (Model)tgtTree.getInput();

		Iterator<String> itr = tmpMap.keySet().iterator();
		while(itr.hasNext())
		{
			String srcName = itr.next();
			
			if(tmpMap.get(srcName) == null)
			{
				mapping.put(new MapFieldModel(srcRoot, srcName, Activator.getImageDescriptor("/icons/topic.gif").createImage()), null);
			}
			else if(srcName.matches("^\\|[0-9]+\\|$"))
			{
				MappingObject mo = (MappingObject)tmpMap.get(srcName);
				MapFieldModel fm = new MapFieldModel(tgtRoot, mo.getTargetMapKey(), Activator.getImageDescriptor("/icons/topic.gif").createImage());
				fm.setIndex(mo.getIndex());
				rtnIndex++;
				fm.setData("SCRIPT", mo.getScript());
				mapping.put(fm, fm);
			}
			else if(tmpMap.get(srcName) instanceof LinkedHashMap)
			{
				//No next mapping
				if(srcName.matches("^.*:$"))
				{
					String srcListName = srcName.split(":")[0];
					MapListModel srcList = null;
					mapping.put(srcList = new MapListModel(srcRoot, srcListName, Activator.getImageDescriptor("/icons/list_obj.gif").createImage()), null);
					
					LinkedHashMap<String, Object> children = (LinkedHashMap<String, Object>)tmpMap.get(srcName);
					Iterator<String> subItr = children.keySet().iterator();
					while(subItr.hasNext())
					{
						String subSrcName = subItr.next();
						mapping.put(new MapFieldModel(srcList, subSrcName, Activator.getImageDescriptor("/icons/topic.gif").createImage()), null);
					}
				}
				else
				{
					String[] nameAry = srcName.split(":");
					MapListModel srcList = null;
					MapListModel tgtList = null;

					if(srcName.matches("^:.*$"))
					{
						tgtList = new MapListModel(tgtRoot, nameAry[1], Activator.getImageDescriptor("/icons/list_obj.gif").createImage());
						srcList = tgtList;
						mapping.put(srcList, tgtList);
					}
					else
					{
						mapping.put(srcList = new MapListModel(srcRoot, nameAry[0], Activator.getImageDescriptor("/icons/list_obj.gif").createImage()), 
								tgtList = new MapListModel(tgtRoot, nameAry[1], Activator.getImageDescriptor("/icons/list_obj.gif").createImage()));
						tgtList.setInstanceID(srcList+"");
					}
					LinkedHashMap<String, Object> children = (LinkedHashMap<String, Object>)tmpMap.get(srcName);
					if(children.get("$") != null)
					{
						tgtList.setIndex((Integer)children.get("$"));
						rtnIndex++;
					}
					
					Iterator<String> subItr = children.keySet().iterator();
					while(subItr.hasNext())
					{
						String subSrcName = subItr.next();
						
						if(subSrcName.equals("$")) continue;
						
						if(children.get(subSrcName) == null)
						{
							mapping.put(new MapFieldModel(srcList, subSrcName, Activator.getImageDescriptor("/icons/topic.gif").createImage()), null);
						}
						else
						{
							MappingObject mo = (MappingObject)children.get(subSrcName);
							MapFieldModel fm = new MapFieldModel(tgtList, mo.getTargetMapKey(), Activator.getImageDescriptor("/icons/topic.gif").createImage());
							fm.setData("SCRIPT", mo.getScript());
							if(subSrcName.matches("^\\|[0-9]+\\|$"))
								mapping.put(fm, fm);
							else
								mapping.put(new MapFieldModel(srcList, subSrcName, Activator.getImageDescriptor("/icons/topic.gif").createImage()), fm);
							
							fm.setIndex(mo.getIndex());
							rtnIndex++;
						}
					}
				}
			}
			else
			{
				MappingObject mo = (MappingObject)tmpMap.get(srcName);
				MapFieldModel fm = new MapFieldModel(tgtRoot, mo.getTargetMapKey(), Activator.getImageDescriptor("/icons/topic.gif").createImage());
				fm.setIndex(mo.getIndex());
				rtnIndex++;
				fm.setData("SCRIPT", mo.getScript());
				mapping.put(new MapFieldModel(srcRoot, srcName, Activator.getImageDescriptor("/icons/topic.gif").createImage()), fm);
			}
		}
		srcTree.refresh();
		srcTree.expandAll();
		tgtTree.refresh();
		tgtTree.expandAll();
		mappingPanel.redraw();
		return rtnIndex;
	}
	
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		
		if(mapping != null && mapping.size() > 0)
		{
			Composite composite = (Composite)e.getSource();
			Rectangle compRect = composite.getBounds();
			
			Iterator<Model> itr = mapping.keySet().iterator();
			while(itr.hasNext())
			{
				Model src = itr.next();
				Model tgt = mapping.get(src);
				
				if(tgt == null) continue;
				if(src == tgt) continue;

				if(selectModel != null && tgt == selectModel)
					gc.setForeground(new Color(display, 242,105,154));
				else
					gc.setForeground(new Color(display, 45, 94, 175));

				Rectangle srcRect = src.getLinkRectangle();
				Rectangle tgtRect = tgt.getLinkRectangle();
				if(srcRect.height == 0 && src.getParent() instanceof MapListModel) srcRect = src.getParent().getLinkRectangle();
				if(tgtRect.height == 0 && tgt.getParent() instanceof MapListModel) tgtRect = tgt.getParent().getLinkRectangle();
				
				gc.drawLine(0, srcRect.y + 1, 5, srcRect.y + 1);
				gc.drawLine(0, srcRect.y + srcRect.height - 1, 5, srcRect.y + srcRect.height - 1);
				gc.drawLine(5, srcRect.y + 1, 5, srcRect.y + srcRect.height - 1);
				
				gc.drawLine(compRect.width - 5, tgtRect.y + 1, compRect.width, tgtRect.y + 1);
				gc.drawLine(compRect.width - 5, tgtRect.y + tgtRect.height -1, compRect.width, tgtRect.y + tgtRect.height - 1);
				gc.drawLine(compRect.width - 5, tgtRect.y + 1, compRect.width - 5, tgtRect.y + tgtRect.height - 1);
				
				if(selectModel != null && tgt == selectModel)
					gc.setLineWidth(2);
				gc.drawLine(5, srcRect.y + (srcRect.height / 2), compRect.width - 5, tgtRect.y + (tgtRect.height / 2));
				gc.setLineWidth(1);
			}
		}
		
		
		gc.dispose();
	}
	
	private void setBottomArea(Composite back)
	{
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 15;
		layoutData.widthHint = 450;
		
		lblMapKey = new CLabel(back, SWT.NONE);
		lblMapKey.setText("Script Editor");
		lblMapKey.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		lblMapKey.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 10;
		layoutData.widthHint = 100;
		
		CLabel label = new CLabel(back, SWT.NONE);
		label.setText("Function List");
		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		label.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, false);
		layoutData.heightHint = 10;
		layoutData.widthHint = 255;
		
		label = new CLabel(back, SWT.NONE);
		label.setText("Function Description");
		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		label.setLayoutData(layoutData);
		
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);

		txtScript = new StyledText(back, SWT.BORDER| SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		txtScript.setLayoutData(layoutData);
		txtScript.setMargins(5, 5, 5, 0);
		txtScript.setFont(new Font(display, "Arial", 9, SWT.NONE));
		txtScript.setEditable(false);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
		layoutData.widthHint = 100;
		
		List lstFunc = new List(back, SWT.BORDER| SWT.V_SCROLL);
		lstFunc.setLayoutData(layoutData);
		Function[] fAr = Function.values();
		for(int i=0; i<fAr.length; i++)
		lstFunc.add(fAr[i].name());
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
		layoutData.widthHint = 255;
		
		final StyledText txtExplain = new StyledText(back, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		txtExplain.setEditable(false);
		txtExplain.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		txtExplain.setLayoutData(layoutData);
		txtExplain.setForeground(new Color(display, 129, 129, 129));
		
		lstFunc.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				List l = (List)e.widget;
				String strFunc = l.getItem(l.getSelectionIndex());
				txtExplain.setText(FunctionDescription.get(strFunc).getDescription(NBLabel.getLocale()));
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		txtScript.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				String strScript = ((StyledText)e.widget).getText();
				
				StyledString styledString = new StyledString();
				
				Matcher funcMatcher = funcPtrn.matcher(strScript);
				Matcher dQuoteMatcher = dQuotePtrn.matcher(strScript);
				Matcher paramMatcher = paramPtrn.matcher(strScript);
				Matcher nullMatcher = nullPtrn.matcher(strScript);
				
				int offset = 0;
				while(true)
				{
					boolean funcFind =funcMatcher.find(offset);
					boolean dQuFind = dQuoteMatcher.find(offset);
					boolean paramFind = paramMatcher.find(offset);
					boolean nullFind = nullMatcher.find(offset);
					int exMatcher = 0;
					
					if(!funcFind && !dQuFind && !paramFind && !nullFind) break;
					else if(funcFind && dQuFind && paramFind && nullFind)
					{
						int minValue = Math.min(Math.min(Math.min(funcMatcher.start(), dQuoteMatcher.start()), paramMatcher.start()), nullMatcher.start());
						if(minValue == funcMatcher.start()) exMatcher = 0;
						else if(minValue == dQuoteMatcher.start()) exMatcher = 1;
						else if(minValue == paramMatcher.start()) exMatcher = 2;
						else if(minValue == nullMatcher.start()) exMatcher = 3;
					}
					else if(!funcFind && dQuFind && paramFind && nullFind)
					{
						int minValue = Math.min(Math.min(dQuoteMatcher.start(), paramMatcher.start()), nullMatcher.start());
						if(minValue == dQuoteMatcher.start()) exMatcher = 1;
						else if(minValue == paramMatcher.start()) exMatcher = 2;
						else if(minValue == nullMatcher.start()) exMatcher = 3;
					}
					else if(funcFind && !dQuFind && paramFind && nullFind)
					{
						int minValue = Math.min(Math.min(funcMatcher.start(), paramMatcher.start()), nullMatcher.start());
						if(minValue == funcMatcher.start()) exMatcher = 0;
						else if(minValue == paramMatcher.start()) exMatcher = 2;
						else if(minValue == nullMatcher.start()) exMatcher = 3;
					}
					else if(funcFind && dQuFind && !paramFind && nullFind)
					{
						int minValue = Math.min(Math.min(funcMatcher.start(), dQuoteMatcher.start()), nullMatcher.start());
						if(minValue == funcMatcher.start()) exMatcher = 0;
						else if(minValue == dQuoteMatcher.start()) exMatcher = 1;
						else if(minValue == nullMatcher.start()) exMatcher = 3;
					}
					else if(funcFind && dQuFind && paramFind && !nullFind)
					{
						int minValue = Math.min(Math.min(funcMatcher.start(), dQuoteMatcher.start()), paramMatcher.start());
						if(minValue == funcMatcher.start()) exMatcher = 0;
						else if(minValue == dQuoteMatcher.start()) exMatcher = 1;
						else if(minValue == paramMatcher.start()) exMatcher = 2;
					}
					else if(!funcFind && !dQuFind && paramFind && nullFind)
					{
						if(paramMatcher.start() < nullMatcher.start()) exMatcher = 2;
						else exMatcher = 3;
					}
					else if(funcFind && !dQuFind && !paramFind && nullFind)
					{
						if(funcMatcher.start() < nullMatcher.start()) exMatcher = 0;
						else exMatcher = 3;
					}
					else if(funcFind && dQuFind && !paramFind && !nullFind)
					{
						if(funcMatcher.start() < dQuoteMatcher.start()) exMatcher = 0;
						else exMatcher = 1;
					}
					else if(!funcFind && dQuFind && paramFind && !nullFind)
					{
						if(dQuoteMatcher.start() < paramMatcher.start()) exMatcher = 1;
						else exMatcher = 2;
					}
					else if(funcFind && !dQuFind && !paramFind && !nullFind)
					{
						exMatcher = 0;
					}
					else if(!funcFind && dQuFind && !paramFind && !nullFind)
					{
						exMatcher = 1;
					}
					else if(!funcFind && !dQuFind && paramFind && !nullFind)
					{
						exMatcher = 2;
					}
					else if(!funcFind && !dQuFind && !paramFind && nullFind)
					{
						exMatcher = 3;
					}

					StyledString tmpStyl = null;
					switch(exMatcher){
					case 0:
						if(offset < funcMatcher.start()) styledString.append(strScript.substring(offset, funcMatcher.start()));
						offset = funcMatcher.end() - 1;
						tmpStyl = new StyledString(strScript.substring(funcMatcher.start(), funcMatcher.end()-1), funcStyler);
						break;
					case 1:
						if(offset < dQuoteMatcher.start()) styledString.append(strScript.substring(offset, dQuoteMatcher.start()));
						
						offset = dQuoteMatcher.end();
						tmpStyl = new StyledString(dQuoteMatcher.group(), dQuoteStyle);
						break;
					case 2:
						if(offset < paramMatcher.start()) styledString.append(strScript.substring(offset, paramMatcher.start()));
						
						offset = paramMatcher.end();
						tmpStyl = new StyledString(paramMatcher.group(), paramStyle);
						break;
					case 3:
						if(offset < nullMatcher.start()) styledString.append(strScript.substring(offset, nullMatcher.start()));
						
						offset = nullMatcher.end();
						tmpStyl = new StyledString(nullMatcher.group(), nullStyle);
						break;
					default:
						break;
					}
					
					styledString.append(tmpStyl);
					
				}
				
				((StyledText)e.widget).setStyleRanges(styledString.getStyleRanges());
			}
		});
	}
	
	private void setTopArea(Composite back)
	{
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
		layoutData.widthHint = 35;
		CLabel label = new CLabel(back, SWT.NONE);
		label.setText("IF (");
		label.setFont(new Font(display, "Courier New", 10, SWT.BOLD));
		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		label.setForeground(display.getSystemColor(SWT.COLOR_RED));
		label.setLayoutData(layoutData);
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		txtCompair = new NStyledText(back, SWT.BORDER);
		txtCompair.setMargins(5, 2, 5, 0);
		txtCompair.setLayoutData(layoutData);
		txtCompair.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				String strScript = ((StyledText)e.widget).getText();
				
				StyledString styledString = new StyledString();
				
				Matcher funcMatcher = funcPtrn.matcher(strScript);
				Matcher dQuoteMatcher = dQuotePtrn.matcher(strScript);
				Matcher paramMatcher = paramPtrn.matcher(strScript);
				Matcher nullMatcher = nullPtrn.matcher(strScript);
				
				int offset = 0;
				while(true)
				{
					boolean funcFind =funcMatcher.find(offset);
					boolean dQuFind = dQuoteMatcher.find(offset);
					boolean paramFind = paramMatcher.find(offset);
					boolean nullFind = nullMatcher.find(offset);
					int exMatcher = 0;
					
					if(!funcFind && !dQuFind && !paramFind && !nullFind) break;
					else if(funcFind && dQuFind && paramFind && nullFind)
					{
						int minValue = Math.min(Math.min(Math.min(funcMatcher.start(), dQuoteMatcher.start()), paramMatcher.start()), nullMatcher.start());
						if(minValue == funcMatcher.start()) exMatcher = 0;
						else if(minValue == dQuoteMatcher.start()) exMatcher = 1;
						else if(minValue == paramMatcher.start()) exMatcher = 2;
						else if(minValue == nullMatcher.start()) exMatcher = 3;
					}
					else if(!funcFind && dQuFind && paramFind && nullFind)
					{
						int minValue = Math.min(Math.min(dQuoteMatcher.start(), paramMatcher.start()), nullMatcher.start());
						if(minValue == dQuoteMatcher.start()) exMatcher = 1;
						else if(minValue == paramMatcher.start()) exMatcher = 2;
						else if(minValue == nullMatcher.start()) exMatcher = 3;
					}
					else if(funcFind && !dQuFind && paramFind && nullFind)
					{
						int minValue = Math.min(Math.min(funcMatcher.start(), paramMatcher.start()), nullMatcher.start());
						if(minValue == funcMatcher.start()) exMatcher = 0;
						else if(minValue == paramMatcher.start()) exMatcher = 2;
						else if(minValue == nullMatcher.start()) exMatcher = 3;
					}
					else if(funcFind && dQuFind && !paramFind && nullFind)
					{
						int minValue = Math.min(Math.min(funcMatcher.start(), dQuoteMatcher.start()), nullMatcher.start());
						if(minValue == funcMatcher.start()) exMatcher = 0;
						else if(minValue == dQuoteMatcher.start()) exMatcher = 1;
						else if(minValue == nullMatcher.start()) exMatcher = 3;
					}
					else if(funcFind && dQuFind && paramFind && !nullFind)
					{
						int minValue = Math.min(Math.min(funcMatcher.start(), dQuoteMatcher.start()), paramMatcher.start());
						if(minValue == funcMatcher.start()) exMatcher = 0;
						else if(minValue == dQuoteMatcher.start()) exMatcher = 1;
						else if(minValue == paramMatcher.start()) exMatcher = 2;
					}
					else if(!funcFind && !dQuFind && paramFind && nullFind)
					{
						if(paramMatcher.start() < nullMatcher.start()) exMatcher = 2;
						else exMatcher = 3;
					}
					else if(funcFind && !dQuFind && !paramFind && nullFind)
					{
						if(funcMatcher.start() < nullMatcher.start()) exMatcher = 0;
						else exMatcher = 3;
					}
					else if(funcFind && dQuFind && !paramFind && !nullFind)
					{
						if(funcMatcher.start() < dQuoteMatcher.start()) exMatcher = 0;
						else exMatcher = 1;
					}
					else if(!funcFind && dQuFind && paramFind && !nullFind)
					{
						if(dQuoteMatcher.start() < paramMatcher.start()) exMatcher = 1;
						else exMatcher = 2;
					}
					else if(funcFind && !dQuFind && !paramFind && !nullFind)
					{
						exMatcher = 0;
					}
					else if(!funcFind && dQuFind && !paramFind && !nullFind)
					{
						exMatcher = 1;
					}
					else if(!funcFind && !dQuFind && paramFind && !nullFind)
					{
						exMatcher = 2;
					}
					else if(!funcFind && !dQuFind && !paramFind && nullFind)
					{
						exMatcher = 3;
					}

					StyledString tmpStyl = null;
					switch(exMatcher){
					case 0:
						if(offset < funcMatcher.start()) styledString.append(strScript.substring(offset, funcMatcher.start()));
						offset = funcMatcher.end() - 1;
						tmpStyl = new StyledString(strScript.substring(funcMatcher.start(), funcMatcher.end()-1), funcStyler);
						break;
					case 1:
						if(offset < dQuoteMatcher.start()) styledString.append(strScript.substring(offset, dQuoteMatcher.start()));
						
						offset = dQuoteMatcher.end();
						tmpStyl = new StyledString(dQuoteMatcher.group(), dQuoteStyle);
						break;
					case 2:
						if(offset < paramMatcher.start()) styledString.append(strScript.substring(offset, paramMatcher.start()));
						
						offset = paramMatcher.end();
						tmpStyl = new StyledString(paramMatcher.group(), paramStyle);
						break;
					case 3:
						if(offset < nullMatcher.start()) styledString.append(strScript.substring(offset, nullMatcher.start()));
						
						offset = nullMatcher.end();
						tmpStyl = new StyledString(nullMatcher.group(), nullStyle);
						break;
					default:
						break;
					}
					
					styledString.append(tmpStyl);
					
				}
				((StyledText)e.widget).setStyleRanges(styledString.getStyleRanges());
			}
		});
		
		if(returnMap.get("CS") != null)
			txtCompair.setText(returnMap.get("CS")+"");
		
		layoutData = new GridData(GridData.FILL, GridData.FILL, false, true);
		layoutData.widthHint = 180;
		label = new CLabel(back, SWT.NONE);
		label.setText(") THEN EXECUTE BELOW.");
		label.setFont(new Font(display, "Courier New", 10, SWT.BOLD));
		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		label.setForeground(display.getSystemColor(SWT.COLOR_RED));
		label.setLayoutData(layoutData);
	}
	
	private void setSrcTreeFunction(final TreeViewer srcTree, IWorkbenchWindow window)
	{
		srcTree.setInput(new Model());
		srcTree.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection incoming = event.getSelection();
				if(incoming instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection)incoming;
					if(selection.getFirstElement() instanceof Model)
					{
						deleteSrcAction.setEnabled(true);
						if(!(selection.getFirstElement() instanceof MapListModel) && mapping.containsKey(selection.getFirstElement()))
						{
							Model m = mapping.get(selection.getFirstElement());
							if(m == null) return;
							lblMapKey.setText("Script editor for mapping target '" + m.getName() + "'");
							txtScript.setEditable(true);
							
							if(selectModel != null && selectModel != m)
							{
								selectModel.setData("SCRIPT", txtScript.getText());
							}
							
							selectModel = m;
							mappingPanel.redraw();
							
							String script = null;
							if((script = (String)m.getData("SCRIPT")) != null)
								txtScript.setText(script);
							else
								txtScript.setText("");
						}
						else if(selection.getFirstElement() instanceof MapListModel)
						{
							selectModel = null;
							mappingPanel.redraw();
							txtScript.setText("");
							txtScript.setEditable(false);
							lblMapKey.setText("Script editor");
						}
					}
					else
					{ 
						deleteSrcAction.setEnabled(false);
					}
				}
			}
		});
		
		MenuManager mgr = new MenuManager();
		mgr.add(newFieldSrcAction = new Action(){
			public void run()
			{
				ISelection incoming = srcTree.getSelection();
				if(incoming.isEmpty())
				{
					mapping.put(new MapFieldModel((Model)srcTree.getInput(), "New Field ID", Activator.getImageDescriptor("/icons/topic.gif").createImage()), null);
					srcTree.refresh();
					mappingPanel.redraw();
				}
				else
				{
					if(incoming instanceof IStructuredSelection)
					{
						IStructuredSelection selection = (IStructuredSelection)incoming;
						if(selection.getFirstElement() instanceof MapListModel)
						{
							mapping.put(new MapFieldModel((MapListModel)selection.getFirstElement(), "New Field ID", Activator.getImageDescriptor("/icons/topic.gif").createImage()), null);
							srcTree.refresh();
							srcTree.expandAll();
							mappingPanel.redraw();
						}
						else
						{
							mapping.put(new MapFieldModel(((MapFieldModel)selection.getFirstElement()).getParent(), "New Field ID", Activator.getImageDescriptor("/icons/topic.gif").createImage()), null);
							srcTree.refresh();
							mappingPanel.redraw();
						}
					}
				}
			}
		});
		newFieldSrcAction.setText("Add New Field");
		newFieldSrcAction.setToolTipText("Add new mapping field");
		newFieldSrcAction.setImageDescriptor(Activator.getImageDescriptor("/icons/topic.gif"));
		mgr.add(newListSrcAction = new Action(){
			public void run()
			{
				ISelection incoming = srcTree.getSelection();
				if(incoming.isEmpty())
				{
					mapping.put(new MapListModel((Model)srcTree.getInput(), "New List ID", Activator.getImageDescriptor("/icons/list_obj.gif").createImage()), null);
					srcTree.refresh();
					mappingPanel.redraw();
				}
				else
				{
					if(incoming instanceof IStructuredSelection)
					{
						IStructuredSelection selection = (IStructuredSelection)incoming;
						if(!(selection.getFirstElement() instanceof MapListModel))
						{
							mapping.put(new MapListModel((Model)srcTree.getInput(), "New List ID", Activator.getImageDescriptor("/icons/list_obj.gif").createImage()), null);
							srcTree.refresh();
							mappingPanel.redraw();
						}
					}
				}
				
			}
		});
		newListSrcAction.setText("Add New List");
		newListSrcAction.setToolTipText("Add new mapping list");
		newListSrcAction.setImageDescriptor(Activator.getImageDescriptor("/icons/list_obj.gif"));
		
		mgr.add(deleteSrcAction = new Action(){
			public void run()
			{
				if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) != SWT.CANCEL)
				{
					ISelection incoming = srcTree.getSelection();
					if(!incoming.isEmpty())
					{
						if(incoming instanceof IStructuredSelection)
						{
							IStructuredSelection selection = (IStructuredSelection)incoming;
							if(selection.getFirstElement() instanceof Model)
							{
								Model model = (Model)selection.getFirstElement();
								if(model instanceof MapListModel && model.getChildren().size() > 0)
								{
									for(int i=0; i<model.getChildren().size(); i++)
									{
										if(mapping.get(model.getChildren().get(i)) != null)
										{
											mapping.put(mapping.get(model.getChildren().get(i)), mapping.get(model.getChildren().get(i)));
										}
										mapping.remove(model.getChildren().get(i));
									}
								}
								if(mapping.get(model) != null)
								{
									if(model instanceof MapListModel) ((MapListModel)mapping.get(model)).setInstanceID(null);
									mapping.put(mapping.get(model), mapping.get(model));
								}
								mapping.remove(model);
								
								model.getParent().getChildren().remove(model);
								srcTree.refresh();
								mappingPanel.redraw();
							}
						}
					}
				}
			}
		});
		deleteSrcAction.setText("Delete");
		deleteSrcAction.setToolTipText("Delete mapping field");
		deleteSrcAction.setImageDescriptor(Activator.getImageDescriptor("/icons/delete_obj.gif"));
		deleteSrcAction.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/delete_obj.gif"));
		deleteSrcAction.setEnabled(false);
		
		Menu menu = mgr.createContextMenu(srcTree.getTree());
		srcTree.getTree().setMenu(menu);
		
		srcTree.getColumnViewerEditor().addEditorActivationListener(
		new ColumnViewerEditorActivationListener(){
			private ViewerCell lastSelected = null;
			@Override
			public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
				if (lastSelected == null || !event.getSource().equals(lastSelected))
				{
					lastSelected = (ViewerCell)event.getSource();
					event.cancel = true;
				}
				else
				{
					event.cancel = false;
				}
			}
			@Override
			public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
			}
			@Override
			public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
			}
			@Override
			public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
		}});
		
		DragSourceAdapter dragSourceListener = new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) 
		    {
				IStructuredSelection selection = (IStructuredSelection)srcTree.getSelection();
				
				if(selection.isEmpty()) return;
				
				if(selection.getFirstElement() instanceof Model)
				{
					srcModel = ((Model)selection.getFirstElement());
					String data = "";
					data += srcModel.getClass().getSimpleName()+":";
					data += srcModel.getName()+":";
					data += ((srcModel instanceof MapListModel)?srcModel:srcModel.getParent())+":";
					data += (srcModel.getParent() instanceof MapListModel);
					event.data = data;
				}
		    }
		};
		
		srcTree.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, types, dragSourceListener);
		
		srcTree.getTree().getVerticalBar().addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				mappingPanel.redraw();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				mappingPanel.redraw();
			}
		});
		
		srcTree.addTreeListener(new ITreeViewerListener(){

			public void treeCollapsed(TreeExpansionEvent event) {
				mappingPanel.redraw();
			}

			public void treeExpanded(TreeExpansionEvent event) {
				mappingPanel.redraw();
			}
			
		});
	}
	
	private void setTgtTreeFunction(final TreeViewer tgtTree, IWorkbenchWindow window)
	{
		tgtTree.setInput(new Model());
		tgtTree.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection incoming = event.getSelection();
				if(incoming instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection)incoming;
					if(selection.getFirstElement() instanceof Model)
					{
						deleteTgtAction.setEnabled(true);
						if(!(selection.getFirstElement() instanceof MapListModel))
						{
							Model m = (Model)selection.getFirstElement();
							lblMapKey.setText("Script editor for mapping target '" + m.getName() + "'");
							txtScript.setEditable(true);
							
							if(selectModel != null && selectModel != m)
							{
								selectModel.setData("SCRIPT", txtScript.getText());
							}
							
							selectModel = m;
							mappingPanel.redraw();
							
							String script = null;
							if((script = (String)m.getData("SCRIPT")) != null)
								txtScript.setText(script);
							else
								txtScript.setText("");
						}
						else if(selection.getFirstElement() instanceof MapListModel)
						{
							selectModel = null;
							mappingPanel.redraw();
							txtScript.setText("");
							txtScript.setEditable(false);
							lblMapKey.setText("Script editor");
						}
					}
					else
					{ 
						deleteTgtAction.setEnabled(false);
					}
				}
			}
		});
		
		MenuManager mgr = new MenuManager();
		mgr.add(newFieldTgtAction = new Action(){
			public void run()
			{
				ISelection incoming = tgtTree.getSelection();
				if(incoming.isEmpty())
				{
					Model mm = new MapFieldModel((Model)tgtTree.getInput(), "New Field ID", Activator.getImageDescriptor("/icons/topic.gif").createImage());
					mm.setIndex(tgtListIndex++);
					mapping.put(mm, mm);
					tgtTree.refresh();
				}
				else
				{
					if(incoming instanceof IStructuredSelection)
					{
						IStructuredSelection selection = (IStructuredSelection)incoming;
						if(selection.getFirstElement() instanceof MapListModel)
						{
							Model mm = new MapFieldModel((MapListModel)selection.getFirstElement(), "New Field ID", Activator.getImageDescriptor("/icons/topic.gif").createImage());
							mm.setIndex(tgtListIndex++);
							mapping.put(mm, mm);
							tgtTree.refresh();
							tgtTree.expandAll();
						}
						else
						{
							Model mm = new MapFieldModel(((MapFieldModel)selection.getFirstElement()).getParent(), "New Field ID", Activator.getImageDescriptor("/icons/topic.gif").createImage());
							mm.setIndex(tgtListIndex++);
							mapping.put(mm, mm);
							tgtTree.refresh();
						}
					}
				}
				mappingPanel.redraw();
			}
		});
		newFieldTgtAction.setText("Add New Field");
		newFieldTgtAction.setToolTipText("Add new mapping field");
		newFieldTgtAction.setImageDescriptor(Activator.getImageDescriptor("/icons/topic.gif"));
		mgr.add(newListTgtAction = new Action(){
			public void run()
			{
				ISelection incoming = tgtTree.getSelection();
				if(incoming.isEmpty())
				{
					Model mm = new MapListModel((Model)tgtTree.getInput(), "New List ID", Activator.getImageDescriptor("/icons/list_obj.gif").createImage());
					mm.setIndex(tgtListIndex++);
					mapping.put(mm, mm);
					tgtTree.refresh();
				}
				else
				{
					if(incoming instanceof IStructuredSelection)
					{
						IStructuredSelection selection = (IStructuredSelection)incoming;
						if(!(selection.getFirstElement() instanceof MapListModel))
						{
							Model mm = new MapListModel((Model)tgtTree.getInput(), "New List ID", Activator.getImageDescriptor("/icons/list_obj.gif").createImage());
							mm.setIndex(tgtListIndex++);
							mapping.put(mm, mm);
							tgtTree.refresh();
						}
					}
				}
				mappingPanel.redraw();
			}
		});
		newListTgtAction.setText("Add New List");
		newListTgtAction.setToolTipText("Add new mapping list");
		newListTgtAction.setImageDescriptor(Activator.getImageDescriptor("/icons/list_obj.gif"));
		
		mgr.add(deleteTgtAction = new Action(){
			public void run()
			{
				if(IMessageBox.Confirm(shell, NBLabel.get(0x0092)) != SWT.CANCEL)
				{
					ISelection incoming = tgtTree.getSelection();
					if(!incoming.isEmpty())
					{
						if(incoming instanceof IStructuredSelection)
						{
							IStructuredSelection selection = (IStructuredSelection)incoming;
							if(selection.getFirstElement() instanceof Model)
							{
								Model targetModel = (Model)selection.getFirstElement();
								ArrayList<Model> deleteList = new ArrayList<Model>();
								Iterator<Model> itr = mapping.keySet().iterator();
								while(itr.hasNext())
								{
									Model model = (Model)itr.next();
									if(mapping.get(model) == targetModel)
									{
										if(targetModel instanceof MapListModel && targetModel.getChildren().size() > 0)
										{
											for(int i=0; i<targetModel.getChildren().size(); i++)
											{
												Model tgtChild = targetModel.getChildren().get(i);
												if(mapping.containsKey(tgtChild)) deleteList.add(tgtChild);
												else
												{
													for(int j=0; j<model.getChildren().size(); j++)
													{
														Model srcChild = model.getChildren().get(i);
														if(mapping.get(srcChild) == tgtChild)
														{
															mapping.put(srcChild, null);
														}
													}
												}
											}
										}
										
										if(model == targetModel) deleteList.add(model);
										else mapping.put(model, null);
									}
								}
								for(int i=0; i<deleteList.size(); i++) mapping.remove(deleteList.get(i));
								targetModel.getParent().getChildren().remove(targetModel);
								tgtTree.refresh();
								mappingPanel.redraw();
							}
						}
					}
				}
				mappingPanel.redraw();
			}
		});
		deleteTgtAction.setText("Delete");
		deleteTgtAction.setToolTipText("Delete mapping field");
		deleteTgtAction.setImageDescriptor(Activator.getImageDescriptor("/icons/delete_obj.gif"));
		deleteTgtAction.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/delete_obj.gif"));
		deleteTgtAction.setEnabled(false);
		
		Menu menu = mgr.createContextMenu(tgtTree.getTree());
		tgtTree.getTree().setMenu(menu);
		
		
		tgtTree.getColumnViewerEditor().addEditorActivationListener(
		new ColumnViewerEditorActivationListener(){
			private ViewerCell lastSelected = null;
			@Override
			public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
				if (lastSelected == null || !event.getSource().equals(lastSelected))
				{
					lastSelected = (ViewerCell)event.getSource();
					event.cancel = true;
				}
				else
				{
					event.cancel = false;
				}
			}
			@Override
			public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
			}
			@Override
			public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
			}
			@Override
			public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
		}});
		
		
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
	    		if (TextTransfer.getInstance().isSupportedType(event.currentDataType))
	    		{
	    			if(event.item instanceof TreeItem)
	    			{
	    				String[] ary = ((String)event.data).split(":");
	    				
	    				Tree treeObj = ((TreeItem)event.item).getParent();
	    				Model root = (Model)tgtTree.getInput();
	    				
	    				Model model = getTargetModel(root, treeObj.getItems(), event.item);
	    				
	    				if(model == null) return;
	    				else if(model instanceof MapListModel)
	    				{
	    					//부모가 링크되어있는지 체크
	    					if(!ary[2].equals(((MapListModel)model).getInstanceID()))
	    					{
	    						if(((MapListModel)model).getInstanceID() != null)
	    						return;
	    					}
	    				}
	    				else if(((MapFieldModel)model).getParent() instanceof MapListModel)
	    				{
	    					if(!ary[2].equals(((MapListModel)model.getParent()).getInstanceID()))
	    					{
	    						//if(((MapListModel)model.getParent()).getInstanceID() != null)
	    						return;
	    					}
	    					if(!mapping.containsKey(model))
		    				{
		    					return;
		    				}
	    				}
	    				else if(!mapping.containsKey(model))
	    				{
	    					return;
	    				}
	    				else if(ary[3].equals("true"))
						{
							return;
						}
	    				
	    				
	    				if(ary[0].equals("MapListModel") && model instanceof MapListModel)
	    				{
	    					//mapping
	    					mapping.remove(model);
	    					mapping.put(srcModel, model);
	    					((MapListModel)model).setInstanceID(ary[2]);
	    					mappingPanel.redraw();
	    				}
	    				else if(ary[0].equals("MapFieldModel") && model instanceof MapFieldModel)
	    				{
	    					//mapping
	    					mapping.remove(model);
	    					mapping.put(srcModel, model);
	    					mappingPanel.redraw();
	    				}
	    				else if(ary[0].equals("MapFieldModel") 
	    						&& model instanceof MapListModel 
	    						&& ary[2].equals(((MapListModel)model).getInstanceID()))
	    				{
	    					mapping.put(srcModel, new MapFieldModel(model, ary[1], Activator.getImageDescriptor("/icons/topic.gif").createImage()));
	    					tgtTree.expandAll();
	    					tgtTree.refresh();
	    					mappingPanel.redraw();
	    				}
	    			}
	    			else
	    			{
	    				String[] ary = ((String)event.data).split(":");
						if(ary[0].equals("MapFieldModel"))
						{
							if(ary[3].equals("true"))
							{
								return;
							}
							MapFieldModel mm = null;
							mapping.put(srcModel, mm = new MapFieldModel((Model)tgtTree.getInput(), ary[1], Activator.getImageDescriptor("/icons/topic.gif").createImage()));
							mm.setIndex(tgtListIndex++);
							tgtTree.refresh();
							mappingPanel.redraw();
						}
						else if(ary[0].equals("MapListModel"))
						{
							
							MapListModel model = new MapListModel((Model)tgtTree.getInput(), ary[1], Activator.getImageDescriptor("/icons/list_obj.gif").createImage());
							model.setIndex(tgtListIndex++);
							model.setInstanceID(ary[2]);
							mapping.put(srcModel, model);
							tgtTree.refresh();
							mappingPanel.redraw();
						
						}
	    			}
	    		}
	    	}
		};
		
		tgtTree.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT, types, dropTargetListener);
		
		tgtTree.getTree().getVerticalBar().addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				mappingPanel.redraw();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				mappingPanel.redraw();
			}
		});
		
		tgtTree.addTreeListener(new ITreeViewerListener(){

			public void treeCollapsed(TreeExpansionEvent event) {
				mappingPanel.redraw();
			}

			public void treeExpanded(TreeExpansionEvent event) {
				mappingPanel.redraw();
			}
			
		});
		
		tgtTree.setComparator(new ViewerComparator(){
			public int compare(Viewer viewer, Object o1, Object o2) {
				if(o1 instanceof Model && o2 instanceof Model)
				{
					Model firstModel = (Model)o1;
					Model secondModel = (Model)o2;
					if(firstModel.getIndex() > secondModel.getIndex())
					{
						Model parent = firstModel.getParent();
						int index = 0;
						for(int i=0; i<parent.getChildren().size(); i++)
						{
							if(parent.getChildren().get(i) == firstModel)
							{
								index = i;
								break;
							}
						}
						parent.getChildren().set(index+1, firstModel);
						parent.getChildren().set(index, secondModel);
						return 1;
					}
					else if(firstModel.getIndex() < secondModel.getIndex())
					{
						return -1;
					}
					else return 0;
				}
				else
				{
					return 0;
				}
		    }
		});
	}
	
	@Override
	protected void confirm() {
		FuncParser fp = new FuncParser();
		try {
			if(txtCompair.getText() == null || txtCompair.getText().equals(""))
			{
				returnMap.put("CE", null);
			}
			else
			{
				returnMap.put("CE", fp.parseScript("IF (" + txtCompair.getText() +") THEN 0 ELSE 1 ENDIF"));
			}
			returnMap.put("CS", txtCompair.getText());
		} catch (Exception e) {
			e.printStackTrace();
			if(IMessageBox.Confirm(shell, "Error message - " + e.getMessage() + "\n\n" + NBLabel.get(0x029F)) == SWT.CANCEL) return;
		}
		
		setTargetModelIndex((Model)returnMap.get("TGT_MODEL"));
		
		if(selectModel != null)
		{
			selectModel.setData("SCRIPT", txtScript.getText());
		}
		
		Iterator<Model> itr = mapping.keySet().iterator();
		while(itr.hasNext())
		{
			Model src = itr.next();
			if(!src.getName().matches("^[1-9a-zA-Z_]+$"))
			{
				IMessageBox.Warning(shell, "Field key format error.");
				return;
			}
			if(mapping.get(src) != null && src != mapping.get(src) && !mapping.get(src).getName().matches("^[1-9a-zA-Z_]+$"))
			{
				IMessageBox.Warning(shell, "Field key format error.");
				return;
			}
		}
		
		itr = mapping.keySet().iterator();
		LinkedHashMap<String, Object> returnMapping = setMapping(itr);
		if(returnMapping == null)
		{
			return;
		}
		returnMap.put("MD", returnMapping);
		
		shell.dispose();
	}
	
	private void setTargetModelIndex(Model parent)
	{
		int index = 0;
		for(int i=0; i<parent.getChildren().size(); i++)
		{
			Model child = parent.getChildren().get(i);
			child.setIndex(index);
			index++;
			
			if(child.getChildren().size() > 0)
			{
				setTargetModelIndex(child);
			}
		}
	}
	
	private Model getTargetModel(Model model, TreeItem[] treeItem, Widget item)
	{
		for(int i=0; i<treeItem.length; i++)
		{
			if(treeItem[i] == item) return model.getChildren().get(i);
			if(treeItem[i].getItemCount() > 0)
			{
				Model rtn = getTargetModel(model.getChildren().get(i), treeItem[i].getItems(), item);
				if(rtn != null) return rtn;
			}
		}
		return null;
	}
	
	private LinkedHashMap<String, Object> setMapping(Iterator<Model> itr){
		LinkedHashMap<String, Object> rtnMapping = new LinkedHashMap<String, Object>();
		int cnt = 0;
		while(itr.hasNext()){
			Model src = itr.next();

			if(src instanceof MapListModel)
			{
				
				if(mapping.get(src) == null)
				{
					LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
					ArrayList<Model> children = src.getChildren();
					for(int i=0; i<children.size(); i++)
					{
						Model child = children.get(i);
						tmp.put(child.getName(), null);
					}
					rtnMapping.put(src.getName()+":", tmp);
				}
				else if(src == mapping.get(src))
				{
					LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
					ArrayList<Model> children = src.getChildren();

					for(int i=0; i<children.size(); i++)
					{
						Model child = children.get(i);

						Model target = mapping.get(child);
						if(target == null)
						{
							tmp.put(child.getName(), null);
						}
						else
						{
							FuncParser parser = new FuncParser();
							FunctionExecutor fe = null;
							try {
								if(target.getData("SCRIPT") != null && !((String)target.getData("SCRIPT")).equals(""))
									fe = parser.parseScript((String)target.getData("SCRIPT"));
							} catch (Exception e) {
								if(IMessageBox.Confirm(shell, "Error message - " + e.getMessage() + "\n\n" + "Target field : " + target.getName() + "\n\n" + NBLabel.get(0x0300)) == SWT.CANCEL) return null;
							}
							
							MappingObject mo = new MappingObject(target.getName(), fe, (String)target.getData("SCRIPT"), target.getIndex());
							
							if(child == target)
								tmp.put("|" +(cnt++)+ "|", mo);
							else
								tmp.put(child.getName(), mo);
						}
					}
					tmp.put("$", src.getIndex());
					rtnMapping.put(":"+mapping.get(src).getName(), tmp);
				}
				else
				{
					LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
					ArrayList<Model> children = src.getChildren();

					for(int i=0; i<children.size(); i++)
					{
						Model child = children.get(i);
						Model target = mapping.get(child);
						if(target == null)
						{
							tmp.put(child.getName(), null);
						}
						else
						{
							FuncParser parser = new FuncParser();
							FunctionExecutor fe = null;
							try {
								if(target.getData("SCRIPT") != null && !((String)target.getData("SCRIPT")).equals(""))
									fe = parser.parseScript((String)target.getData("SCRIPT"));
							} catch (Exception e) {
								if(IMessageBox.Confirm(shell, "Error message - " + e.getMessage() + "\n\n" + "Target field : " + target.getName() + "\n\n" + NBLabel.get(0x0300)) == SWT.CANCEL) return null;
							}
							
							MappingObject mo = new MappingObject(target.getName(), fe, (String)target.getData("SCRIPT"), target.getIndex());
							tmp.put(child.getName(), mo);
						}
					}
					
					children = ((Model)mapping.get(src)).getChildren();
					for(int i=0; i<children.size(); i++)
					{
						Model child = children.get(i);
						//key and value same...
						if(mapping.containsKey(child))
						{
							FuncParser parser = new FuncParser();
							FunctionExecutor fe = null;
							try {
								if(child.getData("SCRIPT") != null && !((String)child.getData("SCRIPT")).equals(""))
									fe = parser.parseScript((String)child.getData("SCRIPT"));
							} catch (Exception e) {
								if(IMessageBox.Confirm(shell, "Error message - " + e.getMessage() + "\n\n" + "Target field : " + child.getName() + "\n\n" + NBLabel.get(0x0300)) == SWT.CANCEL) return null;
							}
							MappingObject mo = new MappingObject(child.getName(), fe, (String)child.getData("SCRIPT"), child.getIndex());
							tmp.put("|" +(cnt++)+ "|", mo);
						}
					}
					
					tmp.put("$", mapping.get(src).getIndex());
					rtnMapping.put(src.getName()+":"+mapping.get(src).getName(), tmp);
				}
			}
			else if(!(src.getParent() instanceof MapListModel))
			{
				Model target = mapping.get(src);
				if(target == null)
				{
					rtnMapping.put(src.getName(), null);
				}
				else
				{
					FuncParser parser = new FuncParser();
					FunctionExecutor fe = null;
					try {
						if(target.getData("SCRIPT") != null && !((String)target.getData("SCRIPT")).equals(""))
							fe = parser.parseScript((String)target.getData("SCRIPT"));
					} catch (Exception e) {
						if(IMessageBox.Confirm(shell, "Error message - " + e.getMessage() + "\n\n" + "Target field : " + target.getName() + "\n\n" + NBLabel.get(0x0300)) == SWT.CANCEL) return null;
					}
					MappingObject mo = new MappingObject(target.getName(), fe, (String)target.getData("SCRIPT"), target.getIndex());
					if(src == target)
						rtnMapping.put("|" +(cnt++)+ "|", mo);
					else
						rtnMapping.put(src.getName(), mo);
				}
			}
		}
		return rtnMapping;
	}

	@Override
	protected void cancel() {
		shell.dispose();
	}

}
