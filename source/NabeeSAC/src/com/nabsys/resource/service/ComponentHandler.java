package com.nabsys.resource.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.nabsys.common.util.CustomLoader;
import com.nabsys.process.Context;

public class ComponentHandler extends ServiceHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String componentName = null;
	private String className = null;
	private String methodName = null;
	private ComponentArgumentContext constructArguments = null;
	private ComponentArgumentContext methodArguments = null;
	private Class<?> returnType = null;
	private boolean isAlwaysNew = true;
	private boolean isSetReturnValue = false;
	private String returnMapKey = null;
	public ComponentHandler(ServiceHandler parent, int x, int y, int width, int height) {
		super(parent, x, y, width, height);
	}
	public void setData(String componentName, String className, ComponentArgumentContext constructArguments, String methodName, ComponentArgumentContext methodArguments, Class<?> returnType, boolean isAlwaysNew, boolean isSetReturnValue, String returnMapKey)
	{
		this.componentName = componentName;
		this.className = className;
		this.constructArguments = constructArguments; 
		this.methodName = methodName;
		this.methodArguments = methodArguments;
		this.returnType = returnType;
		this.isAlwaysNew = isAlwaysNew;
		this.isSetReturnValue = isSetReturnValue; 
		this.returnMapKey = returnMapKey;
	}
	public String getComponentName()
	{
		return componentName;
	}
	public String getClassName()
	{
		return className;
	}
	public ComponentArgumentContext getConstructArguments()
	{
		return constructArguments;
	}
	public String getMethodName()
	{
		return methodName;
	}
	public ComponentArgumentContext getMethodArguments()
	{
		return methodArguments;
	}
	public Class<?> getReturnType()
	{
		return returnType;
	}
	public boolean isAlwaysNew()
	{
		return isAlwaysNew;
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
			CustomLoader cl = new CustomLoader();
			String path = className.replaceAll("\\.", "/") + ".class";

			Object component = null;
			if(!isAlwaysNew) component = getParent().getComponent(componentName);
			if(component == null)
			{

				Class<?> clazz = null;
				try{
					clazz = cl.getCustomClass(className, path);
				}catch(Throwable t){
					throw new Exception(t.getMessage());
				}
	
				Class<?>[] typeArray = constructArguments.getType();
				Constructor<?> constructor = clazz.getConstructor(typeArray);

				for(int i=0; i<typeArray.length; i++)
				{
					if(constructArguments.isSystemValue()[i])
					{
						if(typeArray[i] == Context.class && ((String)constructArguments.getArgument()[i]).equals("Context")) constructArguments.setArgument(i, ctx);
						else if(typeArray[i] == HashMap.class && constructArguments.getArgument()[i].equals("Connector Map")) constructArguments.setArgument(i, map);
						else constructArguments.setArgument(i, map.get((String)constructArguments.getArgument()[i]));
					}
				}
				component = constructor.newInstance(constructArguments.getArgument());
			}
			getParent().setComponent(componentName, component);

			Class<?>[] typeArray = methodArguments.getType();
			Method method = component.getClass().getMethod(methodName, typeArray);
			for(int i=0; i<typeArray.length; i++)
			{
				if(methodArguments.isSystemValue()[i])
				{
					if(typeArray[i] == Context.class && ((String)methodArguments.getArgument()[i]).equals("Context")) methodArguments.setArgument(i, ctx);
					else if(typeArray[i] == HashMap.class && methodArguments.getArgument()[i].equals("Connector Map")) methodArguments.setArgument(i, map);
					else methodArguments.setArgument(i, map.get((String)methodArguments.getArgument()[i]));
				}
			}
			Object rtn = null;
			try{
				rtn = method.invoke(component, methodArguments.getArgument());
			}catch(Exception e){
				throw new Exception("Component execute exception.");
			}
			if(isSetReturnValue) map.put(returnMapKey, rtn);
		}
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Component : " + map + "", false, false);
		}
		super.execute(ctx, map);
	}
}
