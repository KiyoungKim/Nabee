package com.nabsys.net.protocol.flxb;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.AccumByteBuffer;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.socket.channel.ChannelResult;
import com.nabsys.net.socket.channel.DataEvent;
import com.nabsys.net.socket.channel.handler.decoder.FullFrameDecoder;

public class FlexibleProtocolHandler extends FullFrameDecoder{
	private String encoding = "";
	public FlexibleProtocolHandler(String encoding) 
	{
		super();
		this.encoding = encoding;
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
		} catch (DataTypeException ex) {
			fireExceptionStream(e.getChannel(), ex, new ChannelResult(e.getChannel()));
		}
	}
	
	protected Object encode(NBFields fields) throws UnsupportedEncodingException, ProtocolException, DataTypeException
	{
		int 	HEADER_LEN	= (Integer.SIZE / Byte.SIZE) + (20) + (Integer.SIZE / Byte.SIZE)+ (Integer.SIZE / Byte.SIZE);
		int 	rtnCode 	= fields.containsKey(FLXB.RETURN_FIELD_NAME)?(Integer)fields.get(FLXB.RETURN_FIELD_NAME):FLXB.SUCCESS;
		String 	serviceID	= fields.containsKey(FLXB.SERVICE_ID_FIELD_NAME)?(String)fields.get(FLXB.SERVICE_ID_FIELD_NAME):" ";
		int		reponseYN	= fields.containsKey(FLXB.REPLY_YN)?(Integer)fields.get(FLXB.REPLY_YN):FLXB.REP_Y;
		
		if(fields.containsKey(FLXB.LENGTH_FIELD_NAME))fields.remove(FLXB.LENGTH_FIELD_NAME);
		if(fields.containsKey(FLXB.SERVICE_ID_FIELD_NAME))fields.remove(FLXB.SERVICE_ID_FIELD_NAME);
		if(fields.containsKey(FLXB.RETURN_FIELD_NAME))fields.remove(FLXB.RETURN_FIELD_NAME);
		if(fields.containsKey(FLXB.REPLY_YN))fields.remove(FLXB.REPLY_YN);
		
		ByteBuffer bodyBuffer = paramsToByteArray(fields);
		bodyBuffer.rewind();
		
		//HEADER SETTING
		ByteBuffer headerBuffer = ByteBuffer.allocateDirect(HEADER_LEN);
				
		headerBuffer.putInt(bodyBuffer.limit());
		headerBuffer.put(serviceID.getBytes(encoding));
		headerBuffer.position(HEADER_LEN - ((Integer.SIZE / Byte.SIZE) * 2));
		headerBuffer.putInt(rtnCode);
		headerBuffer.putInt(reponseYN);

		headerBuffer.rewind();

		AccumByteBuffer accumBuffer = new AccumByteBuffer();
		accumBuffer.write(headerBuffer);
		accumBuffer.write(bodyBuffer);
		
		fields.put(FLXB.SERVICE_ID_FIELD_NAME, serviceID);
		fields.put(FLXB.RETURN_FIELD_NAME, rtnCode);
		fields.put(FLXB.REPLY_YN, reponseYN);
		
		return accumBuffer.getBuffer();
	}

	@SuppressWarnings("rawtypes")
	private ByteBuffer paramsToByteArray(NBFields params) throws DataTypeException, UnsupportedEncodingException
	{
		if(params.size() <= 0) return ByteBuffer.allocateDirect(0);
			
		int tag = FLXB.RESERVED_FIELDS;
		int capacity = params.getCLen(encoding) + (params.getObjCnt() * (Integer.SIZE/Byte.SIZE + Integer.SIZE/Byte.SIZE));

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity);

		String fieldsInfo = "";

		Set<String> keySet = params.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext())
		{
			String key = itr.next();

			if(key.equals(FLXB.LENGTH_FIELD_NAME) 
					|| key.equals(FLXB.SERVICE_ID_FIELD_NAME) 
					|| key.equals(FLXB.RETURN_FIELD_NAME)
					|| key.equals(FLXB.REPLY_YN)
					|| key.contains(":") 
					|| key.contains("@")
					|| key.contains("~")
					|| key.contains("#"))
			{
				throw new DataTypeException(0x001A, "[" + key + "]");
			}
			
			Object obj = params.get(key);
			
			if(obj.getClass() == String.class)
			{
				byte[] tmp = ((String)obj).getBytes(encoding);

				//TAG
				byteBuffer.putInt(tag++);
				//LENGTH
				byteBuffer.putInt(tmp.length);
				//VALUE
				byteBuffer.put(tmp, 0, tmp.length);

				fieldsInfo += "s" + key + ":";
			}
			else if(obj.getClass() == Integer.class)
			{
				//TAG
				byteBuffer.putInt(tag++);
				//LENGTH
				byteBuffer.putInt(Integer.SIZE/Byte.SIZE);
				//VALUE
				byteBuffer.putInt((Integer)obj);
				
				fieldsInfo += "i" + key + ":";
			}
			else if(obj.getClass() == Long.class)
			{
				//TAG
				byteBuffer.putInt(tag++);
				//LENGTH
				byteBuffer.putInt(Long.SIZE/Byte.SIZE);
				//VALUE
				byteBuffer.putLong((Long)obj);
				
				fieldsInfo += "l" + key + ":";
			}
			else if(obj.getClass() == Double.class)
			{
				//TAG
				byteBuffer.putInt(tag++);
				//LENGTH
				byteBuffer.putInt(Double.SIZE/Byte.SIZE);
				//VALUE
				byteBuffer.putDouble((Double)obj);
				
				fieldsInfo += "d" + key + ":";
			}
			else if(obj.getClass() == Float.class)
			{
				//TAG
				byteBuffer.putInt(tag++);
				//LENGTH
				byteBuffer.putInt(Float.SIZE/Byte.SIZE);
				//VALUE
				byteBuffer.putFloat((Float)obj);
				
				fieldsInfo += "f" + key + ":";
			}
			else if(obj.getClass() == byte[].class)
			{
				byte[] tmp = (byte[])obj;
				//TAG
				byteBuffer.putInt(tag++);
				//LENGTH
				byteBuffer.putInt(tmp.length);
				//VALUE
				byteBuffer.put(tmp);
				
				fieldsInfo += "b" + key + ":";
			}
			else if(obj.getClass() == ArrayList.class)
			{
			
				fieldsInfo += key + "@";

				ArrayList tmpList = (ArrayList)obj;
				for(int i=0; i<tmpList.size(); i++)
				{
					if(tmpList.get(i).getClass() != NBFields.class)
					{
						throw new DataTypeException(0x0019, key + "[" + obj.getClass().getName() + "]");
					}
					
					NBFields tmpFields = (NBFields)tmpList.get(i);
					
					Set<String> fKeySet = tmpFields.keySet();
					Iterator<String> fItr = fKeySet.iterator();
					
					while(fItr.hasNext())
					{
						String fKey = fItr.next();
						
						Object fObj = tmpFields.get(fKey);

						if(fObj.getClass() == String.class)
						{
							byte[] tmp = ((String)fObj).getBytes(encoding);

							//TAG
							byteBuffer.putInt(tag++);
							//LENGTH
							byteBuffer.putInt(tmp.length);
							//VALUE
							byteBuffer.put(tmp, 0, tmp.length);
							
							fieldsInfo += "s" + fKey + "~";
						}
						else if(fObj.getClass() == Integer.class)
						{
							//TAG
							byteBuffer.putInt(tag++);
							//LENGTH
							byteBuffer.putInt(Integer.SIZE/Byte.SIZE);
							//VALUE
							byteBuffer.putInt((Integer)fObj);
							
							fieldsInfo += "i" + fKey + "~";
						}
						else if(fObj.getClass() == Long.class)
						{
							//TAG
							byteBuffer.putInt(tag++);
							//LENGTH
							byteBuffer.putInt(Long.SIZE/Byte.SIZE);
							//VALUE
							byteBuffer.putLong((Long)fObj);
							
							fieldsInfo += "l" + fKey + "~";
						}
						else if(fObj.getClass() == Double.class)
						{
							//TAG
							byteBuffer.putInt(tag++);
							//LENGTH
							byteBuffer.putInt(Double.SIZE/Byte.SIZE);
							//VALUE
							byteBuffer.putDouble((Double)fObj);
							
							fieldsInfo += "d" + fKey + "~";
						}
						else if(fObj.getClass() == Float.class)
						{
							//TAG
							byteBuffer.putInt(tag++);
							//LENGTH
							byteBuffer.putInt(Float.SIZE/Byte.SIZE);
							//VALUE
							byteBuffer.putFloat((Float)fObj);
							
							fieldsInfo += "f" + fKey + "~";
						}
						else if(fObj.getClass() == byte[].class)
						{
							byte[] tmp = (byte[])fObj;
							//TAG
							byteBuffer.putInt(tag++);
							//LENGTH
							byteBuffer.putInt(tmp.length);
							//VALUE
							byteBuffer.put(tmp);

							fieldsInfo += "b" + fKey + "~";
						}
						else
						{
							throw new DataTypeException(0x0019, key + "[" + obj.getClass().getName() + "]");
						}
					}
			
					fieldsInfo += "#";
				}
				
				fieldsInfo += ":";
			}
			else
			{
				throw new DataTypeException(0x0019, key + "[" + obj.getClass().getName() + "]");
			}
		}
		
		
		byteBuffer.rewind();
		
		ByteBuffer fieldInfoByte = ByteBuffer.allocateDirect(FLXB.BODY_TAG_LENGTH 
                											+ FLXB.BODY_LEN_LENGTH 
                											+ fieldsInfo.getBytes(encoding).length );
		
		byte[] tmpByte = fieldsInfo.getBytes(encoding);
		fieldInfoByte.putInt(FLXB.FIELDS_INFO);
		fieldInfoByte.putInt(tmpByte.length);
		fieldInfoByte.put(tmpByte, 0, tmpByte.length);
		fieldInfoByte.rewind();

		ByteBuffer rtnBuffer = ByteBuffer.allocateDirect(fieldInfoByte.limit()
                									+ byteBuffer.limit());
		
		rtnBuffer.put(fieldInfoByte);
		rtnBuffer.put(byteBuffer);
		rtnBuffer.rewind();

		return rtnBuffer;
	}

	protected Object decode(AccumByteBuffer accumBuffer)
	{
		ByteBuffer buffer = (ByteBuffer)super.decode(accumBuffer);
		
		NBFields fields = null;
		
		try {
			fields = getNBFieldsFromBuffer(buffer);
		} catch (UnsupportedEncodingException e) {
		}
		
		return fields;
	}

	private NBFields getNBFieldsFromBuffer(ByteBuffer buffer) throws UnsupportedEncodingException
	{
		NBFields fields = new NBFields();
		
		byte[] tmpByte = null;
		
		//READ HEADER
		//LENGTH
		fields.put(FLXB.LENGTH_FIELD_NAME, buffer.getInt());
		//SERVICE ID => 20Bytes String 고정
		tmpByte = new byte[20];
		buffer.get(tmpByte);
		fields.put(FLXB.SERVICE_ID_FIELD_NAME, (new String(tmpByte, encoding)).trim());
		//RESULT CODE
		fields.put(FLXB.RETURN_FIELD_NAME, buffer.getInt()); 
		//REPLY YN
		fields.put(FLXB.REPLY_YN, buffer.getInt()); 

		if(buffer.limit() - buffer.position() > 0)
		{
			fields.putAll(byteArrayToParams(buffer));
		}
		
		
		return fields;
	}

	private NBFields byteArrayToParams(ByteBuffer buffer) throws UnsupportedEncodingException
	{
		NBFields rtnMap = new NBFields();
		String[]  fieldsMap = null;
		
		int fTag = readTag(buffer);
		if(fTag == FLXB.FIELDS_INFO)
		{
			int length = readLength(buffer);
			byte[] tmp = new byte[length];
			buffer.get(tmp);
			
			String rowFields = new String(tmp, encoding);
			fieldsMap = rowFields.split(":");
		}
		else
		{
			return null;
		}

		int fieldIndex = 0;
		do{
			if(fieldsMap[fieldIndex].contains("@"))
			{
				String[] loopSplit = fieldsMap[fieldIndex].split("@");

				String loopName = loopSplit[0];

				if(loopSplit.length < 2)
				{
					rtnMap.put(loopName, new ArrayList<NBFields>());
					
					fieldIndex++;
					
					if(fieldsMap.length > fieldIndex)
						continue;
					else
						break;
				}
				
				String loopFields = loopSplit[1];
				
				String[] loopListArray = loopFields.split("#");
				
				ArrayList<NBFields> arryList = new ArrayList<NBFields>();
				
				for(int i=0; i<loopListArray.length; i++)
				{
					if(loopListArray[i].length() <= 0) continue;
					
					String[] fields = loopListArray[i].split("~");
					
					NBFields loopFieldMap = new NBFields();
					
					for(int j=0; j<fields.length; j++)
					{
						if(fields[j].length() <= 0) continue;
						
						String type = fields[j].substring(0,1);
						String fieldName = fields[j].substring(1);
						
						readTag(buffer);
						int length = readLength(buffer);
						
						if(type.equals("s"))
						{
							byte[] tmp = new byte[length];
							buffer.get(tmp);
							
							String value = new String(tmp, encoding);
							loopFieldMap.put(fieldName, value);
						}
						else if(type.equals("i"))
						{
							int value = buffer.getInt();
							loopFieldMap.put(fieldName, value);
						}
						else if(type.equals("l"))
						{
							long value = buffer.getLong();
							loopFieldMap.put(fieldName, value);
						}
						else if(type.equals("d"))
						{
							Double value = buffer.getDouble();
							loopFieldMap.put(fieldName, value);
						}
						else if(type.equals("f"))
						{
							Float value = buffer.getFloat();
							loopFieldMap.put(fieldName, value);
						}
						else if(type.equals("b"))
						{
							byte[] tmp = new byte[length];
							
							buffer.get(tmp);
							loopFieldMap.put(fieldName, tmp);
						}
					}
					
					arryList.add(loopFieldMap);
				}
				
				rtnMap.put(loopName, arryList);
				
			}
			else
			{
				if(fieldsMap[fieldIndex].length() == 0) return rtnMap;
				
				String type = fieldsMap[fieldIndex].substring(0, 1);
				String fieldName = fieldsMap[fieldIndex].substring(1);

				readTag(buffer);
				int length = readLength(buffer);

				if(type.equals("s"))
				{
					byte[] tmp = new byte[length];
					buffer.get(tmp);
					
					String value = new String(tmp, encoding);
					rtnMap.put(fieldName, value);
				}
				else if(type.equals("i"))
				{
					int value = buffer.getInt();
					rtnMap.put(fieldName, value);
				}
				else if(type.equals("l"))
				{
					long value = buffer.getLong();
					rtnMap.put(fieldName, value);
				}
				else if(type.equals("d"))
				{
					Double value = buffer.getDouble();
					rtnMap.put(fieldName, value);
				}
				else if(type.equals("f"))
				{
					Float value = buffer.getFloat();
					rtnMap.put(fieldName, value);
				}
				else if(type.equals("b"))
				{
					byte[] tmp = new byte[length];
					
					buffer.get(tmp);
					rtnMap.put(fieldName, tmp);
				}
			}

			fieldIndex++;
		}while(buffer.position() < buffer.limit());
		
		if(fieldIndex != fieldsMap.length)
		{
			for(int i=0; i<fieldsMap.length; i++)
			{
				if(fieldsMap[i].contains("@"))
				{
					String key = fieldsMap[i].replace("@", "");
					if(!rtnMap.containsKey(key))
					{
						rtnMap.put(key, new ArrayList<NBFields>());
					}
				}
			}
		}
		
		return rtnMap;
	}
	final NLogger logger = NLogger.getLogger(this.getClass().getName());
	private int readTag(ByteBuffer buffer)
	{
		return buffer.getInt();
	}
	
	private int readLength(ByteBuffer buffer)
	{
		return buffer.getInt();
	}
}
