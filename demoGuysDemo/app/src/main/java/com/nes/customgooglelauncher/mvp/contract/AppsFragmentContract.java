package com.nes.customgooglelauncher.mvp.contract;

import com.nes.base.mvp.MvpFragmentPresenter;
import com.nes.base.mvp.MvpModel;
import com.nes.base.mvp.MvpView;
import com.nes.customgooglelauncher.bean.AppBean;
import com.nes.customgooglelauncher.broadcast.BroadcastCallback;
import com.nes.customgooglelauncher.ui.fragment.MyAppsFragment;

import java.util.List;

import io.reactivex.Observable;

/**
 * {@link MyAppsFragment}的MVP 契约类
 * @author liuqz
 */
public class AppsFragmentContract {

    public interface IAppsFragmentView extends MvpView {
        /**
         * 通知界面有 pkgname的应用被安装
         * @param pkgName 包名
         */
        void installApk(String pkgName);
        /**
         * 通知界面有 pkgname的应用被卸载
         * @param pkgName 包名
         */
        void unInstallApk(String pkgName);
    }

    public interface IAppsFragmentPresenter extends MvpFragmentPresenter<IAppsFragmentView> {
        /**
         * 添加app卸载、安装广播监听
         */
        void addAppBroadcast();
    }

    public interface IAppsFragmentModel extends MvpModel {
        /**
         * 获取 AppAndGame Observable
         * @return {@link Observable}
         */
        Observable<List<AppBean>> getAppAndGameObservable();

        /**
         * 添加app卸载、安装广播CallBack
         * @param callback {@link BroadcastCallback}
         */
        void addAppBroadcastCallBack(BroadcastCallback callback);

        /**
         * 移除app卸载、安装广播CallBack
         */
        void reMoveAppBroadcastCallBack();
    }

}
