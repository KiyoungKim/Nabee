package com.nabsys.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.HttpJspPage;

import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.ConnectionPool;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.flxb.FLXB;
import com.nabsys.net.socket.channel.Channel;
import com.nabsys.net.socket.channel.IChannelResult;
import com.nabsys.net.socket.channel.handler.util.BlockingReadHandler;
import com.nabsys.net.socket.channel.handler.util.IKeepAlive;
import com.nabsys.net.socket.channel.handler.util.KeepAliveHandler;
import com.nabsys.net.socket.channel.handler.util.LogHandler;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

public abstract class NabeePage extends HttpServlet implements HttpJspPage{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1574910751785905570L;
	protected final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	private ConnectionPool connectionPool = null;
	private IKeepAlive keepAliveManager = null;
	
    @Override
    public final void init(ServletConfig config) 
        throws ServletException 
    {
        super.init(config);
        jspInit();
        keepAliveManager = new KeepAliveManager();
    }
    
	public void jspDestroy() 
	{
	}

    public void jspInit() {
    	connectionPool = (ConnectionPool)getServletContext().getAttribute("NABEECONNECTOR");
    }
    
    @Override
    public String getServletInfo() {
    	return super.getServletInfo();
    }
    
    @Override
    public final void destroy() {
        jspDestroy();
    }
    
    @SuppressWarnings("unchecked")
	protected void action(String serviceID, NBFields fields, HttpServletRequest request) throws Throwable
	{
    	Channel channel = null;
		try {
			channel = connectionPool.getChannel();
			BlockingReadHandler<NBFields> blockingReadHandler = null;
			
			if(!channel.getHandlerChain().isHandlerExist("blockingReadHandler"))
			{
				blockingReadHandler = new BlockingReadHandler<NBFields>();
				channel.getHandlerChain().addFirst("blockingReadHandler", blockingReadHandler);
			}
			else
			{
				blockingReadHandler = (BlockingReadHandler<NBFields>)channel.getHandlerChain().getHandler("blockingReadHandler");
			}
			
			if(!channel.getHandlerChain().isHandlerExist("keepAliveHandler"))
			{
				
				channel.getHandlerChain().addNext("blockingReadHandler", "keepAliveHandler", new KeepAliveHandler(keepAliveManager));
			}
			
			if(!channel.getHandlerChain().isHandlerExist("logHandler"))
			{
				channel.getHandlerChain().addNext("keepAliveHandler", "logHandler", new LogHandler());
			}
			
			fields.put(FLXB.SERVICE_ID_FIELD_NAME, serviceID);
			fields.put(FLXB.RETURN_FIELD_NAME, FLXB.SUCCESS);
			fields.put(FLXB.REPLY_YN, FLXB.REP_Y);
			
			IChannelResult result = channel.write(fields);
			if(!result.isSuccess())
			{
				throw result.getCause();
			}
			
			fields = blockingReadHandler.read(
					Integer.valueOf(getServletContext().getInitParameter("READ_TIMEOUT")), 
					TimeUnit.SECONDS);
			
			NabeeRequest nabsysRequest = (NabeeRequest)request;
			 
			Set<String> keySet = fields.keySet();
			Iterator<String> itr = keySet.iterator();
			 
			while(itr.hasNext())
			{
				String key = itr.next();
				Object rtnObj = fields.get(key);
				
				nabsysRequest.setAttribute(key, rtnObj);
			}
			 
			request = (HttpServletRequest)nabsysRequest;
			if(fields.containsKey(FLXB.RETURN_FIELD_NAME) && ((Integer)fields.get(FLXB.RETURN_FIELD_NAME)) == FLXB.FAIL)
			{
				throw new RuntimeException((String)fields.get("RSV_MESSAGE"));
			}
		} finally {
			channel.close();
		}
	}
    
	@SuppressWarnings("unchecked")
	protected void action(String serviceID, HttpServletRequest request) throws Throwable
	{
		Channel channel = null;
		try {
			channel = connectionPool.getChannel();
			BlockingReadHandler<NBFields> blockingReadHandler = null;
			
			if(!channel.getHandlerChain().isHandlerExist("blockingReadHandler"))
			{
				blockingReadHandler = new BlockingReadHandler<NBFields>();
				channel.getHandlerChain().addFirst("blockingReadHandler", blockingReadHandler);
			}
			else
			{
				blockingReadHandler = (BlockingReadHandler<NBFields>)channel.getHandlerChain().getHandler("blockingReadHandler");
			}
			
			if(!channel.getHandlerChain().isHandlerExist("keepAliveHandler"))
			{
				
				channel.getHandlerChain().addNext("blockingReadHandler", "keepAliveHandler", new KeepAliveHandler(keepAliveManager));
			}
			
			if(!channel.getHandlerChain().isHandlerExist("logHandler"))
			{
				channel.getHandlerChain().addNext("keepAliveHandler", "logHandler", new LogHandler());
			}
			
			NBFields fields = setActionFields(request);
			
			fields.put(FLXB.SERVICE_ID_FIELD_NAME, serviceID);
			fields.put(FLXB.RETURN_FIELD_NAME, FLXB.SUCCESS);
			fields.put(FLXB.REPLY_YN, FLXB.REP_Y);
			
			IChannelResult result = channel.write(fields);
			if(!result.isSuccess())
			{
				throw result.getCause();
			}
			
			fields = blockingReadHandler.read(
					Integer.valueOf(getServletContext().getInitParameter("READ_TIMEOUT")), 
					TimeUnit.SECONDS);
			
			NabeeRequest nabsysRequest = (NabeeRequest)request;
			 
			Set<String> keySet = fields.keySet();
			Iterator<String> itr = keySet.iterator();
			 
			while(itr.hasNext())
			{
				String key = itr.next();
				Object rtnObj = fields.get(key);
				
				nabsysRequest.setAttribute(key, rtnObj);
			}
			 
			request = (HttpServletRequest)nabsysRequest;
			if(fields.containsKey(FLXB.RETURN_FIELD_NAME) && ((Integer)fields.get(FLXB.RETURN_FIELD_NAME)) == FLXB.FAIL)
			{
				throw new RuntimeException((String)fields.get("RSV_MESSAGE"));
			}
		} finally {
			if(channel != null) channel.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected NBFields setActionFields(HttpServletRequest request) throws Exception
    {
    	
     	Enumeration<?> enmReq = request.getParameterNames();
     	NBFields netFields = new NBFields();

		if(request.getContentType() != null 
			&& request.getContentType().length() >= 20
			&& request.getContentType().substring(0, 20).equals("multipart/form-data;"))
		{
			int maxSize = Integer.parseInt(getServletContext().getInitParameter("MAX_FILE_UPLOAD"));
			MultipartParser multipartParser = new MultipartParser(request, maxSize);
			multipartParser.setEncoding(getServletContext().getInitParameter("CHAR_ENCODING"));

			Part part;
			ArrayList<NBFields> list = null;
			int cnt = 0;
			while((part = multipartParser.readNextPart()) != null)
			{
				if(netFields.containsKey(part.getName()))
				{
					list = (ArrayList<NBFields>)netFields.get(part.getName());
					cnt++;
				}
				else
				{
					list = new ArrayList<NBFields>();
				}
				
				if(part.isParam())
				{
					ParamPart paramPart = (ParamPart)part;

					NBFields fields = new NBFields();
					fields.put("VALUE", paramPart.getStringValue());
					list.add(fields);
				}
				else if(part.isFile())
				{
					FilePart filePart = (FilePart)part;
					
					if(filePart.getFileName() == null) continue;
					
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buffer = null;
					try{
						NBFields fields = new NBFields();
						
						filePart.writeTo(bos);
						buffer = bos.toByteArray();
						
						fields.put("_FILE_NAME", filePart.getFileName());
						fields.put("_FILE_SIZE", buffer.length);
						fields.put("VALUE", buffer);
	
						list.add(fields);
					}finally{
						if(bos != null) bos.close();
					}
				}
				
				netFields.put(part.getName().replaceAll("\\[", "").replaceAll("\\]", ""), list);
			}

			if(cnt == 0)
			{
				Set<String> keySet = netFields.keySet();
				Iterator<String> itr = keySet.iterator();
				NBFields tmp = new NBFields();
				while(itr.hasNext())
				{
					String key = itr.next();
				
					tmp.put(key, ((ArrayList<NBFields>)netFields.get(key)).get(0).get("VALUE"));
					if(((ArrayList<NBFields>)netFields.get(key)).get(0).containsKey("_FILE_NAME"))
						tmp.put(key+"_FILE_NAME", ((ArrayList<NBFields>)netFields.get(key)).get(0).get("_FILE_NAME"));
					if(((ArrayList<NBFields>)netFields.get(key)).get(0).containsKey("_FILE_SIZE"))
						tmp.put(key+"_FILE_SIZE", ((ArrayList<NBFields>)netFields.get(key)).get(0).get("_FILE_SIZE"));
				}
				
				netFields.clear();
				netFields.putAll(tmp);
			}
			
			Enumeration<?> enmSession = request.getSession().getAttributeNames();
			while(enmSession.hasMoreElements())
			{
				String parameterName = (String)enmSession.nextElement();
				netFields.put(parameterName.replaceAll("\\[", "").replaceAll("\\]", ""), request.getSession().getAttribute(parameterName));
			}
		}
		else
		{
			while(enmReq.hasMoreElements())
			{
				String parameterName = (String)enmReq.nextElement();
				
				String[] valueArray = request.getParameterValues(parameterName);

				if(valueArray != null && valueArray.length > 1)
				{
					ArrayList<NBFields> list = new ArrayList<NBFields>();
					for(int i=0; i<valueArray.length; i++)
					{
						NBFields fields = new NBFields();
						fields.put("VALUE", valueArray[i]);
						list.add(fields);
					}
					netFields.put(parameterName.replaceAll("\\[", "").replaceAll("\\]", ""), list);
				}
				else
				{
					netFields.put(parameterName.replaceAll("\\[", "").replaceAll("\\]", ""), valueArray[0]);
				}
			}
			
			Enumeration<?> enmSession = request.getSession().getAttributeNames();
			while(enmSession.hasMoreElements())
			{
				String parameterName = (String)enmSession.nextElement();
				netFields.put(parameterName.replaceAll("\\[", "").replaceAll("\\]", ""), request.getSession().getAttribute(parameterName));
			}
		}
		
		netFields.put("RemotePort", request.getRemotePort());
		if(request.getRemoteAddr() != null) netFields.put("RemoteAddr", request.getRemoteAddr());
		if(request.getRemoteHost() != null) netFields.put("RemoteHost", request.getRemoteHost());
		if(request.getRequestURI() != null) netFields.put("RequestURI", request.getRequestURI());

		return netFields;
    }
	
	public final void service(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException 
	{
		request.setCharacterEncoding(getServletContext().getInitParameter("CHAR_ENCODING"));
		NabeeRequest nabeeRequest = new NabeeRequest(request);
		_jspService((HttpServletRequest)nabeeRequest, response);
	}

}
