package Parsers;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Restaurant.Dish;
import Restaurant.Management;
import Restaurant.Menu;
import Restaurant.Order;
import Restaurant.OrderOfDish;
import System.Point;
import Warehouse.WarehouseImpl;

/**
 * This class represents the orders parser from the orders file
 *
 * @author Nir Mendel & Yael Mathov
 *
 */
public class OrdersParser {

	/**
	 * This method parses the order's file and returns all the orders
	 * @param strOrderFile - order's xml file path
	 * @param warWarehouse - warehouse contains all the tools and ingredients
	 * @param mnMenu - menu contains all the dishes
	 * @param lnkOrders - array contains all the orders
	 */
	public static void getOrders(String strOrderFile, WarehouseImpl warWarehouse, Menu mnMenu,
			Management mngMain) throws Exception
	{
		try
		{
			// Opens the Orders file
			NodeList nlOrders = XmlHandler.readXML(strOrderFile, "OrdersList.xsd");
	
			// Gets the orders
			Node ndOrders = XmlHandler.findNodes(nlOrders, InitDataParser.ORDERS_TAG);
			getOrders(ndOrders.getChildNodes(), warWarehouse, mnMenu, mngMain);
		}
		catch (Exception ex)
		{
			Logger.getLogger(OrdersParser.class.getName()).warning(
					"Order's list file is not in the correct format : " + ex.getMessage());
			throw ex;
		}
	}

	/**
	 * This method translates a given order's schema into an order's list
	 * @param nlOrders - node list contains the order's schema
	 * @param warWarehouse - warehouse contains all the tools and ingredients
	 * @param mnMenu - menu contains all the dishes
	 * @param lnkAllOrders - array list contains all the orders
	 */
	private static void getOrders(NodeList nlOrders, WarehouseImpl warWarehouse, Menu mnMenu, 
			Management mngMain)
	{
		// Goes over the orders, creates each order and saves them in the collection
		for (int nCurOrder = 0; nCurOrder < nlOrders.getLength(); nCurOrder++)
		{
			Node ndOrder = nlOrders.item(nCurOrder);

			if ((ndOrder.getNodeType() == Node.ELEMENT_NODE) &&
					(ndOrder.getNodeName().equals(InitDataParser.ORDER_TAG)))
			{
				// Adds the order to the management
				Order oCurOrder = getOrder(ndOrder, warWarehouse, mnMenu);
				if (oCurOrder != null)
				{
					mngMain.addOrder(oCurOrder);
				}
			}
		}
	
	}

	/**
	 * This method translates a given order's dishes into a collection of OrderOfDish
	 * @param nlOrders - Node list contains schema for all the order of dishes
	 * @param warWarehouse - warehouse containing all the tools and ingredients
	 * @param mnMenu - menu contains all the dishes
	 * @param vecAllOrders - Collection of all OrderOfDish
	 */
	private static void getAllOrderOfDish(NodeList nlOrders, WarehouseImpl warWarehouse, 
			Menu mnMenu, ArrayList<OrderOfDish> vecAllOrders)
	{		
		// Goes over the dishes and creates an order of dish
		for (int nCurDish = 0; nCurDish < nlOrders.getLength(); nCurDish++)
		{
			Node ndCurDish = nlOrders.item(nCurDish);

			if ((ndCurDish.getNodeType() == Node.ELEMENT_NODE) &&
					(ndCurDish.getNodeName().equals(InitDataParser.DISH_TAG)))
			{
				OrderOfDish ordNewDish = getOrderOfDish(ndCurDish, warWarehouse, mnMenu);

				if (ordNewDish != null)
				{
					vecAllOrders.add(ordNewDish);
				}
			}
		}
	}	

	/**
	 * This method creates an order from a given schema 
	 * @param ndOrder - order's schema
	 * @param warWarehouse - warehouse contains all the tools and ingredients
	 * @param mnMenu - menu contains all the dishes
	 * @return Order
	 */
	private static Order getOrder(Node ndOrder, WarehouseImpl warWarehouse, Menu mnMenu)
	{
		Order oNewOrder = null;

		// Gets the order's id
		String strID = ((org.w3c.dom.Element)ndOrder).getAttribute(InitDataParser.ID_TAG);

		// Gets the delivery address
		Node ndDelAddress = XmlHandler.findNodes(ndOrder.getChildNodes(), InitDataParser.DELIVERY_ADDRESS);
		Point pntDelAddress = InitDataParser.getAddress(ndDelAddress);

		// Gets all the order of dishes
		Node ndOrderOfDish = XmlHandler.findNodes(ndOrder.getChildNodes(), InitDataParser.DISHES_TAG);
		ArrayList<OrderOfDish> vecOrderDishes = new ArrayList<OrderOfDish>(); 
		getAllOrderOfDish(ndOrderOfDish.getChildNodes(), warWarehouse, mnMenu, vecOrderDishes);

		oNewOrder = new Order(strID, pntDelAddress, vecOrderDishes);

		return (oNewOrder);
	}

	/**
	 * This method creates an order of dish from a given node schema
	 * @param nlOrders - node contains data of order of dish
	 * @param warWarehouse - warehouse contains all the tools and ingredients
	 * @param mnMenu - menu contains all the dishes
	 * @return OrderOfDish
	 */
	private static OrderOfDish getOrderOfDish(Node ndOrders, WarehouseImpl warWarehouse, Menu mnMenu)
	{
		OrderOfDish ordNewDish = null;

		// Gets the order of dish's data
		Node ndName = XmlHandler.findNodes(ndOrders.getChildNodes(), InitDataParser.NAME_TAG);
		Node ndQuantity = XmlHandler.findNodes(ndOrders.getChildNodes(), InitDataParser.QUANTITY_TAG);

		String strName = XmlHandler.getContent(ndName);
		String strQuantity = XmlHandler.getContent(ndQuantity);

		// Finds the dish in the menu
		Dish dOrderDish = mnMenu.getDish(strName);

		if (dOrderDish != null)
		{
			ordNewDish = new OrderOfDish(dOrderDish, new Integer(strQuantity));
		}


		return (ordNewDish);
	}
}
