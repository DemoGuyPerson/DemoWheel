package com.nes.customgooglelauncher.mvp.presenter;


import android.text.TextUtils;

import com.nes.base.mvp.BaseMvpPresenter;
import com.nes.customgooglelauncher.bean.AppBean;
import com.nes.customgooglelauncher.broadcast.BroadcastCallback;
import com.nes.customgooglelauncher.mvp.contract.AppsFragmentContract;
import com.nes.customgooglelauncher.mvp.model.AppsFragmentModelImpl;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * {@link com.nes.customgooglelauncher.ui.fragment.MyAppsFragment} MVP presenter Impl
 * @author liuqz
 */
public class AppsFragmentPresenterImpl extends BaseMvpPresenter<AppsFragmentContract.IAppsFragmentView>
        implements AppsFragmentContract.IAppsFragmentPresenter {

    private AppsFragmentModelImpl mModel;

    public AppsFragmentPresenterImpl(){
        mModel = new AppsFragmentModelImpl();
    }

    @Override
    public void addAppBroadcast() {
        if (isViewAttached()){
            mModel.addAppBroadcastCallBack(new BroadcastCallback() {
                @Override
                public void installApk(String packName) {
                   if (isViewAttached() && !TextUtils.isEmpty(packName)){
                       getView().installApk(packName);
                   }
                }

                @Override
                public void unInstallApk(String packName) {
                    if (isViewAttached() && !TextUtils.isEmpty(packName)){
                        getView().unInstallApk(packName);
                    }
                }
            });
        }
    }

    @Override
    public void onMvpDestroy() {
        super.onMvpDestroy();
        if (mModel != null){
            mModel.reMoveAppBroadcastCallBack();
        }
    }
}
