package com.nabsys.nabeeplus.design;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.resource.service.CloseNetworkHandler;
import com.nabsys.resource.service.ServiceHandler;

public class CloseNetworkObject extends IconObject{

	public CloseNetworkObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		if(parent instanceof BlockObject)
		{
			dispose();
			return;
		}
	}
	public CloseNetworkObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handlerID = this.handler.getHandlerID();
		this.handler = (CloseNetworkHandler) handler;
	}
	public void initHandler(){this.handler = null;}
	private CloseNetworkHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new CloseNetworkHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		return handler;
	}
}
