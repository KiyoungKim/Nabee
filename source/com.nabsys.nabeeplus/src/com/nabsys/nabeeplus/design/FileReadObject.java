package com.nabsys.nabeeplus.design;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.FileReadConfig;
import com.nabsys.resource.service.FileReadHandler;
import com.nabsys.resource.service.ServiceHandler;

public class FileReadObject extends BlockObject{
	
	private LinkedHashMap<String, Object[]>	fileMap = null;
	private int readType = 0;
	private String countFieldID = "";
	public FileReadObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		
		if(!chkParent(parent))
		{
			dispose();
		}
	}
	public FileReadObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (FileReadHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		fileMap = this.handler.getFileMap();
		readType = this.handler.getReadType();
		countFieldID = this.handler.getCountFieldID();
		
	}
	public void initHandler(){this.handler = null;}
	private FileReadHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new FileReadHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(fileMap, readType, countFieldID);
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
		FileReadConfig config = new FileReadConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("FM", fileMap);
		map.put("RT", readType);
		map.put("CFI", countFieldID);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			fileMap = (LinkedHashMap<String, Object[]>)map.get("FM");
			readType = (Integer)map.get("RT");
			countFieldID = (String)map.get("CFI");
		}
	}
}
