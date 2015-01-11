package StompFrame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Connection.MessageCounter;

public class MessageFrame extends StompFrame{

	private String _body;
	private String _destenation;
	private String _subscription;
	private String _messageID;
	private DateFormat dateFormat;
	private Calendar cal;

	private final String DEST_HEAD = "destination";
	private final String SUB_HEAD = "subscription";
	private final String ID_HEAD = "message-id";

	/**
	 * constructor
	 * @param headers: frame information
	 * @param body: frame body
	 */
	public MessageFrame(HashMap<String, String> headers, String body){
		super(headers);
		this._body = body;
		initFrame();
	}

	public MessageFrame(String destenation, String subscription, 
			String body, MessageCounter count, boolean isTweet){
		super(null);
		this._destenation = destenation;
		this._body = body;
		this._subscription = subscription;
		this._messageID = String.valueOf(count.getCount());
		if(isTweet){
			dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			cal = Calendar.getInstance();
		}
	}
	/**
	 * Initialize the frame fields according to the
	 * Information in the father's hash map.
	 */
	@Override
	public void initFrame() {
		this._destenation = getField(DEST_HEAD);
		this._messageID = getField(ID_HEAD);
		this._subscription = getField(SUB_HEAD);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MESSAGE\n");
		if (_destenation != null) {
			builder.append("destination:");
			builder.append(_destenation);
			builder.append("\n");
		}
		if (_subscription != null) {
			builder.append("subscription:");
			builder.append(_subscription);
			builder.append("\n");
		}
		if (_messageID != null) {
			builder.append("message-id:");
			builder.append(_messageID);
			builder.append("\n");
		}
		if(this.dateFormat!=null){
			builder.append("time:");
			builder.append(dateFormat.format(cal.getTime()));
			builder.append("\n");
		}
		if (_body != null) {
			builder.append("\n");
			builder.append(_body);
		}
		builder.append("\n\0\n");
		return builder.toString();
	}
}
