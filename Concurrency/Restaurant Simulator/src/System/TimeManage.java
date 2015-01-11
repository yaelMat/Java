package System;
/**
 * This class Manage the time counting and calculation.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class TimeManage {
	private long startCookingTime;
	private boolean startCookingTimeUpdated;
	private long endCookingTime;
	private boolean endCookingTimeUpdated;
	private long startDelivery;
	private boolean startDeliveryUpdated;
	private long endDelivery;
	private boolean endDeliveryUpdated;
	private final double CALC_REWARD_WITH = 1.15;
	private final double GOT_REWARD = 1;
	private final double DIDNT_GET_REWARD = 0.5;

	/**
	 * Constructor
	 */
	public TimeManage(){
		this.startCookingTime = -1;
		this.endCookingTime = -1;
		this.startDelivery = -1;
		this.endDelivery = -1;
		this.startCookingTimeUpdated = false;
		this.endCookingTimeUpdated = false;
		this.startDeliveryUpdated = false;
		this.endDeliveryUpdated = false;
	}

	/**
	 * update "start cooking time" (once)
	 */
	public void startCooking(){
		if(!(this.startCookingTimeUpdated)){
			this.startCookingTime = System.currentTimeMillis();
			this.startCookingTimeUpdated = true;
		}
	}

	/**
	 * update "start delivering time" (once) 
	 */
	public void startDelivering(){
		if(!(this.startDeliveryUpdated)){
			this.startDeliveryUpdated = true;
			this.startDelivery = System.currentTimeMillis();
		
		}
	}

	/**
	 * update "end cooking time" (once)
	 */
	public void endCooking(){
		if(!(this.endCookingTimeUpdated)){
			this.endCookingTime = System.currentTimeMillis();
			this.endCookingTimeUpdated = true;
		}
	}

	/**
	 * update "end delivering time" (once)
	 */
	public void endDelivering(){
		if(!(this.endDeliveryUpdated)){
			this.endDelivery = System.currentTimeMillis();
			this.endDeliveryUpdated = true;
		}
	}

	/**
	 * calculate cooking time
	 * @return actual cooking time
	 */
	public long calcCookingTime(){
		if(this.startCookingTimeUpdated && this.endCookingTimeUpdated){
			return (this.endCookingTime-this.startCookingTime);
		}
		return -1;
	}

	/**
	 * calculate delivering time
	 * @return actual delivering time
	 */
	public long calaDeliveringTime(){
		if(this.startDeliveryUpdated && this.endDeliveryUpdated){
			return (this.endDelivery-this.startDelivery);
		}
		return -1;
	}

	/**
	 * calculate the reward
	 * 
	 * @param expectedCookingTime
	 * @param expectedDeliveringTime
	 * @return 
	 */
	public double calculateReward(long expectedCookingTime,long expectedDeliveringTime){
		if((calcCookingTime()+calaDeliveringTime())>(CALC_REWARD_WITH*(expectedCookingTime+expectedDeliveringTime))){
			return DIDNT_GET_REWARD;
		}
		return GOT_REWARD;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeManage [startCookingTime=");
		builder.append(startCookingTime);
		builder.append(", endCookingTime=");
		builder.append(endCookingTime);
		builder.append(", startDelivery=");
		builder.append(startDelivery);
		builder.append(", endDelivery=");
		builder.append(endDelivery);
		builder.append("]");
		return builder.toString();
	}




}







