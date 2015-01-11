package StompFrame;

import java.util.HashMap;

public class ConnectedFrame extends StompFrame{
	
	
	/**
	 * constructor
	 * @param headers: frame information
	 */
	public ConnectedFrame(HashMap<String, String> headers){
		super(headers);
	}
	
	/**
	 * Default constructor
	 */
	public ConnectedFrame(){
		super(null);
	}

	
	@Override
	public void initFrame() {
		
	}
	
	/**
	 * toString override, frame form.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CONNECTED\n");
		builder.append("version:1.2\n");
		builder.append("\n\0\n");
		return builder.toString();
	}
}
