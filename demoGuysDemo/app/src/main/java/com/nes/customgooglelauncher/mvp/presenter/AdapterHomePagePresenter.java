package com.nes.customgooglelauncher.mvp.presenter;

import android.content.pm.ResolveInfo;

import androidx.tvprovider.media.tv.PreviewProgram;

import com.nes.customgooglelauncher.bean.AppBean;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.mvp.model.AppsFragmentModelImpl;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.customgooglelauncher.utils.ChannelProgramUtils;
import com.nes.customgooglelauncher.utils.Constants;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 嵌套RecyclerView --> 子RecyclerView load的异步方法
 * @author liuqz
 */
public class AdapterHomePagePresenter {
    private String mTag = AdapterHomePagePresenter.class.getSimpleName();

    private AppsFragmentModelImpl mAppsFragmentModel;
    public AdapterHomePagePresenter(){
        mAppsFragmentModel = new AppsFragmentModelImpl();
    }

    public interface LoadListener<T> {
        public void onLoadResult(int code, List<T> list, Object o);
    }

    public void loadThirdRecommend(final String pkgName, final LoadListener<PreviewProgram> listener){
        Observable.create(new ObservableOnSubscribe<List<PreviewProgram>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<PreviewProgram>> emitter) throws Exception {

                List<ChannelBean> list = ChannelProgramUtils.getInstance().getProgramList(ChannelProgramUtils.getInstance().getChannelByPkgName(pkgName));
                ChannelBean bean = new ChannelBean();
                if (!list.isEmpty()) {
                    bean = list.get(0);
                }
                emitter.onNext(bean.getPrograms());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<PreviewProgram>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<PreviewProgram> list) {
                callBackList(listener,list,"loadThirdRecommend");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                LogX.d(mTag + " loadThirdRecommend onError e : " + e.getMessage());
                callBackErrorList(listener);
            }

            @Override
            public void onComplete() {
                LogX.d(mTag + " loadThirdRecommend onComplete");
            }
        });
    }

    public void loadOtherRecommend(final long channelId, final LoadListener<PreviewProgram> listener){
        Observable.create(new ObservableOnSubscribe<List<PreviewProgram>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<PreviewProgram>> emitter) throws Exception {
                List<PreviewProgram> list = ChannelProgramUtils.getInstance().getProgramList(channelId);
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<PreviewProgram>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<PreviewProgram> list) {
                callBackList(listener,list,"loadOtherRecommend channelId : "+channelId);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                LogX.d(mTag + " loadOtherRecommend onError e : " + e.getMessage());
                callBackErrorList(listener);
            }

            @Override
            public void onComplete() {
                LogX.d(mTag + " loadOtherRecommend onComplete");
            }
        });
    }



    public void loadAllAppOrGame(final HomeType homeType, final LoadListener<ResolveInfo> listener){
        mAppsFragmentModel.getAppAndGameObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<List<AppBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<AppBean> appBeans) {
                        if (Util.isNoEmptyList(appBeans)){
                            int currentFlag = homeType == HomeType.ALL_APP ? Constants.FLAG_APP : Constants.FLAG_GAME;
                            for (AppBean appBean : appBeans){
                                if (appBean != null && appBean.getFlag() == currentFlag){
                                    List<ResolveInfo> list = appBean.getApps();
                                    callBackList(listener,list,"loadAllAppOrGame "+homeType.toString());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBackErrorList(listener);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private <T> void callBackList(LoadListener<T> listener,List<T> list,String mothName){
        boolean isNoNull = Util.isNoEmptyList(list);
        LogX.d(mTag +" "+ mothName+" onNext list : " + (isNoNull ? String.valueOf(list.size()) : "null"));
        if (isNoNull) {
            if (listener != null) {
                listener.onLoadResult(0, list, 0);
            }
        } else {
            if (listener != null) {
                listener.onLoadResult(-1, null, 0);
            }
        }
    }

    private <T> void callBackErrorList(LoadListener<T> listener){
        if (listener != null) {
            listener.onLoadResult(-1, null, 0);
        }
    }

}
