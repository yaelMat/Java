package Users;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import Connection.MessageCounter;
import StompFrame.MessageFrame;
import StompFrame.SendFrame;


public class Client implements Comparable<Client>{
	private String _userName;
	private String _password;
	private boolean _isLoggedIn;
	private ConcurrentHashMap<String, Client> hm_myFollowers;
	private ConcurrentHashMap<String, Client> hm_thisFollowing;
	private PrintWriter pw_outPut;
	private StringBuilder sb_toWrite;

	private int _numTweets;
	private int _myMention;
	private int _mentionMe;
	private long _sendTweetTime;

	private final String SERVER_NAME = "server";


	/**
	 * constructor
	 * @param userName
	 * @param password
	 */
	public Client(String userName, String password, PrintWriter output){
		this._isLoggedIn = true;
		this._userName = userName;
		this._password = password;
		this.pw_outPut = output;
		this._mentionMe = 0;
		this._myMention = 0;
		this._numTweets = 0;
		this._sendTweetTime = 0;

		this.hm_myFollowers = new ConcurrentHashMap<>();
		this.hm_thisFollowing = new ConcurrentHashMap<>();
		this.sb_toWrite = new StringBuilder();
		startFollowing(this, "0");
	}

	/**
	 * constructor-edit fake client
	 * @throws  
	 */
	public Client() {
		this._isLoggedIn = false;
		this._userName = SERVER_NAME;
		this._password = "";
		this.hm_myFollowers = new ConcurrentHashMap<>();
		this.hm_thisFollowing = new ConcurrentHashMap<>();
		this.sb_toWrite = new StringBuilder();
		this.pw_outPut = null;
		this._mentionMe = 0;
		this._myMention = 0;
		this._numTweets = 0;
		this._sendTweetTime = 0;
	}


	/**
	 * @param password
	 * @return true if this is the user password. else-false.
	 */
	public synchronized boolean checkPassword(String password){
		return (this._password.equals(password));
	}

	/**
	 * @return true if the user is now log in.
	 */
	public synchronized boolean isLogIn(){
		return this._isLoggedIn;
	}

	/**
	 * @param toComper
	 * @return true if other has more followers
	 */
	public synchronized boolean isHasMoreFollowers(Client other){
		return (this.hm_myFollowers.size() < 
				other.hm_myFollowers.size());
	}

	/**
	 * @param userName
	 * @return true if this user already following the
	 * user that was given.
	 */
	public boolean isFollowing(String userName){
		for (String str : this.hm_thisFollowing.keySet()){
			if(this.hm_thisFollowing.get(str).checkUserName(userName)){
				return true;
			}
		}
		return false;
	}

	/**
	 * send tweet to all followers
	 * @param msg
	 * @param count
	 * @param userName-the name of the user how tweet
	 * this message.
	 */
	public void sendTweet(SendFrame msg, MessageCounter count,
			String userName){
		boolean myTweet = false;
		long startTime = 0;
		long endTime = 0;

		if(userName.equals(this._userName)){
			this._numTweets++;
			myTweet = true;
			startTime = System.currentTimeMillis();
		}
		else{
			this._mentionMe++;
		}
		synchronized (this.hm_myFollowers) {
			for (String id : this.hm_myFollowers.keySet()){
				MessageFrame tweet = msg.getTweet(userName, id, count);
				this.hm_myFollowers.get(id).sendMessage(tweet.toString());
			}
		}
		if(myTweet){
			endTime = System.currentTimeMillis();
			this._sendTweetTime =+ (endTime-startTime);
		}
	}

	/**
	 * write to the client the message he got
	 * when he wasn't connected.
	 * @param output: the new output writer
	 */
	public void connect(PrintWriter output){
		synchronized (this.sb_toWrite) {
			this.pw_outPut = output;
			this.pw_outPut.println(this.sb_toWrite.toString());
			this.sb_toWrite = new StringBuilder();
		}
		this._isLoggedIn = true;
	}

	/**
	 * user logout-edit the printWriter add 
	 * isLoggedIn
	 */
	public void disconnect(){
		this._isLoggedIn = false;
		if(this.pw_outPut!=null){
			synchronized (this.pw_outPut) {
				this.pw_outPut = null;
			}
		}
	}

	/**
	 * send to PrintWriter if logged or to
	 * stringBuilder if not.
	 * @param msg
	 */
	public void sendMessage(String msg){
		if(this.pw_outPut == null){
			synchronized (this.sb_toWrite) {
				this.sb_toWrite.append(msg);
			}
		}
		else{
			synchronized (this.pw_outPut) {
				this.pw_outPut.println(msg);	
			}
		}
	}

	public void addOtherMantion(){
		this._myMention++;
	}

	public int getNumOfTweets(){
		return this._numTweets;
	}

	public int getNumOfMentionInOther(){
		return this._mentionMe;
	}

	public int getNumOfMyMention(){
		return this._myMention;
	}

	public int getNumOfFollowers(){
		return this.hm_myFollowers.size();
	}


	public long getSendTweetTime(){
		return this._sendTweetTime;
	}

	/**
	 * @param name
	 * @return true if this as the same userName
	 * in the method input
	 */
	public synchronized boolean checkUserName(String name){
		return (this._userName.equals(name));
	}

	/**
	 * Create Follower and Following an add to
	 * the appropriate collection.
	 * @param userToFollow
	 * @param id
	 */
	public synchronized void startFollowing
	(Client userToFollow, String id)
	{
		userToFollow.hm_myFollowers.put(id, this);
		this.hm_thisFollowing.put(id, userToFollow);
	}

	/**
	 * remove the user from followers map
	 * @param id
	 */
	public void removeFollowing(String id){
		synchronized (this.hm_myFollowers) {
			this.hm_thisFollowing.remove(id);
		}
	}

	/**
	 * remove the users from each other maps
	 * @param id
	 * @return this name
	 */
	public synchronized String unfollow(String id){
		this.hm_myFollowers.get(id).removeFollowing(id);
		this.hm_myFollowers.remove(id);

		return this._userName;
	}

	/**
	 * @param id
	 * @return the user this following is exist, else-null
	 */
	public Client getFolloingUser(String id){
		return this.hm_thisFollowing.get(id);
	}

	/**
	 * @return true if this client is the fake client.
	 */
	public boolean isServer(){
		return this._userName.equals(SERVER_NAME);
	}

	/**
	 * used only on server.
	 * @param msg
	 */
	public synchronized void writeServer(String msg){
		this.pw_outPut.append(msg);
	}

	/**
	 * comperTo override - compare by userName.
	 */
	@Override
	public int compareTo(Client other) {
		return (this._userName.compareTo(other._userName));
	}

	/**
	 * @return copy of the user name
	 */
	public String getNameCopy(){
		return (new String(this._userName));
	}

	/**
	 * @return if the user is online, copy of the 
	 * user name. else-null.
	 */
	public String getNameIfOnline(){
		if(this._isLoggedIn) return (new String (this._userName));
		return null;
	}

}
