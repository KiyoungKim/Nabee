package com.nabsys.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nabsys.common.cipher.hash.Hash;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.NabeeProtocol;
import com.nabsys.process.nabee.ResourceParameter;
import com.nabsys.resource.InstanceContext;
import com.nabsys.resource.ServerConfiguration;
import com.nabsys.resource.UserContext;

public class InstanceManager extends HashMap<String, HashMap<String, Object>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5871005784556578668L;
	
	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	private ResourceParameter rscPrm = null;
	
	public InstanceManager(ResourceParameter rscPrm)
	{
		super();
		this.rscPrm = rscPrm;
	}
	
	public void loadInstances() throws NoSuchAlgorithmException, IOException, ClassNotFoundException
	{
		ArrayList<String> instanceList = rscPrm.getInstanceList();
		
		for(int i=0;i<instanceList.size(); i++)
		{
			InstanceContext instanceContext = rscPrm.getInstanceConfiguration(instanceList.get(i));
			
			if(!instanceContext.isLoadOnStartup()) continue;
				
			String pw = Hash.getMD5Hash(Long.toString(System.currentTimeMillis()));
			UserContext instanceUser = new UserContext(instanceContext.getID(), instanceContext.getID(), Hash.getMD5Hash(pw), "Instance", "", true);
			rscPrm.addUser(instanceUser);
			
			String extParam[] =  instanceContext.getExtraLoadParams().split(" ");
			String param[] = new String[extParam.length + 8];
			param[0] = "java";
			param[1] = "-Du=" + instanceContext.getID();
			param[2] = "-Dfile.encoding=" +  instanceContext.getFileEncoding();//-Dfile.encoding=KSC5601;
			for(int j=0; j<extParam.length; j++) param[3+j] = extParam[j];
			param[3 + extParam.length + 0] = "com.nabsys.process.instance.InstanceApp";
			param[3 + extParam.length + 1] = instanceContext.getID();
			param[3 + extParam.length + 2] = ServerConfiguration.getConfigFile();
			param[3 + extParam.length + 3] = pw;
			param[3 + extParam.length + 4] = rscPrm.getConfigDBPort();
			
			ProcessBuilder processBuilder = new ProcessBuilder(param);
			Map<String, String> env = processBuilder.environment();

			env.put("CLASSPATH", instanceContext.getClassPath()+";bin/com.nabsys.sac.jar;lib/commons-net-2.0.jar;lib/commons-net-ftp-2.0.jar;lib/log4j-1.2.15.jar;lib/commons-collections-3.2.jar;lib/commons-pool-1.2.jar;lib/commons-dbcp-1.2.1.jar;lib/hsqldb-2.2.8-jdk5.jar;");
			env.put("JAVA_HOME", instanceContext.getJavaHome());
			env.put("PATH", instanceContext.getSystemPath());//JAVAHOME\bin
			env.put("LANG", instanceContext.getSystemEncoding());//LANG=ko_KR.euc_kr
			env.put("LC_ALL", instanceContext.getSystemEncoding());//LC_ALL=ko_KR.euc_kr
	
			Process process = null;
			try {
				process = processBuilder.start();

				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String errMsg = null;

				errMsg = reader.readLine();
				if(!errMsg.equals("S")) throw new IOException(errMsg);
			} catch (IOException e) {
				process.destroy();
				logger.fatal(NLabel.get(0x003E) + " [" + instanceContext.getID() + "]");
				logger.fatal(e, e.getMessage());
				continue;
			} 

			logger.info(NLabel.get(0x003D) + " [" + instanceContext.getID() + "]");
			
			NBsetProcess(instanceContext.getID(), process);
			NBsetStartTime(instanceContext.getID(), System.currentTimeMillis());
			NBsetInstancePassword(instanceContext.getID(), pw);
		}
	}
	
	public boolean loadInstance(String instanceID) throws IOException, NoSuchAlgorithmException
	{
		InstanceContext instanceContext = rscPrm.getInstanceConfiguration(instanceID);
		
		String pw = Hash.getMD5Hash(Long.toString(System.currentTimeMillis()));
		UserContext instanceUser = new UserContext(instanceID, instanceID, Hash.getMD5Hash(pw), "Instance", "", true);
		rscPrm.removeUser(instanceUser);
		rscPrm.addUser(instanceUser);
		
		String extParam[] =  instanceContext.getExtraLoadParams().split(" ");
		String param[] = new String[extParam.length + 8];
		param[0] = "java";
		param[1] = "-Du=" + instanceContext.getID();
		param[2] = "-Dfile.encoding=" +  instanceContext.getFileEncoding();//-Dfile.encoding=KSC5601;
		for(int j=0; j<extParam.length; j++) param[3+j] = extParam[j];
		param[3 + extParam.length + 0] = "com.nabsys.process.instance.InstanceApp";
		param[3 + extParam.length + 1] = instanceContext.getID();
		param[3 + extParam.length + 2] = ServerConfiguration.getConfigFile();
		param[3 + extParam.length + 3] = pw;
		param[3 + extParam.length + 4] = rscPrm.getConfigDBPort();
		
		ProcessBuilder processBuilder = new ProcessBuilder(param);
		
		Map<String, String> env = processBuilder.environment();
		
		env.put("CLASSPATH", instanceContext.getClassPath()+";bin/com.nabsys.sac.jar;lib/commons-net-2.0.jar;lib/commons-net-ftp-2.0.jar;lib/log4j-1.2.15.jar;lib/commons-collections-3.2.jar;lib/commons-pool-1.2.jar;lib/commons-dbcp-1.2.1.jar;lib/hsqldb-2.2.8-jdk5.jar;");
		env.put("JAVA_HOME", instanceContext.getJavaHome());
		env.put("PATH", instanceContext.getSystemPath());//JAVAHOME\bin
		env.put("LANG", instanceContext.getSystemEncoding());//LANG=ko_KR.euc_kr
		env.put("LC_ALL", instanceContext.getSystemEncoding());//LC_ALL=ko_KR.euc_kr

		Process process = null;
		try {
			process = processBuilder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String errMsg = reader.readLine();
			if(!errMsg.equals("S")) throw new IOException(errMsg);
		} catch (IOException e) {
			process.destroy();
			logger.fatal(NLabel.get(0x003E) + " [" + instanceID + "]");
			logger.fatal(e, e.getMessage());
			return false;
		} 

		logger.info(NLabel.get(0x003D) + " [" + instanceID + "]");
		
		NBsetProcess(instanceID, process);
		NBsetStartTime(instanceID, System.currentTimeMillis());
		NBsetInstancePassword(instanceID, pw);
		
		return true;
	}
	
	private void put(String id, String fieldName, Object value)
	{
		if(super.containsKey(id))
		{
			super.get(id).put(fieldName, value);
		}
		else
		{
			HashMap<String, Object> tmpMap = new HashMap<String, Object>();
			tmpMap.put(fieldName, value);
			super.put(id, tmpMap);
		}
	}
	
	public void NBsetStartTime(String id, long value)
	{
		put(id, "INS_S_TIME", value);
	}
	
	public void NBsetProcess(String id, Process value)
	{
		put(id, "INS_PROCESS", value);
	}
	
	public void NBsetStatus(String id, boolean value)
	{
		put(id, "INS_STAT", value);
	}
	
	public void NBsetProtocol(String id, NabeeProtocol value)
	{
		put(id, "INS_PROTOCOL", value);
	}
	
	public void NBsetInstancePassword(String id, String value)
	{
		put(id, "INS_PW", value);
	}
	
	
	
	public boolean NBgetStatus(String id)
	{
		if(!super.containsKey(id)) return false;
		if(super.get(id).get("INS_STAT") == null) return false;
		return (Boolean)super.get(id).get("INS_STAT");
	}
	
	public String NBgetInstancePassword(String id)
	{
		return (String)super.get(id).get("INS_PW");
	}
	
	public Process NBgetProcess(String id)
	{
		return (Process)super.get(id).get("INS_PROCESS");
	}
	
	public NabeeProtocol NBgetProtocol(String id)
	{
		return (NabeeProtocol)super.get(id).get("INS_PROTOCOL");
	}
	
	public synchronized void destroyInstance(String id)
	{
		try {
			((Process)NBgetProcess(id)).destroy();
			((Process)NBgetProcess(id)).waitFor();
		} catch (InterruptedException e) {
			logger.error(e, e.getMessage());
		}

		super.remove(id);
		logger.info(NLabel.get(0x003C) + " [" + id + "]");
	}
	
	public synchronized void removeInstance(String id)
	{
		try {
			((Process)NBgetProcess(id)).waitFor();
		} catch (InterruptedException e) {
			logger.error(e, e.getMessage());
		}

		super.remove(id);
		logger.info(NLabel.get(0x003C) + " [" + id + "]");
	}
	
	public InstanceContext getInstanceConfiguration(String id)
	{
		return rscPrm.getInstanceConfiguration(id);
	}
}
