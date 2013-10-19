package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.nabsys.nabeeplus.design.window.NetworkWriteConfig;
import com.nabsys.resource.service.NetworkWriteHandler;
import com.nabsys.resource.service.ServiceHandler;

public class NetworkWriteObject extends IconObject{
	private String telegramID = "";
	public NetworkWriteObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		
		if(!chkParent(parent))
		{
			dispose();
		}
	}
	public NetworkWriteObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (NetworkWriteHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		this.telegramID = this.handler.getTelegramID();
	}
	public void initHandler(){this.handler = null;}
	private NetworkWriteHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new NetworkWriteHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(telegramID);
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
		DesignObject tmp = (DesignObject)getParent();
		PanelObject panel = null;

		while(true)
		{
			Composite parentObj = tmp.getParent();
			if(tmp instanceof PanelObject && !(tmp instanceof BlockObject))
			{
				panel = (PanelObject)tmp;
				break;
			}
			if(!(parentObj instanceof DesignObject) || parentObj == null) break;
			tmp = (DesignObject)parentObj;
		}
		
		ServiceInfoObject si = findServiceGate(panel);
		if(si != null && si.getType().equals("MessageQueue"))
		{
			return;
		}
		else
		{
			NetworkWriteConfig config = new NetworkWriteConfig(getShell());
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("TI", telegramID);
			config.initMap(map);
			config.setProtocol(protocol);
			config.setInstanceID(instanceID);
			config.open(((PanelObject) getParent()).getWindow(), getIconImage());
			if(((String)map.get("RTN")).equals("OK"))
			{
				telegramID = (String)map.get("TI");
			}
		}
		
	}
	
	private ServiceInfoObject findServiceGate(PanelObject panel)
	{
		Control[] children = panel.getChildren();
		
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof ServiceInfoObject)
			{
				return (ServiceInfoObject)children[i];
			}
		}
		return null;
	}
}
