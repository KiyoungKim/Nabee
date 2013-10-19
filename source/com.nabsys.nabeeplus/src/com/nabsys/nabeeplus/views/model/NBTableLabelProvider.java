package com.nabsys.nabeeplus.views.model;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class NBTableLabelProvider implements ITableLabelProvider {

	Display display = null;
	
	public NBTableLabelProvider(Display display)
	{
		this.display = display;
	}
	
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return ((TableModel)element).getImage(columnIndex);
	}

	public String getColumnText(Object element, int columnIndex) {
		return ((TableModel)element).getValue(columnIndex);
	}

}
