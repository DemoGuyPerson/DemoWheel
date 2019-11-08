package com.nes.customgooglelauncher.ui.widget;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author zhurongkun
 */

public class FocusLinearLayoutManager extends LinearLayoutManager {
    public FocusLinearLayoutManager(Context context) {
        super(context);
    }

    public FocusLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public FocusLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public View onInterceptFocusSearch(View focused, int direction) {
        int orientation = getOrientation();
        View itemView = findContainingItemView(focused);
        int nextFocusPosition = getPosition(itemView);
        if (orientation == HORIZONTAL) {
            switch (direction) {
                case View.FOCUS_LEFT:
                    nextFocusPosition--;

                    break;
                case View.FOCUS_RIGHT:
                    nextFocusPosition++;
                    break;
                default:
                    return super.onInterceptFocusSearch(focused, direction);
            }
        } else if (orientation == VERTICAL) {
            switch (direction) {
                case View.FOCUS_DOWN:
                    nextFocusPosition++;
                    break;
                case View.FOCUS_UP:
                    nextFocusPosition--;
                    break;
                default:
                    return super.onInterceptFocusSearch(focused, direction);
            }
        }
        if (nextFocusPosition >= getItemCount()) {
            return focused;
        } else {
            scrollToPosition(nextFocusPosition);
            return super.onInterceptFocusSearch(focused, direction);
        }
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        TopSnappedSmoothScroller topSnappedSmoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        topSnappedSmoothScroller.setTargetPosition(position);
        startSmoothScroll(topSnappedSmoothScroller);
    }

    class TopSnappedSmoothScroller extends LinearSmoothScroller {

        TopSnappedSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return FocusLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return SNAP_TO_START;
        }
    }


}
