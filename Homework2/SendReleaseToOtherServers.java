import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class SendReleaseToOtherServers implements Runnable {
	private int _myID;
	private ArrayList<ServerMetadata> _listOfOtherServers = null;
	private ServerCommand _action;

	/**
	 * Creates Thread to notify others of completion of CS
	 * 
	 * @param myID
	 * @param listOfOtherServers
	 * @param action
	 */
	public SendReleaseToOtherServers(int myID, LamportClock clock, ArrayList<ServerMetadata> listOfOtherServers) {
		_myID = myID;
		_listOfOtherServers = listOfOtherServers;
		_action = new ServerCommand("release",new LamportClock(clock.getTimestamp()+1), ServerCommandType.releaseMessage, myID);
		
	}
	@Override
	public void run() {
		System.out.println("SendReleaseToOtherServers started.");

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

					newSocket.close();

				} catch (SocketTimeoutException e) {
					// TODO: Uncomment this so the connection will move on to the next server if the
					// connection dies
					// _listOfServers.remove(serverNumber);
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
