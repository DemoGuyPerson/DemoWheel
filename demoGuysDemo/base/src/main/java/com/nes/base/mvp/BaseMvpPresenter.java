package com.nes.base.mvp;

import android.content.Context;
import android.os.Bundle;


import com.nes.utils.BaseRxManger;

import java.lang.ref.WeakReference;

/**
 * Presenter生命周期包装、View的绑定和解除，P层实现的基类
 * @author liuqz
 */

public class BaseMvpPresenter<V extends MvpView> implements MvpFragmentPresenter<V>{

    protected String TAG = this.getClass().getSimpleName();
    private WeakReference<V> mReference;
    protected BaseRxManger mRxManger = new BaseRxManger();

    protected V getView(){
        return mReference.get();
    }

    protected boolean isViewAttached() {
        return mReference != null && getView() != null;
    }

    private void attach(V view, Bundle savedInstanceState) {
        mReference = new WeakReference<V>(view);
    }

    @Override
    public void onMvpAttachView(V view, Bundle savedInstanceState) {
        attach(view,savedInstanceState);
    }

    @Override
    public void onMvpStart() {

    }

    @Override
    public void onMvpResume() {

    }

    @Override
    public void onMvpPause() {

    }

    @Override
    public void onMvpStop() {

    }

    @Override
    public void onMvpSaveInstanceState(Bundle savedInstanceState) {

    }
    private void detach(boolean retainInstance) {
        if (mReference != null) {
            clearAllRx();
            mReference.clear();
            mReference = null;
        }
    }

    @Override
    public void onMvpDetachView(boolean retainInstance) {
        detach(retainInstance);
    }

    @Override
    public void onMvpDestroy() {

    }

    @Override
    public void onMvpFragmentAttach(Context context) {

    }

    @Override
    public void onMvpFragmentActivityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void onMvpFragmentDestroyView() {

    }

    @Override
    public void onMvpFragmentDetach() {

    }

    @Override
    public void onMvpFragmentHiddenChanged(boolean hidden) {

    }


    public void clearAllRx() {
        mRxManger.clear();
    }


    public void clearRx(String key) {
        mRxManger.clear(key);
    }
}
