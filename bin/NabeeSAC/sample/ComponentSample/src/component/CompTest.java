package component;

import com.nabsys.common.logger.NLogger;


public class CompTest {
	int a = 0;
	String b = "";
	boolean c = false;
	
	final NLogger logger = (NLogger)NLogger.getLogger(this.getClass());
	
	public int getInt()
	{
		logger.info("getInt : " + a);
		return a;
	}
	
	public String getStr()
	{
		logger.info("getStr : " + b);
		return b;
	}
	
	public boolean getBool()
	{
		logger.info("getBool : " + c);
		return c;
	}
	
	public void setValue(int a, String b, boolean c)
	{
		this.a = a;
		this.b = b;
		this.c = c;
	}
}
