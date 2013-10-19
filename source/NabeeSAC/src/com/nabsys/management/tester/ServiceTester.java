package com.nabsys.management.tester;

import java.io.UnsupportedEncodingException;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.ManagementContext;

public class ServiceTester implements IManagementClass{

	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	public NBFields execute(ManagementContext context, long clientSequence) {
		NBFields fromClient = context.getFields();
		NBFields toClient = new NBFields();
		
		if(fromClient.get("CMD_CODE").equals("E"))
		{
			if(!context.getInstances().NBgetStatus(context.getInstanceID()))
			{
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Instance is not running.");
				return toClient;
			}
			
			fromClient.put(IPC.NB_MSG_TYPE, IPC.CMD_EXEC_SVC);
			fromClient.put(IPC.NB_MGR_SQNC, clientSequence);
			
			try {
				if(fromClient.get("_STATUS_").equals("S"))
				{
					logger.info(NLabel.get(0x008E) + " [" + context.getInstanceID() + "] " + fromClient.get("_SERVICE_ID"));
					context.getInstances().NBgetProtocol(context.getInstanceID())._writePacket(fromClient);
					return null;
				}
				else if(fromClient.get("_STATUS_").equals("N"))
				{
					context.getInstances().NBgetProtocol(context.getInstanceID())._writePacket(fromClient);
					return null;
				}
			} catch (SocketClosedException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (TimeoutException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NetException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (UnsupportedEncodingException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (ProtocolException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (DataTypeException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		
		return toClient;
	}
	

}
