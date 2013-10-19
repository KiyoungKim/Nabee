package com.nabsys.common.fileio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileLock;

public class ObjectFileIO {
	
	public ObjectFileIO()
	{
	}
	
	public void saveObject(String path, Object object) throws IOException
	{
		ByteArrayOutputStream byteOS = null;
		ObjectOutputStream objOS = null;
		byte[] byteData = null;
		
		FileLock lock = null;
		FileOutputStream fileOS = null;
		try{
			byteOS = new ByteArrayOutputStream();
			objOS = new ObjectOutputStream(byteOS);
			objOS.writeObject(object);
			
			byteData = byteOS.toByteArray();
			
			File file = new File(path);
			fileOS = new FileOutputStream(file);
			
			lock = fileOS.getChannel().tryLock(); 

			fileOS.write(byteData);
			fileOS.flush();
			
			
		} finally {
			if(lock != null) lock.release();
			if(objOS != null) objOS.close();
			if(byteOS != null) byteOS.close();
			if(fileOS != null) fileOS.close();
		}
	}
	
	public Object recoverObject(String path) throws IOException, ClassNotFoundException
	{
		File file = new File(path);
		FileInputStream fileIS = null;
		byte[] byteData = null;
		ByteArrayInputStream byteIS = null;
		ObjectInputStream objIS = null;
		Object object = null;
		try{
			fileIS = new FileInputStream(file);
			byteData = new byte[(int) file.length()];
			fileIS.read(byteData);
			byteIS = new ByteArrayInputStream(byteData);
			objIS = new ObjectInputStream(byteIS);
			object = objIS.readObject();
		} finally {
			if(objIS != null) objIS.close();
			if(byteIS != null) byteIS.close();
			if(fileIS != null) fileIS.close();
		}
		return object;
	}
	
	public Object recoverObject(byte[] byteData) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream byteIS = null;
		ObjectInputStream objIS = null;
		Object object = null;
		try{
			byteIS = new ByteArrayInputStream(byteData);
			objIS = new ObjectInputStream(byteIS);
			
			object = objIS.readObject();
		} finally {
			if(objIS != null) objIS.close();
			if(byteIS != null) byteIS.close();
		}
		return object;
	}
	
	public byte[] convertToByte(Object object) throws IOException
	{
		ByteArrayOutputStream byteOS = null;
		ObjectOutputStream objOS = null;
		byte[] byteData = null;
		
		try{
			byteOS = new ByteArrayOutputStream();
			objOS = new ObjectOutputStream(byteOS);
			objOS.writeObject(object);
			
			byteData = byteOS.toByteArray();
		} finally {
			if(objOS != null) objOS.close();
			if(byteOS != null) byteOS.close();
		}
		
		return byteData;
	}
}
