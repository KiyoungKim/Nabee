package com.nabsys.nabeeplus;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.nabsys.nabeeplus.actions.NewServerAction;
import com.nabsys.nabeeplus.actions.ShowConsoleViewAction;
import com.nabsys.nabeeplus.actions.ShowSearchResultViewAction;
import com.nabsys.nabeeplus.actions.ShowServiceRequestViewAction;
import com.nabsys.nabeeplus.actions.ShowSqlParamViewAction;
import com.nabsys.nabeeplus.actions.ShowSqlResultViewAction;
import com.nabsys.nabeeplus.common.ResourceFactory;
import com.nabsys.nabeeplus.common.label.NBLabel;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}
	
	protected void makeActions(IWorkbenchWindow window) {
		IWorkbenchAction exitAction = ActionFactory.QUIT.create(window);
		exitAction.setToolTipText(NBLabel.get(0x004D));
		exitAction.setText(NBLabel.get(0x005A));
		register(exitAction);
		
		IWorkbenchAction aboutAction = ActionFactory.ABOUT.create(window);
		aboutAction.setToolTipText(NBLabel.get(0x004E));
		aboutAction.setText(NBLabel.get(0x005B));
		register(aboutAction);
		
		IWorkbenchAction undoAction = ActionFactory.UNDO.create(window);
		undoAction.setToolTipText(NBLabel.get(0x005E));
		undoAction.setText(NBLabel.get(0x0063));
		register(undoAction);
		
		IWorkbenchAction redoAction = ActionFactory.REDO.create(window);
		redoAction.setToolTipText(NBLabel.get(0x005E));
		redoAction.setText(NBLabel.get(0x0064));
		register(redoAction);
		
		IWorkbenchAction cutAction = ActionFactory.CUT.create(window);
		cutAction.setToolTipText(NBLabel.get(0x005F));
		cutAction.setText(NBLabel.get(0x0065));
		register(cutAction);
		
		IWorkbenchAction copyAction = ActionFactory.COPY.create(window);
		copyAction.setToolTipText(NBLabel.get(0x0060));
		copyAction.setText(NBLabel.get(0x0066));
		register(copyAction);
		
		IWorkbenchAction pasteAction = ActionFactory.PASTE.create(window);
		pasteAction.setToolTipText(NBLabel.get(0x0061));
		pasteAction.setText(NBLabel.get(0x0067));
		register(pasteAction);
		
		IWorkbenchAction deleteAction = ActionFactory.DELETE.create(window);
		deleteAction.setToolTipText(NBLabel.get(0x0062));
		deleteAction.setText(NBLabel.get(0x0068));
		register(deleteAction);
		
		IWorkbenchAction saveAction = ActionFactory.SAVE.create(window);
		saveAction.setToolTipText(NBLabel.get(0x0069));
		saveAction.setText(NBLabel.get(0x006C));
		register(saveAction);
		
		IWorkbenchAction saveAllAction = ActionFactory.SAVE_ALL.create(window);
		saveAllAction.setToolTipText(NBLabel.get(0x006A));
		saveAllAction.setText(NBLabel.get(0x006D));
		register(saveAllAction);
		
		IWorkbenchAction renameAction = ActionFactory.RENAME.create(window);
		renameAction.setToolTipText(NBLabel.get(0x006B));
		renameAction.setText(NBLabel.get(0x006E));
		register(renameAction);
		
		IWorkbenchAction refreshAction = ActionFactory.REFRESH.create(window);
		refreshAction.setToolTipText(NBLabel.get(0x025F));
		refreshAction.setText(NBLabel.get(0x0260));
		register(refreshAction);
		
		register(ResourceFactory.CONNECT.create(window));
		register(ResourceFactory.DISCONNECT.create(window));
		register(ResourceFactory.RUN.create(window));
		register(ResourceFactory.TERMINATE.create(window));
		register(ResourceFactory.RESUME.create(window));
		register(ResourceFactory.NEW_SQL_DIR.create(window));
		register(ResourceFactory.NEW_SQL_DOC.create(window));
		register(ResourceFactory.NEW_SERVICE.create(window));
		register(ResourceFactory.NEW_INSTANCE.create(window));
		register(ResourceFactory.SEARCH.create(window));
		register(ResourceFactory.ARROW.create(window));
		register(ResourceFactory.RELATION.create(window));
		register(ResourceFactory.ASSIGN.create(window));
		register(ResourceFactory.INBOUND_NETWORK.create(window));
		register(ResourceFactory.OUTBOUND_NETWORK.create(window));
		register(ResourceFactory.CLOSE_NETWORK.create(window));
		register(ResourceFactory.MESSAGE_QUEUE.create(window));
		register(ResourceFactory.BATCH.create(window));
		register(ResourceFactory.DATABASE.create(window));
		register(ResourceFactory.SELECT.create(window));
		register(ResourceFactory.UPDATE.create(window));
		register(ResourceFactory.PROCEDURE.create(window));
		register(ResourceFactory.LOOP.create(window));
		register(ResourceFactory.CLIENT_NETWORK.create(window));
		register(ResourceFactory.NETWORK_READ.create(window));
		register(ResourceFactory.NETWORK_WRITE.create(window));
		register(ResourceFactory.FILE.create(window));
		register(ResourceFactory.FILE_READ.create(window));
		register(ResourceFactory.FILE_WRITE.create(window));
		register(ResourceFactory.FILE_DELETE.create(window));
		register(ResourceFactory.SERVICE_CALLER.create(window));
		register(ResourceFactory.THREAD.create(window));
		register(ResourceFactory.COMPONENT.create(window));
		register(ResourceFactory.EXCEPTION.create(window));
		register(ResourceFactory.THROW.create(window));
		register(ResourceFactory.SERVICE_END.create(window));
		
		NewServerAction newServerAction = new NewServerAction(window);
		register(newServerAction);
		
		
		register(new ShowSqlParamViewAction(window));
		register(new ShowSqlResultViewAction(window));
		register(new ShowServiceRequestViewAction(window));
		register(new ShowConsoleViewAction(window));
		register(new ShowSearchResultViewAction(window));
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager newManager = new MenuManager("&New                       ", "file.mnuNew");
		newManager.add(getAction(NewServerAction.ID));
		newManager.add(getAction(ResourceFactory.NEW_INSTANCE.getId()));
		newManager.add(new Separator());
		newManager.add(getAction(ResourceFactory.NEW_SQL_DIR.getId()));
		newManager.add(getAction(ResourceFactory.NEW_SQL_DOC.getId()));
		newManager.add(new Separator());
		newManager.add(getAction(ResourceFactory.NEW_SERVICE.getId()));
		newManager.add(new GroupMarker("file.new.OTHERS"));
		
		MenuManager fileManager = new MenuManager(NBLabel.get(0x0051), "mnuFile");
    	fileManager.add(newManager);
    	fileManager.add(new Separator());
    	fileManager.add(getAction(ActionFactory.SAVE.getId()));
		fileManager.add(getAction(ActionFactory.SAVE_ALL.getId()));
		fileManager.add(new Separator());
		fileManager.add(getAction(ActionFactory.REFRESH.getId()));
		fileManager.add(getAction(ActionFactory.RENAME.getId()));
		fileManager.add(new Separator());
    	fileManager.add(getAction(ActionFactory.QUIT.getId()));
    	
    	MenuManager editManager 			= new MenuManager(NBLabel.get(0x0052), "mnuEdit");
    	editManager.add(getAction(ActionFactory.UNDO.getId()));
    	editManager.add(getAction(ActionFactory.REDO.getId()));
    	editManager.add(new Separator());
    	editManager.add(getAction(ActionFactory.CUT.getId()));
    	editManager.add(getAction(ActionFactory.COPY.getId()));
    	editManager.add(getAction(ActionFactory.PASTE.getId()));
    	editManager.add(new Separator());
    	editManager.add(getAction(ActionFactory.DELETE.getId()));

    	
    	MenuManager searchManager 			= new MenuManager(NBLabel.get(0x0053), "mnuSearch");
    	searchManager.add(getAction(ResourceFactory.SEARCH.getId()));
    	
    	
    	MenuManager runManager 				= new MenuManager(NBLabel.get(0x0054), "mnuRun");
    	runManager.add(getAction(ResourceFactory.RUN.getId()));
    	runManager.add(getAction(ResourceFactory.RESUME.getId()));
    	runManager.add(getAction(ResourceFactory.TERMINATE.getId()));
    	
    	
    	MenuManager connectionManager 		= new MenuManager(NBLabel.get(0x0055), "mnuConnection");
    	connectionManager.add(getAction(ResourceFactory.CONNECT.getId()));
    	connectionManager.add(getAction(ResourceFactory.DISCONNECT.getId()));
    	
    	
    	MenuManager configurationManager 	= new MenuManager(NBLabel.get(0x0056), "mnuConfiguration");
    	
    	
    	MenuManager windowManager 			= new MenuManager(NBLabel.get(0x0057), "mnuWindow");
    	windowManager.add(getAction(ShowSqlParamViewAction.ID));
    	windowManager.add(getAction(ShowSqlResultViewAction.ID));
    	windowManager.add(getAction(ShowServiceRequestViewAction.ID));
    	windowManager.add(getAction(ShowConsoleViewAction.ID));
    	windowManager.add(getAction(ShowSearchResultViewAction.ID));

    	MenuManager helpManager 			= new MenuManager(NBLabel.get(0x005C), "mnuHelp");
    	helpManager.add(getAction(ActionFactory.ABOUT.getId()));
    	
		menuBar.add(fileManager);
    	menuBar.add(editManager);
    	menuBar.add(searchManager);
    	menuBar.add(runManager);
    	menuBar.add(connectionManager);
    	menuBar.add(configurationManager);
    	menuBar.add(windowManager);
    	menuBar.add(helpManager);
	}
	
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbarGeneral = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbarGeneral, "General"));  
		toolbarGeneral.add(getAction(NewServerAction.ID));
		toolbarGeneral.add(getAction(ResourceFactory.NEW_INSTANCE.getId()));
		toolbarGeneral.add(getAction(ResourceFactory.NEW_SQL_DIR.getId()));
		toolbarGeneral.add(getAction(ResourceFactory.NEW_SQL_DOC.getId()));
		toolbarGeneral.add(getAction(ResourceFactory.NEW_SERVICE.getId()));
        toolbarGeneral.add(getAction(ActionFactory.SAVE.getId()));
        toolbarGeneral.add(getAction(ActionFactory.SAVE_ALL.getId()));
        toolbarGeneral.add(new Separator()); 
        toolbarGeneral.add(getAction(ResourceFactory.CONNECT.getId()));
        toolbarGeneral.add(getAction(ResourceFactory.DISCONNECT.getId()));
        toolbarGeneral.add(new Separator());
        toolbarGeneral.add(getAction(ResourceFactory.SEARCH.getId()));
        toolbarGeneral.add(new Separator());
        toolbarGeneral.add(getAction(ActionFactory.UNDO.getId()));
        toolbarGeneral.add(getAction(ActionFactory.REDO.getId()));
        toolbarGeneral.add(getAction(ActionFactory.CUT.getId()));
        toolbarGeneral.add(getAction(ActionFactory.COPY.getId()));
        toolbarGeneral.add(getAction(ActionFactory.PASTE.getId()));
        toolbarGeneral.add(getAction(ActionFactory.DELETE.getId()));
        toolbarGeneral.add(new Separator());
        toolbarGeneral.add(getAction(ResourceFactory.RUN.getId()));
        toolbarGeneral.add(getAction(ResourceFactory.RESUME.getId()));
        toolbarGeneral.add(getAction(ResourceFactory.TERMINATE.getId()));
        
        IToolBarManager toolbarServiceDesigner = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolbarServiceDesigner, "ServiceDesigner"));
        toolbarServiceDesigner.add(getAction(ResourceFactory.ARROW.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.RELATION.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.ASSIGN.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.LOOP.getId()));
        toolbarServiceDesigner.add(new Separator());
        toolbarServiceDesigner.add(getAction(ResourceFactory.INBOUND_NETWORK.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.OUTBOUND_NETWORK.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.CLOSE_NETWORK.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.MESSAGE_QUEUE.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.BATCH.getId()));
        toolbarServiceDesigner.add(new Separator());
        toolbarServiceDesigner.add(getAction(ResourceFactory.DATABASE.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.SELECT.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.UPDATE.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.PROCEDURE.getId()));
        toolbarServiceDesigner.add(new Separator());
        toolbarServiceDesigner.add(getAction(ResourceFactory.CLIENT_NETWORK.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.NETWORK_READ.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.NETWORK_WRITE.getId()));
        toolbarServiceDesigner.add(new Separator());
        toolbarServiceDesigner.add(getAction(ResourceFactory.FILE.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.FILE_READ.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.FILE_WRITE.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.FILE_DELETE.getId()));
        toolbarServiceDesigner.add(new Separator());
        toolbarServiceDesigner.add(getAction(ResourceFactory.SERVICE_CALLER.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.THREAD.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.COMPONENT.getId()));
        toolbarServiceDesigner.add(new Separator());
        toolbarServiceDesigner.add(getAction(ResourceFactory.THROW.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.EXCEPTION.getId()));
        toolbarServiceDesigner.add(getAction(ResourceFactory.SERVICE_END.getId()));
	}
}
