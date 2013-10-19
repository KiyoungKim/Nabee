package com.nabsys.net.protocol.util.soap;

import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;

public class SOAPService {
	public SOAPService()
	{
		try {
			SOAPConnectionFactory a= SOAPConnectionFactory.newInstance();
			a.toString();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (SOAPException e) {
			e.printStackTrace();
		}
	}
	
	
}
