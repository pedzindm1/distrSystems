package dale.talyor.finalproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by dalepedzinski on 11/28/17.
 */

public class ClientTask extends AsyncTask<TaskParameters,Integer,Void>
{
    private Context context;
    private LocalDataManager dataManager;

    public ClientTask(Context context) {
        this.context = context;
        this.dataManager = new LocalDataManager(context);
    }

    @Override
    protected Void doInBackground(TaskParameters... taskData) {
        try {
            if(taskData[0].systemData!=null) {
                Log.d("myBroadcastReceiver","ClientTask-"+taskData[0].systemData.groupOwnerAddress+":"+taskData[0].systemData.groupOwnerPort);
                Socket clientSocket = new Socket(taskData[0].systemData.groupOwnerAddress, taskData[0].systemData.groupOwnerPort);
                Log.d("myBroadcastReceiver","ClientTask-Connected");

                Log.d("myBroadcastReceiver","ClientTask-Connected -"+taskData[0].systemData.groupOwnerAddress+":"+taskData[0].systemData.groupOwnerPort);

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                objectOutputStream.writeObject(taskData[0].systemData);
                objectOutputStream.flush();
                Log.d("myBroadcastReceiver","ClientTask-Data Sent");

                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                Log.d("myBroadcastReceiver","ClientTask-Data Received");
                SystemData systemState = (SystemData) ois.readObject();
                dataManager.saveSystemData(systemState);
                Log.d("myBroadcastReceiver","ClientTask-Data Saved");

                clientSocket.close();
                Log.d("myBroadcastReceiver","ClientTask-Connection closed");
            }else{
                Log.d("myBroadcastReceiver","ClientTask-Error");
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("myBroadcastReceiver","ClientTask-Error"+e.getMessage());
        }
        return  null;
    }



}

