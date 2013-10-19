package com.nabsys.resource.service;

import java.util.Date;
import java.util.HashMap;

import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.DateUtil;
import com.nabsys.process.Context;
import com.nabsys.resource.BatchTimeList;
import com.nabsys.resource.ServerConfiguration;

public class TimeScheduleHandler  extends ServiceHandler{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BatchTimeList exTimeList = null;
	public TimeScheduleHandler(ServiceHandler parent, int x, int y, int width,
			int height) {
		super(parent, x, y, width, height);
	}
	public void setData(BatchTimeList exTimeList){
		this.exTimeList = exTimeList;
	}
	
	public BatchTimeList getTimeList()
	{
		return this.exTimeList;
	}
	
	public void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		TimeSector[] sectors = TimeSector.values();
		DateUtil dateUtil = new DateUtil(ServerConfiguration.getTimeLocale());
		Date curTime = new Date();
		boolean exc = true;
		for(int i=0; i<exTimeList.size(); i++)
		{
			exc = true;
			HashMap<TimeSector, Integer> exTime = exTimeList.get(i);
			for(int j=0; j<sectors.length; j++)
			{
				int setValue = exTime.get(sectors[j]);
				if(setValue < 0) continue;
				
				if(sectors[j] == TimeSector.YER && setValue != dateUtil.getYear(curTime))
				{
					exc = false;
					break;
				}
				else if(sectors[j] == TimeSector.MON && setValue != dateUtil.getMonth(curTime))
				{
					exc = false;
					break;
				}
				else if(sectors[j] == TimeSector.DAY && setValue != dateUtil.getDate(curTime))
				{
					exc = false;
					break;
				}
				else if(sectors[j] == TimeSector.HUR && setValue != dateUtil.getHour(curTime))
				{
					exc = false;
					break;
				}
				else if(sectors[j] == TimeSector.MIN && setValue != dateUtil.getMinute(curTime))
				{
					exc = false;
					break;
				}
			}
			if(exc) break;
		}
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Batch : " + map + "", false, false);
		}
		if(exc)
		{
			ThreadExecutor te = new ThreadExecutor(this, ctx, map);
			te.start();
			NLogger logger = ctx.getLogger(this.getClass());
			logger.info(NLabel.get(0x005F) + " - Service ID [" +ctx.getServiceID()+ "]");
		}
	}
}
