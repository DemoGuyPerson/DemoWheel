package com.nes.customgooglelauncher.mvp.presenter;

import android.text.TextUtils;

import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;

import com.nes.base.mvp.BaseMvpPresenter;
import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.broadcast.BroadcastCallback;
import com.nes.customgooglelauncher.mvp.contract.HomeFragmentContract;
import com.nes.customgooglelauncher.mvp.model.HomeFragmentModelImpl;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.presenter.ThirdRecommendAdapterPresenter;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.customgooglelauncher.utils.Constants;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * {@link com.nes.customgooglelauncher.ui.fragment.HomeFragment} MVP presenter Impl
 * @author liuqz
 */
public class HomeFragmentPresenterImpl extends BaseMvpPresenter<HomeFragmentContract.IHomeFragmentView>
        implements HomeFragmentContract.IHomeFragmentPresenter {

    private HomeFragmentModelImpl mModel;

    public HomeFragmentPresenterImpl(){
        mModel = new HomeFragmentModelImpl();
    }

    @Override
    public void onMvpDestroy() {
        super.onMvpDestroy();
        if (mModel != null){
            mModel.destroy();
            mModel.reMoveAppBroadcastCallBack();
        }
    }


    @Override
    public void loadAllShowChannel() {
        if (!isViewAttached()){
            LogX.e(TAG+" loadAllShowChannel isViewAttached() == false");
            return;
        }
        mModel.getAllShowChannelObservable().subscribe(new Observer<List<ChannelBean>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mRxManger.add("loadAllShowChannel",d);
            }

            @Override
            public void onNext(List<ChannelBean> list) {
                LogX.d(TAG + " loadAllShowChannel onNext list : "+(Util.isNoEmptyList(list) ? String.valueOf(list.size()):" null" ));
                if (!isViewAttached()){
                    return;
                }
                List<AbsItem> data = new ArrayList<>();
                if (Util.isNoEmptyList(list)) {
                    for (ChannelBean channel : list) {
                        String title = Utils.getChannelShowTitle(channel.getChannel());
                        LogX.d(TAG+" loadAllShowChannel title : "+title);
                        AbsItem<PreviewProgram> absItem  = new AbsItem<PreviewProgram>(title,
                                String.valueOf(channel.getChannel().getId()),HomeType.OTHER_RECOMMEND,0,
                                Utils.getDimension(MyApplication.getInstance(),R.dimen.home_item_item_type3_height));
                        absItem.setSourceList(channel.getPrograms());
                        absItem.addPage();
                        absItem.setAdapterPresenter(new ThirdRecommendAdapterPresenter());
                        data.add(absItem);
                    }
                }
                getView().showAllShowChannel(data);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mRxManger.remove("loadAllShowChannel");
                LogX.d(TAG + " loadAllShowChannel onError e : " + e.getMessage());
                if (!isViewAttached()){
                    return;
                }
                getView().showAllShowChannel(null);
            }

            @Override
            public void onComplete() {
                LogX.d(TAG + " loadTrendingNow onComplete");
                mRxManger.remove("loadAllShowChannel");
            }
        });
    }

    @Override
    public void monitordb() {
        if (isViewAttached()){
            mModel.monitordb(new HomeFragmentContract.NotifyRecyclerViewCallBack() {
                @Override
                public void notifyAllData() {
                    if (isViewAttached()){
                        getView().notifyAllData();
                    }
                }

                @Override
                public void notifyChannelOne(int action, ChannelBean channelBean) {
                    if (isViewAttached()){
                        getView().notifyChannelOne(action,channelBean);
                    }
                }

                @Override
                public void notifyProgram(int action, Channel channel, PreviewProgram previewProgram, long programId) {
                    if (isViewAttached()){
                        getView().notifyProgram(action,channel,previewProgram,programId);
                    }
                }

                @Override
                public void notifyChannels(List<ChannelBean> channelBeans) {
                    if (isViewAttached()){
                        getView().notifyChannels(channelBeans);
                    }
                }

                @Override
                public int getActionByProgram(PreviewProgram previewProgram) {
                    return isViewAttached() ? getView().getActionByProgram(previewProgram) : Constants.TYPE_NOTIFY_DEFULT;
                }
            });

        }
    }

    @Override
    public void addAppBroadcast() {
        if (isViewAttached()){
            mModel.addAppBroadcastCallBack(new BroadcastCallback() {
                @Override
                public void unInstallApk(String packName) {
                    super.unInstallApk(packName);
                    if (isViewAttached() && !TextUtils.isEmpty(packName)) {
                        getView().unInstallApk(packName);
                    }
                }
            });
        }
    }


}
