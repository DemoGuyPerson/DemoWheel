package com.nes.customgooglelauncher.ui.inter;

import android.app.Activity;
import com.nes.customgooglelauncher.ui.adapter.AbsRecyclerViewItemAdapter;
import com.nes.customgooglelauncher.ui.presenter.AbsAdapterPresenter;

/**
 * {@link AbsRecyclerViewItemAdapter}和{@link AbsAdapterPresenter}的"纽带";
 * 用于在{@link AbsAdapterPresenter}中对{@link AbsRecyclerViewItemAdapter}进行一些操作
 * @author liuqz
 */
public interface AdapterContract {

    /**
     * 当前是不是移动模式
     * @return 移动模式
     */
    boolean isMoveMode();

    /**
     * 设置制定 position 为 isMove 模式
     * @param isMove 是否为移动模式
     * @param position layout position
     */
    void setMoveMode(boolean isMove,int position);

    /**
     * 移除指定项
     * @param position layout position
     */
    void removePosition(int position);

    /**
     * 获取Activity
     * @return
     */
    Activity getActivity();

    /**
     * 移动两项
     * @param fromPosition 开始项
     * @param toPosition 移动项
     */
    void movePosition(int fromPosition,int toPosition);
}
