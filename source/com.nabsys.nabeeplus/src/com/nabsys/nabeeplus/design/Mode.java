package com.nabsys.nabeeplus.design;

public class Mode {
	public static final int			NONE					= 0;
	public static final int			RELATION_MODE			= 1;
	public static final int			END_RELATION_MODE		= 2;
	public static final int			INBOUND_NETWORK_MODE	= 3;
	public static final int			OUTBOUND_NETWORK_MODE	= 4;
	public static final int			CLOSE_NETWORK_MODE		= 5;
	public static final int			MESSAGE_QUEUE_MODE		= 6;
	public static final int			BATCH_MODE				= 7;
	public static final int			DB_FRAME_MODE			= 8;
	public static final int			DB_SELECT_MODE			= 9;
	public static final int			DB_UPDATE_MODE			= 10;
	public static final int			DB_PROCEDURE_MODE		= 11;
	public static final int			LOOP_MODE				= 12;
	public static final int			THREAD_MODE				= 13; 
	public static final int			COMPONENT_MODE			= 14;
	public static final int			FILE_FRAME_MODE			= 15; //BLOCK
	public static final int			FILE_READ_MODE			= 16; //ICON
	public static final int			FILE_WRITE_MODE			= 17; //ICON
	public static final int			FILE_DELETE_MODE		= 18; //ICON
	public static final int			EXCEPTION_MODE			= 19;
	public static final int			SERVICE_CALLER_MODE		= 20;
	public static final int			TERMINATE_MODE			= 21;
	public static final int 		ASSIGN					= 22;
	public static final int 		THROW_MODE				= 23;
	public static final int 		CLIENT_NETWORK_MODE		= 24;
	public static final int 		NETWORK_READ_MODE		= 25;
	public static final int 		NETWORK_WRITE_MODE		= 26;
}
