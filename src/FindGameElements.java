import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.List;

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
	
	GrayF32 desktop;
	String directory = UtilIO.pathExample("cookies");
	
	/** Initializes most of the necessary variables for the computer to see the screen
	 *  Main purpose of this constructor is to take a screenshot and store it into a Mat, used in opencv
	 *  If anything fails along the way, terminates the program. Program can't run without a picture
	 */
	public FindGameElements() {
		//declaring temporary variables. These will not be necessary in the future so left in constructor
		//most variables declared with items from the screen
		BufferedImage screenPicture;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screenDevice = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = screenDevice.getDefaultConfiguration();
		Robot screenDeviceShot = null;
		desktop = null;
		//standard screenshot taking with java.awt.Robot
		try {screenDeviceShot = new Robot(screenDevice);} catch (AWTException e) {System.exit(1);}
		screenPicture = screenDeviceShot.createScreenCapture(gc.getBounds());
		desktop = ConvertBufferedImage.convertFrom(screenPicture, desktop);
	}	
	
	public void displayImage()
	{
		ShowImages.showWindow(desktop, "Desktop",true);
	}
	
	public void findCookie()
	{
		BufferedImage output = new BufferedImage(desktop.width, desktop.height, BufferedImage.TYPE_INT_BGR);
		ConvertBufferedImage.convertTo(desktop, output);
		Graphics2D g2 = output.createGraphics();
		GrayF32 templateCookie = UtilImageIO.loadImage(directory , "cookie.jpg", GrayF32.class);
		//FAILS TO LOAD TEMPLATE COOKIE!!!!
		g2.setColor(Color.BLUE); g2.setStroke(new BasicStroke(2));
		drawRectangles(g2, templateCookie, null, 1);
		ShowImages.showWindow(output, "Found Matches",true);
	}

	/**
	 * Helper function will is finds matches and displays the results as colored rectangles
	 */
	private void drawRectangles(Graphics2D g2, GrayF32 template, GrayF32 mask, int expectedMatches) {
		List<Match> found = findMatches(template, mask, expectedMatches);

		int r = 2;
		int w = template.width + 2 * r;
		int h = template.height + 2 * r;

		for (Match m : found) {
			System.out.println("Match "+m.x+" "+m.y+"    score "+m.score);
			// this demonstrates how to filter out false positives
			// the meaning of score will depend on the template technique
//			if( m.score < -1000 )  // This line is commented out for demonstration purposes
//				continue;

			// the return point is the template's top left corner
			int x0 = m.x - r;
			int y0 = m.y - r;
			int x1 = x0 + w;
			int y1 = y0 + h;

			g2.drawLine(x0, y0, x1, y0);
			g2.drawLine(x1, y0, x1, y1);
			g2.drawLine(x1, y1, x0, y1);
			g2.drawLine(x0, y1, x0, y0);
		}
	}
	
	private List<Match> findMatches(GrayF32 template, GrayF32 mask, int expectedMatches) {
		// create template matcher.
		TemplateMatching<GrayF32> matcher = 
				FactoryTemplateMatching.createMatcher(TemplateScoreType.SUM_DIFF_SQ, GrayF32.class);
		
		// Find the points which match the template the best
		matcher.setImage(desktop);
		matcher.setTemplate(template, mask,expectedMatches);
		matcher.process();
		return matcher.getResults().toList();
}
	
	public GrayF32 getDesktop(){return desktop;}		
}
