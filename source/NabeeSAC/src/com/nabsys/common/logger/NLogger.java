package com.nabsys.common.logger;


import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggingEvent;

import com.nabsys.common.label.NLabel;

public class NLogger extends org.apache.log4j.Logger{
	
	private static CustomLoggerFactory factory = new CustomLoggerFactory();
	
	public NLogger(String name) {
		super(name);
	}
	
	public static Category getInstance(String name)
	{
		return Logger.getLogger(name, (LoggerFactory) factory);
	}
	
	public static NLogger getLogger(String name)
	{
		return (NLogger)Logger.getLogger(name, (LoggerFactory) factory);
	}
	
	public static NLogger getLogger(Class<?> clazz)
	{
		return (NLogger)Logger.getLogger(clazz.getName(), (LoggerFactory) factory);
	}
	
	public void debug(int code)
	{
		super.log(FQCN, Level.DEBUG, NLabel.get(code), null);
	}
	
	public void debug(String message)
	{
		super.log(FQCN, Level.DEBUG, message, null);
	}
	
	public void info(int code)
	{
		super.log(FQCN, Level.INFO, NLabel.get(code), null);
	}
	
	public void info(String message)
	{
		super.log(FQCN, Level.INFO, message, null);
	}
	
	public void warn(int code)
	{
		super.log(FQCN, Level.WARN, NLabel.get(code), null);
	}
	
	public void warn(String message)
	{
		super.log(FQCN, Level.WARN, message, null);
	}

	public void warn(Exception e, int code)
	{
		String stackTrace = "";
		
		for(int i=0; i<e.getStackTrace().length; i++)
		{
			stackTrace = stackTrace + 
				 String.format("\nCLASS [%-50s] METHOD [%-20s] LINE [%-5s]"
						   	   , e.getStackTrace()[i].getClassName()
						   	   , e.getStackTrace()[i].getMethodName()
						   	   , String.valueOf(e.getStackTrace()[i].getLineNumber()));
		}
		
		super.log(FQCN, Level.WARN, NLabel.get(code) + stackTrace, null);
	}
	
	public void warn(Exception e, String message)
	{
		String stackTrace = "";
		
		for(int i=0; i<e.getStackTrace().length; i++)
		{
			stackTrace = stackTrace + 
				 String.format("\nCLASS [%-50s] METHOD [%-20s] LINE [%-5s]"
						   	   , e.getStackTrace()[i].getClassName()
						   	   , e.getStackTrace()[i].getMethodName()
						   	   , String.valueOf(e.getStackTrace()[i].getLineNumber()));
		}
		super.log(FQCN, Level.WARN, message + stackTrace, null);
	}
	
	public void error(int code)
	{
		super.log(FQCN, Level.ERROR, NLabel.get(code), null);
	}
	
	public void error(String message)
	{
		super.log(FQCN, Level.ERROR, message, null);
	}
	
	public void error(Throwable e, int code)
	{
		String stackTrace = "";
		
		for(int i=0; i<e.getStackTrace().length; i++)
		{
			stackTrace = stackTrace + 
				 String.format("\nCLASS [%-50s] METHOD [%-20s] LINE [%-5s]"
						   	   , e.getStackTrace()[i].getClassName()
						   	   , e.getStackTrace()[i].getMethodName()
						   	   , String.valueOf(e.getStackTrace()[i].getLineNumber()));
		}
		
		super.log(FQCN, Level.ERROR, NLabel.get(code) + stackTrace, null);
	}
	
	public void error(Throwable e, String message)
	{
		String stackTrace = "";
		
		for(int i=0; i<e.getStackTrace().length; i++)
		{
			stackTrace = stackTrace + 
				 String.format("\nCLASS [%-50s] METHOD [%-20s] LINE [%-5s]"
						   	   , e.getStackTrace()[i].getClassName()
						   	   , e.getStackTrace()[i].getMethodName()
						   	   , String.valueOf(e.getStackTrace()[i].getLineNumber()));
		}
		
		super.log(FQCN, Level.ERROR, message + stackTrace, null);
	}
	
	public void fatal(int code)
	{
		super.log(FQCN, Level.FATAL, NLabel.get(code), null);
	}
	
	public void fatal(String message)
	{
		super.log(FQCN, Level.FATAL, message, null);
	}
	
	public void fatal(Throwable e, int code)
	{
		String stackTrace = "";
		
		for(int i=0; i<e.getStackTrace().length; i++)
		{
			stackTrace = stackTrace + 
				 String.format("\nCLASS [%-50s] METHOD [%-20s] LINE [%-5s]"
						   	   , e.getStackTrace()[i].getClassName()
						   	   , e.getStackTrace()[i].getMethodName()
						   	   , String.valueOf(e.getStackTrace()[i].getLineNumber()));
		}
		
		super.log(FQCN, Level.FATAL, NLabel.get(code) + stackTrace, null);
	}
	
	public void fatal(Throwable e, String message)
	{
		String stackTrace = "";
		
		for(int i=0; i<e.getStackTrace().length; i++)
		{
			stackTrace = stackTrace + 
				 String.format("\nCLASS [%-50s] METHOD [%-20s] LINE [%-5s]"
						   	   , e.getStackTrace()[i].getClassName()
						   	   , e.getStackTrace()[i].getMethodName()
						   	   , String.valueOf(e.getStackTrace()[i].getLineNumber()));
		}
		
		super.log(FQCN, Level.FATAL, message + stackTrace, null);
	}
	
	public synchronized void addAppender(Appender newAppender)
	{
		super.addAppender(newAppender);
	}

	public void callAppenders(LoggingEvent event)
	{
		super.callAppenders(event);
	}

	public synchronized Appender getAppender(String name)
	{
		return super.getAppender(name);
	}

	public synchronized void removeAllAppenders()
	{
		super.removeAllAppenders();
	}

	public synchronized void removeAppender(Appender appender)
	{
		super.removeAppender(appender);
	}

	public synchronized void removeAppender(String name)
	{
		super.removeAppender(name);
	}
	
	static String FQCN = NLogger.class.getName();
}
