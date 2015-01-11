package StompFrame;
import java.util.HashMap;


public abstract class StompFrame {
	protected HashMap<String, String> hm_headers;
	
	/**
	 * 
	 */
	public StompFrame(HashMap<String, String> headers){
		this.hm_headers = headers;
	}
	
	protected HashMap<String, String> getHeaders(){
		return this.hm_headers;
	}
	
	/**
	 * return the header value according to key.
	 * @param key 
	 * @return
	 */
	public String getField(String key){
		if((this.hm_headers != null) && 
				(this.hm_headers.containsKey(key))){
			return this.hm_headers.get(key);
		}
		else{
		//Exception
		return "";
		}
	}
	
	public abstract void initFrame();
	public abstract String toString();
	
	
}
