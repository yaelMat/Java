package Restaurant;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import System.Point;
import System.TimeManage;
import Warehouse.WarehouseImpl;
/**
 * This class represents an order.
 * Helping in the cooking process and sending information. 
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class Order implements Comparable<Order>{
	private final String m_fstrID;
	private final double m_fdDifficulty;
	private final ArrayList<OrderOfDish> m_farrlstOrdersOfDishes;
	private final Point m_fpntCustomerAddress;
	private final TimeManage m_ftmTime;
	private final long m_flExpectedCookingTime;
	private final int numberOfDishes;
	private final double m_fdOrderReward;
	private OrderStatus m_enmStatus;
	private int m_nNumToolNeeded;
	private Logger m_logOrderLogger = Logger.getLogger(Order.class.getName());

	public enum OrderStatus
	{
		INCOMPLETE,
		IN_PROGRESS,
		COMPLETE,
		DELIVERED
	}

	/**
	 * constructor
	 * @param String: order Id
	 * @param Point: address to deliver the order
	 * @param ArrayList<OrderOfDish>: orderOfDish in the order
	 */
	public Order(String name, Point address, ArrayList<OrderOfDish> ordersOfDish)
	{
		this.m_fstrID = name;
		this.m_enmStatus = OrderStatus.INCOMPLETE;
		this.m_farrlstOrdersOfDishes = ordersOfDish;
		this.m_fpntCustomerAddress = address;
		this.m_ftmTime = new TimeManage();
		this.m_flExpectedCookingTime = findLongestToCooktDish();
		this.m_fdOrderReward = getRewardForDishInTheOrder();
		this.m_fdDifficulty = calculateOrderDifficulty();
		this.numberOfDishes = getNumOfDishes();
		this.m_nNumToolNeeded = getNumToolsNeeded();
	}

	/**
	 * calculate the difficulty of the total order by using, for each dish,
	 * the function calculateDishDifficulty.
	 * @return double: order's difficulty.
	 */
	private double calculateOrderDifficulty()
	{
		double totalDifficulty=0;
		if (this.m_farrlstOrdersOfDishes != null)
		{
			// For each tool sums the difficulty 
			for(int i=0;i<this.m_farrlstOrdersOfDishes.size();i++){
				totalDifficulty = totalDifficulty + (this.m_farrlstOrdersOfDishes.get(i).calculateDishDifficulty());
			}
		}
		return totalDifficulty;
	}

	/**
	 * @return int: number of tools needed to cook the order.
	 */
	private int getNumToolsNeeded()
	{
		int toolsCount = 0;
		if (this.m_farrlstOrdersOfDishes != null)
		{
			// for each dish counts the number of tools 
			for(int i=0;i<this.m_farrlstOrdersOfDishes.size();i++){
				toolsCount = toolsCount + this.m_farrlstOrdersOfDishes.get(i).toolCounter();
			}
		}
		return toolsCount;
	}

	/**
	 * @return double: return the total reward from all the dishes
	 */
	private double getRewardForDishInTheOrder()
	{
		double totalReward=0;
		if (m_farrlstOrdersOfDishes != null)
		{
			// For each dish sums the reward
			for(int i=0;i<this.m_farrlstOrdersOfDishes.size();i++){
				totalReward = totalReward + (this.m_farrlstOrdersOfDishes.get(i).getRewardForDishInTheOrder());
			}
		}
		return totalReward;
	}

	/**
	 * @return find the difficulty of the hardest dish
	 */
	private long findLongestToCooktDish()
	{
		long longestCookingTime  = -1;
		if (this.m_farrlstOrdersOfDishes != null)
		{
			OrderOfDish longestToCook = this.m_farrlstOrdersOfDishes.get(0);
			longestCookingTime = longestToCook.comparCookTime(longestToCook);

			// Goes over the dishes and saves the maximum cook time
			for(int i=1;i<this.m_farrlstOrdersOfDishes.size();i++){
				long tempTime = longestToCook.comparCookTime(this.m_farrlstOrdersOfDishes.get(i));

				// If the current time bigger than the previous time, saves it
				if(longestCookingTime<tempTime){
					longestCookingTime = tempTime;
					longestToCook = this.m_farrlstOrdersOfDishes.get(i);
				}
			}
		}
		return longestCookingTime;
	}

	/**
	 *  get the total quantity of dishes need to be cook in this order.
	 * @return int: number of dishes in the order.
	 */
	public int getNumOfDishes()
	{
		int countNumOfDishes = 0;
		if(this.m_farrlstOrdersOfDishes!=null){
			// Goes over the dishes and sums the total dishes to cook
			for(int i=0;i<this.m_farrlstOrdersOfDishes.size();i++){
				countNumOfDishes = (countNumOfDishes + this.m_farrlstOrdersOfDishes.get(i).getQuantity());
			}
		}
		return countNumOfDishes;
	}

	/**
	 * calculate the new pressure after adding difficulty. 
	 * @param int: chef pressure.
	 * @return int: new pressure.
	 */
	public int calculatePressureToAdd(int chefPressure)
	{
		return (int)(chefPressure+m_fdDifficulty);
	}

	/**
	 * calculate the new pressure after reducing difficulty. 
	 * @param int: chef pressure.
	 * @return int: new pressure.
	 */
	public int calculatePressureToReduce(int chefPressure)
	{
		return (int)(chefPressure-m_fdDifficulty);
	}

	/**
	 * manage the order's cooking.
	 * change status, start time counting and for each orderOfDish, start cooking.
	 * @param int: cooking chef efficiency
	 * @param WarehouseImpl: warehouse
	 * @param countDownLatch: countDown the cooked dishes.
	 */
	public void cookOrder(double chefEfficiency, 
			WarehouseImpl warehouse, CountDownLatch countDown)
	{
		// The order starts cooking
		this.m_enmStatus = OrderStatus.IN_PROGRESS;
		this.m_ftmTime.startCooking();

		// Goes over the dishes and cooks them
		for(int i=0;i<this.m_farrlstOrdersOfDishes.size();i++){
			this.m_farrlstOrdersOfDishes.get(i).cookOrderOfDish
			(chefEfficiency ,warehouse,countDown);
		}
	}

	/**
	 * manage the end of the cooking of this order
	 * change status, stop time counting
	 */
	public void finishCooking()
	{
		this.m_ftmTime.endCooking();
		this.m_enmStatus = OrderStatus.COMPLETE;
	}

	/**
	 * manage start delivering-start counting delivery time
	 */
	public void startDelivering()
	{
		this.m_ftmTime.startDelivering();
	}

	/**
	 * manage the end of the delivery
	 * stop counting delivery time and return reward 
	 * @param long: expected delivery time
	 * @return double: actual reward
	 */
	public double delivered(long expectedDeliveryTime)
	{
		this.m_ftmTime.endDelivering();
		this.m_enmStatus = OrderStatus.DELIVERED;
		double finalReward = getReward(expectedDeliveryTime);

		// Updates the statistics with the reward
		Statistics.getOnlyInstanne().addReward(this.m_fdOrderReward, finalReward);
		return (finalReward);
	}

	/**
	 * Checks if a given chef's property can cook the order
	 * @param double: get from the chef (enduranceRating-Pressure).
	 * @return boolean: "true" if the chef can cook the order, else-"false".
	 */
	public boolean canTheChefCookTheOrder(double enduranceRatingMinusPressure)
	{
		return (m_fdDifficulty <= enduranceRatingMinusPressure);
	}

	/**
	 * This method calculates the delivery time
	 * @param Point: resturant address
	 * @param double: delivery speed
	 * @return long: calculate and return delivary time
	 */
	public long calculateDeliveryTime(Point resturantAddress, double speed)
	{
		if(this.m_fpntCustomerAddress!= resturantAddress){
			return ((long)(this.m_fpntCustomerAddress.distanceFrom
					(resturantAddress))/(long)(speed));
		}		
		return 0;
	}

	/**
	 * This method gets the order's reward
	 * @param long: expected delivery time.
	 * @return double: calculate and return the actual reward.
	 */
	public double getReward(long expectedDeliveryTime)
	{
		return (this.m_fdOrderReward*(this.m_ftmTime.calculateReward(
				this.m_flExpectedCookingTime, expectedDeliveryTime)));
	}

	/**
	 * This method gets the order's status
	 * @return OrderStatus: the status of the order.
	 */
	public OrderStatus getStatus()
	{
		return this.m_enmStatus;
	}

	@Override
	public int compareTo(Order other) 
	{
		return (this.m_nNumToolNeeded-other.m_nNumToolNeeded);
	}

	/**
	 * toStrind override
	 */
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Order [");
		if (m_fstrID != null) 
		{
			builder.append("ID=");
			builder.append(m_fstrID);
			builder.append(", ");
			builder.append("Difficulty=");
			builder.append(m_fdDifficulty);
			builder.append(", orderStatus=");
			builder.append(m_enmStatus);
			builder.append(", ");
			builder.append("ExpectedCookingTime=");
			builder.append(m_flExpectedCookingTime);
			builder.append(", ");
			builder.append("NumberOfDishToCook=");
			builder.append(this.numberOfDishes);
			builder.append(", orderReward=");
			builder.append(m_fdOrderReward);
			if (m_fpntCustomerAddress != null) {
				builder.append(", ");
				builder.append("customerAddress=");
				builder.append(m_fpntCustomerAddress.toString());
			}
			if (m_farrlstOrdersOfDishes != null) {
				builder.append("\ndishes:");
				for(int i=0 ; i<this.m_farrlstOrdersOfDishes.size();i++){
					if (m_farrlstOrdersOfDishes.get(i) != null) {
						builder.append(i+1);
						builder.append(". ");
						builder.append(m_farrlstOrdersOfDishes.get(i).toString());
						builder.append("\n");
					}
				}
			}
			builder.append("]");
		}
		
		return builder.toString();
	}

	//Logger functions

	/**
	 * @return A message with the order ID and number of dishes.
	 */
	public String orderInfoLog()
	{
		StringBuilder msg = new StringBuilder();
		msg.append("[OrderID: ");
		msg.append(this.m_fstrID);
		msg.append(", number of meals: ");
		msg.append(this.numberOfDishes);
		msg.append("]");
		m_logOrderLogger.info(msg.toString());
		return msg.toString();
	}

	/**
	 * @return A message that say the order cook is cooked, with the
	 * expected and actual cooking time.
	 */
	public String cookedOrderLog()
	{
		StringBuilder msg = new StringBuilder();
		msg.append("ORDER COOKED: ");
		msg.append(this.m_fstrID);
		msg.append(", [expected cooking time: ");
		msg.append(this.m_flExpectedCookingTime);
		msg.append(", actual cooking time: ");
		msg.append(this.m_ftmTime.calcCookingTime());
		msg.append("]");
		this.m_logOrderLogger.info(msg.toString());
		return msg.toString();
	}

	/**
	 * @param expecteddeliveringTime: the expected delivering time from the delivery.
	 * @param orderFinalReward: The actual reward 
	 * @return A message that say the order was deliverd, with the
	 * expected and actual delivering time, and expected and actual reward
	 */
	public String orderDeliverdLog(long expecteddeliveringTime, 
			double orderFinalReward)
	{
		StringBuilder msg = new StringBuilder();
		msg.append("ORDER DELIVERED: ");
		msg.append(this.m_fstrID);
		msg.append(", [expected delivering time: ");
		msg.append(expecteddeliveringTime);
		msg.append(", actual delivering time: ");
		msg.append(this.m_ftmTime.calaDeliveringTime());
		msg.append("]\n[expected order reward: ");
		msg.append(this.m_fdOrderReward);
		msg.append(", actual order reward: ");
		msg.append(orderFinalReward);
		msg.append("]");
		this.m_logOrderLogger.info(msg.toString());
		return msg.toString();
	}
}





