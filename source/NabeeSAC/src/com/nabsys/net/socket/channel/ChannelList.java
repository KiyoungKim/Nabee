package com.nabsys.net.socket.channel;

import java.net.SocketAddress;
import java.util.ArrayList;

public class ChannelList extends ArrayList<Channel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1219463113317205446L;
	private int capacity = 0;

	public ChannelList()
	{
		super();
	}
	
	public ChannelList(int size)
	{
		super(size);
		this.capacity = size;
	}
	
	public synchronized boolean add(Channel channel)
	{
		if(this.capacity > 0 && size() >= this.capacity)
		{
			return false;
		}
		
		return super.add(channel);
	}
	
	IChannelResult write(SocketAddress remoteAddress, Object message)
	{
		for(int i=0; i<size(); i++)
		{
			if(get(i).getRemoteAddress().equals(remoteAddress))
			{
				return get(i).write(message);
			}
		}
		
		return null;
	}
	
	public void remove(Channel channel)
	{
		super.remove(channel);
		((AsyncAcceptChannel)channel).setParent(null);
	}
}
