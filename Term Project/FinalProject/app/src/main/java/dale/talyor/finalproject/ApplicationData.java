package dale.talyor.finalproject;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by dalepedzinski on 11/25/17.
 */

public class ApplicationData implements  Comparable, Serializable{

    private String appName;

    private int appCount;

    public String getAppName() {
        return appName;
    }

    public int getAppCount() {
        return appCount;
    }

    public void setAppCount(int _appCount) {
        this.appCount = _appCount;
    }


    public ApplicationData(String _appName, int _appCount) {
        this.appName = _appName;
        this.appCount = _appCount;
    }

    @Override
    public String toString(){
        return this.appName+" : "+this.appCount;
    }

    @Override
    public int compareTo(@NonNull Object obj) {

        ApplicationData objToCompare = (ApplicationData) obj;
        if(this.appCount== objToCompare.appCount){
            return this.appName.compareTo(objToCompare.appName);
        }else if(this.appCount>objToCompare.appCount){
            return -1;
        }else{
            return  1;
        }
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof ApplicationData){
            String toCompare =  ((ApplicationData) obj).appName;
            return appName.equals(toCompare);
        }
        return false;
    }



    @Override
    public int hashCode(){
        return this.appName.hashCode();
    }
}
