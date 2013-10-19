package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.LoopConfig;
import com.nabsys.resource.service.LoopHandler;
import com.nabsys.resource.service.ServiceHandler;

public class LoopObject extends BlockObject{
	
	private PanelObject			parent		= null;
	private String				listKey		= "";
	
	public LoopObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		this.parent = parent;
	}
	public LoopObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
		this.handler = (LoopHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		listKey = this.handler.getListKey();
	}
	public void initHandler(){this.handler = null;}
	private LoopHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new LoopHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(listKey);
		return handler;
	}
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		LoopConfig config = new LoopConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("MK", listKey);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(parent.getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			listKey = (String)map.get("MK");
		}
	}

}
