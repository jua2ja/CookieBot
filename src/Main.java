

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;

import com.sun.glass.events.KeyEvent;

import boofcv.gui.image.ShowImages;




public class Main {

	
	public static final int buildingDistance = 95;
	public static Point cookie;
	public static Point building;
	public static Robot mouse;
	public static Logic buy;
	private static long cookies = 0;
	private static boolean done = false;
	private static Point upgrade = new Point();

	
	public static void main(String[] args) {
		
		//used so I can use screen.getWidth()
		BufferedImage screen = FindGameElements.getScreen();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME );
		boolean running = false;
		CookieGUI gui = new CookieGUI();
		gui.displayBot();
		
		//waits until button is clicked to start robot. Does no initialize until clicked
		while(!running)
		{
			try {TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
			running = gui.isRunning();
		}
		
		//actual bot starts here
		FindGameElements find = new FindGameElements(); //uses the default constrctor that takes a picture of the current screen
		boolean again = true;
		int count = 0;

		//initializes the mouse
		mouse = null;
		try {
			mouse = new Robot();
		} catch (AWTException e) {
			System.out.println("critical error");
			System.exit(1);
		}

		mouse.mouseMove(0, 0);
		
		//tries to find the cookie until a possible location is found.
		cookie = null;
		while(again) {			
			if(count >= 100)
			{
				System.out.println("Not finding the cookie, exiting the program");
				System.exit(1);
			}
			count++;
			find.setImage(FindGameElements.getScreen());
			try {
			cookie = find.findCookie();
				if(cookie.x < screen.getWidth() / 2)
				{
					again  = false;				
					System.out.println("found cookie in an impossible location... trying again");
				}
			}
			catch(NullPointerException ex){System.out.println("failed to find cookie... trying again");}
		}
		
		count = 0;
		//finds buildings, confirming that the location is possible
		do
		{
			if(count >= 100)
			{
				System.out.println("Not finding the building, exiting the program");
				System.exit(1);
			}
			count++;
			find.setImage(FindGameElements.getScreen());
			building = find.findBuilding();
			if(building.x < screen.getWidth() / 2)
				System.out.println("found building in an impossible location... trying again");
		}while(building.x < screen.getWidth() / 2);
		
		
		System.out.println(cookie.toString());
		System.out.println(building.toString());
		System.out.println(screen.getWidth());
				
		
		Point[] positions = new Point[8];
		positions[0] = building;
		for(int i = 1; i < positions.length; i++)
			positions[i] = new Point(building.x, building.y + buildingDistance * i);
		
		find.drawPoint(cookie);
		for(Point building : positions)
			find.drawPoint(building);
		
		buy = new Logic(positions);
		
		for(int a = 0; a < 20; a++) {
			click(cookie);
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("Failed to sleep for a millisecond... this should never happen");
			}
		}
		
		click(positions[0]);
		
		buy.buy(0);
		
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e) {
			System.out.println("Failed to sleep for a millisecond... this should never happen");
		}
		
		find.setImage(FindGameElements.getScreen());
		
		upgrade = find.findUpgrade();
		
		//runs main loops of the program
		while(true)
			loop();
		
		
	}
	/**
	 * main loop for the game
	 * @param mouse object for the screen device
	 */
	public static void loop()
	{
		if((System.currentTimeMillis() / 1000L) % 10 == 0 && done)
		{
			if(buy.upgradeAvailable(cookies))
			{
				click(upgrade);
			}
			else
			{
				cookies += (int)(buy.getCPS());
				Building temp = buy.buyBestAvailable(cookies);
				if(temp != null)
				{
					for(int i = 0; i < temp.maxBuys(cookies); i++)
					{
						click(temp.getPosition());
						cookies -= temp.buy(1);
					}
					System.out.println(temp.toString() + ", you also have " + cookies  + " cookies by internal calculations");
					System.out.println(cookies);
					System.out.println(buy.getCPS());
				}
				else
					System.out.println("no building can be bought");
			}
			done = false;
		}
		
		if((System.currentTimeMillis() / 1000L) % 10 == 1 && !done)
			done = true;
		
		click(cookie);
		cookies += buy.cookieValue;
		
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			System.out.println("Failed to sleep for a millisecond... this should never happen");
		}
	}
	
	/**
	 * simple clicking function. Moves mouse prior to clicking
	 * @param mouse object for the screen device that needs to be clicked
	 * @param location location of the click WHY IS THIS BROKEN FOR HIGH X VALUES
	 */
	public static void click(Point location)
	{       
		int count = 0;
		do {
			mouse.mouseMove((int)location.x, (int)location.y);
			count++;
			if(count >= 100)
			{
				System.out.println("Crashed due to a known bug with robot. There is nothing I can do about it");
				System.exit(1);
			}
//			Here so I can display stuff if it decides to break;
//			System.out.print(location.toString() + " | ");
//			System.out.println(MouseInfo.getPointerInfo().getLocation().getX() + " , "
//			        + MouseInfo.getPointerInfo().getLocation().getY());
		}while(!(MouseInfo.getPointerInfo().getLocation().getX() - location.x < 20 && MouseInfo.getPointerInfo().getLocation().getX() - location.x > -20 && MouseInfo.getPointerInfo().getLocation().getY() - location.y < 20 && MouseInfo.getPointerInfo().getLocation().getY() - location.y > -20));
		click();
	}
	
	/**
	 * simple clicking function
	 * @param mouse: Robot object
	 */
	public static void click()
	{
		mouse.mousePress(InputEvent.BUTTON1_MASK);
		mouse.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	

}
