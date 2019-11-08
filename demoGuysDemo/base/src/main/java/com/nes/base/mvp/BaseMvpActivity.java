package com.nes.base.mvp;

import android.os.Bundle;

import com.nes.base.BaseActivity;

/**
 *
 * @author liuqz
 * @date 2018/11/21
 */

public abstract class BaseMvpActivity<P extends MvpPresenter> extends BaseActivity implements MvpView {

    protected P presenter;

    protected abstract P createPresenter();

    protected abstract void initMvp(Bundle savedInstanceState);

    @Override
    protected void init(Bundle savedInstanceState) {
        presenter = createPresenter();
        if (presenter != null){
            presenter.onMvpAttachView(this, savedInstanceState);
        }
        initMvp(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.onMvpStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.onMvpResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.onMvpPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.onMvpStop();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (presenter != null) {
            presenter.onMvpSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onMvpDetachView(false);
            presenter.onMvpDestroy();
        }
    }
}
