package com.nabsys.nabeeplus.design;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

import com.nabsys.resource.service.ServiceHandler;
import com.nabsys.resource.service.TerminateHandler;

public class TerminateObject extends IconObject{

	public TerminateObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		if(parent instanceof BlockObject)
		{
			dispose();
			return;
		}
		Control[] controls = parent.getChildren();
		for(int i=0; i<controls.length; i++)
		{
			if(controls[i] instanceof TerminateObject && controls[i] != this)
			{
				dispose();
				return;
			}
		}
	}
	public TerminateObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (TerminateHandler) handler;
		this.handlerID = this.handler.getHandlerID();
	}
	public void initHandler(){this.handler = null;}
	private TerminateHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new TerminateHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		return handler;
	}
	public void mouseDown(MouseEvent e) {
		if(MODE == Mode.RELATION_MODE)
		{
			return;
		}

		super.mouseDown(e);
	}
}
