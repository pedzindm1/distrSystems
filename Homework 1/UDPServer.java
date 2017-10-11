import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class UDPServer implements Runnable {
    private int port;
    private OrderTable orderTable;
    private Inventory inventory;
    public UDPServer(int port, Inventory inventory, OrderTable orderTable) {
        this.port = port;
        this.inventory = inventory;
        this.orderTable = orderTable;
    }

    @Override
    public void run() {
        System.out.println("listening for udp");
        DatagramPacket datapacket, returnpacket;

        try {
            DatagramSocket datasocket = new DatagramSocket(this.port);
            byte[] buf = new byte[1024];
            while (true) {
                datapacket = new DatagramPacket(buf, buf.length);
                datasocket.receive(datapacket);

                String[] bufferArray = new String(buf, 0, datapacket.getLength()).split(" ");
                byte[] response;

                switch (bufferArray[0]) {
                    case "list":
                        response = this.inventory.list().getBytes(StandardCharsets.UTF_8);
                        break;
                    case "purchase":
                        response = this.orderTable.purchase(bufferArray[1], bufferArray[2], Integer.parseInt(bufferArray[3]), this.inventory).getBytes(StandardCharsets.UTF_8);
                        break;
                    case "search":
                        response = this.orderTable.search(bufferArray[1]).getBytes(StandardCharsets.UTF_8);
                        break;
                    case "cancel":
                        response = this.orderTable.cancel(Integer.parseInt(bufferArray[1]), this.inventory).getBytes(StandardCharsets.UTF_8);
                        break;
                    case "listorders": //temp feature for testing
                        response = this.orderTable.search(null).getBytes(StandardCharsets.UTF_8);
                        break;
                    default:
                        response = ("ERROR: No such command").getBytes(StandardCharsets.UTF_8);
                        break;
                }

                returnpacket = new DatagramPacket(
                        response,
                        response.length,
                        datapacket.getAddress(),
                        datapacket.getPort());

                datasocket.send(returnpacket);
            }
        } catch (SocketException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
