package com.nabsys.web;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class NabeeResponse extends HttpServletResponseWrapper {

	private NabeeWriter buffer = null; 
	
	public NabeeResponse(HttpServletResponse response) {
		super(response);
		buffer = new NabeeWriter();
	}
	
	public PrintWriter getWriter() throws java.io.IOException { 
		return buffer;
	} 
     
	public void setContentType(String contentType) { 
		super.setContentType(contentType);
	} 
     
	public String getBufferedString() { 
		return buffer.toString();
	} 
}
