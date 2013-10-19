package com.nabsys.net.protocol.IPC;

public class IPC {
	public static final int SET_SO_TIMEOUT 			= 1000; //1초
	public static final int CLOSE_SIGNAL 			= 0;

	public static final int BODY_TAG_LENGTH			= Integer.SIZE/Byte.SIZE;
	public static final int BODY_LEN_LENGTH			= Integer.SIZE/Byte.SIZE;
	
	public static final int CMD_ALIVE	 			= 0x0000;
	public static final int CMD_GENERAL 			= 0x0001;
	public static final int CMD_BIND_REQ 			= 0x0002;  //BIND 요청
	public static final int CMD_BIND	 			= 0x0003;  //BIND 처리 요청
	public static final int CMD_BIND_RES 			= 0x0004;  //BIND 결과
	public static final int CMD_IPC		 			= 0x0005;  //IPC 연결
	public static final int CMD_RPT_ALV	 			= 0x0006;  //상태정보보고
	public static final int CMD_SHUTDOWN 			= 0x0007;  //종료
	public static final int CMD_REFRESH	 			= 0x0008;  //설정 리로드
	public static final int CMD_SQL_UPDATE			= 0x0009;  //SQL 업데이트
	public static final int CMD_TLGM_UPDATE			= 0x000A;  //전문 업데이트
	public static final int CMD_COMP_UPDATE			= 0x000B;  //컴포넌트 업데이트
	public static final int CMD_TRX_UPDATE			= 0x000C;  //트랜잭션 업데이트
	public static final int CMD_SVC_UPDATE			= 0x000D;  //서비스 업데이트
	public static final int CMD_USR_UPDATE			= 0x000E;  //사용자 업데이트
	public static final int CMD_EXEC_SQL			= 0x000F;  //SQL 실행
	public static final int CMD_EXEC_SVC			= 0x0010;  //서비스 실행
	
	//성공실패
	public static final int SUCCESS	 				= 0x0000;
	public static final int FAIL		 			= 0x0001;
	public static final int BIND_FAIL	 			= 0x0002;

	public static final String NB_MSG_LENGTH		= "NB_MSG_LENGTH";
	public static final String NB_MSG_DPT_ID		= "NB_MSG_DPT_ID";
	public static final String NB_MSG_SVC_ID		= "NB_MSG_SVC_ID";
	public static final String NB_MSG_TYPE			= "NB_MSG_TYPE";
	public static final String NB_MSG_RETURN		= "NB_MSG_RETURN";

	
	public static final int RESERVED_FIELDS			= 0x0050;
	
	//예약필드
	public static final String NB_BIND_ID			= "NB_BIND_ID";
	public static final String NB_BIND_PW			= "NB_BIND_PW";
	public static final String NB_BIND_AUTH			= "NB_BIND_AUTH";
	public static final String NB_LOAD_CLASS		= "NB_LOAD_CLASS";
	public static final String NB_INVOKE_METHOD		= "NB_INVOKE_METHOD";
	public static final String NB_INSTANCE_NM		= "NB_INSTANCE_NM";
	public static final String NB_MGR_SQNC			= "NB_MGR_SQNC";
	public static final String NB_INSTNCE_ID		= "NB_INSTNCE_ID";
	
	public static final int FIELDS_INFO				= 0x0001;
}
