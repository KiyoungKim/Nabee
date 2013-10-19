package com.nabsys.nabeeplus.common.label;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

public class NBLabel {
	public static HashMap<Integer, String[]> labelMap = new HashMap<Integer, String[]>();
	public static int KOR = 0;
	public static int ENG = 1;
	private static int locale = -1;
	private static Locale lLocale;
	
	public static Locale getLocale()
	{
		try {
			if(labelMap.size() <= 0)
				loadLabel();
		} catch (IOException e) {
		}
		
		return NBLabel.lLocale;
	}
	
	private final static void loadLabel() throws IOException
	{
		Properties prop = new Properties();
		InputStream is = NBLabel.class.getResourceAsStream("label.properties");
		
		prop.load(is);
		
		Set<Object> keySet = prop.keySet();
		Iterator<Object> itr = keySet.iterator();
		
		while(itr.hasNext())
		{
			Object key = itr.next();
			if("locale".equals((String)key))
			{
				if(((String)prop.get(key)).equals("KOREA"))
					NBLabel.locale = NBLabel.KOR;
				else
					NBLabel.locale = NBLabel.ENG;
				
				
				if(((String)prop.get(key)).equals("")) lLocale = java.util.Locale.KOREA;
				else if(((String)prop.get(key)).equals("KOREA")) lLocale = java.util.Locale.KOREA;
				else if(((String)prop.get(key)).equals("CANADA")) lLocale = java.util.Locale.CANADA;
				else if(((String)prop.get(key)).equals("CHINA")) lLocale = java.util.Locale.CHINA;
				else if(((String)prop.get(key)).equals("UK")) lLocale = java.util.Locale.UK;
				else if(((String)prop.get(key)).equals("FRANCE")) lLocale = java.util.Locale.FRANCE;
				else if(((String)prop.get(key)).equals("GERMAN")) lLocale = java.util.Locale.GERMAN;
				else if(((String)prop.get(key)).equals("ITALY")) lLocale = java.util.Locale.ITALY;
				else if(((String)prop.get(key)).equals("JAPAN")) lLocale = java.util.Locale.JAPAN;
				else if(((String)prop.get(key)).equals("PRC")) lLocale = java.util.Locale.PRC;
				else if(((String)prop.get(key)).equals("TAIWAN")) lLocale = java.util.Locale.TAIWAN;
				else if(((String)prop.get(key)).equals("US")) lLocale = java.util.Locale.US;
				else lLocale = java.util.Locale.US;
				
				continue;
			}
			
			String[] label = new String(((String)prop.get(key)).getBytes("ISO-8859-1"), "UTF-8").split(":");
			labelMap.put(Integer.parseInt(((String)key).replaceFirst("0x", ""), 16), label);
		}
	}
	
	public static String get(int code)
	{
		try {
			if(labelMap.size() <= 0)
				loadLabel();
		} catch (IOException e) {
		}
		
		if(NBLabel.locale == -1) NBLabel.locale = NBLabel.KOR;
		
		return labelMap.get(code)[NBLabel.locale];
	}
}
