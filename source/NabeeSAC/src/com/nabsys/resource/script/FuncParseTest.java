package com.nabsys.resource.script;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class FuncParseTest {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//IF()
		//THEN
		//ELSE
		//ELSEIF
		//ENDIF
		
		//:KEY
		FuncParser fp = new FuncParser();

		String str = "LASTINDEXOF(DELETECHARAT(DELETE(SUBSTR(INSERT(APPEND(\"ab\", \"cd\"), 2, APPEND(\"12\", \"34\")), 2), 1, 2), 3), \"d\")";
		String str2 = "REPLACE(\"ABcdEFGHIJK\", ADD(1,1), ADD(3,1), \"CD\")";
		String str3 = "DOUBLE(ABS(MULTIPLY(ADD(1989.1, 	1), -1)))";
		String str4 = "SUBTRACT(DIVIDE(6, 2), DIVIDE(4, 2))";
		String str5 = "ADD(DOUBLE(23442.49333232), 1.1)";
		String str6 = "APPEND(:VAL, \"TEST\")";
		String str7 = "ADD(:VAL1, :VAL2)";
		String str8 = "INTEGER(:VAL3)";
		
		String str9 = "IF (1 == 1 && (!(1 < 2) || !(!(1 != ADD(1, 1)) && 1 <= 3 && 1 != 3 && !(12 != 13) || 22 == 22))) THEN\n";
		str9 		+= "	REPLACE(\"ABcdEFGHIJK\", ADD(1,1), ADD(3,1), \"CD\")\n";
		str9 		+= "ELSEIF(\"ABC\" != \"ABC\") THEN\n";
		str9 		+= "	SUBTRACT(DIVIDE(6, 3), DIVIDE(4, 2))\n";
		str9 		+= "ELSEIF((ADD(:VAL1, :VAL2) == 35)) THEN\n";
		str9 		+= "	SUBTRACT(DIVIDE(6, 2), DIVIDE(4, 2))\n";
		str9 		+= "ELSE\n";
		str9 		+= "	APPEND(:VAL, \"TEST\")\n";
		str9 		+= "ENDIF";

		String str10 = ":VAL";
		
		String str11 = "IF(               \"IF( 1==1 ) THEN \"==\"IF( 1==1 ) THEN \"                       ) THEN\n";
		str11 		+= "	IF(2==2) THEN\n";
		str11 		+= "		IF(                    3!=3                   ) THEN\n";
		str11 		+= "			1\n";
		str11 		+= "		ELSEIF(1==1) THEN\n";
		str11 		+= "			2\n";
		str11 		+= "		ENDIF\n";
		str11 		+= "	ENDIF\n";
		str11 		+= "ELSEIF(\"ABC\" != \"ABC\") THEN\n";
		str11 		+= "	IF(ADD(:VAL1, :VAL2) == 35) THEN\n";
		str11 		+= "		:VAL\n";
		str11 		+= "	ENDIF\n";
		str11 		+= "ELSE\n";
		str11 		+= "	IF(4==4) THEN\n";
		str11 		+= "		7\n";
		str11 		+= "	ENDIF\n";
		str11 		+= "ENDIF\n";
		try {
			FunctionExecutor fe = fp.parseScript(str);
			FunctionExecutor fe2 = fp.parseScript(str2);
			FunctionExecutor fe3 = fp.parseScript(str3);
			FunctionExecutor fe4 = fp.parseScript(str4);
			FunctionExecutor fe5 = fp.parseScript(str5);
			FunctionExecutor fe6 = fp.parseScript(str6);
			FunctionExecutor fe7 = fp.parseScript(str7);
			FunctionExecutor fe8 = fp.parseScript(str8);
			FunctionExecutor fe9 = fp.parseScript(str9);
			FunctionExecutor fe10 = fp.parseScript(str10);
			FunctionExecutor fe11 = fp.parseScript(str11);
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("VAL", "MAPPING ");
			map.put("VAL1", 10);
			map.put("VAL2", 25);
			map.put("VAL3", 4543L);
			
			
			System.out.println(fe.execute(null));
			System.out.println(fe2.execute(null));
			System.out.println(fe3.execute(null));
			System.out.println(fe4.execute(null));
			System.out.println(fe5.execute(null));
			System.out.println(fe5.execute(null) instanceof Double);
			System.out.println("============================");
			
			System.out.println(fe6.execute(map));
			System.out.println(fe7.execute(map));
			System.out.println(fe8.execute(map));
			System.out.println("============================>>");
			System.out.println(fe9.execute(map));
			System.out.println(fe10.execute(map));
			System.out.println(fe11.execute(map));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
