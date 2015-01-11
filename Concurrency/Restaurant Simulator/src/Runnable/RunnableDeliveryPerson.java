package Runnable;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import Restaurant.Order;
import Restaurant.Statistics;
import System.Lock;
import System.Point;

/**
 * This class represents a delivery person.
 * Simulat a delivery and update the statistic.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */

public class RunnableDeliveryPerson implements Runnable,Comparable<RunnableDeliveryPerson> {

	private final String name;
	private final Point resturantAddress;
	private final double speed;
	private final LinkedBlockingQueue<Order> orderToDelivere;
	private boolean isShutDown;
	private boolean isAvailable;
	private Lock lock;
	private final int DEFAULT_SPEED = 1;
	private final int QUEUE_CAPACITY = 1;
	private ArrayList<Order> arr_deliverdOrders;
	private Semaphore m_smFinishedDelvieries;

	Logger logger = Logger.getLogger(RunnableDeliveryPerson.class.getName());
	
	/**
	 * Constructor
	 * 
	 * @param name: Delivery's name
	 * @param speed: Delivery's speed
	 * @param address: Resturant's address
	 * @param lock: lock use to check if there is an available delivery
	 */
	public RunnableDeliveryPerson(String name, double speed, Point address,Lock lock,
			Semaphore smFinishedDel){
		this.name = name;
		this.resturantAddress = address;
		this.lock = lock;
		this.orderToDelivere = new LinkedBlockingQueue<Order>(QUEUE_CAPACITY);
		this.isShutDown = false;
		//speed can't be 0!
		if(speed==0) this.speed = DEFAULT_SPEED;
		else this.speed = speed;
		this.isAvailable = true;
		this.arr_deliverdOrders = new ArrayList<Order>();
		this.m_smFinishedDelvieries = smFinishedDel;
	}
	
	/**
	 * delivering method.
	 * calculate order's delivering time, sleep while delivering and
	 * get the reward. wait when there is no order to deliver1
	 */
	@Override
	public void run() {
		logger.info("DeliveryPerson [" + this.name + "] START RUNING");
		
		boolean bIsShutDown = false;

		// Runs until shutted down
		while(!bIsShutDown)
		{
			Order nowDelivering;
			try 
			{
				// Attempt to take an order
				nowDelivering = this.orderToDelivere.take();				
				synchronized (this) 
				{
					bIsShutDown = this.isShutDown;
				}

				// If found a real order
				if ((nowDelivering != null) && (nowDelivering.getStatus() == Order.OrderStatus.COMPLETE))
				{
					// Delivers the order
					deliverOrder(nowDelivering);
				}
			} 
			catch (InterruptedException e1) {
				logger.warning("Delivery Person " + this.name + " threw an exception : " + e1.getMessage());
			}
		}
		try
		{
			// Tells the management that the deliver had finished working
			if (m_smFinishedDelvieries.availablePermits() != 0)
			{
				this.m_smFinishedDelvieries.acquire();
			}
			
			// Wakes the manangement if it's the last delivery person
			synchronized (m_smFinishedDelvieries) {				
				if (m_smFinishedDelvieries.availablePermits() == 0)
				{
					this.m_smFinishedDelvieries.notify();
				}
			}
		}
		catch (Exception ex)
		{
			logger.severe("Delivery Person [" + this.name + "] could not release semaphore");
		}
		logger.info("DeliveryPerson [" + this.name + "] FINISHED RUNING");
	}

	/**
	 * This method delivers a given order to the correct delivery person
	 * @param nowDelivering - Order to deliver
	 */
	private void deliverOrder(Order nowDelivering) {
		synchronized (this) {
			this.isAvailable=false;
		}

		// Calculates the delivery time
		long deliveryTime = nowDelivering.calculateDeliveryTime(
				this.resturantAddress,this.speed);
		
		logger.info("DeliveryPerson [" + this.name + "] START DELIVERING "
				+ nowDelivering.orderInfoLog());
	
		//sleep during driving to the customer
		nowDelivering.startDelivering();
		try {
			Thread.sleep(deliveryTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Updates the delivered order
		double orderReward = nowDelivering.delivered(deliveryTime);
		finishDelivery(orderReward, nowDelivering, deliveryTime);
	}

	/**
	 * Check if this deliveryMen can deliver a new order
	 * 
	 * @return true if this can deliver an order. else-false.
	 */
	public synchronized boolean canMakeADelivery()
	{
		return ((!this.isShutDown) && (this.orderToDelivere.isEmpty()) && this.isAvailable);
	}

	/**
	 * Adds order to deliver
	 * 
	 * @param order: order to deliver
	 */
	public synchronized void addNewOrder(Order order){
		if(order!=null && this.orderToDelivere.isEmpty()){
			this.orderToDelivere.add(order);
		}
	}
	
	/**
	 * finish the delivery.
	 * update statistic and add to delivered orders collection.
	 * 
	 * @param orderReward: the actual reward from the order.
	 * @param nowDelivering: the delivered order
	 * @param deliveryTime: expected delivering time.
	 */
	public void finishDelivery(double orderReward, Order nowDelivering ,long deliveryTime)
	{
		updateStatistics(orderReward, nowDelivering);
		this.arr_deliverdOrders.add(nowDelivering);

		//sleep during driving back to the restaurant
		try {
			Thread.sleep(deliveryTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		logger.info(nowDelivering.orderDeliverdLog(deliveryTime, orderReward));
		
		synchronized (this) {
			this.isAvailable = true;	
		}

		synchronized (this.lock) {
			this.lock.notifyAll();
			this.lock.trueLock();

		}
	}

	/**
	 * This method updates the statistics with the new reward and the delivered order
	 * @param orderReward - reward gained for the current order
	 * @param nowDelivering - delivered order
	 */
	private void updateStatistics(double orderReward, Order nowDelivering) {
		//edit statistic
		Statistics.getOnlyInstanne().gainMoney(orderReward);
		Statistics.getOnlyInstanne().addOrder(nowDelivering);
	}
	
	/**
	 * Shutdown the delivery's thread
	 */
	public void shutdown()
	{
		synchronized (this) {
			logger.info("DeliveryPerson [" + this.name + "] has been SHUTDOWN");
			this.isShutDown = true;
		}
		
		try {
			// Sends fake order to end the delivery dude
			Order orFake = new Order(null, null, null);
			this.orderToDelivere.put(orFake);
		} catch (InterruptedException e) {
			logger.warning("Delivery person " + this.name + " may be not ended, because of : "+ 
							e.getMessage());
		}
	}

	/**
	 * Override "comperTo" - comper by speed.
	 */
	public int compareTo(RunnableDeliveryPerson otherDeliveryPerson) {
		return (int)(otherDeliveryPerson.speed-this.speed);
	}
}
