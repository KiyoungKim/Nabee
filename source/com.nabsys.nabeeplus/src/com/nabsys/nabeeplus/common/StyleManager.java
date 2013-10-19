package com.nabsys.nabeeplus.common;

import org.eclipse.swt.widgets.Display;

public class StyleManager {
	

	public static StyleChanger styleChanger;
	private static boolean isStart = false;
	
	public StyleManager(Display display)
	{
		if(isStart && styleChanger.isStart()) return;
		styleChanger = new StyleChanger(display);
		isStart = true;
	}
	
	public void start()
	{
		styleChanger.addReference();
		if(styleChanger.isStart()) return;
		styleChanger.start();
	}
}
