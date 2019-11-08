package com.nes.customgooglelauncher.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.PopBean;
import com.nes.customgooglelauncher.bean.notifications.NotificationsContract;
import com.nes.utils.LogX;

import java.util.List;

import static android.app.ActivityThread.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.nes.customgooglelauncher.utils.Constants.ZEE5_PACKAGENAME;

/**
 * @author Lund
 */

public class AppUtils {

    private static final String SCHEME = "package";

    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */

    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
     */

    private static final String APP_PKG_NAME_22 = "pkg";

    /**
     * InstalledAppDetails所在包名
     */

    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

    /**
     * InstalledAppDetails类名
     */

    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";


    private static AppUtils instance;

    public AppUtils() {

    }

    public static AppUtils getInstance() {
        if (instance == null) {
            synchronized (AppUtils.class) {
                if (instance == null) {
                    instance = new AppUtils();
                }
            }
        }
        return instance;
    }


    public void launchApp(Context mContext, String packageName) {
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = null;
        try {
            intent = packageManager.getLeanbackLaunchIntentForPackage(packageName);
        } catch (Exception e) {
            e.printStackTrace();
            intent = packageManager.getLaunchIntentForPackage(packageName);
        }
        checkZEE5(intent);

        if (intent == null) {
            launchAppDetail(mContext, packageName);
        } else {
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            mContext.startActivity(intent);
        }
    }


    public void openSettings(Context context) {
        if (context != null){
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }
    }

    public void openNotification(Context context) {
        if (context != null){
            Intent intent = new Intent(NotificationsContract.ACTION_OPEN_NOTIFICATION_PANEL);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }
    }

    public void openWifiSettings(Context context) {
        if (context != null){
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }
    }

    private ResolveInfo getSettingInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(
                new Intent(Intent.ACTION_MAIN)
                        .addCategory(Constants.CATEGORY_LEANBACK_SETTINGS),
                PackageManager.GET_RESOLVED_FILTER);

        List<ResolveInfo> networkInfos =
                packageManager.queryIntentActivities(new Intent(Settings.ACTION_WIFI_SETTINGS)
                        .addCategory(Constants.CATEGORY_LEANBACK_SETTINGS), 0);


        if (resolveInfos != null) {
            for (ResolveInfo resolveInfo : resolveInfos) {
                if (resolveInfo.activityInfo == null) {
                    continue;
                }

                for (ResolveInfo netInfo : networkInfos) {
                    if (netInfo.activityInfo == null) {
                        continue;
                    }
                    if (!TextUtils.equals(netInfo.activityInfo.name, resolveInfo.activityInfo.name)
                            || !TextUtils.equals(netInfo.activityInfo.packageName, resolveInfo.activityInfo.packageName)) {
                        return resolveInfo;
                    }
                }

            }
        }
        return null;

    }


    public static void launchAppDetail(Context context, String appPkg) {
        final String GOOGLE_PLAY = "com.android.vending";
        try {
            if (TextUtils.isEmpty(appPkg)){
                return;
            }
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(GOOGLE_PLAY);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }


    private static final void checkZEE5(Intent intent) {
        if (null != intent) {
            String pakcageName = intent.getPackage();
            if (!TextUtils.isEmpty(pakcageName) && ZEE5_PACKAGENAME.equals(pakcageName)) {
                intent.putExtra("cmp_launcher", "Hathway_Launcher");
                LogX.i("putExtra: cmp_launcher, Hathway_Launcher");
                return;
            }
            ComponentName componentName = intent.getComponent();
            if (null != componentName && ZEE5_PACKAGENAME.equals(componentName.getPackageName())) {
                intent.putExtra("cmp_launcher", "Hathway_Launcher");
                LogX.i("putExtra: cmp_launcher, Hathway_Launcher");
                return;
            }
        }
    }


    public void openAssistant(Context context,int type) {
        final Intent intent = new Intent(Intent.ACTION_ASSIST)
                .addFlags(FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra("search_type", type);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogX.e(TAG, "Exception launching intent " + intent);
        }
    }

    public void launchApp(Context mContext, ResolveInfo resolveInfo) {
        try {
            if (resolveInfo != null) {
                Intent intent = new Intent();
                intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                checkZEE5(intent);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void launchAppDetails(Context context, String packageName) {

        try {
            Intent intent = new Intent();

            final int apiLevel = Build.VERSION.SDK_INT;

            if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口

                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

//                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);

                Uri uri = Uri.fromParts(SCHEME, packageName, null);

                intent.setData(uri);

            } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。

                final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);

                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);

                intent.putExtra(appPkgName, packageName);

            }

            context.startActivity(intent);
        } catch (Exception e) {
           e.printStackTrace();
           LogX.e(TAG+" launchAppDetails error : "+e.getMessage());
        }

    }


    public void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);

    }


    public void control(Context context,ResolveInfo info, PopBean bean) {
        LogX.d(TAG+" control start... ");
        if (info == null){
            return;
        }
        if (bean.getName().equals(context.getString(R.string.app_opera_open))) {
            launchApp(context, info);
        } else if (bean.getName().equals(context.getString(R.string.app_opera_info))) {
            LogX.d(TAG+" control info pkgname : "+info.activityInfo.packageName);
            launchAppDetails(context, info.activityInfo.packageName);
        } else if (bean.getName().equals(context.getString(R.string.app_opera_uninstall))) {
            uninstallApk(context, info.activityInfo.packageName);
        }

    }

    public String getAppName(Context context,String packageName) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo info = null;
        try {
            info = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null != info ? info.loadLabel(packageManager).toString() : "";
    }


    //获取版本号
    public static int getVersionCode(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }catch(PackageManager.NameNotFoundException e){
            return 0;
        }
    }
}
