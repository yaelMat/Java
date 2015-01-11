package System;
/**
 * This class represents lock objects that needs to represent True/False state 
 * parallel computing environment.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class Lock {
	private boolean freeChef;
	
	/**
	 * Default constuctor
	 */
	public Lock()
	{
		this.freeChef = false;
	}
	
	/**
	 * Change lock to "false"
	 */
	public synchronized void falseLock(){
		this.freeChef = false;
	}
	
	/**
	 * Change lock to "true"
	 */
	public synchronized void trueLock(){
		this.freeChef = true;
	}
	
	/**
	 * Check if the lock is "true"
	 */
	public synchronized boolean isTrue(){
		return this.freeChef;
	}
}
