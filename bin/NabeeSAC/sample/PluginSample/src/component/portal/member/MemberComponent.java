package component.portal.member;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Level;

import com.nabsys.common.logger.NLogger;
import com.nabsys.database.Connection;
import com.nabsys.database.DBPoolManager;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.flxb.FLXB;
import com.nabsys.process.Context;

public class MemberComponent {
	private final NLogger logger = NLogger.getLogger(this.getClass());
	
	@SuppressWarnings("unchecked")
	public void addNewAccessList(Context context, HashMap<String, Object> map) throws SQLException
	{
		logger.debug("MemberComponent.addNewAccessList Start");
		map.put(FLXB.RETURN_FIELD_NAME, FLXB.FAIL);
		map.put("RSV_MESSAGE", "데이터 등록에 실패하였습니다.");

		Connection connection = ((DBPoolManager)context.getPlugins("PMS_DB")).getConnection();
		boolean isAutoCommit = connection.getAutoCommit();
		connection.setAutoCommit(false);

		try{
			if(map.get("ACCESS_LEVEL") instanceof ArrayList)
			{
				ArrayList<NBFields> menuID 			= (ArrayList<NBFields>)map.get("MENU_ID");
				ArrayList<NBFields> organizationID	= (ArrayList<NBFields>)map.get("ORGANIZATION_ID");
				ArrayList<NBFields> roleMemberID	= (ArrayList<NBFields>)map.get("ROLE_MEMBER_ID");
				ArrayList<NBFields> accessLevel		= (ArrayList<NBFields>)map.get("ACCESS_LEVEL");
				
				for(int i=0; i<accessLevel.size(); i++)
				{
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("MENU_ID"			, String.valueOf(menuID.get(i).get("VALUE")));
					params.put("ORGANIZATION_ID"	, String.valueOf(organizationID.get(i).get("VALUE")));
					params.put("ROLE_MEMBER_ID"		, String.valueOf(roleMemberID.get(i).get("VALUE")));
					params.put("ACCESS_LEVEL"		, String.valueOf(accessLevel.get(i).get("VALUE")));
					
					if(logger.isEnabledFor(Level.DEBUG));
					logger.debug("EXECUTE ID : pms.portal.member.insert.access_list_c_01\r" + connection.getQuery("pms.portal.member.insert.access_list_c_01", params));
				
					connection.update("pms.portal.member.insert.access_list_c_01", params);
				}
			}
			else
			{
				if(logger.isEnabledFor(Level.DEBUG));
				logger.debug("EXECUTE ID : pms.portal.member.insert.access_list_c_01\r" + connection.getQuery("pms.portal.member.insert.access_list_c_01", map));
			
				connection.update("pms.portal.member.insert.access_list_c_01", map);
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
		logger.debug("MemberComponent.addNewAccessList End");
	}
}
