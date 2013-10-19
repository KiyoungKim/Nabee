package com.nabsys.nabeeplus.design;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public interface GroupEventListener {
	public void groupMove(Point p);
	public void groupSelect(Rectangle r);
	public void groupMouseDown(DesignObject obj);
	public void groupMouseUp();
	public void relationMode(ImageData cursor);
	public void endRelationMode();
	public void newObjectMode(int type, ImageData cursor);
	public void groupPreDelete();
	public boolean groupDelete();
	public void freeMode();
}
