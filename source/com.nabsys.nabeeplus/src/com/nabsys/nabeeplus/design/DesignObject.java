package com.nabsys.nabeeplus.design;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.nabsys.nabeeplus.editors.ServiceDesigner;
import com.nabsys.nabeeplus.editors.input.ServiceDesignerInput;
import com.nabsys.net.protocol.IPC.IPCProtocol;

public abstract class DesignObject extends Canvas{

	protected static int			NONE				= 0;
	protected static int			FOCUSED				= 1;
	protected static int			MOUSE_DOWN			= 1;
	protected static int			MOUSE_MOVE			= 2;
	protected static int			OVER				= 2;
	protected static int			TEST				= 3;
	protected int					STATE				= NONE;
	protected int					MOUSE_STATE			= NONE;
	protected int					MODE				= NONE;
	private boolean					isGroupSelected		= false;
	private ServiceDesigner 		editor				= null;
	protected ArrayList<ConnectorObject>		goingOutList		= null;
	protected ArrayList<ConnectorObject>		comingInList		= null;
	protected ArrayList<ConnectorObject>		goingOutInsideList	= null;
	protected ArrayList<ConnectorObject>		comingInInsideList	= null;
	protected IPCProtocol			protocol			= null;
	protected String				instanceID			= null;
	protected String				serviceID			= null;
	public DesignObject(Composite parent, int style) {
		super(parent, style);
	}
	
	public Rectangle getLinkPointRectangle(ConnectorObject connector)
	{
		return getBounds();
	}
	
	public Rectangle getLinkPointRectangle(LineConnectorObject line)
	{
		return getBounds();
	}
	
	public void setEditorSite(ServiceDesigner editor)
	{
		this.editor = editor;
		protocol = ((ServiceDesignerInput)editor.getEditorInput()).getProtocol();
		instanceID = ((ServiceDesignerInput)editor.getEditorInput()).getInstance();
		serviceID = ((ServiceDesignerInput)editor.getEditorInput()).getID();
	}
	
	public ServiceDesigner getEditorSite()
	{
		return editor;
	}
	
	public boolean isFocused()
	{
		return STATE == FOCUSED;
	}
	
	public boolean isGroupSelected()
	{
		return isGroupSelected;
	}
	
	public void setGroupSelected(boolean s)
	{
		this.isGroupSelected = s;
	}
	
	public void addGoingOut(ConnectorObject connect)
	{
		if(goingOutList == null) goingOutList = new ArrayList<ConnectorObject>();
		goingOutList.add(connect);
	}
	
	public void addComingIn(ConnectorObject connect)
	{
		if(comingInList == null) comingInList = new ArrayList<ConnectorObject>();
		comingInList.add(connect);
	}
	
	public void addGoingOutInside(ConnectorObject connect)
	{
		if(goingOutInsideList == null) goingOutInsideList = new ArrayList<ConnectorObject>();
		goingOutInsideList.add(connect);
	}
	
	public void addComingInInside(ConnectorObject connect)
	{
		if(comingInInsideList == null) comingInInsideList = new ArrayList<ConnectorObject>();
		comingInInsideList.add(connect);
	}
	
	public abstract void focusGained();
	public abstract void focusLost();
	public abstract void removeConnector(ConnectorObject obj);
}
