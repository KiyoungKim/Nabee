package component.webtest;

import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.Context;

public class IndexTest {
	final NLogger logger = (NLogger)NLogger.getLogger(this.getClass());
	public NBFields execute(Context context)
	{
		NBFields fields = context.getNBFields();
		logger.debug(fields);
		
		fields.put("NAME", "KIYOUNG KIM");
		return fields;
	}
}
