package com.nabsys.net.protocol.asnd;

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
import com.nabsys.net.socket.channel.handler.FieldType;
import com.nabsys.net.socket.channel.handler.decoder.LengthFieldFrameDecoder;
import com.nabsys.resource.NetworkContext;
import com.nabsys.resource.TelegramFieldContext;
import com.nabsys.resource.TelegramFieldContextList;

public class AssignedProtocolHandler extends LengthFieldFrameDecoder{
	
	private TelegramFieldContext[] telegramFieldArray;
	private NetworkContext networkContext;
	
	public AssignedProtocolHandler(
			NetworkContext networkContext, TelegramFieldContextList telegramFieldArrayContextList, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment) 
	{
		super(lengthFieldOffset, lengthFieldLength, lengthAdjustment, FieldType.STR_LENGTH_FIELD);
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
		int lengthFieldLen = 0;
		char lengthFieldType = 0;
		for(int i=0; i<telegramFieldArray.length; i++)
		{
			if(telegramFieldArray[i].getID().equals(networkContext.getLengthFieldID()))
			{
				lengthFieldLen = telegramFieldArray[i].getLength();
				lengthFieldType = telegramFieldArray[i].getType();
			}
			
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
		
		int packetLength = expLoopBlockSize - lengthFieldLen + (loopBlockSize * loopBlock.size());

		switch(lengthFieldType)
		{
		case('C') :
			fields.put(networkContext.getLengthFieldID(), Integer.toString(packetLength));
			break;
		case('N') :
			fields.put(networkContext.getLengthFieldID(), packetLength);
			break;
		case('I') :
			fields.put(networkContext.getLengthFieldID(), packetLength);
			break;
		case('D') :
			fields.put(networkContext.getLengthFieldID(), (double)(packetLength));
			break;
		case('F') :
			fields.put(networkContext.getLengthFieldID(), (float)(packetLength));
			break;
		case('L') :
			fields.put(networkContext.getLengthFieldID(), (long)(packetLength));
			break;
		case('B') :
			fields.put(networkContext.getLengthFieldID(), (byte)(packetLength));
			break;
		case('A') :
			fields.put(networkContext.getLengthFieldID(), intToByte((packetLength)));
			break;
		default :
		}

		//PACKET SETTING
		ByteBuffer buffer = ByteBuffer.allocateDirect(expLoopBlockSize + (loopBlockSize * loopBlock.size()));
		
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
	
	private byte[] intToByte(int value)
	{
		byte[] byteArray = new byte[Integer.SIZE/Byte.SIZE];
		
		byteArray[0] = (byte)((value >> 24)& 0xff);
		byteArray[1] = (byte)((value >> 16)& 0xff);
		byteArray[2] = (byte)((value >> 8)& 0xff);
		byteArray[3] = (byte)(value);
		
		return byteArray;
	}
	
}
