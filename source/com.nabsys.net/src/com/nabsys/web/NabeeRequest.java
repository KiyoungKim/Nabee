package com.nabsys.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class NabeeRequest extends HttpServletRequestWrapper {

	HashMap<String, Object> params;
	
	@SuppressWarnings("unchecked")
	public NabeeRequest(HttpServletRequest request) {
		super(request);
		this.params = new HashMap<String, Object>(request.getParameterMap());
	}
	
	public String getParameter(String name){
		Object val = getParameterValues(name);
		
		if (val instanceof String)
		{
			return (String)val;  
		}
		
		if (val instanceof String[]) {  
			String[] values = (String[]) val;  
			return values[0];  
		}
		return (val == null ? getRequest().getParameter(name) : val.toString());
	}
	
	@SuppressWarnings("rawtypes")
	public Map getParameterMap() {
		 return Collections.unmodifiableMap(params);
	}
	
	@SuppressWarnings("rawtypes")
	public Enumeration getParameterNames() {
		return Collections.enumeration(params.keySet());        
	}
	
	public String[] getParameterValues(String name) {
    	String[] result = null;
    	String[] temp = (String[])params.get(name);
    	if (temp != null){
    		result = new String[temp.length];
    		System.arraycopy(temp, 0, result, 0, temp.length);    		
    	}
        return result;
    }
	
	public void setParameter(String name, String value){
		String[] oneParam = {value};
	    setParameter(name, oneParam);
	}
	
	public void setParameter(String name, String[] value){
	      params.put(name, value);   
	}

}
