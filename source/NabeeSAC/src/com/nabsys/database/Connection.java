package com.nabsys.database;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.label.NLabel;
import com.nabsys.process.ResourceFactory;


public class Connection implements java.sql.Connection{
	private java.sql.Connection connection = null;
	private ResourceFactory resourceFactory = null;
	
	public Connection(java.sql.Connection connection)
	{
		synchronized (this) {
			this.connection = connection;
		}
	}
	
	public Connection(java.sql.Connection connection, ResourceFactory resourceFactory)
	{
		synchronized (this) {
			this.connection = connection;
			this.resourceFactory = resourceFactory;
		}
	}
	
	public void clearWarnings() throws SQLException
	{
		connection.clearWarnings();
	}
	
	public void commit() throws SQLException
	{
		connection.commit();
	}
	
	public ResultSet select(String url) throws SQLException, NotFoundException, ClassCastException, IOException, ClassNotFoundException
	{
		Statement st = null;
		
		//int splitIndex = url.lastIndexOf('.');
		//if(splitIndex <= 0) throw new SQLException(NLabel.get(0x0071));
		
		SqlContext sqlContext = null;
		SqlComposite sqlComposite = null;
		synchronized(sqlContext = resourceFactory.getSql(url)){
			if(sqlContext.isNeedParam()) throw new SQLException(NLabel.get(0x0073));
			sqlComposite = sqlContext.getSql(null);
		}
		
		st = connection.createStatement();
		return new ResultSet(st, sqlComposite.getSql());
	}
	
	public String getQuery(String url) throws SQLException, NotFoundException, IOException, ClassNotFoundException
	{
		//int splitIndex = url.lastIndexOf('.');
		//if(splitIndex <= 0) throw new SQLException(NLabel.get(0x0071));
		
		SqlContext sqlContext = null;
		String sql = "";

		synchronized(sqlContext = resourceFactory.getSql(url)){
			if(sqlContext.isNeedParam()) throw new SQLException(NLabel.get(0x0073));
			SqlComposite sqlComposite = sqlContext.getSql(null);
			sql = sqlComposite.getSql();
		}
	
		return sql;
	}
	
	public String getQuery(String url, HashMap<String, Object> parameters) throws SQLException, NotFoundException, IOException, ClassNotFoundException
	{
		//int splitIndex = url.lastIndexOf('.');
		//if(splitIndex <= 0) throw new SQLException(NLabel.get(0x0071));
		
		SqlContext sqlContext = null;
		String sql = "";
		ArrayList<String> paramArry = null;
		synchronized(sqlContext = resourceFactory.getSql(url)){
			if(sqlContext.isNeedParam())
			{
				SqlComposite sqlComposite = sqlContext.getSql(parameters);
				
				paramArry = sqlComposite.getParams();
				sql = sqlComposite.getSql();
			}
			else
			{
				SqlComposite sqlComposite = sqlContext.getSql(null);
				sql = sqlComposite.getSql();
			}
			
		}
		
		for(int i=0; paramArry != null && i<paramArry.size(); i++)
		{
			sql = sql.replaceFirst("\\?", "\'" + parameters.get(paramArry.get(i)) + "\'");
		}
		
		return sql;
	}
	
	public ResultSet select(String url, HashMap<String, Object> parameters) throws SQLException, NotFoundException, ClassCastException, IOException, ClassNotFoundException
	{
		PreparedStatement pstmt = null;
		
		//int splitIndex = url.lastIndexOf('.');
		//if(splitIndex <= 0) throw new SQLException(NLabel.get(0x0071));
		
		SqlContext sqlContext = null;
		String sql = "";
		ArrayList<String> paramArry = null;
		synchronized(sqlContext = resourceFactory.getSql(url)){
			if(sqlContext.isNeedParam())
			{
				SqlComposite sqlComposite = sqlContext.getSql(parameters);
				
				paramArry = sqlComposite.getParams();
				sql = sqlComposite.getSql();
			}
			else
			{
				SqlComposite sqlComposite = sqlContext.getSql(parameters);
				sql = sqlComposite.getSql();
			}
			
		}
		
		pstmt = prepareStatement(sql);

		for(int i=1; paramArry != null && i<=paramArry.size(); i++)
		{
			pstmt.setObject(i, parameters.get(paramArry.get(i-1)));
		}
		return new ResultSet(pstmt);
	}
	
	public int update(String url) throws SQLException, NotFoundException, ClassCastException, IOException, ClassNotFoundException
	{
		Statement st = null;
		
		//int splitIndex = url.lastIndexOf('.');
		//if(splitIndex <= 0) throw new SQLException(NLabel.get(0x0071));
		
		SqlContext sqlContext = null;
		SqlComposite sqlComposite = null;
		synchronized(sqlContext = resourceFactory.getSql(url)){
			if(sqlContext.isNeedParam()) throw new SQLException(NLabel.get(0x0073));
			sqlComposite = sqlContext.getSql(null);
		}
		
		try{
			st = connection.createStatement();
			return st.executeUpdate(sqlComposite.getSql());
		}finally{
			if(st!=null) st.close();
		}
	}
	
	public int update(String url, HashMap<String, Object> parameters) throws SQLException, NotFoundException, ClassCastException, IOException, ClassNotFoundException
	{
		PreparedStatement pstmt = null;
		
		//int splitIndex = url.lastIndexOf('.');
		//if(splitIndex <= 0) throw new SQLException(NLabel.get(0x0071));
		
		SqlContext sqlContext = null;
		String sql = "";
		ArrayList<String> paramArry = null;
		synchronized(sqlContext = resourceFactory.getSql(url)){
			if(sqlContext.isNeedParam())
			{
				SqlComposite sqlComposite = sqlContext.getSql(parameters);
				
				paramArry = sqlComposite.getParams();
				sql = sqlComposite.getSql();
			}
			else
			{
				SqlComposite sqlComposite = sqlContext.getSql(null);
				sql = sqlComposite.getSql();
			}
		}
		
		try{
			pstmt = prepareStatement(sql);
				
			for(int i=1; paramArry != null && i<=paramArry.size(); i++)
			{
				pstmt.setObject(i, parameters.get(paramArry.get(i-1)));
			}
	
			return pstmt.executeUpdate();
		}finally{
			if(pstmt != null) pstmt.close();
		}
	}

	public Statement createStatement() throws SQLException
	{
		return connection.createStatement();
	}
	
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
	{
		return connection.createStatement(resultSetType, resultSetConcurrency);
	}
	
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	{
		return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	public boolean equals(Object obj)
	{
		return this.equals(obj);
	}
	
	public boolean getAutoCommit() throws SQLException
	{
		return connection.getAutoCommit();
	}
	
	public String getCatalog() throws SQLException
	{
		return connection.getCatalog();
	}
	
	public int getHoldability() throws SQLException
	{
		return connection.getHoldability();
	}
	
	public DatabaseMetaData getMetaData() throws SQLException
	{
		return connection.getMetaData();
	}
	
	public int getTransactionIsolation() throws SQLException
	{
		return connection.getTransactionIsolation();
	}
	
	public Map<String, Class<?>> getTypeMap() throws SQLException
	{
		return connection.getTypeMap();
	}
	
	public SQLWarning getWarnings() throws SQLException
	{
		return connection.getWarnings();
	}
	
	public int hashCode()
	{
		return this.hashCode();
	}
	
	public boolean isClosed() throws SQLException
	{
		return connection.isClosed();
	}
	
	public boolean isReadOnly() throws SQLException
	{
		return connection.isReadOnly();
	}

	public String nativeSQL(String sql) throws SQLException
	{
		return connection.nativeSQL(sql);
	}
	
	public CallableStatement prepareCall(String sql) throws SQLException
	{
		return connection.prepareCall(sql);
	}
	
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
	{
		return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}
	
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	{
		return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		return connection.prepareStatement(sql);
	}
	
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
	{
		return connection.prepareStatement(sql, autoGeneratedKeys);
	}
	
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
	{
		return connection.prepareStatement(sql, columnIndexes);
	}
	
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
	{
		return connection.prepareStatement(sql, columnNames);
	}
	
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
	{
		return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}
	
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	{
		return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}
	
	public void rollback() throws SQLException
	{
		connection.rollback();
	}
	
	public void rollback(Savepoint savepoint) throws SQLException
	{
		connection.rollback(savepoint);
	}
	
	public void setAutoCommit(boolean autoCommit) throws SQLException
	{
		connection.setAutoCommit(autoCommit);
	}
	
	public void setCatalog(String catalog) throws SQLException
	{
		connection.setCatalog(catalog);
	}
	
	public void setHoldability(int holdability) throws SQLException
	{
		connection.setHoldability(holdability);
	}
	
	public void setReadOnly(boolean readOnly) throws SQLException
	{
		connection.setReadOnly(readOnly);
	}
	
	public Savepoint setSavepoint() throws SQLException
	{
		return connection.setSavepoint();
	}
	
	public Savepoint setSavepoint(String name) throws SQLException
	{
		return connection.setSavepoint(name);
	}
	
	public void setTransactionIsolation(int level) throws SQLException
	{
		connection.setTransactionIsolation(level);
	}
	
	public void setTypeMap(Map<String, Class<?>>map) throws SQLException
	{
		connection.setTypeMap(map);
	}
	
	public void close() throws SQLException
	{
		if(connection != null)
		{
			connection.close();
			connection = null;
		}
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		connection.releaseSavepoint(savepoint);
	}
}
