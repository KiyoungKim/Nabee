package com.nabsys.nabeeplus.views.model;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class NBTableContentProvider implements IStructuredContentProvider{

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	public Object[] getElements(Object inputElement) {
		return ((TableModel)inputElement).getChildren().toArray();
	}


}
