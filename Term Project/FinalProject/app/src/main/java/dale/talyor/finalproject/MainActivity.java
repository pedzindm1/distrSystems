package dale.talyor.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView mListView;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private MainActivity mActivity;
    LocalDataManager dataManager;
    boolean isConnected = false;
    boolean isSupported = false;
    boolean isGroupOwner = false;




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                //Triggers when the user hits the Apps Button
                case R.id.navigation_home:
                    Log.d("myBroadcastReceiver","Home-Nav-Apps");
                    //sets the Heading Text and nulls out the List on the page
                    if(dataManager.getSystemData().nodeList.systemNodeArrayList.size()==0){
                        mTextMessage.setText("Applications on this Device");
                    }else{
                        mTextMessage.setText("Applications on "+(dataManager.getSystemData().nodeList.systemNodeArrayList.size()+1)+" Devices");
                    }

                    mListView.setAdapter(null);
                    generateAppListView(dataManager.getSystemData());

                    return true;
                case R.id.navigation_dashboard:
                    Log.d("myBroadcastReceiver","Home-Nav-Peers");
                    mListView.setAdapter(null);
                    if(!isConnected && isSupported && !isGroupOwner) {
                        Log.d("myBroadcastReceiver","Home-Nav-Peers Connection=No,Supported=Yes, GroupOwner=No");

                        //search Wifi Direct protocol for Peers
                        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("myBroadcastReceiver","Home-Nav-Peers Success");
                            }

                            @Override
                            public void onFailure(int reasonCode) {
                                Log.d("myBroadcastReceiver","Home-Nav-Peers Failure");
                            }
                        });
                    }else if(isConnected && isSupported && !isGroupOwner){
                        Log.d("myBroadcastReceiver","Home-Nav-Peers Connection=Yes,Supported=Yes, GroupOwner=No");
                        mTextMessage.setText("You are connected to a group");

                    }else if(isConnected && isSupported && isGroupOwner){
                        Log.d("myBroadcastReceiver","Home-Nav-Peers Connection=Yes,Supported=Yes, GroupOwner=Yes");
                        mTextMessage.setText("You are the group owner");
                    }else if (!isSupported){
                        mTextMessage.setText("Turn on Wifi to discover devices");
                        Log.d("myBroadcastReceiver","Home-Nav-Peers Connection="+mActivity.isConnected+",Supported=NO, GroupOwner="+mActivity.isGroupOwner);
                    }else{
                        Log.d("myBroadcastReceiver","Home-Nav-Peers Connection="+mActivity.isConnected+",Supported="+mActivity.isSupported+", GroupOwner="+mActivity.isGroupOwner);
                        mTextMessage.setText("Restart the application");
                    }
                    return true;
                case R.id.navigation_notifications:
                    Log.d("myBroadcastReceiver","Home-Nav-Groups");
                    mListView.setAdapter(null);
                    if(!isConnected && isSupported && !isGroupOwner) {
                        Log.d("myBroadcastReceiver","Home-Nav-Groups Connection=No,Supported=Yes, GroupOwner=No");
                        mTextMessage.setText("Creating Group, Please wait");
                        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("myBroadcastReceiver","Home-Nav-Groups Group Success");
                                mActivity.isGroupOwner=true;
                                mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                                    @Override
                                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                                        if(group!=null) {
                                            Log.d("myBroadcastReceiver", "Home-Nav-Groups Group" + group.toString());
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int reason) {
                                if(reason==2) {
                                    mTextMessage.setText("Try again");
                                    mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                                        @Override
                                        public void onSuccess() {
                                            mActivity.isGroupOwner=false;
                                        }

                                        @Override
                                        public void onFailure(int reason) {

                                        }
                                    });
                                }
                                Log.d("myBroadcastReceiver","Home-Nav-Groups Group Failed "+reason);
                            }
                        });
                    }else if(!isConnected && isSupported && isGroupOwner){
                        Log.d("myBroadcastReceiver","Home-Nav-Groups Connection=NO,Supported=Yes, GroupOwner=Yes");

                        mTextMessage.setText("Your group was already created");
                        SystemData currentData =dataManager.getSystemData();
                        generateDeviceListView(currentData);
                    }
                    else if(isConnected && isSupported && isGroupOwner){
                        Log.d("myBroadcastReceiver","Home-Nav-Groups Connection=Yes,Supported=Yes, GroupOwner=Yes");
                        mTextMessage.setText("Your group was created at: " + dataManager.getSystemData().groupOwnerAddress);
                       SystemData currentData =dataManager.getSystemData();
                        generateDeviceListView(currentData);
                    }else if(isConnected && isSupported && !isGroupOwner){
                        Log.d("myBroadcastReceiver","Home-Nav-Groups Connection=Yes,Supported=Yes, GroupOwner=No");
                        mTextMessage.setText("You are connected to: " +dataManager.getSystemData().groupOwnerAddress);
                       // generateDeviceListView(dataManager.getSystemData());
                    }else if (!isSupported){
                        Log.d("myBroadcastReceiver","Home-Nav-Groups Connection="+mActivity.isConnected+",Supported="+mActivity.isSupported+", GroupOwner="+mActivity.isGroupOwner);
                        mTextMessage.setText("Turn on Wifi to create group");
                    }else{
                        Log.d("myBroadcastReceiver","Home-Nav-Groups Connection="+mActivity.isConnected+",Supported="+mActivity.isSupported+", GroupOwner="+mActivity.isGroupOwner);
                        mTextMessage.setText("Restart the application");
                    }
                 return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        setContentView(R.layout.activity_main);
        mActivity = this;
        mTextMessage = (TextView) findViewById(R.id.message);
        mListView = findViewById(R.id.ListView);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
                    @Override
                    public void onChannelDisconnected() {
                        Log.v("myActivity","Channel Disconnected");
                    }
                });
        mReceiver = new myBroadcastReceiver(mManager, mChannel, this);

        dataManager = new LocalDataManager(this);
        dataManager.InitialSystemData(this.getPackageManager());
        generateAppListView(dataManager.getSystemData());
        isConnected = false;
        isSupported = false;
        isGroupOwner = false;

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


    }



    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("myBroadcastReceiver","Home-OnResume");
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("myBroadcastReceiver","Home-OnPause");
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("myBroadcastReceiver","Home-OnStop");
        if(isGroupOwner){
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    mActivity.isGroupOwner=false;
                }

                @Override
                public void onFailure(int reason) {

                }
            });
        }

    }
    private boolean executeCommand(String address){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 "+address);
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }


    protected void generateAppListView(SystemData systemData) {

        ArrayList<String> applicationNames = new ArrayList<>();
        int index=1;
        for (ApplicationData appInfo : systemData.appDataList.listOfApplications) {
            applicationNames.add(index+" - "+appInfo.toString());
            Log.d("myBroadcastReceiver","Home-App-"+appInfo.toString());
            index++;;
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mActivity, R.layout.rowitem, applicationNames);
        mListView.setAdapter(listAdapter);
    }

    private void generateDeviceListView(SystemData systemData) {
        Log.d("myBroadcastReceiver","Home-Device-"+systemData);
        ArrayList<String> applicationNames = new ArrayList<>();
        int index=1;
        for (SystemNode nodeInfo : systemData.nodeList.systemNodeArrayList) {
            applicationNames.add(nodeInfo.nodeAddress.getHostAddress());
            Log.d("myBroadcastReceiver","Home-Device-"+nodeInfo.nodeAddress.getHostAddress());
            index++;;
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mActivity, R.layout.rowitem, applicationNames);
        mListView.setAdapter(listAdapter);
    }


}
