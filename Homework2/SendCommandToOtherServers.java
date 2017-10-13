import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class SendCommandToOtherServers implements Runnable {
	private int _myID;
	private ArrayList<ServerMetadata> _listOfOtherServers = null;
	private ServerCommand _action;

	/**
	 * Creates Thread to send command to other Servers
	 * 
	 * @param myID
	 * @param listOfOtherServers
	 * @param action
	 */
	public SendCommandToOtherServers(int myID, ServerCommand action, ArrayList<ServerMetadata> listOfOtherServers) {
		_myID = myID;
		_listOfOtherServers = listOfOtherServers;
		_action = action;

	}

	@Override
	public synchronized void run() {
		System.out.println("SendCommandToOtherServers started.");
		int acknowledgementsFromServer = 1;

		for (int i = 0; i < _listOfOtherServers.size(); i++) {
			if (i != (_myID - 1)) {
				try {
					Socket newSocket = new Socket(_listOfOtherServers.get(i).getIpAddress(),
							_listOfOtherServers.get(i).getPortAddress());
					// TODO: Uncomment before turn in
					// newSocket.setSoTimeout(100);

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

				} catch (SocketTimeoutException e) {
					// TODO: Uncomment this so the connection will move on to the next server if the
					// connection dies
					// _listOfServers.remove(serverNumber);
					continue;
				} catch (IOException | ClassNotFoundException e) {
					System.err.println(e.getMessage());
					continue;
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
