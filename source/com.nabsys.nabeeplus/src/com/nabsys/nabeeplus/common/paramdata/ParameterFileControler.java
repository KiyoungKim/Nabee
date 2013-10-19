package com.nabsys.nabeeplus.common.paramdata;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

import com.nabsys.nabeeplus.Application;

@SuppressWarnings("restriction")
public class ParameterFileControler {
	
	private ParameterMap parameterMap = null;
	private String path = "";
	
	public ParameterFileControler() throws IOException, ClassNotFoundException
	{
		Bundle bundle = Platform.getBundle(Application.PLUGIN_ID);
		URL fileURL = FileLocator.toFileURL(BundleUtility.find(bundle, "configuration/"));
		path = fileURL.getPath() + "paramdata.dat";
		File file = new File(path);
		
		if(!file.exists())
		{
			ObjectFileIO objIO = new ObjectFileIO();
			objIO.saveObject(path, parameterMap = new ParameterMap());
		}
		else
		{
			ObjectFileIO objIO = new ObjectFileIO();
			parameterMap = (ParameterMap) objIO.recoverObject(path);
		}
	}
	
	public ParameterMap getObject()
	{
		return this.parameterMap;
	}
	
	public void save() throws IOException, ClassNotFoundException
	{
		ObjectFileIO objIO = new ObjectFileIO();
		objIO.saveObject(path, parameterMap);
	}
}
