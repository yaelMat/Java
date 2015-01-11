package StompFrame;

import java.util.HashMap;

import Connection.MessageCounter;
import Users.Client;

public class UnsubscribeFrame extends StompFrame{

	private String _id;

	private final String ID_HEAD = "id";

	
	/**
	 * constructor
	 * @param headers: frame information
	 */
	public UnsubscribeFrame(HashMap<String, String> headers){
		super(headers);
		initFrame();
	}

	/**
	 * Initialize the frame fields according to the
	 * Information in the father's hash map.
	 */
	@Override
	public void initFrame() {
		this._id = getField(ID_HEAD);
	}
	
	/**
	 * @param user
	 * @return return the following with the id in
	 * this frame if exist. else-null.
	 */
	public Client getFollowingUser(Client user){
		return user.getFolloingUser(this._id);
	}
	
	/**
	 * unfollow 
	 * @param user
	 * @param counter
	 * @return successful unfollow
	 */
	public MessageFrame getSuccessFrame(Client user, MessageCounter counter)
	{
		String userName = user.unfollow(this._id);
		String msg = ("Unfollowing " + userName);
		
		return (new MessageFrame(userName,this._id,
				msg, counter,false));
	}
	
	/**
	 * @return errorFrame with the message "didn't follow this user"
	 */
	public ErrorFrame getErrorNotFollowing(){
		String errorMsg = ("You are not following user with this id: "+this._id);
		return (new ErrorFrame("didn't follow",errorMsg));
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UnsubscribeFrame [_id=");
		builder.append(_id);
		builder.append("]");
		return builder.toString();
	}


}
