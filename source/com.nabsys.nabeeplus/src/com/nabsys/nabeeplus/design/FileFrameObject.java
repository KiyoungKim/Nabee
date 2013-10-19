package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.FileFrameConfig;
import com.nabsys.resource.service.FileFrameHandler;
import com.nabsys.resource.service.ServiceHandler;

public class FileFrameObject extends BlockObject{
	
	private PanelObject			parent		= null;
	private String				fileNamePattern = "";
	public FileFrameObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		this.parent = parent;
	}
	public FileFrameObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
		this.handler = (FileFrameHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		fileNamePattern = this.handler.getFileNamePattern();
	}
	public void initHandler(){this.handler = null;}
	private FileFrameHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new FileFrameHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(fileNamePattern);
		return handler;
	}
	public FileFrameObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		FileFrameConfig config = new FileFrameConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("NP", fileNamePattern);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(parent.getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			fileNamePattern = (String)map.get("NP");
		}
	}

}
