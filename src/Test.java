


import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.extract.ConfigExtract;
import boofcv.abst.feature.detect.extract.NonMaxSuppression;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.orientation.OrientationIntegral;
import boofcv.alg.feature.describe.DescribePointSurf;
import boofcv.alg.feature.detect.interest.FastHessianFeatureDetector;
import boofcv.alg.transform.ii.GIntegralImageOps;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.factory.feature.describe.FactoryDescribePointAlgs;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.factory.feature.detect.extract.FactoryFeatureExtractor;
import boofcv.factory.feature.orientation.FactoryOrientationAlgs;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.BoofDefaults;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageGray;
import georegression.struct.shapes.EllipseRotated_F64;

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
import java.io.File;
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
		System.out.println("converted");
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

	
	public static void main( String args[] ) {
		List<Point> points = new ArrayList<Point>();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME );
		BufferedImage result = findColors(getScreen(), CHIPCOLOR, 120);
//		BufferedImage result = getScreen();
		Mat src = BufferedImage2Mat(result);

//		ShowImages.showWindow(matToBufferedImage(src), "test");
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		System.out.println("why do you hate me");
		Imgproc.medianBlur(gray, gray, 11);

		
		
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
		
		
		Mat screen = BufferedImage2Mat((BufferedImage) getScreen());
		BufferedImage temp = null;
		try {
			temp = ImageIO.read(new File("data\\example\\cookies\\darkBuy.jpg"));
		} catch (IOException e) {
			System.out.println("File should be found");
			System.exit(1);
		}
		
		Mat template = BufferedImage2Mat(temp);
		int cols = screen.cols() - template.cols() + 1;
		int rows = screen.rows() - template.rows() + 1;
		Mat resultTemplate = new Mat(rows, cols, CvType.CV_32FC1);
		Imgproc.matchTemplate(screen, template, resultTemplate, Imgproc.TM_SQDIFF_NORMED);
		Core.normalize(resultTemplate, resultTemplate, 0, 1, Core.NORM_MINMAX, -1, new Mat());
		MinMaxLocResult mmr = Core.minMaxLoc(resultTemplate);
		Point building = mmr.minLoc;
		Imgproc.rectangle(screen, building, new Point(building.x + 
				template.cols(), building.y + template.rows()), new Scalar(0, 255, 0));
		HighGui.imshow("detected building", screen);
		HighGui.waitKey();
		
		
		
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
