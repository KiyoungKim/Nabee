package com.nabsys.process.instance;

import java.util.ArrayList;

import com.nabsys.common.logger.NLogger;
import com.nabsys.process.ResourceFactory;


public class InstanceRuntime {
	private static ArrayList<LifeCycleController> shutdownThreadList =  new ArrayList<LifeCycleController>();

	private static boolean isCalled = false;
	private static ResourceFactory resourceFactory = null;
	
	public static void setResourceFactory(ResourceFactory resourceFactory)
	{
		InstanceRuntime.resourceFactory = resourceFactory;
	}
	
	public static void addShutdownHook(LifeCycleController shutdownThread)
	{
		InstanceRuntime.shutdownThreadList.add(shutdownThread);
	}
	
	public static void exit()
	{
		if(isCalled) return;
		isCalled = true;

		Thread exitThread = new Thread(new Runnable(){
			public void run() {

				final NLogger logger = NLogger.getLogger(this.getClass());

				int shutdownCnt = shutdownThreadList.size();
				ArrayList<Integer> shutdownList = new ArrayList<Integer>();
				for(int i=0; i<shutdownCnt; i++)
				{
					LifeCycleController lifeCycleController = shutdownThreadList.get(i);
					lifeCycleController.setList(shutdownList);
					lifeCycleController.start();
					lifeCycleController.waitForStart();
				}
				
				int tryCnt = 0;

				while(shutdownList.size() != shutdownCnt)
				{
					if(tryCnt > 6000) break;
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
					
					tryCnt++;
				}

				resourceFactory.closeResources();

				logger.info(0x0040);
				
				System.exit(0);
			}
		});
		
		exitThread.start();
	}
	
}
