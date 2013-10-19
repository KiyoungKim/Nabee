package com.nabsys.nabeeplus.views.model;

import org.eclipse.swt.graphics.Image;

public class Instance extends Model{
	
	private boolean isRunning = false;
	Image runImage = null;
	Image terminateImage = null;
	
	public Instance()
	{
		super();
	}

	public Instance(Model parent, String name, Image runImage, Image terminateImage) {
		super(parent, name, terminateImage);
		this.runImage = runImage;
		this.terminateImage = terminateImage;
	}
	
	private boolean hasAuthority = true;
	public void setAuthority(boolean hasAuthority)
	{
		this.hasAuthority = hasAuthority;
	}
	
	public boolean hasAuthority()
	{
		return this.hasAuthority;
	}
	
	public void setRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
		
		if(isRunning)
			super.image = runImage;
		else
			super.image = terminateImage;
	}
	
	public boolean isRunning()
	{
		return this.isRunning;
	}
}
