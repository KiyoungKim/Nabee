package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.DatabaseUpdateConfig;
import com.nabsys.resource.service.DatabaseUpdateHandler;
import com.nabsys.resource.service.ServiceHandler;

public class DatabaseUpdateObject extends IconObject{
	
	private PanelObject			parent				= null;
	private String				sqlURL				= null;
	private boolean				isLogging			= false;
	private boolean 			isSetReturnValue 	= false;
	private String 				returnMapKey 		= null;
	public DatabaseUpdateObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		if(!chkParent(parent))
		{
			dispose();
		}
		this.parent = parent;
	}
	public DatabaseUpdateObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
		this.handler = (DatabaseUpdateHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		sqlURL = this.handler.getSqlURL();
		isLogging = this.handler.isLogging();
		isSetReturnValue = this.handler.isSetReturnValue();
		returnMapKey = this.handler.getReturnMapKey();
	}
	public void initHandler(){this.handler = null;}
	private DatabaseUpdateHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new DatabaseUpdateHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(sqlURL, isLogging, isSetReturnValue, returnMapKey);
		return handler;
	}
	private boolean chkParent(PanelObject parent)
	{
		if(parent instanceof DatabaseObject)
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
	
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		DatabaseUpdateConfig config = new DatabaseUpdateConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("SU", sqlURL);
		map.put("IL", isLogging);
		map.put("ISR", isSetReturnValue);
		map.put("RMK", returnMapKey);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(parent.getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			sqlURL = (String)map.get("SU");
			isLogging = (Boolean)map.get("IL");
			isSetReturnValue = (Boolean)map.get("ISR");
			returnMapKey = (String)map.get("RMK");
		}
	}
}
