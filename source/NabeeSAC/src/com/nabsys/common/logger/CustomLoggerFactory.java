package com.nabsys.common.logger;

import org.apache.log4j.spi.LoggerFactory;

public class CustomLoggerFactory implements LoggerFactory{

	public NLogger makeNewLoggerInstance(String arg0) {
		return new com.nabsys.common.logger.NLogger(arg0);
	}

}
