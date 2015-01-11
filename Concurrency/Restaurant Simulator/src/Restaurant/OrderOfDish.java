package Restaurant;
import java.util.concurrent.CountDownLatch;

import Runnable.RunnableCookOneDish;
import Warehouse.WarehouseImpl;
/**
 * This class represents a dish's order.
 * Managing cooking to dish.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */

public class OrderOfDish {
	private final Dish m_fdsDish;
	private final int m_fnQuantity;
	
	/**
	 * Constructor.
	 * 
	 * @param dish: the dish in the order.
	 * @param quantity: quantity of the dish in the order.
	 */
	public OrderOfDish(Dish dish, int quantity)
	{
		this.m_fdsDish = dish;
		this.m_fnQuantity = quantity;
	}

	/**
	 * Create threads of "RunnableCookOneDish" for each dish, according to this.quantity.
	 * 
	 * @param chefEfficiency: Cooking chef efficiency
	 * @param warehouse: The main warehouse from Managment
	 * @param countDown: CountDown the cooked dishes.
	 */
	public void cookOrderOfDish(double chefEfficiency,WarehouseImpl warehouse, 
			CountDownLatch countDown)
	{
		// For each quantity, cooks a dish
		for(int i=0;i<this.m_fnQuantity;i++){
			Thread cookTread = new Thread(
					new RunnableCookOneDish(this.m_fdsDish, warehouse, chefEfficiency ,countDown));
			cookTread.start();
		}
	}

	/**
	 * @return this dish quantity.
	 */
	public int getQuantity()
	{
		return this.m_fnQuantity;
	}
	
	/**
	 * @return number of tools needed to cook this dish.
	 */
	public int toolCounter()
	{
		return (this.m_fdsDish.getNumberOfTools()*this.m_fnQuantity);
	}

	/**
	 * @return Difficulty of this dish by using 
	 * this.dish.calculateDishDifficulty
	 */
	public double calculateDishDifficulty()
	{
		return (this.m_fdsDish.getDishDifficulty());
	}

	/**
	 * @param toCompar: orderOfDish for compar cooking time,
	 * @return Cooking time of the longest one to cook.
	 */
	public long comparCookTime(OrderOfDish toCompar)
	{
		return this.m_fdsDish.comperCookingTime(toCompar.m_fdsDish);
	}

	/**
	 * @return Total reward for this dish.
	 */
	public double getRewardForDishInTheOrder()
	{
		return (this.m_fdsDish.getRewardForDishInTheOrder(this.m_fnQuantity));
	}

	/**
	 * toString override
	 */
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		if (m_fdsDish != null) {
			builder.append("dish  ");			
			builder.append(m_fdsDish.toString());
			builder.append(", quantity=");
			builder.append(m_fnQuantity);
		}
		builder.append("]");
		return builder.toString();
	}
}
