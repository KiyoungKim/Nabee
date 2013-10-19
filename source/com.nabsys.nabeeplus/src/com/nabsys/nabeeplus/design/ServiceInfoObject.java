package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.ServiceConfig;
import com.nabsys.resource.service.BatchServiceHandler;
import com.nabsys.resource.service.GateServiceHandler;
import com.nabsys.resource.service.GeneralServiceHandler;
import com.nabsys.resource.service.MessageQueueServiceHandler;
import com.nabsys.resource.service.OnlineServiceHandler;
import com.nabsys.resource.service.ServiceHandler;

public class ServiceInfoObject extends IconObject {
	private final int ONLINE			= 1;
	private final int MESSAGE_QUEUE 	= 2;
	private final int BATCH 			= 3;
	private final int GENERAL			= 4;
	private int TYPE 					= GENERAL;
	private String serviceID			= null;
	private String serviceName			= null;
	private String remark				= null;
	private boolean activate			= false;
	
	public ServiceInfoObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
	}
	public ServiceInfoObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (GateServiceHandler) handler;
		this.handlerID = this.handler.getHandlerID();
	}
	public void initHandler() {handler = null;}
	private GateServiceHandler handler = null;
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		ServiceConfig config = new ServiceConfig(getShell());
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("ID", serviceID==null?"":serviceID);
		returnMap.put("NAME", serviceName==null?"":serviceName);
		returnMap.put("REMARK", remark==null?"":remark);
		returnMap.put("ACTIVATE", activate);
		switch(TYPE){
		case GENERAL:
			returnMap.put("TYPE", "General");
			break;
		case ONLINE:
			returnMap.put("TYPE", "Online");
			break;
		case BATCH:
			returnMap.put("TYPE", "Batch");
			break;
		case MESSAGE_QUEUE:
			returnMap.put("TYPE", "Message Queue");
			break;
		}
		config.initMap(returnMap);
		returnMap = config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		if(((String)returnMap.get("RTN")).equals("OK"))
		{
			TYPE = returnMap.containsKey("TYPE")?(Integer)returnMap.get("TYPE"):TYPE;
			serviceID = returnMap.containsKey("ID")?(String)returnMap.get("ID"):serviceID;
			serviceName = returnMap.containsKey("NAME")?(String)returnMap.get("NAME"):serviceName;
			remark = returnMap.containsKey("REMARK")?(String)returnMap.get("REMARK"):remark;
			activate = returnMap.containsKey("ACTIVATE")?(Boolean)returnMap.get("ACTIVATE"):activate;
			getEditorSite().changeTitle(serviceID);

			getEditorSite().setDirty();

			int loop = 0;
			switch(TYPE){
			case GENERAL:
				loop = goingOutList.size();
				for(int i=0; i<loop; i++)
				{
					ConnectorObject co = goingOutList.get(i);
					DesignObject obj = co.getEndObject();
					if(obj != null && (obj instanceof InboundNetworkObject ||
							obj instanceof BatchObject ||
							obj instanceof MessageQueueObject))
					{
						((IconObject)obj).focusGained();
						((PanelObject) getParent()).notifyDelete();
					}
				}
				break;
			case ONLINE:
				loop = goingOutList.size();
				for(int i=0; i<loop; i++)
				{
					ConnectorObject co = goingOutList.get(i);
					DesignObject obj = co.getEndObject();
					if(obj != null && !(obj instanceof InboundNetworkObject))
					{
						obj.comingInList.remove(co);
						goingOutList.remove(co);
						co.dispose();
					}
				}
				break;
			case BATCH:
				loop = goingOutList.size();
				for(int i=0; i<loop; i++)
				{
					ConnectorObject co = goingOutList.get(i);
					DesignObject obj = co.getEndObject();
					if(obj != null && !(obj instanceof BatchObject))
					{
						obj.comingInList.remove(co);
						goingOutList.remove(co);
						co.dispose();
					}
				}
				break;
			case MESSAGE_QUEUE:
				loop = goingOutList.size();
				for(int i=0; i<loop; i++)
				{
					ConnectorObject co = goingOutList.get(i);
					DesignObject obj = co.getEndObject();
					if(obj != null && !(obj instanceof MessageQueueObject))
					{
						obj.comingInList.remove(co);
						goingOutList.remove(co);
						co.dispose();
					}
				}
				break;
			}
		}
	}

	public ServiceHandler getHandler(ServiceHandler parent)
	{
		switch(TYPE){
		case ONLINE :
			if(handler == null) handler = new OnlineServiceHandler(null, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
			break;
		case MESSAGE_QUEUE :
			if(handler == null) handler = new MessageQueueServiceHandler(null, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
			break;
		case BATCH :
			if(handler == null) handler = new BatchServiceHandler(null, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
			break;
		case GENERAL :
			if(handler == null) handler = new GeneralServiceHandler(null, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
			break;
		}
		this.handlerID = this.handler.getHandlerID();
		return handler;
	}
	public String getServiceID()
	{
		return serviceID;
	}
	public String getServiceName()
	{
		return serviceName;
	}
	public String getType()
	{
		String type = "";
		switch(TYPE){
		case ONLINE :
			type = "Online";
			break;
		case MESSAGE_QUEUE :
			type = "MessageQueue";
			break;
		case BATCH :
			type = "Batch";
			break;
		case GENERAL :
			type = "General";
			break;
		}
		return type;
	}
	public boolean isActivate()
	{
		return activate;
	}
	public String getRemark()
	{
		return remark;
	}
	public void setServiceID(String id)
	{
		this.serviceID = id;
	}
	public void setServiceName(String name)
	{
		this.serviceName = name;
	}
	public void setType(String type)
	{
		if(type.equals("Online")) 			this.TYPE = ONLINE;
		if(type.equals("MessageQueue")) 	this.TYPE = MESSAGE_QUEUE;
		if(type.equals("Batch")) 			this.TYPE = BATCH;
		if(type.equals("General")) 			this.TYPE = GENERAL;
	}
	public void setActivate(boolean activate)
	{
		this.activate = activate;
	}
	public void setRemark(String remark)
	{
		this.remark = remark;
	}
	public void groupPreDelete()
	{
		return;
	}
	public boolean groupDelete()
	{
		return false;
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
}
