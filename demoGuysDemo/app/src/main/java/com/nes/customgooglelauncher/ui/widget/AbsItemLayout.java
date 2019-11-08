package com.nes.customgooglelauncher.ui.widget;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.nes.base.BaseViewHolder;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.adapter.AbsRecyclerViewItemAdapter;
import com.nes.customgooglelauncher.ui.adapter.AbsAdapter;
import com.nes.customgooglelauncher.ui.inter.HRecyclerViewCallback;
import com.nes.customgooglelauncher.ui.presenter.AbsAdapterPresenter;
import com.nes.utils.LogX;
import com.nes.utils.Util;
/**
 * 抽象类,不可直接使用,需继承它;
 * 在嵌套RecyclerView中,是外层RecyclerView的itemView;
 * 一般需包括RecyclerView和title等元素。
 *
 * <pre class="prettyprint">
 *     需要注意的是,该viewGroup下的RecyclerView有两种焦点模式,
 *     分别是{@link FirstColumnRecyclerView#FOCUS_NOMAL_MODE}、{@link FirstColumnRecyclerView#FOCUS_START_MODE}
 *     和{@link FirstColumnRecyclerView#FOCUS_CUSTOM_MODE},
 *     他们是互斥存在的,配置他们的关键代码在{@link AbsAdapterPresenter#isUseNomalRecyclerView()},
 *     默认使用{@link FirstColumnRecyclerView#FOCUS_NOMAL_MODE}
 * </pre>
 * @author liuqz
 */
public abstract class AbsItemLayout<T> extends LinearLayout implements HRecyclerViewCallback {

    protected String TAG = getClass().getSimpleName();
    protected CustomTextView mTitleTv;
    protected FirstColumnRecyclerView mFirstColumnRecyclerView;
    protected AbsRecyclerViewItemAdapter<T> mItemAdapter;
    protected AbsAdapterPresenter<T> mAdapterPresenter;
    protected boolean mAllShow = true;
    protected AbsAdapter mAbsAdapter;
    protected int mPosition;

    public AbsItemLayout(Context context) {
        this(context,null);
    }

    public AbsItemLayout(Context context,AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 该View需要addView的layout id;
     * 子类可重写,达到不一样的布局效果
     * @return layout id
     */
    protected int getContentViewId(){
        return R.layout.view_home_item;
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(getContentViewId(),this,true);
        setClipChildren(false);
        setClipToPadding(false);
        setOrientation(VERTICAL);
        mItemAdapter = new AbsRecyclerViewItemAdapter<>();
        initView();
        initRecyclerView();
    }

    @Override
    public int getPosition() {
        return mPosition;
    }

    /**
     * 初始化各种view,子类可重写
     * @see #getContentViewId()
     */
    protected void initView(){
        mTitleTv = findViewById(R.id.item_title);
        mFirstColumnRecyclerView = findViewById(R.id.item_recyclerView);
    }

    /**
     * 初始化各种RecyclerView,子类可重写
     * @see #getContentViewId()
     */
    protected void initRecyclerView(){
        mFirstColumnRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mFirstColumnRecyclerView.setHasFixedSize(true);
    }

    /**
     * 显示或者隐藏title,受{@link #mAllShow}影响
     * @param isShow
     */
    private void showOrHideTitle(boolean isShow){
        if (mTitleTv != null){
            mTitleTv.setVisibility((isShow && mAllShow) ? View.VISIBLE : View.GONE);
        }
    }

    public HomeType getHomeType(){
        return mAdapterPresenter.getCurrentHomeType();
    }

    public void setAbsAdapter(AbsAdapter absAdapter) {
        mAbsAdapter = absAdapter;
        if (mItemAdapter != null){
            mItemAdapter.setAbsAdapter(absAdapter);
        }
    }

    /**
     * 设置缓存池
     * @param recycledViewPool {@link RecyclerView.RecycledViewPool}
     */
    public void setRecycledViewPool(RecyclerView.RecycledViewPool recycledViewPool){
        mFirstColumnRecyclerView.setRecycledViewPool(recycledViewPool);
    }

    /**
     * 设置此ViewGroup的RecyclerView的使用模式;
     * 默认使用{@link AbsAdapterPresenter#isUseNomalRecyclerView()}来判断
     * @param isUserNomalRecyclerView 是否使用正常焦点模式的RecyclerView
     */
    public void setRecyclerModel(boolean isUseNomalRecyclerView,boolean isUseCustomLayoutManager){
        LogX.d( " focusSearch setRecyclerModel isUseNomalRecyclerView : "+isUseNomalRecyclerView+" view : "+this);
        if (isUseCustomLayoutManager){
            mFirstColumnRecyclerView.setCustomMode();
        }else{
            mFirstColumnRecyclerView.setNomalMode(isUseNomalRecyclerView);
        }
    }

    /**
     * bind设置数据
     * @param isShow 是否显示布局
     * @param absItem 对应的数据源,包括子RecyclerView的数据
     * @param position layout position
     *
     * @see AbsAdapter#onBaseBindViewHolder(AbsAdapter.AbsViewHolder, int)
     * @see AbsAdapter.AbsViewHolder#bind(boolean, AbsItem, int)
     */
    public void bind(boolean isShow, AbsItem<T> absItem, int position){
        LogX.e("FirstRowRecyclerView bind type : "+absItem.getHomeType().toString()
                +" title : "+absItem.getTitle()+" position : "+position+" isShow : "+isShow);
        setViewVisibility(isShow);
        mTitleTv.setText(absItem.getTitle());
        mPosition = position;
        mItemAdapter.setParentPosition(position);
        if (isShow){
            LogX.d(TAG+" bind list is no null : "+ Util.isNoEmptyList(absItem.getSourceList())+" mHomeType : "+absItem.getHomeType());
//            mAdapter.setList(list);
//            mFocusHorizontalGridView.setAdapter(mAdapter);
            mItemAdapter.setAdapterPresenter(absItem.getAdapterPresenter());
            mItemAdapter.setData(absItem.getSourceList());
            getRecyclerView().swapAdapter(mItemAdapter,true);
        }
    }

    /**
     * 解绑,用于RecyclerView移除屏幕外时,解除掉一些绑定关系
     *
     * @see AbsAdapter#onViewRecycled(BaseViewHolder)
     */
    public void unBind(){
        getRecyclerView().setAdapter(null);
        getRecyclerView().removeAllViews();
        mFirstColumnRecyclerView.destroy();
        mAdapterPresenter = null;
        mPosition = -1;
        mAllShow = true;
    }

    public boolean isNomalType(){
        return mAdapterPresenter.isUseNomalRecyclerView();
    }

    /**
     * 设置此ViewGroup对应的{@link AbsAdapterPresenter},可以用它
     * 获取一些配置信息,以及一些之后的set工作
     * @param adapterPresenter {@link AbsAdapterPresenter}
     *
     * @see #showOrHideTitle(boolean)
     * @see #setRecyclerModel(boolean)
     * @see #setLayoutSize()
     */
    public void setAdapterPresenter(AbsAdapterPresenter<T> adapterPresenter) {
        mAdapterPresenter = adapterPresenter;
        showOrHideTitle(mAdapterPresenter.isShowRowTitle());
        setRecyclerModel(mAdapterPresenter.isUseNomalRecyclerView(),mAdapterPresenter.isUseCustomLayoutManager());
        setLayoutSize();
    }

    public RecyclerView getRecyclerView(){
        return mFirstColumnRecyclerView;
    }

    /**
     * 设置ViewGroup的大小;
     * 具体是{@link AbsItem#getHeight()}+{@link AbsAdapterPresenter#getItemBottomPadding()}
     */
    protected void setLayoutSize(){
        ViewGroup.LayoutParams layoutParams = getRecyclerView().getLayoutParams();
        int newHeight = mAdapterPresenter.mAbsItem.getHeight() + mAdapterPresenter.getItemBottomPadding();
        if (layoutParams.height != newHeight){
            layoutParams.height = mAdapterPresenter.mAbsItem.getHeight() + mAdapterPresenter.getItemBottomPadding();
            getRecyclerView().setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置当前ViewGroup是否显示
     * @param isShow 是否显示
     */
    public void setViewVisibility(boolean isShow){
        mAllShow = isShow;
        if (isShow){
            if (mTitleTv.getVisibility() == View.GONE){
                showOrHideTitle(mAdapterPresenter.isShowRowTitle());
            }
            if (getRecyclerView().getVisibility() == View.GONE){
                LogX.d(TAG+" setViewVisibility getRecyclerView() : "+getRecyclerView());
                getRecyclerView().setVisibility(View.VISIBLE);
            }
        }else{
            if (mTitleTv.getVisibility() == View.VISIBLE){
                mTitleTv.setVisibility(View.GONE);
            }
            if (getRecyclerView().getVisibility() == View.VISIBLE){
                getRecyclerView().setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean isAllShow(){
        return mAllShow;
    }

    /**
     * 获取当前RecyclerView的滑动状态
     * @return
     *
     * @see AbsAdapter#onBaseViewDetachedFromWindow(AbsAdapter.AbsViewHolder)
     * @see AbsAdapter.AbsViewHolder#getRecyclerViewParcelable()
     */
    public Parcelable getRecyclerViewParcelable(){
        if (getRecyclerView() != null && getRecyclerView().getLayoutManager() != null &&
                isShouldSetParcelable() ){
            return getRecyclerView().getLayoutManager().onSaveInstanceState();
        }
        return null;
    }

    private boolean isShouldSetParcelable(){
        return mFirstColumnRecyclerView.isShouldSetParcelable();
    }

    /**
     * 设置当前RecyclerView的滑动状态,与{@link #getRecyclerViewParcelable()}对应
     * @param parcelable
     *
     * @see AbsAdapter#onBaseViewAttachedToWindow(AbsAdapter.AbsViewHolder)
     * @see AbsAdapter.AbsViewHolder#setRecyclerViewRestoreInstanceState(Parcelable)
     */
    public void setRecyclerViewRestoreInstanceState(Parcelable parcelable){
        mFirstColumnRecyclerView.onCustomRestoreInstanceState(parcelable);
    }

    public AbsRecyclerViewItemAdapter<T> getAdapter() {
        return mItemAdapter;
    }

    public void notifyItemRemoved(int position){
        boolean isShouldMove = false;
        //todo:因为数据在之前已经remove了,所以这里不需要减一
        if (position == mItemAdapter.getRealCount()){
            if (!mAdapterPresenter.isUseNomalRecyclerView()){
                isShouldMove = true;
                mFirstColumnRecyclerView.notifyItemRemoved(position);
            }
        }
        if (!isShouldMove){
            mItemAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    public View getStartView() {
        return null;
    }

    @Override
    public int getViewHeight() {
        return getHeight();
    }

    @Override
    public FirstColumnRecyclerView getItemRecyclerView() {
        return mFirstColumnRecyclerView;
    }
}
