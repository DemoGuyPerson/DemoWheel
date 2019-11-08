package com.nes.customgooglelauncher.bean;

import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * app信息类
 * @author liuqz
 */
public class AppBean{

    private int mFlag;
    private List<ResolveInfo> apps;

    public AppBean(int flag, List<ResolveInfo> apps) {
        mFlag = flag;
        this.apps = apps;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }

    public List<ResolveInfo> getApps() {
        return apps;
    }

    public void setApps(List<ResolveInfo> apps) {
        this.apps = apps;
    }
}
