package com.nabsys.nabeeplus.design;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;

import com.nabsys.nabeeplus.Activator;

public class PanelObject extends DesignObject implements MouseListener{

	private ArrayList<GroupEventListener> 	groupEventListener 	= new ArrayList<GroupEventListener>();
	private Composite 						parent 				= null;
	private RangeObject						rangeObj 			= null;
	protected int							MODE				= Mode.NONE;
	private Rectangle						clientBoundary		= null;
	private ConnectorObject					curConnect			= null;
	private LineConnectorObject				curLine				= null;
	protected IWorkbenchWindow 				window				= null;
	
	
	public PanelObject(Composite parent, IWorkbenchWindow window) {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.parent = parent;
		this.window = window;
		addMouseListener(this);
	}
	
	public IWorkbenchWindow getWindow()
	{
		return this.window;
	}
	
	protected void setClientBoundary(Rectangle area)
	{
		clientBoundary = area;
	}
	
	public void setCurrentConnect(ConnectorObject con)
	{
		this.curConnect = con;
	}
	
	protected ConnectorObject getCurrentConnect()
	{
		return this.curConnect;
	}
	
	public Rectangle getClientBoundary()
	{
		if(this.clientBoundary == null)
		{
			Rectangle rect = getBounds();
			rect.x = 0;
			rect.y = 0;
			return rect;
		}
		return this.clientBoundary;
	}
	
	public void dispose()
	{
		removeMouseListener(this);
		this.groupEventListener.clear();
		Control[] children = getChildren();
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof DesignObject) ((DesignObject)children[i]).dispose();
		}
		
		super.dispose();
	}

	protected void addGroupEvent(GroupEventListener groupEventListener)
	{
		this.groupEventListener.add(groupEventListener);
	}
	
	protected void removeGroupEvent(GroupEventListener groupEventListener)
	{
		this.groupEventListener.remove(groupEventListener);
	}
	
	public void notifyMouseDown(DesignObject obj)
	{
		forceFocus();
		for(int i=0; i<groupEventListener.size(); i++) groupEventListener.get(i).groupMouseDown(obj);
		if(parent instanceof PanelObject)
		{
			((PanelObject)parent).notifyMouseDown(obj);
		}
	}
	
	public void notifyGroupMouseUp()
	{
		for(int i=0; i<groupEventListener.size(); i++) groupEventListener.get(i).groupMouseUp();
		if(parent instanceof PanelObject)
		{
			((PanelObject)parent).notifyGroupMouseUp();
		}
	}

	protected void notifyGroupSelect(final Rectangle r)
	{
		for(int i=0; i<groupEventListener.size(); i++) groupEventListener.get(i).groupSelect(r);
	}
	
	public void notifyGroupMove(Point p)
	{
		for(int i=0; i<groupEventListener.size(); i++)
		{
			groupEventListener.get(i).groupMove(p);
		}
	}
	
	public void notifyRelationMode(ImageData cursor)
	{
		if(curConnect != null && curConnect.getStartObject() != null)
		{
			curConnect.getStartObject().removeConnector(curConnect);
			curConnect.dispose();
			curConnect = null;
		}
		for(int i=0; i<groupEventListener.size(); i++) groupEventListener.get(i).relationMode(Activator.getImageDescriptor("/icons/cross.png").getImageData());
	}
	
	public void notifyEndRelationMode()
	{
		for(int i=0; i<groupEventListener.size(); i++) groupEventListener.get(i).endRelationMode();
		if(parent instanceof PanelObject)
		{
			((PanelObject)parent).notifyEndRelationMode();
		}
	}
	
	public void notifyNewObjectMode(int type, ImageData cursor)
	{
		MODE = type;
		if(curConnect != null && curConnect.getStartObject() != null)
		{
			curConnect.getStartObject().removeConnector(curConnect);
			curConnect.dispose();
			curConnect = null;
		}
		disposeRelationLine();
		for(int i=0; i<groupEventListener.size(); i++) groupEventListener.get(i).newObjectMode(type, cursor);
	}
	
	public void notifyFreeMode()
	{
		setCursor(new Cursor(getDisplay(), SWT.CURSOR_ARROW));
		MODE = Mode.NONE;
		for(int i=0; i<groupEventListener.size(); i++) groupEventListener.get(i).freeMode();
		if(parent instanceof PanelObject)
		{
			((PanelObject)parent).notifyFreeMode();
		}
	}
	
	public void notifyDelete()
	{
		int length = groupEventListener.size();
		for(int i=0; i<length; i++)
		{
			groupEventListener.get(i).groupPreDelete();
		}
		
		int idx = 0;
		for(int i=0; i<length; i++)
		{
			if(!groupEventListener.get(idx).groupDelete()) idx++;
		}
	}

	public void mouseDoubleClick(MouseEvent e) {
	}
	
	public void genRelationLine(DesignObject startObj)
	{
		if(curLine == null)
			curLine = new LineConnectorObject(this, startObj);
	}
	
	public void setEndObjectToLine(DesignObject obj)
	{
		if(this.curLine != null) this.curLine.setEndObject(obj);
	}
	
	public void breakeEndObjectOfLine()
	{
		if(this.curLine != null) this.curLine.breakeEndObject();
	}
	
	public void disposeRelationLine()
	{
		if(curLine != null) curLine.dispose();
		curLine = null;
	}

	public void mouseDown(MouseEvent e) {

		DesignObject obj = null;
		switch(MODE){
		case Mode.NONE:
			notifyMouseDown(null);
			if(rangeObj == null && curLine == null)
			{
				rangeObj = new RangeObject(this, new Point(e.x, e.y));	
			}
			return;
		case Mode.RELATION_MODE:
			return;
		case Mode.END_RELATION_MODE:
			return;
		case Mode.ASSIGN:
			obj = new AssignObject(this, "/icons/assign.gif", "Assign", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.INBOUND_NETWORK_MODE:
			obj = new InboundNetworkObject(this, "/icons/inbound_obj.gif", "Inbound Network",  new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.OUTBOUND_NETWORK_MODE:
			obj = new OutboundNetworkObject(this, "/icons/outbound_obj.gif", "Outbound Network", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.CLOSE_NETWORK_MODE:
			obj = new CloseNetworkObject(this, "/icons/close_network_obj.gif", "Close Network", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.MESSAGE_QUEUE_MODE:
			obj = new MessageQueueObject(this, "/icons/mq_obj.gif", "Message Queue", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.BATCH_MODE:
			obj = new BatchObject(this, "/icons/batch_obj.gif", "Batch", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.DB_FRAME_MODE:
			obj = new DatabaseObject(this, "/icons/database_obj.gif", "Database", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.DB_SELECT_MODE:
			obj = new DatabaseSelectObject(this, "/icons/select_obj.gif", "Select", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.DB_UPDATE_MODE:
			obj = new DatabaseUpdateObject(this, "/icons/update_obj.gif", "Update", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.DB_PROCEDURE_MODE:
			obj = new DatabaseProcedureObject(this, "/icons/procedure_obj.gif", "Procedure", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.LOOP_MODE:
			obj = new LoopObject(this, "/icons/loop_obj.gif", "Loop", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.THREAD_MODE:
			obj = new ThreadObject(this, "/icons/thread_obj.gif", "Thread", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.COMPONENT_MODE:
			obj = new ComponentObject(this, "/icons/component_obj.gif", "Component", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.FILE_FRAME_MODE:
			obj = new FileFrameObject(this, "/icons/file_obj.gif", "File", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.FILE_READ_MODE:
			obj = new FileReadObject(this, "/icons/fileread_obj.gif", "File Read", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.FILE_WRITE_MODE:
			obj = new FileWriteObject(this, "/icons/filewrite_obj.gif", "File Write", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.FILE_DELETE_MODE:
			obj = new FileDeleteObject(this, "/icons/filedelete_obj.gif", "File Delete", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.THROW_MODE:
			obj = new ThrowObject(this, "/icons/throw_obj.gif", "Throw Exception", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.EXCEPTION_MODE:
			obj = new ExceptionObject(this, "/icons/exception_obj.gif", "Exception", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.SERVICE_CALLER_MODE:
			obj = new ServiceCallerObject(this, "/icons/servicecaller_obj.gif", "Service Caller", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.TERMINATE_MODE:
			obj = new TerminateObject(this, "/icons/terminate_obj.gif", "Terminate", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.CLIENT_NETWORK_MODE:
			obj = new ClientNetworkObject(this, "/icons/clientnetwork_obj.gif", "Connect To Server", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.NETWORK_READ_MODE:
			obj = new NetworkReadObject(this, "/icons/readnetwork_obj.gif", "Read Network", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		case Mode.NETWORK_WRITE_MODE:
			obj = new NetworkWriteObject(this, "/icons/writenetwork_obj.gif", "Write Network", new Point(e.x, e.y));
			notifyFreeMode();
			obj.setEditorSite(getEditorSite());
			getEditorSite().setDirty();
			break;
		default:
			notifyMouseDown(null);
			if(rangeObj == null)
			{
				rangeObj = new RangeObject(this, new Point(e.x, e.y));	
			}
			return;
		}
		obj.pack();
		changeParentSize(obj, e.x, e.y);
	}
	
	private void changeParentSize(DesignObject curObject, int x, int y)
	{
		if(curObject != null && (curObject.getParent() instanceof BlockObject))
		{
			BlockObject parentObject = (BlockObject)curObject.getParent();
			Rectangle childRect = curObject.getBounds();
			childRect.x = x;
			childRect.y = y;

			Rectangle rect = parentObject.getClientBoundary();
			
			Rectangle newRect = rect.union(childRect);
			if(newRect.width != rect.width || newRect.height != rect.height)
			{
				parentObject.setSize(new Point(newRect.width + 4, newRect.height + 46));
				parentObject.setControlBounds();
				DesignObject parent = (DesignObject)curObject.getParent();
				if(parent != null && parent instanceof BlockObject)
				{
					changeParentSize(parent, parent.getBounds().x, parent.getBounds().y);
				}
			}
		}

	}

	public void mouseUp(MouseEvent e) {
		if(rangeObj != null)
		{
			rangeObj.dispose();
			rangeObj = null;
		}
	}
	
	public void setControlBounds(){}

	@Override
	public void focusGained() {
	}

	@Override
	public void focusLost() {
	}

	@Override
	public void removeConnector(ConnectorObject obj) {
	}

}
