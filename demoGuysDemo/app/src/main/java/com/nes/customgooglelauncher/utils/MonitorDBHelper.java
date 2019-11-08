package com.nes.customgooglelauncher.utils;

import android.annotation.SuppressLint;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;
import androidx.tvprovider.media.tv.TvContractCompat;

import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.bean.SyncHandlerUriEntry;
import com.nes.customgooglelauncher.mvp.contract.HomeFragmentContract;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.nes.customgooglelauncher.utils.MonitorDBHelper.MonitorHandler.FLAY_TIME_OUT;

/**
 * 监听Google Channel Program DB的Help类，用来处理数据过快，UI刷新不过来，卡顿问题
 *
 * @author liuqz
 *
 */
public class MonitorDBHelper {
    private ContentObserver mChannelContentObserver;
    private ContentObserver mProgramContentObserver;
    /**
     * 用 HandlerThread 来进行异步通讯
     */
    private HandlerThread mHandlerThread;
    private MonitorHandler mHandler;

    public UriMatcher sUriMatcher = new UriMatcher(-1);

    /**
     * 给外界调用者的唯一CallBack
     */
    private HomeFragmentContract.NotifyRecyclerViewCallBack mCallBack;

    /**
     * 用来装任务的List，把数据库一个变化看作是一个要处理的任务
     */
    private List<SyncHandlerUriEntry> mQueueList;
    /**
     * 操作mQueueList 的类型，指往里面添加数据
     */
    private final int TYPE_LIST_ADD_ACTION = 102;
    /**
     * 操作mQueueList 的类型，指删除mQueueList.get(0);
     */
    private final int TYPE_LIST_REMOVE_ACTION = 103;
    private final long TIME_OUT_LONG = 60 * 1000;
    /**
     * mCallBack回调之后，之后应该会刷新UI，这里延迟 2 秒进行下个任务处理
     */
    private final long DELATY_TIME = 2000L;

    /**
     * 同步对象
     */
    private final Object mObject = new Object();
    /**
     * 标记当前是否正在处理任务，true的话，只往mQueueList里面添加数据
     */
    private AtomicBoolean isLoading = new AtomicBoolean(false);
    /**
     * 记录上个任务开始处理时间，用作超时，防止出现异常，该处理消息线程失去作用。
     */
    private AtomicLong mLastStartTime = new AtomicLong(-1);

    /**
     * 处理Program uri变化时，上个任务的创建时间
     */
    private AtomicLong mLastTaskCreateTime = new AtomicLong(-1);
    /**
     * 处理Program uri变化时，记录放弃掉的任务个数，达到一定数时，刷新全部UI
     */
    private AtomicInteger mAbandonNum = new AtomicInteger(0);
    /**
     * 处理Program uri变化时，放弃掉两个创建时间间隔小于500毫秒的任务
     */
    private final int CHANGE_VIEW_INTERVAL_TIME = 500;
    /**
     * 处理Program uri变化时，放弃掉的任务个数达到20个时，强制刷新UI
     */
    private final int CHANGE_VIEW_MAX_NUM = 40;//todo:刷新Adapter间隔时间
//    private List<ChannelBean> mTemChannelBeanList = new ArrayList<>();

    public MonitorDBHelper(){
        mHandlerThread = new HandlerThread("process_monitor_handler");
        mHandlerThread.start();
        mHandler = new MonitorHandler(mHandlerThread.getLooper());
        mQueueList = new ArrayList<>();

        sUriMatcher.addURI(TvContractCompat.AUTHORITY, "channel", 1);
        sUriMatcher.addURI(TvContractCompat.AUTHORITY, "channel/#", 2);
        sUriMatcher.addURI(TvContractCompat.AUTHORITY, "channel/#/logo", 3);
        sUriMatcher.addURI(TvContractCompat.AUTHORITY, "preview_program", 4);
        sUriMatcher.addURI(TvContractCompat.AUTHORITY, "preview_program/#", 5);
        sUriMatcher.addURI(TvContractCompat.AUTHORITY, "watch_next_program", 6);
        sUriMatcher.addURI(TvContractCompat.AUTHORITY, "watch_next_program/#", 7);
    }

    public void setCallBack(HomeFragmentContract.NotifyRecyclerViewCallBack callBack) {
        mCallBack = callBack;
    }

    /**
     * 监听 {@link TvContractCompat.Channels.CONTENT_URI} 数据库
     */
    public void monitorChannelDB() {
        if (mChannelContentObserver == null){
            mChannelContentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    int match = sUriMatcher.match(uri);
                    LogX.e("monitorChannelDB uri : "+uri+" match : "+match);
                    if (uri.toString().contains("logo")) {
                        return;
                    }
                    SyncHandlerUriEntry entry = new SyncHandlerUriEntry();
                    entry.setChannelUri(uri);
                    entry.currentTime = System.currentTimeMillis();
                    tryUpdate(entry);
                }
            };
        }
        MyApplication.getInstance().getContentResolver()
                .registerContentObserver(TvContractCompat.Channels.CONTENT_URI,true,mChannelContentObserver);
    }

    /**
     * 监听 {@link TvContractCompat.PreviewPrograms.CONTENT_URI} 数据库
     */
    public void monitorProgramDB() {
        if (mProgramContentObserver == null){
            mProgramContentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    int match = sUriMatcher.match(uri);
                    LogX.e("monitorProgramDB uri : "+uri+" match : "+match);
                    SyncHandlerUriEntry entry = new SyncHandlerUriEntry();
                    entry.setProgramUri(uri);
                    entry.currentTime = System.currentTimeMillis();
                    tryUpdate(entry);
                }

                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                }
            };
        }
        MyApplication.getInstance().getContentResolver()
                .registerContentObserver(TvContractCompat.PreviewPrograms.CONTENT_URI,true,mProgramContentObserver);
    }

    /**
     * 切换处理任务的线程到HandlerThread线程
     */
    class MonitorHandler extends Handler {
        static final int START = 1001;
        static final int PROCESS_ENTRY_OVER = 1002;
        static final int FLAY_TIME_OUT = 1003;

        MonitorHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == START){
                /**开始切换，执行任务*/
                doSyncHandlerEnter();
            }else if (msg.what == PROCESS_ENTRY_OVER){
                LogX.d(" handleMessage PROCESS_ENTRY_OVER");
                isLoading.set(false);
                startTask();
            }else if(msg.what == FLAY_TIME_OUT){
                /**
                 * 刷新 Program 的时候，处理超时数据，或者最后一批数据
                 */
                LogX.d(" handleMessage FLAY_TIME_OUT");
                mAbandonNum.set(0);
                mLastTaskCreateTime.set(System.currentTimeMillis());
//                mHandler.removeMessages(FLAY_TIME_OUT);
                if (mCallBack != null){
                    LogX.e(" handleMessage CALLBACK notifyAllData TIME END");
                    mCallBack.notifyAllData();
                }
            }
        }
    }

    /**
     * 同步List操作
     * @param action 有添加和删除两种Action
     * @param entry 需同步的数据
     */
    private void syncEntryList(int action, SyncHandlerUriEntry entry){
        synchronized (mObject){
           if (action == TYPE_LIST_ADD_ACTION){
               mQueueList.add(entry);
           }else if(action == TYPE_LIST_REMOVE_ACTION){
               mQueueList.remove(0);
           }
        }
    }

    private void tryUpdate(SyncHandlerUriEntry entry){
        syncEntryList(TYPE_LIST_ADD_ACTION,entry);
        startTask();
    }

    private void startTask(){
        if (mHandler != null){
            if (isLoading.compareAndSet(false, true)) {
                mLastStartTime.set(System.currentTimeMillis());
                mHandler.sendEmptyMessage(MonitorHandler.START);
            }else{
                long lastTime = mLastStartTime.get();
                if (lastTime > 0 && System.currentTimeMillis() - lastTime > TIME_OUT_LONG) {
                    LogX.e(" tryUpdate isLoading : " + isLoading.get() + " TIME OUT!!!!!!!!!!!");
                    /**
                     * 处理单条数据超时，一分钟之后全刷
                     */
                    callbackAll();
                    notifyOver(DELATY_TIME);
                }
            }
        }
    }

    /**
     * 拿 mQueueList 第一个元素，成功的话，从 mQueueList 中删掉第一个元素，
     * 然后处理该任务.
     */
    private void doSyncHandlerEnter(){
        SyncHandlerUriEntry entry = null;
        if (!mQueueList.isEmpty()) {
            entry = mQueueList.get(0);
        }
        if (entry == null) {
            isLoading.set(false);
        } else {
            syncEntryList(TYPE_LIST_REMOVE_ACTION, null);
            if (entry.isChannel()){
                dealChannel(entry);
            }else {
                dealProgram(entry);
            }
        }
    }

    /**
     * 处理 Channel 任务
     * @param entry 任务实体类
     */
    private void dealChannel(SyncHandlerUriEntry entry){
        long detailTime = 0;
        if (entry.uri == null || entry.uri.toString().equals(TvContractCompat.Channels.CONTENT_URI.toString())) {
            LogX.e( " dealChannel uri == null || equals TvContractCompat.Channels.CONTENT_URI");
            notifyOver();
            return;
        }
        ChannelBean channelBean = ChannelProgramUtils.getInstance().getChannelBean(entry.uri);
        Channel channel = channelBean.getChannel();
        if (channel != null){
            if (mCallBack != null){
                int action = channel.isBrowsable() ? Constants.TYPE_NOTIFY_INSERTED : Constants.TYPE_NOTIFY_REMOVE;
                mCallBack.notifyChannelOne(action,channelBean);
                detailTime = DELATY_TIME;

            }
        }else{
            if (mCallBack != null){
                channelBean.setChannelId(Util.stringToLong(entry.uri.getLastPathSegment()));
                mCallBack.notifyChannelOne(Constants.TYPE_NOTIFY_REMOVE,channelBean);
                detailTime = DELATY_TIME;
            }
        }
        notifyOver(detailTime);
    }

    /**
     * 处理 Program 任务
     * @param entry 任务实体类
     */
    @SuppressLint("RestrictedApi")
    private void dealProgram(SyncHandlerUriEntry entry){
        if (entry.uri == null){
            LogX.e( " dealProgram uri == null");
            notifyOver();
            return;
        }
        PreviewProgram program = ChannelProgramUtils.getInstance().getProgram(entry.uri);
        if (program == null){
            long programId = Util.stringToLong(entry.uri.getLastPathSegment());
            if (programId != -1){
                tryProgramCallBack(Constants.TYPE_NOTIFY_REMOVE,null,Util.stringToLong(entry.uri.getLastPathSegment()),entry);
            }else{
                notifyOver();
            }
        }else{
//            int action = program.isBrowsable() ? Constants.TYPE_NOTIFY_INSERTED : Constants.TYPE_NOTIFY_REMOVE;
            int action = mCallBack.getActionByProgram(program);
            if(action == Constants.TYPE_NOTIFY_DEFULT){
                notifyOver();
            }else{
                tryProgramCallBack(action,program,program.getId(),entry);
            }
        }
    }
    /**
     * 判断此次刷新的状态
     *
     * @return -1：刷新局部，默认状态。
     * 1：达到刷新状态，累计次数达到20次。
     * 2：放弃刷新，还在快速刷新时间间隔以内，放弃掉本次刷新。
     */
    private int getCanChangeViewFlag(SyncHandlerUriEntry entry){
        int result = -1;
        long lastChangeViewTime = mLastTaskCreateTime.get();
        long currentTime = entry.currentTime;
        int abandonNum = mAbandonNum.get();
        if (lastChangeViewTime == -1) {
            /**一开始的状态，刷新局部*/
            result = -1;
        } else {
            if (abandonNum >= CHANGE_VIEW_MAX_NUM) {
                /**累计次数达到{@link CHANGE_VIEW_MAX_NUM}次，刷新全部*/
                mAbandonNum.set(0);
                result = 1;
            } else {
                /**累计次数没达到15次*/
                LogX.d(" currentTime : "+currentTime +" lastChangeViewTime : "+lastChangeViewTime+" cha : "+(currentTime - lastChangeViewTime));
                if (currentTime - lastChangeViewTime >= CHANGE_VIEW_INTERVAL_TIME) {
                    /**两次间隔时间超过 {@link CHANGE_VIEW_INTERVAL_TIME}*/
                    if (abandonNum == 0) {
                        /**之前没有累计刷新的情况，刷新局部*/
                        result = -1;
                    } else {
                        /**之前有累计刷新的情况，刷新全部，置空mAbandonNum*/
                        mAbandonNum.set(0);
                        result = 1;
                    }
                } else {
                    /**快速刷新情况，自增*/
                    mAbandonNum.getAndIncrement();
                    result = 2;
                }
            }
        }
        mLastTaskCreateTime.set(currentTime);
        return result;
    }

    /**
     * 尝试对Program事件 CallBack
     * @param action
     * @param previewProgram
     * @param programId
     * @param entry
     */
    private void tryProgramCallBack(int action, PreviewProgram previewProgram, long programId, SyncHandlerUriEntry entry){
        int flag = getCanChangeViewFlag(entry);
        LogX.d(" tryProgramCallBack flag : "+flag+" remove FLAY_TIME_OUT");
        mHandler.removeMessages(FLAY_TIME_OUT);
        if (flag == -1){
//            mTemChannelBeanList.clear();
            if (mCallBack != null){
                LogX.e(" tryProgramCallBack CALLBACK notifyProgram action : "+action);
                Channel channel = previewProgram == null ? null : ChannelProgramUtils.getInstance().getChannelByChannelId(previewProgram.getChannelId());
                mCallBack.notifyProgram(action,channel,previewProgram,programId);
                notifyOver(DELATY_TIME);
            }else {
                notifyOver();
            }
        }else if(flag == 1){
            if (mCallBack != null ){
//                LogX.e(" tryProgramCallBack CALLBACK notifyChannels mTemChannelBeanList : "
//                        +(Util.isNoEmptyList(mTemChannelBeanList) ? String.valueOf(mTemChannelBeanList.size()) : " null "));
//                mCallBack.notifyChannels(mTemChannelBeanList);
//                mTemChannelBeanList.clear();
                LogX.e(" tryProgramCallBack CALLBACK notifyAllData ");
                mCallBack.notifyAllData();
                notifyOver(DELATY_TIME);
            }else {
                notifyOver();
            }
        }else if(flag == 2){
//            ChannelBean channelBean = previewProgram == null ? null : ChannelProgramUtils.getInstance().getChannelBeanByChannelId(previewProgram.getChannelId());
//            if (channelBean != null) {
//                int index = mTemChannelBeanList.indexOf(channelBean);
//                if (index == -1) {
//                    LogX.e(" tryProgramCallBack NO CALLBACK add channel Id : " + channelBean.getRealChannelId());
//                    mTemChannelBeanList.add(channelBean);
//                } else {
//                    LogX.e(" tryProgramCallBack NO CALLBACK set channel Id : " + channelBean.getRealChannelId() + " index : " + index);
//                    mTemChannelBeanList.set(index, channelBean);
//                }
//            }
            mHandler.removeMessages(FLAY_TIME_OUT);
            LogX.e(" tryProgramCallBack sendEmptyMessageDelayed FLAY_TIME_OUT : "+ CHANGE_VIEW_INTERVAL_TIME * 2);
            mHandler.sendEmptyMessageDelayed(FLAY_TIME_OUT, CHANGE_VIEW_INTERVAL_TIME * 2);
            notifyOver();
        }
    }

    private void notifyOver(){
        notifyOver(0);
    }
    private void notifyOver(long delayTime) {
        if (delayTime > 0) {
            /**这里一定要清掉Program的{@link FLAY_TIME_OUT}标志，否则的话，{@link DELATY_TIME} 会导致刷新Program超时*/
            mHandler.removeMessages(FLAY_TIME_OUT);
            LogX.e(" notifyOver delayTime : " + delayTime);
        }
        if (mHandler != null) {
            mHandler.removeMessages(MonitorHandler.PROCESS_ENTRY_OVER);
            mHandler.sendEmptyMessageDelayed(MonitorHandler.PROCESS_ENTRY_OVER, delayTime);
        }
    }

    private void callbackAll(){
        if (mCallBack != null){
            mCallBack.notifyAllData();
        }
    }

    public void destroy(){
        if (mChannelContentObserver != null){
            MyApplication.getInstance().getContentResolver().unregisterContentObserver(mChannelContentObserver);
        }
        if (mProgramContentObserver != null){
            MyApplication.getInstance().getContentResolver().unregisterContentObserver(mProgramContentObserver);
        }
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mHandlerThread != null){
            mHandlerThread.quitSafely();
        }
    }

}
