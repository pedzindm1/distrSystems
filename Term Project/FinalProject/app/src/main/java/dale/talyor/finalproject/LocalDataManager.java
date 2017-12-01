package dale.talyor.finalproject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dalepedzinski on 11/30/17.
 */

public class LocalDataManager {

    private Context context;

    public LocalDataManager(Context context) {
        this.context = context;
    }

    public static String getFileContent(FileInputStream fis )
    {
        try{ BufferedReader br =
                new BufferedReader( new InputStreamReader(fis ));

            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            return sb.toString();
        }catch (Exception e){

        }
        return null;
    }


    protected void saveSystemData(SystemData systemState) {
        FileOutputStream fos = null;
        try {
            String FILENAME = "system_data";
            fos = this.context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            Collections.sort(systemState.appDataList.listOfApplications);

            fos.write(gson.toJson(systemState).getBytes());
            fos.close();
        } catch (Exception e) {

        }
    }

    protected SystemData getSystemData() {
        SystemData systemData=null;
        FileInputStream fos = null;
        try {
            String FILENAME = "system_data";
            fos = this.context.openFileInput(FILENAME);
            String systemDataString= getFileContent(fos);
            Gson gson  = new Gson();
            systemData=gson.fromJson(systemDataString,SystemData.class);
            fos.close();
            return  systemData;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        return  null;
    }

    protected void InitialSystemData(PackageManager pm) {

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<ApplicationData> applicationsToStore = new ArrayList<>();
        SystemData systemData = new SystemData(new ApplicationList(applicationsToStore));
        for (ApplicationInfo packageInfo : packages) {
            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!=null) {

                applicationsToStore.add(new ApplicationData(packageInfo.packageName.toString(),1));
            }
        }
        Gson gson = new Gson();
        Log.v("DataManager", gson.toJson(systemData));
        this.saveSystemData(systemData);

    }

}
