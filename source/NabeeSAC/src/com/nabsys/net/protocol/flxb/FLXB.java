package com.nabsys.net.protocol.flxb;

public class FLXB {
	public static final String 	LENGTH_FIELD_NAME 			= "RSV_LENGTH";
	public static final String 	SERVICE_ID_FIELD_NAME 		= "RSV_SERVICE_ID";
	public static final String 	RETURN_FIELD_NAME 			= "RSV_RETURN";
	public static final String	REPLY_YN					= "RSV_REP_YN";
	public static final String	KEEP_ALIVE_SERVICE			= "RSV_KEEP_ALIVE_SVC";

	public static final int 	BODY_TAG_LENGTH				= Integer.SIZE/Byte.SIZE;
	public static final int 	BODY_LEN_LENGTH				= Integer.SIZE/Byte.SIZE;
	
	public static final int		SUCCESS						= 0x0000;
	public static final int		FAIL						= 0x0001;
	
	public static final int		REP_Y						= 0x0000;
	public static final int		REP_N						= 0x0001;
	
	public static final int 	FIELDS_INFO					= 0x0001;
	
	public static final int 	RESERVED_FIELDS				= 0x0002; //다음부터 태그를 붙일 수 있음
}
