import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main (String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;
        String mode = "T";

        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <hostAddress>: the address of the server");
            System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(3) <udpPort>: the port number for UDP connection");
            System.exit(-1);
        }

        hostAddress = args[0];
        tcpPort = Integer.parseInt(args[1]);
        udpPort = Integer.parseInt(args[2]);

        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");

            if (tokens[0].equals("setmode")) {
                // set the mode of communication for sending commands to the server
                // and display the name of the protocol that will be used in future
                mode = tokens[1];
                if (mode.equalsIgnoreCase("U")) {
                    System.out.println("Mode: UDP");
                } else {
                    System.out.println("Mode: TCP");
                }
            // command interetation is handled by the server, this would be redundant
            //else if (tokens[0].equals("purchase")) {}
            //else if (tokens[0].equals("cancel")) {}
            //else if (tokens[0].equals("search")) {}
            //else if (tokens[0].equals("list")) {

            } else if (tokens[0].equals("exit")) {
                break;

            } else {
                System.out.println((mode.equalsIgnoreCase("U")) ? SendUDPCommand(cmd, hostAddress, udpPort) : SendTCPCommand(cmd, hostAddress, tcpPort));

                //this is handled by the server
                //System.out.println("ERROR: No such command");
            }
        }
    }

    private static String SendUDPCommand(String command, String hostAddress, int port) {
        String response = "";

        try {
            //open connection
            InetAddress ia = InetAddress.getByName(hostAddress);
            DatagramSocket datasocket = new DatagramSocket();

            //send command
            byte[] buffer = new byte[command.length()];
            buffer = command.getBytes();
            DatagramPacket sPacket = new DatagramPacket(buffer, buffer.length, ia, port);
            datasocket.send(sPacket);

            //receive response
            byte[] rbuffer = new byte[1024]; //todo test this length by listing a large input file
            DatagramPacket rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            datasocket.receive(rPacket);

            response = new String(rPacket.getData(), 0, rPacket.getLength());

        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (SocketException e) {
            System.err.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private static String SendTCPCommand(String command, String hostAddress, int port) {
        String response = "";

        try {
            //setup the socket/stream resources
            Socket socket = new Socket(hostAddress, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //send command
            out.println(command);

            //receive response
            String line;
            while ((line = in.readLine()) != null) {
                response += line + "\n";
            }

        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
        return response.trim();
    }
}
