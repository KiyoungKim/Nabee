package com.nabsys.process.instance;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.process.ResourceFactory;
import com.nabsys.resource.ServerConfiguration;


public class InstanceApp {

	public static void main(String[] args) {
		String instanceName 			= args[0];
		String serverConfigFilePath 	= args[1];
		String serverLoginPW 			= args[2];
		String configDatabasePort		= args[3];
		ServerConfiguration sc = new ServerConfiguration();
		try {
			sc.loadConfig(serverConfigFilePath);
		} catch (ParserConfigurationException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		} catch (SAXException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		} catch (TransformerException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
		
		
		DBPoolManager configurationPool = null;
		ConfigurationInitializer ci = new ConfigurationInitializer();
		try {
			configurationPool = ci.initConfigRepository(configDatabasePort, instanceName);
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}

		NLabel label = new NLabel();
		try {
			label.loadLabel();
			label.setLocale(ServerConfiguration.getTimeLocale());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
		
		InstanceStatic.setInstanceName(instanceName);
		InstanceStatic.setServerLoginPW(serverLoginPW);

		org.apache.log4j.xml.DOMConfigurator.configure(ci.getLogConfigurationFile());
		NLogger logger = NLogger.getLogger(InstanceApp.class.getName());
		
		ResourceFactory resourceFactory = null;
		try {
			resourceFactory = new ResourceFactory(configurationPool, instanceName);
		} catch (SecurityException e) {
			logger.fatal(e, 0x0046);
			System.err.println(e.getMessage());
			System.exit(0);
		} catch (Exception e) {
			logger.fatal(e, 0x0046);
			System.err.println(e.getMessage());
			System.exit(0);
		}
		
		logger.info(0x0011);
		
		System.err.println("S");

		InternalCommunicator internalCommunicator = new InternalCommunicator(resourceFactory);
		internalCommunicator.start();

		InstanceRuntime.setResourceFactory(resourceFactory);
		InstanceRuntime.addShutdownHook(new LifeCycleController(internalCommunicator));
		
	}
}
