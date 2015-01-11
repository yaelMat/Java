package Connection;
import Interface.ServerProtocol;
import Interface.ServerProtocolFactory;
import Users.ClientManagment;


public class TwitterProtocolFactory implements ServerProtocolFactory {
	private MessageCounter _msgCount;
	private ClientManagment _clientManagment;
	
	public TwitterProtocolFactory(ClientManagment clientManage,
			MessageCounter count) {
		this._msgCount = count;
		this._clientManagment = clientManage;
	}

	@Override
	public ServerProtocol create() {
		return new TwitterProtocol(this._msgCount, 
				this._clientManagment);
	}

}
