package com.nabsys.nabeeplus.design;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.resource.service.FileDeleteHandler;
import com.nabsys.resource.service.ServiceHandler;

public class FileDeleteObject extends IconObject{
	public FileDeleteObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		if(!chkParent(parent))
		{
			dispose();
		}
	}
	public FileDeleteObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (FileDeleteHandler) handler;
		this.handlerID = this.handler.getHandlerID();
	}
	public void initHandler(){this.handler = null;}
	private FileDeleteHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new FileDeleteHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		return handler;
	}
	private boolean chkParent(PanelObject parent)
	{
		if(parent instanceof FileFrameObject)
		{
			return true;
		}
		else
		{
			if(parent.getParent() == null) return false;
			if(!(parent.getParent() instanceof PanelObject)) return false;
			return chkParent((PanelObject)parent.getParent());
		}
	}
}
