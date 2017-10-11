import java.io.*;
import java.net.*;

public class TCPServer implements Runnable {
    private int port;
    private OrderTable orderTable;
    private Inventory inventory;

    public TCPServer(int port, Inventory inventory, OrderTable orderTable) {
        this.port = port;
        this.inventory = inventory;
        this.orderTable = orderTable;
    }

    @Override
    public void run() {
        System.out.println("listening for tcp");

        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            while (true) {
                Runnable t = new TCPServerThread(this.orderTable, this.inventory, serverSocket.accept());
                new Thread(t).start();
            }
        } catch (IOException e) {
            System.err.println("Server aborted:" + e);
        }
    }
}
