package dale.talyor.finalproject;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dalepedzinski on 11/28/17.
 */

//public class ServerTask extends AsyncTask<TaskParameters,Integer,Void> {
//
//
//
//    @Override
//    protected Void doInBackground(TaskParameters... taskParameters) {
//        try {
//            Log.d("Server","Starting Connection");
//            ServerSocket serverSocket = new ServerSocket();
//            serverSocket.setReuseAddress(true);
//            serverSocket.bind(taskParameters[0].getServerInfo());
//            Log.d("Server",taskParameters[0].getServerInfo().toString());
//            Socket client = serverSocket.accept();
//            Log.d("Server","Connected");
//            InputStream inputStream = client.getInputStream();
//            Log.d("Server","starting Stream :"+ client.getInetAddress());
//            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
//            Log.d("Server","Stream is :"+ r.ready());
//            StringBuilder total = new StringBuilder();
//            String line;
//            Log.d("Server","trying to get a Line");
//            while ((line = r.readLine()) != null) {
//                total.append(line);
//                Log.d("Server","Read Line: "+line);
//            }
//            Gson gson = new Gson();
//            Log.d("Server","message received");
//            Applications appDataFromClient= gson.fromJson(total.toString(), Applications.class);
//            Applications savedData= taskParameters[0].getApplications();
//            savedData.updateApplication(appDataFromClient._applicationsData);
//
//            Log.d("Server","new message created");
//            OutputStream outputStream = client.getOutputStream();
//            outputStream.write(gson.toJson(savedData).getBytes());
//            outputStream.flush();
//            client.close();
//            Log.d("Server","Done");
//
//        }catch (Exception e){
//            Log.d("Server Error",e.toString());
//        }
//        return  null;
//    }
//}

public class ServerTask implements Runnable {

    private InetSocketAddress serverInfo;
    private Applications applications;

    public ServerTask(Applications applications, InetSocketAddress serverInfo) {
        this.serverInfo = serverInfo;
        this.applications = applications;
    }


    @Override
    public void run() {
                try {
            Log.d("Server","Starting Connection");
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(this.serverInfo);
            Log.d("Server",this.serverInfo.toString());
            Socket client = serverSocket.accept();
            Log.d("Server","Connected");
            InputStream inputStream = client.getInputStream();
            Log.d("Server","starting Stream :"+ client.getInetAddress());
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            Log.d("Server","Stream is :"+ r.ready());
            StringBuilder total = new StringBuilder();
            String line;
            Log.d("Server","trying to get a Line");
            while ((line = r.readLine()) != null) {
                total.append(line);
                Log.d("Server","Read Line: "+line);
            }
            Gson gson = new Gson();
            Log.d("Server","message received");
            Applications appDataFromClient= gson.fromJson(total.toString(), Applications.class);
            Applications savedData= this.applications;
            savedData.updateApplication(appDataFromClient._applicationsData);

            Log.d("Server","new message created");
            OutputStream outputStream = client.getOutputStream();
            outputStream.write(gson.toJson(savedData).getBytes());
            outputStream.flush();
            client.close();
            Log.d("Server","Done");

        }catch (Exception e){
            Log.d("Server Error",e.toString());
        }
    }
}
