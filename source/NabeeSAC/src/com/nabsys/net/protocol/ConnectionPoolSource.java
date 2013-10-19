package com.nabsys.net.protocol;


public class ConnectionPoolSource {
	private String user = "";
	private String password = "";
	private String address = "";
	private int port = 0;
	private int maxSocketBuff = 4096;
	private int socketReadTimeOut = 0;
	private int maxActive = 0;
	private int maxIdle = 0;
	private int maxWait = 0;
	private boolean keepAlive = false;
	private int keepAliveSecond = 10;
	private int lengthFieldOffset = 0;
	private int lengthFieldLength = 0;
	private int	lengthFieldAdjustment = 0;
	private String encoding = "EUC-KR";
	private String lengthFieldID = "";
	private String idFieldID = "";

	
	public void setUser(String user) {
		this.user = user;
	}
	public String getUser() {
		return user;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress() {
		return address;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getPort() {
		return port;
	}
	public void setMaxSocketBuff(int maxSocketBuff) {
		this.maxSocketBuff = maxSocketBuff;
	}
	public int getMaxSocketBuff() {
		return maxSocketBuff;
	}
	public void setSocketReadTimeOut(int socketReadTimeOut) {
		this.socketReadTimeOut = socketReadTimeOut;
	}
	public int getSocketReadTimeOut() {
		return socketReadTimeOut;
	}
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	public int getMaxActive() {
		return maxActive;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}
	public int getMaxWait() {
		return maxWait;
	}
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
	public boolean isKeepAlive() {
		return keepAlive;
	}
	public void setKeepAliveSecond(int keepAliveSecond) {
		this.keepAliveSecond = keepAliveSecond;
	}
	public int getKeepAliveSecond() {
		return keepAliveSecond;
	}
	public int getLengthFieldOffset() {
		return lengthFieldOffset;
	}
	public void setLengthFieldOffset(int lengthFieldOffset) {
		this.lengthFieldOffset = lengthFieldOffset;
	}
	public int getLengthFieldLength() {
		return lengthFieldLength;
	}
	public void setLengthFieldLength(int lengthFieldLength) {
		this.lengthFieldLength = lengthFieldLength;
	}
	public int getLengthFieldAdjustment()
	{
		return lengthFieldAdjustment;
	}
	public void setLengthFieldAdjustment(int lengthFieldAdjustment)
	{
		this.lengthFieldAdjustment = lengthFieldAdjustment;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getLengthFieldID() {
		return lengthFieldID;
	}
	public void setLengthFieldID(String lengthFieldID) {
		this.lengthFieldID = lengthFieldID;
	}
	public String getIDFieldID() {
		return idFieldID;
	}
	public void setIDFieldID(String idFieldID) {
		this.idFieldID = idFieldID;
	}
}