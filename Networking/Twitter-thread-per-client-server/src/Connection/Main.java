package Connection;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MultipleClientProtocolServer server = new MultipleClientProtocolServer(4444);
		server.run();
	}

}
