package com.nabsys.nabeeplus.views.model;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.nabsys.nabeeplus.Activator;

public class NBLabelProvider extends LabelProvider{
	
	Display display = null;
	
	public NBLabelProvider(Display display)
	{
		this.display = display;
	}
	
	public Image getImage(Object element) {
		if(((Model)element).getImage() == null) {
			return Activator.getImageDescriptor("/icons/warning.gif").createImage(display);
		} else {
			return ((Model)element).getImage();
		}
	}
	
	public String getText(Object element) {
		if(((Model)element).getName() == null) {
			return "No title";
		} else {
			return ((Model)element).getName();
		}
	}
	
	public void dispose() {
		
	}
}
