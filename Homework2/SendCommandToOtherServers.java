import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class SendCommandToOtherServers implements Runnable {
	private static final int SERVER_TIMEOUT = 100;

	private int _myID;
	private ArrayList<ServerMetadata> _listOfOtherServers = null;
	private ArrayList<ServerMetadata> _listOfDownServers = null;
	private ServerCommand _action;

	/**
	 * Creates Thread to send command to other Servers
	 * 
	 * @param myID
	 * @param listOfOtherServers
	 * @param action
	 */
	public SendCommandToOtherServers(int myID, ServerCommand action, ArrayList<ServerMetadata> listOfOtherServers, ArrayList<ServerMetadata> listOfDownServers) {
		_myID = myID;
		_listOfOtherServers = listOfOtherServers;
		_action = action;
		_listOfDownServers = listOfDownServers;
	}

	@Override
	public synchronized void run() {
		System.out.println("SendCommandToOtherServers started.");
		int acknowledgementsFromServer = 1;

		for (int i = 0; i < _listOfOtherServers.size(); i++) {
			if (_listOfOtherServers.get(i).get_serverID() != _myID) {
				try {
					Socket newSocket = new Socket();
					newSocket.connect(new InetSocketAddress(_listOfOtherServers.get(i).getIpAddress(), _listOfOtherServers.get(i).getPortAddress()), SERVER_TIMEOUT);
					
					// Send Message to other Servers
					ObjectOutputStream oos = new ObjectOutputStream(newSocket.getOutputStream());
					oos.writeObject(_action);
					oos.flush();

					// receive acknowledgement from other servers
					ObjectInputStream ois = new ObjectInputStream(newSocket.getInputStream());
					ServerCommand acknowledgement = (ServerCommand) ois.readObject();
					if (acknowledgement.getMessageType() == ServerCommandType.acknowledgementMessage) {
						acknowledgementsFromServer++;
					}
					newSocket.close();

				} catch (SocketTimeoutException | ConnectException e) {
					//timeout or connection error
					//remove the server from the list, caller should see this change
					//_listOfDownServers.add(_listOfOtherServers.get(i));
					//_listOfOtherServers.remove(i);
					System.out.print("Error with Server:"+ _listOfOtherServers.get(_myID-1).getPortAddress());
					//System.out.println(e.toString()+e.getMessage());
					//e.printStackTrace();
					acknowledgementsFromServer++;
					//continue;
				} catch (IOException | ClassNotFoundException e) {
					System.out.print("Error with Server:"+ _listOfOtherServers.get(_myID-1).getPortAddress());
					//System.out.println(e.toString()+e.getMessage());
					//e.printStackTrace();
					//continue;
				}
			}
		}
		_action.setAcknowledgements(acknowledgementsFromServer);
		// TODO: Count acknowledgements
		// while(acknowledgementsFromServer!=_listOfOtherServers.size()) {
		//
		// }
		System.out.println("Acknowledgements received " + acknowledgementsFromServer);

	}
}
