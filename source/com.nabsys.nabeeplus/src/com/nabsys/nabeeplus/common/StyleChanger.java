package com.nabsys.nabeeplus.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

public class StyleChanger extends Thread{
	private StyledText 				editor 			= null;
	private Display 				display 		= null;
	private Styler					commentStyle	= null;
	private Styler					sqlStyle 		= null;
	private Styler					quoteStyle		= null;
	private Styler					dQuoteStyle		= null;
	private Pattern 				pattern 		= null;
	private static boolean			isStart			= false;
	private boolean 				exit 			= false;
	private static int				referCnt		= 0;
	
	public StyleChanger(Display display)
	{
		this.display = display;
	}
	
	public boolean isStart()
	{
		return isStart;
	}
	
	public void addReference()
	{
		referCnt++;
	}
	
	public void run()
	{
		isStart = true;
		while(!exit)
		{
			try {
				synchronized(this)
				{
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
				
				if(exit) break;
				
				display.syncExec(new Runnable(){
					public void run(){
						
						String string = editor.getText();
						int horizontalIndex = editor.getVerticalBar().getSelection() / editor.getVerticalBar().getIncrement();
						int lineCount = editor.getClientArea().height / (editor.getLineHeight() + editor.getLineSpacing()) + 1;
						
						StyledString styledString = new StyledString();

						Matcher matcher = pattern.matcher(string.toUpperCase());
						Matcher commentCloseMatcher = Pattern.compile("\\*/").matcher(string.toUpperCase());
						Matcher quoteCloseMatcher = Pattern.compile("(')|(\")").matcher(string.toUpperCase());
						
						int start = 0;
						int moved = 0;
						while(matcher.find(start))
						{
							int ms = matcher.start();
							int me = matcher.end();
							
							String subedString = string.substring(ms, ms+2);
							String quoteString = string.substring(ms, ms+1);
							if(subedString.equals("/*"))
							{
								if(commentCloseMatcher.find(ms))
								{
									me = commentCloseMatcher.end();
								}
								else
								{
									me = string.length();
									styledString.append(string.substring(start, ms));
									StyledString tmpString = new StyledString(string.substring(ms, me), commentStyle);
									styledString.append(tmpString);
									break;
								}
							}
							else if(quoteString.equals("'") || quoteString.equals("\""))
							{
								if(quoteCloseMatcher.find(ms+1))
								{
									me = quoteCloseMatcher.end();
								}
							}
							else if(!subedString.equals("--"))
							{
								if(ms >= 1)
								{
									char tmp = string.charAt(ms -1);
									if(tmp != '=' && tmp != '\n' && tmp != '	' && tmp != ' ' && tmp != '(' && tmp != '{' && tmp != '[' && tmp != ')' && tmp != '}' && tmp != ']')
									{
										moved = me - start;
										start = me;
										continue;
									}
								}
							}
							
							String matchString = string.substring(ms, me);

							boolean changed = false;
							char tmp = matchString.charAt(matchString.length() - 1);
							if(tmp == '	' || tmp == ' ' || tmp == '(' || tmp == '{' || tmp == '[' || tmp == ')' || tmp == '}' || tmp == ']')
							{
								me -= 1;
								changed = true;
							}
							
							if(changed)
								matchString = string.substring(ms, me);

							int line = editor.getLineAtOffset(me - 1);
							
							
							if(line == 0 || (horizontalIndex <= line && horizontalIndex + lineCount > line))
							{
								StyledString tmpString = null;
								
								if(Pattern.matches("((?s)/\\*.*)|(\\-\\-.*)", matchString))
									tmpString = new StyledString(matchString, commentStyle);
								else if(Pattern.matches("\'.*", matchString))
									tmpString = new StyledString(matchString, quoteStyle);
								else if(Pattern.matches("\".*", matchString))
									tmpString = new StyledString(matchString, dQuoteStyle);
								else
									tmpString = new StyledString(matchString, sqlStyle);
								
								styledString.append(string.substring(start - moved, ms));
								moved = 0;
								styledString.append(tmpString);
								start = me;
							}
							else if(horizontalIndex + lineCount < line)
							{
								break;
							}
							else
							{
								styledString.append(string.substring(start - moved, me));
								moved = 0;
								start = me;
							}
						}
						editor.setStyleRanges(styledString.getStyleRanges());
					}
				});
				
				if(exit) break;
			}catch(Exception e){
				
			}
		}

		isStart = false;
	}
	
	public synchronized void changeStyle(StyledText editor, Styler commentStyle, Styler sqlStyle, Styler quoteStyle, Styler dQuoteStyle,Pattern pattern)
	{
		this.editor = editor;
		this.commentStyle = commentStyle;
		this.sqlStyle = sqlStyle;
		this.quoteStyle = quoteStyle;
		this.dQuoteStyle = dQuoteStyle;
		this.pattern = pattern;
		
		this.notify();
	}
	
	public synchronized void exit()
	{
		referCnt--;
		if(referCnt > 0) return;
		
		this.exit = true;
		this.notify();
	}
}
