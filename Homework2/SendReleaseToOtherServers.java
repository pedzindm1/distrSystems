import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
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
			if (_listOfOtherServers.get(i).get_serverID() != _myID) {
				try {
					Socket newSocket = new Socket();
					newSocket.connect(new InetSocketAddress(_listOfOtherServers.get(i).getIpAddress(), _listOfOtherServers.get(i).getPortAddress()), SERVER_TIMEOUT);

					// Send Message to other Servers
					ObjectOutputStream oos = new ObjectOutputStream(newSocket.getOutputStream());
					oos.writeObject(_action);
					oos.flush();
					newSocket.close();

				} catch (SocketTimeoutException | ConnectException e) {
					//timeout or connection error
					//remove the server
					//_listOfDownServers.add(_listOfOtherServers.get(i));
					//_listOfOtherServers.remove(i);
					System.out.print("Error with Server:"+ _listOfOtherServers.get(_myID-1).getPortAddress());
					//System.out.println(e.toString()+e.getMessage());
					//e.printStackTrace();
					//continue;
				} catch (IOException e) {
					System.out.print("Error with Server:"+ _listOfOtherServers.get(_myID-1).getPortAddress());
					//System.out.println(e.toString()+e.getMessage());
					//e.printStackTrace();
					//continue;
				}
			}
		}

		System.out.println("Releases sent");

	}

}
