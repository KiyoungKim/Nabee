package com.nabsys.resource.script;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuncParser {
	private Pattern stPtrn			= null;
	private Pattern thenPtrn		= null;
	private Pattern cmptTagPtrn		= null;
	private Pattern grpPtrn			= null;
	private Pattern cndPtrn			= null;
	private Pattern relationPtrn	= null;
	private String funcStPtrnStr	= null;
	private String ifStr			= null;
	private String elseIfStr		= null;
	private String funcPtrnStr		= null;
	private String dqPtrnStr		= null;
	private String prmPtrnStr		= null;
	
	
	public FuncParser()
	{
		initKeywords();
	}
	
	
	public FunctionExecutor parseScript(String script) throws Exception
	{
		FunctionExecutor executor = new FunctionExecutor();
		if(script.trim().length() <= 0) return executor;
		
		Matcher cndMchr = cndPtrn.matcher(script);
		
		boolean ifFound = false;
		int offset = 0;
		while(cndMchr.find(offset))
		{
			String keyword = script.substring(cndMchr.start(), cndMchr.end());
	
			if(keyword.matches(ifStr))
			{
				if(ifFound)
				{
					if(!script.substring(offset, cndMchr.start()).trim().equals("")) throw new Exception("Script pattern error");
					offset = parseScript(executor.get(executor.size() - 1), script.substring(cndMchr.start()));
					offset += cndMchr.start();
				}
				else
				{
					Comparison comparison = new Comparison(null, true);
					offset = getComparison(comparison, script.substring(cndMchr.end()), true);
					offset += cndMchr.end();
					executor.add(comparison);
					ifFound = true;
				}
			}
			else if(keyword.matches(elseIfStr))
			{
				if(!ifFound) throw new Exception("Script parsing error. 'IF' is needed.");
				if(!executor.get(executor.size() - 1).hasSubExecutor())
				{
					executor.get(executor.size() - 1).setFuncFactory(parseFunction(script.substring(offset, cndMchr.start()).trim(), 0));
				}
				
				Comparison comparison = new Comparison(null, true);
				offset = getComparison(comparison, script.substring(cndMchr.end()), true);
				offset += cndMchr.end();
				executor.add(comparison);
			}
			else if(keyword.equals("ENDIF"))
			{
				if(!ifFound) throw new Exception("Script parsing error. 'IF' is needed.");
				if(!executor.get(executor.size() - 1).hasSubExecutor())
				{
					executor.get(executor.size() - 1).setFuncFactory(parseFunction(script.substring(offset, cndMchr.start()).trim(), 0));
				}
				
				return executor;
			}
			else
			{
				if(!ifFound) throw new Exception("Script parsing error. 'IF' is needed.");
				if(!executor.get(executor.size() - 1).hasSubExecutor())
				{
					executor.get(executor.size() - 1).setFuncFactory(parseFunction(script.substring(offset, cndMchr.start()).trim(), 0));
				}
				
				Comparison comparison = new Comparison(null, true);
				offset = cndMchr.end();
				executor.add(comparison);
			}
		}
		
		if(ifFound)
		{
			throw new Exception("Script parsing error. 'ENDIF' is needed.");
		}
		else
		{
			Comparison comparison = new Comparison(null, true);
			comparison.setFuncFactory(parseFunction(script.trim(), 0));
			executor.add(comparison);
			return executor;
		}
	}
	
	private int parseScript(Comparison parent, String script) throws Exception
	{
		FunctionExecutor executor = new FunctionExecutor();
		parent.setSubExecutor(executor);

		if(script.trim().length() <= 0) return script.length();
		
		Matcher cndMchr = cndPtrn.matcher(script);
		boolean ifFound = false;
		int offset = 0;
		while(cndMchr.find(offset))
		{
			String keyword = script.substring(cndMchr.start(), cndMchr.end());

			if(keyword.matches(ifStr))
			{
				if(ifFound)
				{
					if(!script.substring(offset, cndMchr.start()).trim().equals("")) throw new Exception("Script pattern error");
					offset = parseScript(executor.get(executor.size() - 1), script.substring(cndMchr.start()));
					offset += cndMchr.start();
				}
				else
				{
					Comparison comparison = new Comparison(null, true);
					offset = getComparison(comparison, script.substring(cndMchr.end()), true);
					offset += cndMchr.end();
					executor.add(comparison);
					ifFound = true;
				}
			}
			else if(keyword.matches(elseIfStr))
			{
				if(!ifFound) throw new Exception("Script parsing error. 'IF' is needed.");
				if(!executor.get(executor.size() - 1).hasSubExecutor())
				{
					executor.get(executor.size() - 1).setFuncFactory(parseFunction(script.substring(offset, cndMchr.start()).trim(), 0));
				}
				
				Comparison comparison = new Comparison(null, true);
				offset = getComparison(comparison, script.substring(cndMchr.end()), true);
				offset += cndMchr.end();
				executor.add(comparison);
			}
			else if(keyword.equals("ENDIF"))
			{
				if(!ifFound) throw new Exception("Script parsing error. 'IF' is needed.");
				if(!executor.get(executor.size() - 1).hasSubExecutor())
				{
					executor.get(executor.size() - 1).setFuncFactory(parseFunction(script.substring(offset, cndMchr.start()).trim(), 0));
				}
				return cndMchr.end();
			}
			else
			{
				if(!ifFound) throw new Exception("Script parsing error. 'IF' is needed.");
				if(!executor.get(executor.size() - 1).hasSubExecutor())
				{
					executor.get(executor.size() - 1).setFuncFactory(parseFunction(script.substring(offset, cndMchr.start()).trim(), 0));
				}
				
				Comparison comparison = new Comparison(null, true);
				offset = cndMchr.end();
				executor.add(comparison);
			}
		}
		
		if(ifFound)
		{
			throw new Exception("Script parsing error. 'ENDIF' is needed.");
		}
		else
		{
			Comparison comparison = new Comparison(null, true);
			comparison.setFuncFactory(parseFunction(script.trim(), 0));
			executor.add(comparison);
			return script.length();
		}
	}
	
	private boolean chkBracket(String script)
	{
		int pairChk = 0;
		boolean meetQuote = false;
		for(int i=0; i<script.length(); i++)
		{
			if(script.charAt(i) == '"')
			{
				if(meetQuote) meetQuote = false;
				else meetQuote = true;
			}
			
			if(!meetQuote)
			{
				if(script.charAt(i) == '(')
				{
					pairChk++;
				}
				else if(script.charAt(i) == ')')
				{
					pairChk--;
				}
			}
		}
		
		return pairChk == 0;
	}
	
	private int[][] getSkipRegion(String script)
	{
		boolean skipStart = false;
		ArrayList<int[]> list = new ArrayList<int[]>();
		int[] skip = null;
		for(int i=0; i<script.length(); i++)
		{
			if(script.charAt(i) == '"')
			{
				if(skipStart)
				{
					skip[1] = i;
					list.add(skip);
					skipStart = false;
				}
				else
				{
					skip = new int[2];
					skip[0] = i;
					skipStart = true;
				}
			}
		}
		
		return list.toArray(new int[list.size()][2]);
	}
	
	private int getComparison(Comparison parent, String script, boolean thenExists) throws Exception
	{
		int rtn = script.length();
		int[][] skipRegion = getSkipRegion(script);
		if(thenExists)
		{
			Matcher	thenMchr = thenPtrn.matcher(script);
			boolean isSkip = false;
			boolean isFind = false;
			int mchrStart = 0;
			int mchrEnd = 0;
			while(thenMchr.find())
			{
				for(int i=0; i<skipRegion.length; i++)
				{
					if(skipRegion[i][0] <= thenMchr.start() && skipRegion[i][1] >= thenMchr.end())
					{
						isSkip = true;
					}
				}

				if(!isSkip)
				{
					mchrStart = thenMchr.start();
					mchrEnd = thenMchr.end();
					isFind = true;
					break;
				}
				isSkip = false;
			}

			if(!isFind)
			{
				throw new Exception("Script pattern error. Needs 'THEN' keyword.");
			}

			script = script.substring(0, mchrStart);

			Matcher cndMchr = cndPtrn.matcher(script);
			isSkip = false;
			isFind = false;
			while(cndMchr.find())
			{
				for(int i=0; i<skipRegion.length; i++)
				{
					if(skipRegion[i][0] <= cndMchr.start() && skipRegion[i][1] >= cndMchr.end())
					{
						isSkip = true;
					}
				}
				
				if(!isSkip)
				{
					isFind = true;
					break;
				}
				isSkip = false;
			}
			
			if(isFind)
			{
				throw new Exception("Script pattern error. Needs 'THEN' keyword.");
			}
			
			if(!chkBracket(script)) throw new Exception("Bracket matching error : \n" + script);

			rtn = mchrEnd;
		}
		
		String[] list = splitComparison(script);
		
		Comparison comparison = null;
		for(int i=0; i<list.length; i++)
		{
			if(list[i].equals("&&"))
			{
				boolean nega = true;
				if(list.length > i + 1)
				{
					if(list[i+1].matches("^!\\p{javaWhitespace}*\\(.*")) nega = false;
				}
				comparison = new Comparison(parent, nega);
				comparison.setPrevRelation(1);
			}
			else if(list[i].equals("||"))
			{
				boolean nega = true;
				if(list.length > i + 1)
				{
					if(list[i+1].matches("^!\\p{javaWhitespace}*\\(.*")) nega = false;
				}
				comparison = new Comparison(parent, nega);
				comparison.setPrevRelation(2);
			}
			else
			{
				if(comparison == null)
				{
					boolean nega = !(list[i].matches("^!\\p{javaWhitespace}*\\(.*"));
					comparison = new Comparison(parent, nega);
					comparison.setPrevRelation(0);
				}
				
				Matcher grpMatcher = grpPtrn.matcher(list[i]);
				int offset = 0;
				//TODO Need to skip in double quotes.
				while(grpMatcher.find(offset))
				{
					int endIndex = getCloseIndex(list[i].substring(grpMatcher.end() - 1))  + grpMatcher.end() - 2;
					offset = getComparison(comparison, list[i].substring(grpMatcher.end(), endIndex), false);
					offset += grpMatcher.end();
				}

				String coreComparison = list[i].substring(offset).trim();

				if(coreComparison.matches(".*(<=|<|>=|==|!=|<>).*"))
				{
					analyzaComparison(comparison, coreComparison);
				}
				
				comparison = null;
			}
		}
		return rtn;
	}
	
	private void analyzaComparison(Comparison comparison, String script) throws Exception
	{
		Matcher cmptTagMchr = cmptTagPtrn.matcher(script);
		
		int[][] skipRegion = getSkipRegion(script);
		boolean isSkip = false;
		boolean isFind = false;
		while(cmptTagMchr.find())
		{
			for(int i=0; i<skipRegion.length; i++)
			{
				if(skipRegion[i][0] <= cmptTagMchr.start() && skipRegion[i][1] >= cmptTagMchr.end())
				{
					isSkip = true;
				}
			}
			
			if(!isSkip)
			{
				isFind = true;
				break;
			}
			isSkip = false;
		}
		
		if(!isFind) throw new Exception("Compare script parsing error");
		
		String[] cmptArray = new String[3];
		cmptArray[0] = script.substring(0, cmptTagMchr.start()).trim();
		cmptArray[1] = script.substring(cmptTagMchr.end()).trim();
		cmptArray[2] = script.substring(cmptTagMchr.start(), cmptTagMchr.end()).trim();
		for(int i=0; i<3; i++)
		{
			if(i == 2)
			{
				comparison.setComparison(i, cmptArray[i]);
			}
			else if(cmptArray[i].matches(funcPtrnStr))
			{
				comparison.setComparison(i, parseFunction(cmptArray[i], 0));
			}
			else if(cmptArray[i].matches(dqPtrnStr))
			{
				comparison.setComparison(i, new StringBuilder(cmptArray[i].replaceAll("\"", "")));
			}
			else if(cmptArray[i].matches(prmPtrnStr))
			{
				comparison.setComparison(i, new FuncParam(cmptArray[i]));
			}
			else
			{
				if((cmptArray[i] + "").trim().toUpperCase().equals("NULL"))
				{
					comparison.setComparison(i, null);
				}
				else
				{
					comparison.setComparison(i, new BigDecimal(cmptArray[i]));
				}
			}
		}
	}
	
	private String[] splitComparison(String script)
	{
		Matcher m = relationPtrn.matcher(script);
		ArrayList<String> list = new ArrayList<String>();
		int prevIndex = 0;
		while(m.find())
		{
			if(getPairChk(script.substring(prevIndex, m.start())) == 0)
			{
				list.add(script.substring(prevIndex, m.start()).trim());
				list.add(script.substring(m.start(), m.end()));
				prevIndex = m.end();
			}
		}
		
		list.add(script.substring(prevIndex).trim());
		String[] listArray = new String[list.size()];
		return list.toArray(listArray);
	}
	
	private int getPairChk(String script)
	{
		int pairChk = 0;
		for(int i=0; i<script.length(); i++)
		{
			if(script.charAt(i) == '(')
			{
				pairChk++;
			}
			else if(script.charAt(i) == ')')
			{
				pairChk--;
			}
		}
		return pairChk;
	}
	
	private FuncFactory parseFunction(String script, int offset) throws Exception
	{
		FuncFactory ff = null;
		Matcher stMchr = stPtrn.matcher(script);
		boolean mchrExist = false;

		while(stMchr.find(offset))
		{
			mchrExist = true;
			int endIndex = getCloseIndex(script.substring(stMchr.end() - 1)) + stMchr.end() -1;

			Object[] paramArray = getParamArray(script.substring(stMchr.end(), endIndex - 1));

			Object[] objParam = new Object[paramArray.length];
			for(int i=0; i<paramArray.length; i++)
			{
				if(paramArray[i] instanceof String && ((String)paramArray[i]).matches(funcPtrnStr))
				{
					objParam[i] = parseFunction((String)paramArray[i], 0);
				}
				else
				{
					if(((String)paramArray[i]).matches(dqPtrnStr))
					{
						objParam[i] = new StringBuilder(((String)paramArray[i]).replaceAll("\"", ""));
					}
					else if(((String)paramArray[i]).matches(prmPtrnStr))
					{
						objParam[i] = new FuncParam(paramArray[i] + "");
					}
					else
					{
						if((paramArray[i] + "").toUpperCase().trim().equals("NULL"))
						{
							objParam[i] = null;
						}
						else
						{
							objParam[i] = new BigDecimal(paramArray[i] + "");
						}
					}
				}
			}
			
			ff = new FuncFactory(Function.get(script.substring(stMchr.start(), stMchr.end() - 1)), objParam);
			
			offset = endIndex;
		}
		
		if(!mchrExist)
		{
			if(script.substring(offset).matches(dqPtrnStr))
			{
				ff = new FuncFactory(new StringBuilder(script.replaceAll("\"", "")));
			}
			else if(script.substring(offset).matches(prmPtrnStr))
			{
				ff = new FuncFactory(new FuncParam(script));
			}
			else
			{
				ff = new FuncFactory(new BigDecimal(script));
			}
		}
		
		return ff;
	}
	
	private Object[] getParamArray(String prmStr)
	{
		ArrayList<String> rArry = new ArrayList<String>();
		int pairChk = 0;
		int beginIndex =0;
		boolean meetQuote = false;

		for(int i=0; i<prmStr.length(); i++)
		{
			if(prmStr.charAt(i) == '"')
			{
				if(meetQuote) meetQuote = false;
				else meetQuote = true;
			}
			
			if(!meetQuote)
			{
				if(prmStr.charAt(i) == '(')
				{
					pairChk++;
				}
				else if(prmStr.charAt(i) == ')')
				{
					pairChk--;
				}
				
				if(prmStr.charAt(i) == ',' && pairChk == 0)
				{
					rArry.add(prmStr.substring(beginIndex, i).trim());
					beginIndex = i + 1;
				}
				else if(i == prmStr.length() - 1 && pairChk == 0)
				{
					rArry.add(prmStr.substring(beginIndex, i + 1).trim());
					beginIndex = i + 1;
				}
			}
		}
		
		if(rArry.size() > 0)
			return rArry.toArray();
		else
			return null;//exception
	}
	
	private void initKeywords()
	{
		//IF()
		//THEN
		//ELSE
		//ELSEIF()
		//ENDIF
		
		String strPtrn = "";
		String funcPtrn = "";
		Function[] enmFnc = Function.values();
		for(int i=0; i< enmFnc.length; i++)
		{
			strPtrn += enmFnc[i].pattern();
			funcPtrn += enmFnc[i].pattern().replaceAll("\\\\b", "");
			if(i < enmFnc.length - 1)
			{
				strPtrn += "|";
				funcPtrn += "|";
			}
		}

		cndPtrn 		= Pattern.compile("(IF?\\p{javaWhitespace}*\\(|ELSEIF?\\p{javaWhitespace}*\\(|ELSE|ENDIF)");
		cmptTagPtrn	 	= Pattern.compile("(<=|<|>=|==|!=|<>)");
		grpPtrn 		= Pattern.compile("([^("+funcPtrn+")](\\(|!\\()|^(\\(|!\\())");
		thenPtrn 		= Pattern.compile("\\)\\p{javaWhitespace}*THEN");
		relationPtrn	= Pattern.compile("\\|\\||&&");
			
		ifStr 			= "IF?\\p{javaWhitespace}*\\(";
		elseIfStr		= "ELSEIF?\\p{javaWhitespace}*\\(";
		funcStPtrnStr 	= "("+strPtrn+")?\\p{javaWhitespace}*\\(";
		stPtrn 			= Pattern.compile(funcStPtrnStr);
		funcPtrnStr 	= "("+strPtrn+")?\\p{javaWhitespace}*\\(.*\\)";
		dqPtrnStr 		= "\".*\"";
		prmPtrnStr 		= ":[a-zA-Z0-9_]+(\\[:[a-zA-Z0-9_]+\\])?";
	}
	
	private int getCloseIndex(String script)
	{
		int idx = 0;
		int pairChk = 0;
		boolean meetQuote = false;
		for(int i=0; i<script.length(); i++)
		{
			if(script.charAt(i) == '"')
			{
				if(meetQuote) meetQuote = false;
				else meetQuote = true;
			}
			
			if(!meetQuote)
			{
				if(script.charAt(i) == '(')
				{
					pairChk++;
				}
				else if(script.charAt(i) == ')')
				{
					pairChk--;
					if(pairChk == 0)
					{
						idx = i + 1;
						break;
					}
				}
			}
		}
		
		return idx;
	}
}
