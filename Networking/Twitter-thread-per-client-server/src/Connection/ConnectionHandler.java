package Connection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import StompFrame.ConnectFrame;
import StompFrame.ConnectedFrame;
import StompFrame.ReceiptFrame;
import StompFrame.StompFrame;


public class ConnectionHandler implements Runnable {

	private BufferedReader br_input;
	private PrintWriter pr_outPut;
	private Socket _clientSocket;
	private TwitterProtocol _protocol;
	private boolean isShutDown;
	private StompTokenizerImpl _tokenizer;
	private boolean isRunning;


	/**
	 * constructor 
	 * 
	 * @param acceptedSocket
	 * @param protocol: copy protocol
	 */
	public ConnectionHandler(Socket acceptedSocket, 
			TwitterProtocol protocol)
	{
		br_input = null;
		pr_outPut = null;
		this._clientSocket = acceptedSocket;
		this._protocol = protocol;
		this.isShutDown = false;
		this._tokenizer = new StompTokenizerImpl();
		this.isRunning = true; 
	}

	@Override
	public void run()
	{

		try {
			initialize();
		}
		catch (IOException e) {
			System.out.println("Error in initializing I/O");
		}

		try {
			process();
		} 
		catch (IOException e) {
			System.out.println("Error in I/O");
		} 
		close();
	}


	/**
	 * initialize input and output
	 * @throws IOException
	 */
	private void initialize() throws IOException
	{
		// Initialize I/O
		InputStream inputS = this._clientSocket.getInputStream();
		OutputStream outputS = this._clientSocket.getOutputStream();


		this.br_input = new BufferedReader(new InputStreamReader
				(inputS ,"UTF-8"));

		this.pr_outPut = new PrintWriter(new OutputStreamWriter
				(outputS,"UTF-8"), true);

	}


	private void process() throws IOException
	{
		while (!this.isShutDown){
			StompFrame currentFrame = null;
			if(this.br_input.ready()){
				//read the next frame
				try {
					currentFrame = this._tokenizer
							.getFrame(this.br_input);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//process frame
				StompFrame ansFrame;
				if(currentFrame instanceof ConnectFrame){
					ansFrame = this._protocol.connectionProcess
							((ConnectFrame)currentFrame, this.pr_outPut);
				}
				else{
					ansFrame = this._protocol.
							processMessage(currentFrame);
				}
				//send anser to client
				if(ansFrame != null){
					this.pr_outPut.println(ansFrame.toString());
					if(ansFrame instanceof ConnectedFrame){
						this._protocol.connect(this.pr_outPut);
					}
					else if(ansFrame instanceof ReceiptFrame){
						shutDown();
					}
				}
			}

		}
	}


	/*
	 * 
	 */
	public synchronized void shutDown(){
		this.isShutDown = true;
	}
	
	public boolean isRunning(){
		return this.isRunning;
	}

	// Closes the connection
	private void close()
	{
		try {
			if (this.br_input != null)
			{
				this.br_input.close();
			}
			if (this.pr_outPut != null)
			{
				this.pr_outPut.close();
			}

			this._clientSocket.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception in closing I/O");
		}
		this.isRunning = false;
	}

}
