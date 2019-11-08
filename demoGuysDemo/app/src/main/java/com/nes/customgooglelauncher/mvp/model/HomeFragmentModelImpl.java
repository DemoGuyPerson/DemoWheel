package com.nes.customgooglelauncher.mvp.model;

import android.os.Handler;
import android.os.Looper;

import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;

import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.broadcast.AppBroadcastReceiver;
import com.nes.customgooglelauncher.broadcast.BroadcastCallback;
import com.nes.customgooglelauncher.mvp.contract.HomeFragmentContract;
import com.nes.customgooglelauncher.utils.ChannelProgramUtils;
import com.nes.customgooglelauncher.utils.MonitorDBHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * {@link com.nes.customgooglelauncher.ui.fragment.HomeFragment} MVP model Impl
 * @author liuqz
 */
public class HomeFragmentModelImpl implements HomeFragmentContract.IHomeFragmentModel, HomeFragmentContract.NotifyRecyclerViewCallBack {

    private MonitorDBHelper mMonitorDBHelper;
    private Handler mMainHandler;
    private HomeFragmentContract.NotifyRecyclerViewCallBack mCallBack;
    private BroadcastCallback mBroadcastCallback;

    public HomeFragmentModelImpl(){
        mMainHandler = new Handler(Looper.getMainLooper());
        mMonitorDBHelper = new MonitorDBHelper();
        mMonitorDBHelper.setCallBack(this);
    }

    @Override
    public Observable<List<ChannelBean>> getAllShowChannelObservable() {
        return Observable.create(new ObservableOnSubscribe<List<ChannelBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<ChannelBean>> emitter) throws Exception {
                List<ChannelBean> list = ChannelProgramUtils.getInstance().getProgramList(ChannelProgramUtils.getInstance().getAllShowChannel());
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void monitordb(HomeFragmentContract.NotifyRecyclerViewCallBack callBack) {
        mCallBack = callBack;
        mMonitorDBHelper.monitorChannelDB();
        mMonitorDBHelper.monitorProgramDB();
    }

    @Override
    public void addAppBroadcastCallBack(BroadcastCallback callback) {
        mBroadcastCallback = callback;
        AppBroadcastReceiver.getInstance().addBroadcastCallback(callback);
    }

    @Override
    public void reMoveAppBroadcastCallBack() {
        if (mBroadcastCallback != null){
            AppBroadcastReceiver.getInstance().removeBroadcastCallback(mBroadcastCallback);
        }
    }


    public void destroy(){
        if (mMonitorDBHelper != null){
            mMonitorDBHelper.destroy();
        }
    }


    @Override
    public void notifyAllData() {
        if (mMainHandler != null){
            mMainHandler.post(new MainRunnable());
        }
    }

    @Override
    public void notifyChannelOne(int action, ChannelBean channelBean) {
        if (mMainHandler != null){
            mMainHandler.post(new MainRunnable(action,channelBean));
        }
    }

    @Override
    public void notifyProgram(int action, Channel channel, PreviewProgram previewProgram,long programId) {
        if (mMainHandler != null){
            mMainHandler.post(new MainRunnable(action,channel,previewProgram,programId));
        }
    }

    @Override
    public void notifyChannels(List<ChannelBean> channelBeans) {
        if (mMainHandler != null){
            mMainHandler.post(new MainRunnable(channelBeans));
        }
    }

    @Override
    public int getActionByProgram(PreviewProgram previewProgram) {
        return mCallBack != null ? mCallBack.getActionByProgram(previewProgram) : com.nes.customgooglelauncher.utils.Constants.TYPE_NOTIFY_DEFULT;
    }


    class MainRunnable implements Runnable{
        static final int TYPE_ALL_DATA = 1;
        static final int TYPE_ONR_CHANNEL = 2;
        static final int TYPE_PROGRAM = 3;
        static final int TYPE_CHANNELS = 4;

        int mType;
        int mAction;
        ChannelBean mChannelBean;
        Channel mChannel;
        PreviewProgram mPreviewProgram;
        List<ChannelBean> mChannelBeans;
        long mProgramId;


        MainRunnable(int type, int action, ChannelBean channelBean, Channel channel, PreviewProgram previewProgram, List<ChannelBean> channelBeans,long programId) {
            mType = type;
            mAction = action;
            mChannelBean = channelBean;
            mChannel = channel;
            mPreviewProgram = previewProgram;
            mChannelBeans = channelBeans;
            mProgramId = programId;
        }
        MainRunnable(){
            this(TYPE_ALL_DATA,-1,null,null,null,null,-1);
        }
        MainRunnable(int action,ChannelBean channelBean){
            this(TYPE_ONR_CHANNEL,action,channelBean,null,null,null,-1);
        }
        MainRunnable(int action,Channel channel,PreviewProgram previewProgram,long programId){
            this(TYPE_PROGRAM,action,null,channel,previewProgram,null,programId);
        }
        MainRunnable(List<ChannelBean> channelBeans){
            this(TYPE_CHANNELS,-1,null,null,null,channelBeans,-1);
        }

        MainRunnable(int type){
            this(type,-1,null,null,null,null,-1);
        }


        @Override
        public void run() {
            switch (mType){
                case TYPE_ALL_DATA:
                    if (mCallBack != null){
                        mCallBack.notifyAllData();
                    }
                    break;
                case TYPE_ONR_CHANNEL:
                    if (mCallBack != null){
                        mCallBack.notifyChannelOne(mAction,mChannelBean);
                    }
                    break;
                case TYPE_PROGRAM:
                    if (mCallBack != null){
                        mCallBack.notifyProgram(mAction,mChannel,mPreviewProgram,mProgramId);
                    }
                    break;
                case TYPE_CHANNELS:
                    if (mCallBack != null){
                        mCallBack.notifyChannels(mChannelBeans);
                    }
                    break;
                default:

                    break;
            }
        }
    }
}
