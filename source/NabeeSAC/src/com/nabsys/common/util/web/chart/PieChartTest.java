package com.nabsys.common.util.web.chart;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PieChartTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    PieChart pc = new PieChart(500, 300);

		ArrayList<DrawFactor> barList = new ArrayList<DrawFactor>();
		 
		DrawFactor drawFactor = new DrawFactor("value 1");
		drawFactor.setValue(80);
		drawFactor.setColor(new Color(254, 111, 25));
		barList.add(drawFactor);
		drawFactor = new DrawFactor("value 1");
		drawFactor.setValue(10);
		drawFactor.setColor(new Color(254, 111, 25));
		barList.add(drawFactor);
		drawFactor = new DrawFactor("value 1");
		drawFactor.setValue(10);
		drawFactor.setColor(new Color(254, 111, 25));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("value 2");
		drawFactor.setValue(674);
		drawFactor.setColor(new Color(85, 100, 215));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("OKk value 3");
		drawFactor.setValue(423);
		drawFactor.setColor(new Color(94, 215, 85));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("±è±â¿µ value 4");
		drawFactor.setValue(10);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("v");
		drawFactor.setValue(10);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("±è±â¿µ value 4");
		drawFactor.setValue(300);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("value 5");
		drawFactor.setValue(50);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("value 6");
		drawFactor.setValue(50);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("value 7");
		drawFactor.setValue(50);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("value 8");
		drawFactor.setValue(50);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("value 9");
		drawFactor.setValue(50);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		drawFactor = new DrawFactor("value 10");
		drawFactor.setValue(50);
		drawFactor.setColor(new Color(233, 208, 0));
		barList.add(drawFactor);
		
		pc.drawChart(barList, new Font("System", Font.PLAIN, 11));
		
		File file = new File("F:/NabeeWorkspace/NabeeSAC/src/com/nabsys/common/util/web/chart/piechart.jpg");
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(file);
			fos.write(pc.getJpegBuffer());
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
