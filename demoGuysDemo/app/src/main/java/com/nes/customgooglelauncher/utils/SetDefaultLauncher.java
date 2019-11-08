package com.nes.customgooglelauncher.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.nes.utils.LogX;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JIANGYICHENG
 * @date 2019/4/1
 *
 */

public class SetDefaultLauncher {
    private static final String TAG = SetDefaultLauncher.class.getSimpleName();

    private static final String GOOGLE_LAUNCHER_TV_HOME_PACKAGE_NAME = "com.google.android.tvlauncher";
    private static final String GOOGLE_LAUNCHER_TV_HOME_CLASS_NAME = "com.google.android.tvlauncher.MainActivity";

    private static final String NES_LAUNCHER_TV_HOME_CLASS_NAME = "com.nes.customgooglelauncher.ui.activity.MainActivity";
    public static void disableGoogleLauncher(Context context){
        PackageManager pm = context.getPackageManager();
        ComponentName name = new ComponentName(GOOGLE_LAUNCHER_TV_HOME_PACKAGE_NAME, GOOGLE_LAUNCHER_TV_HOME_CLASS_NAME);
        int componentEnabledSetting;
        try {
            componentEnabledSetting = pm.getComponentEnabledSetting(name);
            LogX.d("","---------------componentEnabledSetting0 = "
                    + componentEnabledSetting);
            pm.setComponentEnabledSetting(name,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Log.d("","---------------componentEnabledSetting1 = "
                    + e1.getMessage());
        }
        LogX.d("","---------------componentEnabledSetting = end");

        setDefaultLauncher(context);
        silentDelete(context, GOOGLE_LAUNCHER_TV_HOME_PACKAGE_NAME);
//        execCommand("pm","uninstall", GOOGLE_LAUNCHER_TV_HOME_PACKAGE_NAME);
    }

    private static void setDefaultLauncher(Context context) {
        // get default component
        LogX.d(TAG, "setDefaultLauncher ======= start");
        PackageManager pm = context.getPackageManager();
        //默认launcher包名
        String packageName = context.getPackageName();
        //默认launcher入口
        //判断指定的launcher是否存在
        if (hasApkInstalled(context, packageName)) {
            //清除当前默认launcher
            ArrayList<IntentFilter> intentList = new ArrayList<>();
            ArrayList<ComponentName> cnList = new ArrayList<>();
            context.getPackageManager().getPreferredActivities(intentList, cnList, null);
            IntentFilter intentFilter;
            for (int i = 0; i < cnList.size(); i++) {
                intentFilter = intentList.get(i);
                if (intentFilter.hasAction(Intent.ACTION_MAIN) && intentFilter.hasCategory(Intent.CATEGORY_HOME)) {
                    context.getPackageManager().clearPackagePreferredActivities(cnList.get(i).getPackageName());
                }
            }
            //获取所有launcher activity
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> list;
            list = pm.queryIntentActivities(intent, 0);

            // get all components and the best match
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MAIN);
            filter.addCategory(Intent.CATEGORY_HOME);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            final int size = list.size();
            //设置默认launcher
            ComponentName launcher = new ComponentName(packageName, NES_LAUNCHER_TV_HOME_CLASS_NAME);
            ComponentName[] set = new ComponentName[size];
            int defaultMatch = 0;
            for (int i = 0; i < size; i++) {
                ResolveInfo r = list.get(i);
                set[i] = new ComponentName(r.activityInfo.packageName, r.activityInfo.name);
                if (launcher.getClassName().equals(r.activityInfo.name)) {
                    defaultMatch = r.match;
                }
            }
            pm.addPreferredActivity(filter, defaultMatch, set, launcher);
            LogX.d(TAG, "setDefaultLauncher ======= end");
        }
    }
    private static boolean hasApkInstalled(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)){
            return false;
        }
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static void silentDelete(Context context , String packageName) {
        Log.d(TAG, "silentDelete ======= start ");
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            if (packageInfo == null) {
                return;
            }
            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "silentDelete ======= error " + e.getMessage());
        }
        LogX.d(TAG, "silentDelete ======= end ");
    }

}
