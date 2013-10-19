package com.nabsys.process.nabee;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.xml.DOMConfigurator;
import org.xml.sax.SAXException;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.resource.ServerConfiguration;

public class NabeeApp {
	
	public static void main(String[] args) {
		if(args.length != 2) 
		{
			System.out.println("Need 2 arguments : nabee configuration file, configuration database port");
			System.exit(0);
		}
		
		ServerConfiguration sc = new ServerConfiguration();
		try {
			sc.loadConfig(args[0]);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (TransformerException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		NLabel label = new NLabel();
		try {
			label.loadLabel();
			label.setLocale(ServerConfiguration.getTimeLocale());
		} catch (IOException e) {
			System.exit(0);
		}
		
		DOMConfigurator.configure(ServerConfiguration.getLogPropertyPath());

		NLogger logger = NLogger.getLogger(NabeeApp.class.getName());

		logger.info(0x0011);
		AppManager controlWorker = new AppManager(args[1]);
		controlWorker.start();
		
		Runtime.getRuntime().addShutdownHook(new ShutdownManager(controlWorker));
	}

}
