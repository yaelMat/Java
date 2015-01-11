package Restaurant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import Runnable.RunnableChef;
import Runnable.RunnableDeliveryManagment;
import Runnable.RunnableDeliveryPerson;
import System.Lock;
import Warehouse.WarehouseImpl;


/**
 * This class contain all the chefs, deliveries and order, and
 * manage the order sending and threads shut down.
 * Manage the simulation.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class Management {
	private LinkedList<Order> m_lnklstOrdersList;
	private ArrayList<RunnableChef> m_arrlstChefList;
	private ArrayList<RunnableDeliveryPerson> m_arrlstDeliveryMenList; 
	private WarehouseImpl m_whMainWarehouse;
	private RunnableDeliveryManagment m_rndmDeliveryManage;
	private final Menu m_mnDishMenu;
	private Lock m_lockChefLocker;
	private Semaphore m_smChefCounter;
	private Semaphore m_smDeliveryCounter;

	Logger logger = Logger.getLogger(Management.class.getName());

	/**
	 * constructor
	 * 
	 * @param warehouse: Main warehouse.
	 * @param chefLock: the same Lock that the chefs have.
	 * @param deliveryLock: the same Lock that the deliveries men have.
	 * @param finishedOrders: the same BlockingQueue that the chefs have for the cooked orders.
	 * 	Must be LinkedBlockQueue - we don't have supremum
	 * @param menu: Restaurant menu contains all the dishes
	 * @param smFinishedChefs: the same Semaphore that the chefs have.
	 * 						use to know when all chefs finished running.
	 */
	public Management(WarehouseImpl warehouse, Lock chefLock, Lock deliveryLock, 
			LinkedBlockingQueue<Order> finishedOrders, Menu menu, 
			Semaphore smFinishedChefs, Semaphore smFinishedDelivering)
	{		
		this.m_lnklstOrdersList = new LinkedList<Order>();
		this.m_arrlstChefList = new ArrayList<RunnableChef>();
		this.m_arrlstDeliveryMenList = new ArrayList<RunnableDeliveryPerson>();
		this.m_whMainWarehouse = warehouse; 
		this.m_rndmDeliveryManage = new RunnableDeliveryManagment(finishedOrders,deliveryLock);
		this.m_mnDishMenu = menu;
		this.m_smChefCounter = smFinishedChefs;
		this.m_smDeliveryCounter = smFinishedDelivering;
		this.m_lockChefLocker = chefLock;
	}

	/**
	 * This method starts the management's simulation
	 */
	public void startSimulation()
	{		
		try
		{
			this.initializeManagment();
			this.handleOrders();
			this.shutDown();
		}
		catch (Exception ex)
		{
			logger.severe("Management simulation got interrupted because " + ex.getMessage());
		}
	}

	/**
	 * initialize this orders, chefs and deliveries.
	 * sort and start the treads. 
	 * @exception ClassCastException - occurs when the collection could not be sorted due to bad parsing
	 */
	private void initializeManagment()
	{
		try
		{
			/* Sorts the orders by their number of tools
			 * Sorting the  tools prevent deadlock
			 */
			Collections.sort(this.m_lnklstOrdersList);
		}
		catch (ClassCastException ccex)
		{
			logger.warning("Collection of orders contains non order element");
			throw ccex;
		}
		catch (Exception ex)
		{
			logger.warning("Orders are not sorted the way we wanted. reason:\n" +
					ex.getMessage());
		}

		// Starts the delivery management
		Thread deliveryManageTread = new Thread(this.m_rndmDeliveryManage);
		deliveryManageTread.start();

		// Initializes the chefs
		initializeChefs();		
	}

	/**
	 * initialize chef's threads and sort the chefs collection
	 * @exception ClassCastException - occurs when the collection could not be sorted 
	 * due to non chef element in the collection
	 */
	private void initializeChefs()
	{
		try
		{
			/* Sorts the chefs by their efficiency
			 * Sorting the  tools prevent deadlock
			 */
			Collections.sort(m_arrlstChefList);
		}
		catch (ClassCastException ccex)
		{
			logger.warning("Collection of chefs contains non chef element");
			throw ccex;
		}
		catch (Exception ex)
		{
			logger.warning("Chefs are not sorted the way we wanted. reason:\n" +
					ex.getMessage());
		}

		// Starts running the chefs
		for(int i = 0; i<this.m_arrlstChefList.size();i++){
			Thread chefTread = new Thread(this.m_arrlstChefList.get(i));
			chefTread.start();
		}
	}

	/**
	 * manage the order cooking and delivery
	 * for each order in "orders", find chef that can cook it
	 * @throws InterruptedException
	 */
	public void handleOrders () throws InterruptedException
	{ 
		// Goes over the orders list
		while(!(this.m_lnklstOrdersList.isEmpty())){

			logger.info("NUMBER OF ORDERS TO COOK: "+this.m_lnklstOrdersList.size());

			// Gets the first order
			Order orderToCook = this.m_lnklstOrdersList.poll();

			if (orderToCook != null)
			{
				logger.info("ATTEMPTING TO SEND ORDER: " + orderToCook.toString());
				//search for an available chef
				findAvilableChef(orderToCook);
			}
			else
			{
				logger.info("GOT NULL IN THE ORDERS LIST");
			}
		}
		logger.info("FINISH COOKING ORDERS");
	}

	/**
	 * This method adds new uncooked order to the order's list
	 * @param ordNewOrder - New Order
	 */
	public void addOrder(Order ordNewOrder)
	{
		if ((ordNewOrder != null) &&(ordNewOrder.getStatus() == Order.OrderStatus.INCOMPLETE))
		{
			this.m_lnklstOrdersList.add(ordNewOrder);
		}
	}

	/**
	 * This method hires new delivery person to the restaurant
	 * @param rndDelPerson - new RunnableDeliveryPerson
	 */
	public void addDeliveryMan(RunnableDeliveryPerson rndDelPerson)
	{
		if (rndDelPerson != null)
		{
			this.m_rndmDeliveryManage.addDeliveryPerson(rndDelPerson);
			this.m_smDeliveryCounter.release();
		}
	}

	/**
	 * This method hires new chef to the restaurant 
	 * @param rndChef - new RunnableChef
	 */
	public void addChef(RunnableChef rndChef)
	{
		if (rndChef != null)
		{
			this.m_arrlstChefList.add(rndChef);
			this.m_smChefCounter.release();
		}
	}	

	/**
	 * find available chef. if there isn't, wait.
	 * @param orderToCook: order that we need to send to a chef
	 * @throws InterruptedException
	 */
	private void findAvilableChef(Order orderToCook) throws InterruptedException{
		boolean chefFounded = false;

		// Don't stop until a chef is available to cook the order
		while(!chefFounded)
		{			
			// Sets the locker to false
			LockForChefs();

			// Searches for an available chef
			chefFounded = SearchForChef(orderToCook);

			if(!chefFounded)
			{
				// Wait if none of the chefs are available
				WaitForAvailableChef(chefFounded);
			}
		}
	}

	/**
	 * This method sends an order for an available
	 * @param orderToCook - Order for cooking
	 * @param chefFounded - 
	 * @return
	 * @throws InterruptedException
	 */
	private boolean SearchForChef(Order orderToCook) throws InterruptedException 
	{
		boolean bChefFound = false;

		// Goes over the the chefs until finds the first one available
		for(int i=0 ; (!bChefFound) && (i < this.m_arrlstChefList.size()) ; i++)
		{
			RunnableChef rnCurChef = this.m_arrlstChefList.get(i);

			// Check if the chef is available
			if(rnCurChef.isAvailableToCookTheOrder(orderToCook))
			{
				// adds the order to the chef
				bChefFound = true;
				rnCurChef.addNewOrder(orderToCook);
			}
		}
		return bChefFound;
	}

	/**
	 * This method  waits for available chef if needed
	 * @throws InterruptedException - while waiting
	 */
	private void WaitForAvailableChef(boolean chefFounded)
	throws InterruptedException {
		synchronized (this.m_lockChefLocker) {
			// If a chef finished an order between the check and now, check again
			if(!this.m_lockChefLocker.isTrue())
			{
				logger.info("No available chef, WAITING.");
				m_lockChefLocker.wait();
				logger.info("Managenent WOKEN UP. searching for available chef.");
			}
		}
	}

	/**
	 * This method sets the locker for the found chefs as false
	 */
	private void LockForChefs() {
		// Lock when chef was not found
		synchronized (this.m_lockChefLocker) {
			this.m_lockChefLocker.falseLock();
		}
	}

	/**
	 * manage the chefs and delivers shut down
	 * check if a chef finish cooking, if finish-shut down (for each chef)
	 * when all chef shut down, shut down the delivery 
	 */
	public void shutDown()
	{
		logger.info("Start SHUTDOWN request for the simulation");

		try
		{
			// Shuts down the chefs
			shutDownChefs();
			waitForShutChefs();

			//shut down deliveries
			logger.info("finished shutdown on chefs. SHUTDOWN DELIVERY");
			this.m_rndmDeliveryManage.shutdown();
			this.waitForShutDeliveries();
		}
		catch (Exception e) {
			logger.severe("Could not shut down : " + e.getMessage());
		}

		logger.info("MANAGMENT FINISH RUNNING.\n" + Statistics.getOnlyInstanne().toString());
	}

	/**
	 * This method shuts down all the chefs
	 */
	private void shutDownChefs() {
		//shut down chefs
		logger.info("Sending SHUTDOWN requests to the chefs");
		for(int i=0; i<this.m_arrlstChefList.size();i++){
			this.m_arrlstChefList.get(i).shutdown();
		}
	}

	/**
	 * This method waits until all chefs shutted down
	 */
	private void waitForShutChefs() {
		// Wait for all the chefs to finishing the orders
		synchronized (this.m_smChefCounter) {
			if (this.m_smChefCounter.availablePermits()!=0)
			{
				logger.info("Waiting for CHEFS SHUTDOWN");
				try 
				{
					this.m_smChefCounter.wait();
				} 
				catch (InterruptedException e) 
				{
					logger.severe("Shutdown stopped waiting for chefs due to an exception : " + e.getMessage());
				}
			}	
		}
	}

	/**
	 * This method waits for all the delivery men to finish their missions shutdown
	 */
	private void waitForShutDeliveries()
	{
		// Wait for all the orders to be delivered
		synchronized (this.m_smDeliveryCounter) {
			if (this.m_smDeliveryCounter.availablePermits() != 0)
			{
				logger.info("Waiting for the Deliveries Shutdown");

				try
				{
					this.m_smDeliveryCounter.wait();
				}
				catch (InterruptedException e) {
					logger.severe("Shutdown stopped waiting for delivering orders due to an exception : " + e.getMessage());
				}
			}
		}
	}

	/**
	 * toString override
	 */
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Management [");
		builder.append("statics=\n");
		builder.append(Statistics.getOnlyInstanne().toString());
		builder.append(", ");
		if (m_mnDishMenu != null) {
			builder.append("menu=\n");
			builder.append(m_mnDishMenu.toString());
			builder.append(", ");
		}
		if (m_lnklstOrdersList != null) {
			builder.append("orders:");
			for(int i=0 ; i<this.m_lnklstOrdersList.size();i++){
				if (m_lnklstOrdersList.get(i) != null) {
					builder.append("\n");
					builder.append("order number ");
					builder.append(i+1);
					builder.append(":\n");
					builder.append(m_lnklstOrdersList.get(i).toString());
				}
			}
		}
		builder.append("\n");
		if (m_arrlstChefList != null) {
			builder.append("chefs:");
			for(int i=0 ; i<this.m_arrlstChefList.size();i++){
				if (m_arrlstChefList.get(i) != null) {
					builder.append("\n");
					builder.append("chef number ");
					builder.append(i+1);
					builder.append(":\n");
					builder.append(m_arrlstChefList.get(i).toString());
				}
			}
		}
		builder.append("\n");
		if (m_arrlstDeliveryMenList != null) {
			builder.append("deliveryMen:");
			for(int i=0 ; i<this.m_arrlstDeliveryMenList.size();i++){
				if (m_arrlstDeliveryMenList.get(i) != null) {
					builder.append("\n");
					builder.append("delivery number ");
					builder.append(i+1);
					builder.append(":\n");
					builder.append(m_arrlstDeliveryMenList.get(i).toString());
				}
			}
		}
		builder.append("\n");
		if (m_whMainWarehouse != null) {
			builder.append("warehouse=\n");
			builder.append(m_whMainWarehouse.toString());
		}
		builder.append("]");
		return builder.toString();
	}
}
