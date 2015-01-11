package Interface;

import StompFrame.StompFrame;

/**
 * 
 * @author MATHOV
 *
 */
public interface ServerProtocol {
	
	StompFrame processMessage(StompFrame msg);
    boolean isEnd(String msg);
}
