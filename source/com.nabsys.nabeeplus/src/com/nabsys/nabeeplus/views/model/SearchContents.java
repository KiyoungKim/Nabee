package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;

public class SearchContents extends QueryStorageList{

	private StyleRange[] styleRange = null; 
	private int matchStart = 0;
	public SearchContents(Model parent, int matchStart, String name, StyleRange[] styleRange, Image image) {
		super(parent, name, image);
		this.styleRange = styleRange;
		this.matchStart = matchStart;
	}
	
	public int getSearchFrom()
	{
		return this.matchStart;
	}
	
	public StyleRange[] getStyleRange()
	{
		return this.styleRange;
	}
}
