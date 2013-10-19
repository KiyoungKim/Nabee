package component.webtest;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import com.nabsys.common.logger.NLogger;
import com.nabsys.common.util.web.chart.DrawFactor;
import com.nabsys.common.util.web.chart.VerticalBarChart;
import com.nabsys.net.protocol.NBFields;
import com.nabsys.process.Context;

public class ChartCallTest {
	final NLogger logger = (NLogger)NLogger.getLogger(this.getClass());
	public NBFields loadImage(Context context)
	{
		logger.debug("ChartCallTest.loadImage start");
		try{
		    VerticalBarChart vbc = new VerticalBarChart(300, 200, 12);

			vbc.setBarWidth(15);
			vbc.setMaxValue(100);
			
			ArrayList<String> lineLabel = new ArrayList<String>();
			lineLabel.add("0");
			lineLabel.add("10");
			lineLabel.add("20");
			lineLabel.add("30");
			lineLabel.add("40");
			lineLabel.add("50");
			lineLabel.add("60");
			lineLabel.add("70");
			lineLabel.add("80");
			lineLabel.add("90");
			lineLabel.add("100");
			vbc.drawHorizontalLine(lineLabel, new Font("Arial", Font.PLAIN, 10));
			
			ArrayList<String> bottomLabel = new ArrayList<String>();
			bottomLabel.add("1월");
			bottomLabel.add("4월");
			bottomLabel.add("7월");
			bottomLabel.add("10월");
			vbc.setBottomLabel(bottomLabel, new Font("System", Font.PLAIN, 11));
			
			    
			ArrayList<DrawFactor> barList = new ArrayList<DrawFactor>();
			 
			DrawFactor drawFactor = new DrawFactor();
			drawFactor.setValue(10);
			drawFactor.setColor(new Color(254, 111, 25));
			barList.add(drawFactor);
			
			drawFactor = new DrawFactor();
			drawFactor.setValue(40);
			drawFactor.setColor(new Color(85, 100, 215));
			barList.add(drawFactor);
			
			drawFactor = new DrawFactor();
			drawFactor.setValue(20);
			drawFactor.setColor(new Color(94, 215, 85));
			barList.add(drawFactor);
			
			drawFactor = new DrawFactor();
			drawFactor.setValue(100);
			drawFactor.setColor(new Color(233, 208, 0));
			barList.add(drawFactor);
			
			vbc.drawChart(barList);
			
			NBFields fields = context.getNBFields();
			
			fields.put("CHART", vbc.getJpegBuffer());
			
			return fields;
		}catch(NullPointerException ex){
			logger.error(ex, ex.getMessage());
		}catch(Exception ex){
			logger.error(ex, ex.getMessage());
		}
		return null;
	}
}
