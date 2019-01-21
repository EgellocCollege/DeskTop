package com.hello.desktop;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv;
    private Button btn;

    LauncherUtils launcherUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tv = findViewById(R.id.tv);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);
        launcherUtils = new LauncherUtils();
    }

    public void showApps(View v){
        Intent i = new Intent(this, AppsListActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        Toast.makeText(this, "KeyCode: " + keyCode, Toast.LENGTH_SHORT).show();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {

        display();
//        launchAppChooser();
        launcherUtils.clearDefaultLauncherApps(this);
        launcherUtils.setDefaultLauncher(this,HomeActivity.this.getLocalClassName());

    }

    private  List<ResolveInfo> getHomeApps() {
            List<ResolveInfo> mApps = new ArrayList<>();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);

            PackageManager pManager = getPackageManager();
            mApps = pManager.queryIntentActivities(intent,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

            return mApps;
    }

    private void display(){
        List<ResolveInfo>  homeApps = getHomeApps();

        StringBuilder sb = new StringBuilder();
        for (ResolveInfo info:homeApps
             ) {
            sb.append(info.activityInfo.name + " " + info.priority + "\n");
        }
        sb.append( "isMyAppLauncherDefault: " + isMyAppLauncherDefault() + "\n");
        sb.append( "LauncherDefault: " + getDefaultLauncher() + "\n");
        sb.append( "CurrentActivityName: " +  HomeActivity.this.getClass().getName() + "\n");


        tv.setText(sb.toString());
    }

    private boolean isMyAppLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();

        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }
    private String getDefaultLauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }


    private void launchAppChooser() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
