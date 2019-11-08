package com.nes.customgooglelauncher.ui.presenter;

import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.bean.PopBean;
import com.nes.customgooglelauncher.mvp.presenter.AdapterHomePagePresenter;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.adapter.AbsAdapter;
import com.nes.customgooglelauncher.ui.adapter.AppsAdapter;
import com.nes.customgooglelauncher.ui.adapter.PvrAdapter;
import com.nes.customgooglelauncher.ui.widget.CustomPopupWindow;
import com.nes.customgooglelauncher.ui.widget.RecyclerViewItemLayout;
import com.nes.customgooglelauncher.utils.AppUtils;
import com.nes.customgooglelauncher.utils.ClickUtils;
import com.nes.customgooglelauncher.utils.PopBeanUtils;
import com.nes.utils.Util;

import java.util.List;
import java.util.Map;

/**
 * 所有App or Game的{@link AbsAdapterPresenter}
 * @author liuqz
 */
public class AllAppAndGameAdapterPresenter extends AbsAdapterPresenter<ResolveInfo>{


    public AllAppAndGameAdapterPresenter(){

    }

    @Override
    public boolean isShowRowTitle() {
        return true;
    }

    @Override
    public boolean isShowItemTitle() {
        return true;
    }

    @Override
    public boolean isUseCustomLayoutManager() {
        return true;
    }

    @Override
    public void onBindView(ResolveInfo resolveInfo, int position, RecyclerViewItemLayout itemLayout, boolean isMoveMode) {
        itemLayout.setLayoutSizeMatch(this,6);
        itemLayout.bindPkgName(resolveInfo.activityInfo != null ? resolveInfo.activityInfo.packageName : "");
        itemLayout.bindName(resolveInfo.loadLabel(MyApplication.getInstance().getPackageManager()).toString());
    }


    @Override
    public void onItemClickListener(View view, int position, ResolveInfo resolveInfo, boolean isEmpty) {
        ClickUtils.clickResolveInfo(view.getContext(),resolveInfo);
    }

    @Override
    public boolean onItemLongClickListener(final ImageView view, int position,final ResolveInfo resolveInfo) {
        if (resolveInfo != null){
            final CustomPopupWindow customPop = new CustomPopupWindow(view.getContext(), PopBeanUtils.get().getAppType(resolveInfo));
            customPop.setView(view);
            customPop.setPosterImageView(view.getDrawable());
            customPop.show();
            customPop.setPopItemClick(new PvrAdapter.PopItemClick() {
                @Override
                public void onPopItemClick(int position, PopBean bean) {
                    AppUtils.getInstance().control(view.getContext(),resolveInfo, bean);
                    customPop.dismiss();
                }
            });
        }
        return false;
    }

    @Override
    public void load(AdapterHomePagePresenter pagePresenter, final AbsAdapter.AbsViewHolder holder, final int position, final Map<AbsItem, Boolean> map) {
         pagePresenter.loadAllAppOrGame(mAbsItem.getHomeType(), new AdapterHomePagePresenter.LoadListener<ResolveInfo>() {
             @Override
             public void onLoadResult(int code, List<ResolveInfo> list, Object o) {
                 processLoadResult(code,list,holder,position,map);
             }
         });
    }

    public boolean removeApp(AppsAdapter adapter, String pkgName,int position){
        boolean result = false;
        if (Util.isNoEmptyList(mAbsItem.getSourceList())) {
            for (int j = 0, jSize = mAbsItem.getSourceList().size(); j < jSize; j++) {
                String packname = mAbsItem.getSourceList().get(j).activityInfo == null ? "" : mAbsItem.getSourceList().get(j).activityInfo.packageName;
                if (!TextUtils.isEmpty(packname) && packname.equals(pkgName)) {
                    AbsAdapter.AbsViewHolder holder = (AbsAdapter.AbsViewHolder) adapter.getRecyclerView().findViewHolderForLayoutPosition(position);
                    if (holder != null) {
                        mAbsItem.getSourceList().remove(j);
                        if (mAbsItem.getSourceList().size() == 0) {
//                            mList.remove(bean);
                            adapter.notifyItemChanged(position);
                        } else {
                            holder.getItemView().getAdapter().notifyItemRemoved(j);
                        }
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}
