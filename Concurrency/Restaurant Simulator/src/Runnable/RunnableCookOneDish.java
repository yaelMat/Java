package Runnable;

import java.util.concurrent.CountDownLatch;

import Restaurant.Dish;
import Warehouse.WarehouseImpl;

/**
 * This class manage the cooking of one dish.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */

public class RunnableCookOneDish implements Runnable {

	private final Dish dishToCook;
	private final WarehouseImpl warehouse;
	private final double chefEfficiency;
	private final CountDownLatch countDown;

	

	/**
	 * Constructor
	 * 
	 * @param dishToCook: The dish this need to cook 
	 * @param warehouse: warehouse from the Managment
	 * @param chefEfficiency: The chef's who cook that dish efficiency 
	 * @param count: the countDown from the "CallableCookWholeOrder",
	 * countDown the cooked dishes.
	 */
	public RunnableCookOneDish(Dish dishToCook, WarehouseImpl warehouse, 
			double chefEfficiency ,CountDownLatch count)
	{
		this.dishToCook = dishToCook;
		this.warehouse = warehouse;
		this.chefEfficiency = chefEfficiency;
		this.countDown = count;
		
	}

	/**
	 * running method. 
	 * take the tools and ingredients, sleep during cooking time and return tools.
	 */

	@Override
	public void run() 
	{
		this.dishToCook.aquireToolsAndIngredients(warehouse);
		
		//calculate cooking time and sleep during the cooking
		long sleepingTime = this.dishToCook.computCookTime(chefEfficiency);
		try {
			Thread.sleep(sleepingTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//return tools
		this.dishToCook.returnTools(warehouse);
		this.countDown.countDown();

	}


}

