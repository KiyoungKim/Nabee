package com.nabsys.resource.script;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuncParam implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key = null;
	private String subKey = null;
	private boolean isList = false;
	public FuncParam(String key) throws Exception
	{
		if(key.matches(":[a-zA-Z0-9_]+\\[:[a-zA-Z0-9_]+\\]"))
		{
			Pattern lp = Pattern.compile("\\[:[a-zA-Z0-9_]+\\]");
			Matcher lm = lp.matcher(key);
			if(lm.find())
			{
				this.key = key.substring(1, lm.start());
				this.subKey = key.substring(lm.start() + 2, lm.end() - 1);
				isList = true;
			}
			else
			{
				throw new Exception("Parameter pattern error. : " + key);
			}
		}
		else
		{
			this.key = key.substring(1);
		}
	}
	
	public Object getValue(HashMap<String, Object> map) throws Exception
	{
		if(isList) throw new Exception("List field key type requires index argument.");
		if(!map.containsKey(key)) return null;
		Object val = map.get(key);
		if(val instanceof String) return new StringBuilder((String)val);
		else if(val instanceof Integer) return new BigDecimal((Integer)val);
		else if(val instanceof Long) return new BigDecimal((Long)val);
		else if(val instanceof Float) return new BigDecimal((Float)val);
		else if(val instanceof Double) return new BigDecimal((Double)val);
		else return val;
	}
	
	@SuppressWarnings("unchecked")
	public Object getValue(HashMap<String, Object> map, int index) throws Exception
	{
		if(!isList)
		{
			return getValue(map);
		}
		else
		{
			if(!map.containsKey(key)) return null;
			ArrayList<HashMap<String, Object>> list = null;
			try{
			list = (ArrayList<HashMap<String, Object>>)map.get(key);
			} catch(Exception e){
				throw new Exception("Can't find list using key '" + key + "'.");
			}
			if(!list.get(index).containsKey(subKey)) throw new Exception("Mapping list doesn't contains Field key '" +subKey+ "'.");
			Object val = list.get(index).get(subKey);
			
			if(val instanceof String) return new StringBuilder((String)val);
			else if(val instanceof Integer) return new BigDecimal((Integer)val);
			else if(val instanceof Long) return new BigDecimal((Long)val);
			else if(val instanceof Float) return new BigDecimal((Float)val);
			else if(val instanceof Double) return new BigDecimal((Double)val);
			else return val;
		}
	}
}
