package com.nabsys.nabeeplus.editors;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;

import com.nabsys.common.fileio.ObjectFileIO;
import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.IMessageBox;
import com.nabsys.nabeeplus.common.ResourceFactory;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.design.AssignObject;
import com.nabsys.nabeeplus.design.BatchObject;
import com.nabsys.nabeeplus.design.BlockObject;
import com.nabsys.nabeeplus.design.ClientNetworkObject;
import com.nabsys.nabeeplus.design.CloseNetworkObject;
import com.nabsys.nabeeplus.design.ComponentObject;
import com.nabsys.nabeeplus.design.ConnectorObject;
import com.nabsys.nabeeplus.design.DatabaseObject;
import com.nabsys.nabeeplus.design.DatabaseProcedureObject;
import com.nabsys.nabeeplus.design.DatabaseSelectObject;
import com.nabsys.nabeeplus.design.DatabaseUpdateObject;
import com.nabsys.nabeeplus.design.DesignObject;
import com.nabsys.nabeeplus.design.ExceptionObject;
import com.nabsys.nabeeplus.design.FileDeleteObject;
import com.nabsys.nabeeplus.design.FileFrameObject;
import com.nabsys.nabeeplus.design.FileReadObject;
import com.nabsys.nabeeplus.design.FileWriteObject;
import com.nabsys.nabeeplus.design.IconObject;
import com.nabsys.nabeeplus.design.InboundNetworkObject;
import com.nabsys.nabeeplus.design.LoopObject;
import com.nabsys.nabeeplus.design.MessageQueueObject;
import com.nabsys.nabeeplus.design.Mode;
import com.nabsys.nabeeplus.design.NetworkReadObject;
import com.nabsys.nabeeplus.design.NetworkWriteObject;
import com.nabsys.nabeeplus.design.OutboundNetworkObject;
import com.nabsys.nabeeplus.design.PanelObject;
import com.nabsys.nabeeplus.design.ServiceCallerObject;
import com.nabsys.nabeeplus.design.ServiceInfoObject;
import com.nabsys.nabeeplus.design.TerminateObject;
import com.nabsys.nabeeplus.design.ThreadObject;
import com.nabsys.nabeeplus.design.ThrowObject;
import com.nabsys.nabeeplus.editors.input.ServiceDesignerInput;
import com.nabsys.nabeeplus.views.ServiceList;
import com.nabsys.nabeeplus.views.ServiceRequestView;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.resource.ServiceDesign;
import com.nabsys.resource.service.AssignHandler;
import com.nabsys.resource.service.ClientNetworkHandler;
import com.nabsys.resource.service.CloseNetworkHandler;
import com.nabsys.resource.service.ComponentHandler;
import com.nabsys.resource.service.DatabaseHandler;
import com.nabsys.resource.service.DatabaseProcedureHandler;
import com.nabsys.resource.service.DatabaseSelectHandler;
import com.nabsys.resource.service.DatabaseUpdateHandler;
import com.nabsys.resource.service.ExceptionHandler;
import com.nabsys.resource.service.FileDeleteHandler;
import com.nabsys.resource.service.FileFrameHandler;
import com.nabsys.resource.service.FileReadHandler;
import com.nabsys.resource.service.FileWriteHandler;
import com.nabsys.resource.service.GateServiceHandler;
import com.nabsys.resource.service.InboundHandler;
import com.nabsys.resource.service.LoopHandler;
import com.nabsys.resource.service.MappingHandler;
import com.nabsys.resource.service.MessageQueueHandler;
import com.nabsys.resource.service.NetworkReadHandler;
import com.nabsys.resource.service.NetworkWriteHandler;
import com.nabsys.resource.service.OutboundHandler;
import com.nabsys.resource.service.PluginHandler;
import com.nabsys.resource.service.ServiceCallHandler;
import com.nabsys.resource.service.ServiceHandler;
import com.nabsys.resource.service.TerminateHandler;
import com.nabsys.resource.service.ThreadHandler;
import com.nabsys.resource.service.ThrowHandler;
import com.nabsys.resource.service.TimeScheduleHandler;

public class ServiceDesigner extends NabeeEditor{

	public final static String 				ID 						= "com.nabsys.nabeeplus.editors.serviceDesigner";
	private 			PanelObject 		panel 					= null;
	private 			IAction		 		deleteAction 			= null;
	private				Composite			parent					= null;
	private 			boolean				isDirty					= false;
	private				Shell				shell					= null;		
	private				String				action					= null;
	private				String 				serviceID 				= null;
	private				String 				serviceName 			= null;
	private 			String 				serviceType 			= null;
	private				String 				serviceRemark 			= null;
	private				boolean 			serviceActivate 		= false;
	private 			KeyListener			keyListener				= null;
	private				ServiceRequestView	requestView				= null;
	private				boolean				isDisposed				= false;
	public ServiceDesigner(){
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		this.parent						= parent;
		this.shell						= parent.getShell();
		GridLayout layout 				= new GridLayout();
		
		ScrolledComposite scrollBack 	= new ScrolledComposite(parent, SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
		scrollBack.setExpandHorizontal	(true);
		scrollBack.setExpandVertical	(true);
		scrollBack.setMinSize			(3000, 4000);
		scrollBack.getVerticalBar().setIncrement(5);
		scrollBack.setLayoutData		(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		panel = new PanelObject(scrollBack, window);
		panel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		scrollBack.setContent(panel);
		
		panel.setLayout(layout);
		panel.setEditorSite(this);
		
		deleteAction = new Action() {
			public void run(){
				panel.notifyDelete();
			}
		};
		
		IActionBars actionBars = getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId()			, deleteAction);
		
		panel.addKeyListener(keyListener = new KeyListener(){
			Point point = new Point(0, 0);
			public void keyPressed(KeyEvent e) {
				switch(e.keyCode){
				case SWT.KEYCODE_BIT + 1 ://up
					point.y -= 2; 
					panel.notifyGroupMove(point);
					break;
				case SWT.KEYCODE_BIT + 2 ://down
					point.y += 2;
					panel.notifyGroupMove(point);
					break;
				case SWT.KEYCODE_BIT + 3 ://left
					point.x -= 2;
					panel.notifyGroupMove(point);
					break;
				case SWT.KEYCODE_BIT + 4 ://right
					point.x += 2;
					panel.notifyGroupMove(point);
					break;
				}
			}

			public void keyReleased(KeyEvent e) {
				panel.notifyGroupMouseUp();
				point = new Point(0, 0);
			}
			
		});
		
		addRetargetableAction();
		
		if(((ServiceDesignerInput)getEditorInput()).getFields() == null)
		{
			action = "I";
			ServiceInfoObject service = new ServiceInfoObject(panel, "/icons/serviceinfo_obj.gif", "Service Information", new Point(10, 10));
			service.setEditorSite(this);
		}
	}
	
	public void setDirty()
	{
		if(isDirty) return;
		isDirty = true;
		firePropertyChange(PROP_DIRTY);
	}
	
	public void setEditState(int mode, Image cursorImage)
	{
		switch(mode){
		case Mode.NONE:
			panel.notifyFreeMode();
			panel.setCursor(new Cursor(panel.getDisplay(), SWT.CURSOR_ARROW));
			break;
		case Mode.RELATION_MODE:
			panel.notifyRelationMode(cursorImage.getImageData());
			panel.setCursor(new Cursor(panel.getDisplay(), cursorImage.getImageData(), 0, 0));
			break;
		default:
			panel.notifyNewObjectMode(mode, cursorImage.getImageData());
			panel.setCursor(new Cursor(panel.getDisplay(), cursorImage.getImageData(), 0, 0));
			break;
		}
	}
	
	private ServiceInfoObject findServiceGate()
	{
		Control[] children = panel.getChildren();
		
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof ServiceInfoObject)
			{
				return (ServiceInfoObject)children[i];
			}
		}
		return null;
	}
	
	private void getHandlers(DesignObject parent, ServiceHandler parentHandler)
	{
		Control[] children = parent.getChildren();
		
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof BlockObject)
			{
				ServiceHandler handler = ((BlockObject)children[i]).getHandler(parentHandler);
				getHandlers((BlockObject)children[i], handler);
			}
			else if(children[i] instanceof ServiceInfoObject)
			{
				continue;
			}
			else if(children[i] instanceof IconObject)
			{
				((IconObject)children[i]).getHandler(parentHandler);
			}
		}
	}
	
	private void getHandlerLink(DesignObject parent) throws SecurityException, NoSuchMethodException
	{
		Control[] children = parent.getChildren();
		
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof BlockObject)
			{
				getHandlerLink((BlockObject)children[i]);
			}
			else if(children[i] instanceof ConnectorObject)
			{
				ConnectorObject connector = ((ConnectorObject)children[i]);
				if(connector.getNextObject() == null) continue;
				MappingHandler handler = connector.getHandler();
				
				DesignObject prevObject = connector.getPrevObject();
				ServiceHandler prevServiceHandler = null;
				if(prevObject instanceof BlockObject && prevObject == connector.getParent())
				{
					handler.addPrevService(prevServiceHandler = ((BlockObject)prevObject).getHandler(null));
					prevServiceHandler.addNextInsideService(handler);
				}
				else
				{
					if(prevObject instanceof IconObject)
					{
						handler.addPrevService(prevServiceHandler = ((IconObject)prevObject).getHandler(null));
						prevServiceHandler.addNextService(handler);
					}
					else if(prevObject instanceof BlockObject)
					{
						handler.addPrevService(prevServiceHandler = ((BlockObject)prevObject).getHandler(null));
						prevServiceHandler.addNextService(handler);
					}
				}
				
				
				DesignObject nextObject = connector.getNextObject();
				if(nextObject instanceof IconObject)
				{
					ServiceHandler nextServiceHandler = ((IconObject)nextObject).getHandler(null);
					handler.addNextService(nextServiceHandler);
				}
				else if(nextObject instanceof BlockObject)
				{
					ServiceHandler nextServiceHandler = ((BlockObject)nextObject).getHandler(null);
					handler.addNextService(nextServiceHandler);
				}
			}
		}
	}
	
	private void setLinkObject(PanelObject parentObject)
	{
		int loop = parentObject.getChildren().length;
		for(int i=0; i<loop; i++)
		{
			Control obj = parentObject.getChildren()[i];
			ServiceHandler handler = null;
			if(obj instanceof BlockObject)handler = ((BlockObject)obj).getHandler(null);
			else if(obj instanceof IconObject)handler = ((IconObject)obj).getHandler(null);
			
			if(handler == null)
			{
				continue;
			}
			
			if(obj instanceof BlockObject)
			{
				if(handler.getMappingList() != null && handler.getMappingList().size() > 0)
				{
					int mappingSize = handler.getMappingList().size();

					DesignObject designObject = (DesignObject)obj;
					for(int j=0; j<mappingSize; j++)
					{
						MappingHandler mapping = handler.getMappingList().get(j);
						ConnectorObject connector = new ConnectorObject(parentObject, mapping);
						connector.setEditorSite(this);
						designObject.addGoingOut(connector);
						connector.setStartObject(designObject);

						if(mapping.getNextService() == null) continue;
						DesignObject endObject = (DesignObject)mapping.getNextService().getDesignObject();
						if(endObject instanceof BlockObject && endObject == connector.getParent())
						{
							endObject.addComingInInside(connector);
							connector.setEndObject(endObject);
							connector.pointDecision();
						}
						else
						{
							endObject.addComingIn(connector);
							connector.setEndObject(endObject);
							connector.pointDecision();
						}
					}
				}
				if(handler.getInsideMappingList() != null && handler.getInsideMappingList().size() > 0)
				{
					int mappingSize = handler.getInsideMappingList().size();
					DesignObject designObject = (DesignObject)obj;
					for(int j=0; j<mappingSize; j++)
					{
						MappingHandler mapping = handler.getInsideMappingList().get(j);
						ConnectorObject connector = new ConnectorObject((PanelObject)obj, mapping);
						connector.setEditorSite(this);
						designObject.addGoingOutInside(connector);
						connector.setStartObject(designObject);
						
						if(mapping.getNextService() == null) continue;
						DesignObject endObject = (DesignObject)mapping.getNextService().getDesignObject();
						endObject.addComingIn(connector);
						connector.setEndObject(endObject);
						connector.pointDecision();
					}
				}
				setLinkObject((BlockObject)obj);
			}
			else if(obj instanceof IconObject)
			{
				if(handler.getMappingList() != null && handler.getMappingList().size() > 0)
				{
					int mappingSize = handler.getMappingList().size();

					DesignObject designObject = (DesignObject)obj;
					for(int j=0; j<mappingSize; j++)
					{
						MappingHandler mapping = handler.getMappingList().get(j);
						ConnectorObject connector = new ConnectorObject(parentObject, mapping);
						connector.setEditorSite(this);
						designObject.addGoingOut(connector);
						connector.setStartObject(designObject);
						
						if(mapping.getNextService() == null) continue;
						DesignObject endObject = (DesignObject)mapping.getNextService().getDesignObject();
						if(endObject instanceof BlockObject && endObject == connector.getParent())
						{
							endObject.addComingInInside(connector);
							connector.setEndObject(endObject);
							connector.pointDecision();
						}
						else
						{
							endObject.addComingIn(connector);
							connector.setEndObject(endObject);
							connector.pointDecision();
						}
					}
				}
			}
		}
	}
	
	private void setDesignObjects(ServiceHandler parentHandler, PanelObject parentObject)
	{
		int loop = parentHandler.getChildren().size();
		
		for(int i=0; i<loop; i++)
		{
			ServiceHandler handler = parentHandler.getChildren().get(i);
			
			if(handler instanceof ClientNetworkHandler)
			{
				ClientNetworkObject obj = new ClientNetworkObject(parentObject, "/icons/clientnetwork_obj.gif", "Connect To Server", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				setDesignObjects(handler, obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof AssignHandler)
			{
				AssignObject obj = new AssignObject(parentObject, "/icons/assign.gif", "Assign", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof CloseNetworkHandler)
			{
				CloseNetworkObject obj = new CloseNetworkObject(parentObject, "/icons/close_network_obj.gif", "Close Network", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof ComponentHandler)
			{
				ComponentObject obj = new ComponentObject(parentObject, "/icons/component_obj.gif", "Component", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof DatabaseHandler)
			{
				DatabaseObject obj = new DatabaseObject(parentObject, "/icons/database_obj.gif", "Database", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				setDesignObjects(handler, obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof DatabaseSelectHandler)
			{
				DatabaseSelectObject obj = new DatabaseSelectObject(parentObject, "/icons/select_obj.gif", "Select", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				setDesignObjects(handler, obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof DatabaseUpdateHandler)
			{
				DatabaseUpdateObject obj = new DatabaseUpdateObject(parentObject, "/icons/update_obj.gif", "Update", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof DatabaseProcedureHandler)
			{
				DatabaseProcedureObject obj = new DatabaseProcedureObject(parentObject, "/icons/procedure_obj.gif", "Procedure", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof ThrowHandler)
			{
				ThrowObject obj = new ThrowObject(parentObject, "/icons/throw_obj.gif", "Throw Exception", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof ExceptionHandler)
			{
				ExceptionObject obj = new ExceptionObject(parentObject, "/icons/exception_obj.gif", "Exception", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof FileDeleteHandler)
			{
				FileDeleteObject obj = new FileDeleteObject(parentObject, "/icons/filedelete_obj.gif", "File Delete", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof FileFrameHandler)
			{
				FileFrameObject obj = new FileFrameObject(parentObject, "/icons/file_obj.gif", "File", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				setDesignObjects(handler, obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof FileReadHandler)
			{
				FileReadObject obj = new FileReadObject(parentObject, "/icons/fileread_obj.gif", "File Read", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				setDesignObjects(handler, obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof FileWriteHandler)
			{
				FileWriteObject obj = new FileWriteObject(parentObject, "/icons/filewrite_obj.gif", "File Write", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof LoopHandler)
			{
				LoopObject obj = new LoopObject(parentObject, "/icons/loop_obj.gif", "Loop", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				setDesignObjects(handler, obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof InboundHandler)
			{
				InboundNetworkObject obj = new InboundNetworkObject(parentObject, "/icons/inbound_obj.gif", "Inbound Network", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof TimeScheduleHandler)
			{
				BatchObject obj = new BatchObject(parentObject, "/icons/batch_obj.gif", "Batch", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof MessageQueueHandler)
			{
				MessageQueueObject obj = new MessageQueueObject(parentObject, "/icons/mq_obj.gif", "Message Queue", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof NetworkReadHandler)
			{
				NetworkReadObject obj = new NetworkReadObject(parentObject, "/icons/readnetwork_obj.gif", "Read Network", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof NetworkWriteHandler)
			{
				NetworkWriteObject obj = new NetworkWriteObject(parentObject, "/icons/writenetwork_obj.gif", "Write Network", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof OutboundHandler)
			{
				OutboundNetworkObject obj = new OutboundNetworkObject(parentObject, "/icons/outbound_obj.gif", "Outbound Network", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof PluginHandler)
			{
				//PluginObject obj = new PluginObject(parentObject, "/icons/repo_rep.gif", "Plugin", new Point(handler.getX(), handler.getY()), handler);
				//handler.setDesignObject(obj);
				//obj.pack();
				//obj.setEditorSite(this);
			}
			else if(handler instanceof TerminateHandler)
			{
				TerminateObject obj = new TerminateObject(parentObject, "/icons/terminate_obj.gif", "Terminate", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof ThreadHandler)
			{
				ThreadObject obj = new ThreadObject(parentObject, "/icons/thread_obj.gif", "Thread", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				setDesignObjects(handler, obj);
				obj.pack();
				obj.setEditorSite(this);
			}
			else if(handler instanceof ServiceCallHandler)
			{
				ServiceCallerObject obj = new ServiceCallerObject(parentObject, "/icons/servicecaller_obj.gif", "Service Caller", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
				handler.setDesignObject(obj);
				obj.pack();
				obj.setEditorSite(this);
			}
		}
	}
	
	private void clearHandler(DesignObject parent)
	{
		Control[] children = parent.getChildren();
		
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof BlockObject)
			{
				((BlockObject)children[i]).initHandler();
				clearHandler((BlockObject)children[i]);
			}
			else if(children[i] instanceof IconObject)
			{
				((IconObject)children[i]).initHandler();
			}
		}
	}
	
	public void loadDesign(NBFields field)
	{
		action = "U";
		if(field.get("SERVICE_DESIGN") instanceof String && ((String)field.get("SERVICE_DESIGN")).equals("null"))
		{
			ServiceInfoObject obj = new ServiceInfoObject(panel, "/icons/serviceinfo_obj.gif", "Service Information", new Point(10, 10));
			obj.setServiceID((String)field.get("ID"));
			obj.setServiceName((String)field.get("NAME"));
			obj.setType((String)field.get("TYPE"));
			obj.setRemark((String)field.get("REMARK"));
			obj.setActivate(((String)field.get("ACTIVATE")).equals("true"));
			obj.setEditorSite(this);
			obj.pack();
		}
		else
		{
			serviceID = (String)field.get("ID");
			serviceName = (String)field.get("NAME");
			serviceType = (String)field.get("TYPE");
			serviceRemark = (String)field.get("REMARK");
			serviceActivate = ((String)field.get("ACTIVATE")).equals("true");
			
			ServiceDesign serviceDesign = null;
			byte[] designData = (byte[])field.get("SERVICE_DESIGN");
			ObjectFileIO ofio = new ObjectFileIO();
			try {
				serviceDesign = (ServiceDesign) ofio.recoverObject(designData);
			} catch (IOException e) {
				IMessageBox.Error(shell, NBLabel.get(0x0270));
				return;
			} catch (ClassNotFoundException e) {
				IMessageBox.Error(shell, NBLabel.get(0x0270));
				return;
			}

			ServiceHandler handler = serviceDesign.getHandler();
			ServiceInfoObject obj = new ServiceInfoObject(panel, "/icons/serviceinfo_obj.gif", "Service Information", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
			
			obj.setServiceID(serviceID);
			obj.setServiceName(serviceName);
			obj.setType(serviceType);
			obj.setRemark(serviceRemark);
			obj.setActivate(serviceActivate);
			obj.pack();
			obj.setEditorSite(this);
			handler.setDesignObject(obj);
			
			setDesignObjects(handler, panel);
			setLinkObject(panel);
			clearHandler(panel);
			obj.initHandler();
			panel.setCurrentConnect(null);
			
			requestView = (ServiceRequestView)getSite().getWorkbenchWindow().getActivePage().findView(ServiceRequestView.ID);
			if(requestView != null && !requestView.isDispose())
			{
				requestView.setProtocol(((ServiceDesignerInput)getEditorInput()).getProtocol());
				requestView.setServer(getServer());
				requestView.parseParams(((ServiceDesignerInput)getEditorInput()).getInstance(), serviceID);
				requestView.setEditor(this);
			}
		}
	}
	
	public void copyDesign(String cpyServiceID, NBFields field)
	{
		action = "I";
		if(field.get("SERVICE_DESIGN") instanceof String && ((String)field.get("SERVICE_DESIGN")).equals("null"))
		{
			ServiceInfoObject obj = new ServiceInfoObject(panel, "/icons/serviceinfo_obj.gif", "Service Information", new Point(10, 10));
			obj.setServiceID(cpyServiceID);
			obj.setServiceName((String)field.get("NAME"));
			obj.setType((String)field.get("TYPE"));
			obj.setRemark((String)field.get("REMARK"));
			obj.setActivate(((String)field.get("ACTIVATE")).equals("true"));
			obj.setEditorSite(this);
			obj.pack();
		}
		else
		{
			serviceID = cpyServiceID;
			serviceName = (String)field.get("NAME");
			serviceType = (String)field.get("TYPE");
			serviceRemark = (String)field.get("REMARK");
			serviceActivate = ((String)field.get("ACTIVATE")).equals("true");
			
			ServiceDesign serviceDesign = null;
			byte[] designData = (byte[])field.get("SERVICE_DESIGN");
			ObjectFileIO ofio = new ObjectFileIO();
			try {
				serviceDesign = (ServiceDesign) ofio.recoverObject(designData);
			} catch (IOException e) {
				IMessageBox.Error(shell, NBLabel.get(0x0270));
				return;
			} catch (ClassNotFoundException e) {
				IMessageBox.Error(shell, NBLabel.get(0x0270));
				return;
			}

			ServiceHandler handler = serviceDesign.getHandler();
			ServiceInfoObject obj = new ServiceInfoObject(panel, "/icons/serviceinfo_obj.gif", "Service Information", new Rectangle(handler.getX(), handler.getY(), handler.getWidth(), handler.getHeight()), handler);
			
			obj.setServiceID(cpyServiceID);
			obj.setServiceName(serviceName);
			obj.setType(serviceType);
			obj.setRemark(serviceRemark);
			obj.setActivate(serviceActivate);
			obj.pack();
			obj.setEditorSite(this);
			handler.setDesignObject(obj);
			
			setDesignObjects(handler, panel);
			setLinkObject(panel);
			clearHandler(panel);
			obj.initHandler();
			panel.setCurrentConnect(null);
			
			requestView = (ServiceRequestView)getSite().getWorkbenchWindow().getActivePage().findView(ServiceRequestView.ID);
			if(requestView != null && !requestView.isDispose())
			{
				requestView.setProtocol(((ServiceDesignerInput)getEditorInput()).getProtocol());
				requestView.setServer(getServer());
				requestView.parseParams(((ServiceDesignerInput)getEditorInput()).getInstance(), cpyServiceID);
				requestView.setEditor(this);
			}
		}
		
		isDirty = true;
		firePropertyChange(PROP_DIRTY);
	}
	
	private ExceptionHandler findExceptionHandler(ServiceHandler parentHandler)
	{
		Control[] children = panel.getChildren();
		
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof ExceptionObject)
			{
				return (ExceptionHandler) ((ExceptionObject)children[i]).getHandler(parentHandler);
			}
		}
		return null;
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		if(getPartName().equals("New Service"))
		{
			IMessageBox.Warning(shell, "Need service ID.");
			return;
		}
		ServiceInfoObject serviceInfoObject = findServiceGate();
		if(serviceInfoObject == null) 
		{
			IMessageBox.Error(shell, "Service object null.");
			return;
		}
		ServiceHandler handler = null;
		getHandlers(panel, handler = serviceInfoObject.getHandler(null));

		try{
			getHandlerLink(panel);
		} catch (SecurityException e) {
			IMessageBox.Error(shell, NBLabel.get(0x026F));
			return;
		} catch (NoSuchMethodException e) {
			IMessageBox.Error(shell, NBLabel.get(0x026F));
			return;
		}
		
		if(serviceInfoObject.getServiceID().equals(""))
		{
			IMessageBox.Warning(shell, "Need service ID.");
			return;
		}
		
		ServiceDesign serviceDesign = new ServiceDesign(serviceInfoObject.getServiceID(),
											serviceInfoObject.getServiceName(), 
											serviceInfoObject.getType(), 
											serviceInfoObject.getRemark(), 
											serviceInfoObject.isActivate(), 
											(GateServiceHandler) handler);
		serviceDesign.setExceptionHandler(findExceptionHandler(handler));
		
		ObjectFileIO ofio = new ObjectFileIO();
		byte[] data = null;
		try {
			data = ofio.convertToByte(serviceDesign);
		} catch (IOException e) {
			e.printStackTrace();
			IMessageBox.Error(shell, NBLabel.get(0x026F));
			return;
		}
		
		clearHandler(panel);
		serviceInfoObject.initHandler();
		
		NBFields fields = new NBFields();
		fields.put(IPC.NB_LOAD_CLASS	, "com.nabsys.management.document.ServiceConfig");
		fields.put("CMD_CODE"			, "S");
		fields.put(IPC.NB_INSTNCE_ID	, ((ServiceDesignerInput)getEditorInput()).getInstance());
		fields.put("ACTION"				, action);
		fields.put("ID"					, serviceDesign.getID());
		fields.put("NAME"				, serviceDesign.getName() == null?"":serviceDesign.getName());
		fields.put("TYPE"				, serviceDesign.getType());
		fields.put("ACTIVATE"			, serviceDesign.isActivate() +"");
		fields.put("SERVICE_DESIGN"		, data);
		fields.put("REMARK"				, serviceDesign.getRemark() == null?"":serviceDesign.getRemark());

		try {

			fields = ((ServiceDesignerInput)getEditorInput()).getProtocol().execute(fields);

			if((Integer)fields.get(IPC.NB_MSG_RETURN) == IPC.FAIL)
			{
				IMessageBox.Error(shell, (String)fields.get("RTN_MSG"));
				return;
			}
			
			ServiceList serviceList = (ServiceList)getSite().getPage().findView(ServiceList.ID);
			serviceList.refreshList(action.equals("I")
					, serviceDesign.getID()
					, serviceDesign.getName()
					, serviceDesign.getType()
					, serviceDesign.getRemark());
			
			isDirty = false;
			firePropertyChange(PROP_DIRTY);
			
		} catch (SocketClosedException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (TimeoutException e) {
			IMessageBox.Error(shell, e.getMessage());
		} catch (NetException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (UnsupportedEncodingException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (NoSuchAlgorithmException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (DataTypeException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		} catch (ProtocolException e) {
			IMessageBox.Error(shell, NBLabel.get(0x0090));
			closeConnection();
		}
		action = "U";
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}
	
	public void changeTitle(String title)
	{
		if(title == null || title.equals(""))
		{
			return;
		}
		setPartName(title);
	}
	
	private void addRetargetableAction()
	{
		IActionBars actionBars = getEditorSite().getActionBars();
        actionBars.setGlobalActionHandler(ResourceFactory.ARROW.getId(), new Action() {
			public void run(){
				setEditState(Mode.NONE, new Image(parent.getDisplay(), ResourceFactory.ARROW.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.RELATION.getId(), new Action() {
			public void run(){
				setEditState(Mode.RELATION_MODE, new Image(parent.getDisplay(), Activator.getImageDescriptor("/icons/cross.png").getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.ASSIGN.getId(), new Action() {
			public void run(){
				setEditState(Mode.ASSIGN, new Image(parent.getDisplay(), ResourceFactory.ASSIGN.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.INBOUND_NETWORK.getId(), new Action() {
			public void run(){
				setEditState(Mode.INBOUND_NETWORK_MODE, new Image(parent.getDisplay(), ResourceFactory.INBOUND_NETWORK.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.OUTBOUND_NETWORK.getId(), new Action() {
			public void run(){
				setEditState(Mode.OUTBOUND_NETWORK_MODE, new Image(parent.getDisplay(), ResourceFactory.OUTBOUND_NETWORK.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.CLOSE_NETWORK.getId(), new Action() {
			public void run(){
				setEditState(Mode.CLOSE_NETWORK_MODE, new Image(parent.getDisplay(),ResourceFactory.CLOSE_NETWORK.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.MESSAGE_QUEUE.getId(), new Action() {
			public void run(){
				setEditState(Mode.MESSAGE_QUEUE_MODE, new Image(parent.getDisplay(), ResourceFactory.MESSAGE_QUEUE.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.BATCH.getId(), new Action() {
			public void run(){
				setEditState(Mode.BATCH_MODE, new Image(parent.getDisplay(), ResourceFactory.BATCH.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.DATABASE.getId(), new Action() {
			public void run(){
				setEditState(Mode.DB_FRAME_MODE, new Image(parent.getDisplay(), ResourceFactory.DATABASE.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.SELECT.getId(), new Action() {
			public void run(){
				setEditState(Mode.DB_SELECT_MODE, new Image(parent.getDisplay(), ResourceFactory.SELECT.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.UPDATE.getId(), new Action() {
			public void run(){
				setEditState(Mode.DB_UPDATE_MODE, new Image(parent.getDisplay(), ResourceFactory.UPDATE.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.PROCEDURE.getId(), new Action() {
			public void run(){
				setEditState(Mode.DB_PROCEDURE_MODE, new Image(parent.getDisplay(), ResourceFactory.PROCEDURE.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.LOOP.getId(), new Action() {
			public void run(){
				setEditState(Mode.LOOP_MODE, new Image(parent.getDisplay(), ResourceFactory.LOOP.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.CLIENT_NETWORK.getId(), new Action() {
			public void run(){
				setEditState(Mode.CLIENT_NETWORK_MODE, new Image(parent.getDisplay(), ResourceFactory.CLIENT_NETWORK.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.NETWORK_READ.getId(), new Action() {
			public void run(){
				setEditState(Mode.NETWORK_READ_MODE, new Image(parent.getDisplay(), ResourceFactory.NETWORK_READ.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.NETWORK_WRITE.getId(), new Action() {
			public void run(){
				setEditState(Mode.NETWORK_WRITE_MODE, new Image(parent.getDisplay(), ResourceFactory.NETWORK_WRITE.getImageDescriptor().getImageData()));
			}
		});
        
        actionBars.setGlobalActionHandler(ResourceFactory.FILE.getId(), new Action() {
			public void run(){
				setEditState(Mode.FILE_FRAME_MODE, new Image(parent.getDisplay(), ResourceFactory.FILE.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.FILE_READ.getId(), new Action() {
			public void run(){
				setEditState(Mode.FILE_READ_MODE, new Image(parent.getDisplay(), ResourceFactory.FILE_READ.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.FILE_WRITE.getId(), new Action() {
			public void run(){
				setEditState(Mode.FILE_WRITE_MODE, new Image(parent.getDisplay(), ResourceFactory.FILE_WRITE.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.FILE_DELETE.getId(), new Action() {
			public void run(){
				setEditState(Mode.FILE_DELETE_MODE, new Image(parent.getDisplay(), ResourceFactory.FILE_DELETE.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.SERVICE_CALLER.getId(), new Action() {
			public void run(){
				setEditState(Mode.SERVICE_CALLER_MODE, new Image(parent.getDisplay(), ResourceFactory.SERVICE_CALLER.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.THREAD.getId(), new Action() {
			public void run(){
				setEditState(Mode.THREAD_MODE, new Image(parent.getDisplay(), ResourceFactory.THREAD.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.COMPONENT.getId(), new Action() {
			public void run(){
				setEditState(Mode.COMPONENT_MODE, new Image(parent.getDisplay(), ResourceFactory.COMPONENT.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.THROW.getId(), new Action() {
			public void run(){
				setEditState(Mode.THROW_MODE, new Image(parent.getDisplay(), ResourceFactory.THROW.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.EXCEPTION.getId(), new Action() {
			public void run(){
				setEditState(Mode.EXCEPTION_MODE, new Image(parent.getDisplay(), ResourceFactory.EXCEPTION.getImageDescriptor().getImageData()));
			}
		});
        actionBars.setGlobalActionHandler(ResourceFactory.SERVICE_END.getId(), new Action() {
			public void run(){
				setEditState(Mode.TERMINATE_MODE, new Image(parent.getDisplay(), ResourceFactory.SERVICE_END.getImageDescriptor().getImageData()));
			}
		});
	}
	
	private void setSubTestStatus(DesignObject parent, int handlerID)
	{
		Control[] children = parent.getChildren();
		for(int i=0; i<children.length; i++)
		{
			Control child = children[i];
			if(child instanceof IconObject)
			{
				((IconObject)child).setTestStatus(handlerID);
			}
			else if(child instanceof ConnectorObject)
			{
				((ConnectorObject)child).setTestStatus(handlerID);
			}
			else if(child instanceof BlockObject)
			{
				setSubTestStatus((DesignObject)child, handlerID);
				((BlockObject)child).setTestStatus(handlerID);
			}
		}
	}
	
	private void setSubInitStatus(DesignObject parent)
	{
		Control[] children = parent.getChildren();
		for(int i=0; i<children.length; i++)
		{
			Control child = children[i];
			if(child instanceof IconObject)
			{
				((IconObject)child).setInitStatus();
			}
			else if(child instanceof ConnectorObject)
			{
				((ConnectorObject)child).setInitStatus();
			}
			else if(child instanceof BlockObject)
			{
				setSubInitStatus((DesignObject)child);
				((BlockObject)child).setInitStatus();
			}
		}
	}
	
	public void initTestStatus()
	{
		setSubInitStatus(panel);
	}
	
	public void setTestStatus(int handlerID)
	{
		setSubTestStatus(panel, handlerID);
	}
	
	public void dispose()
	{
		if(!panel.isDisposed())
		{
			panel.removeKeyListener(keyListener);
			panel.dispose();
		}
		isDisposed = true;
		super.dispose();
	}
	
	public boolean isDisposed()
	{
		return isDisposed;
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {
		requestView = (ServiceRequestView)getSite().getWorkbenchWindow().getActivePage().findView(ServiceRequestView.ID);
		if(requestView != null && !requestView.isDispose())
		{
			requestView.setProtocol(((ServiceDesignerInput)getEditorInput()).getProtocol());
			requestView.setServer(getServer());
			requestView.parseParams(((ServiceDesignerInput)getEditorInput()).getInstance(), serviceID);
			requestView.setEditor(this);
		}
		addRetargetableAction();
	}

}
