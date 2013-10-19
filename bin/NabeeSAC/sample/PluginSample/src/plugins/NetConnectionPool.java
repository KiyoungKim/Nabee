package plugins;

import java.io.IOException;
import java.util.HashMap;

import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.ConnectionPool;
import com.nabsys.net.protocol.ConnectionPoolSource;
import com.nabsys.net.protocol.asnd.AssignedProtocolHandler;
import com.nabsys.process.IPlugin;
import com.nabsys.process.exception.PluginInitializationException;

public class NetConnectionPool  implements IPlugin{

	final NLogger logger = NLogger.getLogger(this.getClass());
	private ConnectionPool pool = null;
	
	public Object initializer(HashMap<String, String> params)
			throws PluginInitializationException {
		
		ConnectionPoolSource source = new ConnectionPoolSource();
		
		//source.setUser(params.get());
		//source.setPassword(params.get());
		source.setAddress(params.get("CONNECTION_TARGET"));
		source.setPort(Integer.parseInt(params.get("TARGET_PORT")));
		source.setMaxSocketBuff(Integer.parseInt(params.get("MAX_SOCK_BUFF")));
		source.setSocketReadTimeOut(Integer.parseInt(params.get("SOCK_READ_TIMEOUT"))); //second
		source.setKeepAlive(params.get("SOCK_KEEP_ALIVE").equals("true"));
		source.setMaxActive(Integer.parseInt(params.get("MAX_POOL_ACTIVE")));
		source.setMaxIdle(Integer.parseInt(params.get("MAX_POOL_IDLE")));
		source.setMaxWait(Integer.parseInt(params.get("SOCK_MAX_WAIT")));
		source.setEncoding(params.get("ENCODING"));
		source.setIDFieldID(params.get("ID_FIELD_ID"));
		source.setLengthFieldID(params.get("LENGTH_FIELD_ID"));
		source.setLengthFieldOffset(Integer.parseInt(params.get("LENGTH_FIELD_OFFSET")));
		source.setLengthFieldLength(Integer.parseInt(params.get("LENGTH_FIELD_LENGTH")));
		source.setLengthFieldAdjustment(Integer.parseInt(params.get("LENGTH_FIELD_ADJUSTMENT")));
		
		pool = new ConnectionPool(source);
		pool.setProtocolType(AssignedProtocolHandler.class);
		
		logger.info("Connection pool loaded.");
		
		return pool;
	}

	public void finalizer() {
		try {
			if(pool != null)
			{
				pool.closeAll();
				logger.info("Connection pool terminated.");
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			logger.error(e, 0x0022);
		}
	}

}
