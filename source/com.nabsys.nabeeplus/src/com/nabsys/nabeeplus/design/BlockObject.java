package com.nabsys.nabeeplus.design;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.resource.service.ServiceHandler;

public abstract class BlockObject extends PanelObject implements GroupEventListener, MouseListener, PaintListener, MouseMoveListener{

	private PanelObject			parent				= null;
	private static final int	MIN_WIDTH			= 150;
	private static final int	MIN_HEIGHT			= 120; 
	private boolean				init				= false;
	private Rectangle			rectangle			= null;
	private boolean				startDrag			= false;
	private Point 				clickPoint 			= null;
	private Listener			grapEnterListener	= null;
	private Listener			grapExitListener	= null;
	private Listener			mouseEnterListener	= null;
	private Listener			mouseExitListener	= null;
	private MouseMoveListener	grapMoveListener	= null; 
	private MouseListener		grapMouseListener	= null;
	private PaintListener 		avatarPaintListener = null;
	private Composite 			grap				= null;
	private CLabel				startLabel			= null;
	private Label				endLabel			= null;
	private Composite			avatarFrame			= null;
	private GC 					thisgc 				= null;
	protected int 				handlerID 			= 0;
	
	public BlockObject(PanelObject parent, String srcIcon, String name, Point point) {
		this(parent, srcIcon, name, new Rectangle(point.x, point.y, MIN_WIDTH, MIN_HEIGHT));
	}
	
	public BlockObject(PanelObject parent, String srcIcon, String name, Rectangle rectangle) {
		super(parent, parent.getWindow());
		this.rectangle = rectangle;
		this.parent = parent;
		startLabel = new CLabel(this, SWT.NONE);
		startLabel.setBackground(new Color(getDisplay(), 233,233,233));
		startLabel.setText(name);
		startLabel.setAlignment(SWT.CENTER);
		startLabel.setImage(Activator.getImageDescriptor(srcIcon).createImage(parent.getDisplay()));
		endLabel = new Label(this, SWT.NONE);
		endLabel.setBackground(new Color(getDisplay(), 233,233,233));
		endLabel.setText("END");
		endLabel.setAlignment(SWT.CENTER);
		
		createInitObject();
	} 
	
	public Point getInitPoint()
	{
		return new Point(this.rectangle.x, this.rectangle.y);
	}
	
	
	private void createInitObject()
	{
		Image grapImg = Activator.getImageDescriptor("/icons/group_resize_grap.png").createImage(parent.getDisplay());
		grap = new Composite(this, SWT.NONE);
		grap.setBackgroundImage(grapImg);
		grap.addListener(SWT.MouseEnter, grapEnterListener = new Listener(){
			public void handleEvent(Event event) {
				switch(event.type)
				{
				case SWT.MouseEnter:
					grap.setCursor(new Cursor(getDisplay(), SWT.CURSOR_SIZESE));
					break;
				default:
					break;
				}
			}
		});
		grap.addListener(SWT.MouseExit, grapExitListener = new Listener(){
			public void handleEvent(Event event) {
				switch(event.type)
				{
				case SWT.MouseExit:
					grap.setCursor(new Cursor(getDisplay(), SWT.CURSOR_ARROW));
					break;
				default:
					break;
				}
			}
		});
		grap.addMouseListener(grapMouseListener = new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {
			}
			
			Composite 			shadow 					= null;
			PaintListener 		shadowPaintListener 	= null;
			public void mouseDown(MouseEvent e) {
				shadow = new Composite(parent, SWT.TRANSPARENT|SWT.DOUBLE_BUFFERED);
				shadow.setBounds(getBounds());
				shadow.moveAbove(null);
				
				final int	startWidth	= shadow.getBounds().width;
				final int	startHeight	= shadow.getBounds().height;
				final int	startX		= e.x;
				final int	startY		= e.y;
				int 		tmpMinWidth = MIN_WIDTH;
				int 		tmpMinHeight= MIN_HEIGHT;
				Control[] 	controls 	= getChildren();
				
				for(int i=0; i<controls.length ; i++)
				{
					if(!(controls[i] instanceof DesignObject) || (controls[i] instanceof ConnectorObject)) continue;
					Rectangle childRect = ((Composite)controls[i]).getBounds();
					if(tmpMinWidth  < childRect.x + childRect.width + 2) tmpMinWidth = childRect.x + childRect.width + 2;
					if(tmpMinHeight < childRect.y + childRect.height + 23) tmpMinHeight = childRect.y + childRect.height + 23;
				}
				
				final int	minWidth 	= tmpMinWidth;
				final int	minHeight 	= tmpMinHeight;
				
				shadow.addPaintListener(shadowPaintListener = new PaintListener(){
					public void paintControl(PaintEvent e) {
						Rectangle rect = ((Composite)e.getSource()).getBounds();
						e.gc.setBackground(new Color(e.display, 0,0,0));
						e.gc.setLineStyle(SWT.LINE_DOT);
						e.gc.drawRectangle(0, 0, rect.width - 1, rect.height - 1);
						e.gc.setAlpha(50);
						e.gc.setBackground(new Color(e.display, 21, 16, 90));
						e.gc.fillRectangle(0, 0, rect.width, rect.height);
						e.gc.dispose();
					}
				});
				
				grap.addMouseMoveListener(grapMoveListener = new MouseMoveListener(){
					public void mouseMove(MouseEvent e) {
						int mdfWidth = e.x - startX;
						int mdfHeight = e.y - startY;

						int width = startWidth + mdfWidth;
						int height = startHeight + mdfHeight;
						
						if(width < minWidth)width = minWidth;
						if(height < minHeight)height = minHeight;
						
						if(parent instanceof BlockObject)
						{
							Rectangle rect = parent.getClientBoundary();
							if(shadow.getBounds().x + width >= rect.x + rect.width) width = rect.x + rect.width - shadow.getBounds().x;
							if(shadow.getBounds().y + height >= rect.y + rect.height) height = rect.y + rect.height - shadow.getBounds().y;
						}
						shadow.setBounds(shadow.getBounds().x, shadow.getBounds().y, width, height);
					}
				});
			}

			public void mouseUp(MouseEvent e) {
				setBounds(shadow.getBounds());
				setControlBounds();
				for(int i=0; goingOutList != null && i<goingOutList.size(); i++)goingOutList.get(i).pointDecision();
				for(int i=0; goingOutInsideList != null && i<goingOutInsideList.size(); i++)goingOutInsideList.get(i).pointDecision();
				for(int i=0; comingInList != null && i<comingInList.size(); i++)comingInList.get(i).pointDecision();
				for(int i=0; comingInInsideList != null && i<comingInInsideList.size(); i++)comingInInsideList.get(i).pointDecision();
				
				shadow.removePaintListener(shadowPaintListener);
				grap.removeMouseMoveListener(grapMoveListener);
				shadow.dispose();
				shadow = null;
				getEditorSite().setDirty();
			}
		});
		Region region = new Region();
		region.add(new int[]{10, 0, 11, 0, 11, 11, 0, 11, 0, 10, 10, 0});
		grap.setRegion(region);
		
		addPaintListener(this);
		addListener(SWT.MouseEnter, mouseEnterListener = new Listener(){
			public void handleEvent(Event e) {
				if(STATE != FOCUSED)
				{
					STATE = OVER;
					redraw();
					parent.setEndObjectToLine((DesignObject)e.widget);
				}
			}
		});
		addListener(SWT.MouseExit, mouseExitListener = new Listener(){
			public void handleEvent(Event e) {
				if(STATE != FOCUSED)
				{
					STATE = NONE;
					redraw();
				}
				parent.breakeEndObjectOfLine();
			}
		});

		addMouseMoveListener			(this);
		parent.addGroupEvent			(this);
		startLabel.addMouseListener		(this);
		startLabel.addMouseMoveListener	(this);
		startLabel.addListener			(SWT.MouseEnter, mouseEnterListener);
		startLabel.addListener			(SWT.MouseExit, mouseExitListener);
		endLabel.addMouseListener		(this);
		endLabel.addMouseMoveListener	(this);
		endLabel.addListener			(SWT.MouseEnter, mouseEnterListener);
		endLabel.addListener			(SWT.MouseExit, mouseExitListener);
		
		setBackground(new Color(parent.getDisplay(), 255,255,255));
		moveAbove(null);
	}
	
	public void pack()
	{
		setBounds(rectangle);
	}
	
	public void dispose()
	{
		removePaintListener					(this);
		removeMouseMoveListener				(this);
		parent.removeGroupEvent				(this);
		grap.removeListener					(SWT.MouseEnter, grapEnterListener);
		grap.removeListener					(SWT.MouseExit, grapExitListener);
		removeListener						(SWT.MouseEnter, mouseEnterListener);
		removeListener						(SWT.MouseExit, mouseExitListener);
		grap.removeMouseListener			(grapMouseListener);
		startLabel.removeMouseListener		(this);
		startLabel.removeMouseMoveListener	(this);
		startLabel.removeListener			(SWT.MouseEnter, mouseEnterListener);
		startLabel.removeListener			(SWT.MouseExit, mouseExitListener);
		endLabel.removeMouseListener		(this);
		endLabel.removeMouseMoveListener	(this);
		endLabel.removeListener				(SWT.MouseEnter, mouseEnterListener);
		endLabel.removeListener				(SWT.MouseExit, mouseExitListener);
		Control[] children = getChildren();
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof DesignObject) ((DesignObject)children[i]).dispose();
		}
		super.dispose						();
	}
	
	public void setControlBounds()
	{
		grap.setBounds(getBounds().width - 15, getBounds().height - 15, 11, 11);
		startLabel.setBounds(3, 3, getBounds().width - 6, 16);
		endLabel.setBounds(19, getBounds().height - 17, getBounds().width - 38, 15);
	}
	
	public void paintControl(PaintEvent e) {
		Display display = e.display;
		GC gc = e.gc;
		if(!init)
		{
			if(rectangle.width < MIN_WIDTH) rectangle.width = MIN_WIDTH;
			if(rectangle.height < MIN_HEIGHT) rectangle.height = MIN_HEIGHT;
			setBounds(rectangle);
			init = true;
			rectangle = null;
			setControlBounds();
		}
		
		if(STATE == FOCUSED)
		{
			gc.setBackground(new Color(display, 216,177,101));
			gc.fillRoundRectangle(0, 0, getBounds().width, getBounds().height, 2, 2);
			gc.setBackground(new Color(display, 233,194,119));
			gc.fillRoundRectangle(1, 1, getBounds().width - 2, getBounds().height - 2, 1, 1);
			gc.setBackground(new Color(display, 147, 147, 147));
			gc.fillRectangle(2, 2, getBounds().width -4, getBounds().height - 4);
		}
		else if(STATE == OVER)
		{
			gc.setBackground(new Color(display, 103,160,183));
			gc.fillRoundRectangle(0, 0, getBounds().width, getBounds().height, 2, 2);
			gc.setBackground(new Color(display, 106,181,212));
			gc.fillRoundRectangle(1, 1, getBounds().width - 2, getBounds().height - 2, 1, 1);
			gc.setBackground(new Color(display, 147, 147, 147));
			gc.fillRectangle(2, 2, getBounds().width -4, getBounds().height - 4);
		}
		else if(STATE == TEST)
		{
			gc.setBackground(new Color(display, 229,51,64));
			gc.fillRoundRectangle(0, 0, getBounds().width, getBounds().height, 2, 2);
			gc.setBackground(new Color(display, 235,71,83));
			gc.fillRoundRectangle(1, 1, getBounds().width - 2, getBounds().height - 2, 1, 1);
			gc.setBackground(new Color(display, 147, 147, 147));
			gc.fillRectangle(2, 2, getBounds().width -4, getBounds().height - 4);
		}
		else
		{
			gc.setBackground(new Color(display, 147, 147, 147));
			gc.fillRectangle(1, 1, getBounds().width -2, getBounds().height - 2);
		}
		
		gc.setBackground(new Color(getDisplay(), 233,233,233));
		gc.fillRectangle(2, 2, getBounds().width - 4, 20);
		gc.fillRectangle(2, getBounds().height - 22, getBounds().width - 4, 20);
		gc.setBackground(new Color(display, 255,255,255));
		gc.fillRectangle(2, 23, getBounds().width - 4, getBounds().height - 46);
		
		setClientBoundary(new Rectangle(2, 23, getBounds().width - 4, getBounds().height - 46));
		gc.dispose();
	}

	public void mouseMove(MouseEvent e) {
		MOUSE_STATE = NONE;
		if(startDrag)
		{
			Point p = new Point(e.x - clickPoint.x, e.y - clickPoint.y);
			parent.notifyGroupMove(p);
		}
	}

	public void mouseDoubleClick(MouseEvent e) {
		disposeAvatar();
		startDrag = false;
		getEditorSite().setDirty();
	}
	
	public Rectangle getLinkPointRectangle(ConnectorObject connector)
	{
		Rectangle bounds = getBounds(); 
		if(rectangle != null) bounds = rectangle;
		if(connector.getParent() == this)
		{
			if(connector.getStartObject() == this)
			{
				return new Rectangle(3, 3, bounds.width - 6, 20);
			}
			else
			{
				return new Rectangle(3, bounds.height - 23, bounds.width - 6, 20);
			}
		}
		else
		{
			return bounds;
		}
	}
	
	public Rectangle getLinkPointRectangle(LineConnectorObject line)
	{
		Rectangle bounds = getBounds(); 
		if(rectangle != null) bounds = rectangle;
		if(line.getParent() == this)
		{
			if(line.getStartObject() == this)
			{
				return new Rectangle(3, 3, bounds.width - 6, 20);
			}
			else
			{
				return new Rectangle(3, bounds.height - 23, bounds.width - 6, 20);
			}
		}
		else
		{
			return bounds;
		}
	}

	public void mouseDown(MouseEvent e) {
		if((new Rectangle(3, 24, getBounds().width - 6, getBounds().height - 48)).contains(new Point(e.x, e.y)))
		{
			super.mouseDown(e);
			return;
		}
		
		if(MODE == NONE)
		{
			parent.notifyMouseDown(this);
			startDrag = true;
			clickPoint = new Point(e.x, e.y);
			MOUSE_STATE = MOUSE_DOWN;
		}
		else if(MODE == Mode.RELATION_MODE)
		{
			//START
			if((e.getSource() == this && new Rectangle(3, 3, getBounds().width - 6, 20).contains(new Point(e.x, e.y))) || e.getSource() == startLabel) 
			{
				if(goingOutInsideList == null) goingOutInsideList = new ArrayList<ConnectorObject>();
				ConnectorObject goingOutInside = null;
				goingOutInsideList.add(goingOutInside = new ConnectorObject(this));
				goingOutInside.setEntities(this);
				genRelationLine(this);
			}
			//END
			else if((e.getSource() == this && new Rectangle(3, getBounds().height - 23, getBounds().width - 6, 20).contains(new Point(e.x, e.y))) || e.getSource() == endLabel)
			{
				if(goingOutList == null) goingOutList = new ArrayList<ConnectorObject>();
				ConnectorObject goingOut = null;
				goingOutList.add(goingOut = new ConnectorObject(parent));
				goingOut.setEntities(this);
				parent.genRelationLine(this);
			}
		}
		else if(MODE == Mode.END_RELATION_MODE)
		{
			//START
			if((e.getSource() == this && new Rectangle(3, 3, getBounds().width - 6, 20).contains(new Point(e.x, e.y))) || e.getSource() == startLabel) 
			{
				if(parent.getCurrentConnect() == null) return;
				if(parent.getCurrentConnect().getStartObject() instanceof ServiceInfoObject)
				{
					String type = ((ServiceInfoObject)parent.getCurrentConnect().getStartObject()).getType();
					if(!type.equals("General")) return;
				}
				if(parent.getCurrentConnect().getStartObject() == this) return;
				for(int i=0; comingInList != null && i<comingInList.size(); i++)
				{
					if(parent.getCurrentConnect().getStartObject() == comingInList.get(i).getStartObject()) return;
				}
				
				
				if(comingInList == null) comingInList = new ArrayList<ConnectorObject>();
				comingInList.add(parent.getCurrentConnect());
				parent.getCurrentConnect().setEntities(this);
				parent.setCurrentConnect(null);
				getEditorSite().setDirty();
				disposeRelationLine();
			}
			//END
			else if((e.getSource() == this && new Rectangle(3, getBounds().height - 23, getBounds().width - 6, 20).contains(new Point(e.x, e.y))) || e.getSource() == endLabel )
			{
				if(getCurrentConnect() == null) return;
				if(getCurrentConnect().getStartObject() == this) return;
				if(comingInInsideList == null) comingInInsideList = new ArrayList<ConnectorObject>();
				comingInInsideList.add(getCurrentConnect());
				getCurrentConnect().setEntities(this);
				setCurrentConnect(null);
				getEditorSite().setDirty();
				parent.disposeRelationLine();
			}
		}
	}

	public void mouseUp(MouseEvent e) {
		startDrag = false;
		parent.notifyGroupMouseUp();
		
		//click
		if(MOUSE_STATE == MOUSE_DOWN)
		{
		}
		
		MOUSE_STATE = NONE;
		super.mouseUp(e);
	}

	public void groupMove(Point p)
	{
		if(STATE == FOCUSED)
		{
			if(avatarFrame == null) initAvatarFrame();
			
			Rectangle moveBoundary = parent.getClientBoundary();
			
			int x = getBounds().x + p.x;
			int y = getBounds().y + p.y;
			
			if(x <= moveBoundary.x) x = moveBoundary.x;
			if(y <= moveBoundary.y) y = moveBoundary.y;
			if(x + avatarFrame.getBounds().width >= moveBoundary.x + moveBoundary.width) x = moveBoundary.x + moveBoundary.width - avatarFrame.getBounds().width;
			if(y + avatarFrame.getBounds().height >= moveBoundary.y + moveBoundary.height) y = moveBoundary.y + moveBoundary.height - avatarFrame.getBounds().height;
				
			int width = avatarFrame.getBounds().width;
			int height = avatarFrame.getBounds().height;
			avatarFrame.setBounds(x, y, width, height);
			getEditorSite().setDirty();
		}
	}

	public void groupSelect(Rectangle r) {

		Rectangle rect = getBounds();

		if(rect.x < r.x) {setGroupSelected(false);focusLost();return;}
		if(rect.x + rect.width > r.x + r.width) {setGroupSelected(false);focusLost();return;}
		if(rect.y < r.y) {setGroupSelected(false);focusLost();return;}
		if(rect.y + rect.height > r.y + r.height) {setGroupSelected(false);focusLost();return;}
		setGroupSelected(true);
		focusGained();
	}

	public void groupMouseDown(DesignObject obj) {
		if(avatarFrame != null) disposeAvatar();
		if(obj == null)
		{
			setGroupSelected(false);
			focusLost();
		}
		else
		{
			if(!(STATE == FOCUSED && isGroupSelected() && obj.isGroupSelected()))
			{
				if(obj == this)
				{
					this.moveAbove(null);
					focusGained();
				}
				else
				{
					focusLost();
				}
			}
			
			if(STATE == FOCUSED)
			{
				initAvatarFrame();
			}
		}
		
		Control[] children = getChildren();
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof BlockObject)
			{
				((BlockObject)children[i]).groupMouseDown(obj);
			}
			else if(children[i] instanceof IconObject)
			{
				((IconObject)children[i]).groupMouseDown(obj);
			}
		}
	}
	
	private void initAvatarFrame()
	{
		thisgc = new GC(this);
		avatarFrame = new Composite(parent, SWT.TRANSPARENT | SWT.DOUBLE_BUFFERED);
		final Image image = new Image(getDisplay(), getBounds().width, getBounds().height);
		avatarFrame.addPaintListener(avatarPaintListener = new PaintListener(){
			public void paintControl(PaintEvent e) {
				thisgc.copyArea(image, 0, 0);
				e.gc.setAlpha(150);
				e.gc.drawImage(image, 0, 0);
				e.gc.dispose();
			}
		});
		avatarFrame.setBounds(getBounds());
		avatarFrame.moveAbove(null);
	}

	public void relationMode(ImageData cursor) {
		MODE = Mode.RELATION_MODE;
		setCursor(new Cursor(getDisplay(), cursor, 0, 0));
		Control[] children = getChildren();
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof BlockObject)
			{
				((BlockObject)children[i]).relationMode(cursor);
			}
			else if(children[i] instanceof IconObject)
			{
				((IconObject)children[i]).relationMode(cursor);
			}
		}
	}
	
	public void endRelationMode()
	{
		MODE = Mode.END_RELATION_MODE;
		Control[] children = getChildren();
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof BlockObject)
			{
				((BlockObject)children[i]).endRelationMode();
			}
			else if(children[i] instanceof IconObject)
			{
				((IconObject)children[i]).endRelationMode();
			}
		}
	}

	public void freeMode() {
		MODE = Mode.NONE;
		setCursor(new Cursor(getDisplay(), SWT.CURSOR_ARROW));
		Control[] children = getChildren();
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof BlockObject)
			{
				((BlockObject)children[i]).freeMode();
			}
			else if(children[i] instanceof IconObject)
			{
				((IconObject)children[i]).freeMode();
			}
		}
		
		parent.disposeRelationLine();
		disposeRelationLine();
	}

	@Override
	public void focusGained() {
		STATE = FOCUSED;
		redraw();
	}

	@Override
	public void focusLost() {
		STATE = NONE;
		redraw();
	}

	public void newObjectMode(int type, ImageData cursor) {
		MODE = type;
		setCursor(new Cursor(getDisplay(), cursor, 0, 0));
		Control[] children = getChildren();
		for(int i=0; i<children.length; i++)
		{
			if(children[i] instanceof BlockObject)
			{
				((BlockObject)children[i]).newObjectMode(type, cursor);
			}
			else if(children[i] instanceof IconObject)
			{
				((IconObject)children[i]).newObjectMode(type, cursor);
			}
		}
	}
	
	public void removeConnector(ConnectorObject obj)
	{
		if(goingOutList != null && goingOutList.contains(obj))
			goingOutList.remove(obj);
		else if(comingInList != null && comingInList.contains(obj))
			comingInList.remove(obj);
		else if(goingOutInsideList != null && goingOutInsideList.contains(obj))
			goingOutInsideList.remove(obj);
		else if(comingInInsideList != null && comingInInsideList.contains(obj))
			comingInInsideList.remove(obj);
		
		getEditorSite().setDirty();
	}
	
	private void disposeAvatar()
	{
		if(avatarFrame == null) return;
		if(thisgc != null)
		{
			thisgc.dispose();
			thisgc = null;
		}
		if(avatarPaintListener != null)
		{
			avatarFrame.removePaintListener(avatarPaintListener);
			avatarPaintListener = null;
		}
		avatarFrame.dispose();
		avatarFrame = null;
	}
	
	public void groupMouseUp() {
		if(avatarFrame != null)
		{
			setBounds(avatarFrame.getBounds());
			disposeAvatar();
			
			if(goingOutList != null && goingOutList.size() > 0)
			{
				for(int i=0; i<goingOutList.size(); i++)
				{
					((ConnectorObject)goingOutList.get(i)).pointDecision();
				}
			}
			if(comingInList != null && comingInList.size() > 0)
			{
				for(int i=0; i<comingInList.size(); i++)
				{
					((ConnectorObject)comingInList.get(i)).pointDecision();
				}
			}
		}
	}

	public boolean groupDelete() {
		if(STATE == FOCUSED)
		{
			getEditorSite().setDirty();
			dispose();
			return true;
		}
		
		return false;
	}
	
	private void childrenDelete(BlockObject obj)
	{
		Control[] children = obj.getChildren();
		int length = children.length;
		for(int i=0; i<length; i++)
		{
			if(children[i] instanceof BlockObject)
			{
				childrenDelete((BlockObject)children[i]);
				children[i].dispose();
			}
			else if(children[i] instanceof IconObject|| children[i] instanceof ConnectorObject)
			{
				children[i].dispose();
			}
		}
	}

	public void groupPreDelete() {
		if(STATE == FOCUSED)
		{
			Control[] children = getChildren();
			int length = children.length;
			for(int i=0; i<length; i++)
			{
				if(children[i] instanceof BlockObject)
				{
					childrenDelete((BlockObject)children[i]);
					children[i].dispose();
				}
				else if(children[i] instanceof IconObject || children[i] instanceof ConnectorObject)
				{
					children[i].dispose();
				}
			}
			
			if(goingOutList != null)
			{
				for(int i=0; i<goingOutList.size(); i++)
				{
					goingOutList.get(i).focusGained();
				}
			}
			if(comingInList != null)
			{
				for(int i=0; i<comingInList.size(); i++)
				{
					comingInList.get(i).focusGained();
				}
			}
		}
		else if(getChildren().length > 0)
		{
			notifyDelete();
		}
	}
	
	public void setInitStatus()
	{
		this.STATE = NONE;
		redraw();
	}
	
	public void setTestStatus(int handlerID)
	{
		if(this.handlerID == handlerID)
		{
			if(this.STATE == TEST)
			{
				this.STATE = NONE;
				redraw();
				this.STATE = TEST;
				redraw();
			}
			else
			{
				this.STATE = TEST;
				redraw();
			}
		}
	}
	
	public Image getIconImage()
	{
		return startLabel.getImage();
	}

	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		
	}

	public abstract ServiceHandler getHandler(ServiceHandler parent);
	public abstract void initHandler();
	
}
