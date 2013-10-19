package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.AssignConfig;
import com.nabsys.resource.service.AssignHandler;
import com.nabsys.resource.service.ServiceHandler;

public class AssignObject  extends IconObject{
	private PanelObject parent = null;
	private HashMap<String, Object> mapping = null;
	
	public AssignObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		this.parent = parent;
	}
	public AssignObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
		this.handler = (AssignHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		mapping = this.handler.getMappingData();
	}
	public void initHandler(){this.handler = null;}
	private AssignHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new AssignHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(mapping);
		return handler;
	}

	@SuppressWarnings("unchecked")
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		AssignConfig config = new AssignConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("MD", mapping);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(parent.getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			mapping = (HashMap<String, Object>)map.get("MD");
		}
	}
}
