package com.jtautry.applist;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AppList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind to AppListTextView
//        TextView appListTextView = (TextView) findViewById(R.id.AppListTextView);
//        appListTextView.setText("This is the app list.");

        //list the apps
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
//            if packageInfo.packageName.startsWith("com.android")
            Log.d(TAG, "Installed package: " + packageInfo.packageName);
        }

    }
}
