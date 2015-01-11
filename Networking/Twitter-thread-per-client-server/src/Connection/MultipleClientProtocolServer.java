package Connection;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import Interface.ServerProtocolFactory;
import Users.ClientManagment;


public class MultipleClientProtocolServer implements Runnable {

	private ServerSocket serverSocket;
	private int listenPort;
	private ServerProtocolFactory factory;
	private ClientManagment clientManage;
	private MessageCounter _msgCount;
	private ArrayList<ConnectionHandler> arr_loggedUsers;
	private ShutDown shutDown;


	public MultipleClientProtocolServer(int port)
	{
		this.serverSocket = null;
		this.listenPort = port;
		this.shutDown = new ShutDown();
		this.arr_loggedUsers = new ArrayList<>();
		this._msgCount = new MessageCounter();
		this.clientManage = new ClientManagment
				(this._msgCount, this.shutDown);
		this.factory = new TwitterProtocolFactory
				(this.clientManage, this._msgCount);

	}
	
	/**
	 * create new thread for each connection.
	 * delete finished threads.
	 */
	public void run()
	{
		try {
			serverSocket = new ServerSocket(listenPort);
			this.shutDown.init(serverSocket);
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port " + listenPort);
		}

		while (!(this.shutDown.isShutDown()))
		{
			//create new connection.
			try {
				ConnectionHandler newConnection = new ConnectionHandler
						(this.serverSocket.accept(),
								((TwitterProtocol)this.factory.create()));
				this.arr_loggedUsers.add(newConnection);
				new Thread(newConnection).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port " + listenPort);
			}
			//remove finished threads
			for(int i=0;i<this.arr_loggedUsers.size();i++){
				if(!(this.arr_loggedUsers.get(i).isRunning())){
					this.arr_loggedUsers.remove(i);
					i--;
				}
			}
		}

		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Closes the connection.
	 * wait for all thread to finish.
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		shutdown();
		serverSocket.close();
		while(!(this.arr_loggedUsers.isEmpty())){
			for(int i=0;i<this.arr_loggedUsers.size();i++){
				if(!(this.arr_loggedUsers.get(i).isRunning())){
					this.arr_loggedUsers.remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * shutdown all the threads
	 */
	private void shutdown(){
		for(int i=0;i<this.arr_loggedUsers.size();i++){
			this.arr_loggedUsers.get(i).shutDown();;
		}
	}

}