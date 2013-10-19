package component;

import java.nio.ByteBuffer;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.Context;

public class ProxyAssigner {
	
	public NBFields setOutbound(String address, int port)
	{
		NBFields fields = new NBFields();
		fields.put("OUTBOUND_ADDRESS", address);
		fields.put("OUTBOUND_PORT", port);
		return fields;
	}
	
	public NBFields setOutbound(Context context)
	{
		String msg = "Connect to :\n";
		msg += "(1)DEV  (2)DEW\n";
		
		ByteBuffer rtn = ByteBuffer.allocateDirect(msg.getBytes().length);
		context.getChannel().write(rtn.put(msg.getBytes()));
		
		
		NBFields fields = new NBFields();
		fields.put("OUTBOUND_ADDRESS", "10.217.54.56");
		fields.put("OUTBOUND_PORT", 22);
		return fields;
	}
}
