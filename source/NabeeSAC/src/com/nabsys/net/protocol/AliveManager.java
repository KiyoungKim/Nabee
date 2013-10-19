package com.nabsys.net.protocol;

public class AliveManager implements Runnable{
	private Protocol p = null;
	private int aliveInterval = 0;
	private boolean exit = false;
	private boolean isExit = false;
	
	public AliveManager(Protocol p, int aliveSecond)
	{
		this.p = p;
		this.aliveInterval = aliveSecond;
	}
	
	public void run()
	{
		while(true)
		{
			if(exit) break;
			
			try {
				p._sendAliveMessage();
			} catch (Throwable e) {
			}
			
			try {
				Thread.sleep(aliveInterval * 1000);
			} catch (InterruptedException e) {
			}
		}
		
		this.isExit = true;
	}
	
	public boolean isExit()
	{
		return this.isExit;
	}
	
	public void exit()
	{
		this.exit = true;
	}
}
