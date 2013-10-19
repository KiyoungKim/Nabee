package com.nabsys.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	Locale locale = null;
	public DateUtil()
	{
		locale = java.util.Locale.KOREA;
	}
	
	public DateUtil(Locale locale)
	{
		this.locale = locale;
	}
	
	public String getCurrentDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
		return sdf.format(new Date());
	}
	
	public String getFormatedDate(String format, long yyyyMMddHHmmss) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		Date date = sdf.parse(Long.toString(yyyyMMddHHmmss));
		SimpleDateFormat sdfr = new SimpleDateFormat(format, locale);
		return sdfr.format(date);
	}
	
	public int getWeekDay(long yyyyMMddHHmmss) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		sdf.parse(Long.toString(yyyyMMddHHmmss));
		Calendar cal = sdf.getCalendar();
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	public int getLastDayMonth(long yyyyMMddHHmmss) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		sdf.parse(Long.toString(yyyyMMddHHmmss));
		Calendar cal = sdf.getCalendar();
		int date = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		return date;
	}
	
	public long getNextDate(long yyyyMMddHHmmss, int amount) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		sdf.parse(Long.toString(yyyyMMddHHmmss));
		Calendar cal = sdf.getCalendar();
		cal.add(Calendar.DATE, amount);
		return Long.parseLong(sdf.format(cal.getTime()));
	}
	
	public long getNextSecond(long yyyyMMddHHmmss, int amount) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		sdf.parse(Long.toString(yyyyMMddHHmmss));
		Calendar cal = sdf.getCalendar();
		cal.add(Calendar.SECOND, amount);
		return Long.parseLong(sdf.format(cal.getTime()));
	}
	
	public long getNextMinute(long yyyyMMddHHmmss, int amount) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		sdf.parse(Long.toString(yyyyMMddHHmmss));
		Calendar cal = sdf.getCalendar();
		cal.add(Calendar.MINUTE, amount);
		return Long.parseLong(sdf.format(cal.getTime()));
	}
	
	public long getNextHour(long yyyyMMddHHmmss, int amount) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		sdf.parse(Long.toString(yyyyMMddHHmmss));
		Calendar cal = sdf.getCalendar();
		cal.add(Calendar.HOUR, amount);
		return Long.parseLong(sdf.format(cal.getTime()));
	}
	
	public long getNextMonth(long yyyyMMddHHmmss, int amount) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		sdf.parse(Long.toString(yyyyMMddHHmmss));
		Calendar cal = sdf.getCalendar();
		cal.add(Calendar.MONTH, amount);
		return Long.parseLong(sdf.format(cal.getTime()));
	}
	
	public long getNextYear(long yyyyMMddHHmmss, int amount) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		sdf.parse(Long.toString(yyyyMMddHHmmss));
		Calendar cal = sdf.getCalendar();
		cal.add(Calendar.YEAR, amount);
		return Long.parseLong(sdf.format(cal.getTime()));
	}
	
	public int getSecond(Date date) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("ss", locale);
		return Integer.parseInt(sdf.format(date));
	}
	
	public int getMinute(Date date) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("mm", locale);
		return Integer.parseInt(sdf.format(date));
	}
	
	public int getHour(Date date) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH", locale);
		return Integer.parseInt(sdf.format(date));
	}
	
	public int getDate(Date date) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd", locale);
		return Integer.parseInt(sdf.format(date));
	}
	
	public int getMonth(Date date) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM", locale);
		return Integer.parseInt(sdf.format(date));
	}
	
	public int getYear(Date date) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy", locale);
		return Integer.parseInt(sdf.format(date));
	}
}
