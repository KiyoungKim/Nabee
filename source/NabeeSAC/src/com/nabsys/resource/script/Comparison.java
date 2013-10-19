package com.nabsys.resource.script;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Comparison implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean nega = true;
	private ArrayList<Comparison> children = null;
	private Comparison parent = null;
	private Object[] competeArray = null;
	private String cptStr = null;
	private FuncFactory function = null;
	private int LFT = 0; //왼쪽 조건
	private int RHT = 1; //오른쪽 조건
	private int CPT = 2; //비교조건
	
	private int NONE = 0;
	private int AND = 1;
	private int OR = 2;
	private int PREV_RELATION = NONE;
	
	private FunctionExecutor subExecutor = null;
	
	public Comparison(Comparison parent, boolean nega)
	{
		if(parent != null)
		{
			this.parent = parent;
			parent.addChild(this);
		}
		this.nega = nega;
		competeArray = new Object[3];
		children = new ArrayList<Comparison>();
	}
	
	protected void setSubExecutor(FunctionExecutor subExecutor)
	{
		this.subExecutor = subExecutor;
	}
	
	protected boolean hasSubExecutor()
	{
		return this.subExecutor != null;
	}
	
	public Object execute(HashMap<String, Object> map) throws Exception
	{
		if(getResult(map))
		{
			if(subExecutor != null) return subExecutor.execute(map);
			else return function.execute(map);
		}
		else
		{
			return null;
		}
	}
	
	public Object execute(HashMap<String, Object> map, int index) throws Exception
	{
		if(getResult(map))
		{
			if(subExecutor != null) return subExecutor.execute(map, index);
			else return function.execute(map, index);
		}
		else
		{
			return null;
		}
	}
	
	protected void setFuncFactory(FuncFactory function)
	{
		this.function = function;
	}
	
	protected boolean getResult(HashMap<String, Object> map) throws Exception
	{
		boolean rtn = true;
		if(competeArray == null && children == null && cptStr == null) return true;
		
		for(int i=0; i<children.size(); i++)
		{
			if(children.get(i).getPrevRelation() == NONE)
			{
				rtn = children.get(i).getResult(map) && rtn;
			}
			else if(children.get(i).getPrevRelation() == AND)
			{
				rtn = children.get(i).getResult(map) && rtn;	
			}
			else if(children.get(i).getPrevRelation() == OR)
			{
				rtn = children.get(i).getResult(map) || rtn;
			}
		}
		
		if(cptStr != null)
		{
			Object left = competeArray[LFT];
			Object right = competeArray[RHT];

			if(left instanceof FuncFactory) left = ((FuncFactory)left).execute(map);
			else if(left instanceof FuncParam) left = ((FuncParam)left).getValue(map);

			if(right instanceof FuncFactory) right = ((FuncFactory)right).execute(map);
			if(right instanceof FuncParam) right = ((FuncParam)right).getValue(map);
			
			Method compairMethod = null;
			if(((String)cptStr).equals(">"))
			{
				compairMethod = getClass().getMethod("leftOpen", new Class[]{BigDecimal.class, BigDecimal.class});
			}
			else if(((String)cptStr).equals(">="))
			{
				compairMethod = getClass().getMethod("leftOpenEqual", new Class[]{BigDecimal.class, BigDecimal.class});
			}
			else if(((String)cptStr).equals("<"))
			{
				compairMethod = getClass().getMethod("rightOpen", new Class[]{BigDecimal.class, BigDecimal.class});
			}
			else if(((String)cptStr).equals("<="))
			{
				compairMethod = getClass().getMethod("rightOpenEqual", new Class[]{BigDecimal.class, BigDecimal.class});
			}
			else if(((String)cptStr).equals("=="))
			{
				compairMethod = getClass().getMethod("equals", new Class[]{Object.class, Object.class});
			}
			else if(((String)cptStr).equals("!="))
			{
				compairMethod = getClass().getMethod("notEqual", new Class[]{Object.class, Object.class});
			}
			else if(((String)cptStr).equals("<>"))
			{
				compairMethod = getClass().getMethod("notEqual", new Class[]{Object.class, Object.class});
			}

			try{
				rtn = (Boolean)compairMethod.invoke(this, left, right);
			}catch(Exception e){
				if(left == null || right == null)
				{
					if(((String)cptStr).equals("!=") || ((String)cptStr).equals("<>")) rtn = (left != right);
					else if(((String)cptStr).equals("==")) rtn = (left == right);
					else rtn = false;
				}
				else
				{
					if(((String)cptStr).equals("!=") || ((String)cptStr).equals("<>")) rtn = true;
					else rtn = false;
				}
			}
		}
		
		
		return (rtn && nega);
	}
	
	protected Object[] getTmpArray()
	{
		return competeArray;
	}
	
	protected int getPrevRelation()
	{
		return PREV_RELATION;
	}
	
	protected boolean getNega()
	{
		return nega;
	}
	
	protected ArrayList<Comparison> getChildren()
	{
		return this.children;
	}
	
	protected void addChild(Comparison child)
	{
		children.add(child);
	}
	
	protected void setPrevRelation(int relation)
	{
		this.PREV_RELATION = relation;
	}
	
	protected Comparison getParent()
	{
		return this.parent;
	}
	
	public void setComparison(int type, Object value) throws Exception
	{
		if(type == CPT)
		{
			cptStr = ((String)value).replaceAll(" ", "").replace("	", "");
			if(!cptStr.equals(">") && !cptStr.equals(">=") && !cptStr.equals("<") && !cptStr.equals("<=") && !cptStr.equals("<>") && !cptStr.equals("!=") && !cptStr.equals("=="))
			{
				throw new Exception("Script parsing error. '" + cptStr + "' is not a keyword.");
			}
		}
		else
		{
			competeArray[type] = value;
		}
	}
	
	public boolean leftOpen(BigDecimal left, BigDecimal right)
	{
		int result = left.compareTo(right);
		return result == 1;
	}
	
	public boolean leftOpenEqual(BigDecimal left, BigDecimal right)
	{
		int result = left.compareTo(right);
		return result == 1 || result == 0;
	}
	
	public boolean rightOpen(BigDecimal left, BigDecimal right)
	{
		int result = left.compareTo(right);
		return result == -1;
	}
	
	public boolean rightOpenEqual(BigDecimal left, BigDecimal right)
	{
		int result = left.compareTo(right);
		return result == -1 || result == 0;
	}
	
	public boolean equals(Object left, Object right)
	{
		if(left.getClass() == right.getClass())
		{
			if(left instanceof StringBuilder)
			{
				return ((StringBuilder)left).equals((StringBuilder)right);
			}
			else
			{
				int result = ((BigDecimal)left).compareTo((BigDecimal)right);
				return result == 0;
			}
		}
		else
		{
			return false;
		}
	}
	
	public boolean notEqual(Object left, Object right)
	{
		if(left.getClass() == right.getClass())
		{
			if(left instanceof StringBuilder)
			{
				return !((StringBuilder)left).equals((StringBuilder)right);
			}
			else
			{
				int result = ((BigDecimal)left).compareTo((BigDecimal)right);
				return result != 0;
			}
		}
		else
		{
			return true;
		}
	}
}
