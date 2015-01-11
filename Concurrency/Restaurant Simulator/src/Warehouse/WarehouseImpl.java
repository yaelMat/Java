package Warehouse;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents the resturant's warehouse.
 * Manage acquire and return tools and ingredients.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class WarehouseImpl{
	// Data Members
	private ConcurrentHashMap<KitchenTool,KitchenTool> m_mapTools;
	private ConcurrentHashMap<Ingredient,Ingredient> m_mapIngredients;

	/**
	 * Default constructor
	 */
	public WarehouseImpl() {
		this.m_mapIngredients = new ConcurrentHashMap<Ingredient, Ingredient>();
		this.m_mapTools = new ConcurrentHashMap<KitchenTool, KitchenTool>();
	}
	
	/**
	 * This method adds a tool to the warehouse.
	 * if the tool exists - the method adds the quantities
	 * @param ktNewTool - KitchenTool
	 */
	public void addTool(KitchenTool ktNewTool)
	{
		// Adds the quantity if the tools exist
		if (m_mapTools.contains(ktNewTool))
		{
			m_mapTools.get(ktNewTool).addQuantity(ktNewTool);
		}
		else
		{
			m_mapTools.put(ktNewTool, ktNewTool);
		}
	}
	
	/**
	 * This method adds an ingredient to the warehouse.
	 * if the ingredient exits - adds the quantity
	 * @param ingNewIngredient - Ingredient
	 */
	public void addIngredient(Ingredient ingNewIngredient)
	{
		// Adds the quantity if the ingredient exists
		if (m_mapIngredients.contains(ingNewIngredient))
		{
			m_mapIngredients.get(ingNewIngredient);
		}
		else
		{
			m_mapIngredients.put(ingNewIngredient, ingNewIngredient);
		}
	}

	/**
	 * This method finds a given tool in the collection and returns its index
	 * @return the tool's index in the collection. if the tool does not exist (or we received null) - returns -1
	 */
	public KitchenTool findTool(KitchenTool tool) 
	{
		if (tool != null){
			return (m_mapTools.get(tool));
		}
		return null;
	}

	/**
	 * This method finds a given ingredient in the collection and returns its index
	 * @return the ingredient's index in the collection. if the ingredient does not exist (or we received null) - returns -1
	 */
	public Ingredient findIngredient(Ingredient ingredient)
	{
		if (ingredient != null){
			return (m_mapIngredients.get(ingredient));
		}
		return null;
	}

	/**
	 * get tool and acquire it's quantity to the similar tool in the warehouse
	 * @param tool: toll to take
	 */
	public void aquireTool(KitchenTool tool)
	{
		if (tool!=null){
			KitchenTool tempTool = findTool(tool);
			tempTool.reduceQuantity(tool);
		}
	}

	/**
	 * get ingredient and acquire it's quantity to the similar ingredient in the warehouse
	 * @param ingredient: ingredient to take
	 */
	public void aquireIngredient(Ingredient ingredient)
	{
		if (ingredient!=null){
			Ingredient tempIngredient = findIngredient(ingredient);
			tempIngredient.reduceQuantity(ingredient);
		}
	}

	/**
	 * get tool and return it's quantity to the similar tool in the warehouse
	 * 
	 * @param tool: tool to return
	 */
	public void returnTool(KitchenTool tool)
	{
		if (tool!=null){
			findTool(tool).addQuantity(tool);
		}
	}
}
