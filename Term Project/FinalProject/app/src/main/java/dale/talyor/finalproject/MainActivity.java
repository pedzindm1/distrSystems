package dale.talyor.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView mListView;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private MainActivity mActivity;
    private PackageManager pm;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText("Apps");
                    mListView.setAdapter(null);
                    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                    ArrayList<String> applicationNames = new ArrayList<>();
                    for (ApplicationInfo packageInfo : packages) {
                        if(pm.getLaunchIntentForPackage(packageInfo.packageName)!=null) {
                            applicationNames.add(packageInfo.packageName.toString());
                        }
                    }
                        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mActivity, R.layout.rowitem, applicationNames);
                        mListView.setAdapter(listAdapter);

                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText("Discovering Peers");
                    mListView.setAdapter(null);
                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.v("myActivity","discoverPeers Success");

                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Log.v("myActivity","discoverPeers Failure");
                        }
                    });

                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText("Creating Group, Please Connect");
                    mListView.setAdapter(null);
                    mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.v("myActivity","createGroup Success");
                            // Device is ready to accept incoming connections from peers.
                           // mTextMessage.setText("Successfully Created a Group");
                            mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                                @Override
                                public void onGroupInfoAvailable(WifiP2pGroup group) {
                                    Log.v("myActivity","Client Group Available");
                                    if(group!=null) {
                                        ArrayList<String> deviceNameList = new ArrayList<>();
                                        for (WifiP2pDevice device : group.getClientList()) {
                                            deviceNameList.add(device.deviceName + ": " + device.deviceAddress);
                                        }
                                        ListView mListView = findViewById(R.id.ListView);
                                        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(mActivity, R.layout.rowitem, deviceNameList);
                                        mListView.setAdapter(listAdapter);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.v("myActivity","createGroup Failure: "+ reason);
                        }
                    });
                    mTextMessage.setText("Group Created");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> applicationNames = new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!=null) {
                applicationNames.add(packageInfo.packageName.toString());
            }
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mActivity, R.layout.rowitem, applicationNames);
        mListView.setAdapter(listAdapter);
        
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
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
