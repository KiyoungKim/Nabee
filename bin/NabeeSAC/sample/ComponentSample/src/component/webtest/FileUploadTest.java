package component.webtest;

import com.nabsys.common.logger.NLogger;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.Context;

public class FileUploadTest {
	final NLogger logger = (NLogger)NLogger.getLogger(this.getClass());
	public NBFields fileUpload(Context context)
	{
		NBFields fields = new NBFields();
		fields.put("RESULT", "SUCCESS");
		
		return fields;
	}
}
