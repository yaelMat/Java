package StompFrame;

import java.io.PrintWriter;
import java.util.HashMap;
import Users.Client;
import Users.ClientManagment;

public class ConnectFrame extends StompFrame{
	private String _host;
	private String _userName;
	private String _password;
	
	private final String HOST_HEAD = "host";
	private final String USER_HEAD = "login";
	private final String PASSWORD_HEAD = "passcode";
	
	/**
	 * constructor
	 * @param headers: frame information
	 */
	public ConnectFrame(HashMap<String, String> headers){
		super(headers);
		initFrame();

	}
	
	/**
	 * Initialize the frame fields according to the
	 * Information in the father's hash map.
	 */
	@Override
	public void initFrame() {
		this._host = getField(HOST_HEAD);
		this._userName = getField(USER_HEAD);
		this._password = getField(PASSWORD_HEAD);
	}
	
	/**
	 * successful connecting
	 * @return ConnectedFarme
	 */
	public StompFrame getSuccessFrame(){
		return new ConnectedFrame();
	}
	
	/**
	 * unsuccessful connecting - user already logged in.
	 * @param userName: the user name
	 * @return Error Frame with the message "already logged in"
	 */
	public StompFrame getAlreadyLoggedFrame(){
		return new ErrorFrame("Already logged in.",
				"You're is already logged in.");
	}
	
	/**
	 * unsuccessful connecting - user used wrong password.
	 * @return Error Frame with the message "wrong password"
	 */
	public ErrorFrame getWrongPasswordFrame(){
		return new ErrorFrame("Wrong password",
				"Wrong password. please try again.");
	}
	
	/**
	 * unsuccessful connecting - can't connect to the server.
	 * @return Error Frame with the message " can't connect to the server"
	 */
	public ErrorFrame getConnectToServerFrame(){
		return new ErrorFrame("connect server.",
				"Can't connect to the server.");
	}
	
	/**
	 * unsuccessful connecting - user try to log again.
	 * @return Error Frame with the message "Can't Log twice"
	 */
	public ErrorFrame getCantLogTwiceFrame(){
		return new ErrorFrame("Already Logged in.",
				"Already Logged in. Cant Log twice. Please disconnect first.");
	}

	/**
	 * @param clientManag
	 * @return this frame user if exist. else-null.
	 */
	public Client findUser(ClientManagment clientManag){
		return clientManag.findClient(this._userName);
	}
	
	/**
	 * @param clientManag
	 * @return the new user according to this frame info.
	 */
	public Client registerUser(ClientManagment clientManag,
			PrintWriter output)
	{
		return clientManag.register(this._userName, 
				this._password, output);
	}
	
	public boolean checkPassword(Client userToCheack){
		return userToCheack.checkPassword(this._password);
	}
	
	
	@Override
	public String toString() {
		return "ConnectFrame [userName=" + _userName + ", password="
				+ _password + "]";
	}

	
	
}
