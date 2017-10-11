import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The client of the distributed system
 *
 */
public class Client {

	private static ArrayList<ServerMetadata> _listOfServers = new ArrayList<ServerMetadata>();
	private static int _numberOfServers = 0;

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		initializeClient(sc);

		while (sc.hasNextLine()) {
			String cmd = sc.nextLine();
			String[] tokens = cmd.split(" ");

			if (tokens[0].equals("reserve") && tokens.length == 2) {
				runCommand(tokens);
			} else if (tokens[0].equals("bookSeat") && tokens.length == 3) {
				runCommand(tokens);
			} else if (tokens[0].equals("search") && tokens.length == 2) {
				runCommand(tokens);
			} else if (tokens[0].equals("delete") && tokens.length == 2) {
				runCommand(tokens);
			} else {
				System.out.println("ERROR: No such command");
			}
		}
	}

	/**
	 * Initializes the Client with the metaData of the servers
	 * 
	 * @param Scanner
	 *            of the Client
	 */
	private static void initializeClient(Scanner sc) {
		_numberOfServers = sc.nextInt();

		while (_listOfServers.size() < _numberOfServers) {
			String serverInfo = sc.nextLine();
			if (!serverInfo.isEmpty()) {
				String[] partsOfServerAddress = serverInfo.split(":");
				if (partsOfServerAddress.length == 2) {
					ServerMetadata serverObj = new ServerMetadata(partsOfServerAddress[0],
							Integer.parseInt(partsOfServerAddress[1]));
					_listOfServers.add(serverObj);
					System.out.println("Server " + _listOfServers.size() + " is " + serverObj.toString());
				} else {
					System.out.println("Try again");
				}
			}
		}
	}

	/**
	 * Sends the command to the servers
	 * 
	 * @param tokens
	 */
	private static void runCommand(String[] tokens) {
		for (int serverNumber = 0; serverNumber < _listOfServers.size(); serverNumber++) {
			Socket socket;
			try {
				socket = new Socket(_listOfServers.get(serverNumber).getIpAddress(),
						_listOfServers.get(serverNumber).getPortAddress());
				// TODO: Uncomment this so the connection will move on to the next server if the
				// connection dies
				// socket.setSoTimeout(100);
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

				// Creates the ServerCommand and sends it to the server
				ServerCommand action = new ServerCommand(new String(createActionString(tokens)));
				out.writeObject(action);
				out.flush();

				// Reads the response from the Server and displays it
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line;
				if ((line = in.readLine().toString()) != null) {
					System.out.println(line);
				}
				// close socket connection
				out.close();
				// Stop sending to servers
				break;
			} catch (SocketTimeoutException e) {
				// TODO: Uncomment this so the connection will move on to the next server if the
				// connection dies
				// _listOfServers.remove(serverNumber);
				continue;
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}

	/**
	 * creates the ActionString from client inputs
	 * 
	 * @param tokens
	 * @return the token string a format for the server to understand
	 */
	private static String createActionString(String[] tokens) {
		String str = "";
		for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++) {
			str += tokens[tokenIndex] + " ";
		}
		return str.trim();
	}

}
