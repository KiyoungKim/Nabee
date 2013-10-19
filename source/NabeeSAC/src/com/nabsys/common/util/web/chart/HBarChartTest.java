package com.nabsys.common.util.web.chart;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class HBarChartTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    HorizontalBarChart hbc = new HorizontalBarChart(300, 150);

	    hbc.setBarHeight(15);
		
		ArrayList<DrawFactor> barList = new ArrayList<DrawFactor>();
		 
		DrawFactor drawFactor = new DrawFactor();
		drawFactor.setValue(80);
		drawFactor.setColor(new Color(254, 111, 25));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor();
		drawFactor.setValue(674);
		drawFactor.setColor(new Color(85, 100, 215));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor();
		drawFactor.setValue(423);
		drawFactor.setColor(new Color(94, 215, 85));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor();
		drawFactor.setValue(3296);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		hbc.drawChart(barList, new Font("System", Font.PLAIN, 11));
		
		File file = new File("F:/NabeeWorkspace/NabeeSAC/src/com/nabsys/common/util/web/chart/hbarchart.jpg");
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(file);
			fos.write(hbc.getJpegBuffer());
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
