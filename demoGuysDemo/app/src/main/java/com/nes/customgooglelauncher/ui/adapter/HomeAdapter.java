package com.nes.customgooglelauncher.ui.adapter;


import android.annotation.SuppressLint;
import android.view.View;

import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;

import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.presenter.ThirdRecommendAdapterPresenter;
import com.nes.customgooglelauncher.ui.widget.FirstRowRecyclerView;
import com.nes.customgooglelauncher.ui.widget.FirstColumnRecyclerView;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.customgooglelauncher.utils.Constants;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.List;

/**
 * 首页Adapter
 * 相对于{@link AbsAdapter},这里面处理具体的业务逻辑
 * 但是具体的业务逻辑还是得委托给{@link com.nes.customgooglelauncher.ui.presenter.AbsAdapterPresenter}
 * @author liuqz
 */
public class HomeAdapter extends AbsAdapter implements FirstColumnRecyclerView.FocusCallBack {


    public HomeAdapter(){
        super();
    }

    private FirstColumnRecyclerView.FocusCallBack mFocusCallBack;

    public void setFocusCallBack(FirstColumnRecyclerView.FocusCallBack focusCallBack) {
        mFocusCallBack = focusCallBack;
    }

    @Override
    public int getItemResId() {
        return R.layout.item_home;
    }

    @Override
    protected FirstRowRecyclerView getCustomRecyclerView() {
        if (mRecyclerView instanceof FirstRowRecyclerView){
            return (FirstRowRecyclerView)mRecyclerView;
        }
        return super.getCustomRecyclerView();
    }

    /**
     * 第一次加载的时候，预加载其他项
     */
    public void firstPreLoad(){
//        if(getRealCount() > 3){
//            for (int i = 3, size = getRealCount(); i<size; i++){
//                AbsItem absItem = getItem(i);
//                if (absItem != null && !absItem.isClose()){
//                    load(absItem.getAdapterPresenter(),null,-1);
//                }
//            }
//        }
    }

    /**
     * 刷新所有第三方数据
     */
    public void notifyAllData(){
        if (getRealCount() > 0){
            for (AbsItem absItem : mList){
                if (absItem != null && !absItem.isClose() && absItem.getAdapterPresenter().isThirdRecommend()){
                    absItem.reductionPage();
                    absItem.getSourceList().clear();
                }
            }
            notifyCustomDataSetChanged();
        }
    }

    /**
     * 刷新单条Channel
     * @param action  {@link Constants.TYPE_NOTIFY_CHANGED}、{@link Constants.TYPE_NOTIFY_INSERTED}、{@link Constants.TYPE_NOTIFY_REMOVE}
     * @param channelBean 修改之后的数据源
     */
    public void notifyChannelOne(int action, ChannelBean channelBean){
        if (channelBean != null && channelBean.isDataOK()){
            String pkgName = channelBean.getChannel().getPackageName();
            LogX.d(TAG+" notifyChannelOne pkgName : "+pkgName);
            int position = -1;
            for (int i=0;i<getRealCount();i++){
                AbsItem absItem = getItem(i);
                if (absItem != null){
                    if (absItem.getAdapterPresenter() instanceof ThirdRecommendAdapterPresenter){
                        boolean isThisChannel = ((ThirdRecommendAdapterPresenter)absItem.getAdapterPresenter()).isThisChannelByChannelBean(channelBean);
                        if (isThisChannel){
                            position = getHeadersCount() + i;
                            LogX.d("testtesttest notifyChannelOne OKOKOK i : "+i+" enter : "+absItem.toString());
                            break;
                        }
                    }
                }
            }
            LogX.d(TAG+" notifyChannelOne position : "+position);
            notifyItem(action,position,channelBean.getPrograms(),channelBean.getChannel());
        }
    }

    /**
     * 刷新某一行Channel
     * @param acition {@link Constants.TYPE_NOTIFY_CHANGED}、{@link Constants.TYPE_NOTIFY_INSERTED}、{@link Constants.TYPE_NOTIFY_REMOVE}
     * @param positon Channel所在位置
     * @param list 数据源
     * @param channel Channel信息
     */
    private void notifyItem(int acition, int positon, List<PreviewProgram> list, Channel channel){
        LogX.d(TAG+" notifyItem acition : "+acition+" positon : "+positon+" o : "+(Util.isNoEmptyList(list))+" channel : "+(channel != null)
                +" getRealCount() : "+getRealCount() +" getHeadersCount : "+getHeadersCount());
        if (acition == Constants.TYPE_NOTIFY_INSERTED) {
            if (Util.isNoEmptyList(list) && channel != null && positon == -1) {
                ThirdRecommendAdapterPresenter presenter = new ThirdRecommendAdapterPresenter();
                AbsItem<PreviewProgram> absItem = new AbsItem<>(list,Utils.getChannelShowTitle(channel),String.valueOf(channel.getId()),HomeType.OTHER_RECOMMEND);
                absItem.setAdapterPresenter(presenter);
                absItem.addPage();
                addAbsItem(absItem);
                LogX.d(TAG + " notifyItem TYPE_NOTIFY_INSERTED positon : " + positon);
            }
        }else{
            if (positon > -1 && positon < getRealCount()){
                if (acition == Constants.TYPE_NOTIFY_CHANGED){
                    if ( Util.isNoEmptyList(list)) {
                        if (getItem(positon).getAdapterPresenter() instanceof ThirdRecommendAdapterPresenter){
                            ((ThirdRecommendAdapterPresenter)(getItem(positon).getAdapterPresenter())).setList(list);
                            notifyCustomItemChange(positon,getItem(positon));
                        }
                    }else{
                        reRequestByPosition(positon);
                    }
                }else if(acition == Constants.TYPE_NOTIFY_REMOVE){
                    mList.remove(positon-getHeadersCount());
                    notifyCustomItemReMoved(positon);
                }
            }
        }
    }

    /**
     * 刷新单个Program
     * @param action {@link Constants.TYPE_NOTIFY_CHANGED}、{@link Constants.TYPE_NOTIFY_INSERTED}、{@link Constants.TYPE_NOTIFY_REMOVE}
     * @param channel program所在的Channel,当program的action为{@link Constants.TYPE_NOTIFY_REMOVE}时，channel 为空。
     * @param previewProgram program所在的{@link PreviewProgram},当program的action为{@link Constants.TYPE_NOTIFY_REMOVE}时，previewProgram 为空。
     * @param programId program的id，,当program的action为{@link Constants.TYPE_NOTIFY_REMOVE}时，用 programId 进行判断。
     */
    @SuppressLint("RestrictedApi")
    public void notifyProgram(int action, Channel channel, PreviewProgram previewProgram, long programId){
        LogX.d(TAG+" notifyProgram action : "+action+" channel == null : "+(channel == null)
                +" previewProgram == null : "+(previewProgram == null)+" programId : "+programId);
        int bigIndex = -1;
        int smallIndex = -1;
        long _programId = previewProgram == null ? programId : previewProgram.getId();
        long channelId = channel == null ? -1 : channel.getId();
        for (int i=0,size1 = getRealCount();i<size1;i++){
            AbsItem absItem = getItem(i);
            if (absItem != null && absItem.getAdapterPresenter() instanceof ThirdRecommendAdapterPresenter){
                if (Util.isNoEmptyList(absItem.getSourceList())){
                    smallIndex = ((ThirdRecommendAdapterPresenter)(absItem.getAdapterPresenter())).getProgramPositionById(channelId,_programId);
                    if (smallIndex != -1){
                        if (bigIndex == -1){
                            bigIndex = i;
                        }
                        break;
                    }
                }
            }
        }
        notifyItemItem(action,bigIndex,smallIndex,previewProgram);
    }

    /**
     * 刷新某一行的某一个Program
     * @param action {@link Constants.TYPE_NOTIFY_CHANGED}、{@link Constants.TYPE_NOTIFY_INSERTED}、{@link Constants.TYPE_NOTIFY_REMOVE}
     * @param bigPosition Channel所在位置
     * @param smallPosition Program所在位置
     * @param previewProgram Program数据
     */
    private void notifyItemItem(int action, int bigPosition, int smallPosition, PreviewProgram previewProgram){
        LogX.d("notifyItemItem start smallPosition : "+smallPosition+" bigPosition : "+bigPosition+" action : "
                +action+" previewProgram : "+(previewProgram == null));
        if (bigPosition != -1 && bigPosition <getRealCount()){
            AbsAdapter.AbsViewHolder homeViewHolder = (AbsAdapter.AbsViewHolder)mRecyclerView.findViewHolderForLayoutPosition(bigPosition);
            if (homeViewHolder != null){
                AbsItem absItem = getItem(bigPosition);
                if (absItem != null && absItem.getAdapterPresenter() instanceof ThirdRecommendAdapterPresenter){
                    if (action == Constants.TYPE_NOTIFY_INSERTED){
                        /**这种模式，其实包含 TYPE_NOTIFY_INSERTED 和 TYPE_NOTIFY_CHANGED ; 要看 smallPosition 是否为-1*/
                        if (previewProgram != null){
                            if (smallPosition == -1) {
                                smallPosition = absItem.getSourceList().size();
                                if (smallPosition > 0) {
                                    ((ThirdRecommendAdapterPresenter)absItem.getAdapterPresenter()).addItem(previewProgram);
                                    LogX.d("notifyItemItem notifyItemInserted smallPosition : " + smallPosition + " bigPosition : " + bigPosition);
                                    homeViewHolder.getItemView().getAdapter().notifyItemInserted(smallPosition);
                                }else{
                                    LogX.e(" notifyItemItem TYPE_NOTIFY_INSERTED smallPosition == 0");
                                    reRequestByPosition(bigPosition);
                                }
                            }else{
                                ((ThirdRecommendAdapterPresenter)absItem.getAdapterPresenter()).setItem(smallPosition,previewProgram);
                                LogX.d("notifyItemItem notifyItemChanged smallPosition : "+smallPosition+" bigPosition : "+bigPosition);
                                homeViewHolder.getItemView().getAdapter().notifyItemChanged(smallPosition);
                            }
                        }
                    }else if(action == Constants.TYPE_NOTIFY_REMOVE){
                        if (smallPosition != -1 && smallPosition < absItem.getSourceList().size()){
                            LogX.d("notifyItemItem notifyItemRemoved smallPosition : "+smallPosition+" bigPosition : "+bigPosition);
                            ((ThirdRecommendAdapterPresenter)absItem.getAdapterPresenter()).removeItem(smallPosition);
                            homeViewHolder.getItemView().notifyItemRemoved(smallPosition);
                            if (!Util.isNoEmptyList(absItem.getSourceList())){
                                notifyCustomItemChange(bigPosition,getItem(bigPosition));
                            }
                        }
                    }else if(action == Constants.TYPE_NOTIFY_CHANGED){
                        if (smallPosition != -1 && smallPosition < absItem.getSourceList().size()){
                            LogX.d("notifyItemItem notifyItemChanged smallPosition : "+smallPosition+" bigPosition : "+bigPosition);
                            ((ThirdRecommendAdapterPresenter)absItem.getAdapterPresenter()).setItem(smallPosition,previewProgram);
                            homeViewHolder.getItemView().getAdapter().notifyItemChanged(smallPosition);
                        }
                    }
                }
            }else{
                LogX.d("notifyItemItem notifyItemInserted reRequestByPosition bigPosition : " + bigPosition );
                reRequestByPosition(bigPosition);
            }
        }
    }

    /**
     * 刷新多条Channel，与{@link notifyChannelOne(int action, ChannelBean channelBean)}对应
     * @param channelBeans
     */
    @SuppressLint("RestrictedApi")
    public void notifyChannels(List<ChannelBean> channelBeans){
        LogX.d(TAG+" notifyChannels channelBeans : "+(Util.isNoEmptyList(channelBeans) ?
                String.valueOf(channelBeans.size()) : " null "));
        if (Util.isNoEmptyList(channelBeans)){
            int sum = 0;
            int lastIndex = -1;
            for (ChannelBean channelBean : channelBeans){
                if (channelBean!=null && channelBean.isDataOK() && channelBean.getChannelId() != -1) {
                    for (int i = 0; i < getRealCount(); i++) {
                        AbsItem absItem = getItem(i);
                        if (absItem != null && absItem.getAdapterPresenter() instanceof ThirdRecommendAdapterPresenter){
                            boolean isThisChannel = ((ThirdRecommendAdapterPresenter)absItem.getAdapterPresenter()).isThisChannelByChannelBean(channelBean);
                            if (isThisChannel){
                                ((ThirdRecommendAdapterPresenter)absItem.getAdapterPresenter()).setList(channelBean.getPrograms());
                                lastIndex = i;
                                sum ++;
                            }
                        }
                    }
                }
            }
            if (sum == 1){
                notifyCustomItemChange(lastIndex,getItem(lastIndex));
            }else {
                notifyCustomDataSetChanged();
            }
        }
    }

    /**
     * 根据新{@link PreviewProgram}获取到此次刷新的Action
     * @param previewProgram
     * @return
     */
    public int getActionByProgram(PreviewProgram previewProgram){
        int result = Constants.TYPE_NOTIFY_DEFULT;
        if (previewProgram == null){
            return result;
        }
        for (int i=0;i<getRealCount();i++){
            AbsItem absItem = getItem(i);
            if (absItem != null){
                if (absItem.getAdapterPresenter() instanceof ThirdRecommendAdapterPresenter){
                    int action = ((ThirdRecommendAdapterPresenter)absItem.getAdapterPresenter()).getActionByProgram(previewProgram);
                    if (action != Constants.TYPE_NOTIFY_DEFULT){
                        result = action;
                        LogX.d("getActionByProgram action : "+action+" previewProgram : "+previewProgram);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 对于卸载广播的检查，是否有需要移除的Channel或者Program
     * @param pkgName
     */
    @SuppressLint("RestrictedApi")
    public void checkUnInstallApk(String pkgName){
        if (getRealCount() > 0){
            int sum = 0;
            int lastNum = 0;
            for (int i = getItemCount()-1; i >= 0; i--){
                AbsItem absItem = getItem(i);
                if (absItem != null && absItem.isShow()){
                    if(absItem.getAdapterPresenter() instanceof ThirdRecommendAdapterPresenter){
                        if (Util.isNoEmptyList(absItem.getSourceList())){
                            boolean isThisChannel = ((ThirdRecommendAdapterPresenter)absItem.getAdapterPresenter())
                                    .isThisChannelByPkgName(pkgName);
                            if (isThisChannel){
                                mList.remove(absItem);
                                lastNum = i;
                                sum ++;
                            }
                        }
                    }
                }
            }
            if (sum == 1){
                notifyCustomItemReMoved(lastNum);
            }else if(sum > 1){
                notifyCustomDataSetChanged();
            }
            LogX.d(TAG+" checkUnInstallApk sum : "+sum+" lastNum : "+lastNum);
        }
    }


    @Override
    public View getNextFocusView() {
        if (mFocusCallBack != null){
            return mFocusCallBack.getNextFocusView();
        }
        return null;
    }
}
