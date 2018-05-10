/*
 * Copyright (c) 2011-2017, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;



public class Test {

	private final static Color COOKIECOLOR = new Color(201, 159, 111);
	private final static Color CHIPCOLOR = new Color(107, 79, 68);
	private final static Color BUYCOLOR = new Color(143, 140, 132);

	public static BufferedImage findColors(BufferedImage image, Color find, double threshhold)
	{
		//BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		BufferedImage result = image;
		for(int i = 0; i < image.getHeight(); i++)
			for(int j = 0; j < image.getWidth(); j++)
			{
				if(colorDistance(new Color (image.getRGB(j, i)), find) < threshhold)
					result.setRGB(j, i, -16777216);
				else
					result.setRGB(j, i, -1);
			}
		return result;
	}
	
	
	private static double colorDistance (Color check, Color target)
	{
		return Math.sqrt(((check.getRed() - target.getRed()) * (check.getRed() - target.getRed())) + ((check.getGreen() - target.getGreen()) * (check.getGreen() - target.getGreen())) + ((check.getBlue() - target.getBlue()) * (check.getBlue() - target.getBlue())));
	}
	
	public static BufferedImage getScreen()
	{
		BufferedImage screenPicture;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screenDevice = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = screenDevice.getDefaultConfiguration();
		Robot screenDeviceShot = null;
		//standard screenshot taking with java.awt.Robot
		try {screenDeviceShot = new Robot(screenDevice);} catch (AWTException e) {System.exit(1);}
		screenPicture = screenDeviceShot.createScreenCapture(gc.getBounds());

		return screenPicture;
	}
	
	public static void main( String args[] ) {
		BufferedImage result = findColors(getScreen(), CHIPCOLOR, 50);
		

		
		System.out.println("Done!");
		
	}
}
