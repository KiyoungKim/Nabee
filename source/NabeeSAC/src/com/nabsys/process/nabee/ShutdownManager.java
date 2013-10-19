package com.nabsys.process.nabee;

import com.nabsys.common.logger.NLogger;


public class ShutdownManager extends Thread {
	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	ThreadControler thread = null;
	
	public ShutdownManager(ThreadControler thread)
	{
		this.thread = thread;
	}
	
	public void run()
	{
		try {
			
			if(thread != null)
			{
				logger.info(0x0012);
				this.thread.exit();
				
				int tryNum = 0;
		
				while(!this.thread.isExit())
				{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						logger.error(e, 0x0016);
					}
					
					if(tryNum%10 == 0 && tryNum != 0)
						logger.info("Shutting down try count : " + (tryNum/10));
					
					if(tryNum > 600)
					{
						logger.fatal(0x0013);
						return;
					}
					
					tryNum++;
				}
			}
		} catch (NullPointerException e) {
			
		} catch (Exception e) {
			
		}
		
		if(thread != null && thread.isExit())
		{
			logger.info(0x0014);
		}
		else
		{
			logger.info(0x0015);
		}
		
		return;
	}
}
