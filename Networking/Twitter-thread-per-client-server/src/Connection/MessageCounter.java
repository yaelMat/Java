package Connection;
/**
 * Use to count the number of server messages safely.
 * 
 * @author MATHOV
 *
 */
public class MessageCounter {
	private int _counter;
	
	/**
	 * constructor
	 */
	public MessageCounter() {
		this._counter = 0;
	}
	

	/**
	 * Increase counting
	 * @return counting
	 */
	public synchronized int getCount(){
		this._counter++;
		return this._counter;
	}
	

}
