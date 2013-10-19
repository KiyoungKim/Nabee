package com.nabsys.nabeeplus.common.paramdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileLock;

class ObjectFileIO {
	
	protected ObjectFileIO()
	{
	}
	
	protected void saveObject(String path, Object object) throws IOException
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
	
	protected Object recoverObject(String path) throws IOException, ClassNotFoundException
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
}
