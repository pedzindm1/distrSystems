import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class SendReleaseToOtherServers implements Runnable {
	private static final int SERVER_TIMEOUT = 100;

	private int _myID;
	private ArrayList<ServerMetadata> _listOfOtherServers = null;
	private ArrayList<ServerMetadata> _listOfDownServers = null;
	private ServerCommand _action;

	/**
	 * Creates Thread to notify others of completion of CS
	 * 
	 * @param myID
	 * @param listOfOtherServers
	 * @param action
	 */
	public SendReleaseToOtherServers(int myID, LamportClock clock, ArrayList<ServerMetadata> listOfOtherServers, ArrayList<ServerMetadata> listOfDownServers) {
		_myID = myID;
		_listOfOtherServers = listOfOtherServers;
		_listOfDownServers = listOfDownServers;
		_action = new ServerCommand("release",new LamportClock(clock.getTimestamp()+1), ServerCommandType.releaseMessage, myID);
		
	}
	@Override
	public synchronized void run() {
		System.out.println("SendReleaseToOtherServers started.");

		for (int i = 0; i < _listOfOtherServers.size(); i++) {
			if (_listOfOtherServers.get(i).get_serverID() != (_myID)) {
				try {
					Socket newSocket = new Socket(_listOfOtherServers.get(i).getIpAddress(),
							_listOfOtherServers.get(i).getPortAddress());
					newSocket.setSoTimeout(SERVER_TIMEOUT);

					// Send Message to other Servers
					ObjectOutputStream oos = new ObjectOutputStream(newSocket.getOutputStream());
					oos.writeObject(_action);
					oos.flush();

					newSocket.close();

				} catch (SocketTimeoutException | ConnectException e) {
					//timeout or connection error
					//remove the server
					_listOfDownServers.add(_listOfOtherServers.get(i));
					_listOfOtherServers.remove(i);

					continue;
				} catch (IOException e) {
					System.err.println(e.getMessage());
					continue;
				}
			}
		}

		System.out.println("Releases sent");

	}

}
