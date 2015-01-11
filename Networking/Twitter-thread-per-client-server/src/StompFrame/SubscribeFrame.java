package StompFrame;

import java.util.HashMap;

import Connection.MessageCounter;
import Users.Client;
import Users.ClientManagment;

public class SubscribeFrame extends StompFrame{

	private String _destination;
	private String _id;

	private final String DEST_HEAD = "destination";
	private final String ID_HEAD = "id";

	/**
	 * constructor
	 * @param headers: frame information
	 */
	public SubscribeFrame(HashMap<String, String> headers){
		super(headers);
		initFrame();
	}
	
	/**
	 * start the following
	 * @param toFollow
	 * @param follower
	 * @param counter
	 * @return successful subscribe
	 */
	public MessageFrame getSuccessFrame(Client toFollow,
			Client follower, MessageCounter counter)
	{
		follower.startFollowing(toFollow, this._id);
		
		String msg = ("Now following " + this._destination);
		
		return (new MessageFrame(this._destination,this._id,
				msg, counter,false));
	}
	
	/**
	 * @return errorFrame with the message "already following"
	 */
	public ErrorFrame errorAlredyFollowFrame(){
		String msg = ("Already following "
				+this._destination);
		return new ErrorFrame("already following",msg);
	}
	
	/**
	 * @return errorFrame with the message "can't follow yourself"
	 */
	public ErrorFrame errorCantFollowYourselfFrame(){
		return new ErrorFrame("follow yourself",
				"Can't follow yourself");
	}
	
	/**
	 * @return errorFrame with the message 
	 * "user doesn't exist"
	 */
	public ErrorFrame errorUserNotExistFrame(){
		String msg = (this._destination
				+ " doesn't exist");
		return new ErrorFrame("doesn't exist",msg);
	}

	/**
	 * Initialize the frame fields according to the
	 * Information in the father's hash map.
	 */
	@Override
	public void initFrame() {
		this._destination = getField(DEST_HEAD);
		this._id = getField(ID_HEAD);
	}
	
	/**
	 * @param userFollowing
	 * @return return true if the client is following 
	 * the user in the destination field.
	 */
	public boolean isFollowing(Client userFollowing){
		return userFollowing.isFollowing(this._destination);
	}
	
	/**
	 * @param clientManage
	 * @return true if the user exist in the client management
	 */
	public Client isUserExist(ClientManagment clientManage){
		return clientManage.findClient(this._destination);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SUBSCRIBE\ndestination:");
		builder.append(_destination);
		builder.append("\nid:");
		builder.append(_id);
		return builder.toString();
	}


}
