package Warehouse;

import java.util.concurrent.Semaphore;

import System.Lock;
/**
 * This class represents a tool in a warehouse or needed to cook a dish.
 * implements Comparable and IWarehouse
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class KitchenTool implements IWarehouse
{
	private final String m_fstrName;
	private int m_nQuantity;
	private Lock m_flockQuantityLocker;
	private final Semaphore m_smToolSemaphor;
	
	/**
	 * Constructor
	 * 
	 * @param name: Tool name
	 * @param quantity: Tool quantity
	 */
	public KitchenTool(String name, int quantity) 
	{
		this.m_fstrName = name;
		this.m_nQuantity = quantity;
		this.m_smToolSemaphor = new Semaphore(quantity);
		this.m_flockQuantityLocker = new Lock();
	}

	/**
	 * reduce this tool's quantity according to the other tool's 
	 * quantity. if there is enough tool - if not, wait.
	 * edit semaphor
	 * 
	 * @param other: Tool that has the quantity 
	 * needed to reduce fron this. 
	 */
	public void reduceQuantity(IWarehouse other) 
	{
			try {
				this.m_smToolSemaphor.acquire(other.getQuantity());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (this.m_flockQuantityLocker) {
				this.m_nQuantity = this.m_nQuantity - other.getQuantity();
			}
	}

	/**
	 * add to this tool's quantity according to the 
	 * other tool's quantity. edit semaphor
	 * 
	 * @param other: Tool that has the quantity 
	 * needed to add to this. 
	 */
	public void addQuantity(IWarehouse other) 
	{
			this.m_smToolSemaphor.release(other.getQuantity());
			synchronized (this.m_flockQuantityLocker) {
				this.m_nQuantity = this.m_nQuantity + other.getQuantity();
			}
	}

	/**
	 * Add this quantity to a tool's counter.
	 * 
	 * @param currentCounting: Tool's counter.
	 * @return Tool's counter + this tool quantity
	 */
	public int countTools(int currentCounting)
	{
		return (currentCounting+this.m_nQuantity);
	}

	/**
	 * Override "equals" - comper by name.
	 */
	@Override
	public boolean equals(Object other) 
	{
		if(other instanceof KitchenTool){
			return (this.m_fstrName.equals(((KitchenTool)other).m_fstrName));
		}
		return false;
	}

	/**
	 * Override the "hashCode" method, using name.hashCode
	 */
	@Override
	public int hashCode()
	{
		return 31*this.m_fstrName.hashCode();
	}

	/**
	 * This method compares between two kitchen tools
	 * @param arg0
	 * @return
	 */
	@Override
	public int compareTo(IWarehouse other) {
		return (this.m_fstrName.compareTo(((KitchenTool)other).m_fstrName));
	}

	/**
	 * This method gets the tool's quantity
	 */
	@Override
	public int getQuantity() {
		return this.m_nQuantity;
	}
	
	/**
	 * toString override
	 */
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("KitchenTool [");
		if (m_fstrName != null) {
			builder.append("name=");
			builder.append(m_fstrName);
			builder.append(", ");
			builder.append("quantity=");
			builder.append(m_nQuantity);
		}
		builder.append("]");
		return builder.toString();
	}
}

