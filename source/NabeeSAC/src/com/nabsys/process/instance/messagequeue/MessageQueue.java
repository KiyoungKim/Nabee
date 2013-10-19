package com.nabsys.process.instance.messagequeue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.ResourceFactory;

public class MessageQueue extends PriorityQueue<Message> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9008767115634567950L;

	int initSize = 0;
	public MessageQueue(int size, ResourceFactory resourceFactory)
	{
		super(size + 1);
		initSize = size;
	}
	
	public boolean offer(HashMap<String, Object> map, String telegramID)
	{
		if(initSize <= size()) return false;
		return offer(new Message((NBFields)map, telegramID));
	}
	
	/*@SuppressWarnings("unchecked")
	private NBFields setNBFields(HashMap<String, Object> map){
		NBFields fields = new NBFields();
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()){
			String key = itr.next();
			if((map.get(key) instanceof ArrayList))
			{
				ArrayList<Object> list = (ArrayList<Object>)map.get(key);
				ArrayList<NBFields> listFields = new ArrayList<NBFields>();
				
				for(int i=0; i<list.size(); i++)
				{
					HashMap<String, Object> listMap = (HashMap<String, Object>)list.get(i);
					listFields.add(setNBFields(listMap));
				}
				
				fields.put(key, listFields);
			}
			else
			{
				fields.put(key, map.get(key));
			}
		}
		
		return fields;
	}*/

	public Message poll()
	{
		return super.poll();
	}
}
