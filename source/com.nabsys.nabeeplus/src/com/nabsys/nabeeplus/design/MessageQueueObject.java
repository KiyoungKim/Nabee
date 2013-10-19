package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.MessageQueueConfig;
import com.nabsys.resource.service.MessageQueueHandler;
import com.nabsys.resource.service.ServiceHandler;

public class MessageQueueObject extends GateObject{
	private int queueSize = 0;
	public MessageQueueObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
	}
	public MessageQueueObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (MessageQueueHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		queueSize = this.handler.getQueueSize();
	}
	public void initHandler(){this.handler = null;}
	private MessageQueueHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new MessageQueueHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(queueSize);
		return handler;
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		MessageQueueConfig config = new MessageQueueConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("QS", queueSize);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			queueSize = (Integer)map.get("QS");
		}
	}
}
