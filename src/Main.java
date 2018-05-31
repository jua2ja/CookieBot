package src;

import java.awt.Robot;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Point;


public class Main {

	public static void main(String[] args) {
		long cookies = 0;
		int[] buildingCounts = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		long[] buildingCosts = {15L, 100L, 500L, 3000L, 10000L, 40000L, 200000L, 1666666L, 123456789L, 3999999999L};
		double[] buildingProduction = {0.1, 1, 8, 47, 260};
		boolean running = false;
		CookieGUI gui = new CookieGUI();
		FindGameElements find = new FindGameElements(); //uses the default constructor that takes a picture of the current screen
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
		FindGameElements vision = new FindGameElements();
	}
		
	public void bestBuy() {
		
	}
	
	public void buyAvailable() {
		
	}
}
