package com.nabsys.nabeeplus.common;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.RetargetAction;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.nabeeplus.common.label.NBLabel;
import com.nabsys.nabeeplus.views.model.Model;

public abstract class ResourceFactory {
	
	private static Model serverRoot = null;
	private static Model searchRoot = null;
	private static String searchString = "";
	private static String searchKey = "";
	private static boolean searchFolder = false;
	private static boolean searchContents = true;
	private static boolean caseSensitive = false;
	private static boolean searchSchemaRoot = false;
	private static boolean searchSchemaSelected = true;
	
	public static void setSearchRoot(Model root)
	{
		ResourceFactory.searchRoot = root;
	}
	
	public static Model getSearchRoot()
	{
		return ResourceFactory.searchRoot;
	}
	
	public static void removeSearchRoot()
	{
		ResourceFactory.searchRoot = null;
	}

	public static void setSearchString(String string)
	{
		ResourceFactory.searchString = string;
	}
	
	public static String getSearchString()
	{
		return ResourceFactory.searchString;
	}
	
	public static void removeSearchString()
	{
		ResourceFactory.searchString = "";;
	}
	
	public static void setServerRoot(Model root)
	{
		ResourceFactory.serverRoot = root;
	}
	
	public static Model getServerRoot()
	{
		return ResourceFactory.serverRoot;
	}
	
	public static final ResourceFactory CONNECT = new ResourceFactory("connect") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x004B));
            action.setText(NBLabel.get(0x0058) + "                    ");
            action.setToolTipText(NBLabel.get(0x004B));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/net_connect.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/net_connect.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
	
    public static final ResourceFactory DISCONNECT = new ResourceFactory("disconnect") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x0059));
            action.setText(NBLabel.get(0x0059) + "                    ");
            action.setToolTipText(NBLabel.get(0x004C));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/net_disconnect.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/net_disconnect.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory RUN = new ResourceFactory("run") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x0054));
            action.setText(NBLabel.get(0x0054) + "                    ");
            action.setToolTipText(NBLabel.get(0x0224));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/run_exc.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/run_exc.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory TERMINATE = new ResourceFactory("terminate") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x0225));
            action.setText(NBLabel.get(0x0225) + "                    ");
            action.setToolTipText(NBLabel.get(0x0226));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/terminate.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/terminate.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory RESUME = new ResourceFactory("resume") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x0227));
            action.setText(NBLabel.get(0x0227) + "                    ");
            action.setToolTipText(NBLabel.get(0x0228));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/resume.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/resume.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory NEW_SQL_DIR = new ResourceFactory("newsqldir") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x022D));
            action.setText(NBLabel.get(0x022D) + "                    ");
            action.setToolTipText(NBLabel.get(0x022E));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/new_sqlfolder.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/new_sqlfolder.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory NEW_SQL_DOC = new ResourceFactory("newsqldoc") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x022F));
            action.setText(NBLabel.get(0x022F) + "                    ");
            action.setToolTipText(NBLabel.get(0x0230));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/new_sqldoc.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/new_sqldoc.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory NEW_INSTANCE = new ResourceFactory("newinstance") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x019D));
            action.setText(NBLabel.get(0x019D) + "                    ");
            action.setToolTipText(NBLabel.get(0x019C));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/new_instance.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/new_instance.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory SEARCH = new ResourceFactory("searchsql") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x0053));
            action.setText(NBLabel.get(0x0053) + "                    ");
            action.setToolTipText(NBLabel.get(0x0046));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/search_src.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/search_src.gif"));
            action.setAccelerator(SWT.CTRL | 'H');
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory NEW_SERVICE = new ResourceFactory("newservice") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),NBLabel.get(0x025B));
            action.setText(NBLabel.get(0x025B) + "                    ");
            action.setToolTipText(NBLabel.get(0x025C));
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/new_service.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/new_service.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory ARROW = new ResourceFactory("arrow") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Select item");
            action.setText("Select item");
            action.setToolTipText("Select item");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/icon_arrow.png"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/icon_arrow.png"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory RELATION = new ResourceFactory("relation") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Connector");
            action.setText("Connector");
            action.setToolTipText("Connector");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/connector.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/connector.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory ASSIGN = new ResourceFactory("Assign") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Assign");
            action.setText("Assign");
            action.setToolTipText("Assign");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/assign.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/assign.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory INBOUND_NETWORK = new ResourceFactory("InboundNetwork") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Inbound network");
            action.setText("Inbound network");
            action.setToolTipText("Inbound network");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/inbound_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/inbound_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory OUTBOUND_NETWORK = new ResourceFactory("OutboundNetwork") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Outbound network");
            action.setText("Outbound network");
            action.setToolTipText("Outbound network");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/outbound_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/outbound_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory CLOSE_NETWORK = new ResourceFactory("CloseNetwork") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Close network");
            action.setText("Close network");
            action.setToolTipText("Close network");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/close_network_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/close_network_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory MESSAGE_QUEUE = new ResourceFactory("MessageQueue") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Message queue");
            action.setText("Message queue");
            action.setToolTipText("Message queue");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/mq_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/mq_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory BATCH = new ResourceFactory("Batch") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Batch");
            action.setText("Batch");
            action.setToolTipText("Batch");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/batch_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/batch_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory DATABASE = new ResourceFactory("Database") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Database");
            action.setText("Database");
            action.setToolTipText("Database");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/database_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/database_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory SELECT = new ResourceFactory("Select") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Select");
            action.setText("Select");
            action.setToolTipText("Select");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/select_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/select_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory UPDATE = new ResourceFactory("Update") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Update");
            action.setText("Update");
            action.setToolTipText("Update");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/update_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/update_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory PROCEDURE = new ResourceFactory("Procedure") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Procedure");
            action.setText("Procedure");
            action.setToolTipText("Procedure");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/procedure_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/procedure_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory LOOP = new ResourceFactory("Loop") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Loop");
            action.setText("Loop");
            action.setToolTipText("Loop");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/loop_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/loop_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory FILE = new ResourceFactory("File") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"File");
            action.setText("File");
            action.setToolTipText("File");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/file_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/file_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory FILE_READ = new ResourceFactory("FileRead") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"File read");
            action.setText("File read");
            action.setToolTipText("File read");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/fileread_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/fileread_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory FILE_WRITE = new ResourceFactory("FileWrite") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"File Write");
            action.setText("File Write");
            action.setToolTipText("File Write");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/filewrite_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/filewrite_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory FILE_DELETE = new ResourceFactory("FileDelete") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"File Delete");
            action.setText("File Delete");
            action.setToolTipText("File Delete");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/filedelete_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/filedelete_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory FILE_COPY = new ResourceFactory("FileCopy") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"File Copy");
            action.setText("File Copy");
            action.setToolTipText("File Copy");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/filecopy_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/filecopy_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory SERVICE_CALLER = new ResourceFactory("servicecaller") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"ServiceCaller");
            action.setText("Service Caller");
            action.setToolTipText("Service Caller");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/servicecaller_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/servicecaller_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory THREAD = new ResourceFactory("Thread") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Thread");
            action.setText("Thread");
            action.setToolTipText("Thread");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/thread_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/thread_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory COMPONENT = new ResourceFactory("Component") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Component");
            action.setText("Component");
            action.setToolTipText("Component");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/component_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/component_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory EXCEPTION = new ResourceFactory("Exception") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Exception");
            action.setText("Exception");
            action.setToolTipText("Exception");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/exception_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/exception_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory THROW = new ResourceFactory("Throw") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Throw Exception");
            action.setText("Throw Exception");
            action.setToolTipText("Throw Exception");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/throw_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/throw_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory CLIENT_NETWORK = new ResourceFactory("ConnecServer") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"ConnecServer");
            action.setText("Connect server");
            action.setToolTipText("Connect server");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/clientnetwork_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/clientnetwork_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory NETWORK_READ = new ResourceFactory("ReadFromServer") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"ReadFromServer");
            action.setText("Read from server");
            action.setToolTipText("Read from server");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/readnetwork_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/readnetwork_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory NETWORK_WRITE = new ResourceFactory("WriteToServer") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"WriteToServer");
            action.setText("Write to server");
            action.setToolTipText("Write to server");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/writenetwork_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/writenetwork_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
    public static final ResourceFactory SERVICE_END = new ResourceFactory("ServiceEnd") {

        public RetargetAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(),"Terminate");
            action.setText("Terminate");
            action.setToolTipText("Terminate");
            action.setImageDescriptor(Activator.getImageDescriptor("/icons/terminate_obj.gif"));
            action.setDisabledImageDescriptor(Activator.getImageDescriptor("/icons/dbl/terminate_obj.gif"));
            window.getPartService().addPartListener(action);
            super.setImageDescriptor(action.getImageDescriptor());
            return action;
        }
    };
    
	private final String actionId;
	private ImageDescriptor imageDescriptor;
    protected ResourceFactory(String actionId) {
    	this.actionId = actionId;
    }
    
    public String getId() {
        return actionId;
    }
    
    protected void setImageDescriptor(ImageDescriptor imageDescriptor){
    	this.imageDescriptor = imageDescriptor;
    }
    
    public ImageDescriptor getImageDescriptor()
    {
    	return this.imageDescriptor;
    }
    
    public abstract RetargetAction create(IWorkbenchWindow window);

	public static String getSearchKey() {
		return searchKey;
	}

	public static void setSearchKey(String searchKey) {
		ResourceFactory.searchKey = searchKey;
	}

	public static boolean isSearchFolder() {
		return searchFolder;
	}

	public static void setSearchFolder(boolean searchFolder) {
		ResourceFactory.searchFolder = searchFolder;
	}

	public static boolean isSearchContents() {
		return searchContents;
	}

	public static void setSearchContents(boolean searchContents) {
		ResourceFactory.searchContents = searchContents;
	}

	public static boolean isCaseSensitive() {
		return caseSensitive;
	}

	public static void setCaseSensitive(boolean caseSensitive) {
		ResourceFactory.caseSensitive = caseSensitive;
	}

	public static boolean isSearchSchemaRoot() {
		return searchSchemaRoot;
	}

	public static void setSearchSchemaRoot(boolean searchSchemaRoot) {
		ResourceFactory.searchSchemaRoot = searchSchemaRoot;
	}

	public static boolean isSearchSchemaSelected() {
		return searchSchemaSelected;
	}

	public static void setSearchSchemaSelected(boolean searchSchemaSelected) {
		ResourceFactory.searchSchemaSelected = searchSchemaSelected;
	}

}
