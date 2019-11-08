package com.nes.customgooglelauncher.ui.inter;

import android.view.View;

import com.nes.customgooglelauncher.bean.HomeDetailBean;
import com.nes.customgooglelauncher.ui.widget.HomeType;

/**
 * 对于嵌套RecyclerView来说,"可见"的最小item的FocusChangeListener事件
 * @author liuqz
 */
public interface ItemFocusChangeListener {
    /**
     * 焦点状态改变
     * @param homeType 该item对应的{@link HomeType}
     * @param bean 用该item的原始数据封装之后的{@link HomeDetailBean}数据
     * @param hasFocus 获得还是失去焦点
     * @param view itemView
     */
    void onItemChange(HomeType homeType, HomeDetailBean bean, boolean hasFocus, View view);
}
