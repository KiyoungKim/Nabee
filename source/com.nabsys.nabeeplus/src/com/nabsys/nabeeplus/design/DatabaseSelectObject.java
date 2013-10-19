package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.DatabaseSelectConfig;
import com.nabsys.resource.service.DatabaseSelectHandler;
import com.nabsys.resource.service.ServiceHandler;

public class DatabaseSelectObject extends BlockObject{

	private PanelObject			parent				= null;
	private String				sqlURL				= null;
	private String				listName			= null;
	private boolean				isLogging			= false;
	private boolean				isOneRow			= false;
	private boolean 			isSetReturnValue 	= false;
	private String 				returnMapKey 		= null;
	public DatabaseSelectObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		
		if(!chkParent(parent))
		{
			dispose();
		}
		this.parent = parent;
	}
	public DatabaseSelectObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		if(!chkParent(parent))
		{
			dispose();
		}
		this.parent = parent;
		this.handler = (DatabaseSelectHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		sqlURL = this.handler.getSqlURL();
		listName = this.handler.getListName();
		isLogging = this.handler.isLogging();
		isOneRow = this.handler.isOneRow();
		isSetReturnValue = this.handler.isSetReturnValue();
		returnMapKey = this.handler.getReturnMapKey();
	}
	public void initHandler(){this.handler = null;}
	private DatabaseSelectHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new DatabaseSelectHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(sqlURL, listName, isLogging, isOneRow, isSetReturnValue, returnMapKey);
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
		DatabaseSelectConfig config = new DatabaseSelectConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("SU", sqlURL);
		map.put("LN", listName);
		map.put("IL", isLogging);
		map.put("CHL", isOneRow);
		map.put("ISR", isSetReturnValue);
		map.put("RMK", returnMapKey);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(parent.getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			sqlURL = (String)map.get("SU");
			listName = (String)map.get("LN");
			isLogging = (Boolean)map.get("IL");
			isOneRow = (Boolean)map.get("CHL");
			isSetReturnValue = (Boolean)map.get("ISR");
			returnMapKey = (String)map.get("RMK");
		}
		
	}

}
