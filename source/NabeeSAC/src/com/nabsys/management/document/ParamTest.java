package com.nabsys.management.document;

import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;


public class ParamTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			org.apache.bcel.classfile.JavaClass jc = org.apache.bcel.Repository.lookupClass("com.nabsys.management.document.TelegramConfig");
			Method[] methods = jc.getMethods();
			
			org.apache.bcel.generic.Type[] types = methods[1].getArgumentTypes();
			
			
			
			String paramString = "";
			for(int j=0; j<types.length; j++)
			{
				String strType = types[j].toString();
				String tmpParamString = strType.substring(strType.lastIndexOf(".") + 1);
				
				paramString = paramString + tmpParamString;
				if(j < types.length - 1) paramString = paramString + ", ";
			}
			
			if(paramString.equals("")) paramString = "void";
			
			System.out.println(paramString);
			
			
			for(int i=0; i<methods.length; i++)
			{
				System.out.println("Method : " + methods[i].getName());
				LocalVariable[] lvt = methods[i].getLocalVariableTable().getLocalVariableTable();
				types = methods[i].getArgumentTypes();
				if(types.length > 0)
				{
					for(int j=1; j<types.length + 1 ; j++)
					{
						System.out.println(lvt[j].getName());
					}
				}
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
