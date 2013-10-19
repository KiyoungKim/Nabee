package com.nabsys.management.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.ManagementContext;
import com.nabsys.resource.DOMConfigurator;
import com.nabsys.resource.ServerConfiguration;

public class ServerConfig implements IManagementClass{

	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	public NBFields execute(ManagementContext context, long clientSequence) {
		NBFields fromClient = context.getFields();
		NBFields toClient = new NBFields();
		
		if(fromClient.get("CMD_CODE").equals("R"))
		{
			try {
				toClient = getServerConfig();
			} catch (ParserConfigurationException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting server information.");
			} catch (SAXException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting server information.");
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting server information.");
			} catch (TransformerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting server information.");
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
		else if(fromClient.get("CMD_CODE").equals("S"))
		{
			try {
				setServerConfig(fromClient, context);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (ParserConfigurationException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting server information.");
			} catch (SAXException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting server information.");
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting server information.");
			} catch (TransformerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting server information.");
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
		else if(fromClient.get("CMD_CODE").equals("P"))
		{
			try {
				toClient = getPluginList((String)fromClient.get("SCH"));
			} catch (ParserConfigurationException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting server information.");
			} catch (SAXException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting server information.");
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting server information.");
			} catch (TransformerException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting server information.");
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
		else
		{
			toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
			toClient.put("RTN_MSG", "Unsuppored command code.");
		}
		
		
		return toClient;
	}
	
	private NBFields getPluginList(String pluginName) throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		NBFields fields = new NBFields();
		DOMConfigurator dom = new DOMConfigurator(ServerConfiguration.getConfigFile());

		ArrayList<HashMap<String, String>> getPluginList = dom.getSubNodeListParamMap("server/plug-in-list");
		ArrayList<NBFields> pluginList = new ArrayList<NBFields>();
		for(int i=0; i<getPluginList.size(); i++)
		{
			if(pluginName.equals(""))
			{
				NBFields pluginFields = new NBFields();
				pluginFields.put("ID", getPluginList.get(i).get("id"));
				pluginFields.put("NAME", getPluginList.get(i).get("name"));
				pluginFields.put("TYPE", getPluginList.get(i).get("type"));
				
				pluginList.add(pluginFields);
			}
			else
			{
				if(getPluginList.get(i).get("id").contains(pluginName))
				{
					NBFields pluginFields = new NBFields();
					pluginFields.put("ID", getPluginList.get(i).get("id"));
					pluginFields.put("NAME", getPluginList.get(i).get("name"));
					pluginFields.put("TYPE", getPluginList.get(i).get("type"));
					
					pluginList.add(pluginFields);
				}
			}
		}
		
		fields.put("PLG_LST", pluginList);
		
		return fields;
	}
	
	private NBFields getServerConfig() throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		NBFields fields = new NBFields();
		DOMConfigurator dom = new DOMConfigurator(ServerConfiguration.getConfigFile());
		
		fields.put("LICENSE"			, "");
		fields.put("ROOT_PATH"			, dom.getConf("server/default-file-path"));
		fields.put("LOG4J_PATH"			, dom.getConf("server/log-properties"));
		fields.put("USER_PATH"			, dom.getConf("server/user-config-file"));
		fields.put("INSTANCE_CONF_PATH"	, dom.getConf("server/instance-config-file"));
		fields.put("LOCALE"				, dom.getConf("server/time-locale"));
		fields.put("ENCODING"			, dom.getConf("server/server-encoding"));
		fields.put("MAX_CLIENT"			, dom.getConf("server/network/max-client-num"));
		fields.put("SERVICE_PORT"		, dom.getConf("server/network/service-port"));
		fields.put("SOCK_BUFF_SIZE"		, dom.getConf("server/network/max-socket-buffer"));
		fields.put("SOCK_TIME_OUT"		, dom.getConf("server/network/read-time-out"));
		
		
		ArrayList<HashMap<String, String>> getProtocolList = dom.getSubNodeListParamMap("server/protocol-list");
		ArrayList<NBFields> protocolList = new ArrayList<NBFields>();
		for(int i=0; i<getProtocolList.size(); i++)
		{
			NBFields protocolFields = new NBFields();
			protocolFields.put("ID", getProtocolList.get(i).get("id"));
			protocolFields.put("NAME", getProtocolList.get(i).get("name"));
			protocolFields.put("CLASS", getProtocolList.get(i).get("class"));
			protocolList.add(protocolFields);
		}
		fields.put("PCL_LIST", protocolList);
		
		ArrayList<HashMap<String, String>> getPluginList = dom.getSubNodeListParamMap("server/plug-in-list");
		ArrayList<NBFields> pluginList = new ArrayList<NBFields>();
		for(int i=0; i<getPluginList.size(); i++)
		{
			NBFields pluginFields = new NBFields();
			pluginFields.put("ID", getPluginList.get(i).get("id"));
			pluginFields.put("NAME", getPluginList.get(i).get("name"));
			pluginFields.put("TYPE", getPluginList.get(i).get("type"));
			
			//Plugin param
			HashMap<String, String> paramMap = dom.getSubNodeListMapBySubNodeIDList("server/plug-in-list", (String)pluginFields.get("ID"));
			Set<String> keyList = paramMap.keySet();
			Iterator<String> itr = keyList.iterator();
			while(itr.hasNext())
			{
				String key = itr.next();
				pluginFields.put("PRM__" + key, paramMap.get(key));
			}
			
			pluginList.add(pluginFields);
		}
		fields.put("PLG_LIST", pluginList);

		return fields;
	}
	
	@SuppressWarnings("unchecked")
	private void setServerConfig(NBFields fields, ManagementContext context) throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		DOMConfigurator dom = new DOMConfigurator(ServerConfiguration.getConfigFile());
	
		
		if(fields.containsKey("LICENSE"))
		{
			dom.setConf("server/license-key", (String)fields.get("LICENSE"));
		}
		
		if(fields.containsKey("CLASS_PATH"))
		{
			dom.setConf("server/default-file-path", (String)fields.get("CLASS_PATH"));
		}
		
		if(fields.containsKey("LOG4J_PATH"))
		{
			dom.setConf("server/log-properties", (String)fields.get("LOG4J_PATH"));
		}
		
		if(fields.containsKey("USER_PATH"))
		{
			dom.setConf("server/user-config-file", (String)fields.get("USER_PATH"));
		}
		
		if(fields.containsKey("INSTANCE_CONF_PATH"))
		{
			dom.setConf("server/instance-config-file", (String)fields.get("INSTANCE_CONF_PATH"));
		}
		
		if(fields.containsKey("LOCALE"))
		{
			dom.setConf("server/time-locale", (String)fields.get("LOCALE"));
		}
		
		if(fields.containsKey("ENCODING"))
		{
			dom.setConf("server/server-encoding", (String)fields.get("ENCODING"));
		}
		
		if(fields.containsKey("MAX_CLIENT"))
		{
			dom.setConf("server/network/max-client-num", (String)fields.get("MAX_CLIENT"));
		}
		
		if(fields.containsKey("SERVICE_PORT"))
		{
			dom.setConf("server/network/service-port", (String)fields.get("SERVICE_PORT"));
		}
		
		if(fields.containsKey("SOCK_BUFF_SIZE"))
		{
			dom.setConf("server/network/max-socket-buffer", (String)fields.get("SOCK_BUFF_SIZE"));
		}
		
		if(fields.containsKey("SOCK_TIME_OUT"))
		{
			dom.setConf("server/network/read-time-out", (String)fields.get("SOCK_TIME_OUT"));
		}
		
		if(fields.containsKey("ACT_LST"))
		{
			ArrayList<NBFields> actList = (ArrayList<NBFields>)fields.get("ACT_LST");
			for(int i=0; i<actList.size(); i++)
			{
				NBFields act = actList.get(i);
				if(act.get("CTG").equals("PCL"))
				{
					setProtocol(act, dom);
				}
				else if(act.get("CTG").equals("PLG"))
				{
					setPlugin(act, dom);
				}
				else if(act.get("CTG").equals("PRM"))
				{
					setParameter(act, dom);
				}
			}
		}
		
		logger.info(NLabel.get(0x008C) + " by " + context.getUser() + "(" + context.getClientAddress() + ")");
	}
	
	private void setProtocol(NBFields actionInfo, DOMConfigurator dom) throws TransformerException
	{
		if(actionInfo.get("ACT").equals("U"))
		{
			dom.setParamByID("server/protocol-list/protocol", (String)actionInfo.get("KEY"), ((String)actionInfo.get("FLD")).toLowerCase(), (String)actionInfo.get("VLU"));
		}
		else if(actionInfo.get("ACT").equals("D"))
		{
			dom.removeByID("server/protocol-list", (String)actionInfo.get("KEY"));
		}
	}
	
	private void setPlugin(NBFields actionInfo, DOMConfigurator dom) throws TransformerException
	{
		if(actionInfo.get("ACT").equals("U"))
		{
			dom.setParamByID("server/plug-in-list/plug-in", (String)actionInfo.get("KEY"), ((String)actionInfo.get("FLD")).toLowerCase(), (String)actionInfo.get("VLU"));
		}
		else if(actionInfo.get("ACT").equals("D"))
		{
			dom.removeByID("server/plug-in-list", (String)actionInfo.get("KEY"));
		}
	}
	
	private void setParameter(NBFields actionInfo, DOMConfigurator dom) throws TransformerException
	{
		ArrayList<String> idList = new ArrayList<String>();
		idList.add((String)actionInfo.get("PID"));
		idList.add((String)actionInfo.get("KEY"));
		
		if(actionInfo.get("ACT").equals("U"))
		{
			dom.setProtocolParam("server/plug-in-list/plug-in/param", 
					(String)actionInfo.get("PID"), 
					(String)actionInfo.get("KEY"), 
					(String)actionInfo.get("FLD"), 
					(String)actionInfo.get("VLU"));
		}
		else if(actionInfo.get("ACT").equals("D"))
		{
			dom.removeProtocolParam("server/plug-in-list/plug-in/param", 
					(String)actionInfo.get("PID"), 
					(String)actionInfo.get("KEY"));
		}
	}

}
