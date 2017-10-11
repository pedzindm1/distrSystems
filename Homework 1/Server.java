import java.io.*;

public class Server {
    private Inventory inventory;
    private OrderTable orderTable;
    public Server() {
        this.inventory = new Inventory();
        this.orderTable = new OrderTable();
    }

    public static void main (String[] args) {
        Server server = new Server();
        System.out.println("server started (ctrl+c to exit)");

        int tcpPort;
        int udpPort;
        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(2) <udpPort>: the port number for UDP connection");
            System.out.println("\t(3) <file>: the file of inventory");

            System.exit(-1);
        }
        tcpPort = Integer.parseInt(args[0]);
        udpPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        // parse the inventory file
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String[] lineItems;

            while ((line = reader.readLine()) != null) {
                lineItems = line.split(" ");
                server.inventory.add(lineItems[0], Integer.parseInt(lineItems[1]));
            }
        } catch (IOException e) {
          e.printStackTrace();
        }

        //start UDP server
        Runnable udpServer = new UDPServer(udpPort, server.inventory, server.orderTable);
        new Thread(udpServer).start();

        //start TCP server
        Runnable tcpServer = new TCPServer(tcpPort, server.inventory, server.orderTable);
        new Thread(tcpServer).start();
    }
}
