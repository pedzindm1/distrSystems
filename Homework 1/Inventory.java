import java.util.*;

public class Inventory {
    private Map<String, Integer> currentInventory = new TreeMap<String, Integer>();

    public synchronized Integer get(String productName) { return currentInventory.get(productName); }

    public synchronized String list() {
        String retValue = "";

        for(Map.Entry<String,Integer> entry : currentInventory.entrySet()) {
            retValue += String.format("%s %d\n", entry.getKey(), entry.getValue());
        }

        return retValue.trim();
    }

    public synchronized boolean remove(String productName, int quantity) {
        boolean retValue = false;

        Integer q = currentInventory.get(productName);

        if (q == null || q < quantity) {
            retValue = false;
        } else {
            //else update inventory and return true
            currentInventory.put(productName, (q - quantity));
            retValue = true;
        }

        notifyAll();
        return retValue;
    }

    public synchronized void add(String productName, int quantity) {
        Integer q = currentInventory.get(productName);
        this.currentInventory.put(productName, (q == null) ? quantity : q + quantity);
        notifyAll();
    }
}
