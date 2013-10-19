package com.nabsys.nabeeplus.design;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.nabsys.nabeeplus.Activator;
import com.nabsys.resource.service.ServiceHandler;

public abstract class IconObject extends DesignObject implements GroupEventListener, Listener, MouseListener, PaintListener{
	
	private PanelObject			parent				= null;
	private Composite 			icon				= null;
	private Image 				iconImg				= null;
	private PaintListener		iconPaintListener	= null;
	private MouseMoveListener	iconMouseMoveListener = null;
	private PaintListener 		avatarPaintListener = null;
	private FocusListener		txtFocusListener	= null;
	private CLabel 				text				= null;
	private boolean 			init 				= false;
	private boolean				startDrag			= false;
	private int					iconAreaWidth		= 30;
	private int					iconAreaHeight		= 30;
	private Rectangle			rectangle			= null;
	private Composite			avatarFrame			= null;
	private GC 					thisgc 				= null;
	protected int 				handlerID 			= 0;
	
	public IconObject(PanelObject parent, String srcIcon, String name, Point point) {
		this(parent, srcIcon, name, new Rectangle(point.x, point.y, 50, 50));
	}
	
	public IconObject(final PanelObject parent, String srcIcon, String name, Rectangle rectangle) {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.parent = parent;
		this.rectangle = rectangle;
		
		GridLayout iconLayout 		= new GridLayout();
		iconLayout.marginWidth 		= 0;
		iconLayout.marginHeight 	= 0;
		iconLayout.numColumns 		= 1;
		iconLayout.verticalSpacing 	= 5;
		iconLayout.marginTop		= 2;
		iconLayout.marginBottom		= 2;
		iconLayout.marginLeft		= 2;
		iconLayout.marginRight		= 2;
		this.setLayout(iconLayout);
		
		
		iconImg = Activator.getImageDescriptor(srcIcon).createImage(parent.getDisplay());
		
		GridData layoutData 		= new GridData(GridData.CENTER, GridData.CENTER, false, false);
		layoutData.heightHint 		= iconAreaHeight;
		layoutData.widthHint 		= iconAreaWidth;
		icon 						= new Composite(this, SWT.DOUBLE_BUFFERED);
		icon.setLayoutData			(layoutData);
		icon.setBackground			(new Color(getDisplay(), 233,233,233));
		icon.addPaintListener(iconPaintListener = new PaintListener(){
			public void paintControl(PaintEvent e) {
				int iconImgWidth 	= iconImg.getBounds().width;
				int iconImgHeight 	= iconImg.getBounds().height;
				int iconImgX 		= icon.getBounds().width / 2 - (iconImgWidth / 2);
				int iconImgY 		= (iconAreaHeight / 2) - (iconImgHeight / 2);
				e.gc.drawImage		(iconImg, iconImgX, iconImgY); 
				e.gc.dispose();
			}
		});
		
		layoutData 					= new GridData(GridData.FILL, GridData.FILL, true, false);
		text 						= new CLabel(this, SWT.NONE);
		text.setText				(name);
		text.setBackground			(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		text.setAlignment			(SWT.CENTER);
		text.setLayoutData			(layoutData);
		text.setFont(new Font(parent.getDisplay(), "Arial", 9, SWT.NONE));

		text.addFocusListener(txtFocusListener = new FocusListener(){
			public void focusGained(FocusEvent e) {
				Control[] controls = parent.getChildren();
				if(controls.length > 2)
				{
					((Composite)e.getSource()).getParent().moveAbove(null);
				}
				
				STATE = FOCUSED;
				redraw();
			}
			public void focusLost(FocusEvent e) {
				STATE = NONE;
				redraw();
			}
		});
		addPaintListener			(this);
		text.addMouseListener		(this);
		parent.addGroupEvent		(this);
		icon.addMouseListener		(this);
		icon.addListener			(SWT.MouseEnter, this);
		icon.addListener			(SWT.MouseExit, this);
		
		moveAbove(null);
	}
	
	public void pack()
	{
		setBounds(rectangle);
		super.pack();
	}

	public Rectangle getLinkPointRectangle(ConnectorObject connector)
	{
		Rectangle iconRect = icon.getBounds();
		Rectangle backRect = getBounds();
		if(rectangle != null) backRect = rectangle;
		return new Rectangle(backRect.x + iconRect.x - 1, backRect.y + iconRect.y - 1, iconAreaWidth + 2, iconAreaHeight + 2);
	}
	
	public Rectangle getLinkPointRectangle(LineConnectorObject line)
	{
		Rectangle iconRect = icon.getBounds();
		Rectangle backRect = getBounds();
		if(rectangle != null) backRect = rectangle;
		return new Rectangle(backRect.x + iconRect.x - 1, backRect.y + iconRect.y - 1, iconAreaWidth + 2, iconAreaHeight + 2);
	}
	
	public void paintControl(PaintEvent e) {
		Display display = e.display;
		GC gc = e.gc;
		if(!init)
		{
			Rectangle txtRect = text.getBounds();
			Region region = new Region();
			if(txtRect.width < iconAreaWidth)
			{
				setBounds(rectangle.x, rectangle.y, iconAreaWidth + 4, iconAreaHeight + txtRect.height + 8 + 4);
				text.setBounds(1, txtRect.y, iconAreaWidth, txtRect.height);
			}
			else
			{
				setBounds(rectangle.x, rectangle.y, txtRect.width + 4, iconAreaHeight + txtRect.height + 8 + 4);
			}
			region.add(text.getBounds().x - 1, text.getBounds().y - 1, text.getBounds().width + 2, text.getBounds().height + 2);
			region.add(icon.getBounds().x - 2, icon.getBounds().y - 2, icon.getBounds().width + 4, icon.getBounds().height + 4);
			setRegion(region);
			rectangle = null;
			init=true;
		}

		if(STATE == FOCUSED)
		{
			Rectangle rect = text.getBounds();
			int newX = 0;
			if(rect.x == 0) newX = 0;
			else newX = rect.x - 1;
			Rectangle newRect = new Rectangle(newX, rect.y -1, rect.width + 2, rect.height + 2);
			gc.setBackground(new Color(display, 236, 236, 236));
			gc.fillRectangle(newRect);
			
			rect = icon.getBounds();
			gc.setBackground(new Color(display, 216,177,101));
			gc.fillRoundRectangle(rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4, 2, 2);
			gc.setBackground(new Color(display, 233,194,119));
			gc.fillRoundRectangle(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2, 1, 1);
		}
		else if(STATE == OVER)
		{
			Rectangle rect = text.getBounds();
			int newX = 0;
			if(rect.x == 0) newX = 0;
			else newX = rect.x - 1;
			Rectangle newRect = new Rectangle(newX, rect.y -1, rect.width + 2, rect.height + 2);
			gc.setBackground(new Color(display, 236, 236, 236));
			gc.fillRectangle(newRect);
			
			rect = icon.getBounds();
			gc.setBackground(new Color(display, 103,160,183));
			gc.fillRoundRectangle(rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4, 2, 2);
			gc.setBackground(new Color(display, 106,181,212));
			gc.fillRoundRectangle(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2, 1, 1);
		}
		else if(STATE == TEST)
		{
			Rectangle rect = text.getBounds();
			int newX = 0;
			if(rect.x == 0) newX = 0;
			else newX = rect.x - 1;
			Rectangle newRect = new Rectangle(newX, rect.y -1, rect.width + 2, rect.height + 2);
			gc.setBackground(new Color(display, 236, 236, 236));
			gc.fillRectangle(newRect);
			
			rect = icon.getBounds();
			gc.setBackground(new Color(display, 229,51,64));
			gc.fillRoundRectangle(rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4, 2, 2);
			gc.setBackground(new Color(display, 235,71,83));
			gc.fillRoundRectangle(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2, 1, 1);
		}
		else
		{
			Rectangle rect = text.getBounds();
			int newX = 0;
			if(rect.x == 0) newX = 0;
			else newX = rect.x - 1;
			Rectangle newRect = new Rectangle(newX, rect.y -1, rect.width + 2, rect.height + 2);
			gc.setBackground(new Color(display, 236, 236, 236));
			gc.fillRectangle(newRect);
			
			rect = icon.getBounds();
			gc.setBackground(new Color(display, 255,255,255));
			gc.fillRoundRectangle(rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4, 2, 2);
			gc.setBackground(new Color(display, 147, 147, 147));
			newRect = new Rectangle(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2);
			gc.fillRectangle(newRect);
		}
		
		gc.dispose();
	}
	
	public void dispose()
	{
		removePaintListener				(this);
		text.removeMouseListener		(this);
		parent.removeGroupEvent			(this);
		icon.removeListener				(SWT.MouseEnter, this);
		icon.removeListener				(SWT.MouseExit, this);
		icon.removeMouseListener		(this);
		icon.removePaintListener		(iconPaintListener);
		text.removeFocusListener		(txtFocusListener);
		super.dispose();
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

	public void mouseDoubleClick(MouseEvent e) {
		disposeAvatar();
		startDrag = false;
		if(iconMouseMoveListener != null) icon.removeMouseMoveListener(iconMouseMoveListener);
		getEditorSite().setDirty();
	}

	public void mouseDown(MouseEvent e) {
		if(MODE == NONE)
		{
			parent.notifyMouseDown(this);
			startDrag = true;
			MOUSE_STATE = MOUSE_DOWN;
			final Point clickPoint = new Point(e.x, e.y);
			icon.addMouseMoveListener(iconMouseMoveListener = new MouseMoveListener(){
				public void mouseMove(MouseEvent e) {
					MOUSE_STATE = NONE;
					if(startDrag)
					{
						Point p = new Point(e.x - clickPoint.x, e.y - clickPoint.y);
						parent.notifyGroupMove(p);
					}
				}
			});
		}
		else if(MODE == Mode.RELATION_MODE)
		{
			if(goingOutList == null) goingOutList = new ArrayList<ConnectorObject>();
			ConnectorObject goingOut = null;
			goingOutList.add(goingOut = new ConnectorObject(parent));
			goingOut.setEntities(this);
			
			parent.genRelationLine(this);
		}
		else if(MODE == Mode.END_RELATION_MODE)
		{
			if(parent.getCurrentConnect() == null) return;
			if(parent.getCurrentConnect().getStartObject() instanceof ServiceInfoObject)
			{
				String type = ((ServiceInfoObject)parent.getCurrentConnect().getStartObject()).getType();
				if(type.equals("General"))
				{
					if(this instanceof InboundNetworkObject ||
							this instanceof BatchObject ||
							this instanceof MessageQueueObject) return;
				}
				else if(type.equals("Online"))
				{
					if(!(this instanceof InboundNetworkObject)) return;
				}
				else if(type.equals("Batch"))
				{
					if(!(this instanceof BatchObject)) return;
				}
				else if(type.equals("MessageQueue"))
				{
					if(!(this instanceof MessageQueueObject)) return;
				}
			}
			if(this instanceof GateObject && !(parent.getCurrentConnect().getStartObject() instanceof ServiceInfoObject)) return;
			if(parent.getCurrentConnect().getStartObject() == this) return;
			for(int i=0; goingOutList != null && i<goingOutList.size(); i++)
			{
				if(parent.getCurrentConnect().getStartObject() == goingOutList.get(i).getEndObject()) return;
			}
			for(int i=0; comingInList != null && i<comingInList.size(); i++)
			{
				if(parent.getCurrentConnect().getStartObject() == comingInList.get(i).getStartObject()) return;
			}
			
			
			if(comingInList == null) comingInList = new ArrayList<ConnectorObject>();
			comingInList.add(parent.getCurrentConnect());
			parent.getCurrentConnect().setEntities(this);
			parent.setCurrentConnect(null);
			
			parent.disposeRelationLine();
			
			getEditorSite().setDirty();
		}
	}
	
	public boolean hasGoingOut()
	{
		return goingOutList.size() > 0;
	}
	
	public void removeConnector(ConnectorObject obj)
	{
		if(goingOutList != null && goingOutList.contains(obj))
			goingOutList.remove(obj);
		else if(comingInList != null && comingInList.contains(obj))
			comingInList.remove(obj);
		
		getEditorSite().setDirty();
	}

	public void mouseUp(MouseEvent e) {
		startDrag = false;
		if(iconMouseMoveListener != null) icon.removeMouseMoveListener(iconMouseMoveListener);
		parent.notifyGroupMouseUp();
		//click
		if(MOUSE_STATE == MOUSE_DOWN)
		{
		}
		
		MOUSE_STATE = NONE;
	}

/*	public void modifyText(ModifyEvent e) {
		int curWidth 		= getBounds().width;
		
		//TODO 이 값이 관건
		Rectangle clientArea = text.getClientArea();
		int textWidth 		= clientArea.width;

		int newAddWidth 	= textWidth + 2 - getSize().x;
		int diffHalf 		= 0;
		int newXpoint 		= 0;
		int newWidth 		= 0;
		
		if(iconAreaWidth + 4 >= curWidth + newAddWidth)
		{
			newAddWidth 	= (iconAreaWidth + 4) - getSize().x;
			diffHalf 		= newAddWidth - (newAddWidth / 2);
			newXpoint 		= this.getBounds().x - (newAddWidth - (newAddWidth / 2));
			newWidth 		= iconAreaWidth + 4;
		}
		else
		{
			diffHalf 		= newAddWidth - (newAddWidth / 2);
			newXpoint 		= this.getBounds().x - diffHalf;
			newWidth 		= curWidth + newAddWidth;
		}

		if(newAddWidth != 0)
		{
			setBounds(newXpoint, this.getBounds().y, newWidth, this.getBounds().height);
			layout(true);
			Region region = new Region();
			region.add(text.getBounds().x - 1, text.getBounds().y - 1, text.getBounds().width + 2, text.getBounds().height + 2);
			region.add(icon.getBounds().x - 2, icon.getBounds().y - 2, icon.getBounds().width + 4, icon.getBounds().height + 4);
			setRegion(region);
		}
		
	}*/

	public void handleEvent(Event event) {
		switch(event.type)
		{
		case SWT.MouseEnter:
			if(STATE != FOCUSED)
			{
				STATE = OVER;
				redraw();
				parent.setEndObjectToLine(this);
			}
			break;
		case SWT.MouseExit:
			if(STATE != FOCUSED)
			{
				STATE = NONE;
				redraw();
			}
			parent.breakeEndObjectOfLine();
			break;
		}
	}

	public void groupSelect(Rectangle r) {
		Rectangle newRect = new Rectangle(getBounds().x + icon.getBounds().x, 
										getBounds().y + icon.getBounds().y, 
										icon.getBounds().width, 
										icon.getBounds().height);

		if(newRect.x < r.x) {setGroupSelected(false);focusLost();return;}
		if(newRect.x + newRect.width > r.x + r.width) {setGroupSelected(false);focusLost();return;}
		if(newRect.y < r.y) {setGroupSelected(false);focusLost();return;}
		if(newRect.y + newRect.height > r.y + r.height) {setGroupSelected(false);focusLost();return;}
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
		avatarFrame.setRegion(getRegion());
		avatarFrame.setBounds(getBounds());
		avatarFrame.moveAbove(null);
	}

/*	public void verifyText(VerifyEvent e) {
		//e.doit = e.text.matches("(\\w+)");
	}*/

	public void relationMode(ImageData cursor) {
		MODE = Mode.RELATION_MODE;
	}
	
	public void endRelationMode()
	{
		MODE = Mode.END_RELATION_MODE;
	}

	public void freeMode() {
		MODE = Mode.NONE;
		parent.disposeRelationLine();
	}

	public void newObjectMode(int type, ImageData cursor) {
		MODE =	type;
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

	public void groupPreDelete() {
		if(STATE == FOCUSED)
		{
			if(goingOutList != null)
			{
				int length = goingOutList.size();
				for(int i=0; i<length; i++)
				{
					goingOutList.get(i).focusGained();
				}
			}
			if(comingInList != null)
			{
				int length = comingInList.size();
				for(int i=0; i<length; i++)
				{
					comingInList.get(i).focusGained();
				}
			}
		}
	}
	
	public Image getIconImage()
	{
		return iconImg;
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
	
	public abstract ServiceHandler getHandler(ServiceHandler parent);
	public abstract void initHandler();
}
