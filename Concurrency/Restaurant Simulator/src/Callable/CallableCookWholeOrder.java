package Callable;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import Restaurant.Order;
import Warehouse.WarehouseImpl;

/**
 * This class manage the cooking of one order.
 * It's starting the cooking process, wait for all the dishes
 * to finish cooking, and return the cook order.
 * 
 * @author Nir Mendel & Yael Mathov
 */

public class CallableCookWholeOrder implements Callable<Order> {
	private final Order m_ordOrderToCook;
	private WarehouseImpl m_whMainWarehouse;
	private final double m_dChefEfficiency;
	private final int m_nNumberDishToCook;
	private final CountDownLatch m_cntdnCookFinished;
	private Logger m_logWholeOrderLogger = Logger.getLogger(CallableCookWholeOrder.class.getName());

	/**
	 * constructor
	 * 
	 * @param order: the order to cook
	 * @param chefEfficiency:  a chef efficiency rating
	 * @param warehouse: warehouse from the Management
	 */
	public CallableCookWholeOrder(Order order, double chefEfficiency ,WarehouseImpl warehouse)
	{
		this.m_ordOrderToCook = order;
		this.m_whMainWarehouse = warehouse;
		this.m_dChefEfficiency = chefEfficiency;
		this.m_nNumberDishToCook = this.m_ordOrderToCook.getNumOfDishes();
		this.m_cntdnCookFinished = new CountDownLatch(this.m_nNumberDishToCook);
	}

	/**
	 * cook the order. 
	 * start the order's cooking method, wait until all dishes finished
	 * to cook and return the dish.
	 */
	@Override
	public Order call()
	{
		try
		{
			this.m_logWholeOrderLogger.info("START COOKING order [" + m_ordOrderToCook.orderInfoLog() 
					+ "]\nWaiting for " + this.m_nNumberDishToCook + " dishes to cook");

			// Cooks the order
			this.m_ordOrderToCook.cookOrder(this.m_dChefEfficiency,this.m_whMainWarehouse,this.m_cntdnCookFinished);

			//wait until all dishes finish cooking
			this.m_cntdnCookFinished.await();
			this.m_ordOrderToCook.finishCooking();

			this.m_logWholeOrderLogger.info(m_ordOrderToCook.cookedOrderLog());
			return m_ordOrderToCook;
		}
		catch (Exception ex)
		{
			this.m_logWholeOrderLogger.info("Could not finish cook order [" + 
					this.m_ordOrderToCook.toString() + "].\n message: " + 
					ex.getMessage());
			return null;
		}
	}
}
