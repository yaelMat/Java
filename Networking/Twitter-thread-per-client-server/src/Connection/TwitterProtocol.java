package Connection;
import java.io.PrintWriter;

import Interface.ServerProtocol;
import StompFrame.*;
import Users.Client;
import Users.ClientManagment;


public class TwitterProtocol implements ServerProtocol {
	private Client _user;
	private MessageCounter _msgCounter;
	private ClientManagment _clientManage;
	private boolean isNewUser;

	private String NOT_CONNECT_MSG = "need to connect first.";


	/**
	 * constructor		
	 * @param msgCounter
	 */
	public TwitterProtocol(MessageCounter msgCounter,
			ClientManagment clientManage) {
		this._user = null;
		this._msgCounter = msgCounter;
		this._clientManage = clientManage;
		this.isNewUser = false;
	}

	@Override
	public StompFrame processMessage(StompFrame msg) {
		//ERROR frame-wrong input. 
		if (msg instanceof ErrorFrame){
			return error("Wrong input");
		}
		//check if the user connected.
		if (this._user == null){
			return error(NOT_CONNECT_MSG);
		}
		//SUBSCRIBE frame
		if (msg instanceof SubscribeFrame){
			return subscribeProcess((SubscribeFrame)msg);
		}
		//UNSUBSCRIBE frame
		if (msg instanceof UnsubscribeFrame){
			return unsubscribeProcess((UnsubscribeFrame)msg);
		}
		//DISCONNECT frame
		if (msg instanceof DisconnectFrame){
			return disconnectionProcess((DisconnectFrame)msg);
		}
		if (msg instanceof SendFrame){
			sendProcess((SendFrame)msg);
		}
		return null;
	}


	private void sendProcess(SendFrame msg) {
		if(!(msg.isServer())){
			this._user.sendTweet(msg, this._msgCounter, 
					this._user.getNameCopy());
			msg.findMantion(this._clientManage,
					this._user, this._msgCounter);
		}
		else{
			msg.serverSend(this._clientManage);
		}
	}

	/**
	 * if the id match a user, unfollow it
	 * @param msg
	 * @return the appropriate StompFrame:
	 * MessageFrame for success subscribe, Error
	 * frame for not following this user.
	 */
	public StompFrame unsubscribeProcess(UnsubscribeFrame msg){
		Client following = msg.getFollowingUser(this._user);
		if(following == null){
			return msg.getErrorNotFollowing();
		}
		return (msg.getSuccessFrame(following, this._msgCounter));
	}


	/**
	 * if the user to follow exist and not been follow
	 * by this user, add this to his followers collection.
	 * with the id in the frame.
	 * @param msg
	 * @return the appropriate StompFrame:
	 * MessageFrame for success subscribe, Error
	 * frame for already following this user and 
	 * user to follow doen't exist.
	 */
	public StompFrame subscribeProcess(SubscribeFrame msg){
		//check if already following this user
		if(msg.isFollowing(this._user)){
			return msg.errorAlredyFollowFrame();
		}
		//check if the user exist
		Client userToFollow = msg.isUserExist(this._clientManage);
		if(userToFollow == null){
			return msg.errorUserNotExistFrame();
		}
		//check if try to follow yourself 
		if(this._user == userToFollow){
			return msg.errorCantFollowYourselfFrame();
		}
		//Success subscribe
		return msg.getSuccessFrame(userToFollow, 
				this._user, this._msgCounter);
	}

	/**
	 * disconnect
	 * @param msg
	 * @return ReceiptFrame  
	 */
	public ReceiptFrame disconnectionProcess(DisconnectFrame msg){
		this._user.disconnect();
		this._user = null;
		return msg.getDisconnectFrame();
	}


	/**
	 * @param msg
	 * @return the appropriate StompFrame:
	 * ConnectedFrame for successful logging (and 
	 * new user register), Error for wrong password
	 * and already logged in or log twice.
	 */
	public StompFrame connectionProcess(ConnectFrame msg, PrintWriter outPut)
	{
		//check if try to connect twice.
		if(this._user != null){
			return msg.getCantLogTwiceFrame();
		}
		Client logClient = msg.findUser(this._clientManage);
		//check if the user exist, if not-create.
		if (logClient == null){
			this.isNewUser = true;
			this._user = msg.registerUser(this._clientManage, outPut);
			return msg.getSuccessFrame();
		}
		//check if the user is the fake user
		if(logClient.isServer()){
			return msg.getConnectToServerFrame();
		}
		//check if using the right password.
		if(!(msg.checkPassword(logClient))){
			return msg.getWrongPasswordFrame();
		}
		//check if the user already logged in
		if(logClient.isLogIn()){
			return msg.getAlreadyLoggedFrame();
		}
		//Successful logging
		this._user = logClient;
		return msg.getSuccessFrame();
	}

	public void connect(PrintWriter output){
		if(!this.isNewUser){
			this._user.connect(output);
		}
	}

	@Override
	public boolean isEnd(String msg) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param msg: error message
	 * @return ErrorFrame with the given message.
	 */
	public ErrorFrame error(String msg){
		return new ErrorFrame(msg, "");
	}

}
