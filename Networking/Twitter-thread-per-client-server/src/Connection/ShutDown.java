package Connection;

import java.io.IOException;
import java.net.ServerSocket;

public class ShutDown {
	private boolean isShutDown;
	private ServerSocket socket;
	
	public ShutDown() {
		this.isShutDown = false;
		this.socket = null;
	}
	
	public void init(ServerSocket soc){
		this.socket = soc;
	}
	
	public synchronized boolean isShutDown(){
		return this.isShutDown;
	}
	
	public synchronized void shutDown() throws IOException{
		this.isShutDown = true;
		synchronized (socket) {
			this.socket.close();
		}
	}

}
