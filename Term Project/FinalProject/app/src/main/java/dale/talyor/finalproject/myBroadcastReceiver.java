package dale.talyor.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;

import static android.net.wifi.p2p.WifiP2pManager.*;

/**
 * Created by dalepedzinski on 11/25/17.
 */

public class myBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private MainActivity mActivity;
    private Collection<WifiP2pDevice> peerList;
    private ServerTask serverTask;

    public myBroadcastReceiver(WifiP2pManager mManager, Channel mChannel, MainActivity activity) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("myBroadcastReceiver",action);

        if (WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi is enabled
                    mActivity.isSupported = true;
                    Log.d("myBroadcastReceiver","Wifi-Enabled");
                } else {
                    Log.d("myBroadcastReceiver","Wifi-Disabled- Reset Data");
                    // Wi-Fi P2P is not enabled
                    Toast.makeText(mActivity.getApplicationContext(), "Please turn on Wifi", Toast.LENGTH_LONG).show();
                    //reset everything
                    if(mActivity.isGroupOwner){
                        mManager.removeGroup(mChannel, new ActionListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(int reason) {

                            }
                        });
                    }
                    if(mActivity.isConnected){
                        mManager.cancelConnect(mChannel, new ActionListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(int reason) {

                            }
                        });
                    }
                    mActivity.isSupported = false;
                    mActivity.isConnected = false;
                    mActivity.isGroupOwner = false;
                    mActivity.dataManager.InitialSystemData(mActivity.getPackageManager());
                    mActivity.generateAppListView(mActivity.dataManager.getSystemData());
                }

        } else if (WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d("myBroadcastReceiver","Peers Changed");
            if(mActivity.isSupported & !mActivity.isConnected && !mActivity.isGroupOwner) {
                Log.d("myBroadcastReceiver","Wifi is on, Not Connected and Not group owner");
                    if (mManager != null) {
                        mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                            @Override
                            public void onPeersAvailable(WifiP2pDeviceList peers) {
                                if (peers.getDeviceList().size() == 0) {
                                    Toast.makeText(mActivity.getApplicationContext(), "No Peers Found", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                Toast.makeText(mActivity.getApplicationContext(), peers.getDeviceList().size() + " Devices found", Toast.LENGTH_LONG).show();
                                Log.v("myBroadcastReceiver", peers.toString());
                                ArrayList<String> deviceNameList = new ArrayList<>();
                                peerList = peers.getDeviceList();
                                SystemNodeList currentNodeState = new SystemNodeList();
                                SystemData systemState = mActivity.dataManager.getSystemData();

                                for (WifiP2pDevice device : peers.getDeviceList()) {
                                   // currentNodeState.systemNodeArrayList.add(new SystemNode(device.deviceName,device.deviceAddress));
                                    deviceNameList.add(device.deviceName + ": " + device.deviceAddress);
                                    Log.d("myBroadcastReceiver","Device: "+device.deviceName + ": " + device.deviceAddress);
                                }
                                systemState.nodeList= currentNodeState;
                                mActivity.dataManager.saveSystemData(systemState);
                                final ListView mListView = mActivity.findViewById(R.id.ListView);
                                ArrayAdapter<String> listAdapter = new ArrayAdapter<>(mActivity, R.layout.rowitem, deviceNameList);
                                mListView.setAdapter(listAdapter);

                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, final int position,
                                                            long id) {
                                        WifiP2pDevice device = (WifiP2pDevice) peerList.toArray()[position];
                                        if (!mActivity.isConnected && !mActivity.isGroupOwner) {
                                            WifiP2pConfig config = new WifiP2pConfig();
                                            config.deviceAddress = device.deviceAddress;
                                            config.wps.setup = WpsInfo.PBC;
                                            mManager.connect(mChannel, config, new ActionListener() {

                                                @Override
                                                public void onSuccess() {
                                                    mActivity.isConnected = true;
                                                    Toast.makeText(mActivity.getApplicationContext(), "connected", Toast.LENGTH_LONG).show();
                                                    WifiP2pDevice device= (WifiP2pDevice) peerList.toArray()[position];
                                                    SystemData systemState = mActivity.dataManager.getSystemData();
                                                    systemState.nodeList.systemNodeArrayList.add(new SystemNode(device.deviceName,device.deviceAddress));

                                                    Log.d("myBroadcastReceiver","Connected: "+ device.deviceName+ ": " + device.deviceAddress);

                                                }

                                                @Override
                                                public void onFailure(int reason) {
                                                    Toast.makeText(mActivity.getApplicationContext(), "connection failed, try again", Toast.LENGTH_LONG).show();
                                                    WifiP2pDevice device= (WifiP2pDevice) peerList.toArray()[position];
                                                    Log.d("myBroadcastReceiver","Failed to Connect to: "+ device.deviceName+ ": " + device.deviceAddress);
                                                }
                                            });

                                        } else if(!mActivity.isGroupOwner){
                                            mManager.cancelConnect(mChannel, new ActionListener() {
                                                @Override
                                                public void onSuccess() {
                                                    mActivity.isConnected = false;
                                                    Toast.makeText(mActivity.getApplicationContext(), "disconnected", Toast.LENGTH_LONG).show();
                                                    WifiP2pDevice device= (WifiP2pDevice) peerList.toArray()[position];
                                                    Log.d("myBroadcastReceiver","disconnected from: "+ device.deviceName+ ": " + device.deviceAddress);
                                                }

                                                @Override
                                                public void onFailure(int reason) {
                                                    Toast.makeText(mActivity.getApplicationContext(), "disconnection failed, try again", Toast.LENGTH_LONG).show();
                                                    WifiP2pDevice device= (WifiP2pDevice) peerList.toArray()[position];
                                                    Log.d("myBroadcastReceiver","failed to disconnect from: "+ device.deviceName+ ": " + device.deviceAddress);

                                                }
                                            });
                                        }
                                    }
                                });


                            }

                        });
                    }
            }else if(mActivity.isSupported & mActivity.isConnected && !mActivity.isGroupOwner){
                if(mActivity.dataManager.getSystemData().groupOwnerAddress!=null && !executeCommand(mActivity.dataManager.getSystemData().groupOwnerAddress.getHostAddress())) {
                    Toast.makeText(mActivity.getApplicationContext(), "Group was Disconnected", Toast.LENGTH_LONG).show();
                    Toast.makeText(mActivity.getApplicationContext(), "Resetting App information", Toast.LENGTH_LONG).show();
                    mManager.removeGroup(mChannel, new ActionListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int reason) {

                        }
                    });
                    mActivity.isSupported = false;
                    mActivity.isConnected = false;
                    mActivity.isGroupOwner = false;
                    mActivity.dataManager.InitialSystemData(mActivity.getPackageManager());
                    mActivity.generateAppListView(mActivity.dataManager.getSystemData());

                }
            }
        } else if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(mActivity.isSupported) {
                // Respond to new connection or disconnections
                if (mManager == null) {
                    return;
                }

                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected() && (mActivity.isConnected)) {
                    Log.d("myBroadcastReceiver","Device Connected");
                    // We are connected with the other device, request connection
                    // info to find group owner IP

                    mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            Log.d("myBroadcastReceiver", info.toString());
                            // InetAddress from WifiP2pInfo struct.
                            InetAddress groupOwnerAddress = info.groupOwnerAddress;

                            // After the group negotiation, we can determine the group owner
                            // (server).
                            if (info.groupFormed && info.isGroupOwner && mActivity.isConnected) {
                                TextView _textView = mActivity.findViewById(R.id.message);
                                _textView.setText("Group at: " + info.groupOwnerAddress.toString());
                                SystemData localData= mActivity.dataManager.getSystemData();
                                //localData.groupOwnerAddress=info.groupOwnerAddress;
                                //mActivity.dataManager.saveSystemData(localData);
                                if (serverTask == null) {
                                    Log.d("myBroadcastReceiver","Starting Data Transfer");
                                    serverTask = new ServerTask(mActivity.getApplicationContext());
                                    serverTask.execute(
                                            new TaskParameters(localData));
                                    Log.d("myBroadcastReceiver","Finished Data Transfer");
                                }


                            } else if (info.groupFormed && mActivity.isConnected) {
                                Log.d("myBroadcastReceiver","Started Client Data Transfer");
                                ClientTask clientTask = new ClientTask(mActivity.getApplicationContext());
                                SystemData localData= mActivity.dataManager.getSystemData();
                                localData.groupOwnerAddress=info.groupOwnerAddress;
                                mActivity.dataManager.saveSystemData(localData);

                                clientTask.execute(new TaskParameters(localData));
                                Log.d("myBroadcastReceiver","Finished Client Data Transfer");
                            }
                        }
                    });
                }else if(networkInfo.isConnected() && (mActivity.isGroupOwner && !mActivity.isConnected)) {
                    mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            Log.d("myBroadcastReceiver", info.toString());
                            // InetAddress from WifiP2pInfo struct.
                            InetAddress groupOwnerAddress = info.groupOwnerAddress;
                            // After the group negotiation, we can determine the group owner
                            // (server).
                            if (info.groupFormed && info.isGroupOwner) {
                                mActivity.isGroupOwner = true;




                                TextView _textView = mActivity.findViewById(R.id.message);
                                _textView.setText("Group Created at: " + info.groupOwnerAddress.toString());
                                mActivity.isConnected=true;
                                SystemData localData= mActivity.dataManager.getSystemData();
                                localData.groupOwnerAddress=info.groupOwnerAddress;
                                mActivity.dataManager.saveSystemData(localData);
                            }
                        }
                    });
                }else if(!networkInfo.isConnected()){
                    Log.d("myBroadcastReceiver", "Client has been lost");
                }
            }
        } else if (WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

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


}
