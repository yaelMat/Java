package Connection;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import Interface.StompTokenizer;
import StompFrame.*;


public class StompTokenizerImpl implements StompTokenizer {
	private boolean isAlive;

	private final char SEPARATOR = '\0';

	enum Command{
		CONNECT,
		SUBSCRIBE,
		UNSUBSCRIBE,
		DISCONNECT,
		SEND,
		ERROR,
	}
	
	public StompTokenizerImpl(){
		this.isAlive = true;
	}

	@Override
	public StompFrame getFrame(BufferedReader buffer) throws IOException, InterruptedException {
		
		if (!this.isAlive){
			throw new IOException("tokenizer isn't alive");
		}
		if (buffer == null){
			throw new InterruptedException("null buffer");
		}
		String frameInfo = getFrameInfo(buffer);
		return stringToFrame(frameInfo);
	}

	/**
	 * @param buffer: input buffer reader
	 * @return appropriate frame according to the next "token"
	 * @throws IOException
	 */
	public String getFrameInfo(BufferedReader buffer) throws IOException{
		StringBuilder framInfo = new StringBuilder();
		int currentInput = buffer.read();

		//keep reading until read ^@
		while(currentInput != this.SEPARATOR){

			framInfo.append((char)currentInput);
			
			//wait for the completing message.
			while(!buffer.ready());

			currentInput = buffer.read();
		}
		return (framInfo.toString());
	}

	
	
	public StompFrame stringToFrame(String frameInfo){
		Command command = null;
		HashMap<String, String> hm_headers = new HashMap<>();
		String body = "";

		//create link for each of the input line.
		LinkedList<String> frameLines = 
				addStringInfoToLink(frameInfo);

		//get command
		Command[] arr_commandValues = Command.values();
		for(int i=0; i<arr_commandValues.length; i++){
			int pos = frameLines.get(0).indexOf(arr_commandValues[i].toString());
			if(pos == 0){
				command = arr_commandValues[i];
				break;
			}
		}
		//get body & headers
		boolean bodyline=false;
		for(int i=1; i< frameLines.size();i++){
			String line = frameLines.get(i);
			if(bodyline){
				body += line+'\n';
			}
			else{
				if(line != ""){
					int pos = line.indexOf(":");
					
					hm_headers.put(line.substring(0,pos), 
							line.substring(pos+1));
				}
				else{
					bodyline = true;                                
				}
			}
		}
		//if the frame command incorrect, create error frame
		if(command == null){
			command=Command.ERROR;     
		}
		//Create appropriate frame
		return createFrame(command, hm_headers, body);
	}
	
	/**
	 * @param frameInfo: string that contain the frame information
	 * @return the information in linkedList, every link
	 * contain line of information.
	 */
	private LinkedList<String> addStringInfoToLink(String frameInfo){
		LinkedList<String> frameLines = new LinkedList<String>();
		String actualLine = "";
		char actualChar;
		for(int i=0; i < frameInfo.length(); i++){
			actualChar = frameInfo.charAt(i);
			if(actualChar == '\n'){
				frameLines.add(actualLine);
				actualLine = "";
			}
			else{
				actualLine += actualChar;
			}
		}
		return frameLines;
	}
	
	/**
	 * @param command: frame command
	 * @param hm_headers: header info
	 * @param body: frame body
	 * @return appropriate frame according to command
	 */
	private StompFrame createFrame(Command command, 
			HashMap<String, String> hm_headers, String body)
	{
		if(command == Command.CONNECT){
			return new ConnectFrame(hm_headers);
		}
		if(command == Command.DISCONNECT){
			return new DisconnectFrame(hm_headers);
		}
		if(command == Command.ERROR){
			return new ErrorFrame(hm_headers,body);
		}
		if(command == Command.SUBSCRIBE){
			return new SubscribeFrame(hm_headers);
		}
		if(command == Command.SEND){
			return new SendFrame(hm_headers,body);
		}
		if(command == Command.UNSUBSCRIBE){
			return new UnsubscribeFrame(hm_headers);
		}
		else return null;
	}

}
