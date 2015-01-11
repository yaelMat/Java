package Parsers;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import Restaurant.Management;
import Restaurant.Menu;
import Restaurant.Order;
import Runnable.RunnableChef;
import Runnable.RunnableDeliveryPerson;
import System.Lock;
import System.Point;
import Warehouse.Ingredient;
import Warehouse.KitchenTool;
import Warehouse.WarehouseImpl;

/**
 * This class represents parsing XML files to the simulation objects
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class InitDataParser {
	public static final String ADDRESS_TAG = "Address";
	public static final String ADDRESS_X_TAG = "x";
	public static final String ADDRESS_Y_TAG = "y";
	public static final String REPOSITORY_TAG = "Repository";
	public static final String KITCHEN_TOOLS_TAG = "Tools";
	public static final String DISH_KITCHEN_TOOLS_TAG = "KitchenTools";
	public static final String INGREDIENTS_TAG = "Ingredients";
	public static final String KITCHEN_TOOL_TAG = "KitchenTool";
	public static final String INGREDIENT_TAG = "Ingredient";
	public static final String NAME_TAG = "name";
	public static final String QUANTITY_TAG = "quantity";
	public static final String STAFF_TAG = "Staff";
	public static final String CHEFS_TAG = "Chefs";	
	public static final String CHEF_TAG = "Chef";
	public static final String EFFICIENCY_RATING_TAG = "efficiencyRating";
	public static final String ENDURANCE_RATING_TAG = "enduranceRating";
	public static final String DELIVERY_PERSONALS_TAG = "DeliveryPersonals";
	public static final String DELIVERY_PERSON_TAG = "DeliveryPerson";
	public static final String SPEED_TAG = "speed";
	public static final String DISHES_TAG = "Dishes";
	public static final String DISH_TAG = "Dish";
	public static final String DIFFICULTY_RATING_TAG = "difficultyRating";
	public static final String EXPECTED_COOK_TIME_TAG = "expectedCookTime";
	public static final String REWARD_TAG = "reward";
	public static final String ORDERS_TAG = "Orders";
	public static final String ORDER_TAG = "Order";
	public static final String ID_TAG = "id";
	public static final String DELIVERY_ADDRESS = "DeliveryAddress";

	/**
	 * This method parses given files to the management simulator
	 * @param strInitialData - InitData file path
	 * @param strMenu - Menu file path
	 * @param strOrdersList - Orders file path
	 * @return Management
	 */
	public static Management ParseFiles(String strInitialData, String strMenu, 
			String strOrdersList) throws Exception 
	{
		// Warehouse & Menu
		WarehouseImpl warAllWarehouse; 
		Menu menMainMenu;

		// Blocking Queue for the finished orders
		LinkedBlockingQueue<Order> lnkFinishedOrders = new LinkedBlockingQueue<Order>();
		// Semaphore for the finished chefs and delivers
		Semaphore smFinishedChefs = new Semaphore(0);
		Semaphore smFinishedDelivers = new Semaphore(0);
		// Booleans for locking
		Lock bDelPersonsLock = new Lock();
		Lock bChefLock = new Lock();
		// Address of the restaurant
		Point pntRestAddress;

		NodeList nlInitData = null;
		try
		{
			// Opens the InitialData file 
			nlInitData = XmlHandler.readXML(strInitialData, "InitialData.xsd");

			// Gets the Address data
			Node ndAddress = XmlHandler.findNodes(nlInitData, ADDRESS_TAG);
			pntRestAddress = getAddress(ndAddress);

			warAllWarehouse = new WarehouseImpl();

			// Gets the repository and creates the warehouse
			getRepository(nlInitData, warAllWarehouse);
		}
		catch (Exception ex)
		{
			Logger.getLogger(InitDataParser.class.getName()).severe(
					"InitialData file is not in the correct format : " + ex.getMessage());
			throw ex;
		}

		// Gets the menu
		menMainMenu = MenuParser.ParseMenu(strMenu, warAllWarehouse);

		// Creates the management
		Management mngNew = new Management(warAllWarehouse, bChefLock, bDelPersonsLock, 
				lnkFinishedOrders, menMainMenu, smFinishedChefs, smFinishedDelivers); 

		// Gets the staff and creates the delivery management
		getStaff(nlInitData, pntRestAddress, bChefLock, bDelPersonsLock, warAllWarehouse, 
				lnkFinishedOrders, smFinishedChefs, mngNew, smFinishedDelivers);

		// Gets the orders
		OrdersParser.getOrders(strOrdersList, warAllWarehouse, menMainMenu, mngNew);

		return (mngNew);
	}

	/**
	 * This method gets all the staff from the given file
	 * @param nlInitData - NodeList contains all the nodes under the root
	 * @param arrlstAllChefs - output array of RunnableChef
	 * @param arrlstAllDelPersons - output array of RunnableDeliveryPerson
	 * @param pntRestAddress - Address of the restaurant
	 * @param bChefLock - Boolean for locking the chefs
	 * @param bDelPersonsLock - Boolean for locking the delivery persons
	 */
	private static void getStaff(NodeList nlInitData, Point pntRestAddress, 
			Lock bChefLock, Lock bDelPersonsLock, WarehouseImpl warWarehouse,
			LinkedBlockingQueue<Order> lnkFinishedOreders, Semaphore smFinishedChefs,
			Management mngMain, Semaphore smFinishedDeliveries)
	{
		// Gets the staff
		Node ndStaff = XmlHandler.findNodes(nlInitData, STAFF_TAG);
		NodeList nlAllStaff = ndStaff.getChildNodes();

		// Gets the chefs
		Node ndChefs = XmlHandler.findNodes(nlAllStaff, CHEFS_TAG);
		getChefs(ndChefs.getChildNodes(), bChefLock, warWarehouse, lnkFinishedOreders, 
				smFinishedChefs, mngMain);

		// Gets the delivery boys
		Node ndDelPersons = XmlHandler.findNodes(nlAllStaff, DELIVERY_PERSONALS_TAG);
		getDeliveryPersons(ndDelPersons.getChildNodes(), pntRestAddress, bDelPersonsLock, 
				mngMain, smFinishedDeliveries);
	}

	/**
	 * This method gets all the repository from the given file
	 * @param nlInitData - NodeList contains all the nodes under the root
	 * @param hsAllTools - output HashMap of KitchenTool contains all the tools
	 * @param hsAllIngredients - output HashMap of Ingredient contains all the ingredients
	 */
	private static void getRepository(NodeList nlInitData, 
			WarehouseImpl war)
	{
		// Gets the Repository
		Node ndRepository = XmlHandler.findNodes(nlInitData, REPOSITORY_TAG);
		NodeList nlAllRepository = ndRepository.getChildNodes();

		// Gets the tools
		Node ndTools = XmlHandler.findNodes(nlAllRepository, KITCHEN_TOOLS_TAG);
		getTools(ndTools.getChildNodes(), war);

		// Gets the ingredients
		Node ndIngredients = XmlHandler.findNodes(nlAllRepository, INGREDIENTS_TAG);
		getIngredients(ndIngredients.getChildNodes(),war);
	}

	/**
	 * This method gets an address from the given address node
	 * @param ndAddress
	 * @return
	 */
	public static Point getAddress(Node ndAddress)
	{
		Point pntAddress = null;

		// Gets the addres's location
		NodeList nlAddressPoint = ndAddress.getChildNodes();
		Node ndX = XmlHandler.findNodes(nlAddressPoint, ADDRESS_X_TAG);
		Node ndY = XmlHandler.findNodes(nlAddressPoint, ADDRESS_Y_TAG);

		String strX = XmlHandler.getContent(ndX);
		String strY = XmlHandler.getContent(ndY);

		// Creates the address
		pntAddress = new Point(new Double(strX), new Double(strY));

		return (pntAddress);
	}

	/**
	 * This method retreives a tool from a given node
	 * @param ndTool - node contains tool's data
	 * @return KitchenTool
	 */
	public static KitchenTool getTool(Node ndTool)
	{
		KitchenTool ktNewTool = null;

		// Gets the kitchen tool's data
		Node ndName = XmlHandler.findNodes(ndTool.getChildNodes(), NAME_TAG);
		Node ndQuantity = XmlHandler.findNodes(ndTool.getChildNodes(), QUANTITY_TAG);

		String strName = XmlHandler.getContent(ndName);
		String strQuantity = XmlHandler.getContent(ndQuantity);

		ktNewTool = new KitchenTool(strName, new Integer(strQuantity));

		return (ktNewTool);
	}

	/**
	 * This method creates an ingredient from a given ingredient schema
	 * @param ndIngredient
	 * @return
	 */
	public static Ingredient getIngredient(Node ndIngredient)
	{
		Ingredient ingNewIng = null;

		// Gets the ingredient's data
		Node ndName = XmlHandler.findNodes(ndIngredient.getChildNodes(), NAME_TAG);
		Node ndQuantity = XmlHandler.findNodes(ndIngredient.getChildNodes(), QUANTITY_TAG);

		String strName = XmlHandler.getContent(ndName);
		String strQuantity = XmlHandler.getContent(ndQuantity);

		// Creates the ingredient
		ingNewIng = new Ingredient(strName, new Integer(strQuantity));


		return (ingNewIng);
	}

	/**
	 * This method creates a runnable chef from a given chef schema
	 * @param ndChef - node contains chef's data
	 * @return RunnableChef
	 */
	private static RunnableChef getChef(Node ndChef, Lock bLock, WarehouseImpl warWarhouse,
			LinkedBlockingQueue<Order> lnkFinisedOrders, Semaphore smFinishedChefs)
	{
		RunnableChef rnChef = null;

		// Gets the chef's data
		Node ndName = XmlHandler.findNodes(ndChef.getChildNodes(), NAME_TAG);
		Node ndEfficiency = XmlHandler.findNodes(ndChef.getChildNodes(), EFFICIENCY_RATING_TAG);
		Node ndEndurance = XmlHandler.findNodes(ndChef.getChildNodes(), ENDURANCE_RATING_TAG);

		String strName = XmlHandler.getContent(ndName);
		String strEffiecieny = XmlHandler.getContent(ndEfficiency);
		String strEndurance = XmlHandler.getContent(ndEndurance);

		// Creates the runnable new chef
		rnChef = new RunnableChef(strName, ((double)new Double(strEffiecieny)), 
				((int)new Integer(strEndurance)), lnkFinisedOrders,
				warWarhouse, bLock, smFinishedChefs);

		return (rnChef);
	}

	/**
	 * This method creates a runnable delivery person from a given delivery schema
	 * @param ndDelPerson - node contains the delivery person's data
	 * @param pntAddress - restaurant's address
	 * @param bLock - locker for the delivery persons
	 * @return RunnableDeliveryPerson
	 */
	private static RunnableDeliveryPerson getDelPerson(Node ndDelPerson, Point pntAddress, 
			Lock bLock, Semaphore smFinishedDeliveries)
	{
		RunnableDeliveryPerson rndDelPerson = null;

		// Gets the delivery person's data
		Node ndName = XmlHandler.findNodes(ndDelPerson.getChildNodes(), NAME_TAG);
		Node ndSpeed = XmlHandler.findNodes(ndDelPerson.getChildNodes(), SPEED_TAG);

		String strName = XmlHandler.getContent(ndName);
		String strSpeed = XmlHandler.getContent(ndSpeed);

		Double nSpeed = new Double(strSpeed);
		rndDelPerson = new RunnableDeliveryPerson(strName, nSpeed, pntAddress, bLock,
				smFinishedDeliveries);

		return (rndDelPerson);
	}

	/**
	 * This method returns an hash map of all the kitchen tools in the given nodes
	 * @param nlTools - list of notes contain all the tools
	 * @param hsAllTools - hash map of all the tools
	 */
	private static void getTools(NodeList nlTools, WarehouseImpl war)
	{
		// Goes over the tools' nodes and saves the tools
		for (int nCurToolNode = 0; nCurToolNode < nlTools.getLength(); nCurToolNode++)
		{
			Node ndCurTool = nlTools.item(nCurToolNode);

			if ((ndCurTool.getNodeType() == Node.ELEMENT_NODE) && 
					(ndCurTool.getNodeName().equals(KITCHEN_TOOL_TAG)))
			{
				KitchenTool ktCurTool = getTool(ndCurTool);
				if (ktCurTool != null)
				{
					war.addTool(ktCurTool);
				}
			}
		}
	}

	/**
	 * This method translate a given ingredients schema into an hashmap of ingredients
	 * @param nlTools - node list of ingredients
	 * @param hsAllIngredients - hash map contains all the ingredients
	 */
	private static void getIngredients(NodeList nlIngredients, WarehouseImpl war)
	{
		// Goes over the tools' nodes and saves the tools
		for (int nCurIngNode = 0; nCurIngNode < nlIngredients.getLength(); nCurIngNode++)
		{
			Node ndCurIngredient = nlIngredients.item(nCurIngNode);

			if ((ndCurIngredient.getNodeType() == Node.ELEMENT_NODE) && 
					(ndCurIngredient.getNodeName().equals(INGREDIENT_TAG)))
			{
				// Gets the ingredient and adds to the warehouse
				Ingredient ingIngredient = getIngredient(ndCurIngredient);
				if (ingIngredient != null)
				{
					war.addIngredient(ingIngredient);
				}
			}
		}
	}

	/**
	 * This method translate a given chefs schema into an array list contains all the chefs,
	 * sorted by their efficiency
	 * @param ndChefs - nodeList contains all the chefs
	 * @param arrlstChefs - array list of all the chefs sorted
	 */
	private static void getChefs(NodeList nlChefs, Lock bLock, WarehouseImpl warWarehouse,
			LinkedBlockingQueue<Order> lnkFinisedOrders, Semaphore smFinishedChefs,
			Management mngMain)
	{
		// Goes over the chefs nodes and creates a runnablechef
		for (int nCurChefIndex = 0; nCurChefIndex < nlChefs.getLength(); nCurChefIndex++)
		{
			Node ndChef = nlChefs.item(nCurChefIndex);

			if ((ndChef.getNodeType() == Node.ELEMENT_NODE) && 
					(ndChef.getNodeName().equals(CHEF_TAG)))
			{
				RunnableChef rnChef = getChef(ndChef, bLock, warWarehouse, 
						lnkFinisedOrders, smFinishedChefs);
				if (rnChef != null)
				{
					mngMain.addChef(rnChef);
				}
			}
		}
	}

	/**
	 * This method translate a given deilveries men schema into an array list contains all the 
	 * deliveries men
	 * @param nlDelPersons - node list contains all the deliveries men
	 * @param restAddPoint - the restuarant address
	 * @param bLock - lock for deliveries
	 * @param arrlstAllDeliveryMen - array contains all the deliveries men
	 */
	private static void getDeliveryPersons(NodeList nlDelPersons, Point restAddPoint,
			Lock bLock, Management mngMain, Semaphore smFinishedDel)
	{

		// Goes over the delivery men and creates them
		for (int nCurDelPerson = 0; nCurDelPerson < nlDelPersons.getLength(); nCurDelPerson++)
		{
			Node ndCurPerson = nlDelPersons.item(nCurDelPerson);

			if ((ndCurPerson.getNodeType() == Node.ELEMENT_NODE) &&
					(ndCurPerson.getNodeName().equals(DELIVERY_PERSON_TAG)))
			{
				// Adds the current delivery to the management
				RunnableDeliveryPerson rnDelPerson = getDelPerson(ndCurPerson, restAddPoint, 
						bLock, smFinishedDel);

				if (rnDelPerson != null)
				{
					mngMain.addDeliveryMan(rnDelPerson);
				}
			}
		}
	}
}
