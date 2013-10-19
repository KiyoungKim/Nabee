package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.ThrowConfig;
import com.nabsys.resource.service.ServiceHandler;
import com.nabsys.resource.service.ThrowHandler;

public class ThrowObject extends IconObject{
	private PanelObject parent = null;
	private String message = "";
	public ThrowObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		this.parent = parent;
	}
	public ThrowObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
		this.handler = (ThrowHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		message = this.handler.getMessage();
	}
	public void initHandler(){this.handler = null;}
	private ThrowHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new ThrowHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(message);
		return handler;
	}
	public void mouseDown(MouseEvent e) {
		if(MODE == Mode.RELATION_MODE)
		{
			if(goingOutList != null && goingOutList.size() > 0) return;
		}
		else if(MODE == Mode.END_RELATION_MODE)
		{
			return;
		}
		super.mouseDown(e);
	}
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		ThrowConfig config = new ThrowConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("MSG", message);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(parent.getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			message = (String)map.get("MSG");
		}
	}
}
