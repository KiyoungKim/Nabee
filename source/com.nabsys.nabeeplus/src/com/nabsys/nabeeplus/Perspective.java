package com.nabsys.nabeeplus;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;
import org.eclipse.ui.console.IConsoleConstants;

import com.nabsys.nabeeplus.views.InstanceView;
import com.nabsys.nabeeplus.views.SearchResultView;
import com.nabsys.nabeeplus.views.ServerView;
import com.nabsys.nabeeplus.views.ServiceList;
import com.nabsys.nabeeplus.views.ServiceRequestView;
import com.nabsys.nabeeplus.views.SqlParameterView;
import com.nabsys.nabeeplus.views.SqlResultView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		
		IFolderLayout leftTop 		= layout.createFolder("leftTop", IPageLayout.LEFT, 0.20f, layout.getEditorArea());
		IFolderLayout leftBottom 	= layout.createFolder("leftBottom", IPageLayout.BOTTOM, 0.25f, "leftTop");
		IFolderLayout rightBottom 	= layout.createFolder("rightBottom", IPageLayout.BOTTOM, 0.70f, layout.getEditorArea());
		
		leftTop.addView(ServerView.ID);
		leftBottom.addView(InstanceView.ID);
		IViewLayout vLayout = layout.getViewLayout(ServerView.ID);
		vLayout.setCloseable(false);
		
		vLayout = layout.getViewLayout(InstanceView.ID);
		vLayout.setCloseable(false);
		leftBottom.addPlaceholder(ServiceList.ID);
		rightBottom.addPlaceholder(ServiceRequestView.ID);
		rightBottom.addPlaceholder(SqlParameterView.ID);
		rightBottom.addPlaceholder(SearchResultView.ID);
		rightBottom.addPlaceholder(SqlResultView.ID);
		rightBottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
	}

}
