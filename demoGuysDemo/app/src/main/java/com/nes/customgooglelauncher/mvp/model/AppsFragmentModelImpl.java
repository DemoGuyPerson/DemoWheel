package com.nes.customgooglelauncher.mvp.model;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.ArraySet;

import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.bean.AppBean;
import com.nes.customgooglelauncher.broadcast.AppBroadcastReceiver;
import com.nes.customgooglelauncher.broadcast.BroadcastCallback;
import com.nes.customgooglelauncher.mvp.contract.AppsFragmentContract;
import com.nes.customgooglelauncher.utils.Constants;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.LogX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

import static com.nes.customgooglelauncher.utils.Constants.HIDE_APPS_NESUPDATE_PACKAGE_NAME;

/**
 * {@link com.nes.customgooglelauncher.ui.fragment.MyAppsFragment} MVP model Impl
 * @author liuqz
 */
public class AppsFragmentModelImpl implements AppsFragmentContract.IAppsFragmentModel {

    private BroadcastCallback mCallback;

    Observable<List<ResolveInfo>> loadAllAppsAndGames() {
        return Observable.create(new ObservableOnSubscribe<List<ResolveInfo>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<ResolveInfo>> emitter) throws Exception {
                final PackageManager packageManager = MyApplication.getInstance().getPackageManager();
                final List<ResolveInfo> infos = packageManager.queryIntentActivities(
                        new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER),
                        0);
                final Set<ResolveInfo> appItems = new ArraySet<>(infos.size());

                for (ResolveInfo info : infos) {
                    if (info.activityInfo.packageName.equals(Utils.getPackageName(MyApplication.getInstance()))) {
                        continue;
                    }
                    if (info.activityInfo.packageName.equals(HIDE_APPS_NESUPDATE_PACKAGE_NAME)) {
                        continue;
                    }

                    appItems.add(info);
                }

                List<ResolveInfo> list = new ArrayList<>(appItems);
                orderResolveInfoList(list);
                emitter.onNext(list);
            }
        });

    }

    private void orderResolveInfoList(List<ResolveInfo> list) {
        if (null != list && !list.isEmpty()) {
            Collections.sort(list, mResolveInfoComparator);
        }
    }

    Comparator<ResolveInfo> mResolveInfoComparator = new Comparator<ResolveInfo>() {
        private static final int ORDER = 1;
        private static final int ORDER_DESCENDANT = -1;

        private int order = ORDER;

        @Override
        public int compare(ResolveInfo o1, ResolveInfo o2) {
            if (o1 == o2) {
                return 0;
            }
            if (null == o1) {
                return -1 * order;
            }
            if (null == o2) {
                return order;
            }
            PackageManager pm = MyApplication.getInstance().getPackageManager();
            CharSequence packageName1 = o1.activityInfo.loadLabel(pm);
            CharSequence packageName2 = o2.activityInfo.loadLabel(pm);
            if (packageName1 == packageName2) {
                return 0;
            }
            if (TextUtils.isEmpty(packageName1)) {
                return -1 * order;
            }
            if (TextUtils.isEmpty(packageName2)) {
                return order;
            }
            return packageName1.toString().compareTo(packageName2.toString());
        }
    };

    @Override
    public Observable<List<AppBean>> getAppAndGameObservable() {
        return loadAllAppsAndGames().map(new Function<List<ResolveInfo>, List<AppBean>>() {
            @Override
            public List<AppBean> apply(List<ResolveInfo> resolveInfos) throws Exception {

                LogX.i("LoadAppAndGame:" + resolveInfos.size());
                List<AppBean> list = new ArrayList<>();
                AppBean bean = new AppBean(Constants.FLAG_APP, new ArrayList<ResolveInfo>());
                list.add(bean);
                bean = new AppBean(Constants.FLAG_GAME, new ArrayList<ResolveInfo>());
                list.add(bean);
                for (ResolveInfo info : resolveInfos) {
                    LogX.i("LoadAppAndGame  info:" + info.activityInfo.packageName + " isGame:" + (info.activityInfo.applicationInfo.category == ApplicationInfo.CATEGORY_GAME));
                    if (info.activityInfo.applicationInfo.category == ApplicationInfo.CATEGORY_GAME
                            || ((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_IS_GAME) != 0)) {
                        list.get(1).getApps().add(info);
                        continue;
                    }
                    list.get(0).getApps().add(info);

                    if (info.activityInfo.packageName.equals(Constants.PLAYSTORE_PACKAGENAME)) {
                        Constants.PLAYSTORE_INFO = info;
                    }
                    if (info.activityInfo.packageName.equals(Constants.GOOGLEGAME_PACKAGENAME)) {
                        Constants.GOOGLEGAME_INFO = info;
                    }
                }
                return list;
            }
        });
    }

    @Override
    public void addAppBroadcastCallBack(BroadcastCallback callback) {
        mCallback = callback;
        AppBroadcastReceiver.getInstance().addBroadcastCallback(callback);
    }

    @Override
    public void reMoveAppBroadcastCallBack() {
        if (mCallback != null){
            AppBroadcastReceiver.getInstance().removeBroadcastCallback(mCallback);
        }
    }
}
