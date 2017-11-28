package dale.talyor.finalproject;

import java.util.ArrayList;

/**
 * Created by dalepedzinski on 11/25/17.
 */

public class Applications {

    public ArrayList<ApplicationData> _applicationsData;

    public Applications(ArrayList<ApplicationData> _applicationsData) {
        this._applicationsData = _applicationsData;
    }

    public void updateApplication(ArrayList<ApplicationData> newApplicationData){
        for (ApplicationData newApplication: newApplicationData) {
            if(_applicationsData.contains(newApplication)){
                int oldAppDataIndex= _applicationsData.indexOf(newApplication);
                ApplicationData oldAppData = _applicationsData.get(oldAppDataIndex);
                oldAppData.set_appCount(oldAppData.get_appCount()+1);
            }
        }
    }
}
