package dale.talyor.finalproject;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Created by dalepedzinski on 11/30/17.
 */

public class SystemData implements Serializable {

    InetAddress groupOwnerAddress = null;
    int groupOwnerPort = 8988;


    ApplicationList appDataList;

    SystemNodeList nodeList = new SystemNodeList();


    public SystemData(ApplicationList appDataList) {
        this.appDataList = appDataList;
    }

}
