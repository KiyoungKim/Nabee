package com.nabsys.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class NabeeFilter implements Filter {
	private String charset = null;
	
	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		res.setContentType("text/html; charset=" + charset); 
		PrintWriter writer = res.getWriter();
        NabeeResponse responseWrapper = new NabeeResponse((HttpServletResponse)res); 
        
		chain.doFilter(req, responseWrapper);

		try{

			//Filtering Class call
			String bufferedContents = responseWrapper.getBufferedString();
			
			writer.print(bufferedContents);
		}catch(Exception e){
			throw new ServletException(e);
		} catch (Throwable e) {
			throw new ServletException(e);
		}finally{
		}
		
		writer.flush();
		writer.close();
	}

	public void init(FilterConfig conf) throws ServletException {
		charset = conf.getInitParameter("CHARSET");
	}

}
