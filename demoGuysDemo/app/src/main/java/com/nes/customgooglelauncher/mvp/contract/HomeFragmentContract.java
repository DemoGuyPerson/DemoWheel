package com.nes.customgooglelauncher.mvp.contract;

import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;

import com.nes.base.mvp.MvpFragmentPresenter;
import com.nes.base.mvp.MvpModel;
import com.nes.base.mvp.MvpView;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.broadcast.BroadcastCallback;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.fragment.HomeFragment;
import com.nes.customgooglelauncher.utils.Constants;

import java.util.List;

import io.reactivex.Observable;

/**
 * {@link HomeFragment}的MVP 契约类
 * @author liuqz
 */
public class HomeFragmentContract {

    public interface IHomeFragmentView extends MvpView {

        /**
         * 显示所有“能显示”的Channel
         * @param list List<>{@link AbsItem}
         */
        void showAllShowChannel(List<AbsItem> list);

        /**
         * 刷新整个RecyclerView
         */
        void notifyAllData();

        /**
         * 刷新单条Channel
         * @param action 刷新动作 : {@link Constants#TYPE_NOTIFY_INSERTED}、{@link Constants#TYPE_NOTIFY_REMOVE}、{@link Constants#TYPE_NOTIFY_CHANGED}
         * @param channelBean {@link ChannelBean}
         */
        void notifyChannelOne(int action, ChannelBean channelBean);

        /**
         * 刷新单条Program
         * @param action 刷新动作 : {@link Constants#TYPE_NOTIFY_INSERTED}、{@link Constants#TYPE_NOTIFY_REMOVE}、{@link Constants#TYPE_NOTIFY_CHANGED}
         * @param channel Program对应的{@link Channel}
         * @param previewProgram {@link PreviewProgram} Program信息
         * @param programId program id
         */
        void notifyProgram(int action, Channel channel, PreviewProgram previewProgram, long programId);

        /**
         * 刷新若干个Channel
         * @param channelBeans List<>{@link ChannelBean}
         */
        void notifyChannels(List<ChannelBean> channelBeans);

        /**
         * 根据{@link PreviewProgram}判断这次操作的Action
         * @param previewProgram
         * @return
         */
        int getActionByProgram(PreviewProgram previewProgram);

        /**
         * 有 pkgName 包名的应用卸载
         * @param pkgName 包名
         */
        void unInstallApk(String pkgName);
    }

    public interface IHomeFragmentPresenter extends MvpFragmentPresenter<HomeFragmentContract.IHomeFragmentView> {

        /**
         * 加载所有可以展示的Channel
         */
        void loadAllShowChannel();

        /**
         * 监听数据库
         */
        void monitordb();

        /**
         * 添加监听app安装、卸载的广播监听器
         */
        void addAppBroadcast();
    }

    public interface IHomeFragmentModel extends MvpModel {
        /**
         * 获取Observable
         * @return {@link Observable}
         */
        Observable<List<ChannelBean>> getAllShowChannelObservable();

        /**
         * 监听数据库
         * @param callBack 给{@link MvpFragmentPresenter}端的回调
         */
        void monitordb(NotifyRecyclerViewCallBack callBack);

        /**
         * 添加app卸载、安装CallBack
         * @param callback {@link BroadcastCallback}
         */
        void addAppBroadcastCallBack(BroadcastCallback callback);
        /**
         * 移除app卸载、安装CallBack
         */
        void reMoveAppBroadcastCallBack();
    }

    /**
     * 监听数据库变化引起的刷新状态回调
     */
    public interface NotifyRecyclerViewCallBack {
        /**
         * 刷新整个RecyclerView
         */
        void notifyAllData();

        /**
         * 刷新单条Channel
         * @param action 刷新动作 : {@link Constants#TYPE_NOTIFY_INSERTED}、{@link Constants#TYPE_NOTIFY_REMOVE}、{@link Constants#TYPE_NOTIFY_CHANGED}
         * @param channelBean @link ChannelBean}
         */
        void notifyChannelOne(int action, ChannelBean channelBean);

        /**
         * 刷新单条Program
         * @param action 刷新动作 : {@link Constants#TYPE_NOTIFY_INSERTED}、{@link Constants#TYPE_NOTIFY_REMOVE}、{@link Constants#TYPE_NOTIFY_CHANGED}
         * @param channel Program对应的{@link Channel}
         * @param previewProgram {@link PreviewProgram} Program信息
         * @param programId program id
         */
        void notifyProgram(int action, Channel channel, PreviewProgram previewProgram,long programId);

        /**
         * 刷新若干个Channel
         * @param channelBeans List<>{@link ChannelBean}
         */
        void notifyChannels(List<ChannelBean> channelBeans);

        /**
         * 根据{@link PreviewProgram}判断这次操作的Action
         * @param previewProgram
         * @return
         */
        int getActionByProgram(PreviewProgram previewProgram);
    }

}
