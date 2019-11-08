package com.nes.customgooglelauncher.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用安装卸载广播监听器，全局一个
 * @author liuqz
 */
public class AppBroadcastReceiver extends BroadcastReceiver {
    private String TAG = AppBroadcastReceiver.class.getSimpleName();

    private volatile static AppBroadcastReceiver sAppBroadcastReceiver;

    private List<BroadcastCallback> mCallbacks;

    private AppBroadcastReceiver(){
        mCallbacks = new ArrayList<>();
    }

    public static AppBroadcastReceiver getInstance(){
        if (sAppBroadcastReceiver == null){
            synchronized (AppBroadcastReceiver.class){
                if (sAppBroadcastReceiver == null){
                    sAppBroadcastReceiver = new AppBroadcastReceiver();
                }
            }
        }
        return sAppBroadcastReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null){
            return;
        }
        String action = intent.getAction();
        LogX.i(TAG+" intent action :" + action);
        if (!TextUtils.isEmpty(action)){
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)){
                String pkgName = intent.getData() == null ? "" : intent.getData().getSchemeSpecificPart();
                if (!TextUtils.isEmpty(pkgName) && Util.isNoEmptyList(mCallbacks)){
                    LogX.e(TAG+" action : ACTION_PACKAGE_ADDED"+" pkgName : "+pkgName);
                    for (BroadcastCallback callback : mCallbacks){
                        if (callback != null){
                            callback.installApk(pkgName);
                        }
                    }
                }
            }else if(action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)){
                String pkgName = intent.getData() == null ? "" : intent.getData().getSchemeSpecificPart();
                if (!TextUtils.isEmpty(pkgName) && Util.isNoEmptyList(mCallbacks)){
                    LogX.e(TAG+" action : ACTION_PACKAGE_FULLY_REMOVED"+" pkgName : "+pkgName);
                    for (BroadcastCallback callback : mCallbacks){
                        if (callback != null){
                            callback.unInstallApk(pkgName);
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加广播的被回调者
     * @param callback {@link BroadcastCallback}
     */
    public void addBroadcastCallback(BroadcastCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)){
            mCallbacks.add(callback);
        }
    }

    /**
     * 移除广播的被回调者
     * @param callback {@link BroadcastCallback}
     */
    public void removeBroadcastCallback(BroadcastCallback callback){
        if (mCallbacks != null){
            mCallbacks.remove(callback);
        }
    }

    public void register(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        context.registerReceiver(sAppBroadcastReceiver, intentFilter);
    }

    public void unRegister(Context context) {
        if (context != null){
            context.unregisterReceiver(sAppBroadcastReceiver);
        }
    }
}
