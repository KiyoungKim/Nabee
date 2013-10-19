package com.nabsys.management.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.label.NLabel;
import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.CustomLoader;
import com.nabsys.database.DBPoolManager;
import com.nabsys.management.exception.KeyDuplicateException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.IPC.IPC;
import com.nabsys.process.IManagementClass;
import com.nabsys.process.ManagementContext;
import com.nabsys.resource.InstanceContext;

public class ComponentConfig  implements IManagementClass{
	
	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	
	public NBFields execute(ManagementContext context, long clientSequence) {
		NBFields fromClient = context.getFields();
		NBFields toClient = new NBFields();
		
		if(fromClient.get("CMD_CODE").equals("L"))
		{
			try {
				toClient = getCmplist(context, fromClient);
			} catch (ClassCastException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting component information.");
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("R"))
		{
			try {
				toClient = getCmpConfig(context, fromClient);
			} catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("D"))
		{
			try {
				deleteComponent(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to removing component information.");
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("I"))
		{
			try {
				newComponent(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting component information.");
			} catch (KeyDuplicateException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("U"))
		{
			try {
				modifyComponent(context, fromClient);
				toClient.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to setting component information.");
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("ML"))
		{
			try {
				toClient = getMethodList(context, fromClient);
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting component information.");
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("PL"))
		{
			try {
				toClient = getParameterList(context, fromClient);
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting component information.");
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		else if(fromClient.get("CMD_CODE").equals("B"))
		{
			try {
				toClient = getBinaryData(context, fromClient);
			} catch (IOException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "Fail to getting component information.");
			} catch (NotFoundException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}catch (NullPointerException e) {
				logger.error(e, "Null exception.");
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", "System error occurred.");
			} catch (SQLException e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			} catch (Exception e) {
				logger.error(e, e.getMessage());
				toClient.put(IPC.NB_MSG_RETURN, IPC.FAIL);
				toClient.put("RTN_MSG", e.getMessage());
			}
		}
		
			
		return toClient;
	}
	
	private NBFields getBinaryData(ManagementContext context, NBFields fields) throws Exception
	{
		NBFields rtnFields = new NBFields();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT FILE_PATH FROM PUBLIC.COMPONENT WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'";
			rs = stmt.executeQuery(sql);
			if(!rs.next()) throw new NotFoundException();
			
			String classFile = rs.getString("FILE_PATH");
			File file = new File(classFile);
			
			if(!file.exists()) throw new NotFoundException();
			
			FileInputStream fileInputStream = null;
			try{
				fileInputStream = new FileInputStream(file);
				FileChannel fileChannel = fileInputStream.getChannel();
				ByteBuffer byteBuffer = ByteBuffer.allocateDirect((int)file.length());
				byte[] byteData = new byte[(int)file.length()];
				fileChannel.read(byteBuffer);
				byteBuffer.rewind();
				
				byteBuffer.get(byteData);
				rtnFields.put("BIN_DATA", byteData);
			}finally{
				if(fileInputStream != null) fileInputStream.close();
			}
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return rtnFields;
	}
	
	private NBFields getParameterList(ManagementContext context, NBFields fields) throws Exception
	{
		NBFields rtnFields = new NBFields();
		ArrayList<NBFields> paramList = new ArrayList<NBFields>();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT FILE_PATH, CLASS_NAME FROM PUBLIC.COMPONENT WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'";
			rs = stmt.executeQuery(sql);
			if(!rs.next()) throw new NotFoundException();
			
			String classFile = rs.getString("FILE_PATH");
			String className = rs.getString("CLASS_NAME");
			
			CustomLoader cl = new CustomLoader(className, classFile);
	        
			org.apache.bcel.classfile.Method[] methods = cl.getMethods();
			boolean found = false;
			
			for(int i=0; i<methods.length; i++)
			{
				String paramString = cl.getParameterTypeString(methods[i]);
				
				if(methods[i].getName().equals(fields.get("METHOD")) && paramString.equals(fields.get("PARAMS")))
				{
					String[] argumentNames = cl.getParameterNames(methods[i]);
					String[] argumentTypes = cl.getParameterTypes(methods[i]);

					for(int j=0; j<argumentTypes.length; j++)
					{
						NBFields tmp = new NBFields();
						
						tmp.put("PARAM", argumentNames[j]);
						tmp.put("TYPE", argumentTypes[j]);
						
						paramList.add(tmp);
					}
					found = true;
				}
			}
			
			if(!found) throw new NotFoundException();
			
			rtnFields.put("PRM_LST", paramList);
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return rtnFields;
	}
	
	private NBFields getMethodList(ManagementContext context, NBFields fields) throws Exception
	{
		NBFields 			rtnFields 		= new NBFields();
		ArrayList<NBFields> methodList 		= new ArrayList<NBFields>();
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT FILE_PATH, CLASS_NAME FROM PUBLIC.COMPONENT WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'";
			rs = stmt.executeQuery(sql);
			if(!rs.next()) throw new NotFoundException();
			
			String classFile = rs.getString("FILE_PATH");
			String className = rs.getString("CLASS_NAME");
			
			CustomLoader cl = new CustomLoader();
			Class<?> clazz = cl.getCustomClass(className, classFile);
			
			Method[] methods = clazz.getMethods();
			
			for(int i=0; i<methods.length; i++)
			{
				if(methods[i].getName().equals("equals") || methods[i].getName().equals("toString")) continue;
				if(methods[i].getModifiers() == Modifier.PUBLIC)
				{
					NBFields tmp = new NBFields();
					String rtnType = methods[i].getReturnType().toString();
					rtnType = rtnType.substring(rtnType.lastIndexOf('.') + 1, rtnType.length());
					
					tmp.put("RTN", rtnType);
					tmp.put("NAME", methods[i].getName());
					
					Class<?>[] params = methods[i].getParameterTypes();
					String paramString = "";
					for(int j=0; j<params.length; j++)
					{
						String tmpParamString = params[j].getSimpleName();
						paramString = paramString + tmpParamString;
						if(j < params.length - 1) paramString = paramString + ", ";
					}
					
					if(paramString.equals("")) paramString = "void";
					
					tmp.put("PRMS", paramString);
					
					methodList.add(tmp);
				}
			}
			
			rtnFields.put("MTH_LST", methodList);
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}	
		
		return rtnFields;
	}
	
	@SuppressWarnings("unchecked")
	private void deleteComponent(ManagementContext context, NBFields fields) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			ArrayList<NBFields> delList = (ArrayList<NBFields>)fields.get("DEL_LST");
			
			for(int i=0; i<delList.size(); i++)
			{
				String sql = "SELECT FILE_PATH, SHARE FROM PUBLIC.COMPONENT WHERE ID = '"+delList.get(i).get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'";
				rs = stmt.executeQuery(sql);
				
				if(!rs.next()) throw new NotFoundException();
				String filePath = rs.getString("FILE_PATH");
				boolean isShare = rs.getString("SHARE").trim().equals("true");
				
				sql = "DELETE FROM PUBLIC.COMPONENT WHERE ID = '"+delList.get(i).get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'";
				stmt.executeUpdate(sql);
				
				if(!isShare)
				{
					File delFile = new File(filePath);
					if(delFile.exists()) delFile.delete();
					
					if(delFile.getParentFile().list().length <= 0)
					{
						delFile.getParentFile().delete();
					}
				}
				
				logger.info(NLabel.get(0x0088) + " [" + delList.get(i).get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
			}
			
			connection.commit();
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}	
	}
	
	private void modifyComponent(ManagementContext context, NBFields fields) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT FILE_PATH, SHARE FROM PUBLIC.COMPONENT WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'";
			rs = stmt.executeQuery(sql);
			
			if(!rs.next()) throw new NotFoundException();
			String filePath = rs.getString("FILE_PATH");
			boolean isShare = rs.getString("SHARE").trim().equals("true");
			
			if(fields.containsKey("SAVE_PATH"))
			{
				if(!filePath.equals((String)fields.get("SAVE_PATH")) && !isShare)
				{
					File delFile = new File(filePath);
					if(delFile.exists()) delFile.delete();
				}
			}
			
			sql = "UPDATE PUBLIC.COMPONENT SET\n";
			sql += "ID = '"+fields.get("ID")+"'\n";
			boolean chkExist = false;
			if(fields.containsKey("SAVE_PATH"))
			{
				sql += ",FILE_PATH = '"+fields.get("SAVE_PATH")+"'\n";
				chkExist = true;
			}
			if(fields.containsKey("CLASS"))
			{
				sql += ",CLASS_NAME = '"+fields.get("CLASS")+"'\n";
				chkExist = true;
			}
			if(fields.containsKey("NAME"))
			{
				sql +=  ",NAME = '"+fields.get("NAME")+"'\n";
				chkExist = true;
			}
			sql += "WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'";
			if(chkExist)
			{
				stmt.executeUpdate(sql);
				
				sql = "INSERT INTO PUBLIC.COMPONENT_H\n";
				sql += "SELECT INSTANCE, ID, CURRENT_TIMESTAMP AS MODIFY_TIME, NAME, CLASS_NAME, FILE_PATH, SHARE FROM PUBLIC.COMPONENT\n";
				sql += "WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'\n";
				stmt.executeUpdate(sql);
			}
			
			if(fields.containsKey("SAVE_PATH"))
			{
				String savePath = ((String)fields.get("SAVE_PATH")).substring(0, ((String)fields.get("SAVE_PATH")).lastIndexOf('/'));
				String saveFile = (String)fields.get("SAVE_PATH");
				
				File file = new File(savePath);
				if(!file.exists())
					file.mkdirs();
				
				file = new File(saveFile);
				
				byte[] classFile = (byte[])fields.get("RSC");
				
				FileOutputStream fos = null;
				try{
					fos = new FileOutputStream(file);
					fos.write(classFile);
					fos.flush();
					fos.close();
				}catch (IOException e){
					throw e;
				}finally{
					try {
						if(fos != null)
							fos.close();
					} catch (IOException e) {
						throw e;
					}
				}
			}
			
			if(chkExist)
			{
				connection.commit();
			}
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		logger.info(NLabel.get(0x0087) + " [" + fields.get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
	}
	
	private void newComponent(ManagementContext context, NBFields fields) throws Exception
	{
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT COUNT(*) CNT FROM PUBLIC.COMPONENT WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'";
			rs = stmt.executeQuery(sql);
			rs.next();
			if(rs.getInt("CNT") >= 1) throw new KeyDuplicateException(0x0024);

			if(fields.containsKey("SHARE") && ((String)fields.get("SHARE")).equals("true"))
			{
				sql = "SELECT COUNT(*) CNT FROM PUBLIC.COMPONENT WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+fields.get("SRC_INS_NAME")+"'";
				rs = stmt.executeQuery(sql);
				rs.next();
				if(rs.getInt("CNT") <= 0) throw new NotFoundException();
				
				sql = "INSERT INTO PUBLIC.COMPONENT\n";
				sql += "SELECT '"+context.getInstanceID()+"', ID, NAME, CLASS_NAME, FILE_PATH, 'true' FROM PUBLIC.COMPONENT\n";
				sql += "WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+fields.get("SRC_INS_NAME")+"'\n";
				stmt.executeUpdate(sql);
				
				sql = "INSERT INTO PUBLIC.COMPONENT_H\n";
				sql += "SELECT INSTANCE, ID, CURRENT_TIMESTAMP AS MODIFY_TIME, NAME, CLASS_NAME, FILE_PATH, SHARE FROM PUBLIC.COMPONENT\n";
				sql += "WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'\n";
				stmt.executeUpdate(sql);
			}
			else
			{
				sql = "INSERT INTO PUBLIC.COMPONENT VALUES (\n";
				sql += "'"+context.getInstanceID()+"',";
				sql += "'"+fields.get("ID")+"',";
				sql += "'"+fields.get("NAME")+"',";
				sql += "'"+fields.get("CLASS")+"',";
				sql += "'"+fields.get("SAVE_PATH")+"',";
				sql += "'false'";
				sql += ")";
				stmt.executeUpdate(sql);
				
				sql = "INSERT INTO PUBLIC.COMPONENT_H\n";
				sql += "SELECT INSTANCE, ID, CURRENT_TIMESTAMP AS MODIFY_TIME, NAME, CLASS_NAME, FILE_PATH, SHARE FROM PUBLIC.COMPONENT\n";
				sql += "WHERE ID = '"+fields.get("ID")+"' AND INSTANCE = '"+context.getInstanceID()+"'\n";
				stmt.executeUpdate(sql);
				
				String savePath = ((String)fields.get("SAVE_PATH")).substring(0, ((String)fields.get("SAVE_PATH")).lastIndexOf('/'));
				String saveFile = (String)fields.get("SAVE_PATH");
				
				File file = new File(savePath);
				if(!file.exists())
					file.mkdirs();
				
				file = new File(saveFile);

				if(file.exists()) throw new KeyDuplicateException(0x0045);
				
				byte[] classFile = (byte[])fields.get("RSC");
				
				FileOutputStream fos = null;
				try{
					fos = new FileOutputStream(file);
					fos.write(classFile);
					fos.flush();
					fos.close();
				}catch (IOException e){
					throw e;
				}finally{
					try {
						if(fos != null)
							fos.close();
					} catch (IOException e) {
						throw e;
					}
				}
			}
			connection.commit();
		} catch(SQLException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(KeyDuplicateException e){
			if(connection != null)connection.rollback();
			throw e;
		} catch(Exception e){
			if(connection != null)connection.rollback();
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		logger.info(NLabel.get(0x0086) + " [" + fields.get("ID") + "] by " + context.getUser() + "(" + context.getClientAddress() + ")");
	}
	
	private NBFields getCmpConfig(ManagementContext context, NBFields fields) throws SQLException
	{
		NBFields rtnFields = new NBFields();
		ArrayList<NBFields> classPathList = new ArrayList<NBFields>();
		
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			if(fields.containsKey("ID"))
			{
				String sql = "SELECT INSTANCE, ID, NAME, CLASS_NAME, FILE_PATH FROM PUBLIC.COMPONENT\n";
				sql += "WHERE 1=1\n";
				sql += "AND ID = '"+fields.get("ID")+"'\n";
				sql += "AND INSTANCE = '"+fields.get("SRC_INS_NAME")+"'\n";
				rs = stmt.executeQuery(sql);
				
				if(rs.next())
				{
					rtnFields.put("NAME"		, rs.getString("NAME"));
				}
			}
			
			InstanceContext instanceContext = context.getResourceParameters().getInstanceConfiguration((String)fields.get("TGT_INS_NAME"));
			String strClassPath = instanceContext.getClassPath();
			
			String pathArray[] = strClassPath.split(";");
			for(int i=0; i<pathArray.length; i++)
			{
				NBFields tmp = new NBFields();
				if(!pathArray[i].contains("."))
				{
					tmp.put("CLS_PTH", pathArray[i]);
					classPathList.add(tmp);
				}
			}
			
			rtnFields.put("CLS_PTH_LST", classPathList);
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return rtnFields;
	}
	
	private NBFields getCmplist(ManagementContext context, NBFields fields) throws SQLException
	{
		NBFields 			rtnFields 		= new NBFields();
		ArrayList<NBFields> componentList	= new ArrayList<NBFields>();
		
		DBPoolManager 		configDBPool 	= context.getResourceParameters().getConfigDBPool();
		Connection 			connection 		= null;
		Statement 			stmt 			= null;
		ResultSet 			rs 				= null;
		try{
			connection = configDBPool.getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT INSTANCE, ID, NAME, CLASS_NAME, FILE_PATH FROM PUBLIC.COMPONENT\n";
			if(fields.containsKey("SMPL")) sql = "SELECT ID, CLASS_NAME FROM PUBLIC.COMPONENT\n";
				
			sql += "WHERE 1=1\n";
			if(((String)fields.get("SCH_TYPE")).equals("SCH_ID"))
			{
				sql += "AND ID LIKE '%"+fields.get("SCH")+"%'\n";
			}
			else if(((String)fields.get("SCH_TYPE")).equals("SCH_NAME"))
			{
				sql += "AND NAME LIKE '%"+fields.get("SCH")+"%'\n";
			}
			if(!context.getInstanceID().equals(""))
			{
				sql += "AND INSTANCE = '"+context.getInstanceID()+"'";
			}
			sql += " ORDER BY ID ASC LIMIT 0, 100";
			rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				NBFields component = new NBFields();
				if(fields.containsKey("SMPL"))
				{
					component.put("ID"					, rs.getString("ID"));
					component.put("CLASS"				, rs.getString("CLASS_NAME"));
				}
				else
				{
					component.put(IPC.NB_INSTNCE_ID		, rs.getString("INSTANCE"));
					component.put("ID"					, rs.getString("ID"));
					component.put("NAME"				, rs.getString("NAME"));
					component.put("CLASS"				, rs.getString("CLASS_NAME"));
					component.put("SAVE_PATH"			, rs.getString("FILE_PATH"));
				}
				componentList.add(component);
			}
			
			rtnFields.put("CMP_LST", componentList);
		} catch(SQLException e){
			throw e;
		} finally {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(connection != null) connection.close();
		}
		
		return rtnFields;
	}

}
