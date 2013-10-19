package com.nabsys.process.instance.messagequeue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.nabsys.common.fileio.ObjectFileIO;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.DateUtil;
import com.nabsys.process.Context;
import com.nabsys.process.ResourceFactory;
import com.nabsys.process.instance.InstanceStatic;
import com.nabsys.process.instance.InstanceThread;
import com.nabsys.resource.ServerConfiguration;
import com.nabsys.resource.ServiceContextList;

public class MessageQueueManager extends InstanceThread{

	private final NLogger logger = NLogger.getLogger(this.getClass());

	private HashMap<String, String> 	workingQueue 	= null;
	
    private ResourceFactory				resourceFactory = null;
    
    public MessageQueueManager(ResourceFactory resourceFactory)
    {
    	super();
    	this.resourceFactory = resourceFactory;
    }
    
    private void backupQueueFile(String queueFileName, String backupFileName)
    {
    	
    	File backupFile = new File(backupFileName);
    	File queueFile = new File(queueFileName);
    	queueFile.renameTo(backupFile);
    }
    
	@Override
	protected void runWorker() {
		logger.info(0x0063);
		
		String instanceName = InstanceStatic.getInstanceName();
		String queueFileName = "data/" + instanceName + "QUE.DAT";
		File queueFile = new File(queueFileName);
		
		QueueFactory queueFactory = null;
		
		if(queueFile.exists())
		{
			DateUtil du = new DateUtil(ServerConfiguration.getTimeLocale());
			String backupFile = queueFileName + "." + du.getCurrentDate("yyyyMMddHHmmss") + ".BAK";
			
			ObjectFileIO objIO = new ObjectFileIO();
			try {
				queueFactory = (QueueFactory) objIO.recoverObject(queueFileName);
				objIO.saveObject(queueFileName, new QueueFactory());
				
			} catch (IOException e) {
				logger.error(e, 0x0065);
				logger.info(NLabel.get(0x0070) + "[" + backupFile + "]");
				backupQueueFile(queueFileName, backupFile);
				queueFactory = new QueueFactory();
			} catch (ClassNotFoundException e) {
				logger.error(e, 0x0065);
				logger.info(NLabel.get(0x0070) + "[" + backupFile + "]");
				backupQueueFile(queueFileName, backupFile);
				queueFactory = new QueueFactory();
			} catch (Exception e) {
				logger.error(e, 0x0065);
				logger.info(NLabel.get(0x0070) + "[" + backupFile + "]");
				backupQueueFile(queueFileName, backupFile);
				queueFactory = new QueueFactory();
			}
		}
		else
		{
			queueFactory = new QueueFactory();
		}
		try {
			ServiceContextList serviceList = resourceFactory.getMessageQueueServiceList();
			workingQueue = new HashMap<String, String>();
			
			ArrayList<String> removeList = new ArrayList<String>();
			
			Set<String> keySet = queueFactory.keySet();
			Iterator<String> itr = keySet.iterator();
			while(itr.hasNext())
			{
				String key = itr.next();
				
				if(serviceList.containsKey(key))
				{
					MessageQueue queue = queueFactory.get(key);
					Context ctx = new Context(resourceFactory);
					ctx.setData("__MESSAGE_QUEUE", queue);
					ctx.setData("__CALL_BY_MANAGER", true);
					ctx.setServiceID(key);
					ctx.setPlugins(resourceFactory.getPluginList());
					serviceList.get(key).getServiceDesign().setWorkingQueue(workingQueue);
					serviceList.get(key).getServiceDesign().execute(ctx, null);
				}
				else
				{
					removeList.add(key);
				}
			}
			
			for(int i=0; i<removeList.size(); i++)
			{
				queueFactory.remove(removeList.get(i));
			}
			
			itr = serviceList.keySet().iterator();
			while(itr.hasNext())
			{
				String key = itr.next();
				if(!queueFactory.containsKey(key))
				{
					int size = serviceList.get(key).getServiceDesign().getMessageQueueSize();
					MessageQueue queue = new MessageQueue(size, resourceFactory);
					Context ctx = new Context(resourceFactory);
					ctx.setData("__MESSAGE_QUEUE", queue);
					ctx.setData("__CALL_BY_MANAGER", true);
					ctx.setServiceID(key);
					ctx.setPlugins(resourceFactory.getPluginList());
					serviceList.get(key).getServiceDesign().setWorkingQueue(workingQueue);
					serviceList.get(key).getServiceDesign().execute(ctx, null);
					queueFactory.put(key, queue);
				}
			}
			
			resourceFactory.setQueueFactory(queueFactory);
			
			while(!exit)
			{
				ServiceContextList newServiceList = resourceFactory.getMessageQueueServiceList();
				itr = newServiceList.keySet().iterator();
				while(itr.hasNext())
				{
					String key = itr.next();
					if(!serviceList.containsKey(key))
					{
						serviceList.put(key, newServiceList.get(key));
					}
				}
				itr = serviceList.keySet().iterator();
				removeList = new ArrayList<String>();
				while(itr.hasNext())
				{
					String key = itr.next();
					if(!newServiceList.containsKey(key))
					{
						serviceList.get(key).getServiceDesign().shutdown();
						queueFactory.remove(key);
						removeList.add(key);
					}
				}
				
				for(int i=0; i<removeList.size(); i++) serviceList.remove(removeList.get(i));
				
				
				itr = serviceList.keySet().iterator();
				while(itr.hasNext())
				{
					String key = itr.next();
					if(!queueFactory.containsKey(key))
					{
						int size = serviceList.get(key).getServiceDesign().getMessageQueueSize();
						MessageQueue queue = new MessageQueue(size, resourceFactory);
						Context ctx = new Context(resourceFactory);
						ctx.setData("__MESSAGE_QUEUE", queue);
						ctx.setData("__CALL_BY_MANAGER", true);
						ctx.setServiceID(key);
						ctx.setPlugins(resourceFactory.getPluginList());
						serviceList.get(key).getServiceDesign().setWorkingQueue(workingQueue);
						serviceList.get(key).getServiceDesign().execute(ctx, null);
						queueFactory.put(key, queue);
					}
					else
					{
						MessageQueue queue = queueFactory.get(key);
						Context ctx = new Context(resourceFactory);
						ctx.setData("__MESSAGE_QUEUE", queue);
						ctx.setData("__CALL_BY_MANAGER", true);
						ctx.setServiceID(key);
						ctx.setPlugins(resourceFactory.getPluginList());
						serviceList.get(key).getServiceDesign().setWorkingQueue(workingQueue);
						serviceList.get(key).getServiceDesign().execute(ctx, null);
					}
				}
				
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e) {
				}
			}
			
			itr = serviceList.keySet().iterator();
			while(itr.hasNext())
			{
				String key = itr.next();
				serviceList.get(key).getServiceDesign().shutdown();
			}
		} catch (SQLException ex) {
			logger.fatal(ex, 0x0065);
			return;
		} catch (IOException ex) {
			logger.fatal(ex, 0x0065);
			return;
		} catch (ClassNotFoundException ex) {
			logger.fatal(ex, 0x0065);
			return;
		} catch (Exception ex) {
			logger.fatal(ex, 0x0065);
			return;
		}
		
		finalize(queueFactory, queueFileName);
		
		logger.info(0x0064);
	}
	
	private boolean isShutdown = false;
	
	public boolean isShutdown()
	{
		return this.isShutdown;
	}
	
	public void exit()
	{
		this.exit = true;
		this.interrupt();
		this.isShutdown = true;
	}
	
	private void finalize(QueueFactory queueFactory, String queueFileName)
	{
		try{
			while(workingQueue.size() > 0)
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}

			Iterator<String> itr = queueFactory.keySet().iterator();
			ArrayList<String> removeList = new ArrayList<String>();
			while(itr.hasNext())
			{
				String key = itr.next();

				MessageQueue queue = queueFactory.get(key);

				if(queue.isEmpty())
				{
					removeList.add(key);
				}
			}

			for(int i=0; i<removeList.size(); i++)
			{
				queueFactory.remove(removeList.get(i));
			}
		} finally {
			ObjectFileIO objIO = new ObjectFileIO();
			try {
				objIO.saveObject(queueFileName, queueFactory);
			} catch (NullPointerException e){
				logger.error(0x0066);
				logger.error(e, e.getMessage());
			} catch (IOException e) {
				logger.error(0x0066);
				logger.error(e, e.getMessage());
			} catch (Exception e) {
				logger.error(0x0066);
				logger.error(e, e.getMessage());
			}
		}
	}
}
