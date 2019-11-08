package com.nes.utils;

import android.util.Log;

import java.util.HashMap;

import io.reactivex.disposables.Disposable;

public class BaseRxManger {
    final static String TAG = BaseRxManger.class.getSimpleName();

    HashMap<String, Disposable> mDisposableHashMap = new HashMap<>();

    /**
     * 入栈
     *
     * @param key        推荐使用方法名
     * @param disposable
     */
    public void add(String key, Disposable disposable) {
        if (!mDisposableHashMap.containsKey(key))
            mDisposableHashMap.put(key, disposable);
    }

    /**
     * 关闭一个请求
     *
     * @param key
     */
    public void clear(String key) {
        if (mDisposableHashMap != null) {
            Disposable disposable = mDisposableHashMap.get(key);
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
                mDisposableHashMap.remove(key);
            }
        }
    }

    /**
     * 关闭所有请求
     */
    public void clear() {
        Log.i(TAG,TAG + "clear" + mDisposableHashMap.size());
        for (Disposable disposable : mDisposableHashMap.values()) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }

        mDisposableHashMap.clear();
    }


    /**
     * 关闭一个请求
     *
     * @param key
     */
    public void remove(String key) {
        if (mDisposableHashMap != null) {
            Disposable disposable = mDisposableHashMap.get(key);
            if (disposable != null) {
                mDisposableHashMap.remove(key);
            }
        }
    }

    public boolean hasRequset(String key) {
        if (mDisposableHashMap != null) {
            Disposable disposable = mDisposableHashMap.get(key);
            if (disposable != null && !disposable.isDisposed()) {
                return true;
            }
        }
        return false;
    }
}
