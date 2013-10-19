package com.nabsys.process.instance;

import java.util.ArrayList;

import com.nabsys.net.socket.channel.Channel;
import com.nabsys.net.socket.channel.ServerSocketConstructor;




public class LifeCycleController extends Thread{
	
	private Object object = null;
	private ArrayList<Integer> list = null;
	private boolean isStart = false;
	private ServerSocketConstructor constructor = null;
	
	public LifeCycleController(InstanceThread instanceThread)
	{
		this.object = instanceThread;
	}
	
	public LifeCycleController(ServerSocketConstructor constructor, Channel channel)
	{
		this.constructor = constructor;
		this.object = channel;
	}
	
	public void run()
	{
		if(object instanceof InstanceThread)
		{
			InstanceThread instanceThread = (InstanceThread)object;
			isStart = true;
			instanceThread.exit();
			instanceThread.waitForExit();
			list.add(0);
		}
		else if(object instanceof Channel)
		{
			Channel channel = (Channel)object;
			isStart = true;
			channel.close();
			constructor.shutdownServer();
			list.add(0);
		}
	}
	
	public void waitForStart()
	{
		while(!isStart)
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void setList(ArrayList<Integer> list)
	{
		this.list = list;
	}
}
