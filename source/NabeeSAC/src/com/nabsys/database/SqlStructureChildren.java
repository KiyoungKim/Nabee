package com.nabsys.database;

import java.util.LinkedHashMap;

public class SqlStructureChildren extends LinkedHashMap<String, SqlStructure> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6040102005808432786L;

	public SqlStructure remove(String key)
	{
		SqlStructure me = get(key);
		int myDencendantsCnt = me.getDescendatnCnt();
		SqlStructure parent = me.getParent();
		
		SqlStructure rtn = super.remove(key);
		
		if(parent != null)
		{
			parent.removeDesencendantCnt(myDencendantsCnt + 1);
		}
		
		return rtn;
	}
}
