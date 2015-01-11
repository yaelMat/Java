package Connection;

import java.util.ArrayList;

import Interface.ServerConcurrencyModel;

public class ThreadPearClient implements ServerConcurrencyModel {

	private ArrayList<ConnectionHandler> arr_loggedUsers;
	
	public ThreadPearClient() {
		this.arr_loggedUsers = new ArrayList<>();
	}

	/**
	 * create new connection thread
	 */
	@Override
	public void apply(Runnable connection) {
		this.arr_loggedUsers.add((ConnectionHandler)connection);
		new Thread(connection).start();
		delete();
	}
	
	/**
	 * delete finished connection
	 */
	public void delete(){
		for(int i=0;i<this.arr_loggedUsers.size();i++){
			if(!(this.arr_loggedUsers.get(i).isRunning())){
				this.arr_loggedUsers.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * shutdown all connection.
	 */
	public void shutdown(){
		for(int i=0;i<this.arr_loggedUsers.size();i++){
			this.arr_loggedUsers.remove(i).shutDown();;
		}
		System.out.println("all ST "+this.arr_loggedUsers.size());
		closeAndDeleteAll();
	}
	
	/**
	 * delete all finished threads. wait for all
	 * threads to finish.
	 */
	public void closeAndDeleteAll(){
		System.out.println("start deleting");
		while(!(this.arr_loggedUsers.isEmpty())){
			for(int i=0;i<this.arr_loggedUsers.size();i++){
				if(!(this.arr_loggedUsers.get(i).isRunning())){
					this.arr_loggedUsers.remove(i);
					i--;
					System.out.println(i+" ST");
				}
			}
		}
		System.out.println("all shut down-and delete");
	}

}
