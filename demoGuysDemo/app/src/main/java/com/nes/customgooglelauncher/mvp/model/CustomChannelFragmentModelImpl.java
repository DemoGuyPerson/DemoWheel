package com.nes.customgooglelauncher.mvp.model;



import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.bean.CustomChannelBean;
import com.nes.customgooglelauncher.mvp.contract.CustomChannelFragmentContract;
import com.nes.customgooglelauncher.utils.ChannelProgramUtils;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * {@link com.nes.customgooglelauncher.ui.activity.CustomChannelsActivity} MVP model Impl
 * @author liuqz
 */
public class CustomChannelFragmentModelImpl implements CustomChannelFragmentContract.ICustomChannelFragmentModel {

    private String TAG = CustomChannelFragmentModelImpl.class.getSimpleName();

    @Override
    public Observable<List<CustomChannelBean>> getAllCustomChannelObservable() {
        return Observable.create(new ObservableOnSubscribe<List<CustomChannelBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<CustomChannelBean>> emitter) throws Exception {
                List<ChannelBean> list = ChannelProgramUtils.getInstance().getProgramList(ChannelProgramUtils.getInstance().getAllChannel());
                List<CustomChannelBean> result = new ArrayList<>();
                if (Util.isNoEmptyList(list)){
                    LogX.d(TAG+" List<ChannelBean> : "+list.size());
                    for (ChannelBean channel : list){
                        LogX.d(TAG+" id : "+channel.getChannel().getId()+" name : "+channel.getChannel().getDisplayName());
                        CustomChannelBean bean = new CustomChannelBean(new ArrayList<ChannelBean>(),channel);
                        int position = result.indexOf(bean);
                        LogX.d(TAG+" indexOf : "+position);
                        if (position != -1) {
                            bean = result.get(position);
                            bean.getChannelBeans().add(channel);
                        } else {
                            bean.getChannelBeans().add(channel);
                            result.add(bean);
                        }
                    }
                }
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
