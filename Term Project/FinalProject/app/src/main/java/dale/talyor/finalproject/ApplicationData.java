package dale.talyor.finalproject;

import android.support.annotation.NonNull;

/**
 * Created by dalepedzinski on 11/25/17.
 */

public class ApplicationData implements  Comparable{

    private String _appName;

    public String get_appName() {
        return _appName;
    }

    public int get_appCount() {
        return _appCount;
    }

    public void set_appCount(int _appCount) {
        this._appCount = _appCount;
    }

    private int _appCount;


    public ApplicationData(String _appName, int _appCount) {
        this._appName = _appName;
        this._appCount = _appCount;
    }

    @Override
    public String toString(){
        return this._appName+" : "+this._appCount;
    }

    @Override
    public int compareTo(@NonNull Object obj) {

        ApplicationData objToCompare = (ApplicationData) obj;

        if(this._appCount== objToCompare._appCount){
            return 0;
        }else if(this._appCount<objToCompare._appCount){
            return -1;
        }else{
            return  1;
        }
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof ApplicationData){
            String toCompare =  ((ApplicationData) obj)._appName;
            return _appName.equals(toCompare);
        }
        return false;
    }



    @Override
    public int hashCode(){
        return this._appName.hashCode();
    }
}
