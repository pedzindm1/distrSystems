import java.io.Serializable;

/**
 * Class representing a command that is sent from Client to Server and Server to
 * Server
 *
 */
public class ServerCommand implements Serializable {

	/**
	 * Serial number from the interface
	 */
	private static final long serialVersionUID = 3891008083136222563L;

	/**
	 * TimeStamp of the Server Command
	 */
	private LamportClock _clock = null;
	/**
	 * Action to be performed by the Server
	 */
	private String _action = null;
	/**
	 * ID of the Server that owes this Action
	 */
	private int _serverId = 0;
	
	private int _acknowledgements =1;
	
	private ServerCommandType _messageType;

	/**
	 * @return the _messageType
	 */
	public ServerCommandType getMessageType() {
		return _messageType;
	}

	/**
	 * @param _messageType the _messageType to set
	 */
	public void setMessageType(ServerCommandType _messageType) {
		this._messageType = _messageType;
	}

	/**
	 * Returns the TimeStamp
	 */
	public LamportClock getClock() {
		return _clock;
	}

	/**
	 * Returns the action that should be performed
	 */
	public String getAction() {
		return _action;
	}

	/**
	 * returns the server id that owes this serverCommand
	 */
	public int getServerId() {
		return _serverId;
	}

	/**
	 * Sets the ServerID of ServerCommand
	 * 
	 * @param ServerId
	 */
	public void setServerId(int id) {
		_serverId = id;
	}
	/**
	 * returns the acknowledgements for serverCommand
	 */
	public synchronized int getAcknowledgements() {
		return _acknowledgements;
	}
	/**
	 * sets the acknowledgements for serverCommand
	 * @param acknowledgements
	 */
	public synchronized void setAcknowledgements(int acknowledgements) {
		this._acknowledgements = acknowledgements;
	}

	/**
	 * Creates a empty ServerCommand with defaults of action="", serverID =0
	 */
	public ServerCommand() {

	}

	/**
	 * Creates a ServerCommand from the client input
	 * 
	 * this is used when a client is sending a message to the server
	 * 
	 * @param timestamp
	 * @param action
	 */
	public ServerCommand(String action ) {
		_action = action;
		_messageType = ServerCommandType.clientMessage;
		_clock = new LamportClock();
	}

	/**
	 * Creates a ServerCommand
	 * 
	 * this is used for server to server communications
	 * 
	 * @param serverId
	 * @param timestamp
	 * @param action
	 */
	public ServerCommand(String action, LamportClock timestamp, ServerCommandType messageType, int serverId) {
		_clock = timestamp;
		_action = action;
		_serverId = serverId;
		_messageType = messageType;
	}

	/*
	 * Equals so that we can utilize it in the Server Class
	 */
	@Override
	public boolean equals(Object o) {
		ServerCommand c = (ServerCommand) o;
		if (this._action == c._action && this._clock == c._clock) {
			return true;
		}
		return false;
	}

	/*
	 * Hash so that we can utilize via contains in the server class
	 */
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (this._action != null ? this._action.hashCode() : 0);
		hash = 53 * hash + (this._clock != null ? this._clock.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		String str = "Timestamp:" + (this._clock != null ? this._clock.toString() : "No TimeStamp");
		str += "-Action:" + (this._action != null ? this._action.toString() : "No Action");
		str += "-Server:" + (this._serverId != 0 ? this._serverId : "No Server ID");
		return str;
	}

}
