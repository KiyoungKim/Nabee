package com.nabsys.net.protocol.delimiter;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.nabsys.common.util.AccumByteBuffer;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.handler.Delimiter;
import com.nabsys.net.socket.channel.handler.decoder.DelimiterFrameDecoder;
import com.nabsys.resource.NetworkContext;
import com.nabsys.resource.TelegramFieldContext;
import com.nabsys.resource.TelegramFieldContextList;

public class LFDelimiterProtocolHandler extends DelimiterFrameDecoder{
	
	private TelegramFieldContext[] telegramFieldArray;
	private NetworkContext networkContext;
	
	public LFDelimiterProtocolHandler(NetworkContext networkContext, TelegramFieldContextList telegramFieldArrayContextList)
	{
		super(Delimiter.NEW_LINE.getDelimiter(), 2000);
		this.telegramFieldArray = telegramFieldArrayContextList.toArray();
		this.networkContext = networkContext;
	}
	
	public void writeRequested(DataEvent e)
	{
		Object d = e.getMessage();
		if(!(d instanceof NBFields))
		{
			super.writeRequested(e);
			return;
		}

		Object frame = null;
		try {
			frame = encode((NBFields)d);
			e.setData(frame);

			super.writeRequested(e);
		} catch (UnsupportedEncodingException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		} catch (ProtocolException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Object encode(NBFields fields) throws UnsupportedEncodingException, ProtocolException
	{
		//////////////////////////////////LOOP block ���� ���
		//�����Ǵ� �Ǿ������� �Ķ���Ϳ� �V�õ��� ���� ��츦 ���� �ݺ����� �ִ°�� ��� ����� �Ѵ�.
		ArrayList<NBFields> loopBlock = new ArrayList<NBFields>();
		Set<String> keySet = fields.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext())
		{
			String key = itr.next();
			Object tmp = fields.get(key);
			if(tmp.getClass() == ArrayList.class)
			{
				loopBlock = (ArrayList<NBFields>)tmp;
				break;
			}
		}
		
		boolean meetLoop = false;
		int loopBlockSize = 0;
		int loopBlockCnt = 0;
		int expLoopBlockSize = 0;
		for(int i=0; i<telegramFieldArray.length; i++)
		{
			if(telegramFieldArray[i].getID().equals("_startLOOP"))
			{
				meetLoop = true;
				continue;
			}
			
			if(telegramFieldArray[i].getID().equals("_endLOOP"))
			{
				meetLoop = false;
				continue;
			}
			
			if(!meetLoop)
			{
				expLoopBlockSize += telegramFieldArray[i].getLength();
			}
			else
			{
				loopBlockSize += telegramFieldArray[i].getLength();
				loopBlockCnt++;
			}
		}
		
		//PACKET SETTING
		ByteBuffer buffer = ByteBuffer.allocateDirect(expLoopBlockSize + (loopBlockSize * loopBlock.size()) + 1);
		
		for(int i=0; i<telegramFieldArray.length; i++)
		{
			if(telegramFieldArray[i].getID().equals("_startLOOP"))
			{
				int loopIndex = i+1;
				int dataLoopIndex = 0;
				for(int j=0; j<loopBlock.size() * loopBlockCnt; j++)
				{
					telegramFieldArray[loopIndex].setConvertData(buffer, loopBlock.get(dataLoopIndex), networkContext.getServerEncoding());
					loopIndex++;
					if((j + 1)%loopBlockCnt == 0)
					{
						loopIndex = i+1;
						dataLoopIndex++;
					}
				}
				
				i = i + loopBlockCnt + 2;
			}
			else
			{
				telegramFieldArray[i].setConvertData(buffer, fields, networkContext.getServerEncoding());
			}
		}
	
		buffer.put(Delimiter.NEW_LINE.getDelimiter());
		buffer.rewind();
		return buffer;
	}
	
	protected Object decode(AccumByteBuffer accumBuffer)
	{
		ByteBuffer buffer = (ByteBuffer)super.decode(accumBuffer);
		
		NBFields rtnFields = null;
		
		try {
			rtnFields = getNBFieldsFromBuffer(buffer);
		} catch (UnsupportedEncodingException e) {
		}
		
		return rtnFields;
	}
	
	private NBFields getNBFieldsFromBuffer(ByteBuffer buffer) throws UnsupportedEncodingException
	{
		NBFields rtnFields = new NBFields();
		
		int afterLoopSize = 0;
		boolean afterLoop = false;
		for(int i=0; i<telegramFieldArray.length; i++)
		{
			if(afterLoop)afterLoopSize += telegramFieldArray[i].getLength();
			if(telegramFieldArray[i].getID().equals("_endLOOP")) afterLoop = true;
		}
		
		for(int i=0; i<telegramFieldArray.length; i++)
		{
			if(telegramFieldArray[i].getID().equals("_startLOOP"))
			{
				ArrayList<NBFields> loopFields = new ArrayList<NBFields>();
				NBFields tmp = null;
				int loopIndex = i + 1;
				tmp = new NBFields();
				while(true)
				{
					if(telegramFieldArray[loopIndex].getID().equals("_endLOOP"))
					{
						if(tmp != null) loopFields.add(tmp);
						if(afterLoopSize == buffer.remaining())
						{
							i = loopIndex;
							break;
						}
						loopIndex = i+1;
						tmp = new NBFields();
					}

					tmp.put(telegramFieldArray[loopIndex].getID(), 
							telegramFieldArray[loopIndex].getConvertData(buffer, networkContext.getServerEncoding()));
					
					loopIndex++;
				}
				
				rtnFields.put("_LOOP", loopFields);
			}
			else
			{
				rtnFields.put(telegramFieldArray[i].getID(), 
						telegramFieldArray[i].getConvertData(buffer, networkContext.getServerEncoding()));
			}
		}
		
		return rtnFields;
	}
}
