package com.nabsys.nabeeplus.design;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.resource.service.ServiceHandler;
import com.nabsys.resource.service.ThreadHandler;

public class ThreadObject extends BlockObject{

	public ThreadObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
	}

	public ThreadObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (ThreadHandler) handler;
		this.handlerID = this.handler.getHandlerID();
	}
	public void initHandler(){this.handler = null;}
	private ThreadHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new ThreadHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		return handler;
	}
}
