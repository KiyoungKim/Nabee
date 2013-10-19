package component.webtest;

import java.util.ArrayList;

import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.Context;

public class WebArrayReturnTest {
	public NBFields execute(Context context)
	{
		ArrayList<NBFields> list = new ArrayList<NBFields>();
		
		for(int i=0; i<5; i++)
		{
			NBFields tmp1 = new NBFields();
			tmp1.put("tmp1", "TMP1-" + i);
			list.add(tmp1);
			
			NBFields tmp2 = new NBFields();
			tmp1.put("tmp2", "TMP-" + i);
			list.add(tmp2);
			
			NBFields tmp3 = new NBFields();
			tmp1.put("tmp3", "TMP-" + i);
			list.add(tmp3);
		}
		
		NBFields rtnFields = context.getNBFields();
		rtnFields.put("ARRAYTEST", list);
		
		return rtnFields;
	}
}
