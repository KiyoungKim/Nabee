package com.nabsys.nabeeplus.design;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.FileWriteConfig;
import com.nabsys.resource.service.FileWriteHandler;
import com.nabsys.resource.service.ServiceHandler;

public class FileWriteObject extends IconObject{
	
	private LinkedHashMap<String, Object[]>	fileMap = null;
	public FileWriteObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		
		if(!chkParent(parent))
		{
			dispose();
		}
	}
	public FileWriteObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (FileWriteHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		fileMap = this.handler.getFileMap();
	}
	public void initHandler(){this.handler = null;}
	private FileWriteHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new FileWriteHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(fileMap);
		return handler;
	}
	private boolean chkParent(PanelObject parent)
	{
		if(parent instanceof FileFrameObject)
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
	
	@SuppressWarnings("unchecked")
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		FileWriteConfig config = new FileWriteConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("FM", fileMap);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			fileMap = (LinkedHashMap<String, Object[]>)map.get("FM");
		}
	}
}
