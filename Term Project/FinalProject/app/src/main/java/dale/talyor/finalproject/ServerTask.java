package dale.talyor.finalproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dalepedzinski on 11/28/17.
 */

public class ServerTask extends AsyncTask<TaskParameters,Integer,Void> {

    private  Context context;

    public ServerTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(TaskParameters... taskParameters) {
        try {
            Log.d("Server","Starting Connection");
            //Gson gson = new Gson();
            Applications currentData = taskParameters[0].getApplications();
            ServerSocket serverSocket = new ServerSocket(taskParameters[0].getServerInfo().getPort());
            //serverSocket.setReuseAddress(true);
            Log.d("Server",taskParameters[0].getServerInfo().toString());
            Socket socket = serverSocket.accept();
            Log.d("Server","Connected");
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Applications clientData = (Applications) ois.readObject();
            currentData.updateApplication(clientData._applicationsData);
            saveAppData(currentData._applicationsData);
//            InputStream inputStream = client.getInputStream();
//            Log.d("Server","starting Stream :"+ client.getInetAddress());
//            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
//            Log.d("Server","Stream is :"+ r.ready());
//            StringBuilder total = new StringBuilder();
//            String line;
//            Log.d("Server","trying to get a Line");
//            r.readLine()
//            if(!total.equals(null)) {
//
//                Log.d("Server", "message received");
//                Applications appDataFromClient = gson.fromJson(total.toString(), Applications.class);
//                savedData.updateApplication(appDataFromClient._applicationsData);
//            }
//            Log.d("Server","new message created");
//            OutputStream outputStream = client.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
//            writer.write(gson.toJson(savedData));
//            outputStream.flush();
            socket.close();
            Log.d("Server","Done");

        }catch (Exception e){
            Log.d("Server Error",e.toString());
        }
        return  null;
    }

    private void saveAppData(ArrayList<ApplicationData> appsToStore){
        FileOutputStream fos = null;
        try {
            String FILENAME = "application_data";
            fos = this.context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            Applications appDataSorted= new Applications(appsToStore);
            Collections.sort(appDataSorted._applicationsData);

            fos.write(gson.toJson(appDataSorted).getBytes());
            fos.close();
        } catch (Exception e) {
        }
    }
}
//
//public class ServerTask implements Runnable {
//
//    private InetSocketAddress serverInfo;
//    private Applications applications;
//
//    public ServerTask(Applications applications, InetSocketAddress serverInfo) {
//        this.serverInfo = serverInfo;
//        this.applications = applications;
//    }
//
//
//    @Override
//    public void run() {
//                try {
//            Log.d("Server","Starting Connection");
//            ServerSocket serverSocket = new ServerSocket();
//            serverSocket.setReuseAddress(true);
//            serverSocket.bind(this.serverInfo);
//            Log.d("Server",this.serverInfo.toString());
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
//            Applications savedData= this.applications;
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
//    }
//}
