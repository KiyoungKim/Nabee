package com.nabsys.nabeeplus.design.model;

import java.sql.Types;

public class SqlTypeArray {
	public static final int ARRAY 			= Types.ARRAY;
	public static final int BIGINT 			= Types.BIGINT;
	public static final int BINARY 			= Types.BINARY;
	public static final int BIT 			= Types.BIT;
	public static final int BLOB 			= Types.BLOB;
	public static final int BOOLEAN 		= Types.BOOLEAN;
	public static final int CHAR 			= Types.CHAR;
	public static final int CLOB 			= Types.CLOB;
	public static final int DATALINK 		= Types.DATALINK;
	public static final int DATE 			= Types.DATE;
	public static final int DECIMAL 		= Types.DECIMAL;
	public static final int DISTINCT 		= Types.DISTINCT;
	public static final int DOUBLE 			= Types.DOUBLE;
	public static final int FLOAT		 	= Types.FLOAT;
	public static final int INTEGER 		= Types.INTEGER;
	public static final int LONGVARBINARY 	= Types.LONGVARBINARY;
	public static final int LONGVARCHAR 	= Types.LONGVARCHAR;
	public static final int NUMERIC 		= Types.NUMERIC;
	public static final int REAL 			= Types.REAL;
	public static final int REF 			= Types.REF;
	public static final int SMALLINT	 	= Types.SMALLINT;
	public static final int TIME 			= Types.TIME;
	public static final int TIMESTAMP 		= Types.TIMESTAMP;
	public static final int TINYINT 		= Types.TINYINT;
	public static final int VARBINARY 		= Types.VARBINARY;
	public static final int VARCHAR 		= Types.VARCHAR;
	public static final String[] TYPE = new String[] {
		"ARRAY",
		"BIGINT",
		"BINARY",
		"BIT",
		"BLOB",
		"BOOLEAN",
		"CHAR",
		"CLOB",
		"DATALINK",
		"DATE",
		"DECIMAL",
		"DISTINCT",
		"DOUBLE",
		"FLOAT",
		"INTEGER",
		"LONGVARBINARY",
		"LONGVARCHAR",
		"NUMERIC",
		"REAL",
		"REF",
		"SMALLINT",
		"TIME",
		"TIMESTAMP",
		"TINYINT",
		"VARBINARY",
		"VARCHAR"
	};
	public static String toString(int type)
	{
		if(type == Types.ARRAY) return "ARRAY";
		else if(type == Types.BIGINT) return "BIGINT";
		else if(type == Types.BINARY) return "BINARY";
		else if(type == Types.BIT) return "BIT";
		else if(type == Types.BLOB) return "BLOB";
		else if(type == Types.BOOLEAN) return "BOOLEAN";
		else if(type == Types.CHAR) return "CHAR";
		else if(type == Types.CLOB) return "CLOB";
		else if(type == Types.DATALINK) return "DATALINK";
		else if(type == Types.DATE) return "DATE";
		else if(type == Types.DECIMAL) return "DECIMAL";
		else if(type == Types.DISTINCT) return "DISTINCT";
		else if(type == Types.DOUBLE) return "DOUBLE";
		else if(type == Types.FLOAT) return "FLOAT";
		else if(type == Types.INTEGER) return "INTEGER";
		else if(type == Types.LONGVARBINARY) return "LONGVARBINARY";
		else if(type == Types.LONGVARCHAR) return "LONGVARCHAR";
		else if(type == Types.NUMERIC) return "NUMERIC";
		else if(type == Types.REAL) return "REAL";
		else if(type == Types.REF) return "REF";
		else if(type == Types.SMALLINT) return "SMALLINT";
		else if(type == Types.TIME) return "TIME";
		else if(type == Types.TIMESTAMP) return "TIMESTAMP";
		else if(type == Types.TINYINT) return "TINYINT";
		else if(type == Types.VARBINARY) return "VARBINARY";
		else if(type == Types.VARCHAR) return "VARCHAR";
		else return "";
	}
	public static int getIndex(int type)
	{
		if(type == Types.ARRAY) return 0;
		else if(type == Types.BIGINT) return 1;
		else if(type == Types.BINARY) return 2;
		else if(type == Types.BIT) return 3;
		else if(type == Types.BLOB) return 4;
		else if(type == Types.BOOLEAN) return 5;
		else if(type == Types.CHAR) return 6;
		else if(type == Types.CLOB) return 7;
		else if(type == Types.DATALINK) return 8;
		else if(type == Types.DATE) return 9;
		else if(type == Types.DECIMAL) return 10;
		else if(type == Types.DISTINCT) return 11;
		else if(type == Types.DOUBLE) return 12;
		else if(type == Types.FLOAT) return 13;
		else if(type == Types.INTEGER) return 14;
		else if(type == Types.LONGVARBINARY) return 15;
		else if(type == Types.LONGVARCHAR) return 16;
		else if(type == Types.NUMERIC) return 17;
		else if(type == Types.REAL) return 18;
		else if(type == Types.REF) return 19;
		else if(type == Types.SMALLINT) return 20;
		else if(type == Types.TIME) return 21;
		else if(type == Types.TIMESTAMP) return 22;
		else if(type == Types.TINYINT) return 23;
		else if(type == Types.VARBINARY) return 24;
		else if(type == Types.VARCHAR) return 25;
		else return -1;
	}
	public static int getType(int index)
	{
		switch(index){
			case 0:
			      return ARRAY;
			case 1:
			      return BIGINT;
			case 2:
			      return BINARY;
			case 3:
			      return BIT;
			case 4:
			      return BLOB;
			case 5:
			      return BOOLEAN;
			case 6:
			      return CHAR;
			case 7:
			      return CLOB;
			case 8:
			      return DATALINK;
			case 9:
			      return DATE;
			case 10:
			      return DECIMAL;
			case 11:
			      return DISTINCT;
			case 12:
			      return DOUBLE;
			case 13:
			      return FLOAT;
			case 14:
			      return INTEGER;
			case 15:
			      return LONGVARBINARY;
			case 16:
			      return LONGVARCHAR;
			case 17:
			      return NUMERIC;
			case 18:
			      return REAL;
			case 19:
			      return REF;
			case 20:
			      return SMALLINT;
			case 21:
			      return TIME;
			case 22:
			      return TIMESTAMP;
			case 23:
			      return TINYINT;
			case 24:
			      return VARBINARY;
			case 25:
			      return VARCHAR;
			default:
			      return Types.NULL;
		}
	}
	public static int getType(String type)
	{
		if(type.equals("ARRAY")) return ARRAY;
		else if(type.equals("BIGINT")) return BIGINT;
		else if(type.equals("BINARY")) return BINARY;
		else if(type.equals("BIT")) return BIT;
		else if(type.equals("BLOB")) return BLOB;
		else if(type.equals("BOOLEAN")) return BOOLEAN;
		else if(type.equals("CHAR")) return CHAR;
		else if(type.equals("CLOB")) return CLOB;
		else if(type.equals("DATALINK")) return DATALINK;
		else if(type.equals("DATE")) return DATE;
		else if(type.equals("DECIMAL")) return DECIMAL;
		else if(type.equals("DISTINCT")) return DISTINCT;
		else if(type.equals("DOUBLE")) return DOUBLE;
		else if(type.equals("FLOAT")) return FLOAT;
		else if(type.equals("INTEGER")) return INTEGER;
		else if(type.equals("LONGVARBINARY")) return LONGVARBINARY;
		else if(type.equals("LONGVARCHAR")) return LONGVARCHAR;
		else if(type.equals("NUMERIC")) return NUMERIC;
		else if(type.equals("REAL")) return REAL;
		else if(type.equals("REF")) return REF;
		else if(type.equals("SMALLINT")) return SMALLINT;
		else if(type.equals("TIME")) return TIME;
		else if(type.equals("TIMESTAMP")) return TIMESTAMP;
		else if(type.equals("TINYINT")) return TINYINT;
		else if(type.equals("VARBINARY")) return VARBINARY;
		else if(type.equals("VARCHAR")) return VARCHAR;
		else return Types.NULL;
	}
}
