package dale.talyor.finalproject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by dalepedzinski on 11/25/17.
 */

public class AppMetaAsyncTask extends AsyncTask {

    private Context context;
    private TextView statusText;

    public AppMetaAsyncTask(Context context, View statusText) {
        this.context = context;
        this.statusText = (TextView) statusText;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
//
//    @Override
//    protected Object doInBackground(Object[] objects) {
//        try {
//
//            /**
//             * Create a server socket and wait for client connections. This
//             * call blocks until a connection is accepted from a client
//             */
//            ServerSocket serverSocket = new ServerSocket(8888);
//            Socket client = serverSocket.accept();
//
//            /**
//             * If this code is reached, a client has connected and transferred data
//             * Save the input stream from the client as a JPEG file
//             */
//            final File f = new File(Environment.getExternalStorageDirectory() + "/"
//                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
//                    + ".jpg");
//
//            File dirs = new File(f.getParent());
//            if (!dirs.exists())
//                dirs.mkdirs();
//            f.createNewFile();
//            InputStream inputstream = client.getInputStream();
//            copyFile(inputstream, new FileOutputStream(f));
//            serverSocket.close();
//            return f.getAbsolutePath();
//        } catch (IOException e) {
//
//            return null;
//        }
//    }
//
//    /**
//     * Start activity that can handle the JPEG image
//     */
//    @Override
//    protected void onPostExecute(String result) {
//        if (result != null) {
//            statusText.setText("File copied - " + result);
//            Intent intent = new Intent();
//            intent.setAction(android.content.Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
//            context.startActivity(intent);
//        }
//    }
}
