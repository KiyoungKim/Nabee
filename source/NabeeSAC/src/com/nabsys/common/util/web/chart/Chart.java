package com.nabsys.common.util.web.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Chart {
	protected Graphics2D g;
	protected int width = 0;
	protected int height = 0;
	protected BufferedImage image;
	
	public Chart(int width, int height)
	{
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.g = (Graphics2D)this.image.getGraphics();
		this.g.setColor(Color.WHITE);
		this.g.fillRect(0, 0, width, height);
		
		this.width = width;
		this.height = height;
	}
	
	public byte[] getJpegBuffer() throws IOException
	{
		ByteArrayOutputStream bos = null;
		ImageOutputStream ios = null;
		try {
			bos = new ByteArrayOutputStream(); 
			ios = ImageIO.createImageOutputStream(bos);
			
			//To increase image quality
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
			ImageWriter writer = (ImageWriter)iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(1); 
			
			writer.setOutput(ios);
			IIOImage ioImage = new IIOImage(image , null, null);
			writer.write(null, ioImage, iwp);
			writer.dispose();
			
			byte[] buf = bos.toByteArray();
			return buf;
		} catch (IOException e) {
			throw e;
		} finally {
			if(bos != null)
				bos.close();
		}
	}
	
	protected int getRatio(int imageSize, int maxValue, int value)
	{
		int imageToDraw = (int)((float)value / (float)maxValue * (float)imageSize);
		return imageToDraw;
	}
}
