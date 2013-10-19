package component;

import com.nabsys.common.logger.NLogger;

public class BatchTimeCheck {
	final NLogger logger = (NLogger)NLogger.getLogger(this.getClass());
	
	public void minRepeat()
	{
		logger.info("MINUTE repeat batch run....");
	}
	
	public void timeRepeat()
	{
		logger.info("TIME repeat batch run....");
	}
	
	public void dayRepeat()
	{
		logger.info("DAY repeat batch run....");
	}
}
