package com.nabsys.process;

import java.util.HashMap;

import com.nabsys.process.exception.PluginInitializationException;



public interface IPlugin {
	public Object initializer(HashMap<String, String> params) throws PluginInitializationException; 
	public void finalizer();
}
