package Warehouse;
import System.Lock;

/**
 * This class represents an ingredient in a warehouse or needed to cook a dish.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class Ingredient implements IWarehouse {

	private final String m_fstrName;
	private int m_fnQuantity;
	private Lock m_flockQuantityLocker;

	/**
	 * Constructor
	 * 
	 * @param name: Ingredient name
	 * @param quantity: Ingredient quantity
	 */
	public Ingredient(String name, int quantity) 
	{
		this.m_fstrName = name;
		this.m_fnQuantity = quantity;
		this.m_flockQuantityLocker = new Lock();
	}

	/**
	 * Copy constructor
	 * 
	 * @param other: Ingredient to copy
	 */
	public Ingredient(Ingredient other) 
	{
		this.m_fstrName = other.m_fstrName;
		this.m_fnQuantity = other.m_fnQuantity;
		this.m_flockQuantityLocker = new Lock();
	}

	/**
	 * Reduce this ingredient's quantity according to the 
	 * other ingredient's quantity. if there is enough 
	 * ingredients - if not, wait.
	 * 
	 * @param other: Ingredient that has the quantity 
	 * needed to reduce from this. 
	 */
	public void reduceQuantity(IWarehouse other) 
	{
		synchronized (this.m_flockQuantityLocker) {
			if(other.getQuantity() > 0)
			{
				this.m_fnQuantity = (this.m_fnQuantity - other.getQuantity());
			}	
		}
	}

	/**
	 * This method gets the warehouse's quantity
	 * 
	 * @return the quantity of the item
	 */
	public int getQuantity() 
	{
		return (this.m_fnQuantity);
	}

	/**
	 * Add to this ingredient's quantity according to the 
	 * other ingredient's quantity. 
	 * 
	 * @param other: Ingredient that has the quantity 
	 * needed to add to this. 
	 */
	public void addQuantity(IWarehouse other)
	{
		synchronized (this.m_flockQuantityLocker) {
			this.m_fnQuantity = (this.m_fnQuantity + other.getQuantity());
		}
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
	 * Override "compareTo" - compare by name.
	 * if the given item is not an ingredient - returns -1
	 */
	@Override
	public int compareTo(IWarehouse other) 
	{
		if (other instanceof Ingredient)
		{
			return (this.m_fstrName.compareTo(((Ingredient)other).m_fstrName));
		}
		else
		{
			return -1;
		}
	}

	/**
	 * Override "equals" - compare by name.
	 */
	@Override
	public boolean equals(Object other) 
	{
		if(other instanceof Ingredient){
			return (this.m_fstrName.equals(((Ingredient)other).m_fstrName));
		}
		return false;
	}

	/*
	 * toString override
	 */
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		if (m_fstrName != null) {
			builder.append("name=");
			builder.append(m_fstrName);
			builder.append(", ");
			builder.append("quantity=");
			builder.append(m_fnQuantity);
		}
		builder.append("]");
		return builder.toString();
	}
}
