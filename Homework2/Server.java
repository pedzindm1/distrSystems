import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	private static int _myID;
	private static int _numServer;
	private static int _numSeat;
	private static ArrayList<ServerMetadata> _listOfServers;
	private static SeatInventory _inventory;
	private static ArrayList<ServerCommand> _serverQueue;
	private static ServerSocket _serverSocket;
	private static LamportClock _clock;

	public static void main(String[] args) {
		// Initialize Server
		Scanner sc = new Scanner(System.in);
		initializeServer(sc);

		try {
			while (true) {

				// creates Object Stream and reads the Command from the Socket
				_serverSocket = new ServerSocket(_listOfServers.get(_myID - 1).getPortAddress());

				Socket socket = _serverSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ServerCommand otherAction = (ServerCommand) ois.readObject();

				switch (otherAction.getMessageType()) {

				// Received Message from Client
				case clientMessage:
					// Set ownership Of the message
					otherAction.setServerId(_myID);
					_clock.sendMessageAction();
					otherAction.getClock().setTimestamp(_clock.getTimestamp());
					otherAction.setMessageType( ServerCommandType.notifyMessage);

					addCommandToQueue(otherAction);
					// notify other Servers of received Message
					Thread t = new Thread(new SendCommandToOtherServers(_myID, otherAction,_listOfServers));
					t.start();
					break;

				// Received a TimeStamp Message from other Server
				case acknowledgementMessage:
					_clock.receiveMessageAction(otherAction.getClock());
					System.out.println(
							_clock.toString() 
							+ ":Acknowledgement received from Server " + otherAction.toString());
					break;

				// Received a ServerCommand Message from other Server
				case notifyMessage:
					System.out
							.println(_clock.toString() 
									+ ":Command received from Server " + otherAction.toString());
					_clock.receiveMessageAction(otherAction.getClock());
					addCommandToQueue(otherAction);
					// respond to Server with Timestamp
					sendAcknowledgementToServer(socket);
					break;

				// Received a Release Message from THE Server
				case releaseMessage:
					_clock.receiveMessageAction(otherAction.getClock());
					System.out
							.println(_clock.toString() 
									+ ":Release received from Server " + otherAction.toString());
					executeCriticalSection(_serverQueue.get(0));
					_serverQueue.remove(0);
					break;
				default:
					break;

				}


				// TODO count responses from the notify thread before execution
				if (_serverQueue.size() > 0 && _serverQueue.get(0).getServerId() == _myID) {

					System.out.println(
							_clock.toString() + ":Entering Critical Section for :" + otherAction.toString());
					String response = executeCriticalSection(otherAction);
					sendResponseToClient(socket, response);
					_serverQueue.remove(0);
					System.out.println(
							_clock.toString() + ":Leaving Critical Section for :" + otherAction.toString());

					Thread t = new Thread(new SendReleaseToOtherServers(_myID,_clock, _listOfServers));
					t.start();
				}
				_serverSocket.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Server aborted: " + e);

		}

	}

	/**
	 * Initializes the Server with data from the input
	 * 
	 * @param sc
	 */
	private static void initializeServer(Scanner sc) {
		// System.out.println("ID?");
		_myID = sc.nextInt();
		// System.out.println("Number of Servers?");
		_numServer = sc.nextInt();
		// System.out.println("Number of Seats");
		_numSeat = sc.nextInt();

		System.out.println(String.format("ID: %d, Servers: %d, Seats: %d", _myID, _numServer, _numSeat));

		_listOfServers = new ArrayList<ServerMetadata>();
		_inventory = new SeatInventory(_numSeat);
		_serverQueue = new ArrayList<ServerCommand>();
		_clock = new LamportClock();

		String serverInfo;
		for (int i = 0; i < _numServer
				&& (serverInfo = sc.next("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:)\\d{1,5}")) != ""; i++) {
			String[] partsOfServerAddress = serverInfo.split(":");
			if (partsOfServerAddress.length == 2) {
				ServerMetadata serverObj = new ServerMetadata(partsOfServerAddress[0],
						Integer.parseInt(partsOfServerAddress[1]));
				_listOfServers.add(serverObj);
				System.out.println(String.format("Server %d is %s", i + 1, serverObj.toString()));
			}
		}
		System.out.println("listening for tcp");

	}

	/**
	 * Adds command to ServerQueue
	 * 
	 * @param otherAction
	 */
	private static void addCommandToQueue(ServerCommand otherAction) {
		_serverQueue.add(otherAction);
		_serverQueue.sort(new ServerActionComparator());
	}

	/**
	 * Sends timestamp as acknowledgement back to the requesting Server
	 * 
	 * @param socket
	 * @throws IOException
	 */
	private static void sendAcknowledgementToServer(Socket socket) throws IOException {
		_clock.sendMessageAction();
		ServerCommand timeStampAction = new ServerCommand("ack",_clock, ServerCommandType.acknowledgementMessage, _myID);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(timeStampAction);
		oos.flush();
	}

	/**
	 * Executes criticalSection commands
	 * 
	 * @param otherAction
	 * @return response to Client
	 */
	private static String executeCriticalSection(ServerCommand otherAction) {
		String[] bufferArray = otherAction.getAction().split(" ");
		String response = "";
		if (bufferArray.length > 1) {
			String actionFromBuffer = bufferArray[0].toLowerCase();
			switch (actionFromBuffer) {
			case "reserve":
				response = _inventory.ReserveSeat(bufferArray[1]);
				break;
			case "bookseat":
				response = _inventory.ReserveThatSeat(bufferArray[1], Integer.parseInt(bufferArray[2]));
				break;
			case "search":
				response = _inventory.SearchPerson(bufferArray[1]);
				break;
			case "delete":
				response = _inventory.RemoveReservation(bufferArray[1]);
				break;
				
			default:
				response = "ERROR: No such command";
				break;
			}
		} else {
			response = "Bad Request";
		}
		return response;
	}

	/**
	 * 
	 * Sends the response to the socketConnection
	 * 
	 * @param currentSocket
	 * @param response
	 * @throws IOException
	 */
	private static void sendResponseToClient(Socket socket, String response) throws IOException {
		PrintWriter pout = new PrintWriter(socket.getOutputStream());
		pout.println(response);
		pout.flush();
	}

}
