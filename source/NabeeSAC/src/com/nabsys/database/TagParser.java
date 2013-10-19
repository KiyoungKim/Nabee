package com.nabsys.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

public final class TagParser {
	private Pattern 	tagOpenPattern 		= null;
	private Pattern 	tagClosePattern 	= null;
	private Pattern		tagPattern			= null;
	private Pattern 	parameterPattern 	= null;
	private Pattern 	operatorPattern 	= null;
	private Pattern 	valuePattern 		= null;
	private Pattern		paramPattern		= null;
	private Pattern		commentPattern		= null;
	private Pattern		commentClosePattern	= null;
	private boolean		needParam			= false;
	private ArrayList<int[]> commentLocation = null;
	private int			START				= 0;
	private int			END					= 1;
	
	public TagParser()
	{
		tagOpenPattern 		= Pattern.compile("<+\\p{javaWhitespace}*dynamic+.*\\p{javaWhitespace}*>");
		tagClosePattern 	= Pattern.compile("<+\\p{javaWhitespace}*/+\\p{javaWhitespace}*dynamic+\\p{javaWhitespace}*>");
		tagPattern			= Pattern.compile("<+\\p{javaWhitespace}*/?\\p{javaWhitespace}*dynamic+.*\\p{javaWhitespace}*>");
		parameterPattern 	= Pattern.compile("parameter+[ =]+\"+[ 0-9a-zA-Z:_]*+\"");
		operatorPattern 	= Pattern.compile("operator+[ =]+\"+[ !<>=]*+\"");
		valuePattern 		= Pattern.compile("value+[ =]+\"+(|(.))+\"");
		paramPattern		= Pattern.compile(":\\w+");
		commentPattern		= Pattern.compile("((?s)/\\*.*)|(\\-\\-.*)");
		commentClosePattern	= Pattern.compile("\\*/");
		
		commentLocation = new ArrayList<int[]>();
	}
	
	private int checkInComment(int start, int end)
	{
		int[] cl = null;
		for(int i=0; i<commentLocation.size(); i++)
		{
			cl = commentLocation.get(i);
			if(cl[START] < start && cl[END] > start)
			{
				return cl[END];
			}
			
			if(cl[START] < end && cl[END] > end)
			{
				return cl[END];
			}
		}
		
		return 0;
	}
	
	public TagDocument parse(String string) throws ParserConfigurationException
	{
		string = "<dynamic>\n" + string + "\n</dynamic>";
		
		Matcher commentMatcher = commentPattern.matcher(string);
		Matcher commentCloseMatcher = commentClosePattern.matcher(string);

		int offset = 0;
		while(commentMatcher.find(offset))
		{
			int[] location	 = new int[2];
			location[START] = commentMatcher.start();
			location[END]	= commentMatcher.end();
			offset = location[END];
			if(string.substring(location[START], location[START]+2).equals("/*"))
			{
				if(commentCloseMatcher.find(location[START]))
				{
					location[END] = commentCloseMatcher.end();
					offset = location[END];
				}
			}
			commentLocation.add(location);
		}
		
		Matcher openMatcher = tagOpenPattern.matcher(string);
		Matcher closeMatcher = tagClosePattern.matcher(string);
		
		int openCnt = 0;
		int closeCnt = 0;
		while(openMatcher.find())
		{
			if(checkInComment(openMatcher.start(), openMatcher.end()) != 0) continue;
			openCnt++;
		}
		while(closeMatcher.find())
		{
			if(checkInComment(closeMatcher.start(), closeMatcher.end()) != 0) continue;
			closeCnt++;
		}

		if(openCnt != closeCnt) throw new ParserConfigurationException("Parse exception occurred. Check tag pairs.");
		
		TagDocument root = new TagDocument(null);
		parse(string, root, 0);
		
		return root;
	}

	private int parse(String string, TagDocument parent, int start) throws ParserConfigurationException
	{
		Matcher matcher = tagPattern.matcher(string);
		
		int nextSearch = start;
		while(matcher.find(nextSearch))
		{
			int chkEnd = 0;
			if((chkEnd = checkInComment(matcher.start(), matcher.end())) != 0)
			{
				nextSearch = chkEnd;
				continue;
			}
			
			if(isCloseTag(string.substring(matcher.start(), matcher.end())))
			{
				if(!string.substring(start, matcher.start()).replace("\n", "").trim().equals(""))
				{
					TagDocument doc = new TagDocument(parent);
					String str = string.substring(start, matcher.start());
					Matcher paramMatcher = paramPattern.matcher(str);
					int nextParamSearch = nextSearch - start;

					while(paramMatcher.find(nextParamSearch))
					{
						int paramStart = paramMatcher.start();
						int paramEnd = paramMatcher.end();

						nextParamSearch = checkInComment(paramStart + start, paramEnd + start);

						if(nextParamSearch >= str.length() + start - 1)
						{
							break;
						}
						else if(nextParamSearch != 0)
						{
							nextParamSearch = nextParamSearch - start;
							continue;
						}
						else
						{
							nextParamSearch = paramStart;
						}

						String paramStr = str.substring(paramStart + 1, paramEnd).trim();
						doc.addParams(paramStr);
						needParam = true;
						String tmp = str.substring(paramStart);
						
						tmp = tmp.replaceFirst(":" +paramStr, "?");
						str = str.substring(0, paramStart);
						str += tmp;
						
						int adj = paramStr.length();
						for(int k=0; k<commentLocation.size(); k++)
						{
							if(paramStart <= commentLocation.get(k)[START])
							{
								commentLocation.get(k)[START] -= adj;
								commentLocation.get(k)[END] -= adj;
							}
						}

						paramMatcher = paramPattern.matcher(str);
					}

					doc.setTextValue(str);
				}
				return matcher.end();
			}
			else
			{
				if(start < matcher.start() && !string.substring(start, matcher.start()).replace("\n", "").trim().equals(""))
				{
					TagDocument doc = new TagDocument(parent);
					String str = string.substring(start, matcher.start());
					Matcher paramMatcher = paramPattern.matcher(str);

					int nextParamSearch = nextSearch - start;
					while(paramMatcher.find(nextParamSearch))
					{
						int paramStart = paramMatcher.start();
						int paramEnd = paramMatcher.end();
						
						nextParamSearch = checkInComment(paramStart + start, paramEnd + start);
						if(nextParamSearch != 0)
						{
							continue;
						}
						else
						{
							nextParamSearch = paramStart;
						}
						
						String paramStr = str.substring(paramStart + 1, paramEnd).trim();
						doc.addParams(paramStr);
						
						needParam = true;
						String tmp = str.substring(paramStart);
						
						tmp = tmp.replaceFirst(":" +paramStr, "?");
						str = str.substring(0, paramStart);
						str += tmp;
						
						int adj = paramStr.length();

						for(int k=0; k<commentLocation.size(); k++)
						{
							if(paramStart <= commentLocation.get(k)[START])
							{
								commentLocation.get(k)[START] -= adj;
								commentLocation.get(k)[END] -= adj;
							}
						}
						
						paramMatcher = paramPattern.matcher(str);
					}

					doc.setTextValue(str);
				}
				
				TagDocument doc = new TagDocument(parent);
				
				setProperties(string.substring(matcher.start(), matcher.end()), doc);
				start = parse(string, doc, matcher.end());
				nextSearch = start;
			}
		}
		return string.length();
	}

	private boolean isCloseTag(String string)
	{
		Matcher closeMatcher = tagClosePattern.matcher(string);
		return closeMatcher.find();
	}

	private void setProperties(String tagString, TagDocument doc)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		Matcher paramMatcher = parameterPattern.matcher(tagString);
		Matcher opMatcher = operatorPattern.matcher(tagString);
		Matcher vlMatcher = valuePattern.matcher(tagString);
		
		if(paramMatcher.find())
		{
			String paramString = tagString.substring(paramMatcher.start(), paramMatcher.end());
			Matcher rightMatcher = Pattern.compile("\"+.+\"").matcher(paramString);
			rightMatcher.find();
			map.put("parameter", paramString.substring(rightMatcher.start()+2, rightMatcher.end()-1).trim());
			needParam = true;
		}
		
		if(opMatcher.find())
		{
			String opString = tagString.substring(opMatcher.start(), opMatcher.end());
			Matcher rightMatcher = Pattern.compile("\"+.+\"").matcher(opString);
			rightMatcher.find();
			map.put("operator", opString.substring(rightMatcher.start()+1, rightMatcher.end()-1).trim());
		}
		
		if(vlMatcher.find())
		{
			String vlString = tagString.substring(vlMatcher.start(), vlMatcher.end());
			Matcher rightMatcher = Pattern.compile("\"+(|(.))+\"").matcher(vlString);
			rightMatcher.find();
			map.put("value", vlString.substring(rightMatcher.start()+1, rightMatcher.end()-1).trim());
		}
		
		doc.setProperty(map);
	}
	
	public boolean isNeedParam()
	{
		return this.needParam;
	}
}
