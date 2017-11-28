package dale.talyor.finalproject;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by dalepedzinski on 11/28/17.
 */

//public class ClientTask extends AsyncTask<TaskParameters,Integer,Void> {
//
//    @Override
//    protected Void doInBackground(TaskParameters... clientTaskParameters) {
//        try {
//            Socket clientSocket = new Socket();
//            Log.d("Client","Starting Connection");
//            clientSocket.connect(clientTaskParameters[0].getServerInfo(),200);
//            Log.d("Client","Connected");
//            OutputStream out = clientSocket.getOutputStream();
//            Applications savedData=clientTaskParameters[0].getApplications();
//            Log.d("Client",savedData._applicationsData.toString());
//            Gson gson = new Gson();
//            out.write(gson.toJson(savedData).getBytes());
//
//            out.flush();
//            Log.d("Client","Sent Data");
//            Log.d("Client","Waiting on new Data");
//            InputStream inputStream = clientSocket.getInputStream();
//            Log.d("Client","new Data started");
//            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder total = new StringBuilder();
//            String line;
//            while ((line = r.readLine()) != null) {
//                total.append(line);
//                Log.d("Client","new Data :"+ line);
//            }
//            Applications appDataFromClient= gson.fromJson(total.toString(), Applications.class);
//            savedData.updateApplication(appDataFromClient._applicationsData);
//            Log.d("Client","Received Data");
//            clientSocket.close();
//            Log.d("Client","Done");
//
//        }catch (Exception e){
//            Log.d("ClientError",e.toString());
//        }
//        return  null;
//    }
//}
public class ClientTask implements Runnable {

    private InetSocketAddress serverInfo;
    private Applications applications;

    public ClientTask(Applications applications, InetSocketAddress serverInfo) {
        this.serverInfo = serverInfo;
        this.applications = applications;
    }


    @Override
    public void run() {
        try {
            Socket clientSocket = new Socket();
            Log.d("Client","Starting Connection");
            clientSocket.bind(null);
            clientSocket.setReuseAddress(true);
            clientSocket.connect(this.serverInfo,200);
            Log.d("Client","Connected");
            OutputStream out = clientSocket.getOutputStream();
            Applications savedData=this.applications;
            Log.d("Client",savedData._applicationsData.toString());
            Gson gson = new Gson();
            out.write(gson.toJson(savedData).getBytes());
            out.flush();

            Log.d("Client","Sent Data");
            Log.d("Client","Waiting on new Data");
            InputStream inputStream = clientSocket.getInputStream();
            Log.d("Client","new Data started");
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
                Log.d("Client","new Data :"+ line);
            }
            Applications appDataFromClient= gson.fromJson(total.toString(), Applications.class);
            savedData.updateApplication(appDataFromClient._applicationsData);
            Log.d("Client","Received Data");
            clientSocket.close();
            Log.d("Client","Done");

        }catch (Exception e){
            Log.d("ClientError",e.toString());
        }
    }
}

