package Restaurant;
import java.util.ArrayList;

import Warehouse.Ingredient;
import Warehouse.KitchenTool;
import Warehouse.WarehouseImpl;

/**
 * This class represents a single dish and contain all the dish information.
 * Managing ingredients and tools acquire, and tools return to the warehouse.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */

public class Dish implements Comparable<Dish>{

	private final String m_fstrName;
	private final long m_flCookTime;
	private final int m_fnDifficulty;
	private final double m_fdReward;
	private ArrayList<Ingredient> m_arringIngredients;
	private ArrayList<KitchenTool> m_arrktTools;

	/**
	 * Constructor
	 * 
	 * @param fName: Dish name
	 * @param fCookTime: Dish cooking time
	 * @param arr_Ingredients: Ingredients we need to cook the dish
	 * @param arr_tools: Tools we need to cook the dish
	 * @param fDifficulty: Difficulty of the dish
	 * @param fReward: The reward for cooking and delivering the dish
	 */
	public Dish(String name, long cookTime, ArrayList<Ingredient> ingredients, 
			ArrayList<KitchenTool> tools, int difficulty, double reward)
	{
		this.m_fstrName=name;
		this.m_flCookTime = cookTime;
		this.m_arringIngredients = ingredients;
		this.m_arrktTools = tools;
		this.m_fnDifficulty = difficulty;
		this.m_fdReward = reward;
	}

	/**
	 *  Gets a chef's efficiency and calculate the cooking time.
	 *  
	 * @param chefEfficiency: chef efficiency.
	 * @return dish's cooking time according to chef's efficiency. 
	 */
	public long computCookTime(double chefEfficiency)
	{
		return (long)(chefEfficiency*this.m_flCookTime);
	}

	/**
	 * Take the needed tools and ingredients from warehouse
	 * 
	 * @param warehouse: The main warehouse from Managment
	 */
	public void aquireToolsAndIngredients(WarehouseImpl warehouse)
	{
		if((this.m_arrktTools!=null) && (this.m_arringIngredients!=null)){
			//take ingredients
			for(int nCurIngredientIndex=0; 
					nCurIngredientIndex<this.m_arringIngredients.size(); 
					nCurIngredientIndex++)
			{
				Ingredient crntIngredient = this.m_arringIngredients.get(nCurIngredientIndex);
			
				// Take the current ingredient from the warehouse
				warehouse.aquireIngredient(crntIngredient );

				//update statisics with consumed ingredient
				Statistics.getOnlyInstanne().addIngredient(new Ingredient(crntIngredient) );
			}
			//take tools from the warehouse
			for(int i=0; i<this.m_arrktTools.size();i++){
				warehouse.aquireTool(this.m_arrktTools.get(i));
			}
		}
	}

	/**
	 * Return the used tools to the warehouse
	 * 
	 * @param warehouse: The main warehouse from Managment
	 */
	public void returnTools(WarehouseImpl warehouse)
	{
		if(this.m_arrktTools!=null)
		{
			// Returns the tools to the warehouse
			for(int i=0; i<this.m_arrktTools.size();i++){
				warehouse.returnTool(this.m_arrktTools.get(i));
			}
		}
	}

	/**
	 * @return Dish difficulty.
	 */
	public double getDishDifficulty ()
	{
		return (double)(this.m_fnDifficulty);
	}

	/**
	 * 
	 * @param dishToCompar: Dish to compar the cooking time with this dish cooking time.
	 * @return The cooking time of the longest  expected cooking time
	 */
	public long comperCookingTime(Dish dishToCompar)
	{
		if(this.m_flCookTime > dishToCompar.m_flCookTime) return this.m_flCookTime;
		return dishToCompar.m_flCookTime;
	}

	/**
	 * @param quantity: This dish quantity in an order
	 * @return Total reward for this dish
	 */
	public double getRewardForDishInTheOrder(int quantity)
	{
		return (this.m_fdReward*quantity);
	}

	/**
	 * compareTo override (compar by name)
	 */
	@Override
	public int compareTo(Dish o) 
	{
		return this.m_fstrName.compareTo(o.m_fstrName);
	}

	/**
	 * compareTo override (compar by name)
	 */
	@Override
	public boolean equals(Object object)
	{
		boolean sameSame = false;

		if (object != null && object instanceof Dish)
		{
			sameSame = this.m_fstrName.equals(((Dish) object).m_fstrName);
		}
		else if (object != null && object instanceof String)
		{
			sameSame = this.m_fstrName.equals(object);
		}

		return sameSame;
	}

	/**
	 * @return Number of tools needed to cook the dish
	 */
	public int getNumberOfTools()
	{
		int toolCount = 0;
		if(this.m_arrktTools!=null)
		{
			// Goes over the dish's needed tools and sums them
			for(int i=0 ; i<this.m_arrktTools.size();i++){
				KitchenTool currentTool = this.m_arrktTools.get(i);
				if(currentTool!=null){
					toolCount = toolCount + currentTool.countTools(toolCount);
				}
			}
		}
		return toolCount;
	}

	/**
	 *  toString override
	 */
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Dish [");
		if (m_fstrName != null) {
			builder.append("name=");
			builder.append(m_fstrName);
			builder.append(", ");
		}
		builder.append("cookTime=");
		builder.append(m_flCookTime);
		if (m_arringIngredients != null) 
		{
			builder.append(",\n");
			builder.append("ingredients:");
			for(int i=0 ; i<this.m_arringIngredients.size();i++){
				if(m_arringIngredients.get(i) != null)
				{
					builder.append("\n");
					builder.append(i+1);
					builder.append(". ");
					builder.append(m_arringIngredients.get(i).toString());
				}
			}
		}
		if (m_arrktTools != null) {
			builder.append("\n");
			builder.append("tools:");
			for(int i=0 ; i<this.m_arrktTools.size();i++){
				if(m_arrktTools.get(i) != null){
					builder.append("\n");
					builder.append(i+1);
					builder.append(". ");
					builder.append(m_arrktTools.get(i).toString());
				}
			}
		}
		builder.append("\n");
		builder.append("difficulty=");
		builder.append(m_fnDifficulty);
		builder.append(", reward=");
		builder.append(m_fdReward);
		builder.append("]");
		return builder.toString();
	}
}
