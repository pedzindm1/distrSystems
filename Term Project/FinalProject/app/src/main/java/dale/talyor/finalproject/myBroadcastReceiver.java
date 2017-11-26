package dale.talyor.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.net.wifi.p2p.WifiP2pManager.*;

/**
 * Created by dalepedzinski on 11/25/17.
 */

public class myBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private MainActivity mActivity;
    private Collection<WifiP2pDevice> peerList;


    public myBroadcastReceiver(WifiP2pManager mManager, Channel mChannel, MainActivity activity) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v("myBroadcastReceiver",action);

        if (WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //mActivity.setIsWifiP2pEnabled(true);
                // Wifi P2P is enabled
                //System.console().printf("*********WIFI ENABLED*******");
            } else {
                // Wi-Fi P2P is not enabled
               //mActivity.setIsWifiP2pEnabled(false);
            }
        } else if (WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel,new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Log.v("myBroadcastReceiver",peers.toString());
                        ArrayList<String> deviceNameList = new ArrayList<>();
                        peerList = peers.getDeviceList();
                        for (WifiP2pDevice device:peers.getDeviceList()) {
                            deviceNameList.add(device.deviceName+": "+device.deviceAddress);
                        }
                        ListView mListView =  mActivity.findViewById(R.id.ListView);
                        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(mActivity, R.layout.rowitem, deviceNameList);
                        mListView.setAdapter( listAdapter );

                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                                 WifiP2pDevice device = (WifiP2pDevice)peerList.toArray()[position];

                                WifiP2pConfig config = new WifiP2pConfig();
                                config.deviceAddress = device.deviceAddress;
                                config.wps.setup = WpsInfo.PBC;
                                mManager.connect(mChannel, config, new ActionListener() {

                                    @Override
                                    public void onSuccess() {
                                        // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                                        Toast.makeText(mActivity.getApplicationContext(), "connected", Toast.LENGTH_LONG).show();

                                    }

                                    @Override
                                    public void onFailure(int reason) {

                                    }
                                });
                            }
                        });

                    }

                });
            }
        } else if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, new ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        Log.v("myBroadcastReceiver",info.toString());
                        // InetAddress from WifiP2pInfo struct.
                        InetAddress groupOwnerAddress = info.groupOwnerAddress;

                        // After the group negotiation, we can determine the group owner
                        // (server).
                        if (info.groupFormed && info.isGroupOwner) {
                            // Do whatever tasks are specific to the group owner.
                            // One common case is creating a group owner thread and accepting
                            // incoming connections.

                        } else if (info.groupFormed) {
                            // The other device acts as the peer (client). In this case,
                            // you'll want to create a peer thread that connects
                            // to the group owner.
                        }
                    }
                });
            }


        } else if (WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
//            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }
    }




}
