package com.nabsys.process;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.socket.channel.Channel;
import com.nabsys.process.instance.management.TestMessageQueue;
import com.nabsys.resource.PluginList;

public class Context {
	
	private SessionData 					session 			= null;
	private PluginList 						pluginList			= null;
	private ResourceFactory					resourceFactory		= null;
	private SocketAddress					remoteAddress		= null;
	private Channel							channel				= null;
	private String							serviceIDFieldID	= null;
	private String							serviceID			= null;
	private HashMap<String, Object>			data				= null;
	private BlockingQueue<TestMessageQueue> testMessageQueue 	= null;
	private boolean							isTest				= false;
	
	public Context(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
		this.data = new HashMap<String, Object>();
	}
	
	public void offerTestMessage(int handlerID, String message, boolean isErr, boolean isEnd)
	{
		try {
			if(testMessageQueue != null)
				testMessageQueue.offer(new TestMessageQueue(handlerID, message, isErr, isEnd), 5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
	}
	
	public void setTest()
	{
		this.isTest = true;
	}
	
	public boolean isTest()
	{
		return this.isTest;
	}
	
	public void initTestMessageQueue()
	{
		testMessageQueue = new LinkedBlockingQueue<TestMessageQueue>(100);
	}
	
	public void clearTestMessageQueue()
	{
		testMessageQueue = null;
	}
	
	public TestMessageQueue getTestMessage()
	{
		try {
			if(testMessageQueue != null)
				return testMessageQueue.poll(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		
		return null;
	}
	
	public void setPlugins(PluginList pluginList) throws Exception
	{
		if(this.pluginList != null) throw new Exception(NLabel.get(0x0099));
		this.pluginList = pluginList;
	}
	
	public void setSessionData(SessionData session)
	{
		this.session = session;
	}
	
	public ResourceFactory getResourceFactory()
	{
		return this.resourceFactory;
	}
	
	public Object getPlugins(String key)
	{
		return this.pluginList.get(key);
	}
	
	public SessionData getSessionData()
	{
		return this.session;
	}
	
	public Object getSessionData(String key)
	{
		return this.session.get(key);
	}

	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(SocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public void setServiceIDFieldID(String serviceIDFieldID){
		this.serviceIDFieldID = serviceIDFieldID;
	}
	public void setServiceID(String serviceId){
		this.serviceID = serviceId;
	}
	public String getServiceIDFieldID(){
		return serviceIDFieldID;
	}
	public String getServiceID(){
		return serviceID;
	}
	public NLogger getLogger(Class<?> clazz){
		return NLogger.getLogger(clazz);
	}
	
	public void setData(String key, Object data){
		this.data.put(key, data);
	}
	
	public void removeData(String key){
		this.data.remove(key);
	}
	
	public Object getData(String key){
		if(!data.containsKey(key)) return null;
		return this.data.get(key);
	}
}
