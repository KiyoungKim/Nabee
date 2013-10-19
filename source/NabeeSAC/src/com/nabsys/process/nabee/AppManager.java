package com.nabsys.process.nabee;

import java.io.IOException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Statement;

import com.nabsys.common.logger.NLogger;
import com.nabsys.database.Connection;
import com.nabsys.database.DBPoolManager;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.protocol.IPC.IPCProtocol;
import com.nabsys.net.socket.client.Socket;
import com.nabsys.net.socket.server.ServerSocket;
import com.nabsys.process.InstanceHeaderFields;
import com.nabsys.process.InstanceManager;
import com.nabsys.process.exception.SequenceGenerationException;
import com.nabsys.resource.ServerConfiguration;
import com.nabsys.resource.TelegramFieldContext;

public class AppManager extends ThreadControler {
	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	String databasePort = null;
	public AppManager(String databasePort)
	{
		this.databasePort = databasePort;
	}
	
	protected void runWorker()
	{
		logger.info(0x0017);
		
		//DATABASE LOAD
		DBPoolManager configDBPool = null;
		try {
			configDBPool = (new ConfigurationInitializer()).initConfigRepository(databasePort);
		} catch (NoSuchAlgorithmException e) {
			logger.fatal(e, e.getMessage());
			System.exit(0);
		} catch (SQLException e) {
			logger.fatal(e, e.getMessage());
			System.exit(0);
		} 
				
		final ResourceParameter rscPrm = new ResourceParameter(configDBPool, databasePort);
		
		final InstanceManager instances = new InstanceManager(rscPrm);
		Thread loaderThread = new Thread(new Runnable(){
			public void run() {
				try {
					instances.loadInstances();
				} catch (NoSuchAlgorithmException e) {
					logger.fatal(e, e.getMessage());
					System.exit(0);
				} catch (IOException e) {
					logger.fatal(e, e.getMessage());
					System.exit(0);
				} catch (ClassNotFoundException e) {
					logger.fatal(e, e.getMessage());
					System.exit(0);
				}
			}
		});
		
		loaderThread.start();
		
		
		Socket socket = null;
		try {
			InstanceHeaderFields instanceHeaderFields = getInstanceHeaderFields();
			
			//LOAD SERVICE SOCKET
			serverSocket = new ServerSocket(ServerConfiguration.getServicePort()
														, ServerConfiguration.getMaxSocketBuffer()
														, ServerConfiguration.getReadTimeOut());
			
			logger.info(0x001D);
			while(!exit)
			{
				//WAITING FOR CLIENTS
				socket = serverSocket.accept();
				long sequence = 0L;
				
				try {
					sequence = getSequence(rscPrm);
				} catch (SequenceGenerationException e) {
					logger.warn(e.getMessage());
					try {
						socket.close();
					} catch (IOException e1) {
						logger.warn(e1, e1.getMessage());
					}
					
					continue;
				}
				
				IPCProtocol ipc = new IPCProtocol(socket, instanceHeaderFields, rscPrm);
				
				WorkerProcess workerProcess = new WorkerProcess(rscPrm, ipc, sequence, instances);
				Thread workerProcessThread = new Thread(workerProcess);
				workerProcessThread.start();
			}
			
		} catch (NetException e) {
			if(this.exit) logger.info(0x000A);
			else logger.fatal(e, e.getMessage());
		} catch (SocketClosedException e) {
			if(this.exit) logger.info(0x000A);
			else logger.fatal(e, e.getMessage());
		} catch (ClassNotFoundException e) {
			if(this.exit) logger.info(0x000A);
			else logger.fatal(e, e.getMessage());
		} catch (KeyException e) {
			if(this.exit) logger.info(0x000A);
			else logger.fatal(e, e.getMessage());
		}finally{
			Connection connection = null;
			try {
				connection = configDBPool.getConnection();
				Statement stmt = connection.createStatement();
				stmt.executeUpdate("SHUTDOWN");
				configDBPool.closePool();
			} catch (SQLException e) {
				if(connection != null)
				{
					try {
						connection.rollback();
					} catch (SQLException e1) {
					}
				}
				logger.error(e, e.getMessage());
			}

			if(!socketCloseCalled)
			{
				try {
					serverSocket.close();
				} catch (NetException e) {
					logger.error(e, e.getMessage());
				}
			}
			rscPrm.closeAllClients();
		}
		
		logger.info(0x0018);
	}
	
	private ServerSocket 	serverSocket 		= null;
	private boolean 		socketCloseCalled 	= false;

	public void exit()
	{
		this.exit = true;
		try {
			socketCloseCalled = true;
			this.serverSocket.close();
		} catch (NetException e) {
		}

		int tryCnt = 0;
		while(!isExit)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
			if(tryCnt >= 60) break; //1�� ���
			
			tryCnt++;
		}
	}

	private synchronized long getSequence(ResourceParameter rscPrm) throws SequenceGenerationException
	{
		if(ServerConfiguration.getMaxClientNum() < rscPrm.getAccessNumber())
			throw new SequenceGenerationException(0x001E);
		
		long seq = 0L;
		while(rscPrm.containsThreadControler(seq = System.currentTimeMillis()))
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		
		return seq;
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
