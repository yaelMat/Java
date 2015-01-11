package StompFrame;

import java.util.HashMap;

public class ErrorFrame extends StompFrame{
	private String _message;
	private String _body;

	private final String HEADER = "message";


	/**
	 * constructor
	 * @param headers: frame information
	 */
	public ErrorFrame(HashMap<String, String> headers, String body){
		super(headers);
		this._body = body;
		initFrame();
	}
	
	/**
	 * constructor
	 * @param massegae: error message
	 */
	public ErrorFrame(String massegae, String body){
		super(null);
		this._message = massegae;
		this._body = body;
	}

	/**
	 * Initialize the frame fields according to the
	 * Information in the father's hash map.
	 */
	@Override
	public void initFrame() {
		this._message = getField(HEADER);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ERROR\n");
		if (_message != null) {
			builder.append("message:");
			builder.append(this._message);
			builder.append("\n");
		}
		if ((this._body != null) && (this._body != "")) {
			builder.append("\n");
			builder.append(this._body);
		}
		builder.append("\n\0\n");
		return builder.toString();
	}


}
