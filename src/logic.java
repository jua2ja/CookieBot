
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
	
	
	
	public void buyMostExpensive(long cookies) {
		double CPS = 0;
		for(int a = 0; a < 8; a++) {
			CPS += buildingCPS[a]*buildingCount[a];
		}
		int whichBuilding = mostExpensiveAvailable(cookies, CPS);
		buildingCosts[whichBuilding] *= 1.15;
		buildingCount[whichBuilding]++;
	}
	
	private double calcTimeTillBuy(int whichBuilding, long cookies, double CPS) {
		double timeTillBuy = (buildingCosts[whichBuilding] - cookies)/CPS;
		if(timeTillBuy < 0) timeTillBuy = 0;
		return timeTillBuy;
	}
	
	private int mostExpensiveAvailable(long cookies, double CPS) {
		int mostExpensive = -1;
		for(int a = 0; a < 8; a++) {
			if(calcTimeTillBuy(a, cookies, CPS) < 300) {
				mostExpensive = a;
			}
		}
		return mostExpensive;
	}
	
}
