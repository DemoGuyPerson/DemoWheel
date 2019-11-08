package com.nes.customgooglelauncher;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.nes.imageloader.cache.DoubleCache;
import com.nes.imageloader.config.ImageLoaderConfig;
import com.nes.imageloader.core.SimpleImageLoader;
import io.fabric.sdk.android.Fabric;

/**
 * Application
 * @author liuqz
 */
public class MyApplication extends Application {

    private static MyApplication sMyApplication;

    public static MyApplication getInstance(){
        return sMyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        sMyApplication = this;
        initImageLoader();
    }

    private void initImageLoader() {
        ImageLoaderConfig config = new ImageLoaderConfig();
        config.setCache(new DoubleCache(sMyApplication));
        SimpleImageLoader.getInstance().init(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
