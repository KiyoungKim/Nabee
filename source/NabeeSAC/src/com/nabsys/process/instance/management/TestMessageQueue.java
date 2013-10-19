package com.nabsys.process.instance.management;

/*
 * Nabee Plus 서비스 테스트용 클래스. 테스트 결과를 리턴하기 위해 큐를 이용함.
 */
public class TestMessageQueue {
	public TestMessageQueue(int handlerID, String message, boolean isErr, boolean isEnd)
	{
		this.handlerID = handlerID;
		this.message = message;
		this.isErr = isErr;
		this.isEnd = isEnd;
	}
	
	public int getHandlerID()
	{
		return this.handlerID;
	}
	
	public String getMessage()
	{
		if(this.message == null) return "Message is null";
		else return this.message;
	}
	
	public boolean isErr()
	{
		return this.isErr;
	}
	public boolean isEnd()
	{
		return this.isEnd;
	}
	private int handlerID = 0;
	private String message = "";
	private boolean isErr = false;
	private boolean isEnd = false;
}
