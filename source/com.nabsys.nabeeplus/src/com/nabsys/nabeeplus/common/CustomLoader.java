package com.nabsys.nabeeplus.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class CustomLoader extends ClassLoader{
	@SuppressWarnings("resource")
	public Class<?> getCustomClass(String className, String filePath) throws IOException
	{
		byte[] binClass = null;
		File classFile = null;
		FileInputStream fis = null;
		int binLen = 0;
		
		classFile = new File(filePath);
		fis = new FileInputStream(classFile);
		binClass = new byte[(int)classFile.length()];
		binLen = fis.read(binClass);
		return defineClass(className, binClass, 0, binLen);
	}
	
	public Class<?> getCustomClass(String className, byte[] binClass) throws IOException
	{
		return defineClass(className, binClass, 0, binClass.length);
	}
}