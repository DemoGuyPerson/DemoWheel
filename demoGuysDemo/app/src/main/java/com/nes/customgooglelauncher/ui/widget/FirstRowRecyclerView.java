package com.nes.customgooglelauncher.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nes.base.BaseRunnable;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.ui.fragment.HomeFragment;
import com.nes.customgooglelauncher.ui.inter.HRecyclerViewCallback;
import com.nes.customgooglelauncher.ui.inter.HRecyclerViewItemViewCallback;
import com.nes.customgooglelauncher.utils.RecyclerViewSoomAnimHelper;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.imageloader.request.BitmapRequest;
import com.nes.utils.LogX;

/**
 * 固定第一行的RecyclerView;
 * 纵向RecyclerView,"劫持"了上下滑动;
 * @author liuqz
 */
public class FirstRowRecyclerView extends RecyclerView implements RecyclerViewSoomAnimHelper.AnimSpeedListener {

    private String TAG = FirstRowRecyclerView.class.getSimpleName();
    private final int INVALID_INDEX = -1;
    private int mPaddingHeight;
    private RecyclerViewSoomAnimHelper mRecyclerViewSoomAnimHelper;
    private long mCanMoveTime = -1L;
    private Handler mHandler;
    private RequestFocusRunnable mRequestFocusRunnable;
    private int mLastFocusPosition = INVALID_INDEX;
    private boolean isResuming = false;
    private boolean isFirstPosition = false;


    public FirstRowRecyclerView(@NonNull Context context) {
        super(context);
    }

    public FirstRowRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mHandler = new Handler();
        mRequestFocusRunnable = new RequestFocusRunnable(this);
        mRecyclerViewSoomAnimHelper = new RecyclerViewSoomAnimHelper(this,LinearLayoutManager.VERTICAL);
        mRecyclerViewSoomAnimHelper.setAnimSpeedListener(this);
        mPaddingHeight = (int)(Utils.getDimension(getContext(), R.dimen.home_recycler_pading_top)/2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean onRequestChildFocus(
                    RecyclerView parent, State state, View child, View focused) {
                // This disables the default scroll behavior for focus movement.
                return true;
            }
        };
        setLayoutManager(linearLayoutManager);
//        OverFlyingLayoutManager overFlyingLayoutManager = new OverFlyingLayoutManager(OrientationHelper.VERTICAL,true);
//        setLayoutManager(overFlyingLayoutManager);
        setHasFixedSize(true);
        setItemViewCacheSize(10);
//        setDrawingCacheEnabled(true);
    }

    public FirstRowRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void notifyDataSetChanged(){
        LogX.i(TAG+" notifyDataSetChanged start...isRunning : "+(mRecyclerViewSoomAnimHelper.isRunning()));
        long currentTime = System.currentTimeMillis();
        if (mRecyclerViewSoomAnimHelper.isRunning()){
            mRecyclerViewSoomAnimHelper.cancelAnimtor();
        }
        long REQUEST_DETAILY_TIME = 2500L;
        mCanMoveTime = currentTime + REQUEST_DETAILY_TIME;
        mHandler.removeCallbacksAndMessages(null);
        long SCROLL_DETAILY_TIME = 2200L;
        mHandler.postDelayed(mRequestFocusRunnable, SCROLL_DETAILY_TIME);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogX.e(TAG+" notifyDataSetChanged start notify...");
                getAdapter().notifyDataSetChanged();
            }
        });
    }

    /**
     * 刷新{@link #mLastFocusPosition}
     */
    public void initLastFocusIndex(){
        mLastFocusPosition = 0;
        isFirstPosition = true;
    }

    /**
     * 刷新{@link #mLastFocusPosition}
     */
    public void setEmptyLastFocusIndex(){
        mLastFocusPosition = INVALID_INDEX;
    }

    public void onPause(){
        isResuming = false;
    }

    public void onResume(){
        isResuming = true;
        checkFocus(true);
    }


    /**
     * 查找当前应该获取焦点的view,以及它所在{@link RecyclerView#getChildCount()}的位置
     * @return length == 2 的数组;index 0 : 它所在{@link RecyclerView#getChildCount()}的位置;
     *                            index 1 : 当前应该获取焦点的view;
     */
    private Object[] getCheckFocusView(){
        LogX.e(TAG+" checkFocus getCheckFocusView start ...isFirstPosition : "+isFirstPosition+" getChildCount() : "+getChildCount());
        Object[] result = null;
        if (getChildCount() <= 0 || mLastFocusPosition == INVALID_INDEX){
            LogX.e(TAG+" checkFocus getCheckFocusView return ...");
            return result;
        }
        result = new Object[2];
        result[0] = -1;
        int index = isFirstPosition ? 0 : 1;
        for (int i = index; i < getChildCount(); i++){
            View view = getChildAt(i);
            if (view instanceof HRecyclerViewCallback) {
                HRecyclerViewCallback hRecyclerViewCallback = (HRecyclerViewCallback) view;
                if (hRecyclerViewCallback.isAllShow()){
                    View view1 = hRecyclerViewCallback.getStartView();
                    if (view1 != null) {
                        result[0] = index;
                        result[1] = view1;
                        break;
                    }
                }else{
//                    LogX.d(TAG+" checkFocus getCheckFocusView i : "+i+" isAllShow == false type : "
//                            +homeItemLayout.getHomeType()+" title : "+homeItemLayout.getTitle());
                }
            }
        }
        LogX.d(TAG+" checkFocus getCheckFocusView result[0] : "+result[0]+" result[1] : "+(result[1] == null ? "null" : result[1]));
        return result;
    }

    /**
     * 检查焦点
     */
    public void checkFocus(boolean isResume){
        int count = getChildCount();
        LogX.d(TAG+" checkFocus start ... count : "+count+" mLastFocusPosition : "
                +mLastFocusPosition+" isResume : "+isResume+" isResuming : "+isResuming);
        if (!isResuming || count <= 0 || mLastFocusPosition == INVALID_INDEX){
            LogX.e(TAG+" checkFocus forgo check"+" isResume : "+isResume);
            return;
        }
        Object[] objects = getCheckFocusView();
        if (objects != null && objects.length == 2){
            int temIndex = objects[0] != null ? (int)objects[0] : -1;
            View nextView = objects[1] instanceof View ? (View)objects[1] : null;
            if (nextView != null && temIndex != -1) {
                int currentIndex = getFocusedChildIndex();
                LogX.d(TAG + " checkFocus currentIndex : " + currentIndex + " mLastFocusPosition : "
                        + mLastFocusPosition + " isResume : " + isResume + " isResuming : " + isResuming + " temIndex : " + temIndex);
                if (temIndex == currentIndex) {
                    //计算出来的焦点和系统找到的焦点一致，那么检查横向的焦点
                    View view = getChildAt(currentIndex);
                    if (view instanceof HRecyclerViewCallback) {
                        HRecyclerViewCallback hRecyclerViewCallback = (HRecyclerViewCallback) view;
                        if (hRecyclerViewCallback.getItemRecyclerView() != null){
                            hRecyclerViewCallback.getItemRecyclerView().checkFocus();
                        }
                    }
                } else {
                    //计算出来的焦点和系统找到的焦点不一致，那么用找到的view获取焦点
                    mLastFocusPosition = temIndex;
                    nextView.requestFocus();
                }
            }
        }
    }

    private int getFocusedChildIndex() {
        for (int i = 0; i < getChildCount(); ++i) {
            View view = getChildAt(i);
            boolean hasFocus = view.hasFocus();
            String type ="";
            if (view instanceof AbsItemLayout){
                type = ((AbsItemLayout)view).getHomeType().toString();
            }
//            LogX.d(TAG+" getFocusedChildIndex i : "+i+" hasFocus : "+hasFocus+" type : "+type);
            if (hasFocus) {
                return i;
            }
        }
        return INVALID_INDEX;
    }


    @Override
    public View focusSearch(int direction) {
//        LogX.d(TAG+" focusSearch1 direction : "+direction);
        return super.focusSearch(direction);
    }
    @Override
    public View focusSearch(View focused, int direction) {
        if (mCanMoveTime != -1 && System.currentTimeMillis() < mCanMoveTime){
            LogX.e(TAG+" focusSearch mCanMoveTime : "+ mCanMoveTime);
            return focused;
        }
        if (direction == FOCUS_DOWN){
            if (focused instanceof HRecyclerViewItemViewCallback){
                isFirstPosition = false;
                HRecyclerViewItemViewCallback hRecyclerViewItemViewCallback = (HRecyclerViewItemViewCallback)focused;
                HRecyclerViewCallback parentCallback = hRecyclerViewItemViewCallback.getParentCallback();
                if (parentCallback!= null){
                    View nextView = super.focusSearch(focused,direction);
                    if (isFootView(nextView)){
                        return nextView;
                    }else {
                        if (parentCallback.getItemRecyclerView() != null && parentCallback.getItemRecyclerView().getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                            if (nextView instanceof HRecyclerViewItemViewCallback){
                                View nextFocusView = nextView;
                                HRecyclerViewCallback hRecyclerViewCallback = ((HRecyclerViewItemViewCallback)nextView).getParentCallback();;
                                if (hRecyclerViewCallback != null){
                                    View nextFirstView = hRecyclerViewCallback.getStartView();
                                    if (nextFirstView != null){
                                        nextFocusView = nextFirstView;
                                    }
                                }
                                if (nextFocusView != null){
                                    int scrollY = parentCallback.getViewHeight() + (parentCallback.getPosition()==0? mPaddingHeight : 0);
//                                   LogX.d(TAG + " FOCUS_DOWN scrollY : " + scrollY);
//                               scrollBy(0, scrollY);
//                                    smoothScrollBy(0, scrollY);
//                               startDownAnim(scrollY);
                                    mRecyclerViewSoomAnimHelper.startPositiveAnim(scrollY);
                                }
                                return nextFocusView;
                            }
                        }
                    }
                }
                return focused;
            }else if(isFootView(focused)){
                return focused;
            }
        }else if(direction == FOCUS_UP){
            View nextView = super.focusSearch(focused,direction);
            View nextFocusView = nextView;
            HRecyclerViewCallback parentCallback = null;
            int scrollY = -1;
            if (nextView instanceof HRecyclerViewItemViewCallback) {
                HRecyclerViewItemViewCallback recyclerViewItemViewCallback = (HRecyclerViewItemViewCallback) nextView;
                parentCallback = recyclerViewItemViewCallback.getParentCallback();
                if (parentCallback != null) {
                    if (parentCallback.getItemRecyclerView() != null && parentCallback.getItemRecyclerView().getScrollState()
                            == RecyclerView.SCROLL_STATE_IDLE) {
                        isFirstPosition = parentCallback.getPosition() == 0;
                        View nextFirstView = parentCallback.getStartView();
                        if (nextFirstView != null){
                            nextFocusView = nextFirstView;
                        }
                        if (!isFootView(focused)){
                            scrollY = -parentCallback.getViewHeight() - (parentCallback.getPosition()==0? (int) (mPaddingHeight * 1.5) : 0);
                        }else{
                            scrollY = -1;
                        }
                    }else{
                        return focused;
                    }
                }
            }
//            LogX.d(TAG+" FOCUS_UP scrollY : "+scrollY);
            if (scrollY < -1){
//                scrollBy(0, scrollY);
//                smoothScrollBy(0, scrollY);
//                startUpAnim(scrollY);
                mRecyclerViewSoomAnimHelper.startNegativeAnim(scrollY);
            }else{
                if (mRecyclerViewSoomAnimHelper.isRunning()){
                    return focused;
                }
            }
            return nextFocusView;
        }
        return super.focusSearch(focused, direction);
    }

    private boolean isFootView(View view){
        boolean result = false;
        if (view != null && view.getTag(BitmapRequest.IMAGE_TAG_KEY) instanceof String){
            String value = (String)view.getTag(BitmapRequest.IMAGE_TAG_KEY);
            result = value.equals(HomeFragment.class.getSimpleName());
        }
        return result;
    }

    @Override
    public void onAnimRunning(int px) {

    }

    @Override
    public void onAnimEnd() {
        mLastFocusPosition = getFocusedChildIndex();
        LogX.d(TAG+" onAnimEnd mLastFocusPosition : "+mLastFocusPosition);
    }

    private void notifyFirstPosition(){
        if (getChildCount() > 0 && getChildAt(0) != null){
            isFirstPosition = getChildAt(0).getTop() >= 0;
        }else{
            isFirstPosition = true;
        }
    }

    static class RequestFocusRunnable extends BaseRunnable<FirstRowRecyclerView> {

        RequestFocusRunnable(FirstRowRecyclerView weakReference) {
            super(weakReference);
        }

        @Override
        protected void work() {
            LogX.d("CustomRecyclerView RequestFocusRunnable work...");
            getContent().notifyFirstPosition();
            getContent().checkFocus(false);
        }
    }

}
