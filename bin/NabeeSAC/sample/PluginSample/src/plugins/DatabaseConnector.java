package plugins;

import java.sql.SQLException;
import java.util.HashMap;

import com.nabsys.common.logger.NLogger;
import com.nabsys.database.DBPoolManager;
import com.nabsys.process.IPlugin;
import com.nabsys.process.exception.PluginInitializationException;

public class DatabaseConnector implements IPlugin{
	
	final NLogger logger = NLogger.getLogger(this.getClass());
	
	private DBPoolManager pool = null;
	
	public void finalizer() {
		try {
			if(pool != null)
			{
				pool.closePool();
				logger.info("Database terminated.");
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			logger.error(e, 0x0022);
		}
	}

	public Object initializer(HashMap<String, String> params) throws PluginInitializationException {
		
		pool = new DBPoolManager();
		
		pool.setPoolName(params.get("POOL_NAME"));
		pool.setDBDriver(params.get("DRIVER"));
		pool.setDBUrl(params.get("URL"));
		pool.setDriverUrl(params.get("POOL_DRIVER_URL"));
		pool.setUser(params.get("USER"));
		pool.setPassword(params.get("PW"));
		pool.setMaxPoolActive(Integer.parseInt(params.get("POOL_MAX_ACTIVE")));
		pool.setPoolIdle(Integer.parseInt(params.get("POOL_IDLE")));
		pool.setPoolMaxWait(Integer.parseInt(params.get("POOL_MAX_WAIT")));
		pool.setIsAutoCommit(params.get("POOL_DEFAULT_AUTO_COMMIT").toUpperCase().equals("TRUE"));
		pool.setIsReadOnly(params.get("POOL_DEFAULT_READ_ONLY").toUpperCase().equals("TRUE"));
		
		try {
			pool.initConnectionPool();
		} catch (SQLException e) {
			logger.warn(e.getMessage());
			throw new PluginInitializationException(e.getMessage(), pool);
		}
		
		logger.info("Database loaded.");
		return pool;
	}

}
