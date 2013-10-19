package com.nabsys.process.instance.management;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.Context;
import com.nabsys.process.ResourceFactory;
import com.nabsys.process.SessionData;
import com.nabsys.resource.ServiceContext;
import com.nabsys.resource.ServiceDesign;

public class ServiceTester {
	private final NLogger logger = NLogger.getLogger(this.getClass());
	private ResourceFactory resourceFactory = null;
	
	public ServiceTester(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	
	public NBFields execute(NBFields fromClient)
	{
		NBFields toClient = new NBFields();
		toClient.put(IPC.NB_MGR_SQNC, (Long)fromClient.get(IPC.NB_MGR_SQNC));
		toClient.put("_STATUS_", "E");
		if(fromClient.get("CMD_CODE").equals("E"))  
		{
			toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_EXEC_SVC);
			try {
				if(fromClient.get("_STATUS_").equals("S"))
				{
					ServiceContext service = resourceFactory.getService((String)fromClient.get("_SERVICE_ID"));
					if(service == null)
					{
						toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
						toClient.put("RTN_MSG", NLabel.get(0x010D));
						return toClient;
					}
					
					if(service.getType().equals("MessageQueue"))
					{
						toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
						toClient.put("RTN_MSG", NLabel.get(0x010C));
						return toClient;
					}
					final Context ctx = new Context(resourceFactory);
					ctx.setPlugins(resourceFactory.getPluginList());
					ctx.setSessionData(new SessionData());
					ctx.setServiceID((String)fromClient.get("_SERVICE_ID"));
					ctx.initTestMessageQueue();
					ctx.setTest();
					
					final ServiceDesign serviceDesign = service.getServiceDesign();
					final HashMap<String, Object> map = setMap(fromClient);
					
					Thread t = new Thread(){
						public void run(){
							try {
								serviceDesign.execute(ctx, map);
							} catch (Exception e) {
							}
						}
					};
					t.start();
					
					
					TestMessageQueue tmq = ctx.getTestMessage();
					if(tmq == null)
					{
						toClient.put("_STATUS_", "E");
						toClient.put("_STD_ERR_", "Terminate testing service.");
						ctx.clearTestMessageQueue();
						return toClient;
					}
					
					toClient.put("_OBJ_ID_", tmq.getHandlerID());
					if(tmq.isErr())
						toClient.put("_STD_ERR_", tmq.getMessage());
					else
						toClient.put("_STD_OUT_", tmq.getMessage());
					
					toClient.put("_STATUS_", "N");
					resourceFactory.setServiceContext((Long)toClient.get(IPC.NB_MGR_SQNC), ctx);
				}
				else if(fromClient.get("_STATUS_").equals("N"))
				{
					Context ctx = resourceFactory.getServiceContext((Long)toClient.get(IPC.NB_MGR_SQNC));
					if(ctx == null)
					{
						toClient.put("_STATUS_", "E");
						toClient.put("_STD_ERR_", "Context is null");
						return toClient;
					}
					TestMessageQueue tmq = ctx.getTestMessage();
					if(tmq == null)
					{
						toClient.put("_STATUS_", "E");
						toClient.put("_STD_ERR_", "Terminate testing service.");
						ctx.clearTestMessageQueue();
						resourceFactory.removeServiceContext((Long)toClient.get(IPC.NB_MGR_SQNC));
						return toClient;
					}
					
					toClient.put("_OBJ_ID_", tmq.getHandlerID());
					if(tmq.isErr())
						toClient.put("_STD_ERR_", tmq.getMessage());
					else
						toClient.put("_STD_OUT_", tmq.getMessage());

					toClient.put("_STATUS_", "N");
				}
				else
				{
					toClient.put("_STATUS_", "E");
					resourceFactory.removeServiceContext((Long)toClient.get(IPC.NB_MGR_SQNC));
					toClient.remove(IPC.NB_MGR_SQNC);
					return toClient;
				}
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
				resourceFactory.removeServiceContext((Long)toClient.get(IPC.NB_MGR_SQNC));
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
				resourceFactory.removeServiceContext((Long)toClient.get(IPC.NB_MGR_SQNC));
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
				resourceFactory.removeServiceContext((Long)toClient.get(IPC.NB_MGR_SQNC));
			} catch (NullPointerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Null Exception");
				resourceFactory.removeServiceContext((Long)toClient.get(IPC.NB_MGR_SQNC));
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
				resourceFactory.removeServiceContext((Long)toClient.get(IPC.NB_MGR_SQNC));
			} finally {
			
			}
		}
		
		return toClient;
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> setMap(NBFields fields){
		HashMap<String, Object> map = new HashMap<String, Object>();
		Iterator<String> itr = fields.keySet().iterator();
		while(itr.hasNext()){
			String key = itr.next();
			
			if(key.equals("_SERVICE_ID") 
					|| key.equals(IPC.NB_MGR_SQNC)
					|| key.equals(IPC.NB_LOAD_CLASS) 
					|| key.equals(IPC.NB_INSTNCE_ID)
					|| key.equals(IPC.NB_MSG_TYPE)
					|| key.equals(IPC.NB_MSG_LENGTH)
					|| key.equals(IPC.NB_MSG_RETURN)
					|| key.equals("CMD_CODE")
					|| key.equals("_STATUS_")) continue;
			
			if((fields.get(key) instanceof ArrayList))
			{
				ArrayList<Object> list = new ArrayList<Object>();
				ArrayList<NBFields> listFields = (ArrayList<NBFields>)fields.get(key);
				
				for(int i=0; i<listFields.size(); i++)
				{
					NBFields fieldMap = (NBFields)listFields.get(i);
					list.add(setMap(fieldMap));
				}
				
				map.put(key, list);
			}
			else
			{
				map.put(key, fields.get(key));
			}
		}
		
		return map;
	}
}
