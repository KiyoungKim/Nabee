package com.nabsys.common.util.web.chart;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class VBarChartTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		vbc.drawHorizontalLine(lineLabel, new Font("System", Font.PLAIN, 11));
		
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
		
		File file = new File("F:/NabeeWorkspace/NabeeSAC/src/com/nabsys/common/util/web/chart/barchart.jpg");
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(file);
			fos.write(vbc.getJpegBuffer());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
