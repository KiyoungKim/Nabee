package com.nabsys.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class ServerConfiguration {
	private static DOMConfigurator cfg				= null;
	private static String	CFG_FILE_PATH			= "";
	private static String 	LOG_FILE_PATH 			= "";
	private static Locale 	TIME_LOCALE 			= null;
	private static String 	SERVER_ENCODING 		= "";
	private static int 		MAX_CLIENT_NUM			= 0;
	private static int 		SERVICE_PORT			= 0;
	private static int 		MAX_SOCKET_BUFFER		= 0;
	private static long 	READ_TIME_OUT			= 0L;
	private static String	USER_CONF				= "";
	
	public void loadConfig(String path) throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		cfg = new DOMConfigurator(path);
		CFG_FILE_PATH = path;
		
		if(cfg.newGenerated())
		{
			cfg.setConf("server/log-properties"									, "config/log4j.xml");
			cfg.setConf("server/time-locale"									, "US");
			cfg.setConf("server/server-encoding"								, "UTF-8");
			cfg.setConf("server/network/max-client-num"							, "5");
			cfg.setConf("server/network/service-port"							, "9487");
			cfg.setConf("server/network/max-socket-buffer"						, "16384");
			cfg.setConf("server/network/read-time-out"							, "0");
			cfg.setConf("server/protocol-list"									, "0");
			cfg.setConf("server/plug-in-list"									, "0");
		}
		
		LOG_FILE_PATH = cfg.getConf("server/log-properties");
		
		String locale = cfg.getConf("server/time-locale");
		if(locale.toUpperCase().equals("")) 			TIME_LOCALE = java.util.Locale.KOREA;
		else if(locale.toUpperCase().equals("KOREA")) 	TIME_LOCALE = java.util.Locale.KOREA;
		else if(locale.toUpperCase().equals("CANADA")) 	TIME_LOCALE = java.util.Locale.CANADA;
		else if(locale.toUpperCase().equals("CHINA")) 	TIME_LOCALE = java.util.Locale.CHINA;
		else if(locale.toUpperCase().equals("UK")) 		TIME_LOCALE = java.util.Locale.UK;
		else if(locale.toUpperCase().equals("FRANCE")) 	TIME_LOCALE = java.util.Locale.FRANCE;
		else if(locale.toUpperCase().equals("GERMAN")) 	TIME_LOCALE = java.util.Locale.GERMAN;
		else if(locale.toUpperCase().equals("ITALY")) 	TIME_LOCALE = java.util.Locale.ITALY;
		else if(locale.toUpperCase().equals("JAPAN")) 	TIME_LOCALE = java.util.Locale.JAPAN;
		else if(locale.toUpperCase().equals("PRC")) 	TIME_LOCALE = java.util.Locale.PRC;
		else if(locale.toUpperCase().equals("TAIWAN")) 	TIME_LOCALE = java.util.Locale.TAIWAN;
		else if(locale.toUpperCase().equals("US")) 		TIME_LOCALE = java.util.Locale.US;
		else TIME_LOCALE = java.util.Locale.US;
		
		SERVER_ENCODING 		= cfg.getConf("server/server-encoding");
		MAX_CLIENT_NUM			= Integer.parseInt(cfg.getConf("server/network/max-client-num"));
		SERVICE_PORT			= Integer.parseInt(cfg.getConf("server/network/service-port"));
		MAX_SOCKET_BUFFER		= Integer.parseInt(cfg.getConf("server/network/max-socket-buffer"));
		READ_TIME_OUT			= Long.parseLong(cfg.getConf("server/network/read-time-out"));
	}
	
	public static String getConfigFile()
	{
		return CFG_FILE_PATH;
	}
	
	public static String getLogPropertyPath()
	{
		return LOG_FILE_PATH;
	}
	
	public static Locale getTimeLocale()
	{
		return TIME_LOCALE;
	}
	
	public static String getServerEncoding()
	{
		return SERVER_ENCODING;
	}
	
	public static HashMap<String, String> getPluginParams(String pluginID)
	{
		return cfg.getSubNodeListMapBySubNodeIDList("server/plug-in-list", pluginID);
	}
	
	public static ArrayList<String> getPluginList()
	{
		return cfg.getSubNodeIDList("server/plug-in-list");
	}
	
	public static int getMaxClientNum()
	{
		return MAX_CLIENT_NUM;
	}
	
	public static int getServicePort()
	{
		return SERVICE_PORT;
	}
	
	public static int getMaxSocketBuffer()
	{
		return MAX_SOCKET_BUFFER;
	}
	
	public static long getReadTimeOut()
	{
		return READ_TIME_OUT;
	}

	public static String getUserConfFile() {
		return USER_CONF;
	}
}
