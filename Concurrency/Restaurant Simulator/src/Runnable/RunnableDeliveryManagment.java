package Runnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import Restaurant.Order;
import System.Lock;

/**
 * This class manage the delivering process.
 * Send cooked orders to the deliveries and shut them down 
 * when needed.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */

public class RunnableDeliveryManagment implements Runnable {

	private Boolean isShutdown;
	private LinkedBlockingQueue<Order> ordersToDeliver;
	private ArrayList<RunnableDeliveryPerson> arr_deliveryMen;
	private Lock lock;

	public static final Logger logger = Logger.getLogger(RunnableDeliveryManagment.class.getName());

	/**
	 * Constructor
	 * 
	 * @param delivery: the collection of deliveries from Management
	 * @param ordersToDeliver: the same finished orders 
	 * BlockingQueue in every chef.
	 * @param lock: lock use to check if there is an available delivery
	 */
	public RunnableDeliveryManagment(LinkedBlockingQueue<Order> ordersToDeliver,Lock lock)
	{
		this.arr_deliveryMen = new ArrayList<RunnableDeliveryPerson>();
		this.ordersToDeliver = ordersToDeliver;
		this.isShutdown = false;
		this.lock = lock;
	}

	/**
	 * This method adds delivery person to the delivery management's collection
	 * @param rndDelMan - RunnableDeliveryPerson
	 */
	public void addDeliveryPerson(RunnableDeliveryPerson rndDelMan)
	{
		if (rndDelMan != null)
		{
			this.arr_deliveryMen.add(rndDelMan);
		}
	}

	/**
	 * For each order, find available delivery and send the order. 
	 */
	@Override
	public void run() {
		try
		{
			logger.info("START DELIVERY MANAGEMENT");
			initDelivery();
			handleDeliveries();
		}
		catch (Exception ex)
		{
			logger.severe("RunnableDeliveryManagement has an EXCEPTION!! reason: " + ex.getMessage());
			shutdownDeliveryPeople();
		}

		logger.info("DELIVERY MANAGEMENT FINISH RUNNING");
	}

	/**
	 * Initialize deliverys's threads and deliveryMangment thread
	 */
	public void initDelivery(){
		Collections.sort(arr_deliveryMen);

		// Start the delivery people
		for(int i = 0; i<this.arr_deliveryMen.size();i++){
			Thread deliveryTread = new Thread(this.arr_deliveryMen.get(i));
			deliveryTread.start();
		}
	}

	/**
	 * This method sends orders to the right delivery person
	 * @throws InterruptedException
	 */
	private void handleDeliveries() throws InterruptedException {
		// Goes until the delivery manangemt was shut down
		boolean b = false;
		synchronized (this) {
			b = isShutdown;
		}
		while (!b){
			Order orderToSendToDeliver = null;

			// Takes an order that needs to be delivered 
			orderToSendToDeliver = this.ordersToDeliver.take();

			if ((orderToSendToDeliver != null) && 
					(orderToSendToDeliver.getStatus() == Order.OrderStatus.COMPLETE))
			{
				//searching for an available delivery
				findAvailableDeliver(orderToSendToDeliver);
			}

			synchronized (this) {
				b = isShutdown;
			}
		}

		shutdownDeliveryManagement();
	}

	/**
	 * Finds available delivery
	 * 
	 * @param orderToSendToDeliver: order to deliver
	 */
	public void findAvailableDeliver(Order orderToSendToDeliver) throws InterruptedException
	{
		// In case all deliveries are unavailable
		synchronized (this.lock) {
			this.lock.falseLock();
		}
		boolean deliveryMenFound = false;

		while (!deliveryMenFound)
		{
			// Goes over the deliveries people (sorted by their speed)
			for(int i=0; !deliveryMenFound && i<this.arr_deliveryMen.size();i++)
			{
				RunnableDeliveryPerson rdDeliver = this.arr_deliveryMen.get(i);

				// Send the order to the current delivery person
				if(rdDeliver.canMakeADelivery()){
					deliveryMenFound = true;
					rdDeliver.addNewOrder(orderToSendToDeliver);
				}
			}
			//if isn't available delivery, wait.
			if(!deliveryMenFound){
				unavailableDeliveryPerson();
			}
		}
	}

	/**
	 * This method locks when non of the delivery people are available
	 * @param deliveryMenFound
	 */
	private void unavailableDeliveryPerson() 
	{
		synchronized (this.lock) {
			// If none of the delivery people finished their orders
			if(!this.lock.isTrue()){
				try {
					lock.wait();
				} catch (InterruptedException e) {
					logger.warning("DeliveryManagement stopped waiting");
				}
			}
		}
	}

	/**
	 * Shut down the delivery management.
	 */
	public synchronized void shutdown() throws Exception
	{
		logger.info("DelivaryManagmant begin SHUTDOWN");

		this.isShutdown = true;
		
		// Inserts fake order to exit the blocking queue
		Order ordFake = new Order(null, null, null);
		this.ordersToDeliver.put(ordFake);
	}

	/**
	 * This method shuts down all the delivery people
	 */
	private void shutdownDeliveryManagement() throws InterruptedException{
		// Does shut down only when all the orders were delivered
		while(!this.ordersToDeliver.isEmpty()){
			Order orderToDeliver = this.ordersToDeliver.poll();
			//searching for an available delivery
			findAvailableDeliver(orderToDeliver);
		}

		// After all the orders were delivered - shuts the workers
		this.shutdownDeliveryPeople();
	}

	/**
	 * Shut down the deliveries people
	 */
	private void shutdownDeliveryPeople()
	{
		logger.info("SHUTINGDOWN all the delivey people");
		for(int i=0;i<this.arr_deliveryMen.size();i++){
			this.arr_deliveryMen.get(i).shutdown();
		}
	}

}
