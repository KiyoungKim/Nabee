package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.InboundNetworkConfig;
import com.nabsys.resource.service.InboundHandler;
import com.nabsys.resource.service.ServiceHandler;

public class InboundNetworkObject extends GateObject{
	
	private String protocolID = null;
	private String telegramID = null;
	public InboundNetworkObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
	}
	public InboundNetworkObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (InboundHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		
		protocolID = this.handler.getProtocolID();
		telegramID = this.handler.getTelegramID();
	}
	public void initHandler(){this.handler = null;}
	private InboundHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null)
		{
			handler = new InboundHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		}
		this.handlerID = this.handler.getHandlerID();
		handler.setData(protocolID, telegramID);
		return handler;
	}

	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		InboundNetworkConfig config = new InboundNetworkConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("PI", protocolID);
		map.put("TI", telegramID);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		
		map = config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		
		if(((String)map.get("RTN")).equals("OK"))
		{
			protocolID = (String)map.get("PI");
			telegramID = (String)map.get("TI");
		}
	}
}
