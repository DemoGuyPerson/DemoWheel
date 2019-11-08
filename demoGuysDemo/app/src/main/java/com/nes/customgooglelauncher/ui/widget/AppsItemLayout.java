package com.nes.customgooglelauncher.ui.widget;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.Util;

/**
 * {@link com.nes.customgooglelauncher.ui.fragment.MyAppsFragment}的RecyclerView的 itemView
 * @author liuqz
 */
public class AppsItemLayout extends AbsItemLayout<ResolveInfo> {

    private final int COLUMS_NUM = 6;
    private int mItemHeight = -1;
    private int mPadingBottom = -1;

    public AppsItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),COLUMS_NUM);
        DividerItemDecoration itemDecorationh = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        itemDecorationh.setDrawable(getResources().getDrawable(R.drawable.shape_line_2));
        DividerItemDecoration itemDecorationv = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorationv.setDrawable(getResources().getDrawable(R.drawable.shape_line_2));
        mFirstColumnRecyclerView.addItemDecoration(itemDecorationv);
        mFirstColumnRecyclerView.addItemDecoration(itemDecorationh);
        mFirstColumnRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mFirstColumnRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void bind(boolean isShow, AbsItem<ResolveInfo> absItem, int position) {
        setViewVisibility(absItem != null && Util.isNoEmptyList(absItem.getSourceList()));
        if (isShow && absItem != null && Util.isNoEmptyList(absItem.getSourceList())){
            mTitleTv.setText(absItem.getTitle());
            if (mPadingBottom == -1){
                mPadingBottom = mAdapterPresenter.getItemBottomPadding() + Utils.getDimension(getContext(),R.dimen.apps_recycler_item_magin);
            }
            if (mItemHeight == -1){
                mItemHeight = mPadingBottom + absItem.getHeight();
            }
            int size = absItem.getSourceList().size();
            int row = size / COLUMS_NUM+(size % COLUMS_NUM == 0 ? 0 : 1);
            ViewGroup.LayoutParams layoutParams = getRecyclerView().getLayoutParams();
            layoutParams.height = mItemHeight * row + Utils.getDimension(getContext(),R.dimen.apps_recycler_item_magin);
            getRecyclerView().setLayoutParams(layoutParams);

            mItemAdapter.setAdapterPresenter(absItem.getAdapterPresenter());
            mItemAdapter.setData(absItem.getSourceList());
            getRecyclerView().swapAdapter(mItemAdapter,true);
        }
    }
}
