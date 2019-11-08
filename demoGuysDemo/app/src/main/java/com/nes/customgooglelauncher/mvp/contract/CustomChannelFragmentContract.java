package com.nes.customgooglelauncher.mvp.contract;

import com.nes.base.mvp.MvpFragmentPresenter;
import com.nes.base.mvp.MvpModel;
import com.nes.base.mvp.MvpView;
import com.nes.customgooglelauncher.bean.CustomChannelBean;
import com.nes.customgooglelauncher.ui.activity.CustomChannelsActivity;

import java.util.List;

import io.reactivex.Observable;

/**
 * {@link CustomChannelsActivity}的MVP 契约类
 * @author liuqz
 */
public class CustomChannelFragmentContract {

    public interface ICustomChannelFragmentView extends MvpView {
        /**
         * 刷新Channel 列表
         * @param list List<>{@link CustomChannelBean}
         */
        void refreshChannelList(List<CustomChannelBean> list);
    }

    public interface ICustomChannelFragmentPresenter extends MvpFragmentPresenter<CustomChannelFragmentContract.ICustomChannelFragmentView> {
        /**
         * 加载所有Channel信息
         */
        void loadAllCustomChannel();
    }

    public interface ICustomChannelFragmentModel extends MvpModel {
        /**
         * 获取Observable
         * @return {@link Observable}
         */
        Observable<List<CustomChannelBean>> getAllCustomChannelObservable();
    }
}
