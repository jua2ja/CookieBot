
public class Building {

	double cost;
	double CPS;
	double upgradeModifier;
	
	public Building(double inputCost, double inputCPS, double inputUpgradeModifier) {
		cost = inputCost;
		CPS = inputCPS;
		upgradeModifier = inputUpgradeModifier;
	}

	public double returnCPS() {
		return CPS*upgradeModifier;
	}
	
	public double returnCost() {
		return cost;
	}
	
	public void buy() {
		cost *= 1.15;
	}
	
	public void changeModifier(double inputUpgradeModifier) {
		upgradeModifier = inputUpgradeModifier;
	}
	
	public int possibleBuys(long cookies, double totalCPS) {
		double tempCost = cost;
		int maxBuys = 0;
		int timeLeft = 300;
		while(timeLeft*totalCPS > tempCost) {
			tempCost *= 1.15;
			totalCPS += CPS;
			maxBuys++;
		}
		return maxBuys;
	}
}
