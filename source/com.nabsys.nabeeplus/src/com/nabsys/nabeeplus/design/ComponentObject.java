package com.nabsys.nabeeplus.design;

import java.util.HashMap;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.nabsys.nabeeplus.design.window.ComponentConfig;
import com.nabsys.resource.service.ComponentArgumentContext;
import com.nabsys.resource.service.ComponentHandler;
import com.nabsys.resource.service.ServiceHandler;

public class ComponentObject extends IconObject{
	
	private PanelObject					parent					= null;
	private String 						componentName 			= null;
	private String						className				= null;
	private ComponentArgumentContext 	constructArguments 		= null;
	private String 						methodName 				= null;
	private ComponentArgumentContext 	methodArguments 		= null;
	private boolean						isAlwaysNew				= true;
	private boolean 					isSetReturnValue 		= false;
	private String 						returnMapKey 			= null;
	public ComponentObject(PanelObject parent, String srcIcon, String name, Point point) {
		super(parent, srcIcon, name, point);
		this.parent = parent;
	}
	public ComponentObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle, ServiceHandler handler) {
		super(parent, srcIcon, name, rectangle);
		this.parent = parent;
		this.handler = (ComponentHandler) handler;
		this.handlerID 			= this.handler.getHandlerID();
		componentName			= this.handler.getComponentName();
		className				= this.handler.getClassName();
		constructArguments 		= this.handler.getConstructArguments();
		methodName 				= this.handler.getMethodName();
		methodArguments 		= this.handler.getMethodArguments();
		isAlwaysNew				= this.handler.isAlwaysNew();
		isSetReturnValue 		= this.handler.isSetReturnValue();
		returnMapKey 			= this.handler.getReturnMapKey();
	}
	public void initHandler(){this.handler = null;}
	private ComponentHandler		handler		= null;
	public ServiceHandler getHandler(ServiceHandler parent)
	{
		if(handler == null) handler = new ComponentHandler(parent, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
		this.handlerID = this.handler.getHandlerID();
		handler.setData(componentName, className, constructArguments, methodName, methodArguments, null, isAlwaysNew, isSetReturnValue, returnMapKey);
		return handler;
	}
	
	public void mouseDoubleClick(MouseEvent e) {
		super.mouseDoubleClick(e);
		ComponentConfig config = new ComponentConfig(getShell());
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("CPN", componentName);
		map.put("CN", className);
		map.put("CA", constructArguments);
		map.put("MN", methodName);
		map.put("MA", methodArguments);
		map.put("IAN", isAlwaysNew);
		map.put("ISR", isSetReturnValue);
		map.put("RMK", returnMapKey);
		config.initMap(map);
		config.setProtocol(protocol);
		config.setInstanceID(instanceID);
		config.open(parent.getWindow(), getIconImage());
		if(((String)map.get("RTN")).equals("OK"))
		{
			componentName 			= (String)map.get("CPN");
			className				= (String)map.get("CN");
			constructArguments 		= (ComponentArgumentContext)map.get("CA");
			methodName 				= (String)map.get("MN");
			methodArguments 		= (ComponentArgumentContext)map.get("MA");
			isAlwaysNew				= (Boolean)map.get("IAN");
			isSetReturnValue 		= (Boolean)map.get("ISR");
			returnMapKey 			= (String)map.get("RMK");
		}
		
	}

}
