package com.nes.customgooglelauncher.ui.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.tvprovider.media.tv.PreviewProgram;

import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.bean.HomeDetailBean;
import com.nes.customgooglelauncher.bean.PopBean;
import com.nes.customgooglelauncher.mvp.presenter.AdapterHomePagePresenter;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.adapter.AbsAdapter;
import com.nes.customgooglelauncher.ui.adapter.PvrAdapter;
import com.nes.customgooglelauncher.ui.inter.ItemFocusChangeListener;
import com.nes.customgooglelauncher.ui.widget.CustomPopupWindow;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.customgooglelauncher.ui.widget.RecyclerViewItemLayout;
import com.nes.customgooglelauncher.utils.ChannelProgramUtils;
import com.nes.customgooglelauncher.utils.ClickUtils;
import com.nes.customgooglelauncher.utils.Constants;
import com.nes.customgooglelauncher.utils.PopBeanUtils;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.List;
import java.util.Map;
import static com.nes.customgooglelauncher.ui.widget.HomeType.OTHER_RECOMMEND;

/**
 * 所有第三方推荐的{@link AbsAdapterPresenter}
 * @author liuqz
 */
public class ThirdRecommendAdapterPresenter extends AbsAdapterPresenter<PreviewProgram>{

    public ThirdRecommendAdapterPresenter(){

    }

    public ThirdRecommendAdapterPresenter(ItemFocusChangeListener listener){
        setItemFocusChangeListener(listener);
    }

    @Override
    public int getEndVisibleItemCount() {
        return 6;
    }

    @Override
    public boolean isThirdRecommend() {
        return true;
    }

    @Override
    public boolean isShowRowTitle() {
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindView(PreviewProgram previewProgram, int position, RecyclerViewItemLayout itemLayout, boolean isMoveMode) {
        itemLayout.setLayoutSize(Utils.getWidthByPreviewProgram(previewProgram,mAbsItem.getHeight()),mAbsItem.getHeight(),this);
        itemLayout.bindUri(previewProgram.getPosterArtUri());
        itemLayout.bindName(previewProgram.getTitle());
    }

    @Override
    public void onBindInVisibleView(int position, RecyclerViewItemLayout itemLayout) {
        itemLayout.setLayoutSize(Utils.getWidthByPreviewProgram(null,mAbsItem.getHeight()),mAbsItem.getHeight(),this);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean isFirstPosition(PreviewProgram previewProgram) {
        if (previewProgram != null && Util.isNoEmptyList(mAbsItem.getSourceList())){
            PreviewProgram firstPreviewProgram = mAbsItem.getSourceList().get(0);
            if (firstPreviewProgram != null ){
                return firstPreviewProgram.getId() == previewProgram.getId();
            }
        }
        return super.isFirstPosition(previewProgram);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean isLastPosition(PreviewProgram previewProgram) {
        if (previewProgram != null && Util.isNoEmptyList(mAbsItem.getSourceList())){
            PreviewProgram lastPreviewProgram = mAbsItem.getSourceList().get(mAbsItem.getSourceList().size() - 1);
            if (lastPreviewProgram != null ){
                return lastPreviewProgram.getId() == previewProgram.getId();
            }
        }
        return super.isLastPosition(previewProgram);
    }

    @Override
    public void onItemFocusChangeListener(boolean hasFocus, View view, HomeType homeType, PreviewProgram previewProgram) {
        HomeDetailBean detailBean = Utils.getHomeDetailsBeanByPreviewProgram(previewProgram);
        onItemChange(homeType,detailBean,hasFocus,view);
    }

    @Override
    public void onItemClickListener(View view, int position, PreviewProgram previewProgram, boolean isEmpty) {
        ClickUtils.clickPreviewProgram(view.getContext(),previewProgram);
    }

    @Override
    public boolean onItemLongClickListener(final ImageView view, int position,final PreviewProgram previewProgram) {
        LogX.d(TAG+" onItemLongClickListener position : "+position);
        if (previewProgram != null){
            final CustomPopupWindow customPop = new CustomPopupWindow(view.getContext(), PopBeanUtils.get().getChannelType());
            customPop.setBottomShow(true);
            customPop.setView(view);
            customPop.setPosterImageView(view.getDrawable());
            customPop.show();
            customPop.setPopItemClick(new PvrAdapter.PopItemClick() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onPopItemClick(int position, PopBean bean) {
                    LogX.d(TAG+" onItemLongClickListener onPopItemClick position : "+position+" bean : "+bean.toString());
                    if (bean.getName().equals(view.getContext().getString(R.string.channel_opera_add_playNext))) {
                        ChannelProgramUtils.getInstance().addPlayNext(previewProgram);
                    } else if (bean.getName().equals(view.getContext().getString(R.string.channel_opera_remove))) {
//                                ChannelUtils.get().updateProgramBrowsable(program.getId() + "", false);
                        ChannelProgramUtils.getInstance().deleteProgram(previewProgram.getId());
                        LogX.i("program longclick:" + previewProgram.toString());
                    }
                    customPop.dismiss();
                }
            });
        }
        return true;
    }

    @Override
    public void load(AdapterHomePagePresenter pagePresenter,final AbsAdapter.AbsViewHolder holder,
                     final int position,final Map<AbsItem, Boolean> map) {
        String pkgName = getPkgName();
        if (!TextUtils.isEmpty(pkgName)){
            LogX.d(TAG+" load pkgName : "+pkgName);
            pagePresenter.loadThirdRecommend(pkgName, new AdapterHomePagePresenter.LoadListener<PreviewProgram>() {
                @Override
                public void onLoadResult(int code, List<PreviewProgram> list, Object o) {
                    processLoadResult(code,list,holder,position,map);
                }
            });
        }else{
            long channelId = Util.stringToLong(mAbsItem.getAdditional());
            LogX.d(TAG+" load channelId : "+channelId);
            if (channelId != -1){
                pagePresenter.loadOtherRecommend(channelId, new AdapterHomePagePresenter.LoadListener<PreviewProgram>() {
                    @Override
                    public void onLoadResult(int code, List<PreviewProgram> list, Object o) {
                        processLoadResult(code,list,holder,position,map);
                    }
                });
            }
        }
    }

    private String getPkgName(){
        return "";
    }

    public boolean isThisChannelByChannelBean(ChannelBean channelBean){
        List<PreviewProgram> list = mAbsItem.getSourceList();
        if (Util.isNoEmptyList(list) && list.get(0).getChannelId() == channelBean.getChannel().getId()) {
            return true;
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    public int getActionByProgram(PreviewProgram previewProgram){
        int result = Constants.TYPE_NOTIFY_DEFULT;
        List<PreviewProgram> list = mAbsItem.getSourceList();
        if (Util.isNoEmptyList(list)) {
            if (list.get(0).getChannelId() == previewProgram.getChannelId()) {
                for (int i = 0,size = list.size();i < size; i++){
                    PreviewProgram program = list.get(i);
                    if (previewProgram.getId() == program.getId()) {
                        result = previewProgram.isBrowsable() ? Constants.TYPE_NOTIFY_CHANGED : Constants.TYPE_NOTIFY_REMOVE;
                        break;
                    }
                    if (i == size - 1){
                        if (previewProgram.isBrowsable()){
                            result = Constants.TYPE_NOTIFY_INSERTED;
                        }
                    }
                }
            }
        }
        return result;
    }

    @SuppressLint("RestrictedApi")
    public boolean isThisChannelByPkgName(String pkgName){
        List<PreviewProgram> list = mAbsItem.getSourceList();
        if (Util.isNoEmptyList(list) && list.get(0).getPackageName().equals(pkgName)) {
            return true;
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    public int getProgramPositionById(long channelId, long programId){
        int result = -1;
        List<PreviewProgram> list = mAbsItem.getSourceList();
        if (channelId != -1){
            if (list.get(0).getChannelId() != channelId){
                result = -1;
            }
        }else{
            for (int j=0,size = list.size(); j<size; j++){
                if (list.get(j).getId() == programId){
                    result = j;
                    break;
                }
            }
        }
        LogX.d(TAG+" getProgramPositionById channelId : "+channelId+" programId : "+programId+" result : "+result);
        return result;
    }


}
