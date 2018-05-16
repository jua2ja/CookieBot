import java.awt.Robot;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Point;

public class Main {

	public static void main(String[] args) {
		boolean running = false;
		CookieGUI gui = new CookieGUI();
		FindGameElements find = new FindGameElements(); //uses the default constrctor that takes a picture of the current screen
		gui.displayBot();
		
		//waits until button is clicked to start robot
		while(!running)
		{
			try {TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
			running = gui.isRunning();
		}
		
		
		
	}
	
	public static void click(Robot mouse, Point location)
	{
		
	}
		
}
