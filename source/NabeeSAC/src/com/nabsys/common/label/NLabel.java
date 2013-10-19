package com.nabsys.common.label;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

public class NLabel {
	public static HashMap<Integer, String[]> labelMap = new HashMap<Integer, String[]>();
	public static int KOR = 0;
	public static int ENG = 1;
	private static int locale = -1;
	
	public final void loadLabel() throws IOException
	{
		Properties prop = new Properties();
		InputStream is = NLabel.class.getResourceAsStream("label.properties");

		prop.load(is);
		
		Set<Object> keySet = prop.keySet();
		Iterator<Object> itr = keySet.iterator();
		
		while(itr.hasNext())
		{
			Object key = itr.next();
			String[] label = new String(((String)prop.get(key)).getBytes("ISO-8859-1"), "UTF-8").split(":");
			labelMap.put(Integer.parseInt(((String)key).replaceFirst("0x", ""), 16), label);
		}
	}
	
	public final void setLocale(Locale locale)
	{
		if(locale == java.util.Locale.KOREA)
			NLabel.locale = NLabel.KOR;
		else
			NLabel.locale = NLabel.ENG;
	}
	
	public static String get(int code)
	{
		if(NLabel.locale == -1) return "";
		
		return labelMap.get(code)[NLabel.locale];
	}
}
