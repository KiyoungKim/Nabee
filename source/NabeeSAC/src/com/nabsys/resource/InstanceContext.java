package com.nabsys.resource;

public class InstanceContext extends NetworkContext{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String 	id					= null;
	private String 	sysHeaderID			= null;
	private boolean useTelegramCache	= false;
	private boolean useComponentCache	= false;
	private boolean useQueryCache		= false;
	private boolean useServiceCache		= false;
	private int 	servicePort			= 0;
	private int 	maxClients			= 0;
	private String 	timeLocale			= null;
	private String 	classPath			= null;
	private String 	javaHome			= null;
	private String 	systemPath			= null;
	private String 	systemEncoding		= null;
	private String 	fileEncoding		= null;
	private String 	logConfigPath		= null;
	private boolean loadOnStartup		= false;
	private String 	extraLoadParams		= null;
	
	public InstanceContext(String id,
							String sysHeaderID,
							boolean useTelegramCache,
							boolean useComponentCache,
							boolean useQueryCache,
							boolean useServiceCache,
							int servicePort,
							int bufferSize,
							int readTimeout,
							int maxClients,
							String serverEncoding,
							String timeLocale,
							String classPath,
							String javaHome,
							String systemPath,
							String systemEncoding,
							String fileEncoding,
							String logConfigPath,
							boolean loadOnStartup,
							String extraLoadParams,
							String lengthFieldID,
							String idFieldID)
	{
		super(bufferSize, readTimeout, serverEncoding, lengthFieldID, idFieldID);
		this.id					= id;
		this.sysHeaderID		= sysHeaderID;
		this.useTelegramCache	= useTelegramCache;
		this.useComponentCache	= useComponentCache;
		this.useQueryCache		= useQueryCache;
		this.useServiceCache 	= useServiceCache;
		this.servicePort		= servicePort;
		this.maxClients			= maxClients;
		this.timeLocale			= timeLocale;
		this.classPath			= classPath;
		this.javaHome			= javaHome;
		this.systemPath			= systemPath;
		this.systemEncoding		= systemEncoding;
		this.fileEncoding		= fileEncoding;
		this.logConfigPath		= logConfigPath;
		this.loadOnStartup		= loadOnStartup;
		this.extraLoadParams	= extraLoadParams;
	}
	
	public String getID(){
		return id;
	}
	public String getSysHeaderID(){
		return sysHeaderID;
	}
	public boolean isUseTelegramCache(){
		return useTelegramCache;
	}
	public boolean isUseComponentCache(){
		return useComponentCache;
	}
	public boolean isUseServiceCache(){
		return useServiceCache;
	}
	public boolean isUseQueryCache(){
		return useQueryCache;
	}
	public int getServicePort(){
		return servicePort;
	}
	public int getMaxClients(){
		return maxClients;
	}
	public String getTimeLocale(){
		return timeLocale;
	}
	public String getClassPath(){
		return classPath;
	}
	public String getJavaHome(){
		return javaHome;
	}
	public String getSystemPath(){
		return systemPath;
	}
	public String getSystemEncoding(){
		return systemEncoding;
	}
	public String getFileEncoding(){
		return fileEncoding;
	}
	public String getLogConfigPath(){
		return logConfigPath;
	}
	public boolean isLoadOnStartup(){
		return loadOnStartup;
	}
	public String getExtraLoadParams(){
		return extraLoadParams;
	}
}
