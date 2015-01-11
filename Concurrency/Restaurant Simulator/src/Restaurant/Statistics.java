package Restaurant;
import java.util.ArrayList;

import Warehouse.Ingredient;
/**
 * This class collect the Simulation information.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class Statistics {
	private double moneyGained;
	private ArrayList<Order> deliverdOrders;
	private ArrayList<Ingredient> ingredientsUsed;
	private ArrayList<Double> expectedRewards;
	private ArrayList<Double> receivedRewards;
	private static Statistics m_stcOnlyInstance;

	/**
	 * Constructor
	 */
	private Statistics(){
		this.moneyGained = 0;
		this.deliverdOrders = new ArrayList<Order>();
		this.ingredientsUsed = new ArrayList<Ingredient>();
		this.expectedRewards = new ArrayList<Double>();
		this.receivedRewards = new ArrayList<Double>();
	}
	
	/**
	 * This method gets the class's only instance.
	 * if the instance wasn't initializes already
	 * 
	 * @return Instance of statistics
	 */
	public static Statistics getOnlyInstanne()
	{
		if (m_stcOnlyInstance == null)
		{
			m_stcOnlyInstance = new Statistics();
		}

		return (m_stcOnlyInstance);
	}

	/**
	 * Add an expected and actual order's reward.
	 * 
	 * @param expected: order's expected reward
	 * @param received: order's received reward
	 */
	public synchronized void addReward(double expected, double received){
		if((this.expectedRewards!=null) && (this.receivedRewards!=null)){
			Double expReward = new Double(expected);
			Double recReward = new Double(received);
			this.expectedRewards.add(expReward);
			this.receivedRewards.add(recReward);
		}
	}

	/**
	 * Adds the amount of money
	 * 
	 * @param dMoney: money to add to the money gained counter
	 */
	public synchronized void gainMoney(double dMoney)
	{
		moneyGained += dMoney;
	}

	/**
	 * This method adds the a given delivered order
	 * 
	 * @param ordDelivered: Order that delivered
	 */
	public synchronized void addOrder(Order ordDelivered)
	{
		// Adds only if the order is delivered
		if ((ordDelivered != null) &&
				(ordDelivered.getStatus() == Order.OrderStatus.DELIVERED))
		{
			this.deliverdOrders.add(ordDelivered);
		}
	}

	/**
	 * This method adds a given ingredient that was consumed to the list
	 * 
	 * @param ingConsumedIngredients
	 */
	public synchronized void addIngredient(Ingredient ingConsumedIngredients)
	{
		if (ingConsumedIngredients != null)
		{
			// Checks if the ingredient already exist
			if (this.ingredientsUsed.contains(ingConsumedIngredients))
			{
				Ingredient ingFoundIngredient = this.ingredientsUsed.get(
						this.ingredientsUsed.indexOf(ingConsumedIngredients));
				ingFoundIngredient.addQuantity(ingConsumedIngredients);
			}
			else
			{
				this.ingredientsUsed.add(ingConsumedIngredients);
			}
		}
	}

	

	/**
	 * toString for statistics
	 */
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Statics [moneyGained=");
		builder.append(moneyGained);
		if(this.expectedRewards!=null){
			builder.append("\nExpected Rewards:");
			for(int i=0 ; i<this.expectedRewards.size();i++){
				if (expectedRewards.get(i) != null) {
					builder.append("{");
					builder.append(expectedRewards.get(i));
					builder.append("}");
				}
			}
		}
		if(this.receivedRewards!=null){
			builder.append("\nReceived Rewards:");
			for(int i=0 ; i<this.receivedRewards.size();i++){
				if (receivedRewards.get(i) != null) {
					builder.append("{");
					builder.append(receivedRewards.get(i));
					builder.append("}");
				}
			}
		}
		if (ingredientsUsed != null) {
			builder.append("\nIngredients Used:");
			for(int i=0 ; i<this.ingredientsUsed.size();i++){
				if (ingredientsUsed.get(i) != null) {
					builder.append("\n");
					builder.append("ingredient number ");
					builder.append(i+1);
					builder.append(":");
					builder.append(ingredientsUsed.get(i).toString());
				}
			}
		}
		
		if (deliverdOrders != null) {
			builder.append("\nDeliverd Orders:");
			for(int i=0 ; i<this.deliverdOrders.size();i++){
				if (deliverdOrders.get(i) != null) {
					builder.append("\n");
					builder.append("deliverdOrder number ");
					builder.append(i+1);
					builder.append(":\n");
					builder.append(deliverdOrders.get(i).toString());
				}
			}
		}
		
		builder.append("]");
		return builder.toString();
	}


}
