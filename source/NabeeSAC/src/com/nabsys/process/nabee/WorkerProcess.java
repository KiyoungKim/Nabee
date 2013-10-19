package com.nabsys.process.nabee;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.ClosedSelectorException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.CustomLoader;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.NabeeProtocol;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.InstanceManager;
import com.nabsys.process.ManagementContext;

public class WorkerProcess extends ThreadControler {
	
	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	private InstanceManager instances = null;
	
	public WorkerProcess(ResourceParameter rscPrm, NabeeProtocol protocol, long sequence, InstanceManager instances) throws ClassNotFoundException
	{
		super(rscPrm, protocol, sequence);
		this.instances = instances;
	}
	
	@Override
	protected void runWorker() {
		String peerAddress = getProtocol().getPeerAddress();
		
		logger.info(NLabel.get(0x0020) + " (Access address : "+ peerAddress + ")");
		
		boolean isInstance = false;
		String instanceID = "";
		
		try {
			ManagementContext context = new ManagementContext();
			
			while(!exit)
			{
				try {
					NBFields fields = getProtocol()._readPacket();
					
					if((Integer)fields.get(IPC.NB_MSG_TYPE) == IPC.CMD_IPC)
					{
						instanceID = (String)fields.get(IPC.NB_INSTANCE_NM);
						isInstance = true;
						
						fields = new NBFields();
						fields.put(IPC.NB_MSG_TYPE, IPC.CMD_RPT_ALV);
						getProtocol()._writePacket(fields);
						
						break;
					}
					
					if((Integer)fields.get(IPC.NB_MSG_TYPE) == IPC.CMD_ALIVE)
						continue;
					
					context.setFields(fields);
					context.setInstances(instances);
					context.setUser(getProtocol().getUser());
					context.setUserAuthority(getProtocol().getUserAuthority());
					context.setClientAddress(peerAddress);
					if(fields.containsKey(IPC.NB_INSTNCE_ID))
					{
						if(((String)fields.get(IPC.NB_INSTNCE_ID)).equals("ALL"))
							context.setInstanceID("");
						else
							context.setInstanceID((String)fields.get(IPC.NB_INSTNCE_ID));
					}

					context.setResourceParameters(getResourceParameter());
					
					CustomLoader cl = new CustomLoader();
					Class<?> tmpClass;

					tmpClass = cl.getCustomClass(getProtocol()._getLoadClass(fields));
					
					Constructor<?> constructor = tmpClass.getConstructor(new Class[]{});
					
					Object obj = constructor.newInstance();
					IManagementClass processObj = (IManagementClass) obj;
					
					try{
						fields = processObj.execute(context, getSequence());
					}catch(NullPointerException ex){
						if(fields != null)
						{
							fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
							fields.put("RTN_MSG", "System error occurred.");
						}
					}catch(Exception ex){
						if(fields != null)
						{
							fields.put(IPC.NB_MSG_RETURN, IPC.FAIL);
							fields.put("RTN_MSG", "System error occurred.");
						}
					}

					if(fields != null)
					{
						getProtocol()._writePacket(fields);
					}

				} catch (NetException e) {
					if(!exit) logger.error(e, e.getMessage());
					throw e; 
				} catch (ClosedSelectorException e) {
					if(!exit) logger.error(e, e.getMessage());
					throw e; 
				} catch (TimeoutException e) {
					logger.warn(e, e.getMessage());
				} catch (SocketClosedException e) {
					if(!exit) logger.info(e.getMessage());
					throw e; 
				} catch (UnsupportedEncodingException e) {
					if(!exit) logger.error(e, e.getMessage());
					throw e; 
				} catch (DataTypeException e) {
					logger.warn(e, e.getMessage());
				} catch (NoSuchAlgorithmException e) {
					logger.warn(e, e.getMessage());
				} catch (IOException e) {
					logger.warn(e, e.getMessage());
				} catch (SecurityException e) {
					logger.warn(e, e.getMessage());
				} catch (NoSuchMethodException e) {
					logger.warn(e, e.getMessage());
				} catch (IllegalArgumentException e) {
					logger.warn(e, e.getMessage());
				} catch (IllegalAccessException e) {
					logger.warn(e, e.getMessage());
				} catch (InvocationTargetException e) {
					logger.warn(e, e.getMessage());
				} catch (InstantiationException e) {
					logger.warn(e, e.getMessage());
				} catch (NullPointerException e) {
					logger.error(e, e.getMessage());
				}
			}
		} catch (ClosedSelectorException e) {
			clearTestProcess();
		} catch (NetException e) {
			clearTestProcess();
		} catch (SocketClosedException e) {
			clearTestProcess();
		} catch (UnsupportedEncodingException e) {
			clearTestProcess();
		}
		
		//IPC
		if(isInstance)
		{
			setInstanceName(instanceID);
			getProtocol().setReadTimeout(0);
			
			instances.NBsetStartTime(instanceID, System.currentTimeMillis());
			instances.NBsetStatus(instanceID, true);
			instances.NBsetProtocol(instanceID, getProtocol());
			
			logger.info(NLabel.get(0x003B) + " [" + instanceID + "]");
			
			boolean isInstanceShutdown = false;
			try{
				while(!exit)
				{
					try {
						isInstanceShutdown = false;
						NBFields fields = getProtocol()._readPacket();

						if(fields.containsKey(IPC.NB_MGR_SQNC))
						{
							long managerSequence = (Long)fields.get(IPC.NB_MGR_SQNC);
							fields.remove(IPC.NB_MGR_SQNC);

							//INSTANCE SHUTDOWN
							if((Integer)fields.get(IPC.NB_MSG_TYPE) == IPC.CMD_SHUTDOWN)
							{
								isInstanceShutdown = true;
								instances.removeInstance(instanceID);
								instances.NBsetStatus(instanceID, false);
								fields.remove(IPC.NB_MSG_TYPE);
								
								getResourceParameter().getThreadControler(managerSequence).getProtocol()._writePacket(fields);
								break;
							}
							else
							{
								fields.remove(IPC.NB_MSG_TYPE);
								getResourceParameter().getThreadControler(managerSequence).getProtocol()._writePacket(fields);
							}
						}
					} catch (SocketClosedException e) {
						
						if(exit) throw e;
						else logger.error(e, e.getMessage());
						
						try {
							instances.destroyInstance(instanceID);
							instances.loadInstance(instanceID);
							
							break;
						} catch (IOException e1) {
							logger.error(e1, NLabel.get(0x003E) + " [" + instanceID + "]");
						} catch (NoSuchAlgorithmException e1) {
							logger.error(e1, NLabel.get(0x003E) + " [" + instanceID + "]");
						}
					} catch (ClosedSelectorException e) {
						
						if(exit) throw e;
						else logger.error(e, e.getMessage());
						
						try {
							instances.destroyInstance(instanceID);
							instances.loadInstance(instanceID);
							
							break;
						} catch (IOException e1) {
							logger.error(e1, NLabel.get(0x003E) + " [" + instanceID + "]");
						} catch (NoSuchAlgorithmException e1) {
							logger.error(e1, NLabel.get(0x003E) + " [" + instanceID + "]");
						}
					} catch (TimeoutException e) {
						logger.warn(e, e.getMessage());
					} catch (NetException e) {
						if(exit) throw e;
						else logger.error(e, e.getMessage());
						
						try {
							instances.destroyInstance(instanceID);
							instances.loadInstance(instanceID);
							
							break;
						} catch (IOException e1) {
							logger.error(e1, NLabel.get(0x003E) + " [" + instanceID + "]");
						} catch (NoSuchAlgorithmException e1) {
							logger.error(e1, NLabel.get(0x003E) + " [" + instanceID + "]");
						}
					} catch (UnsupportedEncodingException e) {
						if(exit) throw e;
						else logger.error(e, e.getMessage());
						
						try {
							instances.destroyInstance(instanceID);
							instances.loadInstance(instanceID);
							
							break;
						} catch (IOException e1) {
							logger.error(e1, NLabel.get(0x003E) + " [" + instanceID + "]");
						} catch (NoSuchAlgorithmException e1) {
							logger.error(e1, NLabel.get(0x003E) + " [" + instanceID + "]");
						}
					} catch (NoSuchAlgorithmException e) {
						logger.warn(e, e.getMessage());
					} catch (DataTypeException e) {
						logger.warn(e, e.getMessage());
					} catch (ProtocolException e) {
						logger.warn(e, e.getMessage());
					}

				}
			} catch (SocketClosedException e) {
			} catch (ClosedSelectorException e) {
			} catch (NetException e) {
			} catch (UnsupportedEncodingException e) {
			} finally {
				if(!isInstanceShutdown)
				{
					instances.removeInstance(instanceID);
					instances.NBsetStatus(instanceID, false);
				}
			}
		}
		
		logger.info(NLabel.get(0x0021) + " [" + peerAddress + "]");
	}
	
	private void clearTestProcess()
	{
		Iterator<String> itr = instances.keySet().iterator();
		NBFields params = new NBFields();
		params.put(IPC.NB_MGR_SQNC, getSequence());
		params.put("CMD_CODE", "E");
		params.put("_STATUS_", "T");
		params.put(IPC.NB_MSG_TYPE, IPC.CMD_EXEC_SVC);
		
		while(itr.hasNext())
		{
			String key = itr.next();
			try {
				instances.NBgetProtocol(key)._writePacket(params);
			} catch (Exception e) {
			}
		}
	}
}
