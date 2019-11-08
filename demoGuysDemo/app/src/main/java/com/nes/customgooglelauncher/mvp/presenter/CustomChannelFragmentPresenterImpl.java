package com.nes.customgooglelauncher.mvp.presenter;


import com.nes.base.mvp.BaseMvpPresenter;
import com.nes.customgooglelauncher.bean.CustomChannelBean;
import com.nes.customgooglelauncher.mvp.contract.CustomChannelFragmentContract;
import com.nes.customgooglelauncher.mvp.model.CustomChannelFragmentModelImpl;
import com.nes.utils.Util;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * {@link com.nes.customgooglelauncher.ui.activity.CustomChannelsActivity} MVP presenter Impl
 * @author liuqz
 */
public class CustomChannelFragmentPresenterImpl extends BaseMvpPresenter<CustomChannelFragmentContract.ICustomChannelFragmentView>
        implements CustomChannelFragmentContract.ICustomChannelFragmentPresenter {

    private CustomChannelFragmentModelImpl mModel;

    public CustomChannelFragmentPresenterImpl(){
        mModel = new CustomChannelFragmentModelImpl();
    }

    @Override
    public void loadAllCustomChannel() {
        if (isViewAttached()){
            mModel.getAllCustomChannelObservable().subscribe((new Observer<List<CustomChannelBean>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    mRxManger.add("loadAllCustomChannel",d);
                }

                @Override
                public void onNext(List<CustomChannelBean> list) {
                    if (isViewAttached()){
                        if (Util.isNoEmptyList(list)){
                            getView().refreshChannelList(list);
                            return;
                        }
                    }
                    getView().refreshChannelList(null);
                }

                @Override
                public void onError(Throwable e) {
                    mRxManger.remove("loadAllCustomChannel");
                    getView().refreshChannelList(null);
                }

                @Override
                public void onComplete() {
                    mRxManger.remove("loadAllCustomChannel");
                }
            }));
        }
    }
}
