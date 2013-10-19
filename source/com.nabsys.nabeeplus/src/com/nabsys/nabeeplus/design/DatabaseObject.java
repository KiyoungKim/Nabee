package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.DatabaseConfig;
import com.nabsys.resource.service.DatabaseHandler;
import com.nabsys.resource.service.ServiceHandler;

public class DatabaseObject extends BlockObject{

	private PanelObject			parent			= null;
	private String 				databaseName	= null;
	private boolean				isAutoCommit	= false;
	
	public DatabaseObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		this.parent = parent;
	}
	
	public DatabaseObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
		this.handler = (DatabaseHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		this.databaseName = this.handler.getDatabaseName();
		this.isAutoCommit = this.handler.isAutoCommit();
	}
	public void initHandler(){this.handler = null;}
	private DatabaseHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new DatabaseHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(databaseName, isAutoCommit);
		
		return handler;
	}
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		DatabaseConfig config = new DatabaseConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DN", databaseName);
		map.put("AC", isAutoCommit);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(parent.getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			databaseName = (String)map.get("DN");
			isAutoCommit = (Boolean)map.get("AC");
		}
	}
}
