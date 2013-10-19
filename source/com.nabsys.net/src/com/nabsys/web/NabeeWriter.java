package com.nabsys.web;

import java.io.PrintWriter;
import java.io.StringWriter;

public class NabeeWriter extends PrintWriter {
	public NabeeWriter() { 
		super(new StringWriter(4096) ); 
	}
	
	public String toString() { 
		return ((StringWriter)super.out).toString();
	}
}
