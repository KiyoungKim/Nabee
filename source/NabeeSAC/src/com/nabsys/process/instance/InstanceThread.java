package com.nabsys.process.instance;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;

import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.NabeeProtocol;

public abstract class InstanceThread extends Thread {
	
	protected final NLogger logger = NLogger.getLogger(this.getClass());
	
	protected 	boolean 			exit 		= false;
	private 	boolean 			isStart 	= false;
	private 	NabeeProtocol 		protocol 	= null;
	private 	int 				sequence 	= -1; 
	private		String				instanceName = "";
	private		boolean				isInstance	= false;
	private		Queue<Integer>		exitQueue	= null;
	
	public InstanceThread()
	{
		exitQueue = new PriorityQueue<Integer>(1);
	}
	
	abstract protected void runWorker();
	
	protected void setInstanceName(String name)
	{
		this.isInstance = true;
		this.instanceName = name;
	}
	
	public boolean isInstance()
	{
		return this.isInstance;
	}
	
	public String getInstanceName()
	{
		return this.instanceName;
	}
	
	public void exit()
	{
		this.exit = true;
		
		if(this.protocol != null)
		{
			try {
				this.protocol._close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isStart()
	{
		int tryCnt = 0;
		while(!this.isStart)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
			if(tryCnt >= 60) break;//1ºÐ ´ë±â
			
			tryCnt++;
		}
		return this.isStart;
	}
	
	public void run()
	{
		this.isStart = true;
		try{
			runWorker();
		} finally {
			exitQueue.offer(1);
		}
	}
	
	public void waitForExit()
	{
		while(exitQueue.poll() == null)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public NabeeProtocol getProtocol() {
		return this.protocol;
	}
	
	protected int getSequence()
	{
		return this.sequence;
	}

}
