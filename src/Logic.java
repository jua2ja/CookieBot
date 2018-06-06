
import java.util.ArrayList;

public class Logic {

	private double CPS = 0;
	private final int clickingEfficiency = 10;
	private int[] buildingCount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private double[] buildingCosts = {15L, 100L, 500L, 3000L, 10000L, 40000L, 200000L, 1666666L, 123456789L, 3999999999L};
	private double[] buildingCPS = {0.1, 0.5, 2, 10, 40, 100, 400, 6666, 98765, 999999}; //only verified first 8
	/*private int cookieTypes1 = 0;
	private int cookieTypes2 = 0;
	private int cookieTypes3 = 0;
	private int catUpgrades = 0;
	private double milkPercent = 0; //for milk since it increases CPS, increased by 4% per achievement unlocked
	*/
	public Logic() {
		
	}
	
	public int[] decideBuy(long cookies) {
		for(int a = 0; a < 10; a++) {
			CPS += buildingCPS[a]*buildingCount[a];
		}
		CPS += clickingEfficiency;
		int[] bestBuys = new int[8];
		for(int a = 0; a < 8; a++) bestBuys[a] = 0;
		bestBuys = testAllBuys(cookies, CPS, bestBuys);
		cookiesCreated = 0;
		return bestBuys;
	}
	
	public void buy(int whichBuilding) {
		buildingCosts[whichBuilding] *= 1.15;
		buildingCount[whichBuilding]++;
	}
	
	private double calcTimeTillBuy(int whichBuilding, long cookies) {
		double timeTillBuy = (buildingCosts[whichBuilding] - cookies)/CPS;
		if(timeTillBuy < 0) timeTillBuy = 0;
		return timeTillBuy;
	}

	/*
	 Plan:
	 method decideBuy runs the first testAllBuys
	 testAllBuys takes in the time left, the current cookies, the cookies per second, what has been bought so far, 
	 	the biggest buy and the cookies created through this strategy.
	 testAllBuys recurses by having a for loop which has it attempt to buy each building of equal or higher CPS than those bought before and run a new testAllBuys
	 This is because there is no situation in which a cheaper thing to buy would be better to buy after a more expensive one
	 testAllBuys returns the buys so far when there are no other possible buys within the time
	 */
	
	public static int[] testAllBuys(long cookies, double CPS, int[] buysSoFar, long additionalCookiesCreated, double timeLeft, int biggestBuy) {
		int[] buys = buysSoFar;
		int[] additionalCookiesCreated = new int[8];
		return ;
	}
	
}
