package com.nes.customgooglelauncher.ui.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.ui.adapter.AbsRecyclerViewItemAdapter;
import com.nes.customgooglelauncher.ui.inter.HRecyclerViewCallback;
import com.nes.customgooglelauncher.ui.inter.HRecyclerViewItemViewCallback;
import com.nes.customgooglelauncher.ui.presenter.AbsAdapterPresenter;
import com.nes.customgooglelauncher.utils.ImageLoaderUtils;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.CornerUtil;
import com.nes.utils.LogX;

/**
 * RecyclerView嵌套中,小RecyclerView的itemView
 * @param <T>
 */
public class RecyclerViewItemLayout<T> extends ConstraintLayout implements HRecyclerViewItemViewCallback {

    private String TAG = RecyclerViewItemLayout.class.getSimpleName();
    private ImageView mImageView,mImageRight,mImageLeft;
    private HomeType mHomeType;
    private DisplayMetrics metrics;
    private TextView mTvBg;
    private CustomTextView mTvName;
    private int matchWidth = -1;
    private int mParentPosition = -1;
    private ProgressBar mProgressBar;
    private boolean isShowName = false;
    //    private ObjectAnimator mObjectAnimator;
    private T mData;
    private AbsRecyclerViewItemAdapter<T> mItemAdapter;

    //    private float animSchedule = 0;
    private int mWidth = -1;
    private int mHeight = -1;

    private boolean isInit = true;

    private boolean isShowProcess = false;

    public RecyclerViewItemLayout(Context context){
        super(context);
        init(context);
    }

    public RecyclerViewItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_recyclerview_item,this,true);
        setClipChildren(false);
        setClipToPadding(false);
        initView();
        setFocusable(true);
        mTvBg.setBackgroundResource(R.drawable.selector_orange_frame);
        mTvName.setVisibility(View.INVISIBLE);
        WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        metrics = new DisplayMetrics();
        if (wm!=null && wm.getDefaultDisplay() != null){
            wm.getDefaultDisplay().getMetrics(metrics);
        }
        showOrHideProgressBar(false);
    }

    private void initView(){
        mImageView = findViewById(R.id.item_img);
        mTvBg = findViewById(R.id.item_tv_bg);
        mTvName = findViewById(R.id.item_tv_name);
        mProgressBar = findViewById(R.id.item_progress);
        mImageRight = findViewById(R.id.item_img_right);
        mImageLeft = findViewById(R.id.item_img_left);
    }

    public AbsRecyclerViewItemAdapter<T> getItemAdapter() {
        return mItemAdapter;
    }

    public void setItemAdapter(AbsRecyclerViewItemAdapter<T> itemAdapter) {
        mItemAdapter = itemAdapter;
    }

    public void setData(T t){
        this.mData = t;
    }

    @Override
    public boolean isLastPosition() {
        return mItemAdapter.isLastPosition(mData);
    }


    @Override
    public HRecyclerViewCallback getParentCallback() {
        HRecyclerViewCallback result = null;
        if (getParentViewGroup(this) instanceof FirstColumnRecyclerView){
            result = (HRecyclerViewCallback)getParentViewGroup(getParentViewGroup(this));
        }
        return result;
    }

    private ViewGroup getParentViewGroup(View view){
        if (view != null && view.getParent() != null && view.getParent() instanceof ViewGroup){
            return (ViewGroup)view.getParent();
        }
        return null;
    }

    @Override
    public int getParentPosition() {
        return mParentPosition;
    }

    @Override
    public boolean isFirstPosition() {
        return mItemAdapter.isFirstPosition(mData);
    }

    public void setParentPosition(int parentPosition) {
        mParentPosition = parentPosition;
    }


    public void setProgressBar(int progress){
        if (progress >= 0 && progress <= 100){
            showOrHideProgressBar(true);
            mProgressBar.setProgress(progress);
        }else{
            showOrHideProgressBar(false);
        }
    }

    public void showOrHideProgressBar(boolean isShowProcess){
        this.isShowProcess = isShowProcess;
        mProgressBar.setVisibility(isShowProcess ? View.VISIBLE : View.GONE);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setClipViewCornerByDp(){
        int clipPx = getContext().getResources().getDimensionPixelSize(R.dimen.home_item_item_image_rate);
        CornerUtil.clipViewCornerByDp(mTvBg,clipPx);
        CornerUtil.clipViewCornerByDp(mImageView,clipPx);
        CornerUtil.clipViewCornerByResId(mProgressBar,R.dimen.home_item_item_progressbar_height);
    }

    public HomeType getHomeType() {
        return mHomeType;
    }

    /**
     * 设置该ViewGroup的size大小
     * @param width 宽
     * @param height 高
     * @param absAdapterPresenter {@link AbsAdapterPresenter}
     *
     * @see AbsAdapterPresenter#getItemBottomPadding()
     */
    public void setLayoutSize(int width, int height, AbsAdapterPresenter absAdapterPresenter){
        setLayoutSize(width,height,(absAdapterPresenter == null ? 0 : absAdapterPresenter.getItemBottomPadding()));
    }

    public void setLayoutSize(int width, int height, int paddingBottom){
        if (mWidth == -1){
            mWidth = width;
        }
        if (mHeight == -1){
            mHeight = height;
        }
        ViewGroup.LayoutParams thisLayoutParams = getLayoutParams();
        if (thisLayoutParams.width != width || thisLayoutParams.height != (height + paddingBottom)) {
            thisLayoutParams.width = width;
            thisLayoutParams.height = height + paddingBottom;
            setLayoutParams(thisLayoutParams);
        }

        ViewGroup.LayoutParams bgLayoutParams = mTvBg.getLayoutParams();
        if (bgLayoutParams.width != mWidth || bgLayoutParams.height != height) {
            bgLayoutParams.width = mWidth;
            bgLayoutParams.height = height;
            mTvBg.setLayoutParams(bgLayoutParams);
        }

        int widthRL = Utils.getDimension(getContext(),R.dimen.home_item_right_width);
        int heightRL = (int)(height/1.18f);
        ViewGroup.LayoutParams imageRightLayoutParams = mImageRight.getLayoutParams();
        if (imageRightLayoutParams.width != widthRL || imageRightLayoutParams.height != heightRL) {
            imageRightLayoutParams.width = widthRL;
            imageRightLayoutParams.height = heightRL;
            mImageRight.setLayoutParams(imageRightLayoutParams);
        }

        ViewGroup.LayoutParams imageLeftLayoutParams = mImageLeft.getLayoutParams();
        if (imageLeftLayoutParams.width != widthRL || imageLeftLayoutParams.height != heightRL) {
            imageLeftLayoutParams.width = widthRL;
            imageLeftLayoutParams.height = heightRL;
            mImageLeft.setLayoutParams(imageLeftLayoutParams);
        }
    }

    /**
     * 设置该ViewGroup的focus状态,
     * 主要是左边阴影和右边阴影
     * @param hasFocus 是否有焦点
     */
    public void setFocusState(boolean hasFocus){
//       if (hasFocus){
//           getAnimator().start();
//       }else{
//           getAnimator().reverse();
//       }
        if (hasFocus){
            mImageLeft.setVisibility(isFirstPosition() ? View.GONE : View.VISIBLE);
            mImageRight.setVisibility(isLastPosition() ? View.GONE : View.VISIBLE);
        }else{
            mImageLeft.setVisibility(View.GONE);
            mImageRight.setVisibility(View.GONE);
        }
    }

//    public float getAnimSchedule() {
//        return animSchedule;
//    }
//
//    public void setAnimSchedule(float animSchedule) {
//        this.animSchedule = animSchedule;
//        setPivotX(0);
//        setPivotY(getHeight()/2);
//        setLayoutSize((int)(mWidth * animSchedule),mHeight);
//        setScaleX(animSchedule);
//        setScaleY(animSchedule);
//    }

//    private ObjectAnimator getAnimator(){
//        if (mObjectAnimator == null){
//            LogX.d(TAG+" getAnimator mWidth : "+mWidth);
//            mObjectAnimator = ObjectAnimator.ofFloat(this,"animSchedule",1,Constants.Scale);
//            mObjectAnimator.setDuration(200);
//        }
//        return mObjectAnimator;
//    }

    /**
     * 横向充满屏幕的模式
     * @param absAdapterPresenter {@link AbsAdapterPresenter}
     * @param columns 列数
     */
    public void setLayoutSizeMatch(AbsAdapterPresenter absAdapterPresenter,int columns){
        if (matchWidth == -1){
            int padding = Utils.getDimension(getContext(),R.dimen.home_out_padding_start);
            int tem = Utils.getDimension(getContext(),R.dimen.home_item_item_magin1);
            matchWidth = (metrics.widthPixels - 2 * padding - (columns-1) * tem)/columns;
        }
        int height = Utils.getDimension(getContext(),R.dimen.home_item_item_type4_height);
        setLayoutSize(matchWidth,height,absAdapterPresenter);
    }

    /**
     * 焦点模式下,是否显示name or title
     * @param isVisible 是否显示
     */
    public void setNameVisible(boolean isVisible){
        isShowName = isVisible;
        mTvName.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        mTvName.setSelected(isVisible);
    }

    public void setHomeType(HomeType homeType) {
        isInit = false;
        mHomeType = homeType;
//        goneProgressBar();
    }

    public void bindName(String name){
        if (mTvName != null){
            mTvName.setText(name);
        }
    }

    public void bindPkgName(String pkgName){
        LogX.d(TAG+" bindPkgName pkgName/url : "+pkgName);
        ImageLoaderUtils.imageLoaderDisplayImagePkg(mImageView,pkgName);
    }

    public void bindHttpUrl(String url){
        ImageLoaderUtils.imageLoaderDisplayImageHttp(mImageView,url);
    }

    public void bindUri(Uri uri){
        ImageLoaderUtils.imageLoaderDisplayImageUri(mImageView,uri);
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    /**
     * 当该ViewGroup被回收时,解绑或置空该View的部分变量
     *
     * @see AbsRecyclerViewItemAdapter#onBaseViewRecycled(AbsRecyclerViewItemAdapter.AbsRecyclerViewItemViewHolder)
     */
    public void unBind(){
        showOrHideProgressBar(false);
        mHomeType = null;
        isInit = true;
        matchWidth = -1;
        mParentPosition = -1;
        mWidth = -1;
        mHeight = -1;
        mData = null;
        mItemAdapter = null;
    }

    /**
     * 设置移动模式,打开移动模式对应的动画
     * @param isMoveModle
     */
    public void setMoveModle(boolean isMoveModle){
        LogX.d(TAG+" setMoveModle isMoveModle");
        if (isMoveModle){
            mImageView.setForeground(getContext().getResources().getDrawable(R.drawable.fav_move_fram));
            ((AnimationDrawable) mImageView.getForeground()).start();
        }else{
            if (null != mImageView.getAnimation()) {
                mImageView.getAnimation().cancel();
            }
            mImageView.setForeground(null);
        }
    }

}
