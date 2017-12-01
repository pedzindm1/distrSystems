package dale.talyor.finalproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dalepedzinski on 11/28/17.
 */

public class ServerTask extends AsyncTask<TaskParameters,Integer,Void> {

    private Context context;
    private SystemNode node;
    private LocalDataManager dataManager;

    public ServerTask(Context context) {
        this.context = context;
        this.dataManager = new LocalDataManager(context);
    }

    @Override
    protected Void doInBackground(TaskParameters... taskData) {
        try {
            if(taskData!=null) {

            Log.d("myBroadcastReceiver","ServerTask-Starting Connection");

            SystemData currentState = taskData[0].systemData;
            ServerSocket serverSocket = new ServerSocket(taskData[0].systemData.groupOwnerPort);

            Log.d("myBroadcastReceiver","ServerTask-"+taskData[0].systemData.groupOwnerAddress.getHostAddress()+":"+taskData[0].systemData.groupOwnerPort);
            Socket socket = serverSocket.accept();
            Log.d("myBroadcastReceiver","ServerTask-Connected at "+socket.getInetAddress());
            currentState.nodeList.systemNodeArrayList.add(new SystemNode(socket.getInetAddress()));
            //Send Updated State
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            SystemData clientData = (SystemData) ois.readObject();
                Log.d("myBroadcastReceiver","ServerTask-received file");
            currentState.appDataList.updateApplicationData(clientData.appDataList.listOfApplications);
            Log.d("myBroadcastReceiver","ServerTask-data updated");
            dataManager.saveSystemData(currentState);
            Log.d("myBroadcastReceiver","ServerTask-Save Data");

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            Log.d("myBroadcastReceiver","ServerTask-Send Data");
            oos.writeObject(currentState);
            oos.flush();

            socket.close();
            Log.d("myBroadcastReceiver","ServerTask-Done");
            }else{
                Log.d("myBroadcastReceiver","ServerTask-Error");
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("myBroadcastReceiver","ServerTask-Error"+e.getMessage());
        }
        return  null;
    }

}

