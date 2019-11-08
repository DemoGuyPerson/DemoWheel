package com.nes.customgooglelauncher.mvp.model;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.bean.notifications.TvNotification;
import com.nes.customgooglelauncher.mvp.contract.MainActivityContract;
import com.nes.customgooglelauncher.utils.NotificationUtil;
import com.nes.utils.LogX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.nes.customgooglelauncher.utils.NotificationUtil.COLUMN_COUNT;

/**
 * {@link com.nes.customgooglelauncher.ui.activity.MainActivity} MVP model Impl
 * @author liuqz
 */
public class MainActivityModelImpl implements MainActivityContract.IMainActivityModel {

    private MainActivityContract.NotificationCallback mCallback;
    private Cursor mNotifsCountCursor = null;
    private Cursor mNotificationsCursor = null;
    private LoaderManager mLoaderManager;
    private final int NOTIFS_COUNT_NUM = 2;
    private final int NOTIFS_COUNT_LIST = 3;

    public MainActivityModelImpl(){

    }

    @Override
    public void initLoader(LoaderManager loaderManager, MainActivityContract.NotificationCallback callback) {
        mCallback = callback;
        mLoaderManager = loaderManager;
        if (loaderManager != null) {
            loaderManager.initLoader(NOTIFS_COUNT_NUM, null, new NotifsCountLoaderCallbacks());
            loaderManager.initLoader(NOTIFS_COUNT_LIST, null, new NotificationsCountLoaderCallbacks());
        }
    }

    @Override
    public void destroyLoader() {
        if (mLoaderManager != null){
            mLoaderManager.destroyLoader(NOTIFS_COUNT_NUM);
            mLoaderManager.destroyLoader(NOTIFS_COUNT_LIST);
        }
    }

    private class NotifsCountLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MyApplication.getInstance(), NotificationUtil.NOTIFS_COUNT_URI,
                    null, null, null, null);
        }

        @Override
        public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
            mNotifsCountCursor = data;
            loadNotification();
        }

        @Override
        public void onLoaderReset(android.content.Loader<Cursor> loader) {

        }
    }

    private class NotificationsCountLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MyApplication.getInstance(), NotificationUtil.CONTENT_URI,
                    TvNotification.PROJECTION, null, null, null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            mNotificationsCursor = data;
            LogX.i("onLoadFinished() called with: " + "newCursor = [" + DatabaseUtils.dumpCursorToString(data) + "]");
            refreshImportantNotification();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }

    }

    private void loadNotification() {
        int num = getNotifsCount();
        LogX.i("loadNotification num : "+num);
        if (mCallback != null){
            mCallback.currentNum(num);
        }
    }

    private int getNotifsCount() {
        if (mNotifsCountCursor != null && mNotifsCountCursor.moveToFirst()) {
            mNotifsCountCursor.moveToFirst();
            int index = mNotifsCountCursor.getColumnIndex(COLUMN_COUNT);
            return mNotifsCountCursor.getInt(index);
        }
        return 0;
    }

    private void refreshImportantNotification() {
        final List<TvNotification> tvNotificationList = getNotifications();
        LogX.i("refreshNotification size()" + tvNotificationList.size());
        if (mCallback != null){
            mCallback.currentListTvNotification(tvNotificationList);
        }
    }

    private List<TvNotification> getNotifications() {
        if (null != mNotificationsCursor && mNotificationsCursor.moveToFirst()) {
            List<TvNotification> tvNotificationList = new ArrayList<>();
            TvNotification tvNotification = TvNotification.fromCursor(mNotificationsCursor);
            if (tvNotification.getChannel() >= 5) {
                tvNotificationList.add(tvNotification);
            }
            while (mNotificationsCursor.moveToNext()) {
                tvNotification = TvNotification.fromCursor(mNotificationsCursor);
                if (tvNotification.getChannel() >= 5) {
                    tvNotificationList.add(tvNotification);
                }
            }
            return tvNotificationList;
        }
        return Collections.emptyList();
    }
}
