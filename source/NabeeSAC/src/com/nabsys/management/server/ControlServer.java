package com.nabsys.management.server;

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

public class ControlServer implements IManagementClass{

	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	public NBFields execute(ManagementContext context, long clientSequence) {
		
		NBFields rcvFields = context.getFields();
		
		NBFields fields = new NBFields();
		
		try {
			if((Integer)rcvFields.get(IPC.NB_MSG_TYPE) == IPC.CMD_SHUTDOWN)
			{
				logger.info(NLabel.get(0x006E) + "[By " + context.getUser() + "]");
				
				fields.put(IPC.NB_MSG_TYPE, IPC.CMD_SHUTDOWN);
				
				context.getResourceParameters().getThreadControler(clientSequence).getProtocol()._writePacket(fields);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				
				context.getResourceParameters().removeThreadControler(clientSequence, false);
				
				System.exit(0);
			}
			else if((Integer)rcvFields.get(IPC.NB_MSG_TYPE) == IPC.CMD_REFRESH)
			{
				logger.info(NLabel.get(0x006F) + "[By " + context.getUser() + "]");

			}
		} catch (SocketClosedException e) {
			logger.error(e, e.getMessage());
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", e.getMessage());
			return fields;
		} catch (TimeoutException e) {
			logger.error(e, e.getMessage());
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", e.getMessage());
			return fields;
		} catch (NetException e) {
			logger.error(e, e.getMessage());
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", e.getMessage());
			return fields;
		} catch (UnsupportedEncodingException e) {
			logger.error(e, e.getMessage());
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", e.getMessage());
			return fields;
		} catch (ProtocolException e) {
			logger.error(e, e.getMessage());
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", e.getMessage());
			return fields;
		} catch (DataTypeException e) {
			logger.error(e, e.getMessage());
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", e.getMessage());
			return fields;
		}catch (NullPointerException e) {
			logger.error(e, "Null exception.");
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", "System error occurred.");
			return fields;
		} catch (Exception e) {
			logger.error(e, e.getMessage());
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", e.getMessage());
			return fields;
		}
		
		return null;
	}

}
