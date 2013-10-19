package com.nabsys.resource.script;

import java.lang.reflect.Method;

public enum Function {
	APPEND,
	INSERT,
	SUBSTR,
	LENGTH,
	CHARAT,
	DELETE,
	DELETECHARAT,
	INDEXOF,
	LASTINDEXOF,
	REPLACE,
	FORMAT,
	TONUMBER,
	ABS,
	ADD,
	DIVIDE,
	SUBTRACT,
	MULTIPLY,
	REMAINDER,
	INTEGER,
	DOUBLE,
	FLOAT,
	LONG,
	STRING;
		
	public static Function get(String name) throws Exception
	{
		if(name.equals("APPEND")) 				return APPEND;
		else if(name.equals("INSERT")) 			return INSERT;
		else if(name.equals("SUBSTR"))	 		return SUBSTR;
		else if(name.equals("LENGTH")) 			return LENGTH;
		else if(name.equals("CHARAT")) 			return CHARAT;
		else if(name.equals("DELETE")) 			return DELETE;
		else if(name.equals("DELETECHARAT")) 	return DELETECHARAT;
		else if(name.equals("INDEXOF")) 		return INDEXOF;
		else if(name.equals("LASTINDEXOF")) 	return LASTINDEXOF;
		else if(name.equals("REPLACE")) 		return REPLACE;
		else if(name.equals("FORMAT")) 			return FORMAT;
		else if(name.equals("TONUMBER")) 		return TONUMBER;
		else if(name.equals("ABS")) 			return ABS;
		else if(name.equals("ADD")) 			return ADD;
		else if(name.equals("DIVIDE")) 			return DIVIDE;
		else if(name.equals("REMAINDER")) 		return REMAINDER;
		else if(name.equals("INTEGER")) 		return INTEGER;
		else if(name.equals("DOUBLE")) 			return DOUBLE;
		else if(name.equals("FLOAT")) 			return FLOAT;
		else if(name.equals("LONG")) 			return LONG;
		else if(name.equals("STRING")) 			return STRING;
		else if(name.equals("SUBTRACT")) 		return SUBTRACT;
		else if(name.equals("MULTIPLY")) 		return MULTIPLY;
		else 									throw new Exception("Unsupported function is called.");
	}
	
	public String pattern()
	{
		switch(this)
		{
		case APPEND:
			return "\\bAPPEND\\b";
		case INSERT:
			return "\\bINSERT\\b";
		case SUBSTR:
			return "\\bSUBSTR\\b";
		case LENGTH:
			return "\\bLENGTH\\b";
		case CHARAT:
			return "\\bCHARAT\\b";
		case DELETE:
			return "\\bDELETE\\b";
		case DELETECHARAT:
			return "\\bDELETECHARAT\\b";
		case INDEXOF:
			return "\\bINDEXOF\\b";
		case LASTINDEXOF:
			return "\\bLASTINDEXOF\\b";
		case REPLACE:
			return "\\bREPLACE\\b";
		case FORMAT:
			return "\\bFORMAT\\b";
		case TONUMBER:
			return "\\bTONUMBER\\b";
		case ABS:
			return "\\bABS\\b";
		case ADD:
			return "\\bADD\\b";
		case DIVIDE:
			return "\\bDIVIDE\\b";
		case REMAINDER:
			return "\\bREMAINDER\\b";
		case INTEGER:
			return "\\bINTEGER\\b";
		case DOUBLE:
			return "\\bDOUBLE\\b";
		case FLOAT:
			return "\\bFLOAT\\b";
		case LONG:
			return "\\bLONG\\b";
		case STRING:
			return "\\bSTRING\\b";
		case SUBTRACT:
			return "\\bSUBTRACT\\b";
		case MULTIPLY:
			return "\\bMULTIPLY\\b";
		default :
			return null;
		}
	}
	
	public Method method(int paramCount) throws SecurityException, NoSuchMethodException
	{
		switch(this)
		{
		case APPEND:
			return StringBuilder.class.getMethod("append", new Class[]{StringBuilder.class});
		case INSERT:
			return StringBuilder.class.getMethod("insert", new Class[]{BigDecimal.class, StringBuilder.class});
		case SUBSTR:
			if(paramCount == 2)
				return StringBuilder.class.getMethod("substring", new Class[]{BigDecimal.class});
			else if(paramCount == 3)
				return StringBuilder.class.getMethod("substring", new Class[]{BigDecimal.class, BigDecimal.class});
		case LENGTH:
			return StringBuilder.class.getMethod("length", new Class[]{});
		case CHARAT:
			return StringBuilder.class.getMethod("charAt", new Class[]{BigDecimal.class});
		case DELETE:
			return StringBuilder.class.getMethod("delete", new Class[]{BigDecimal.class, BigDecimal.class});
		case DELETECHARAT:
			return StringBuilder.class.getMethod("deleteCharAt", new Class[]{BigDecimal.class});
		case INDEXOF:
			return StringBuilder.class.getMethod("indexOf", new Class[]{StringBuilder.class});
		case LASTINDEXOF:
			return StringBuilder.class.getMethod("lastIndexOf", new Class[]{StringBuilder.class});
		case REPLACE:
			return StringBuilder.class.getMethod("replace", new Class[]{BigDecimal.class, BigDecimal.class, StringBuilder.class});
		case FORMAT:
			return StringBuilder.class.getMethod("format", new Class[]{StringBuilder.class, Object[].class});
		case TONUMBER:
			return StringBuilder.class.getMethod("toNumber", new Class[]{});
		case ABS:
			return BigDecimal.class.getMethod("abs", new Class[]{});
		case ADD:
			return BigDecimal.class.getMethod("add", new Class[]{BigDecimal.class});
		case DIVIDE:
			return BigDecimal.class.getMethod("divide", new Class[]{BigDecimal.class});
		case REMAINDER:
			return BigDecimal.class.getMethod("remainder", new Class[]{BigDecimal.class});
		case INTEGER:
			return BigDecimal.class.getMethod("toInt", new Class[]{});
		case DOUBLE:
			return BigDecimal.class.getMethod("toDouble", new Class[]{});
		case FLOAT:
			return BigDecimal.class.getMethod("toFloat", new Class[]{});
		case LONG:
			return BigDecimal.class.getMethod("toLong", new Class[]{});
		case STRING:
			return BigDecimal.class.getMethod("stringValue", new Class[]{});
		case SUBTRACT:
			return BigDecimal.class.getMethod("subtract", new Class[]{BigDecimal.class});
		case MULTIPLY:
			return BigDecimal.class.getMethod("multiply", new Class[]{BigDecimal.class});
		default :
			return null;
		}
	}
	
	public boolean chkParamType(Object[] params)
	{
		switch(this)
		{
		case APPEND:
			if(params.length != 2) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof StringBuilder || params[1] instanceof FuncParam)) return false;
			return true;
		case INSERT:
			if(params.length != 3) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam)|| !(params[2] instanceof StringBuilder || params[2] instanceof FuncParam)) return false;
			return true;
		case SUBSTR:
			if(params.length != 2 && params.length != 3) return false;
			if(params.length == 2 && (!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam))) return false;
			if(params.length == 3 && (!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam) || !(params[2] instanceof BigDecimal || params[2] instanceof FuncParam))) return false;
			return true;
		case LENGTH:
			if(params.length != 1) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam)) return false;
			return true;
		case CHARAT:
			if(params.length != 2) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam)) return false;
			return true;
		case DELETE:
			if(params.length != 3) return false;
			if(params.length == 3 && (!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam) || !(params[2] instanceof BigDecimal || params[1] instanceof FuncParam))) return false;
			return true;
		case DELETECHARAT:
			if(params.length != 2) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam)) return false;
			return true;
		case INDEXOF:
			if(params.length != 2) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof StringBuilder || params[1] instanceof FuncParam)) return false;
			return true;
		case LASTINDEXOF:
			if(params.length != 2) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof StringBuilder || params[1] instanceof FuncParam)) return false;
			return true;
		case REPLACE:
			if(params.length != 4) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam)|| !(params[2] instanceof BigDecimal || params[2] instanceof FuncParam)|| !(params[3] instanceof StringBuilder || params[3] instanceof FuncParam)) return false;
			return true;
		case FORMAT:
			if(params.length < 2) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam)) return false;
			return true;
		case TONUMBER:
			if(params.length != 1) return false;
			if(!(params[0] instanceof StringBuilder || params[0] instanceof FuncParam)) return false;
			return true;
		case ABS:
			if(params.length != 1) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam)) return false;
			return true;
		case ADD:
			if(params.length != 2) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam)) return false;
			return true;
		case DIVIDE:
			if(params.length != 2) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam)) return false;
			return true;
		case REMAINDER:
			if(params.length != 2) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam)) return false;
			return true;
		case INTEGER:
			if(params.length != 1) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam)) return false;
			return true;
		case DOUBLE:
			if(params.length != 1) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam)) return false;
			return true;
		case FLOAT:
			if(params.length != 1) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam)) return false;
			return true;
		case LONG:
			if(params.length != 1) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam)) return false;
			return true;
		case STRING:
			if(params.length != 1) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam)) return false;
			return true;
		case SUBTRACT:
			if(params.length != 2) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam)) return false;
			return true;
		case MULTIPLY:
			if(params.length != 2) return false;
			if(!(params[0] instanceof BigDecimal || params[0] instanceof FuncParam) || !(params[1] instanceof BigDecimal || params[1] instanceof FuncParam)) return false;
			return true;
		default :
			return false;
		}
	}
	
	public Class<?> returnType()
	{
		switch(this)
		{
		case APPEND:
			return StringBuilder.class;
		case INSERT:
			return StringBuilder.class;
		case SUBSTR:
			return StringBuilder.class;
		case LENGTH:
			return StringBuilder.class;
		case CHARAT:
			return StringBuilder.class;
		case DELETE:
			return StringBuilder.class;
		case DELETECHARAT:
			return StringBuilder.class;
		case INDEXOF:
			return BigDecimal.class;
		case LASTINDEXOF:
			return BigDecimal.class;
		case REPLACE:
			return StringBuilder.class;
		case FORMAT:
			return StringBuilder.class;
		case TONUMBER:
			return BigDecimal.class;
		case ABS:
			return BigDecimal.class;
		case ADD:
			return BigDecimal.class;
		case DIVIDE:
			return BigDecimal.class;
		case REMAINDER:
			return BigDecimal.class;
		case INTEGER:
			return BigDecimal.class;
		case DOUBLE:
			return BigDecimal.class;
		case FLOAT:
			return BigDecimal.class;
		case LONG:
			return BigDecimal.class;
		case STRING:
			return StringBuilder.class;
		case SUBTRACT:
			return BigDecimal.class;
		case MULTIPLY:
			return BigDecimal.class;
		default :
			return null;
		}
	}
	
	public Object getTmpReturnObject()
	{
		switch(this)
		{
		case APPEND:
			return new StringBuilder();
		case INSERT:
			return new StringBuilder();
		case SUBSTR:
			return new StringBuilder();
		case LENGTH:
			return new StringBuilder();
		case CHARAT:
			return new StringBuilder();
		case DELETE:
			return new StringBuilder();
		case DELETECHARAT:
			return new StringBuilder();
		case INDEXOF:
			return new BigDecimal(0);
		case LASTINDEXOF:
			return new BigDecimal(0);
		case REPLACE:
			return new StringBuilder();
		case FORMAT:
			return new StringBuilder();
		case TONUMBER:
			return new BigDecimal(0);
		case ABS:
			return new BigDecimal(0);
		case ADD:
			return new BigDecimal(0);
		case DIVIDE:
			return new BigDecimal(0);
		case REMAINDER:
			return new BigDecimal(0);
		case INTEGER:
			return new BigDecimal(0);
		case DOUBLE:
			return new BigDecimal(0);
		case FLOAT:
			return new BigDecimal(0);
		case LONG:
			return new BigDecimal(0);
		case STRING:
			return new StringBuilder();
		case SUBTRACT:
			return new BigDecimal(0);
		case MULTIPLY:
			return new BigDecimal(0);
		default :
			return null;
		}
	}
}
