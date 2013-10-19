package com.nabsys.process.instance.management;

import java.sql.SQLException;
import java.util.ArrayList;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.SimpleQuery;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.ResourceFactory;

public class SqlManagement {
	
	private final NLogger logger = NLogger.getLogger(this.getClass());

	private ResourceFactory resourceFactory = null;
	
	public SqlManagement(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	
	@SuppressWarnings("unchecked")
	public NBFields execute(NBFields fromClient)
	{
		NBFields toClient = new NBFields();
		toClient.put(IPC.NB_MGR_SQNC, (Long)fromClient.get(IPC.NB_MGR_SQNC));
		
		if(fromClient.get("CMD_CODE").equals("E"))  
		{
			toClient.put(IPC.NB_MSG_TYPE, IPC.CMD_EXEC_SQL);
			SimpleQuery query = null;
			try {
				ArrayList<NBFields> paramList = (ArrayList<NBFields>)fromClient.get("PARAM");
				NBFields param = new NBFields();
				if(paramList.size() > 0)
					param = ((ArrayList<NBFields>)fromClient.get("PARAM")).get(0);

				query = new SimpleQuery(resourceFactory);
				query.setConnection((String)fromClient.get("PLUGIN"));
				
				toClient.putAll(query.execute((String)fromClient.get("URL"), param));
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NullPointerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Null Exception");
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} finally {
				try {
					query.close();
				} catch (SQLException e) {
					logger.error(e, e.getMessage());
				} catch (NullPointerException e) {
				}
			}
		}

		return toClient;
	}
}
