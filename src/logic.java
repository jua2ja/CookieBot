package src;

public class logic {

	private double CPS = 0;
	private int clickingEfficiency = 10;
	private double[] buildingCount = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private double[] buildingCosts = {15L, 100L, 500L, 3000L, 10000L, 40000L, 200000L, 1666666L, 123456789L, 3999999999L};
	private double[] buildingCPS = {0.1, 0.5, 2, 10, 40, }; //only verified first 5
	
	public logic() {
		
	}
	
	public int decideBuy(long cookies) {
		for(int a = 0; a < 10; a++) {
			CPS += buildingCPS[a]*buildingCount[a];
		}
		CPS += clickingEfficiency;
		int bestBuy = 0;
		double timeTillBuy = calcTimeTillBuy(0, cookies);
		for(int a = 1; a < 10; a++) { //chooses the buy with the best gains 5 minutes from now
			if(300*buildingCPS[bestBuy] < (300-calcTimeTillBuy(a, cookies))*buildingCPS[a]) {
				bestBuy = a;
			}
		}
		return bestBuy;
	}
	
	public void buy(int whichBuilding) {
		buildingCosts[whichBuilding] *= 1.15;
	}
	
	private double calcTimeTillBuy(int whichBuilding, long cookies){
		double timeTillBuy = (buildingCosts[whichBuilding] - cookies)/CPS;
		if(timeTillBuy < 0) {
			timeTillBuy = 0;
		}
		return timeTillBuy;
	}

}
