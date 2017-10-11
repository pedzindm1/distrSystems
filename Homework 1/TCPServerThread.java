import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServerThread implements Runnable {
    private OrderTable orderTable;
    private Inventory inventory;
    private Socket clientSocket;
    public TCPServerThread(OrderTable orderTable, Inventory inventory, Socket socket) {
        this.inventory = inventory;
        this.orderTable = orderTable;
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        //System.out.println("TCPServerThread started.");

        try (Scanner sc = new Scanner(this.clientSocket.getInputStream());) {
            PrintWriter pout = new PrintWriter(this.clientSocket.getOutputStream());

            if (sc.hasNext()) {
                String[] bufferArray = sc.nextLine().split(" ");
                String response;

                switch (bufferArray[0]) {
                    case "list":
                        response = this.inventory.list();
                        break;
                    case "purchase":
                        response = this.orderTable.purchase(bufferArray[1], bufferArray[2], Integer.parseInt(bufferArray[3]), this.inventory);
                        break;
                    case "search":
                        response = this.orderTable.search(bufferArray[1]);
                        break;
                    case "cancel":
                        response = this.orderTable.cancel(Integer.parseInt(bufferArray[1]), this.inventory);
                        break;
                    case "listorders": //temp feature for testing
                        response = this.orderTable.search(null);
                        break;
                    default:
                        response = "ERROR: No such command";
                        break;
                }

                pout.println(response);
                pout.flush();
            }

        } catch (IOException e) {
            System.err.println(e);
        }
        //System.out.println("TCPServerThread stopped.");
    }
}
