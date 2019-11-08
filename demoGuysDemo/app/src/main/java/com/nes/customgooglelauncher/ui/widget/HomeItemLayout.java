package com.nes.customgooglelauncher.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.nes.customgooglelauncher.ui.adapter.AbsAdapter;
import com.nes.customgooglelauncher.ui.adapter.HomeAdapter;
import com.nes.utils.LogX;

/**
 * {@link FirstRowRecyclerView} itemView
 * @param <T>
 * @author liuqz
 */
public class HomeItemLayout<T> extends AbsItemLayout<T>{

    public HomeItemLayout(Context context){
        super(context);
    }

    public HomeItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 返回横向RecyclerView应该获取焦点的itemView
     * @return
     */
    private View getFirstView(){
        View view = null;
        LogX.d(TAG+" getFirstView mAllShow : "+ mAllShow
                +" isUseNomalRecyclerView : "+ mAdapterPresenter.isUseNomalRecyclerView()
                +" getVisibility : "+ mFirstColumnRecyclerView.getVisibility());
        if (mAllShow){
            boolean isInitState = mFirstColumnRecyclerView.isInitialState();
            LogX.e(TAG+" getFirstView isInitState : "+isInitState+" mFocusHorizontalGridView getChildCount : "+ mFirstColumnRecyclerView.getChildCount());
            view = mFirstColumnRecyclerView.getChildAt(isInitState ? 0 : 1);
        }
        return view;
    }

    @Override
    public View getStartView() {
        return getFirstView();
    }

    @Override
    public void setAbsAdapter(AbsAdapter absAdapter) {
        super.setAbsAdapter(absAdapter);
        if (absAdapter instanceof HomeAdapter){
            mFirstColumnRecyclerView.setFocusCallBack((HomeAdapter)absAdapter);
        }
    }
}
