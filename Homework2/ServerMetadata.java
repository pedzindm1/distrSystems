
/**
 *         This class serves as the model for the serverLists that are generated
 *         on system Init
 *
 */
public class ServerMetadata {


	private int _serverID;
	public int get_serverID() {
		return _serverID;
	}

	/**
	 * IP address of the Server
	 */
	private String _ipAddress;

	/**
	 * Port address of the Server
	 */
	private int _portAddress;

	/**
	 * Creates a empty Server, defaults are set: PortAddress = 0; IPAddress = ""
	 */
	public ServerMetadata() {
		_ipAddress = new String("");
		_portAddress = 0;
		_serverID = 0;
	}


	/**
	 * Creates a server with values
	 * 
	 * @param ipAddress
	 * @param portAddress
	 */
	public ServerMetadata(String ipAddress, int portAddress, int serverID) {
		_ipAddress = ipAddress;
		_portAddress = portAddress;
		_serverID = serverID;
	}

	/**
	 * Returns the Server IP Address
	 */
	public String getIpAddress() {
		return _ipAddress;
	}

	/**
	 * Returns the Server Port Address
	 */
	public int getPortAddress() {
		return _portAddress;
	}

	/**
	 * String Representation of the Server
	 */
	@Override
	public String toString() {

		return _ipAddress + " : " + _portAddress;

	}

}
