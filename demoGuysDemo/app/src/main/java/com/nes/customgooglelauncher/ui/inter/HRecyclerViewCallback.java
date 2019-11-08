package com.nes.customgooglelauncher.ui.inter;

import android.view.View;

import com.nes.customgooglelauncher.ui.widget.FirstColumnRecyclerView;

/**
 * 用于限定大项RecyclerView item的接口，如果
 * @see com.nes.customgooglelauncher.ui.widget.AbsItemLayout
 * @author liuqz
 * @date : 2019/10/14 16:34
 */
public interface HRecyclerViewCallback {

    /**
     * 返回子RecyclerView的应该获取焦点的View
     * @return Focus View
     */
    View getStartView();

    /**
     * 返回View Height
     * @return view height
     */
    int getViewHeight();

    /**
     * 返回对应的RecyclerView Position
     * @return RecyclerView Position
     */
    int getPosition();

    /**
     * 返回是否是显示状态
     * @return 显示状态
     */
    boolean isAllShow();

    /**
     * 获取itemView {@link FirstColumnRecyclerView}
     * @return
     */
    FirstColumnRecyclerView getItemRecyclerView();

}
