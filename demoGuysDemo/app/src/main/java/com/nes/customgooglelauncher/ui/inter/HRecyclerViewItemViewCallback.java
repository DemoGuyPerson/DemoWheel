package com.nes.customgooglelauncher.ui.inter;

/**
 * 小RecyclerView的itemView需实现的接口
 * @see com.nes.customgooglelauncher.ui.widget.RecyclerViewItemLayout
 * @author liuqz
 * @date : 2019/10/14 16:44
 */
public interface HRecyclerViewItemViewCallback {

    /**
     * 根据自身获取到{@link HRecyclerViewCallback}
     * @return
     */
    HRecyclerViewCallback getParentCallback();

    /**
     * 父{@link HRecyclerViewCallback}所在的RecyclerView的position
     * @return
     */
    int getParentPosition();

    /**
     * 自身是否在小RecyclerView的最后一项
     * @return
     */
    boolean isLastPosition();

    /**
     * 自身是否在小RecyclerView的起始项
     * @return
     */
    boolean isFirstPosition();
}
