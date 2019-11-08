package com.nes.customgooglelauncher.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.PopBean;
import com.nes.customgooglelauncher.ui.adapter.PvrAdapter;
import com.nes.customgooglelauncher.utils.Constants;
import com.nes.utils.CornerUtil;
import com.nes.utils.LogX;

import java.util.List;

/**
 * 长按显示的{@link PopupWindow}
 * @author liuqz
 */
public class CustomPopupWindow extends PopupWindow {


    private RecyclerView mRecyclerView;
    private ImageView mIv, mIvPress;

    private int[] screen = new int[2];

    private final int padding = 10;
    private final PvrAdapter adapter;
    private boolean isBottom;

    public void setPopItemClick(PvrAdapter.PopItemClick popItemClick) {
        adapter.setPopItemClick(popItemClick);
    }


    public void setBottomShow(boolean isBottom) {
        this.isBottom = isBottom;
    }

    @SuppressLint("WrongConstant")
    public CustomPopupWindow(Context mContext, List<PopBean> popBeans) {
        WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        screen[0] = metrics.widthPixels;
        screen[1] = metrics.heightPixels;
        setWidth(ConstraintLayout.LayoutParams.MATCH_PARENT);
        setHeight(ConstraintLayout.LayoutParams.MATCH_PARENT);
        setAnimationStyle(R.style.PopStyle);
        setFocusable(true);
        setBackgroundDrawable(mContext.getResources().getDrawable(R.color.custom_pop_bg));
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.pop_prv, null);
        mRecyclerView =  inflate.findViewById(R.id.rcy_option);
        mIv =  inflate.findViewById(R.id.iv_view);
        mIvPress =  inflate.findViewById(R.id.iv_press);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        CornerUtil.clipViewCornerByDp(mRecyclerView, 4);
        CornerUtil.clipViewCornerByDp(mIv, 4);
        adapter = new PvrAdapter(popBeans);
        mRecyclerView.setAdapter(adapter);
        setContentView(inflate);
    }

    private View mView;
    private float scale = Constants.SCALE;

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setView(View view) {
        mView = view;
        final int[] location = new int[2];
        view.getLocationOnScreen(location);
        int width = (int) (view.getWidth() * scale);
        int height = (int) (view.getHeight() * scale);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(width, height);
        mIv.setLayoutParams(layoutParams1);
        LogX.i("Location:" + location[0] + "  " + location[1]+" width : "+width+" hegiht : "+height);
        adjustLocation(location, width, height);
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) mIv.getLayoutParams();
        layoutParams2.setMargins(location[0], location[1], 0, 0);
    }

    public void setPosterImageView(Drawable drawable) {
        mIv.setImageDrawable(drawable);
    }


    public void show() {
        showAsDropDown(mView);
    }

    private void adjustLocation(final int[] location, final int width, final int height) {
//        TransitionManager.beginDelayedTransition();
        int thresholdx = 340;
        int limitx = screen[0] - thresholdx;
        int thresholdy = 340;
        int limity = screen[1] - thresholdy;
        if (isBottom) {
            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    LogX.i(" 上显示 左对齐 mRecyclerView:" + mRecyclerView.getWidth() + " height: " + mRecyclerView.getHeight());
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();
                    layoutParams.setMargins(location[0], location[1] + height + padding, 0, 0);
                    mRecyclerView.setLayoutParams(layoutParams);
                }
            });


            mIvPress.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mIvPress.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvPress.getLayoutParams();
                    mIvPress.setRotation(0);
                    layoutParams.setMargins(location[0] + 15, location[1] + height + 3, 0, 0);
                    mIvPress.setLayoutParams(layoutParams);
                }
            });
            return;
        }


        //上显示 右对齐
        if (location[0] > limitx && location[1] >= limity) {


            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    LogX.i(" 上显示 右对齐 mRecyclerView:" + mRecyclerView.getWidth() + " height: " + mRecyclerView.getHeight());


                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();

                    layoutParams.setMargins(location[0] - (mRecyclerView.getWidth() - width), location[1] - mRecyclerView.getHeight() - padding, 0, 0);

                    mRecyclerView.setLayoutParams(layoutParams);


                }
            });

//
            mIvPress.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mIvPress.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvPress.getLayoutParams();
                    mIvPress.setRotation(180);
                    layoutParams.setMargins(location[0] + width - 20, location[1] - 10, 0, 0);

                    mIvPress.setLayoutParams(layoutParams);
                }
            });
            return;

        }
        //上显示 左对齐
        if (location[0] <= limitx && location[1] > limity) {


            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    LogX.i(" 上显示 左对齐 mRecyclerView:" + mRecyclerView.getWidth() + " height: " + mRecyclerView.getHeight());


                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();

                    layoutParams.setMargins(location[0], location[1] - mRecyclerView.getHeight() - padding, 0, 0);

                    mRecyclerView.setLayoutParams(layoutParams);


                }
            });


            mIvPress.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mIvPress.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvPress.getLayoutParams();
                    mIvPress.setRotation(180);
                    layoutParams.setMargins(location[0] + 15, location[1] - 10, 0, 0);

                    mIvPress.setLayoutParams(layoutParams);
                }
            });
            return;
        }
        //右显示
        if (location[0] < limitx && location[1] <= limity) {

            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    LogX.i("右显示 mRecyclerView:" + mRecyclerView.getWidth() + " height: " + mRecyclerView.getHeight());


                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();

                    layoutParams.setMargins(location[0] + width + padding, location[1], 0, 0);

                    mRecyclerView.setLayoutParams(layoutParams);


                }
            });


            mIvPress.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mIvPress.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvPress.getLayoutParams();
                    mIvPress.setRotation(-90);
                    layoutParams.setMargins(location[0] + width + 2, location[1] + 10, 0, 0);

                    mIvPress.setLayoutParams(layoutParams);
                }
            });
            return;
        }
        //左显示
        if (location[0] >= limitx && location[1] < limity) {
            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    LogX.i("左显示 mRecyclerView:" + mRecyclerView.getWidth() + " height: " + mRecyclerView.getHeight());


                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();

                    layoutParams.setMargins(location[0] - mRecyclerView.getWidth() - padding, location[1], 0, 0);

                    mRecyclerView.setLayoutParams(layoutParams);


                }
            });

            mIvPress.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mIvPress.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvPress.getLayoutParams();
                    mIvPress.setRotation(90);
                    layoutParams.setMargins(location[0] - 13, location[1] + 10, 0, 0);

                    mIvPress.setLayoutParams(layoutParams);
                }
            });
        }


    }


}
