package StompFrame;

import java.util.HashMap;

public class DisconnectFrame  extends StompFrame{
	private String _recepit;

	private final String HEADER = "receipt";

	/**
	 * constructor
	 * @param headers: frame information
	 */
	public DisconnectFrame(HashMap<String, String> headers){
		super(headers);
		initFrame();
	}

	/**
	 * Initialize the frame fields according to the
	 * Information in the father's hash map.
	 */
	@Override
	public void initFrame() {
		this._recepit = getField(HEADER);
	}
	
	public ReceiptFrame getDisconnectFrame(){
		return (new ReceiptFrame(this._recepit));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DisconnectFrame [_recepit=");
		builder.append(_recepit);
		builder.append("]");
		return builder.toString();
	}


}
