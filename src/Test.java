package src;
/*
 * Copyright (c) 2011-2017, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import boofcv.gui.image.ShowImages;



public class Test {

	private final static Color COOKIECOLOR = new Color(201, 159, 111);
	private final static Color CHIPCOLOR = new Color(107, 79, 68);
	private final static Color BUYCOLOR = new Color(168, 194, 207);

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
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
	Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
	byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
	mat.put(0, 0, data);
	return mat;
	}
	
	
	public static BufferedImage matToBufferedImage(Mat rgba)
	{
	Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2GRAY, 0);

	// Create an empty image in matching format
	BufferedImage gray = new BufferedImage(rgba.width(), rgba.height(), BufferedImage.TYPE_BYTE_GRAY);

	// Get the BufferedImage's backing array and copy the pixels directly into it
	byte[] data = ((DataBufferByte) gray.getRaster().getDataBuffer()).getData();
	rgba.get(0, 0, data);
	
	return gray;
	}

	public static Mat BufferedImage2Mat(BufferedImage image) {
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	try {
		ImageIO.write(image, "jpg", byteArrayOutputStream);
	byteArrayOutputStream.flush();
	return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("critical error");
	return null;
	}

	
	public static void main( String args[] ) {
	List<Point> points = new ArrayList<Point>();
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME );
	BufferedImage result = findColors(getScreen(), BUYCOLOR, 34);
//	BufferedImage result = getScreen();
	Mat src = BufferedImage2Mat(result);
//	ShowImages.showWindow(matToBufferedImage(image), "test");
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(gray, gray, 5);

		Mat circles = new Mat();
		Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
		(double)gray.rows()/16, // change this value to detect circles with different distances to each other
		100.0, 30.0, 50, 200); // change the last two parameters
		// (min_radius & max_radius) to detect larger circles
		for (int x = 0; x < circles.cols(); x++) {
			double[] c = circles.get(0, x);
			Point center = new Point(Math.round(c[0]), Math.round(c[1]));
			// circle center
			Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
			// circle outline
			int radius = (int) Math.round(c[2]);
			Imgproc.circle(src, center, radius, new Scalar(255,0,255), 3, 8, 0 );
			points.add(center);
		}
		System.out.println(stringList(points));

		HighGui.imshow("detected circles", src);
		HighGui.waitKey();
		
		/*
		Mat srcBuy = BufferedImage2Mat(result);
		Mat template = Imgcodecs.imread("C:\\Users\\Costl\\Documents\\GitHub\\CookieBot\\dark buy.JPG"); //change based on computer used
		Mat distance = new Mat();
		Mat mask = new Mat();
		Imgproc.matchTemplate(srcBuy, template, distance, Imgproc.THRESH_BINARY);
		Imgproc.threshold(distance, distance, 0.5, 1.0, Imgproc.THRESH_TOZERO);
		MinMaxLocResult mmr = Core.minMaxLoc(distance);
		
	/*
		BufferedImage resultBuy = findColors(getScreen(), BUYCOLOR, 34); //buycolor
//		BufferedImage result = getScreen();
		Mat srcBuy = BufferedImage2Mat(resultBuy);
//		ShowImages.showWindow(matToBufferedImage(image), "test");
			Mat grayBuy = new Mat();
			ArrayList<Mat> lines = new ArrayList<Mat>();
			Imgproc.cvtColor(srcBuy, grayBuy, Imgproc.COLOR_BGR2GRAY);
			Imgproc.medianBlur(grayBuy, grayBuy, 5);

			Mat rectangles = new Mat();
			Imgproc.HoughCircles(grayBuy, rectangles, Imgproc.HOUGH_GRADIENT, 1.0,
			(double)grayBuy.rows()/16, // change this value to detect circles with different distances to each other
			100.0, 30.0, 50, 200); // change the last two parameters
			// (min_radius & max_radius) to detect larger circles
			for (int x = 0; x < rectangles.cols(); x++) {
				double[] c = rectangles.get(0, x);
				Point center = new Point(Math.round(c[0]), Math.round(c[1]));
				// circle center
				Imgproc.circle(srcBuy, center, 1, new Scalar(0,100,100), 3, 8, 0 );
				// circle outline
				int radius = (int) Math.round(c[2]);
				Imgproc.circle(src, center, radius, new Scalar(255,0,255), 3, 8, 0 );
				points.add(center);
			}
			System.out.println(stringList(points));

			HighGui.imshow("detected circles", src);
			HighGui.waitKey();
	*/
	}
	
	private static <T> String stringList(List<T> list)
	{
		String re = "";
		for(T name : list)
		{
			re += name.toString();
			re+= "\n";
		}
		return re;
	}
}
