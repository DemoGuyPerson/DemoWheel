package com.nes.customgooglelauncher.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nes.customgooglelauncher.R;
import com.nes.utils.LogX;

/**
 * 首页的{@link ConstraintLayout}
 * 按下选择焦点的时候，会选择RecyclerView的第一行第一个View作为Focus View
 * @author liuqz
 */
public class FocusConstraintLayout extends ConstraintLayout {

    private String TAG = FocusConstraintLayout.class.getSimpleName();
    private FocusCallBack mFocusCallBack;

    public void setFocusCallBack(FocusCallBack focusCallBack) {
        mFocusCallBack = focusCallBack;
    }

    public interface FocusCallBack{
        /**
         * 按"下"键时,寻找下一个焦点View
         * @return next focus View
         */
        View getNextDownFocusView();

        /**
         * 当前焦点是否为屏幕最顶端的几个view之一
         * @param view current focus view
         * @return
         */
        boolean isTopFocusView(View view);
    }

    public FocusConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        LogX.d(TAG+" focusSearch direction : "+direction);
        if (direction == FOCUS_DOWN){
            if (mFocusCallBack != null){
                LogX.d(TAG+" focusSearch FOCUS_DOWN isTopFocusView : "+mFocusCallBack.isTopFocusView(focused));
                if (mFocusCallBack.isTopFocusView(focused)){
                    View nextView = mFocusCallBack.getNextDownFocusView();
                    LogX.d(TAG+" focusSearch FOCUS_DOWN nextView : "+nextView);
                    if (nextView != null){
                        return nextView;
                    }
                }
            }
        } else if(direction == FOCUS_RIGHT){
            if (focused.getId() == R.id.img_setting){
                return focused;
            }
        }
        return super.focusSearch(focused, direction);
    }

    @Override
    public View focusSearch(int direction) {
        LogX.d(TAG+" focusSearch1 direction : "+direction);
        return super.focusSearch(direction);
    }
}
