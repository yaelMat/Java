package Users;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import StompFrame.SendFrame;
import Connection.MessageCounter;
import Connection.ShutDown;

public class ClientManagment {
	private ConcurrentHashMap<String, Client> hm_clients;
	private Client server;
	private MessageCounter _count;
	private ShutDown shutDownServer;
	private Logger logger = Logger.getLogger("serverLogger");
	
	private final String SERVER_NAME = "server";

	

	/**
	 * default constructor
	 */
	public ClientManagment(MessageCounter count, ShutDown shutDown){
		this.hm_clients = new ConcurrentHashMap<>();
		this.server = new Client();
		this.hm_clients.put(SERVER_NAME, this.server);
		this._count = count;
		this.shutDownServer = shutDown;
	}
	
	/**
	 * @param userName
	 * @return if the user exists, return it. if 
	 * not, return null.
	 */
	public Client findClient(String userName){
		return this.hm_clients.get(userName);
	}

	/**
	 * Create and add to the map a new client
	 * @param userName
	 * @param password
	 * @return the new client
	 */
	public Client register(String userName, 
			String password, PrintWriter output){
		Client newUser = new Client(userName, password,output);
		synchronized (this.hm_clients) {
			this.hm_clients.put(userName, newUser);
		}
		return newUser;
	}

	/**
	 * @return string with all the the users.
	 */
	private String getUsers(){
		StringBuilder users = new StringBuilder("users:\n");
		synchronized(this.hm_clients){
			for (String str : this.hm_clients.keySet()) {
				users.append(this.hm_clients.get(str).getNameCopy());
				users.append("\n");
			}
		}
		return users.toString();
	}

	/**
	 * send to server followers and stdout
	 * a list of all users
	 */
	public void client(){
		String clients = getUsers();
		this.logger.info(clients);
		SendFrame msg = new SendFrame(SERVER_NAME, clients);
		this.server.sendTweet(msg, this._count, SERVER_NAME);
	}

	/**
	 * send to server followers and stdout
	 * a list of all online users
	 */
	public void clientOnline(){
		String clientsOnline = getOnlineUsers();
		this.logger.info(clientsOnline);
		SendFrame msg = new SendFrame(SERVER_NAME, clientsOnline);
		this.server.sendTweet(msg, this._count, SERVER_NAME);
	}

	/**
	 * send to server followers the statistics.
	 */
	public void stats(){
		String stats = getStats();
		SendFrame msg = new SendFrame(SERVER_NAME, stats);
		this.server.sendTweet(msg, this._count, SERVER_NAME);
	}
	
	/**
	 * shutdown everything.
	 */
	public void closeServer(){
		try {
			this.shutDownServer.shutDown();
		} catch (IOException e) {
			System.out.println("can't shutdown");
			e.printStackTrace();
		}
	}

	private String getStats(){
		Client maxTweet = null, maxMentionOther = null;
		Client maxGotMention = null, maxFolloing = null;
		long timeToSendTweet = 0;
		int allTweets = 0, numTweet = 0, numMentionOther = 0;
		int numGotMention = 0, numFolloing = 0, userTweet = 0;
		int userMentionOther = 0, userGotMention = 0, userFolloing = 0;
		synchronized (this.hm_clients) {
			for (String str : this.hm_clients.keySet()) {
				Client user = this.hm_clients.get(str);
				userFolloing = user.getNumOfFollowers();
				userGotMention = user.getNumOfMentionInOther();
				userMentionOther = user.getNumOfMyMention();
				userTweet = user.getNumOfTweets();

				timeToSendTweet =+ user.getSendTweetTime();
				allTweets =+ userTweet;

				if(userFolloing >= numFolloing){
					maxFolloing = user;
					numFolloing = userFolloing;
				}
				if(userMentionOther >= numMentionOther){
					maxMentionOther = user;
					numMentionOther = userMentionOther;
				}
				if(userGotMention >= numGotMention){
					maxGotMention = user;
					numGotMention = userGotMention;
				}
				if(userTweet >= numTweet){
					maxTweet = user;
					numTweet = userTweet;
				}
			}
		}
		double avrgTweetTime;
		if(allTweets == 0) avrgTweetTime = 0; 
		else avrgTweetTime = (timeToSendTweet/allTweets);
		return stringStats(maxTweet, maxMentionOther, maxGotMention,
				maxFolloing, avrgTweetTime);
	}

	/**
	 * @param maxTweet
	 * @param maxMentionOther
	 * @param maxGotMention
	 * @param maxFolloing
	 * @param avrgTweetTime
	 * @return String that contain the statistics.
	 */
	private String stringStats(Client maxTweet,
			Client maxMentionOther, Client maxGotMention,
			Client maxFolloing, double avrgTweetTime)
	{
		StringBuilder stats = new StringBuilder("Statistics:\n");
		stats.append("The average time of sending tweet is ");
		stats.append(avrgTweetTime);
		stats.append(".\n");
		if(maxFolloing != null){
			stats.append("User with the maximum number of followers:\n");
			stats.append(maxFolloing.getNameCopy());
			stats.append(", with ");
			stats.append(maxFolloing.getNumOfFollowers());
			stats.append(" followers.\n");
		}
		if(maxTweet != null){
			stats.append("User with the maximum number of tweets:\n");
			stats.append(maxTweet.getNameCopy());
			stats.append(", with ");
			stats.append(maxTweet.getNumOfTweets());
			stats.append(" tweets.\n");
		}
		if(maxGotMention != null){
			stats.append("User with the maximum mentions in other followers tweets:\n");
			stats.append(maxGotMention.getNameCopy());
			stats.append(", with ");
			stats.append(maxGotMention.getNumOfMentionInOther());
			stats.append(" mentions.\n");
		}
		if(maxMentionOther != null){
			stats.append("User with the maximum number of mentions in her own tweets:\n");
			stats.append(maxMentionOther.getNameCopy());
			stats.append(", with ");
			stats.append(maxMentionOther.getNumOfMyMention());
			stats.append(" mentions.\n");
		}
		return stats.toString();
	}


	/**
	 * @return string with all the the online users.
	 */
	public synchronized String getOnlineUsers(){
		StringBuilder users = new StringBuilder("online users:\n");

		for (String str : this.hm_clients.keySet()) {
			String name = this.hm_clients.get(str).getNameIfOnline();
			if(name != null){
				users.append(name);
				users.append("\n");
			}
		}
		return users.toString();
	}

}
