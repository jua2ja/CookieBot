
import java.util.ArrayList;

public class Logic {

	private final int clickingEfficiency = 10;
	private int[] buildingCount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private double[] buildingCosts = {15L, 100L, 500L, 3000L, 10000L, 40000L, 200000L, 1666666L, 123456789L, 3999999999L};
	private double[] buildingCPS = {0.1, 0.5, 2, 10, 40, 100, 400, 6666, 98765, 999999};
	private ArrayList<Integer> nextBuys = new ArrayList<Integer>();
	private ArrayList<Double> nextTimes = new ArrayList<Double>();
	/*private int cookieTypes1 = 0;
	private int cookieTypes2 = 0;
	private int cookieTypes3 = 0;
	private int catUpgrades = 0;
	private double milkPercent = 0; //for milk since it increases CPS, increased by 4% per achievement unlocked
	*/
	
	public Logic() {
		
	}
	
	public int[] decideBuy(long cookies) {
		double CPS = 0;
		for(int a = 0; a < 10; a++) {
			CPS += buildingCPS[a]*buildingCount[a];
		}
		CPS += clickingEfficiency;
		int[] bestBuys = new int[8];
		for(int a = 0; a < 8; a++) bestBuys[a] = 0;
//		bestBuys = testAllBuys(cookies, CPS, bestBuys);
//		cookiesCreated = 0;
		return bestBuys;
	}
	
	public void buy(int whichBuilding) {
		buildingCosts[whichBuilding] *= 1.15;
		buildingCount[whichBuilding]++;
	}
	
	private double calcTimeTillBuy(int whichBuilding, long cookies, double CPS) {
		double timeTillBuy = (buildingCosts[whichBuilding] - cookies)/CPS;
		if(timeTillBuy < 0) timeTillBuy = 0;
		return timeTillBuy;
	}
	
	private int mostExpensiveAvailable(double timeLeft, long cookies, double CPS) {
		int mostExpensive = -1;
		for(int a = 0; a < 8; a++) {
			if(calcTimeTillBuy(a, cookies, CPS) < timeLeft) {
				mostExpensive = a;
			}
		}
		return mostExpensive;
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
	
	/*
	 New Plan:
	 method decideBuy runs testAllBuys inputing the necessary information
	 testAllBuys branches:
	 	Whenever testAllBuys runs, if there are no buys available, it returns the total increase of cookies by going down this route
	 	When there are buys available, testAllBuys branches to all possible buys within the remaining time
	 	After the branching fully finishes, the recursion ends and totaling is done
	 	Each iteration of testAllBuys takes in the most efficient of the testAllBuys further down the branch and then adds to the global arrayList of the best buy
	 */
	
	public long testAllBuys(long cookies, double CPS, int[] buysSoFar, int[] updatedBuildingCosts, double timeLeft, int biggestBuy, long cookiesCreated) {
		int bestNextBuy = 0; //check this whole thing, very complicated
		boolean buyAvailable = false;
		long mostCookies = 0;
		for(int a = 0; a < 8; a++) {
			if(mostExpensiveAvailable(timeLeft, cookies, CPS) != -1) buyAvailable = true;
		}
		if(buyAvailable == true) {
			updatedBuildingCosts[biggestBuy] *= 1.15;
			CPS += buildingCPS[biggestBuy];
			//mostCookies = testAllBuys(cookies, CPS, buysSoFar, updatedBuildingCosts, timeLeft, biggestBuy, cookiesCreated);
			int mostExpensive = mostExpensiveAvailable(timeLeft, cookies, CPS);
			for(int a = biggestBuy+1; a <= mostExpensive; a++) {
				/*updatedBuildingCosts[a-1] /= 1.15;
				updatedBuildingCosts[a] *= 1.15;
				CPS -= buildingCPS[a-1];
				CPS += buildingCPS[a];
				if(mostCookies < testAllBuys(cookies, CPS, buysSoFar, updatedBuildingCosts, timeLeft, biggestBuy, cookiesCreated)) {
					mostCookies = testAllBuys(cookies, CPS, buysSoFar, updatedBuildingCosts, timeLeft, biggestBuy, cookiesCreated);
					bestNextBuy = a;
				}*/
				
				
			}
			timeLeft -= calcTimeTillBuy(bestNextBuy, cookies, CPS);
			double time = calcTimeTillBuy(bestNextBuy, cookies, CPS);
			if(time == 0) {
				cookies -= updatedBuildingCosts[bestNextBuy];
			}
			else cookies = 0;
			nextBuys.add(bestNextBuy);
			nextTimes.add(time);
		}
		return cookiesCreated; // put temporarily so I can run other stuff
	}
	
}
