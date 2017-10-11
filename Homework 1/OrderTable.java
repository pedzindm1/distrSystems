import java.util.*;

public class OrderTable {
    private int nextOrderID;
    public OrderTable() {
        this.nextOrderID = 1;
    }

    class OrderEntry {
        private int orderID;
        private String userName;
        private String productName;
        private int quantity;
        public OrderEntry(int orderID, String userName, String productName, int quantity) {
            this.orderID = orderID;
            this.userName = userName;
            this.productName = productName;
            this.quantity = quantity;
        }
    }

    private Map<Integer, OrderTable.OrderEntry> orderTable = new TreeMap<Integer, OrderTable.OrderEntry>();

    public synchronized String purchase(String userName, String productName, int quantity, Inventory inventory) {
        String retValue = "";
        Integer stock = inventory.get(productName);

        //check inventory
        if (stock == null) {
            retValue = "Not Available - We do not sell this product";

        } else if (stock < quantity) {
            retValue = "Not Available - Not enough items";

        } else {
            //decrease inventory and make new order
            inventory.remove(productName, quantity);
            orderTable.put(this.nextOrderID, new OrderEntry(this.nextOrderID, userName, productName, quantity));
            retValue = String.format("Your order has been placed, %d %s %s %d", this.nextOrderID, userName, productName, quantity);
            this.nextOrderID ++;
        }

        notifyAll();
        return retValue.trim();
    }

    public synchronized String cancel(Integer orderID, Inventory inventory) {
        String retValue = "";

        if (this.orderTable.containsKey(orderID)) {
            //remove the order and put back in inventory
            OrderEntry entry = this.orderTable.remove(orderID);
            inventory.add(entry.productName, entry.quantity);
            retValue = String.format("Order %d is canceled", orderID);
        } else {
            retValue = String.format("%d not found, no such order", orderID);
        }

        notifyAll();
        return retValue.trim();
    }

    public synchronized String search(String userName) {
        String retValue = "";

        //iterate through orders, build response
        //null userName results in list of all orders, for testing
        for (OrderEntry order: orderTable.values())
            if (userName == null || order.userName.equalsIgnoreCase(userName)) {
                //add to response
                retValue += String.format("%d, %s, %d\n", order.orderID, order.productName, order.quantity);
            }

        if (retValue.equals("")) retValue = String.format("No order found for %s", userName);

        notifyAll();
        return retValue.trim();
    }
}
