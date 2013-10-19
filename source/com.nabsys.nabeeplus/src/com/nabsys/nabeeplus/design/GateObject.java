package com.nabsys.nabeeplus.design;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public abstract class GateObject extends IconObject {

	public GateObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		if(parent instanceof BlockObject)
		{
			dispose();
			return;
		}
		Control[] controls = parent.getChildren();
		for(int i=0; i<controls.length; i++)
		{
			if(controls[i] instanceof GateObject && controls[i] != this)
			{
				dispose();
				return;
			}
		}
	}
	public GateObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle) {
		super(parent, srcIcon, name, rectangle);
		if(parent instanceof BlockObject)
		{
			dispose();
			return;
		}
		Control[] controls = parent.getChildren();
		for(int i=0; i<controls.length; i++)
		{
			if(controls[i] instanceof GateObject && controls[i] != this)
			{
				dispose();
				return;
			}
		}
	}
}
