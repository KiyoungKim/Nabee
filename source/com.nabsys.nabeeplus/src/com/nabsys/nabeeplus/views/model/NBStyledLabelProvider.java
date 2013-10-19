package com.nabsys.nabeeplus.views.model;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.nabsys.nabeeplus.Activator;

public class NBStyledLabelProvider extends StyledCellLabelProvider {

	Display display = null;
	
	public NBStyledLabelProvider(Display display)
	{
		this.display = display;
	}
	
	public void update(ViewerCell cell) 
	{
		Object element= cell.getElement();
		
		if(element instanceof SearchContents)
		{
			cell.setImage(((Model)element).getImage());
			cell.setText(((Model)element).getName());
			cell.setStyleRanges(((SearchContents)element).getStyleRange());
		}
		else if(element instanceof Model)
		{
			cell.setImage(((Model)element).getImage());
			cell.setText(((Model)element).getName());
		}
		else
		{
			cell.setImage(Activator.getImageDescriptor("/icons/warning.gif").createImage(display));
			cell.setText("No title");
		}
		
		super.update(cell);
	}
	
	protected void measure(Event event, Object element) {
		super.measure(event, element);
	}
}
