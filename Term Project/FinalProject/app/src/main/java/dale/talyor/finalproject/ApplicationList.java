package dale.talyor.finalproject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dalepedzinski on 11/25/17.
 */

public class ApplicationList implements Serializable {

    public ArrayList<ApplicationData> listOfApplications = new ArrayList<>();

    public ApplicationList(ArrayList<ApplicationData> listOfApplications) {
        this.listOfApplications = listOfApplications;
    }

    public void updateApplicationData(ArrayList<ApplicationData> newApplicationData){
        for (ApplicationData newApplication: newApplicationData) {
            if(listOfApplications.contains(newApplication)){
                int oldAppDataIndex= listOfApplications.indexOf(newApplication);
                ApplicationData oldAppData = listOfApplications.get(oldAppDataIndex);
                oldAppData.setAppCount(oldAppData.getAppCount()+1);
            }else{
                listOfApplications.add(newApplication);
            }
        }
    }
}
