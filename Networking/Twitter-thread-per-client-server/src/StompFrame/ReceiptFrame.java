package StompFrame;

import java.util.HashMap;

public class ReceiptFrame extends StompFrame{
	private String _receipt;

	private final String HEADER = "receipt-id";

	/**
	 * constructor
	 * @param headers: frame information
	 */
	public ReceiptFrame(HashMap<String, String> headers){
		super(headers);
	}
	
	/**
	 * constructor
	 * @param receipt
	 */
	public ReceiptFrame(String receipt){
		super(null);
		this._receipt = receipt;
	}

	/**
	 * Initialize the frame fields according to the
	 * Information in the father's hash map.
	 */
	@Override
	public void initFrame() {
		this._receipt = getField(HEADER);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RECEIPT\nreceipt:");
		builder.append(_receipt);
		return builder.toString();
	}


}
