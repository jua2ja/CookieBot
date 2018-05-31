package src;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import boofcv.alg.feature.detect.template.TemplateMatching;
import boofcv.factory.feature.detect.template.FactoryTemplateMatching;
import boofcv.factory.feature.detect.template.TemplateScoreType;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.Match;
import boofcv.struct.image.GrayF32;


public class FindGameElements {
	
	
	private final static Color COOKIECOLOR = new Color(201, 159, 111); //kind of useless
	private final static Color CHIPCOLOR = new Color(107, 79, 68); //for finding cookies
	private final static Color BUYCOLOR = new Color(143, 140, 132); //for finding buildings

	private Mat image;
	
	/** Adds the image to the class, class is image specific to allow for more experimentation*/
	public FindGameElements(BufferedImage image) {
		this.image = bufferedImageToMat(image);
	}	

	/** default constructor for FindGameElements, uses a screenshot */
	public FindGameElements() {
		this.image = bufferedImageToMat(FindGameElements.getScreen());
	}
	
	public void setImage(BufferedImage image)
	{
		this.image = bufferedImageToMat(image);
	}
	/**
	 * displays the picture that is contained in FindGameElements
	 */
	public void display()
	{
		HighGui.destroyAllWindows();
		HighGui.imshow("display image", image);
		HighGui.waitKey();
	}
	
	/*
	 * A collection of static functions used for general utilities unrelated to the image. 
	 */
	
	/**
	 * A static method that uses the standard AWT method to take a picture of the screen using java.awt.Robot
	 * @return a BufferdImage format picture of the screen
	 */
	public static BufferedImage getScreen()
	{
	BufferedImage screenPicture;
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice screenDevice = ge.getDefaultScreenDevice();
	GraphicsConfiguration gc = screenDevice.getDefaultConfiguration();
	Robot screenDeviceShot = null;
	//standard screenshot taking with java.awt.Robot
	try {screenDeviceShot = new Robot(screenDevice);} catch (AWTException e) {return null;}
	screenPicture = screenDeviceShot.createScreenCapture(gc.getBounds());

	return screenPicture;
	}

	
	/**
	 * A helper static method that calculates the euclidean distance between two Color objects
	 * @param check a color in an image 
	 * @param target a predetermined color that has itself compared to check
	 * @return the euclidean distance between the two colors, calculated by the usual formula of the square roots of the sum of the squares of the distances
	 */
	private static double colorDistance (Color check, Color target)
	{
		return Math.sqrt(((check.getRed() - target.getRed()) * (check.getRed() - target.getRed())) + ((check.getGreen() - target.getGreen()) * (check.getGreen() - target.getGreen())) + ((check.getBlue() - target.getBlue()) * (check.getBlue() - target.getBlue())));
	}

	/**
	 * A helper static method that converts a BufferdImage object to a Mat object
	 * @param image a valid BufferedImage object with a height and width of at least 1. 
	 * @return a Mat object that has the contents of image
	 */
	public static Mat bufferedImageToMat(BufferedImage image) 
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "jpg", byteArrayOutputStream);
		byteArrayOutputStream.flush();
		return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
		} catch (IOException e) {e.printStackTrace();}
		return null;
	}

	
}
