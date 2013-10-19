package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.ServiceCallConfig;
import com.nabsys.resource.service.ServiceCallHandler;
import com.nabsys.resource.service.ServiceHandler;

public class ServiceCallerObject extends IconObject{
	
	private String callServiceID = "";
	private String telegramID = "";

	public ServiceCallerObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
	}
	public ServiceCallerObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (ServiceCallHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		callServiceID = this.handler.getServiceID();
		telegramID = this.handler.getTelegramID();
	}
	public void initHandler(){this.handler = null;}
	private ServiceCallHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new ServiceCallHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		this.handler.setData(callServiceID, telegramID);
		return handler;
	}
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		ServiceCallConfig config = new ServiceCallConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("TI", telegramID);
		map.put("SI", callServiceID);
		map.put("CALLER", getEditorSite().getTitle());
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			callServiceID = (String)map.get("SI");
			telegramID = (String)map.get("TI");
		}
	}
}
