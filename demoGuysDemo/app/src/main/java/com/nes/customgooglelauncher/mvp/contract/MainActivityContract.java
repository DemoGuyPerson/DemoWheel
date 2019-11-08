package com.nes.customgooglelauncher.mvp.contract;



import android.app.LoaderManager;

import com.nes.base.mvp.MvpModel;
import com.nes.base.mvp.MvpPresenter;
import com.nes.base.mvp.MvpView;
import com.nes.customgooglelauncher.bean.notifications.TvNotification;
import com.nes.customgooglelauncher.ui.activity.MainActivity;

import java.util.List;

/**
 * {@link MainActivity}的MVP 契约类
 * @author liuqz
 */
public class MainActivityContract {

    public interface IMainActivityView extends MvpView {
        /**
         * 获取{@link LoaderManager}
         * @return {@link LoaderManager}
         */
        LoaderManager getActivityLoaderManager();

        /**
         * 展示通知条数
         * @param num 通知条数
         */
        void showNotifyNum(int num);

        /**
         * 展示通知列表
         * @param list List<>{@link TvNotification}
         */
        void showNotifyList(List<TvNotification> list);
    }

    public interface IMainActivityPresenter extends MvpPresenter<IMainActivityView> {
        /**
         * 注册通知
         */
        void registerNotification();
    }

    public interface IMainActivityModel extends MvpModel {
        /**
         * 初始化{@link LoaderManager}
         * @param loaderManager {@link LoaderManager}
         * @param callback {@link NotificationCallback}
         */
        void initLoader(LoaderManager loaderManager,NotificationCallback callback);

        /**
         * 释放{@link LoaderManager}
         */
        void destroyLoader();
    }

    /**
     * 通知回调CallBack
     */
    public interface NotificationCallback{
        /**
         * 当前通知数
         * @param num 通知数
         */
        void currentNum(int num);

        /**
         * 当前通知内容
         * @param list List<>{@link TvNotification}
         */
        void currentListTvNotification(List<TvNotification> list);
    }

}
