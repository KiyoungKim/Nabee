package com.nabsys.resource.service;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.nabsys.process.Context;

public class PluginHandler extends ServiceHandler {

	/**
	 * Similar with calling component. However component initiated on every service.
	 * Plug-in is initiated once on instance's loading.
	 */
	private static final long serialVersionUID = 1L;
	private String pluginName = null;
	private String methodName = null;
	private ComponentArgumentContext methodArguments = null;
	private boolean isSetReturnValue = false;
	private String returnMapKey = null;
	public PluginHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	
	public void setData(String pluginName, String methodName, ComponentArgumentContext methodArguments, boolean isSetReturnValue, String returnMapKey)
	{
		this.pluginName = pluginName;
		this.methodName = methodName;
		this.methodArguments = methodArguments;
		this.isSetReturnValue = isSetReturnValue; 
		this.returnMapKey = returnMapKey;
	}
	
	public String getPluginName()
	{
		return pluginName;
	}
	public String geTmethodName()
	{
		return methodName;
	}
	public ComponentArgumentContext getMethodArguments()
	{
		return methodArguments;
	}
	
	public boolean isSetReturnValue()
	{
		return isSetReturnValue;
	}
	public String getReturnMapKey()
	{
		return returnMapKey;
	}

	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		{
			Object plugin = ctx.getPlugins(pluginName);
			Class<?>[] parameterTypes = methodArguments.getType();
			Method method = plugin.getClass().getMethod(methodName, parameterTypes);
			for(int i=0; i<parameterTypes.length; i++)
			{
				if(parameterTypes[i] == Context.class && methodArguments.getArgument()[i].equals("Context")) methodArguments.getArgument()[i] = ctx;
				else if(parameterTypes[i] == HashMap.class && methodArguments.getArgument()[i].equals("Connector Map")) methodArguments.getArgument()[i] = map;
			}
			Object rtn = null;
			try{
				rtn = method.invoke(plugin, methodArguments.getArgument());
			}catch(Exception e){
				throw new Exception("Component execute exception.");
			}
			if(isSetReturnValue) map.put(returnMapKey, rtn);
		}
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Plugin : " + map + "", false, false);
		}
		super.execute(ctx, map);
	}

}
