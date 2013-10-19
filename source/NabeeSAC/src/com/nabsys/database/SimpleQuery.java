package com.nabsys.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Level;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.ResourceFactory;

public class SimpleQuery {
	
	private final NLogger logger = NLogger.getLogger(this.getClass());

	private Connection connection = null;
	private ResourceFactory resourceFactory = null;
	public SimpleQuery(ResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
	public void setConnection(String dbpluginName)
	{
		try{
			DBPoolManager manager = (DBPoolManager)resourceFactory.getPlugin(dbpluginName);
			connection = manager.getConnection();
			
		}catch(NullPointerException e){
			logger.error(e, e.getMessage());
		}
		catch(Exception e)
		{
			logger.error(e, e.getMessage());
		}
	}
	
	public Connection getConnection()
	{
		return connection;
	}
	
	public NBFields execute(String url, NBFields parameters) throws ClassCastException, SQLException, NotFoundException, IOException, ClassNotFoundException
	{
		String sql = connection.getQuery(url, setParams(parameters)).toUpperCase();
		if(sql.contains("INSERT "))
		{
			return update(url, parameters);
		}
		else if(sql.contains("UPDATE "))
		{
			return update(url, parameters);
		}
		else if(sql.contains("DELETE "))
		{
			return update(url, parameters);
		}
		else
		{
			return select(url, parameters);
		}
	}
	
	public ArrayList<HashMap<String, Object>> select(String url, HashMap<String, Object> parameters) throws ClassCastException, SQLException, NotFoundException
	{
		try{
			ArrayList<HashMap<String, Object>> fields = new ArrayList<HashMap<String, Object>>();
			ResultSet rs = null;
			
			try{
				if(logger.isEnabledFor(Level.DEBUG));
					logger.debug("EXECUTE ID : " + url + "\r" + connection.getQuery(url, parameters));
					
				rs = connection.select(url, parameters);
				
				while(rs.next())
				{
					HashMap<String, Object> tmp = new HashMap<String, Object>();
					for(int i=0; i<rs.getColumnCount(); i++)
					{
						if(rs.getObject(i + 1) instanceof byte[])
						{
							byte[] value = (byte[])rs.getObject(i + 1);
							tmp.put(rs.getColumnLabel(i + 1), value == null?"":value);
						}
						else
						{
							String value = String.valueOf(rs.getObject(i + 1));
							tmp.put(rs.getColumnLabel(i + 1), value == null?"":value);
						}
					}
					
					fields.add(tmp);
				}
			}finally{
				if(rs != null)
					rs.close();
			}
			
			return fields;
		}catch(NullPointerException e){
			logger.error(e, e.getMessage());
		}
		catch(Exception e)
		{
			logger.error(e, e.getMessage());
		}
		return null;
	}
	
	public NBFields select(String url, NBFields parameters) throws ClassCastException, SQLException, NotFoundException, IOException, ClassNotFoundException
	{
		NBFields fields = new NBFields();
		ArrayList<NBFields> list = new ArrayList<NBFields>();
		ResultSet rs = null;
		
		try{
			if(logger.isEnabledFor(Level.DEBUG));
				logger.debug("EXECUTE ID : " + url + "\r" + connection.getQuery(url, setParams(parameters)));
			
			rs = connection.select(url, setParams(parameters));

			while(rs.next())
			{
				NBFields tmp = new NBFields();
				for(int i=0; i<rs.getColumnCount(); i++)
				{
					if(rs.getObject(i + 1) instanceof byte[])
					{
						byte[] value = (byte[])rs.getObject(i + 1);
						tmp.put(rs.getColumnLabel(i + 1), value == null?"":value);
					}
					else
					{
						String value = String.valueOf(rs.getObject(i + 1));
						tmp.put(rs.getColumnLabel(i + 1), value == null?"":value);
					}
				}
				list.add(tmp);
			}
		}finally{
			if(rs != null)
				rs.close();
		}
		fields.put("LIST", list);
		return fields;
	}
	
	public HashMap<String, String> insert(String url, HashMap<String, Object> parameters) throws SQLException
	{
		return update(url, parameters);
	}
	
	public NBFields insert(String url, NBFields parameters) throws SQLException
	{
		return update(url, parameters);
	}
	
	public HashMap<String, String> update(String url, HashMap<String, Object> parameters) throws SQLException
	{
		HashMap<String, String> fields = new HashMap<String, String>();
		
		boolean isAutoCommit = connection.getAutoCommit();
		try{
			connection.setAutoCommit(false);
			
			if(logger.isEnabledFor(Level.DEBUG));
				logger.debug("EXECUTE ID : " + url + "\r" + connection.getQuery(url, parameters));
			
			int updateCount = connection.update(url, parameters);
			connection.commit();
			fields.put("ROWS", Integer.toString(updateCount));
			fields.put("RESULT", updateCount + " rows updated.");
		} catch (ClassCastException e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", "0");
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		} catch (SQLException e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", "0");
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		} catch (NotFoundException e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", "0");
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		}  catch (NullPointerException e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", "0");
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		} catch (Exception e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", "0");
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		} finally{
			connection.setAutoCommit(isAutoCommit);
		}
		
		return fields;
	}
	
	public NBFields update(String url, NBFields parameters) throws SQLException
	{
		NBFields fields = new NBFields();
		
		boolean isAutoCommit = connection.getAutoCommit();
		try{
			connection.setAutoCommit(false);
			
			if(logger.isEnabledFor(Level.DEBUG));
				logger.debug("EXECUTE ID : " + url + "\r" + connection.getQuery(url, setParams(parameters)));
				
			int updateCount = connection.update(url, setParams(parameters));
			connection.commit();
			fields.put("ROWS", updateCount);
			fields.put("RESULT", updateCount + " rows updated.");
		} catch (ClassCastException e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", 0);
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		} catch (SQLException e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", 0);
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		} catch (NotFoundException e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", 0);
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		}  catch (NullPointerException e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", 0);
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		} catch (Exception e) {
			logger.error(e, e.getMessage());
			fields.put("ROWS", 0);
			fields.put("RESULT", e.getMessage());
			connection.rollback();
		} finally{
			connection.setAutoCommit(isAutoCommit);
		}
		
		return fields;
	}
	
	public HashMap<String, String> delete(String url, HashMap<String, Object> parameters) throws SQLException
	{
		return update(url, parameters);
	}
	
	public NBFields delete(String url, NBFields parameters) throws SQLException
	{
		return update(url, parameters);
	}
	
	public NBFields hierarchySelect(String url, NBFields parameters, String keyFieldName, String parentFieldName) throws ClassCastException, SQLException, NotFoundException, IOException, ClassNotFoundException
	{
		NBFields fields = new NBFields();
		ArrayList<NBFields> list = new ArrayList<NBFields>();
		ResultSet rs = null;
		
		try{
			parameters.put("RSV_ISTOP", "Y");
			if(logger.isEnabledFor(Level.DEBUG));
				logger.debug("EXECUTE ID : " + url + "\r" + connection.getQuery(url, setParams(parameters)));
			
			rs = connection.select(url, setParams(parameters));

			while(rs.next())
			{
				NBFields tmp = new NBFields();
				for(int i=0; i<rs.getColumnCount(); i++)
				{
					String value = String.valueOf(rs.getObject(i + 1));
					tmp.put(rs.getColumnLabel(i + 1), value == null?"":value);
				}
				tmp.put("LEVEL", 0);
				list.add(tmp);
				hierarchySelect(url, parameters, parentFieldName, (String)tmp.get(keyFieldName), keyFieldName, list, 0);
			}
		}finally{
			if(rs != null)
				rs.close();
		}

		fields.put("LIST", list);
		return fields;
	}
	
	private void hierarchySelect(String url, NBFields parameters, String parentFieldName, String parentKey, String keyFieldName, ArrayList<NBFields> list, int level) throws NotFoundException, SQLException, IOException, ClassNotFoundException
	{
		parameters.put("RSV_ISTOP", "N");
		parameters.put(parentFieldName, parentKey);
		level++;
		
		if(logger.isEnabledFor(Level.DEBUG));
		logger.debug("EXECUTE ID : " + url + "\r" + connection.getQuery(url, setParams(parameters)));
	
		ResultSet rs = connection.select(url, setParams(parameters));
		
		try{
			while(rs.next())
			{
				NBFields tmp = new NBFields();
				for(int i=0; i<rs.getColumnCount(); i++)
				{
					String value = String.valueOf(rs.getObject(i + 1));
					tmp.put(rs.getColumnLabel(i + 1), value == null?"":value);
				}
				tmp.put("LEVEL", level);
				list.add(tmp);
				hierarchySelect(url, parameters, parentFieldName, (String)tmp.get(keyFieldName), keyFieldName, list, level);
			}
		}finally{
			if(rs != null)
				rs.close();
		}
	}
	
	private HashMap<String, Object> setParams(NBFields parameters)
	{
		HashMap<String, Object> rtnMap = new HashMap<String, Object>();
		
		Set<String> keySet = parameters.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
			
			Object value = parameters.get(key);
			
			rtnMap.put(key, value);
		}
		
		return rtnMap;
	}
	
	public void close() throws SQLException
	{
		connection.close();
	}
}
