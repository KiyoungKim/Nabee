package com.nabsys.management.instance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.NabeeProtocol;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.ManagementContext;

public class LoadInstance implements IManagementClass{

	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	public NBFields execute(ManagementContext context, long clientSequence) {
		
		logger.info(NLabel.get(0x006D) + "[" + context.getInstanceID() + "]");
		
		NBFields fields = new NBFields();
		
		try {
			if(!context.getInstances().NBgetStatus(context.getInstanceID()))
				context.getInstances().loadInstance(context.getInstanceID());
			
			NabeeProtocol protocol = null;
			
			for(int i=0; i<100; i++)
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				
				if((protocol = context.getInstances().NBgetProtocol(context.getInstanceID())) != null)
				{
					fields.put(IPC.NB_MSG_TYPE, IPC.CMD_RPT_ALV);
					fields.put(IPC.NB_MGR_SQNC, clientSequence);
					
					protocol._writePacket(fields);
					break;
				}
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
		} catch (DataTypeException e) {
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
		} catch (NoSuchAlgorithmException e) {
			logger.error(e, e.getMessage());
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", e.getMessage());
			return fields;
		} catch (IOException e) {
			logger.error(e, e.getMessage());
			
			fields = new NBFields();
			fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			fields.put("RTN_MSG", e.getMessage());
			return fields;
		} catch (NullPointerException e) {
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
		
		//NULL 은 INSTANCE 에서 처리결과를 Return 하는 케이스
		
		return null;
	}

}
