package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.BatchConfig;
import com.nabsys.resource.BatchTimeList;
import com.nabsys.resource.service.ServiceHandler;
import com.nabsys.resource.service.TimeScheduleHandler;

public class BatchObject extends GateObject{

	private BatchTimeList timelist = null;
	public BatchObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
	}
	
	public BatchObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (TimeScheduleHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		timelist = this.handler.getTimeList();
	}
	public void initHandler(){this.handler = null;}
	private TimeScheduleHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new TimeScheduleHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(timelist);
		return handler;
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		BatchConfig config = new BatchConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("TL", timelist);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			timelist = (BatchTimeList)map.get("TL");
		}
	}
}
