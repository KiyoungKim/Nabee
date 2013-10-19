package com.nabsys.process.instance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.net.protocol.IPC.IPCProtocol;
import com.nabsys.net.socket.channel.AsyncServerChannelFactory;
import com.nabsys.net.socket.channel.Channel;
import com.nabsys.net.socket.channel.IChannelFactory;
import com.nabsys.net.socket.channel.ServerSocketConstructor;
import com.nabsys.net.socket.client.Socket;
import com.nabsys.process.InstanceHeaderFields;
import com.nabsys.process.ResourceFactory;
import com.nabsys.process.instance.batch.BatchManager;
import com.nabsys.process.instance.management.ServiceTester;
import com.nabsys.process.instance.management.SqlManagement;
import com.nabsys.process.instance.messagequeue.MessageQueueManager;
import com.nabsys.process.instance.online.handler.OnlineHandlerChainFactory;
import com.nabsys.resource.InstanceContext;
import com.nabsys.resource.ServerConfiguration;
import com.nabsys.resource.TelegramFieldContext;

public class InternalCommunicator extends InstanceThread{
	
	private final NLogger logger = NLogger.getLogger(this.getClass());
	private ResourceFactory resourceFactory = null;
	public InternalCommunicator(ResourceFactory resourceFactory)
	{
		super();
		this.resourceFactory = resourceFactory;
	}
	
	protected ResourceFactory getResourceFactory()
	{
		return resourceFactory;
	}
	
	@Override
	protected void runWorker() {
		logger.info(0x006B);
		//REPORT ALIVE
		Socket 				socket 					= null;
		IPCProtocol 		protocol 				= null;
		BatchManager		batchManager			= null;
		MessageQueueManager messageQueueManager 	= null;
		
		boolean isConnected = false;
		int tryCnt = 0;
		while(!isConnected)
		{
			try {
				socket = new Socket("localhost", 
						ServerConfiguration.getServicePort(), 
						ServerConfiguration.getMaxSocketBuffer(), 
						0);
				
				isConnected = true;
			} catch (NetException e) {
				if(tryCnt > 20){
					logger.error(e, e.getMessage());
					InstanceRuntime.exit();
				}
			} catch (SocketClosedException e) {
				if(tryCnt > 20){
					logger.error(e, e.getMessage());
					InstanceRuntime.exit();
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
			tryCnt ++;
		}
		
		try {
			protocol = new IPCProtocol(socket
							, InstanceStatic.getInstanceName()
							, InstanceStatic.getServerLoginPW()
							, getInstanceHeaderFields()
							, ServerConfiguration.getServerEncoding());

			NBFields fields = new NBFields();
			fields.put(IPC.NB_MSG_TYPE, IPC.CMD_IPC);
			fields.put(IPC.NB_INSTANCE_NM, InstanceStatic.getInstanceName());
					
			fields = protocol.execute(fields);
					
			if((Integer)fields.get(IPC.NB_MSG_RETURN) != IPC.SUCCESS)
			{
				logger.fatal(0x0042);
				logger.info(0x0040);
				try {
					protocol._close();
				} catch (IOException e) {
					logger.error(e, e.getMessage());
				}
				InstanceRuntime.exit();
			}
			
			//SERVICE WORKER INVOKE
			messageQueueManager = new MessageQueueManager(resourceFactory);
			messageQueueManager.start();
			InstanceRuntime.addShutdownHook(new LifeCycleController(messageQueueManager));
			
			////////////////////////Channel Generate///////////////
			InstanceContext instanceContext = resourceFactory.getInstanceConfiguration();
			IChannelFactory channelFactory = new AsyncServerChannelFactory(instanceContext.getMaxClients());
			
			ServerSocketConstructor socketConstructor = new ServerSocketConstructor(channelFactory);
			socketConstructor.setHandlerChainFactory(new OnlineHandlerChainFactory(resourceFactory));
			
			Channel channel = socketConstructor.bind(new InetSocketAddress(instanceContext.getServicePort()));
			channel.setMaxBufferSize(instanceContext.getBufferSize());
			logger.info(NLabel.get(0x0068));
			InstanceRuntime.addShutdownHook(new LifeCycleController(socketConstructor, channel));
			////////////////////////Channel Generate///////////////
			
			batchManager = new BatchManager(resourceFactory);
			batchManager.start();
			InstanceRuntime.addShutdownHook(new LifeCycleController(batchManager));
			
			if(!channel.isBound())
			{
				logger.fatal(0x0041);
				logger.info(0x0040);
				try {
					protocol._close();
				} catch (IOException e) {
				}
				InstanceRuntime.exit();
			}
			
			logger.info(NLabel.get(0x010B));
			
			while(!exit)
			{
				fields = protocol._readPacket();
				
				long requestManagerSequence = -1;
				
				if(fields.containsKey(IPC.NB_MGR_SQNC))
				{
					try{
						switch((Integer)fields.get(IPC.NB_MSG_TYPE))
						{
						case IPC.CMD_RPT_ALV:
							requestManagerSequence = (Long)fields.get(IPC.NB_MGR_SQNC);
	
							fields = new NBFields();
							fields.put(IPC.NB_MGR_SQNC, requestManagerSequence);
							fields.put(IPC.NB_MSG_TYPE, IPC.CMD_RPT_ALV);
							
							protocol._writePacket(fields);
							break;
						case IPC.CMD_SHUTDOWN:
							requestManagerSequence = (Long)fields.get(IPC.NB_MGR_SQNC);
							
							fields = new NBFields();
							fields.put(IPC.NB_MGR_SQNC, requestManagerSequence);
							fields.put(IPC.NB_MSG_TYPE, IPC.CMD_SHUTDOWN);
							
							protocol._writePacket(fields);
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
							}
							
							InstanceRuntime.exit();
							
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
							}
	
							break;
						case IPC.CMD_SQL_UPDATE:
							SqlManagement sqlManagement = new SqlManagement(resourceFactory);
							fields = sqlManagement.execute(fields);
							protocol._writePacket(fields);
							break;
						case IPC.CMD_TLGM_UPDATE:
							//TelegramManagement telegramManagement = new TelegramManagement(resourceFactory);
							//fields = telegramManagement.execute(fields);
							//protocol._writePacket(fields);
							break;
						case IPC.CMD_COMP_UPDATE:
							//ComponentManagement compManagement = new ComponentManagement(resourceFactory);
							//fields = compManagement.execute(fields);
							//protocol._writePacket(fields);
							break;
						case IPC.CMD_SVC_UPDATE:
							//ServiceManagement svcManagement = new ServiceManagement(resourceFactory);
							//fields = svcManagement.execute(fields);
							//protocol._writePacket(fields);
							break;
						case IPC.CMD_EXEC_SQL:
							sqlManagement = new SqlManagement(resourceFactory);
							NBFields sqlRtnFields = sqlManagement.execute(fields);
							protocol._writePacket(sqlRtnFields);
							break;
						case IPC.CMD_EXEC_SVC:
							ServiceTester serviceTester = new ServiceTester(resourceFactory);
							NBFields svcRtnFields = serviceTester.execute(fields);
							protocol._writePacket(svcRtnFields);
							break;
						default:
						}
					} catch (NullPointerException e) {
						logger.error(e, "Null Exception");
					}
				}
			}

		} catch (NetException e) {
			logger.fatal(e, e.getMessage());
		} catch (SocketClosedException e) {
		} catch (ClassNotFoundException e) {
			logger.fatal(e, e.getMessage());
		} catch (TimeoutException e) {
			logger.fatal(e, e.getMessage());
		} catch (DataTypeException e) {
			logger.fatal(e, e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.fatal(e, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			logger.fatal(e, e.getMessage());
		} catch (KeyException e) {
			logger.fatal(e, e.getMessage());
		} catch (ProtocolException e) {
			logger.fatal(e, e.getMessage());
		} catch (Exception e) {
			logger.fatal(e, e.getMessage());
		} finally {
			logger.info(0x006A);
			InstanceRuntime.exit();
		}
	}
	
	
	
	private InstanceHeaderFields getInstanceHeaderFields() throws KeyException
	{
		InstanceHeaderFields fields = new InstanceHeaderFields();
		fields.put("LENGTH", new TelegramFieldContext("LENGTH", "LENGTH", 0, 4, true, ' ', ' ', 'I', ""));
		fields.put("MSG_TYPE", new TelegramFieldContext("MSG_TYPE", "MSG_TYPE", 1, 4, true, ' ', ' ', 'I', ""));
		fields.put("RETURN", new TelegramFieldContext("RETURN", "RETURN", 2, 4, true, ' ', ' ', 'I', ""));
		
		fields.setLengthFieldIndex("LENGTH");
		fields.setMsgTypeFieldIndex("MSG_TYPE");
		fields.setReturnFieldIndex("RETURN");
		
		return fields;
	}

}
