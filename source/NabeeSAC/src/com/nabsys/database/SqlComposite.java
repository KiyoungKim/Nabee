package com.nabsys.database;

import java.util.ArrayList;

public class SqlComposite {
	private String sql = "";
	private ArrayList<String> params = new ArrayList<String>();
	
	protected String getSql() {
		return sql;
	}
	protected void setSql(String sql) {
		this.sql += sql;
	}
	public ArrayList<String> getParams() {
		return params;
	}
	public void setParams(ArrayList<String> params) {
		this.params.addAll(params);
	}
	
}
