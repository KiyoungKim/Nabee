package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

import com.nabsys.nabeeplus.design.window.ExceptionConfig;
import com.nabsys.resource.service.ExceptionHandler;
import com.nabsys.resource.service.ServiceHandler;

public class ExceptionObject extends IconObject{
	private PanelObject parent = null;
	private HashMap<String, Object> mapping = null;
	public ExceptionObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		this.parent = parent;
		if(parent instanceof BlockObject)
		{
			dispose();
			return;
		}
		
		Control[] controls = parent.getChildren();
		for(int i=0; i<controls.length; i++)
		{
			if(controls[i] instanceof ExceptionObject && controls[i] != this)
			{
				dispose();
				return;
			}
		}
	}
	public ExceptionObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
		this.handler = (ExceptionHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		mapping = this.handler.getMappingData();
	}
	public void initHandler(){this.handler = null;}
	private ExceptionHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new ExceptionHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(mapping);
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
	@SuppressWarnings("unchecked")
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		ExceptionConfig config = new ExceptionConfig(getShell());
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
