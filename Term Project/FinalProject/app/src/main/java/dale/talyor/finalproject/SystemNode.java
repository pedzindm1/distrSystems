package dale.talyor.finalproject;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by dalepedzinski on 11/30/17.
 */

public class SystemNode implements Comparable,Serializable {

    InetAddress nodeAddress;

    String nodeName;

    String macAddress;

    ApplicationList applicationList = new ApplicationList(new ArrayList<ApplicationData>());



    public SystemNode(InetAddress nodeAddress) {
        this.nodeAddress = nodeAddress;

    }


    public SystemNode(String nodeName, String macAddress) {
        this.nodeName = nodeName;
        this.macAddress = macAddress;
    }

    public SystemNode() {

    }

    @Override
    public int compareTo(@NonNull Object obj) {

        SystemNode objToCompare = (SystemNode) obj;
        if(this.nodeName== objToCompare.nodeName){
            return this.macAddress.compareTo(objToCompare.macAddress);
        }else{
            return  this.nodeName.compareTo(objToCompare.nodeName);
        }
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof ApplicationData){
            String toCompareName =  ((SystemNode) obj).nodeName;
            String toCompareAddress =  ((SystemNode) obj).macAddress;
            return nodeName.equals(toCompareName) && macAddress.equals(toCompareAddress) ;
        }
        return false;
    }



    @Override
    public int hashCode(){
        return this.nodeName.hashCode()+this.macAddress.hashCode();
    }
}
