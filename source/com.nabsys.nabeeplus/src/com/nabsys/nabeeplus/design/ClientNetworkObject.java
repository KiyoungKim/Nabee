package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.ClientNetworkConfig;
import com.nabsys.resource.service.ClientNetworkHandler;
import com.nabsys.resource.service.ServiceHandler;

public class ClientNetworkObject extends BlockObject{
	
	private boolean useConnectionPool = false;
	private String connectionPoolName = "";
	private String protocolID = "";
	private String addr = "";
	private int port = 0;
	private String encoding = "";
	private String idFieldID = "";
	private String lengthFieldID = "";
	private int lengthFieldOffset = 0;
	private int lengthFieldLength = 0;
	private int lengthFieldAdjustment = 0;
	private int maxBufferSize = 0;
	
	public ClientNetworkObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
	}
	public ClientNetworkObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.handler = (ClientNetworkHandler) handler;
		this.handlerID = this.handler.getHandlerID();
		useConnectionPool = this.handler.isUseConnectionPool();
		connectionPoolName = this.handler.getConnectionPoolName();
		protocolID = this.handler.getProtocolID();
		addr = this.handler.getAddr();
		port = this.handler.getPort();
		encoding = this.handler.getEncoding();
		idFieldID = this.handler.getIdFieldID();
		lengthFieldID = this.handler.getLengthFieldID();
		lengthFieldOffset = this.handler.getLengthFieldOffset();
		lengthFieldLength = this.handler.getLengthFieldLength();
		lengthFieldAdjustment = this.handler.getLengthFieldAdjustment();
		maxBufferSize = this.handler.getMaxBufferSize();
	}
	public void initHandler(){this.handler = null;}
	private ClientNetworkHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new ClientNetworkHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(useConnectionPool, connectionPoolName, protocolID, addr, port, encoding, idFieldID, lengthFieldID, lengthFieldOffset, lengthFieldLength, lengthFieldAdjustment, maxBufferSize);
		return handler;
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		ClientNetworkConfig config = new ClientNetworkConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("UCP", useConnectionPool);
		map.put("CPN", connectionPoolName);
		map.put("PI", protocolID);
		map.put("AD", addr);
		map.put("PT", port);
		map.put("SED", encoding);
		map.put("IFI", idFieldID);
		map.put("LFI", lengthFieldID);
		map.put("LFO", lengthFieldOffset);
		map.put("LFL", lengthFieldLength);
		map.put("LFA", lengthFieldAdjustment);
		map.put("MB", maxBufferSize);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(((PanelObject) getParent()).getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			useConnectionPool = (Boolean)map.get("UCP");
			connectionPoolName = (String)map.get("CPN");
			protocolID = (String)map.get("PI");
			addr = (String)map.get("AD");
			port = (Integer)map.get("PT");
			encoding = (String)map.get("SED");
			idFieldID = (String)map.get("IFI");
			lengthFieldID = (String)map.get("LFI");
			lengthFieldOffset = (Integer)map.get("LFO");
			lengthFieldLength = (Integer)map.get("LFL");
			lengthFieldAdjustment = (Integer)map.get("LFA");
			maxBufferSize = (Integer)map.get("MB");
		}
	}
}
