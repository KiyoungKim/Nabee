package com.nabsys.resource.script;

import java.util.Locale;

public enum FunctionDescription {
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
	REMAINDER,
	INTEGER,
	DOUBLE,
	FLOAT,
	LONG,
	STRING,
	SUBTRACT,
	MULTIPLY;
		
	public static FunctionDescription get(String name)
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
		else if(name.equals("FORMAT"))	 		return FORMAT;
		else if(name.equals("TONUMBER"))	 	return TONUMBER;
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
		else 									return null;
	}
	
	public String getDescription(Locale locale)
	{
		switch(this)
		{
		case APPEND:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "APPEND(String targe, String source)\n\nsource의 문자열 값을 target문자열 뒤에 붙인다.";
			else
				return "APPEND(String targe, String source)\n\nAppends the string representation of the String argument source to target.";
		case INSERT:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "INSERT(String targe, int offset, String source)\n\nsource의 문자열을 target의 지정된 자리 offset 에 삽입한다. 그리고 그 자리에 있던 원래의 문자열은 삽입 된 source의 문자열 만큼 뒤로 밀리게 된다. source가 null 이라면 \"null\"이라는 문자열이 삽입된다.";
			else
				return "INSERT(String targe, int offset, String source)\n\nThe characters of the String source are inserted, in order, into target sequence at the indicated offset, moving up any characters originally above that position and increasing the length of target sequence by the length of the argument. If source is null, then the four characters \"null\" are inserted into target sequence.";
		case SUBSTR:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "SUBSTR(String str, int start)\n\nReturns a new String that contains a subsequence of characters currently contained in str character sequence. The substring begins at the specified index and extends to the end of this sequence.\n\nSUBSTR(String str, int start, int end)\n\nReturns a new String that contains a subsequence of characters currently contained in str sequence. The substring begins at the specified start and extends to the character at index end - 1.";
			else
				return "SUBSTR(String str, int start)\n\nReturns a new String that contains a subsequence of characters currently contained in str character sequence. The substring begins at the specified index and extends to the end of this sequence.\n\nSUBSTR(String str, int start, int end)\n\nReturns a new String that contains a subsequence of characters currently contained in str sequence. The substring begins at the specified start and extends to the character at index end - 1.";
		case LENGTH:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "LENGTH(String str)\n\nReturns the length of str sequence.";
			else
				return "LENGTH(String str)\n\nReturns the length of str sequence.";
		case CHARAT:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "CHARAT(String str, int index)\n\nReturns the char value in str sequence at the specified index. The first char value is at index 0, the next at index 1, and so on, as in array indexing. The index argument must be greater than or equal to 0, and less than the length of this sequence.";
			else
				return "CHARAT(String str, int index)\n\nReturns the char value in str sequence at the specified index. The first char value is at index 0, the next at index 1, and so on, as in array indexing. The index argument must be greater than or equal to 0, and less than the length of this sequence.";
		case DELETE:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "DELETE(String str, int start, int end)\n\nRemoves the characters in a substring of str sequence. The substring begins at the specified start and extends to the character at index end - 1 or to the end of the sequence if no such character exists. If start is equal to end, no changes are made.";
			else
				return "DELETE(String str, int start, int end)\n\nRemoves the characters in a substring of str sequence. The substring begins at the specified start and extends to the character at index end - 1 or to the end of the sequence if no such character exists. If start is equal to end, no changes are made.";
		case DELETECHARAT:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "DELETECHARAT(String str, int index)\n\nRemoves the char at the specified position in str sequence. This sequence is shortened by one char.";
			else
				return "DELETECHARAT(String str, int index)\n\nRemoves the char at the specified position in str sequence. This sequence is shortened by one char.";
		case INDEXOF:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "INDEXOF(String source, String str)\n\nReturns the index within source string of the first occurrence of the specified substring. The integer returned is the first index of first appearance.\n if it does not occur as a substring, -1 is returned.";
			else
				return "INDEXOF(String source, String str)\n\nReturns the index within source string of the first occurrence of the specified substring. The integer returned is the first index of first appearance.\n if it does not occur as a substring, -1 is returned.";
		case LASTINDEXOF:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "LASTINDEXOF(String source, String str)\n\nReturns the index within source string of the first occurrence of the specified substring. The integer returned is the first index of last appearance.\n if it does not occur as a substring, -1 is returned.";
			else
				return "LASTINDEXOF(String source, String str)\n\nReturns the index within source string of the first occurrence of the specified substring. The integer returned is the first index of last appearance.\n if it does not occur as a substring, -1 is returned.";
		case REPLACE:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "REPLACE(int start, int end, String str)\n\nReplaces the characters in a substring of this sequence with characters in the specified String. The substring begins at the specified start and extends to the character at index end - 1 or to the end of the sequence if no such character exists. First the characters in the substring are removed and then the specified String is inserted at start.";
			else
				return "REPLACE(int start, int end, String str)\n\nReplaces the characters in a substring of this sequence with characters in the specified String. The substring begins at the specified start and extends to the character at index end - 1 or to the end of the sequence if no such character exists. First the characters in the substring are removed and then the specified String is inserted at start.";
		case FORMAT:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "FORMAT(String format, Object ... args)\n\nWrites and returns a formatted string using the specified locale, format string, and arguments.";
			else
				return "FORMAT(String format, Object ... args)\n\nWrites and returns a formatted string using the specified locale, format string, and arguments.";
		case TONUMBER:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "TONUMBER(String str)\n\nReturns the number value representation of this str.";
			else
				return "TONUMBER(String str)\n\nReturns the number value representation of this str.";
		
		case ABS:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "ABS(Number num)\n\nReturns a Number whose value is the absolute value of this num, and whose scale is num.scale().";
			else
				return "ABS(Number num)\n\nReturns a Number whose value is the absolute value of this num, and whose scale is num.scale().";
		case ADD:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "ADD(Number num1, Number num2)\n\nReturns a Number whose value is (num1 + num2), and whose scale is max(num1.scale(), num2.scale()).";
			else
				return "ADD(Number num1, Number num2)\n\nReturns a Number whose value is (num1 + num2), and whose scale is max(num1.scale(), num2.scale()).";
		case DIVIDE:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "DIVIDE(Number num1, Number num2)\n\nReturns a Number whose value is (num1 / num2), and whose preferred scale is (num1.scale() - num2.scale()); if the exact quotient cannot be represented (because it has a non-terminating decimal expansion) an ArithmeticException is thrown.";
			else
				return "DIVIDE(Number num1, Number num2)\n\nReturns a Number whose value is (num1 / num2), and whose preferred scale is (num1.scale() - num2.scale()); if the exact quotient cannot be represented (because it has a non-terminating decimal expansion) an ArithmeticException is thrown.";
		case REMAINDER:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "REMAINDER(Number num1, Number num2)\n\nReturns a Number whose value is (num1 % num2).";
			else
				return "REMAINDER(Number num1, Number num2)\n\nReturns a Number whose value is (num1 % num2).";
		case INTEGER:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "INTEGER(Number num)\n\nConverts num to an int.";
			else
				return "INTEGER(Number num)\n\nConverts num to an int.";
		case DOUBLE:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "DOUBLE(Number num)\n\nConverts num to a double.";
			else
				return "DOUBLE(Number num)\n\nConverts num to a double.";
		case FLOAT:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "FLOAT(Number num)\n\nConverts num to a float.";
			else
				return "FLOAT(Number num)\n\nConverts num to a float.";
		case LONG:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "LONG(Number num)\n\nConverts num to a long.";
			else
				return "LONG(Number num)\n\nConverts num to a long.";
		case STRING:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "STRING(Number num)\n\nReturns the string representation of this num.";
			else
				return "STRING(Number num)\n\nReturns the string representation of this num.";
		case SUBTRACT:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "SUBTRACT(Number num1, Number num2)\n\nReturns a Number whose value is (num1 - num2), and whose scale is max(num1.scale(), num2.scale()).";
			else
				return "SUBTRACT(Number num1, Number num2)\n\nReturns a Number whose value is (num1 - num2), and whose scale is max(num1.scale(), num2.scale()).";
		case MULTIPLY:
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "MULTIPLY(Number num1, Number num2)\n\nReturns a Number whose value is (num1 × num2), and whose scale is (num1.scale() + num2.scale()).";
			else
				return "MULTIPLY(Number num1, Number num2)\n\nReturns a Number whose value is (num1 × num2), and whose scale is (num1.scale() + num2.scale()).";
		default :
			if(locale == Locale.KOREA || locale == Locale.KOREAN)
				return "";
			else
				return "";
		}
	}
}
