package com.nabsys.resource.script;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			Method m = String.class.getMethod("format", new Class[]{String.class, Object[].class});
			
			Object[] prm = new Object[2];
			String format = "The format method is %s!   %s";
			prm[0] = "greate";
			prm[1] = " aaaa";
			System.out.println(m.invoke(format, format ,  prm));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		FuncParser fp = new FuncParser();
		try {
			FunctionExecutor fe = fp.parseScript("FORMAT(\"%02d -> %02d -> %02d\", 1, 2, 3)");
			System.out.println(fe.execute(null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String tt = "abcdedf";
		System.out.println(tt.startsWith("ed", 4));
	}

}
