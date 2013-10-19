package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.NetworkReadConfig;
import com.nabsys.resource.service.NetworkReadHandler;
import com.nabsys.resource.service.ServiceHandler;

public class NetworkReadObject extends IconObject{
	private String telegramID = "";
	private long timeout = 0L;
	public NetworkReadObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		
		if(!chkParent(parent))
		{
			dispose();
		}
	}
	public NetworkReadObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (NetworkReadHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		this.telegramID = this.handler.getTelegramID();
		this.timeout = this.handler.getTimeout();
	}
	public void initHandler(){this.handler = null;}
	private NetworkReadHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new NetworkReadHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(telegramID, timeout);
		return handler;
	}
	private boolean chkParent(PanelObject parent)
	{
		if(parent instanceof ClientNetworkObject)
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
		NetworkReadConfig config = new NetworkReadConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("TI", telegramID);
		map.put("TO", timeout);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			telegramID = (String)map.get("TI");
			timeout = (Long)map.get("TO");
		}
	}
}
