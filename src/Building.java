import org.opencv.core.Point;

public class Building {

	double cost;
	double CPS;
	double upgradeModifier;
	int ID;
	String name;
	int quantity = 0;
	Point position;
	
	public Building(double inputCost, double inputCPS, double inputUpgradeModifier, Point position, String name, int ID) {
		cost = inputCost;
		CPS = inputCPS;
		upgradeModifier = inputUpgradeModifier;
		this.name = name;
		this.ID = ID;
		this.position = position;
	}

	public double returnTotalCPS() {
		return getSingleCPS() * quantity;
	}
	
	public double getSingleCPS() {
		return CPS*upgradeModifier;
	}
	
	public double returnCost() {
		return cost;
	}
	
	public int getQuantity()
	{
		return quantity;
	}
	
	public int buy(int amount) {
		int total = 0;
		for(int i = 0; i < amount; i++)
		{
			total += cost;
			cost *= 1.15;
			quantity++;
		}
		return total;
	}
	
	public void changeModifier(double inputUpgradeModifier) {
		upgradeModifier = inputUpgradeModifier;
	}

	/**
	 * calculates the most CPS that can be acquired from 1 building if you buy all available
	 * @param cookies
	 * @return the CPS that can be gained
	 */
	public double maxCPSBuy(long cookies) {
		long tempCookies = cookies;
		double tempCPS = 0;
		double tempCost = cost;
		while(tempCookies > tempCost) {
			tempCookies -= tempCost;
			tempCost *= 1.15;
			tempCPS += getSingleCPS();
		}
		
		return tempCPS;// - getSingleCPS();
	}
	
	
	/**
	 * calculates the max amounts of buys with a certain amount of money
	 * @param cookies
	 * @return the max amount of building that can be bought
	 */
	public int maxBuys(long cookies)
	{
		double tempCost = cost;
		int maxBuys = 0;
		while(cookies > tempCost) {
			tempCost *= 1.15;
			maxBuys++;
		}
		
		return maxBuys;
	}
	
	//used in the console for debug information
	public String toString()
	{
		return name + " has ID " + ID + " and you have " + getQuantity() + " of this building that get you " + returnTotalCPS() + " cps";
	}
	
	//used for identification
	public int getID() 
	{
		return ID;
	}
	
	public Point getPosition()
	{
		return position;
	}
}
