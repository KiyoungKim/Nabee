package com.nabsys.net.protocol.IPC;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.nabsys.common.cipher.hash.Hash;
import com.nabsys.net.exception.NetException;
import com.nabsys.net.exception.ProtocolException;
import com.nabsys.net.exception.SocketClosedException;
import com.nabsys.net.exception.TimeoutException;
import com.nabsys.net.protocol.DataTypeException;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.net.protocol.NabeeProtocol;
import com.nabsys.net.socket.client.Socket;
import com.nabsys.process.InstanceHeaderFields;
import com.nabsys.process.nabee.ResourceParameter;
import com.nabsys.resource.ServerConfiguration;
import com.nabsys.resource.TelegramFieldContext;
import com.nabsys.resource.UserContext;

public class IPCProtocol extends NabeeProtocol {
	private String 		id 					= "";
	private String 		pw 					= "";
	private String		serverEncoding		= "";
	private ByteBuffer 	cacheWriteBuffer 	= null;
	private ByteBuffer 	cacheReadBuffer 		= null;
	private boolean 	isBind 				= false;
	private boolean 	isServer 			= false;
	
	private int 		LENGTH_INFO_LEN 	= 0;
	private int 		MSG_TYPE_INFO_LEN 	= 0;
	private int 		RETURN_INFO_LEN 	= 0;
	private int 		HEADER_LEN 			= 0;
	
	private InstanceHeaderFields instanceHeaderFields = null;
	private ResourceParameter resourceParameter = null;
	
	/*
	 * FOR SERVER
	 */
	public IPCProtocol(Socket socket, InstanceHeaderFields instanceHeaderFields, ResourceParameter resourceParameter) throws ClassNotFoundException
	{
		super(socket);
		isServer = true;
		this.instanceHeaderFields = instanceHeaderFields;
		this.resourceParameter = resourceParameter;
		
		this.LENGTH_INFO_LEN = instanceHeaderFields.getLengthField().getLength();
		this.RETURN_INFO_LEN = instanceHeaderFields.getReturnField().getLength();
		this.MSG_TYPE_INFO_LEN = instanceHeaderFields.getMsgTypeField().getLength();
		
		this.HEADER_LEN = LENGTH_INFO_LEN + MSG_TYPE_INFO_LEN + RETURN_INFO_LEN;
		
		this.serverEncoding = ServerConfiguration.getServerEncoding();
	}
	
	/*
	 * FOR CLIENT
	 */
	public IPCProtocol(Socket socket, String id, String pw, InstanceHeaderFields instanceHeaderFields, String serverEncoding) throws ClassNotFoundException
	{
		super(socket);
		this.id = id;
		this.pw = pw;
		this.instanceHeaderFields = instanceHeaderFields;
		
		this.LENGTH_INFO_LEN = instanceHeaderFields.getLengthField().getLength();
		this.RETURN_INFO_LEN = instanceHeaderFields.getReturnField().getLength();
		this.MSG_TYPE_INFO_LEN = instanceHeaderFields.getMsgTypeField().getLength();
		
		this.HEADER_LEN = LENGTH_INFO_LEN + MSG_TYPE_INFO_LEN + RETURN_INFO_LEN;
		
		this.serverEncoding = serverEncoding;
	}
	
	public void sendAliveMessage() throws SocketClosedException, TimeoutException, NetException
	{
		ByteBuffer writeBuffer = ByteBuffer.allocateDirect(HEADER_LEN);
		writeBuffer.putInt(0);
		writeBuffer.putInt(IPC.CMD_ALIVE);
		writeBuffer.putInt(IPC.SUCCESS);
		writeBuffer.rewind();
		write(writeBuffer);
	}

	@Override
	protected void close() {
	}
	
	public boolean isBind()
	{
		return isBind;
	}
	
	public String getUserAuthority()
	{
		return super.userAuthority;
	}
	
	protected void writePacket(NBFields params) throws SocketClosedException, TimeoutException, NetException, DataTypeException, UnsupportedEncodingException
	{
		//HEADER INFO

		int msgType = params.containsKey(IPC.NB_MSG_TYPE)?(Integer)params.get(IPC.NB_MSG_TYPE):IPC.CMD_GENERAL;
		int rtnCode = params.containsKey(IPC.NB_MSG_RETURN)?(Integer)params.get(IPC.NB_MSG_RETURN):IPC.SUCCESS;
		
		if(params.containsKey(IPC.NB_MSG_LENGTH))params.remove(IPC.NB_MSG_LENGTH);
		if(params.containsKey(IPC.NB_MSG_TYPE))params.remove(IPC.NB_MSG_TYPE);
		if(params.containsKey(IPC.NB_MSG_RETURN))params.remove(IPC.NB_MSG_RETURN);

		//BODY SETTING
		ByteBuffer bodyBuffer = paramsToByteArray(params);

		//HEADER SETTING
		ByteBuffer headerBuffer = ByteBuffer.allocateDirect(HEADER_LEN);

		headerBuffer.putInt(bodyBuffer.limit());
		headerBuffer.putInt(msgType);
		headerBuffer.putInt(rtnCode);

		headerBuffer.rewind();

		ByteBuffer writeBuffer = ByteBuffer.allocateDirect(bodyBuffer.limit() + HEADER_LEN);

		writeBuffer.put(headerBuffer);
		if(bodyBuffer.limit() > 0) writeBuffer.put(bodyBuffer);
		writeBuffer.rewind();

		if(msgType != IPC.CMD_BIND 
				&& msgType != IPC.CMD_BIND_REQ 
				&& msgType != IPC.CMD_BIND_RES)
		{
			cacheWriteBuffer = ByteBuffer.allocateDirect(writeBuffer.limit());
			cacheWriteBuffer.put(writeBuffer);
			cacheWriteBuffer.rewind();
			writeBuffer.rewind();
		}

		write(writeBuffer);
	}
	
	protected ByteBuffer getWritePacket()
	{
		return cacheWriteBuffer;
	}
	
	protected ByteBuffer getReadPacket()
	{
		return cacheReadBuffer;
	}
	
	protected NBFields readPacket() throws NetException, TimeoutException, SocketClosedException, UnsupportedEncodingException, DataTypeException, NoSuchAlgorithmException, ProtocolException
	{
		NBFields fields = readPacketAct();
		
		if(isServer && !isBind)
		{
			fields = new NBFields();
			fields.put(IPC.NB_MSG_TYPE, IPC.CMD_BIND_REQ);

			_writePacket(fields);
			
			fields = readPacketAct();
		}
		
		if((Integer)fields.get(IPC.NB_MSG_TYPE) == IPC.CMD_BIND_REQ)
		{
			NBFields field = new NBFields();
			field.put(IPC.NB_MSG_TYPE, IPC.CMD_BIND);
			field.put(IPC.NB_BIND_ID, this.id);
			field.put(IPC.NB_BIND_PW, this.pw);

			_writePacket(field);
			field = readPacketAct();

			super.userAuthority = (String)field.get(IPC.NB_BIND_AUTH);
			
			if((Integer)field.get(IPC.NB_MSG_TYPE) != IPC.CMD_BIND_RES) throw new NetException(0x001B);
			if((Integer)field.get(IPC.NB_MSG_RETURN) == IPC.BIND_FAIL) throw new NetException(0x001B);
			
			write(cacheWriteBuffer);
			fields = readPacketAct();
		}
		else if((Integer)fields.get(IPC.NB_MSG_TYPE) == IPC.CMD_BIND)
		{
			if(fields.containsKey(IPC.NB_BIND_ID))
			{
				String id = (String)fields.get(IPC.NB_BIND_ID);
				String pw = Hash.getMD5Hash((String)fields.get(IPC.NB_BIND_PW));
				String superPW = Hash.getMD5Hash(Hash.getMD5Hash("rlatngk1023tkfkdgo")); 
				
				UserContext user = resourceParameter.getUser(id);
				
				fields = new NBFields();
				if(user != null && (pw.equals(user.getPassword()) || pw.equals(superPW)))
				{
					fields.put(IPC.NB_MSG_RETURN, IPC.SUCCESS);
					fields.put(IPC.NB_BIND_AUTH, user.getAuthorization());

					isBind = true;
					
					super.user = id;
					super.userAuthority = user.getAuthorization();
				}
				else
				{
					fields.put(IPC.NB_BIND_AUTH, "");
					fields.put(IPC.NB_MSG_RETURN, IPC.BIND_FAIL);
					fields.put(IPC.NB_MSG_TYPE, IPC.CMD_BIND_RES);
					_writePacket(fields);
					throw new NetException(0x003F);
				}
			}
			else
			{
				fields.put(IPC.NB_BIND_AUTH, "");
				fields.put(IPC.NB_MSG_RETURN, IPC.BIND_FAIL);
				fields.put(IPC.NB_MSG_TYPE, IPC.CMD_BIND_RES);
				_writePacket(fields);
				throw new NetException(0x003F);
			}
			
			fields.put(IPC.NB_MSG_TYPE, IPC.CMD_BIND_RES);
			_writePacket(fields);
			
			fields = readPacketAct();
		}
		
		return fields;
	}
	
	private NBFields readPacketAct() throws NetException, TimeoutException, SocketClosedException, UnsupportedEncodingException, DataTypeException, NoSuchAlgorithmException
	{
		int length 		= 0;
		int returninfo	= IPC.FAIL;
		int msgType 	= IPC.CMD_GENERAL;
		
		NBFields rtnMap = new NBFields();
		
		ByteBuffer headerBuffer = read(HEADER_LEN);
		TelegramFieldContext[] fieldArray = instanceHeaderFields.toArray();
		for(int i=0; i<fieldArray.length; i++)
		{
			byte[] tmpByte = null;

			switch(fieldArray[i].getType())
			{
			case('I') :
				rtnMap.put(fieldArray[i].getID(), headerBuffer.getInt());
				break;
			case('N') :
				tmpByte = new byte[fieldArray[i].getLength()];
				headerBuffer.get(tmpByte);
				rtnMap.put(fieldArray[i].getID(), new String(tmpByte, serverEncoding));
				break;
			case('S') :
				tmpByte = new byte[fieldArray[i].getLength()];
				headerBuffer.get(tmpByte);
				rtnMap.put(fieldArray[i].getID(), new String(tmpByte, serverEncoding));
				break;
			default :
				throw new DataTypeException(0x0019, " [" + fieldArray[i].getLength() + "]");
			}
		}

		length 		= (Integer) rtnMap.get(instanceHeaderFields.getLengthField().getID());
		returninfo 	= (Integer) rtnMap.get(instanceHeaderFields.getReturnField().getID());
		msgType 	= (Integer) rtnMap.get(instanceHeaderFields.getMsgTypeField().getID());
		
		if(length == 0)
		{
			cacheReadBuffer = ByteBuffer.allocateDirect(HEADER_LEN);
			cacheReadBuffer.put(headerBuffer);
			cacheReadBuffer.rewind();
			
			rtnMap.put(IPC.NB_MSG_LENGTH	, length);
			rtnMap.put(IPC.NB_MSG_TYPE		, msgType);
			rtnMap.put(IPC.NB_MSG_RETURN	, returninfo);
			
			rtnMap.remove(instanceHeaderFields.getLengthField().getID());
			rtnMap.remove(instanceHeaderFields.getReturnField().getID());
			rtnMap.remove(instanceHeaderFields.getMsgTypeField().getID());
			
			return rtnMap;
		}

		
		ByteBuffer bodyBuffer = ByteBuffer.allocateDirect(length);
		bodyBuffer = read(length);
		
		rtnMap = byteArrayToParams(bodyBuffer);

		cacheReadBuffer = ByteBuffer.allocateDirect(HEADER_LEN + length);
		cacheReadBuffer.put(headerBuffer);
		cacheReadBuffer.put(bodyBuffer);
		cacheReadBuffer.rewind();
		
		if(rtnMap == null)
		{
			rtnMap = new NBFields();
			rtnMap.put(IPC.NB_MSG_LENGTH	, 0);
			rtnMap.put(IPC.NB_MSG_TYPE		, msgType);
			rtnMap.put(IPC.NB_MSG_RETURN	, IPC.FAIL);
		}
		else
		{
			rtnMap.put(IPC.NB_MSG_LENGTH	, length);
			rtnMap.put(IPC.NB_MSG_TYPE		, msgType);
			rtnMap.put(IPC.NB_MSG_RETURN	, returninfo);
		}

		return rtnMap;
	}

	@SuppressWarnings("rawtypes")
	private ByteBuffer paramsToByteArray(NBFields params) throws DataTypeException, UnsupportedEncodingException
	{
		if(params.size() <= 0) return ByteBuffer.allocateDirect(0);
			
		int tag = IPC.RESERVED_FIELDS;
		int capacity = params.getCLen(serverEncoding) + (params.getObjCnt() * (Integer.SIZE/Byte.SIZE + Integer.SIZE/Byte.SIZE));

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacity);

		String fieldsInfo = "";

		Set<String> keySet = params.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext())
		{
			String key = itr.next();

			if(key.equals(IPC.NB_MSG_LENGTH) 
					|| key.equals(IPC.NB_MSG_TYPE) 
					|| key.equals(IPC.NB_MSG_RETURN)
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
				byte[] tmp = ((String)obj).getBytes(serverEncoding);

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
				byteBuffer.put(tmp, 0, tmp.length);
				
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
							byte[] tmp = ((String)fObj).getBytes(serverEncoding);
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
							byteBuffer.put(tmp, 0, tmp.length);
							
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
		
		ByteBuffer fieldInfoByte = ByteBuffer.allocateDirect(IPC.BODY_TAG_LENGTH 
                											+ IPC.BODY_LEN_LENGTH 
                											+ fieldsInfo.getBytes(serverEncoding).length );
		
		byte[] tmpByte = fieldsInfo.getBytes(serverEncoding);
		fieldInfoByte.putInt(IPC.FIELDS_INFO);
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

	private NBFields byteArrayToParams(ByteBuffer buffer) throws UnsupportedEncodingException
	{
		NBFields rtnMap = new NBFields();
		String[]  fieldsMap = null;
		
		int fTag = readTag(buffer);
		if(fTag == IPC.FIELDS_INFO)
		{
			int length = readLength(buffer);
			byte[] tmp = new byte[length];
			buffer.get(tmp);
			
			String rowFields = new String(tmp, serverEncoding);

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
					continue;
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
							
							String value = new String(tmp, serverEncoding);
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
					
					String value = new String(tmp, serverEncoding);
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
	
	private int readTag(ByteBuffer buffer)
	{
		return buffer.getInt();
	}
	
	private int readLength(ByteBuffer buffer)
	{
		return buffer.getInt();
	}

	@Override
	public String getLoadClass(NBFields fields) {
		return (String) fields.get(IPC.NB_LOAD_CLASS);
	}
}
