package com.nes.base.mvp;

import android.os.Bundle;

/**
 * 定义P层生命周期与V层同步
 * @author liuqz
 */

public interface MvpPresenter<V extends MvpView> {
    void onMvpAttachView(V view, Bundle savedInstanceState);

    void onMvpStart();

    void onMvpResume();

    void onMvpPause();

    void onMvpStop();

    void onMvpSaveInstanceState(Bundle savedInstanceState);

    void onMvpDetachView(boolean retainInstance);

    void onMvpDestroy();
}
