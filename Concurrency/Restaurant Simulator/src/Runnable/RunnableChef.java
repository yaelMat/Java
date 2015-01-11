package Runnable;

import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import Callable.CallableCookWholeOrder;
import Restaurant.Order;
import System.Lock;
import Warehouse.WarehouseImpl;

/**
 * This class represents a singel in the resturant.
 * Responsible to cook all the orders that the Managment send
 * and send the cooked orders to delivery.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */

public class RunnableChef implements Runnable,Comparable<RunnableChef> {
	private final String m_fstrName;
	private final double m_fdEfficiencyRating;
	private final int m_fnEnduranceRating;
	private int m_nPressure;
	private Vector<Future<Order>> m_vecfutordCurrentCookingOrders;
	private boolean m_bIsShutDown;
	private Lock m_lockChefBusyLocker;
	private ExecutorService m_exserCookingOrders;
	private final WarehouseImpl m_fwhMainWarehouse;
	private LinkedBlockingQueue<Order> m_lnkbqFinishedOrders;
	private LinkedList<Order> m_lnklstordUncookedOrders;
	private Semaphore m_smFinishedChefs;
	private Logger m_logChefLogger = Logger.getLogger(RunnableChef.class.getName());
	
	final int START_PRESSURE = 0;

	/**
	 * Constructor
	 * 
	 * @param name: chef's name
	 * @param efficiencyRating: the chef's efficincy 
	 * (Good=0.9,Normal=1,Bad=1.1)
	 * @param enduranceRating: the chef's endurance 
	 * @param finishedOrders: the same BlockingQueue in DeliveryManagment
	 * @param warehouse: The main warehouse from Managment
	 * @param lock:  the same Lock that the Managment have.
	 * @param smFinishedChefs: the same Semaphore that the Managment
	 *  have. Use to know when all chefs finished running.
	 */
	public RunnableChef(String name, double efficiencyRating, int enduranceRating, 
			LinkedBlockingQueue<Order> finishedOrders ,WarehouseImpl warehouse, 
			Lock lock, Semaphore smFinishedChefs)
	{
		this.m_fstrName = name;
		this.m_fdEfficiencyRating = efficiencyRating;
		this.m_fnEnduranceRating = enduranceRating;
		this.m_lockChefBusyLocker = lock;
		this.m_fwhMainWarehouse = warehouse;
		this.m_nPressure = START_PRESSURE;
		this.m_vecfutordCurrentCookingOrders = new Vector<Future<Order>>();
		this.m_lnklstordUncookedOrders = new LinkedList<Order>(); 
		this.m_lnkbqFinishedOrders = finishedOrders;
		this.m_bIsShutDown = false;
		this.m_smFinishedChefs = smFinishedChefs;
		this.m_exserCookingOrders = Executors.newCachedThreadPool();
	}

	/**
	 * running method.
	 * check if we have an order waiting. create thread and add it to the pool.
	 * check if a thread finished to run.
	 */
	@Override
	public void run() 
	{
		m_logChefLogger.info("CHEF [" + this.m_fstrName + "] START RUNNING.");

		try
		{
			// Starts the chef's ordinary work
			chefWork();		
			shutdownChef();
		}
		catch (Exception ex)
		{
			m_logChefLogger.warning("Chef " + this.m_fstrName + " threw an exception : "+ ex.getMessage());
			this.shutdownChef();
		}

		m_logChefLogger.info("CHEF ["+m_fstrName + "] FINISH RUNNING");
	}

	/**
	 * This method does the chef's regular work - 
	 * 	receives orders and sends to the delivery when finished
	 */
	private void chefWork() {

		// Goes until shutdown request
		while(!this.m_bIsShutDown)
		{
			// If an order received - starts cooking
			if(!(this.m_lnklstordUncookedOrders.isEmpty())){
				Order cookThisOrder = null;

				// Gets the order to cook
				synchronized (this) {
					cookThisOrder = this.m_lnklstordUncookedOrders.poll();	
				}
				
				startCookingNewOrder(cookThisOrder);
			}

			// Sends all the finished orders to delivery
			searchForFinishedOrders();
		}
	}
	
	/**
	 * check if the chef isn't shutdown and can cook
	 * the order (according to his pressure).
	 * 
	 * @param order: order to check if this can cook
	 * @return true if this chef can cook the order. else-false.
	 */
	public synchronized boolean isAvailableToCookTheOrder(Order order)
	{
		if (!this.m_bIsShutDown){
			// Checks if the chef accepted the order
			boolean available = order.canTheChefCookTheOrder(this.m_fnEnduranceRating-this.m_nPressure);

			//log if the chef accepted/rejected the order
			if(available) 
				m_logChefLogger.info(this.m_fstrName+" ACCEPTED ORDER: " + order.orderInfoLog());
			else m_logChefLogger.info(this.m_fstrName + " REJECTED ORDER : " + order.orderInfoLog());

			return available; 
		}
		m_logChefLogger.info("Chef " + this.m_fstrName + "" +
				"can't accept orders - shut down" + order.orderInfoLog());
		return false;
	}

	/**
	 * if the order available, add the new order to the chef and updates his pressure.
	 * 
	 * @param newOrder: order to add.
	 * @throws InterruptedException
	 */
	public synchronized void addNewOrder(Order newOrder) throws InterruptedException
	{
		if(newOrder != null){
			// Updates the chef's pressure and tells him to cook
			this.m_nPressure = newOrder.calculatePressureToAdd(this.m_nPressure);
			this.m_lnklstordUncookedOrders.addLast(newOrder);
		}
	}

	/**
	 * create callable for a new order, and add it to
	 * the current cooking orders
	 */
	public void startCookingNewOrder(Order cookOrder){
		if (cookOrder != null)
		{
			// Starts cooking the order
			Callable<Order> newOrder = new CallableCookWholeOrder(cookOrder, 
					this.m_fdEfficiencyRating, this.m_fwhMainWarehouse);
			Future<Order> future = m_exserCookingOrders.submit(newOrder); 

			// Adds the future result to the collection
			this.m_vecfutordCurrentCookingOrders.add(future);
		}
	}

	/**
	 * check if there is a thread that finished it's running, delete it
	 * from the vector, reduced the chef's pressure and send it to delivery.
	 * the chef will notify that he finish to cook a dish.
	 */
	public void searchForFinishedOrders()
	{
		// Goes over the future orders
		for(int i=0 ; i<this.m_vecfutordCurrentCookingOrders.size();i++){
			Future<Order> futCurOrder = this.m_vecfutordCurrentCookingOrders.elementAt(i);

			//check if the order finished to cook
			if(futCurOrder.isDone()){
				// handle and deliver finished order
				handleFinishedOrder(futCurOrder);
				
				// Removes the finished order from the collection
				this.m_vecfutordCurrentCookingOrders.remove(i);
				i--;
			}
		}
	}

	/**
	 * This method handles a finished order
	 * @param futCurOrder  - Future of finished order
	 */
	private void handleFinishedOrder(Future<Order> futCurOrder) 
	{
		Order cookedOrder = null;
		try 
		{
			// Gets finished order 
			cookedOrder = futCurOrder.get();
			reducePressure(cookedOrder);
		} 
		catch (InterruptedException e) 
		{
			m_logChefLogger.warning("Exception while trying to get future order : " + e.getMessage());
		} 
		catch (ExecutionException e) 
		{
			m_logChefLogger.warning("Exception while trying to get future order : " + e.getMessage());
		}

		// Sends the order to delivery and notifies the management
		unlockManagement();
		sendOrderToDelivery(cookedOrder);
	}

	/**
	 * This method sends a given order to delivery
	 * @param cookedOrder - Finished cooked order
	 */
	private void sendOrderToDelivery(Order cookedOrder) {
		try
		{
			if ((cookedOrder != null) && (cookedOrder.getStatus() == Order.OrderStatus.COMPLETE))
			{
				//send to delivery
				this.m_lnkbqFinishedOrders.put(cookedOrder);
			}
		}
		catch (InterruptedException iex)
		{
			m_logChefLogger.warning("Exception while trying to send order to delivery : " + iex.getMessage());
		}
	}

	/**
	 * This method notifies the management that the chef is available for cooking
	 */
	private void unlockManagement() {
		//notify managment
		synchronized (this.m_lockChefBusyLocker) {
			this.m_lockChefBusyLocker.trueLock();
			this.m_lockChefBusyLocker.notify();
		}
	}

	/**
	 * This method reduces the chef's pressure
	 * @param cookedOrder - Order that the chef just finished cooking
	 */
	private synchronized void reducePressure(Order cookedOrder) 
	{
		// Reduces the chef's pressure
		this.m_nPressure = cookedOrder.calculatePressureToReduce(this.m_nPressure);
	}
	
	/**
	 * this method shutdown the chef's thread
	 */
	public synchronized void shutdown()
	{
		m_logChefLogger.info("Chef [" + this.m_fstrName + "] has been SHUTDOWN");
		this.m_bIsShutDown = true;
	}
	
	/**
	 * This method finishes the remaining chef's work
	 */
	private void finishWork() {
		// Finish the current orders  
		while(!(this.m_lnklstordUncookedOrders.isEmpty()) || !m_vecfutordCurrentCookingOrders.isEmpty())
		{
			// Cooks the order
			if(!(this.m_lnklstordUncookedOrders.isEmpty()))
			{
				Order cookThisOrder = null;

				// Gets the order to cook
				synchronized (this) {
					cookThisOrder = this.m_lnklstordUncookedOrders.poll();	
				}
				
				startCookingNewOrder(cookThisOrder);
			}

			// Delivers the orders
			searchForFinishedOrders();
		}
	}
	
	/**
	 * This method shuts down the current chef 
	 */
	private void shutdownChef() 
	{
		m_logChefLogger.info("CHEF [" + m_fstrName + "] START SHUTDOWN - finish all the orders");

		// Finishes all the orders and delivers them
		finishWork();
		
		synchronized (m_smFinishedChefs) {
			// the chef finished his work 
			this.m_smFinishedChefs.tryAcquire();
			
			// Notify the management that the chef finished all the work
			if (this.m_smFinishedChefs.availablePermits()==0)
			{
				this.m_smFinishedChefs.notify();
			}
		}
		
		// Shuts down all the cooking threads holder
		this.m_exserCookingOrders.shutdown();
	}
	
	/**
	 * Override "compereTo" - compares by efficiency
	 */
	public int compareTo(RunnableChef otherChef) 
	{
		return (int)(this.m_fdEfficiencyRating-otherChef.m_fdEfficiencyRating);
	}

	/**
	 * toString override
	 */
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("RunnableChef [");
		if (m_fstrName != null) {
			builder.append("name=");
			builder.append(m_fstrName);
			builder.append(", ");
		}
		builder.append("efficiencyRating=");
		builder.append(m_fdEfficiencyRating);
		builder.append(", enduranceRating=");
		builder.append(m_fnEnduranceRating);
		builder.append(", pressure=");
		builder.append(m_nPressure);
		builder.append("]");
		return builder.toString();
	}

}
