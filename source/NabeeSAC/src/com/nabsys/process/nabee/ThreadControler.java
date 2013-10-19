package com.nabsys.process.nabee;

import java.io.IOException;

import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.NabeeProtocol;

public abstract class ThreadControler extends Thread {
	protected 	boolean 			isExit 		= false;
	protected 	boolean 			exit 		= false;
	protected 	boolean 			isStart 	= false;
	private 	NabeeProtocol 		protocol 	= null;
	private 	long 				sequence 	= -1; 
	private		ResourceParameter 	rscPrm 		= null;
	private		String				instanceName = "";
	private		boolean				isInstance	= false;
	
	public ThreadControler()
	{
		
	}
	
	public ThreadControler(ResourceParameter rscPrm)
	{
		this.rscPrm = rscPrm;
	}
	
	public ThreadControler(ResourceParameter rscPrm, NabeeProtocol protocol, long sequence)
	{
		this.rscPrm = rscPrm;
		this.protocol = protocol;
		this.sequence = sequence;
		this.rscPrm.setThreadControler(sequence, this);
	}
	
	abstract protected void runWorker();
	
	protected void setInstanceName(String name)
	{
		isInstance = true;
		instanceName = name;
		this.rscPrm.addInstanceThread();
	}
	
	public boolean isInstance()
	{
		return this.isInstance;
	}
	
	public String getInstanceName()
	{
		return this.instanceName;
	}
	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	public void exit()
	{
		this.exit = true;
		
		if(protocol != null)
		{
			try {
				protocol._close();
			} catch (IOException e) {
				logger.error(e, e.getMessage());
			}
		}
	}
	
	public boolean isStart()
	{
		int tryCnt = 0;
		while(!isStart)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
			if(tryCnt >= 60) break;//1ºÐ ´ë±â
			
			tryCnt++;
		}
		return isStart;
	}
	
	public void run()
	{
		isStart = true;
		
		try{
			runWorker();
		}finally{
			if(!this.exit)
			{
				if(protocol != null)
				{
					try {
						protocol._close();
					} catch (IOException e) {
						logger.error(e, e.getMessage());
					}
				}
			}
			
			if(rscPrm != null && sequence >= 0)
			{
				rscPrm.removeThreadControler(this.sequence, isInstance);
			}
			
			isExit = true;
		}
	}
	
	public boolean isExit()
	{
		return this.isExit;
	}

	public NabeeProtocol getProtocol() {
		return protocol;
	}
	
	public ResourceParameter getResourceParameter()
	{
		return this.rscPrm;
	}
	
	protected long getSequence()
	{
		return this.sequence;
	}

}
