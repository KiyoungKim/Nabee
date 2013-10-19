package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.DatabaseProcedureConfig;
import com.nabsys.resource.service.DatabaseProcedureHandler;
import com.nabsys.resource.service.ProcedureArgumentContext;
import com.nabsys.resource.service.ServiceHandler;

public class DatabaseProcedureObject extends IconObject{
	
	private PanelObject					parent				= null;
	private String						procedureName		= null;
	private ProcedureArgumentContext	parameter			= null;
	private boolean						isLogging			= false;
	public DatabaseProcedureObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		if(!chkParent(parent))
		{
			dispose();
		}
		this.parent = parent;
	}
	public DatabaseProcedureObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
		this.handler = (DatabaseProcedureHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		this.procedureName = this.handler.getProcedureName();
		this.parameter = this.handler.getParameter();
		this.isLogging = this.handler.isLogging();
	}
	public void initHandler(){this.handler = null;}
	private DatabaseProcedureHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new DatabaseProcedureHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(procedureName == null?"":procedureName, parameter, isLogging);
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
		DatabaseProcedureConfig config = new DatabaseProcedureConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("PN", procedureName);
		map.put("PRM", parameter);
		map.put("IL", isLogging);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(parent.getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			procedureName = (String)map.get("PN");
			parameter = (ProcedureArgumentContext)map.get("PRM");
			isLogging = (Boolean)map.get("IL");
		}
	}
}
