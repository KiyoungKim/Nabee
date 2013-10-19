package com.nabsys.net.socket.channel;

import java.nio.BufferUnderflowException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import com.nabsys.net.exception.KeyDuplicateException;
import com.nabsys.net.socket.channel.handler.ChannelHandler;

public class HandlerChain {
	
	private final HashMap<String, ChannelHandler> handlerList = new HashMap<String, ChannelHandler>();
	private ChannelHandler firstHandler = null;
	private ChannelHandler lastHandler = null;
	private ChannelCore core = null;
	
	public synchronized void addFirst(String key, ChannelHandler handler) throws KeyDuplicateException
	{
		handler.setName(key);
		
		if(handlerList.isEmpty())
		{
			handlerList.put(key, handler);
			firstHandler = handler;
			lastHandler = handler;
		}
		else
		{
			if(handlerList.containsKey(key))
			{
				throw new KeyDuplicateException("Element duplicate : " + key);
			}
			
			handlerList.put(key, handler);
			
			firstHandler.setPrev(handler);
			handler.setNext(firstHandler);
			
			firstHandler = handler;
		}
	}
	
	public synchronized void addLast(String key, ChannelHandler handler) throws KeyDuplicateException
	{
		handler.setName(key);
		
		if(handlerList.isEmpty())
		{
			handlerList.put(key, handler);
			firstHandler = handler;
			lastHandler = handler;
		}
		else
		{
			if(handlerList.containsKey(key))
			{
				throw new KeyDuplicateException("Element duplicate : " + key);
			}
			
			handlerList.put(key, handler);
			
			lastHandler.setNext(handler);
			handler.setPrev(lastHandler);
			
			lastHandler = handler;
		}
	}
	
	public synchronized void addBefore(String baseKey, String key, ChannelHandler handler) throws KeyDuplicateException
	{
		if(!handlerList.containsKey(baseKey))
		{
			throw new NoSuchElementException(baseKey);
		}
		
		ChannelHandler baseHandler = handlerList.get(baseKey);
		
		if(baseHandler == firstHandler)
		{
			addFirst(key, handler);
		}
		else
		{
			if(handlerList.containsKey(key))
			{
				throw new KeyDuplicateException("Element duplicate : " + key);
			}
			
			handlerList.put(key, handler);
			
			baseHandler.getPrev().setNext(handler);
			handler.setPrev(baseHandler.getPrev());
			baseHandler.setPrev(handler);
			handler.setNext(baseHandler);
			
			handler.setName(key);
			
		}
	}
	
	public synchronized void addNext(String baseKey, String key, ChannelHandler handler) throws KeyDuplicateException
	{
		if(!handlerList.containsKey(baseKey))
		{
			throw new NoSuchElementException(baseKey);
		}
		
		ChannelHandler baseHandler = handlerList.get(baseKey);
		
		if(baseHandler == lastHandler)
		{
			addLast(key, handler);
		}
		else
		{
			if(handlerList.containsKey(key))
			{
				throw new KeyDuplicateException("Element duplicate : " + key);
			}
			
			handlerList.put(key, handler);
			
			baseHandler.getNext().setPrev(handler);
			handler.setNext(baseHandler.getNext());
			handler.setPrev(baseHandler);
			baseHandler.setNext(handler);
			
			handler.setName(key);
			
		}
	}
	
	public synchronized void removeFirst()
	{
		if (handlerList.isEmpty()) {
            throw new NoSuchElementException();
        }
		
		if(firstHandler.getNext() == null)
		{
			firstHandler = lastHandler = null;
			handlerList.clear();
		}
		else
		{
			ChannelHandler newFirstHandler = firstHandler.getNext();
			
			handlerList.remove(firstHandler.getName());
			newFirstHandler.setPrev(null);
			firstHandler = newFirstHandler;
		}
	}
	
	public synchronized void removeLast()
	{
		if (handlerList.isEmpty()) {
            throw new NoSuchElementException();
        }
		
		if(lastHandler.getPrev() == null)
		{
			firstHandler = lastHandler = null;
			handlerList.clear();
		}
		else
		{
			ChannelHandler newLastHandler = lastHandler.getPrev();
			
			handlerList.remove(lastHandler.getName());
			newLastHandler.setNext(null);
			lastHandler = newLastHandler;
		}
	}
	
	public synchronized void remove(ChannelHandler handler)
	{
		if (firstHandler == lastHandler) {
			firstHandler = lastHandler = null;
			handlerList.clear();
        } else if (handler == firstHandler) {
            removeFirst();
        } else if (handler == lastHandler) {
            removeLast();
        } else {
        	handlerList.remove(handler);
        	
        	handler.getPrev().setNext(handler.getNext());
        	handler.getNext().setPrev(handler.getPrev());
        }
	}
	
	public synchronized void remove(String key) {
		if(!handlerList.containsKey(key))
		{
			throw new NoSuchElementException();
		}
		
		remove(handlerList.get(key));
    }
	
	public synchronized boolean isHandlerExist(String key)
	{
		return handlerList.containsKey(key);
	}
	
	public synchronized ChannelHandler getHandler(String key)
	{
		return handlerList.get(key);
	}
	
	public void sendNextDownstream(ChannelHandler handler, IEvent event)
	{
		try{
			handler.getNext().sendDownstream(event);
		}catch(Exception ex){
			lastHandler.sendUpstream(new ExceptionEvent(event.getChannel(), ex, new ChannelResult(event.getChannel())));
		}
		
		if(!(event instanceof ExceptionEvent))
		{
			core.work(event);
		}
	}
	
	//WRITE, Connect, Bind, Close
	public void sendDownstream(IEvent event)
	{
		try{
			firstHandler.sendDownstream(event);
		}catch(Exception ex){
			lastHandler.sendUpstream(new ExceptionEvent(event.getChannel(), ex, new ChannelResult(event.getChannel())));
		}
		
		if(!(event instanceof ExceptionEvent))
		{
			core.work(event);
		}
	}

	//READ
	public void sendUpstream(IEvent event)
	{
		try{
			lastHandler.sendUpstream(event);
			if(!event.getChannelResult().isSuccess() && event.getChannelResult().getCause() instanceof BufferUnderflowException)
			{
				return;
			}
			
		}catch(Exception ex){
			lastHandler.sendUpstream(new ExceptionEvent(event.getChannel(), ex, new ChannelResult(event.getChannel())));
		}
	}
	
	protected void attachChannelCore(ChannelCore core)
	{
		this.core = core;
	}
}
