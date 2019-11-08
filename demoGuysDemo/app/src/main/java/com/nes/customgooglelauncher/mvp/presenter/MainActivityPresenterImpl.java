package com.nes.customgooglelauncher.mvp.presenter;

import com.nes.base.mvp.BaseMvpPresenter;
import com.nes.customgooglelauncher.bean.notifications.TvNotification;
import com.nes.customgooglelauncher.mvp.contract.MainActivityContract;
import com.nes.customgooglelauncher.mvp.model.MainActivityModelImpl;

import java.util.List;

/**
 * {@link com.nes.customgooglelauncher.ui.activity.MainActivity} MVP presenter Impl
 * @author liuqz
 */
public class MainActivityPresenterImpl extends BaseMvpPresenter<MainActivityContract.IMainActivityView> implements MainActivityContract.IMainActivityPresenter {

    private MainActivityModelImpl mModel;

    public MainActivityPresenterImpl(){
        mModel = new MainActivityModelImpl();
    }

    @Override
    public void registerNotification() {
        if (isViewAttached()){
            mModel.initLoader(getView().getActivityLoaderManager(),new MainActivityContract.NotificationCallback() {
                @Override
                public void currentNum(int num) {
                    if (isViewAttached()){
                        getView().showNotifyNum(num);
                    }
                }

                @Override
                public void currentListTvNotification(List<TvNotification> list) {
                    if (isViewAttached()){
                        getView().showNotifyList(list);
                    }
                }
            });
        }
    }

    @Override
    public void onMvpDestroy() {
        super.onMvpDestroy();
        if (mModel != null){
            mModel.destroyLoader();
        }
    }
}
