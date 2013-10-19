package component;

import java.util.ArrayList;
import java.util.HashMap;

import com.nabsys.common.logger.NLogger;

public class SelectBatch {
	
	private final NLogger logger = NLogger.getLogger(this.getClass());
	
	public HashMap<String, String> setHashMap()
	{
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		rtnMap.put("USER_ID", "HKD");
		logger.info(rtnMap);
		return rtnMap;
	}
	
	public void printResult(ArrayList<HashMap<String, String>> resultList)
	{
		logger.info("Start Select Batch Printing====>");
		try{
			logger.info("SELECT COUNT : " + resultList.size());
			
			for(int i=0; i<resultList.size(); i++)
			{
				HashMap<String, String> tmp = resultList.get(i);
				logger.info(tmp.get("USER_NAME") + " | " + tmp.get("USER_ID") + " | " + tmp.get("USER_PHONE"));
			}
		}catch(NullPointerException e){
			logger.error(e, e.getMessage());
		}catch(Exception e){
			logger.error(e, e.getMessage());
		}
		
		logger.info("End Select Batch Printing====>");
	}
}
