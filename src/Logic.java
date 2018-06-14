
import java.util.ArrayList;

import org.opencv.core.Point;

public class Logic {

	private final int clickingEfficiency = 80;
	private int[] buildingCount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private Building[] buildings = new Building[8];
	private double[] buildingCosts = {15L, 100L, 500L, 3000L, 10000L, 40000L, 200000L, 1666666L, 123456789L, 3999999999L};
	private double[] buildingCPS = {0.1, 0.5, 2, 10, 40, 100, 400, 6666, 98765, 999999};
	private String[] buildingNames = {"Cursor", "Grandma", "Farm", "Factory", "Mine", "Shipment", "Alchemy Lab", "Portal"};
	private ArrayList<Integer> nextBuys = new ArrayList<Integer>();
	private ArrayList<Double> nextTimes = new ArrayList<Double>();
	
	public double cookieValue = 1;
	private double milkPercent = 0; //for milk since it increases CPS, increased by 4% per achievement unlocked

	
	/*private int cookieTypes1 = 0;
	private int cookieTypes2 = 0;
	private int cookieTypes3 = 0;
	private int catUpgrades = 0;
	*/
	
	public Logic(Point[] positions) {
		for(int a = 0; a < 8; a++) {
			buildings[a] = new Building(buildingCosts[a], buildingCPS[a], 1, positions[a], buildingNames[a],  a);
		}
	}
	
	public void buy(int whichBuilding) {
		buildings[whichBuilding].buy(1);
	}
	
	public Building buyBestAvailable(long cookies)
	{
		Building best = buildings[0];
		for(Building build : buildings)
			if(build.maxCPSBuy(cookies) > best.maxCPSBuy(cookies))
				best = build;
		return best;
	}
	
	public boolean upgradeAvailable(long cookies)
	{
		return false;
	}
	
	public double getCPS()
	{ 
		double cps = 0;
		for(Building build : buildings)
			cps+= build.returnTotalCPS();
		cps+=clickingEfficiency;
		return cps;
	}

}
