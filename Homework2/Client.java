import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The client of the distributed system
 *
 */
public class Client {

	private static final int SERVER_TIMEOUT = 100;

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
		int i = 0;

		while (_listOfServers.size() < _numberOfServers) {
			i++;

			String serverInfo = sc.nextLine();
			if (!serverInfo.isEmpty()) {
				String[] partsOfServerAddress = serverInfo.split(":");
				if (partsOfServerAddress.length == 2) {
					ServerMetadata serverObj = new ServerMetadata(partsOfServerAddress[0],
							Integer.parseInt(partsOfServerAddress[1]), i);
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
				socket = new Socket();
				socket.connect(new InetSocketAddress(_listOfServers.get(serverNumber).getIpAddress(),_listOfServers.get(serverNumber).getPortAddress()),SERVER_TIMEOUT);
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

				// Creates the ServerCommand and sends it to the server
				ServerCommand action = new ServerCommand(new String(createActionString(tokens)));
				out.writeObject(action);
				out.close();
				socket.close();
				socket = new Socket(_listOfServers.get(serverNumber).getIpAddress(),
						_listOfServers.get(serverNumber).getPortAddress());
				socket.setSoTimeout(SERVER_TIMEOUT);
				// Reads the response from the Server and displays it
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line;
				while(!in.ready()) {
					//wait
				}
				if ((line = in.readLine().toString()) != null) {
					System.out.println(line);
				}
				
				
				// close socket connection
				socket.close();
				// Stop sending to servers
				System.out.print("Sent to Server"+ _listOfServers.get(serverNumber).getPortAddress());
				break;
			} catch (IOException e) {
				//timeout or no connection
				System.out.print("Error with Server:"+ _listOfServers.get(serverNumber).getPortAddress());
				//System.out.println(e.toString()+e.getMessage());
				//e.printStackTrace();
				//continue;
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
