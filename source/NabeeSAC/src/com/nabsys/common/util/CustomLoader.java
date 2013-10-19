package com.nabsys.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;


public class CustomLoader extends ClassLoader{
	private String className = "";
	private String filePath = "";
	private boolean isCompressed = false;
	
	public CustomLoader()
	{
	}
	
	public CustomLoader(String className, String filePath)
	{
		this.className = className;
		this.filePath = filePath;
		
		File file = new File(filePath);
		this.isCompressed = !file.exists();
	}
	
	public Class<?> getCustomClass() throws IOException
	{
		return getCustomClass(className, filePath);
	}
	
	public Class<?> getCustomClass(String className, String filePath) throws IOException
	{
		File file = new File(filePath);
		if(file.exists())
		{
			this.className = className;
			this.filePath = filePath;
			this.isCompressed = false;
			byte[] classByte = null;
			return defineClass(className, classByte = getClassBytes(), 0, classByte.length);
		}
		else
		{
			return getCustomClass(className);
		}
	}
	
	public Class<?> getCustomClass(String className) throws IOException
	{
		this.className = className;
		this.filePath = className.replaceAll("\\.", "/") + ".class";
		this.isCompressed = true;
		byte[] classByte = null;
		
		return defineClass(className, classByte = getClassBytes(), 0, classByte.length);
	}
	
	public String[] getParameterNames(Method method)
	{
		LocalVariable[] lvt = method.getLocalVariableTable().getLocalVariableTable();
		org.apache.bcel.generic.Type[] types = method.getArgumentTypes();
		
		String[] parameterNames = new String[types.length];
		
		if(types.length > 0)
		{
			for(int j=0; j<types.length; j++)
			{
				parameterNames[j] = lvt[j+1].getName();
			}
		}
		
		return parameterNames;
	}
	
	public String[] getParameterTypes(Method method)
	{
		org.apache.bcel.generic.Type[] types = method.getArgumentTypes();
		String[] strTypes = new String[types.length];
		for(int i=0; i<types.length; i++)
		{
			String strType = types[i].toString();
			String tmpParamString = strType.substring(strType.lastIndexOf(".") + 1);
			strTypes[i] = tmpParamString;
		}
		
		return strTypes;
	}
	
	public String getParameterTypeString(Method method)
	{
		org.apache.bcel.generic.Type[] types = method.getArgumentTypes();

		String paramString = "";
		for(int i=0; i<types.length; i++)
		{
			String strType = types[i].toString();
			String tmpParamString = strType.substring(strType.lastIndexOf(".") + 1);
			paramString = paramString + tmpParamString;
			if(i < types.length - 1) paramString = paramString + ", ";
		}
		
		if(paramString.equals("")) paramString = "void";
		
		return paramString;
	}
	
	public org.apache.bcel.classfile.Method[] getMethods() throws IOException
	{
		ByteArrayInputStream bis = null;
		JavaClass clazz = null;
		try{
			bis = new ByteArrayInputStream(getClassBytes());
			ClassParser parser = new ClassParser(bis, className);
			clazz = parser.parse();
		}finally{
			if(bis != null)
			{
				bis.close();
			}
		}
		return clazz.getMethods();
	}

	private byte[] getClassBytes() throws IOException
	{
		if(this.isCompressed)
		{
			InputStream is = null;
			try{
				is = getResourceAsStream(this.filePath);
				
				if(is == null) throw new FileNotFoundException("FileNotFoundException : " + this.filePath);
					
				byte[] binClass = new byte[is.available()];
				for(int i=0; i<binClass.length; i++)
				{
					binClass[i] = (byte)is.read();
				}
				
				return binClass;
			}finally{
				if(is != null)
				{
					is.close();
				}
			}
		}
		else
		{
			byte[] binClass = null;
			File classFile = null;
			FileInputStream fis = null;
			
			try{
				classFile = new File(filePath);
				fis = new FileInputStream(classFile);
				binClass = new byte[(int)classFile.length()];
				fis.read(binClass);
			}finally{
				if(fis != null)
				{
					fis.close();
				}
			}
			
			return binClass;
		}
	}
}