package Parsers;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Restaurant.Dish;
import Restaurant.Menu;
import Warehouse.Ingredient;
import Warehouse.KitchenTool;
import Warehouse.WarehouseImpl;

/**
 * This class represents the menu parser
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class MenuParser {
	/**
	 * This method parses a given menu file
	 * @param strMenuFile - path to menu xml file
	 * @param warAllWarehouse - warehouse contains all the tools and ingredients
	 * @return Menu with all the dishes in the file
	 */
	public static Menu ParseMenu(String strMenuFile, WarehouseImpl warAllWarehouse)
			throws Exception
	{
		Menu mnNew = new Menu();

		try
		{
			// Opens the Menu file
			NodeList nlMenu = XmlHandler.readXML(strMenuFile, "Menu.xsd");

			// Gets the Dishes
			Node ndDishes = XmlHandler.findNodes(nlMenu, InitDataParser.DISHES_TAG);
			getDishes(ndDishes.getChildNodes(), mnNew);
		}
		catch (Exception ex)
		{
			Logger.getLogger(MenuParser.class.getName()).severe(
					"Menu File is not in the correct format : " + ex.getMessage());
		}

		return (mnNew);
	}

	/**
	 * This method translate a given menu schema into an array list of dishes
	 * @param nlDishes - node list contains all the menu data 
	 * @param arrAllDishes - ArrayList of all the dishes
	 */
	private static void getDishes(NodeList nlDishes, Menu mnMenu)
	{
		// Goes over the dishes and adds 
		for (int nCurDish = 0; nCurDish < nlDishes.getLength(); nCurDish++)
		{
			Node ndDish = nlDishes.item(nCurDish);

			if ((ndDish.getNodeType() == Node.ELEMENT_NODE) &&
					(ndDish.getNodeName().equals(InitDataParser.DISH_TAG)))
			{
				Dish dCurDish = getDish(ndDish);

				if (dCurDish != null)
				{
					mnMenu.addDish(dCurDish);
				}
			}
		}
	}

	/**
	 * This method creates a dish from a given schema
	 * @param ndDish - node contains dish schema
	 * @return Dish
	 */
	private static Dish getDish(Node ndDish)
	{
		Dish dNewDish = null;

		// Gets the dish's data
		Node ndName = XmlHandler.findNodes(ndDish.getChildNodes(), InitDataParser.NAME_TAG);
		Node ndDiffRating = XmlHandler.findNodes(ndDish.getChildNodes(), InitDataParser.DIFFICULTY_RATING_TAG);
		Node ndExpcTime = XmlHandler.findNodes(ndDish.getChildNodes(), InitDataParser.EXPECTED_COOK_TIME_TAG);
		Node ndReward = XmlHandler.findNodes(ndDish.getChildNodes(), InitDataParser.REWARD_TAG);
		Node ndTools = XmlHandler.findNodes(ndDish.getChildNodes(), InitDataParser.DISH_KITCHEN_TOOLS_TAG);
		Node ndIngredients = XmlHandler.findNodes(ndDish.getChildNodes(), InitDataParser.INGREDIENTS_TAG);

		String strName = XmlHandler.getContent(ndName);
		String strDiffRating = XmlHandler.getContent(ndDiffRating);
		String strExpTime = XmlHandler.getContent(ndExpcTime);
		String strReward = XmlHandler.getContent(ndReward);

		// Gets the tools & ingredients
		ArrayList<KitchenTool> arrAllTools = getTools(ndTools.getChildNodes());
		ArrayList<Ingredient> arrAllIngredients = getIngredients(ndIngredients.getChildNodes());

		dNewDish = new Dish(strName, new Long(strExpTime), arrAllIngredients, arrAllTools, 
				new Integer(strDiffRating), new Double(strReward));

		return (dNewDish);		
	}

	/**
	 * This method returns all the tools appears in the node
	 * @param nlTools - node contains the data of tools
	 * @return array list of KitcheTool
	 */
	private static ArrayList<KitchenTool> getTools(NodeList nlTools)
	{
		ArrayList<KitchenTool> arrAllTools = new ArrayList<KitchenTool>();

		// Goes over the tools and adds them to the list
		for (int nCurTool = 0; nCurTool < nlTools.getLength(); nCurTool++)
		{
			Node ndCurTool = nlTools.item(nCurTool);

			if ((ndCurTool.getNodeType() == Node.ELEMENT_NODE) &&
					(ndCurTool.getNodeName().equals(InitDataParser.KITCHEN_TOOL_TAG)))
			{
				KitchenTool ktNewTool = InitDataParser.getTool(ndCurTool);
				if (ktNewTool != null)
				{
					arrAllTools.add(ktNewTool);
				}
			}
		}

		return (arrAllTools);
	}

	/**
	 * This method returns all the ingredients appears in the node
	 * @param nlIngredients - node contains the schema for all the ingredients
	 * @return array list of Ingredient
	 */
	private static ArrayList<Ingredient> getIngredients(NodeList nlIngredients)
	{
		ArrayList<Ingredient> arrAllIngredients = new ArrayList<Ingredient>();

		// Goes over the ingredients and adds them to the list
		for (int nCurIng = 0; nCurIng < nlIngredients.getLength(); nCurIng++)
		{
			Node ndCurIng = nlIngredients.item(nCurIng);

			if ((ndCurIng.getNodeType() == Node.ELEMENT_NODE) &&
					(ndCurIng.getNodeName().equals(InitDataParser.INGREDIENT_TAG)))
			{
				Ingredient ingNewIngredient = InitDataParser.getIngredient(ndCurIng);
				if (ingNewIngredient != null)
				{
					arrAllIngredients.add(ingNewIngredient);
				}
			}
		}

		return (arrAllIngredients);
	}
}
