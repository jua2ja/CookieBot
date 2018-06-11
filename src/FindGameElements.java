

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;



public class FindGameElements {
	
	
	//different experimentations of color, only CHIPCOLOR is used
	private final static Color COOKIECOLOR = new Color(201, 159, 111); //kind of useless
	private final static Color CHIPCOLOR = new Color(107, 79, 68); //for finding cookies
	private final static Color BUYCOLOR = new Color(143, 140, 132); //for finding buildings, kind of useless right now

	private Mat image;
	
	/** Adds the image to the class, class is image specific to allow for more experimentation*. Used only for testing/
	public FindGameElements(BufferedImage image) {
		this.image = bufferedImageToMat(image);
	}	

	/** default constructor for FindGameElements, uses a screenshot */
	public FindGameElements() {
		this.image = bufferedImageToMat(FindGameElements.getScreen());
	}
	
	
	/**
	 * changes the image stored in the object. Used only for testing
	 * @param the new image in a BufferedImage format
	 */
	public void setImage(BufferedImage image)
	{
		this.image = bufferedImageToMat(image);
	}
	
	/**
	 * updates the image to a new screenshot
	 */
	public void update()
	{
		this.image = bufferedImageToMat(getScreen());
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
	
	/**
	 * uses hough transform to find the location of the cookie 
	 * @return a position of the cookie in Point form
	 */
	public Point findCookie()
	{
		BufferedImage result = findColors(getScreen(), CHIPCOLOR, 120);
		Mat src = bufferedImageToMat(result);

		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(gray, gray, 11);
		
		Mat circles = new Mat();
		Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
		(double)gray.rows()/16, // change this value to detect circles with different distances to each other
		100.0, 30.0, 50, 200); // change the last two parameters
		// (min_radius & max_radius) to detect larger circles
		return new Point(Math.round(circles.get(0, 0)[0]), Math.round(circles.get(0, 0)[1]));
	}
	/**
	 * Uses template detection to find the cursor on the screen
	 * @return a position of the building in the form of a Point
	 */
	public Point findBuilding()
	{
		BufferedImage temp = null;
		try {
			temp = ImageIO.read(new File("data\\example\\cookies\\darkBuy.jpg"));
		} catch (IOException e) {
			System.out.println("File should be found");
			System.exit(1);
		}
		
		Mat template = bufferedImageToMat(temp);
		int cols = image.cols() - template.cols() + 1;
		int rows = image.rows() - template.rows() + 1;
		Mat resultTemplate = new Mat(rows, cols, CvType.CV_32FC1);
		Imgproc.matchTemplate(image, template, resultTemplate, Imgproc.TM_SQDIFF_NORMED);
		Core.normalize(resultTemplate, resultTemplate, 0, 1, Core.NORM_MINMAX, -1, new Mat());
		MinMaxLocResult mmr = Core.minMaxLoc(resultTemplate);
		return new Point((mmr.minLoc.x + 200), (mmr.minLoc.y + 50));
		
	}
	
	/**
	 * draws a point onto the 
	 * @param draw
	 */
	public void drawPoint(Point draw)
	{
        Imgproc.circle(image, draw, 2, new Scalar(255,0,255), 3, 8, 0 );	
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("critical error");
		return null;
	}
	
	/**
	 * focuses and finds colors in an image
	 * @param image the target image
	 * @param find the target color
	 * @param threshhold the maximum euclidean distance before a color counts as "too far from target"
	 * @return a bufferedImage where all desired colors are black and the rest are white
	 */
	public static BufferedImage findColors(BufferedImage image, Color find, double threshhold)
	{
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


	
}
