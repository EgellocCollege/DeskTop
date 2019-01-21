package com.hello.desktop;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public  class LauncherUtils {

    private static String TAG = "Set Launcher";
    /**
     * 获取到Android系统硬件或者手机上安装的全部的Launcher
     */
    public List<ResolveInfo> getAllLauncherApps(Context context){
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        return pm.queryIntentActivities(intent,0);
    }

    public void  clearDefaultLauncherApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ArrayList <IntentFilter> intentList = new ArrayList <>();
        ArrayList<ComponentName> componetNameList = new ArrayList<>();
        //查询到首先的Activity
        packageManager.getPreferredActivities(intentList, componetNameList, null);
        int size= intentList.size();
        try {
            for (int i= 0;i<size;i++) {
                IntentFilter intentFilter = intentList.get(i);
                if (intentFilter.hasAction(Intent.ACTION_MAIN) && intentFilter.hasCategory(Intent.CATEGORY_HOME)) {
                    packageManager.clearPackagePreferredActivities(componetNameList.get(i).getPackageName());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public StringBuffer createMatchName(ComponentName launcher){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(launcher.getPackageName());
        stringBuffer.append(".");
        stringBuffer.append(launcher.getClassName());
        return stringBuffer;
    }

    public void setDefaultLauncher(Context context,String mainActivity){
        clearDefaultLauncherApps(context);
        List<ResolveInfo> allLauncherList = getAllLauncherApps(context);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        ComponentName launcher = new ComponentName(context.getPackageName(),mainActivity);

        ComponentName[] componentNameSet = new  ComponentName[allLauncherList.size()];
        int defaultMatchLauncher = 0;
        int size = allLauncherList.size();
        for (int i=0;i<size;i++){
            ResolveInfo resolveInfo = allLauncherList.get(i);
            componentNameSet[i] = new ComponentName(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name);
            StringBuffer stringBuffer = createMatchName(launcher);
            Log.i(TAG," 索引是" + i + "  将要设置成默认的Launcher:" + stringBuffer.toString() + "信息名：" + resolveInfo.activityInfo.name );
            if (stringBuffer.toString().equals(resolveInfo.activityInfo.name)){
                defaultMatchLauncher = resolveInfo.match;
                Log.i(TAG," 匹配到的match :  " + resolveInfo.match);
            }

        }
        try{
            Log.i(TAG," Try to Reset Launcher");
            context.getPackageManager().addPreferredActivity(intentFilter, defaultMatchLauncher, componentNameSet, launcher);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
