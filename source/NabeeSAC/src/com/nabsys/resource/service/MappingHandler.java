package com.nabsys.resource.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;

import com.nabsys.process.Context;
import com.nabsys.resource.script.FunctionExecutor;

public class MappingHandler implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServiceHandler 						nextService 		= null;
	private ServiceHandler						prevService			= null;
	private LinkedHashMap<String, Object> 		mappingData 		= new LinkedHashMap<String, Object>();
	private FunctionExecutor					compairExecutor		= null;
	private int 								handlerID 			= 0;
	private String								compairScript		= null;
	public MappingHandler(LinkedHashMap<String, Object> mappingData)
	{
		this.mappingData = mappingData;
		Random rand = new Random();
		handlerID = rand.nextInt(100000000);
	}
	
	public int getHandlerID()
	{
		return this.handlerID;
	}
	
	public void addNextService(ServiceHandler o){
		nextService = o;
	}
	
	public void addPrevService(ServiceHandler o){
		prevService = o;
	}
	
	public ServiceHandler getNextService()
	{
		return nextService;
	}
	
	public FunctionExecutor getCompairExecutor()
	{
		return compairExecutor;
	}
	
	public String getCompairScript()
	{
		return compairScript;
	}
	
	public LinkedHashMap<String, Object> getMappingData()
	{
		return mappingData;
	}
	
	public void addCompairExecutor(FunctionExecutor compairExecutor, String compairScript)
	{
		this.compairExecutor = compairExecutor;
		this.compairScript = compairScript;
	}
	
	protected boolean compare(HashMap<String, Object> map) throws Exception
	{
		if(compairExecutor == null) return true;
		else return ((Integer)compairExecutor.execute(map)) == 0;
	}
	
	protected void execute(Context ctx, HashMap<String, Object> map) throws Exception
	{
		if(mappingData != null && mappingData.size() > 0)
		{
			setMap(map, mappingData);
		}
		if(ctx.isTest())
		{
			ctx.offerTestMessage(getHandlerID(), "Connector : " + map + "", false, false);
		}
		
		if(nextService == prevService.getParent())
		{
			nextService.setBreak();
			return;
		}
		else
		{
			nextService.execute(ctx, map);
		}
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> copyMap(HashMap<String, Object> map)
	{
		HashMap<String, Object> newMap = new HashMap<String, Object>();
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
			Object obj = map.get(key);
			if(obj instanceof HashMap)
			{
				newMap.put(key, copyMap((HashMap<String, Object>)map.get(key)));
			}
			else if(obj instanceof ArrayList)
			{
				ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>();
				for(int i=0; i<((ArrayList<HashMap<String, Object>>)obj).size(); i++)
				{
					newList.add(copyMap(((ArrayList<HashMap<String, Object>>)obj).get(i)));
				}
				newMap.put(key, newList);
			}
			else
			{
				newMap.put(key, obj);
			}
		}
		
		return newMap;
	}
	
	@SuppressWarnings("unchecked")
	private void setMap(HashMap<String, Object> map, LinkedHashMap<String, Object> mapping) throws Exception
	{
		HashMap<String, Object> copyMap = copyMap(map);
		Iterator<String> itr = mapping.keySet().iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
			Object mappingObj = mapping.get(key);
			//delete map field
			if(mappingObj == null)
			{
				map.remove(key);
			}
			//new map field
			else if(key.matches("^\\|[0-9]+\\|$"))
			{
				if((mappingObj = mapping.get(key)) != null)
				{
					MappingObject mo = (MappingObject)mappingObj;
					if(mo.getFunctionExecutor() != null)
					{
						map.put(mo.getTargetMapKey(), mo.getFunctionExecutor().execute(copyMap));
					}
					else
					{
						map.put(mo.getTargetMapKey(), null);
					}
				}
			}
			else if((mappingObj = mapping.get(key)) instanceof LinkedHashMap)
			{
				
				//delete list
				if(key.matches("^.*:$"))
				{
					map.remove(key.split(":")[0]);
				}
				else
				{
					//new list
					if(key.matches("^:.*$"))
					{
						String trgListKey = key.split(":")[1];
						Iterator<String> subItr = ((LinkedHashMap<String, Object>)mappingObj).keySet().iterator();
						ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
						HashMap<String, Object> tmpMap = new HashMap<String, Object>();
						while(subItr.hasNext())
						{
							String subKey = subItr.next();
							if(subKey.equals("$")) continue;
							Object subMappingObj = ((LinkedHashMap<String, Object>)mappingObj).get(subKey);
							
							if(subMappingObj != null)
							{
								MappingObject mo = (MappingObject)subMappingObj;
								if(mo.getFunctionExecutor() != null)
								{
									tmpMap.put(mo.getTargetMapKey(), mo.getFunctionExecutor().execute(copyMap, 0));
								}
								else
								{
									tmpMap.put(mo.getTargetMapKey(), null);
								}
							}
						}
						list.add(tmpMap);
						map.put(trgListKey, list);
					}
					else
					{
						//0 => source name 1=>target name
						//"LIST_SRC:LIST_TGT => mapping info of LIST_TGT (LinkedHashMap<String, Object>)"
						String[] keyMap = key.split(":");
						
						if(!keyMap[0].equals(keyMap[1]))
						{
							map.put(keyMap[1], map.get(keyMap[0]));
							map.remove(keyMap[0]);
						}
						ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>)map.get(keyMap[1]);
						
						Iterator<String> subItr = ((LinkedHashMap<String, Object>)mappingObj).keySet().iterator();
						while(subItr.hasNext())
						{
							String subKey = subItr.next();
							if(subKey.equals("$")) continue;
							Object subMappingObj = ((LinkedHashMap<String, Object>)mappingObj).get(subKey);
							
							for(int i=0; i<list.size(); i++)
							{
								if(subMappingObj == null)
								{
									list.get(i).remove(subKey);
								}
								else
								{
									if(list.get(i).containsKey(subKey))
									{
										list.get(i).remove(subKey);
									}
									
									MappingObject mo = (MappingObject)subMappingObj;
									if(mo.getFunctionExecutor() != null)
									{
										list.get(i).put(mo.getTargetMapKey(), mo.getFunctionExecutor().execute(copyMap, i));
									}
									else
									{
										list.get(i).put(mo.getTargetMapKey(), ((ArrayList<HashMap<String, Object>>)copyMap.get(keyMap[0])).get(i).get(subKey));
									}
								}
							}
						}
					}
				}
			}
			else
			{
				if(map.containsKey(key))
				{
					map.remove(key);
					if((mappingObj = mapping.get(key)) != null)
					{
						MappingObject mo = (MappingObject)mappingObj;
						if(mo.getFunctionExecutor() != null)
						{
							map.put(mo.getTargetMapKey(), mo.getFunctionExecutor().execute(copyMap));
						}
						else
						{
							map.put(mo.getTargetMapKey(), copyMap.get(key));
						}
					}
				}
			}
		}
		//맵핑되지 않은 것들은 그대로 다음으로 넘어감.
	}
}
