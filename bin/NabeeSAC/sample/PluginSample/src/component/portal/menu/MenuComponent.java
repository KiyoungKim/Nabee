package component.portal.menu;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Level;

import com.nabsys.common.exception.NotFoundException;
import com.nabsys.common.logger.NLogger;
import com.nabsys.database.Connection;
import com.nabsys.database.DBPoolManager;
import com.nabsys.net.protocol.flxb.FLXB;
import com.nabsys.process.Context;

public class MenuComponent {
	
	private final NLogger logger = NLogger.getLogger(this.getClass());
	

	public void delete(Context context, HashMap<String, Object> map) throws SQLException, NotFoundException, IOException, ClassNotFoundException
	{
		logger.debug("MenuComponent.delete Start");
		map.put(FLXB.RETURN_FIELD_NAME, FLXB.FAIL);
		map.put("RSV_MESSAGE", "데이터 삭제에 실패하였습니다.");
		
		Connection connection = ((DBPoolManager)context.getPlugins("PMS_DB")).getConnection();
		boolean isAutoCommit = connection.getAutoCommit();
		connection.setAutoCommit(false);
		
		try{
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("ID"				, (String)map.get("OLD_ID"));
			if(logger.isEnabledFor(Level.DEBUG));
			logger.debug("EXECUTE ID : pms.portal.menu.delete.menu_d_01\r" + connection.getQuery("pms.portal.menu.delete.menu_d_01", params));

			connection.update("pms.portal.menu.delete.menu_d_01", params);	

			if(logger.isEnabledFor(Level.DEBUG));
			logger.debug("EXECUTE ID : pms.common.update.free_fk\r" + connection.getQuery("pms.common.update.free_fk"));

			connection.update("pms.common.update.free_fk");	

			if(logger.isEnabledFor(Level.DEBUG));
			logger.debug("EXECUTE ID : pms.portal.menu.delete.menu_d_02\r" + connection.getQuery("pms.portal.menu.delete.menu_d_02", params));
		
			connection.update("pms.portal.menu.delete.menu_d_02", params);	
			
		}catch(NullPointerException e){
			logger.error(e, e.getMessage());
			connection.rollback();
		} catch (SQLException e) {
			logger.error(e, e.getMessage());
			connection.rollback();
		} catch (Exception e) {
			logger.error(e, e.getMessage());
			connection.rollback();
		} finally {
			try {
			if(logger.isEnabledFor(Level.DEBUG));
			logger.debug("EXECUTE ID : pms.common.update.set_fk\r" + connection.getQuery("pms.common.update.set_fk"));
		
			connection.update("pms.common.update.set_fk");
			}finally{
				connection.setAutoCommit(isAutoCommit);
				connection.close();
			}
		}
		
		map.put(FLXB.RETURN_FIELD_NAME, FLXB.SUCCESS);
		map.remove("RSV_MESSAGE");
		logger.debug("MenuComponent.delete End");
	}
	
	@SuppressWarnings("unchecked")
	public void changeOrder(Context context, HashMap<String, Object> map) throws SQLException
	{
		logger.debug("MenuComponent.changeOrder Start");
		map.put(FLXB.RETURN_FIELD_NAME, FLXB.FAIL);
		map.put("RSV_MESSAGE", "데이터 등록에 실패하였습니다.");
		
		Connection connection = ((DBPoolManager)context.getPlugins("PMS_DB")).getConnection();
		boolean isAutoCommit = connection.getAutoCommit();
		connection.setAutoCommit(false);

		try{
			if(map.get("MENU_ORDER") instanceof ArrayList)
			{
				ArrayList<HashMap<String, Object>> menuOrderList 	= (ArrayList<HashMap<String, Object>>)map.get("MENU_ORDER");
				ArrayList<HashMap<String, Object>> idList 			= (ArrayList<HashMap<String, Object>>)map.get("OLD_ID");
				ArrayList<HashMap<String, Object>> parentList 		= null;
				
				if(map.containsKey("PARENT_ID"))
					parentList 						= (ArrayList<HashMap<String, Object>>)map.get("PARENT_ID");
				
				for(int i=0; i<menuOrderList.size(); i++)
				{
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("LOGIN_MEMBER"	, (String)map.get("LOGIN_MEMBER"));
					params.put("MENU_ORDER"		, String.valueOf(menuOrderList.get(i).get("VALUE")));
					params.put("OLD_ID"			, String.valueOf(idList.get(i).get("VALUE")));
					
					if(parentList != null)
						params.put("PARENT_ID"	, String.valueOf(parentList.get(i).get("VALUE")));
					
					if(logger.isEnabledFor(Level.DEBUG));
						logger.debug("EXECUTE ID : pms.portal.menu.update.menu_u_01\r" + connection.getQuery("pms.portal.menu.update.menu_u_01", params));
					
					connection.update("pms.portal.menu.update.menu_u_01", params);
				}
			}
			else
			{
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("LOGIN_MEMBER"		, (String)map.get("LOGIN_MEMBER"));
				params.put("MENU_ORDER"			, (String)map.get("MENU_ORDER"));
				params.put("OLD_ID"				, (String)map.get("OLD_ID"));
				params.put("PARENT_ID"			, (String)map.get("PARENT_ID"));
				
				if(logger.isEnabledFor(Level.DEBUG));
				logger.debug("EXECUTE ID : pms.portal.menu.update.menu_u_01\r" + connection.getQuery("pms.portal.menu.update.menu_u_01", params));
			
				connection.update("pms.portal.menu.update.menu_u_01", params);	
			}
			
			connection.commit();
			
		}catch(NullPointerException e){
			logger.error(e, e.getMessage());
			connection.rollback();
		} catch (SQLException e) {
			logger.error(e, e.getMessage());
			connection.rollback();
		} catch (Exception e) {
			logger.error(e, e.getMessage());
			connection.rollback();
		} finally {
			connection.setAutoCommit(isAutoCommit);
			connection.close();
		}
		
		map.put(FLXB.RETURN_FIELD_NAME, FLXB.SUCCESS);
		map.remove("RSV_MESSAGE");
		logger.debug("MenuComponent.changeOrder End");
	}
}
