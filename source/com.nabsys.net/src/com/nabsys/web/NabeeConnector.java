package com.nabsys.web;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.xml.DOMConfigurator;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.ConnectionPool;
import com.nabsys.net.protocol.ConnectionPoolSource;
import com.nabsys.net.protocol.flxb.FlexibleProtocolHandler;

public class NabeeConnector implements ServletContextListener, HttpSessionListener{

	private ConnectionPool connectionPool = null;
	protected NLogger logger = null;
	
	public void contextDestroyed(ServletContextEvent arg) 
	{
		try {
			if(connectionPool != null)
				connectionPool.closeAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void contextInitialized(ServletContextEvent arg)
	{
		ServletContext context = arg.getServletContext();
		
		NLabel label = new NLabel();
		try {
			label.loadLabel();
			
			String locale = context.getInitParameter("LOCALE");
					
			if(locale.toUpperCase().equals("")) 			label.setLocale(java.util.Locale.KOREA);
			else if(locale.toUpperCase().equals("KOREA")) 	label.setLocale(java.util.Locale.KOREA);
			else if(locale.toUpperCase().equals("CANADA")) 	label.setLocale(java.util.Locale.CANADA);
			else if(locale.toUpperCase().equals("CHINA")) 	label.setLocale(java.util.Locale.CHINA);
			else if(locale.toUpperCase().equals("UK")) 		label.setLocale(java.util.Locale.UK);
			else if(locale.toUpperCase().equals("FRANCE")) 	label.setLocale(java.util.Locale.FRANCE);
			else if(locale.toUpperCase().equals("GERMAN")) 	label.setLocale(java.util.Locale.GERMAN);
			else if(locale.toUpperCase().equals("ITALY")) 	label.setLocale(java.util.Locale.ITALY);
			else if(locale.toUpperCase().equals("JAPAN")) 	label.setLocale(java.util.Locale.JAPAN);
			else if(locale.toUpperCase().equals("PRC")) 	label.setLocale(java.util.Locale.PRC);
			else if(locale.toUpperCase().equals("TAIWAN")) 	label.setLocale(java.util.Locale.TAIWAN);
			else if(locale.toUpperCase().equals("US")) 		label.setLocale(java.util.Locale.US);
			else label.setLocale(java.util.Locale.US);
		} catch (IOException e) {
		}
		
		DOMConfigurator.configure(context.getRealPath(context.getInitParameter("LOG4J_CONFIG")));
		logger = NLogger.getLogger(this.getClass().getName());
		
		ConnectionPoolSource source = new ConnectionPoolSource();

		source.setAddress(context.getInitParameter("HOST_ADDRESS"));
		source.setPort(Integer.valueOf(context.getInitParameter("HOST_SERVICE_PORT")));
		source.setMaxSocketBuff(Integer.valueOf(context.getInitParameter("MAX_SOCK_BUFF")));
		source.setSocketReadTimeOut(Integer.valueOf(context.getInitParameter("READ_TIMEOUT"))); //second
		source.setKeepAlive(context.getInitParameter("KEEP_ALIVE").equals("true"));
		source.setKeepAliveSecond(Integer.valueOf(context.getInitParameter("KEEP_ALIVE_TIME")));
		source.setEncoding(context.getInitParameter("TARGET_ENCODING"));
		source.setMaxActive(Integer.valueOf(context.getInitParameter("MAX_POOL_ACTIVE")));
		source.setMaxIdle(Integer.valueOf(context.getInitParameter("MAX_POOL_IDLE")));
		source.setMaxWait(Integer.valueOf(context.getInitParameter("SOCK_MAX_WAIT")));
		
		connectionPool = new ConnectionPool(source);
		connectionPool.setProtocolType(FlexibleProtocolHandler.class);
		
		arg.getServletContext().setAttribute("NABEECONNECTOR", connectionPool);
		
		logger.info("Complete initializing Nabee connector");
	}

	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
	}

}
