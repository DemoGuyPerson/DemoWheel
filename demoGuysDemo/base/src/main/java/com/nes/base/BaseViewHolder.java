package com.nes.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

import static android.view.ViewGroup.FOCUS_AFTER_DESCENDANTS;
import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

/**
 *
 * @author liuqz
 * @date 2019/5/27
 */

public class BaseViewHolder<T extends BaseRecyclerViewAdapter> extends RecyclerView.ViewHolder{

    private WeakReference<T> mWeakReference;

    public void setAdapter(T t){
        mWeakReference = new WeakReference<T>(t);
    }

    public boolean isRealPosition(int position){
        return getAdapterPosition() == position;
    }

    private boolean isVisibleMode;

    public boolean isVisibleMode() {
        return isVisibleMode;
    }

    public BaseViewHolder(View itemView, T t){
        super(itemView);
        mWeakReference = new WeakReference<T>(t);
    }

    public void toggleVisibility(boolean isShow){
        isVisibleMode = !isShow;
        itemView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        itemView.setClickable(isShow);
        itemView.setFocusable(isShow);
        itemView.setFocusableInTouchMode(isShow);
        if (itemView instanceof ViewGroup){
            ((ViewGroup)itemView).setDescendantFocusability(isShow ? FOCUS_AFTER_DESCENDANTS : FOCUS_BLOCK_DESCENDANTS);
        }
    }

    public T getAdapter(){
        T result = null;
        if (mWeakReference != null){
            result = mWeakReference.get();
        }
        return result;
    }

    public boolean isAdapterNoEmpty(){
        return getAdapter() != null;
    }

    public void destroy(){
        if (mWeakReference!=null){
            mWeakReference.clear();
            mWeakReference = null;
        }
    }

}
