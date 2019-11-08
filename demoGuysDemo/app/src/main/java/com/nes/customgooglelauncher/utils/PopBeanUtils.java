package com.nes.customgooglelauncher.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;


import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.PopBean;
import com.nes.utils.LogX;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author liuqz
 */
public class PopBeanUtils {

    private static PopBeanUtils sPopBeanUtils;

    public static PopBeanUtils get() {
        if (sPopBeanUtils == null) {
            synchronized (PopBeanUtils.class) {
                if (sPopBeanUtils == null) {
                    sPopBeanUtils = new PopBeanUtils();
                }
            }
        }
        return sPopBeanUtils;
    }

    public List<PopBean> getAppType(ResolveInfo info) {
        List<PopBean> list = new ArrayList<>();
        PopBean bean = new PopBean();
        bean.setRes(R.drawable.selector_pop_open);
        bean.setName(MyApplication.getInstance().getString(R.string.app_opera_open));
        list.add(bean);
        bean = new PopBean();
        bean.setName(MyApplication.getInstance().getString(R.string.app_opera_info));
        bean.setRes(R.drawable.selector_pop_info);
        list.add(bean);
        bean = new PopBean();
        bean.setName(MyApplication.getInstance().getString(R.string.app_opera_uninstall));
        PackageManager packageManager = MyApplication.getInstance().getPackageManager();
        try {
            ApplicationInfo ainfo = packageManager.getApplicationInfo(info.activityInfo.packageName, 0);

            if ((ainfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                LogX.e("app:" + info.activityInfo.name + " is system app");
                bean.setRes(R.drawable.ic_uninstall_2);
                bean.setEnable(false);
            } else {
                LogX.e("app:" + info.activityInfo.name + " not system app");
                bean.setRes(R.drawable.selector_pop_uninstall);
                bean.setEnable(true);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        list.add(bean);
        return list;
    }

    public List<PopBean> getChannelType() {

        List<PopBean> list = new ArrayList<>();
        PopBean bean;
        if (!Constants.HIDE_PLAY_NEXT) {
            bean = new PopBean();
            bean.setName(MyApplication.getInstance().getString(R.string.channel_opera_add_playNext));
            bean.setRes(R.drawable.selector_pop_play_next);
            list.add(bean);
        }
        bean = new PopBean();
        bean.setRes(R.drawable.selector_pop_uninstall);
        bean.setName(MyApplication.getInstance().getString(R.string.channel_opera_remove));
        list.add(bean);
        return list;
    }

}
