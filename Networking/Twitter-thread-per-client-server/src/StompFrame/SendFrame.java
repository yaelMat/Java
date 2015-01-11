package StompFrame;

import java.util.HashMap;

import Users.Client;
import Users.ClientManagment;
import Connection.MessageCounter;

public class SendFrame extends StompFrame{
	private String _body;
	private String _destenation;

	private final String DEST_HEAD = "destination";
	private final String SERVER_NAME = "server";
	private final String CLIENT = "clients";
	private final String CLIENT_ONLINE = "clients online";
	private final String STATS = "stats";
	private final String STOP = "stop";

	/**
	 * constructor
	 * @param headers: frame information
	 * @param body: frame body
	 */
	public SendFrame(HashMap<String, String> headers, String body){
		super(headers);
		if(body == null){
			this._body = "";
		}
		else{
			this._body = body.replace("\n", "");
		}
		initFrame();
	}
	
	public SendFrame(String destenation, String body){
		super(null);
		this._destenation = destenation;
		this._body = body;
	}


	/**
	 * Initialize the frame fields according to the
	 * Information in the father's hash map.
	 */
	@Override
	public void initFrame() {
		this._destenation = getField(DEST_HEAD);	
	}

	/**
	 * @return true if the frame send to server fake frame
	 */
	public boolean isServer(){
		return this._destenation.equals(SERVER_NAME);
	}

	/**
	 * @param userName
	 * @param id
	 * @param count
	 * @return message with the tweet and the id+userName
	 */
	public MessageFrame getTweet(String userName, 
			String id, MessageCounter count){
		return (new MessageFrame(userName,id,this._body,count,true));
	}
	
	/**
	 * search for mention users in the tweet.
	 * @param clientManage
	 * @param tweeterName
	 * @param count
	 */
	public void findMantion(ClientManagment clientManage,
			Client tweeterName, MessageCounter count){
		boolean finish = false;
		int currChar = 0;
		while(!finish){
			int begin = this._body.indexOf('@',currChar);
			if(begin == -1) finish = true;
			else{
				currChar = begin+1;
				boolean found = false;
				while(currChar != this._body.length() && !found){
					char toCheck = this._body.charAt(currChar);
					if((toCheck == ' ') || (toCheck == ',') ||
							(toCheck == '.') || (toCheck == '\n')){
						found = true;
					}
					else currChar++;
				}
				String userName = this._body.substring(begin+1, currChar);
				sendMention(clientManage, tweeterName, userName, count);
			}
			if(currChar == this._body.length()) finish = true;
		}
	}
	
	/**
	 * find and send the tweet to the mention user's 
	 * followers
	 * @param clientManage
	 * @param tweeterName
	 * @param mantionName
	 * @param count
	 */
	public void sendMention(ClientManagment clientManage,
			Client tweeterName, String mentionName,
			MessageCounter count)
	{
		Client mentionUser = clientManage.findClient(mentionName);
		if(mentionUser != null){
			tweeterName.addOtherMantion();
			mentionUser.sendTweet(this, count, tweeterName.getNameCopy());
		}
	}
	
	public void serverSend(ClientManagment clientManage){
		if(CLIENT.equals(this._body)){
			clientManage.client();
		}
		else if(CLIENT_ONLINE.equals(this._body)){
			clientManage.clientOnline();
		}
		else if(STATS.equals(this._body)){
			clientManage.stats();
		}
		else if(STOP.equals(this._body)){
			clientManage.closeServer();
		}
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SendFrame [_destenation=");
		builder.append(_destenation);
		builder.append(", _body=");
		builder.append(_body);
		builder.append("]");
		return builder.toString();
	}

}
