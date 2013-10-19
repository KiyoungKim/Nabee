package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

import com.nabsys.nabeeplus.design.window.OutboundNetworkConfig;
import com.nabsys.resource.service.OutboundHandler;
import com.nabsys.resource.service.ServiceHandler;

public class OutboundNetworkObject extends IconObject{
	private String telegramID = null;
	public OutboundNetworkObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		if(parent instanceof BlockObject)
		{
			dispose();
			return;
		}
		
		Control[] controls = parent.getChildren();
		for(int i=0; i<controls.length; i++)
		{
			if(controls[i] instanceof OutboundNetworkObject && controls[i] != this)
			{
				dispose();
				return;
			}
		}
	}
	public OutboundNetworkObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (OutboundHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		telegramID = this.handler.getTelegramID();
	}
	public void initHandler(){this.handler = null;}
	private OutboundHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new OutboundHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(telegramID);
		return handler;
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		OutboundNetworkConfig config = new OutboundNetworkConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("TI", telegramID);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		
		map = config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		
		if(((String)map.get("RTN")).equals("OK"))
		{
			telegramID = (String)map.get("TI");
		}
	}
}
