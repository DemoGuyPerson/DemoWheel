package com.nes.customgooglelauncher.ui.adapter;

import android.app.Activity;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.nes.base.BaseRecyclerViewAdapter;
import com.nes.base.BaseRunnable;
import com.nes.base.BaseViewHolder;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.inter.AdapterContract;
import com.nes.customgooglelauncher.ui.presenter.AbsAdapterPresenter;
import com.nes.customgooglelauncher.ui.widget.RecyclerViewItemLayout;
import com.nes.customgooglelauncher.utils.BrowseItemFocusHighlight;
import com.nes.utils.LogX;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 嵌套RecyclerView的itemView --> RecyclerView --> AbsRecyclerViewItemAdapter;
 * 里面绝大多数逻辑已经处理,各种可以定义项都已委托{@link AbsAdapterPresenter}及其子类来处理;
 *
 * 对于嵌套RecyclerView各小项的差异性,你需要定制{@link AbsAdapterPresenter}
 *
 * @author liuqz
 *
 * @see AbsAdapterPresenter
 */
public class AbsRecyclerViewItemAdapter<T> extends BaseRecyclerViewAdapter<T, AbsRecyclerViewItemAdapter.AbsRecyclerViewItemViewHolder<T>> implements AdapterContract {

    private BrowseItemFocusHighlight mBrowseItemFocusHighlight;
    private AbsAdapterPresenter<T> mAdapterPresenter;
    /**
     * 是否为移动模式,主要用于长按移动位置的功能
     */
    private boolean mMoveMode = false;
    /**
     * 移动模式下,{@link #mMoveMode == true,}当前选中的layout position
     */
    private int mMoveItemPosition = -1;
    /**
     * 改子RecyclerView --> Adapter 在父RecyclerView的layout position
     */
    private int mParentPosition;
    private AbsAdapter mAbsAdapter;
    /**
     * itemView {@link View#onFocusChanged(boolean, int, Rect)}回调时,
     * 为了针对快速滑动,用Runnable消息的方式做了一个延时
     */
    private FocusRunnable<T> mFocusRunnable = null;
    /**
     * 消息延时时间
     */
    private static final long DELAY_TIME = 400;

    public AbsRecyclerViewItemAdapter() {
        mBrowseItemFocusHighlight = BrowseItemFocusHighlight.newInstance();
    }

    public int getParentPosition() {
        return mParentPosition;
    }

    public void setParentPosition(int parentPosition) {
        mParentPosition = parentPosition;
    }

    /**
     * 设置当前Adapter的代理{@link AbsAdapterPresenter},
     * 很重要,一定要在外层RecyclerView Adapter的{@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}中设置.
     *
     * @param adapterPresenter {@link AbsAdapterPresenter}
     *
     * @see AbsAdapter#onBaseBindViewHolder(AbsAdapter.AbsViewHolder, int)
     * @see AbsAdapter.AbsViewHolder#bind(boolean, AbsItem, int)
     * @see com.nes.customgooglelauncher.ui.widget.AbsItemLayout#bind(boolean, AbsItem, int)
     */
    public void setAdapterPresenter(AbsAdapterPresenter<T> adapterPresenter) {
        mAdapterPresenter = adapterPresenter;
        adapterPresenter.setAdapterContract(this);
    }

    public void setAbsAdapter(AbsAdapter absAdapter) {
        mAbsAdapter = absAdapter;
    }

    @Override
    public boolean isMoveMode(){
        return mAdapterPresenter.isCanMoveMode() && mMoveMode;
    }

    /**
     * 判断当前position对应的itemView是否需要显示为移动模式
     * @param position layout position
     * @return 是否为移动模式
     */
    private boolean isMoveMode(int position) {
        return isMoveMode() && position == mMoveItemPosition;
    }

    @Override
    public void setData(List<T> list) {
        super.setData(list);
        T t = mAdapterPresenter.getAdditionalData();
        if (t != null){
            mList.add(t);
        }
    }

    /**
     * {@linkplain AbsAdapterPresenter#isCanEmpty() 能否支持空项}为true
     * && {@linkplain BaseRecyclerViewAdapter#getRealCount() 最原始mList.size()} == 0 的情况，则{@linkplain #getRealCount() 实际需要展示的Count} == 1;
     *  为空的项腾出位置。
     * @return
     */
    @Override
    public int getRealCount() {
        return mAdapterPresenter.isCanEmpty() && super.getRealCount() == 0 ? 1 : super.getRealCount();
    }

    /**
     * change移动模式
     * @param moveMode 是否为移动模式
     * @param position 位置
     *
     * @see AdapterContract#setMoveMode(boolean, int)
     */
    @Override
    public void setMoveMode(boolean moveMode, int position) {
        LogX.i("setMoveMode moveMode : " + moveMode+ " position : "+position);
        this.mMoveMode = moveMode;
        this.mMoveItemPosition = position;
        notifyItemChanged(position);
    }

    @Override
    public void removePosition(final int position) {
        LogX.d(TAG+" removePosition position : "+position);
        if (getItemCount() > position){
            mList.remove(position - getHeadersCount() - getStartVisibleItemCount());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRemoved(position);
                }
            });
        }
    }

    @Override
    public Activity getActivity() {
        return mAbsAdapter.getActivity();
    }

    @Override
    public void movePosition(final int fromPosition,final int toPosition) {
        mMoveItemPosition = toPosition;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    /**
     * {@link AbsAdapterPresenter#isCanEmpty()}为true && {@link BaseRecyclerViewAdapter#getRealCount()} == 0 的情况，直接返回空
     * @param position layout position
     * @return T
     */
    @Override
    public T getItem(int position) {
        if (mAdapterPresenter.isCanEmpty() && super.getRealCount() == 0){
            return null;
        }
        return super.getItem(position);
    }

    /**
     * 判断当前是不是显示的empty模式(空占位符)
     * @return
     */
    private boolean isEmptyMode(){
        return mAdapterPresenter.isCanEmpty() && super.getRealCount() == 0;
    }

    @Override
    protected int getEndVisibleItemCount() {
        LogX.d(TAG+" getEndVisibleItemCount : "+mAdapterPresenter.getEndVisibleItemCount()+" type : "+mAdapterPresenter.getCurrentHomeType().toString());
        return mAdapterPresenter.getEndVisibleItemCount();
    }

    @Override
    protected AbsRecyclerViewItemViewHolder<T> onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        return new AbsRecyclerViewItemAdapter.AbsRecyclerViewItemViewHolder<T>(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview,parent,false),
                this,mBrowseItemFocusHighlight);
    }

    @Override
    protected void onBaseBindViewHolder(AbsRecyclerViewItemViewHolder<T> holder, int position) {
        T t = getItem(position);
        boolean isEmptyMode = isEmptyMode();
        holder.bind(t);
        if (isEmptyMode){
            mAdapterPresenter.onBindEmptyView(t,position,holder.mRecyclerViewItemLayout,isMoveMode(position));
        }else{
            mAdapterPresenter.onBindView(t,position,holder.mRecyclerViewItemLayout,isMoveMode(position));
        }
    }

    @Override
    protected void onBaseInVisibleBindViewHolder(AbsRecyclerViewItemViewHolder<T> holder, int position) {
        super.onBaseInVisibleBindViewHolder(holder, position);
        LogX.d(TAG+" onBaseInVisibleBindViewHolder position : "+position);
        holder.bindInVisible();
        mAdapterPresenter.onBindInVisibleView(position,holder.mRecyclerViewItemLayout);
    }

    @Override
    protected void onBaseViewRecycled(AbsRecyclerViewItemViewHolder<T> holder) {
        super.onBaseViewRecycled(holder);
        holder.unBind();
    }

    /**
     * 是否是数据集合里面的最后一个
     * @param t t
     * @return 是否是数据集合里面的最后一个
     */
    public boolean isLastPosition(T t){
        return mAdapterPresenter.isLastPosition(t);
    }

    /**
     * 是否是数据集合里面的第一个
     * @param t t
     * @return 是否是数据集合里面的第一个
     */
    public boolean isFirstPosition(T t){
        return mAdapterPresenter.isFirstPosition(t);
    }

    /**
     * itemView {@link View#setOnFocusChangeListener(View.OnFocusChangeListener)}回调
     * @param view focus view
     * @param hasFocus 是否获得焦点
     * @param position layout position
     */
    private void callBackFocus(final View view, boolean hasFocus, final int position){
//        LogX.d(TAG+" callBackFocus hasFocus : "+hasFocus+" position : "
//                +position+" mAbsAdapter != null : "+(mAbsAdapter != null)
//                +" listener != null : "+(mAbsAdapter == null || mAbsAdapter.getItemFocusChangeListener() == null));
        if (hasFocus) {
            mAbsAdapter.getHandler().removeCallbacksAndMessages(null);
            if (mFocusRunnable == null) {
                mFocusRunnable = new FocusRunnable<>(this);
            }
            mFocusRunnable.setContent(view, hasFocus, position, getItem(position));
            mAbsAdapter.getHandler().postDelayed(mFocusRunnable, DELAY_TIME);
        }
    }

    private void callBackClick(View view,int position){
        LogX.d(TAG+" callBackClick position : "+position);
        mAdapterPresenter.onItemClickListener(view,position,getItem(position), isEmptyMode());
    }

    private boolean callBackLongClick(final ImageView view, final int position){
        LogX.d(TAG+" callBackLongClick view : "+view+" position : "+position);
        return mAdapterPresenter.onItemLongClickListener(view,position,getItem(position));
    }

    private boolean callKeyDown(View view,int keyCode, KeyEvent event, int position){
        LogX.d(TAG+" callKeyDown position : "+position);
        return mAdapterPresenter.onItemKeyDown(view,keyCode,event,position,getItem(position));
    }

    static class AbsRecyclerViewItemViewHolder<T> extends BaseViewHolder<AbsRecyclerViewItemAdapter<T>>{

        BrowseItemFocusHighlight mBrowseItemFocusHighlight;
        RecyclerViewItemLayout<T> mRecyclerViewItemLayout;

        AbsRecyclerViewItemViewHolder(View itemView, AbsRecyclerViewItemAdapter<T> absRecyclerViewItemAdapter,
                                             BrowseItemFocusHighlight browseItemFocusHighlight) {
            super(itemView, absRecyclerViewItemAdapter);
            mBrowseItemFocusHighlight = browseItemFocusHighlight;
            mRecyclerViewItemLayout = (RecyclerViewItemLayout<T>)itemView;
            mBrowseItemFocusHighlight.onInitializeView(itemView);
            mRecyclerViewItemLayout.setClipViewCornerByDp();
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (v == null){
                        return;
                    }
                    mBrowseItemFocusHighlight.onItemFocused(v,hasFocus);
                    LogX.d("RecyclerViewItemViewHolder onFocusChange hasFocus : "+hasFocus);
                    mRecyclerViewItemLayout.setFocusState(!getAdapter().isEmptyMode() && hasFocus);
                    if (getAdapter().mAdapterPresenter.isShowItemTitle()){
                        mRecyclerViewItemLayout.setNameVisible(hasFocus);
                    }
                    getAdapter().callBackFocus(v,hasFocus,getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == null){
                        return;
                    }
                    getAdapter().callBackClick(v,getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (v == null){
                        return true;
                    }
                    if (v instanceof RecyclerViewItemLayout){
                        return getAdapter().callBackLongClick(((RecyclerViewItemLayout)v).getImageView(),getAdapterPosition());
                    }
                    return false;
                }
            });
            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (v == null){
                        return true;
                    }
                    return getAdapter().callKeyDown(v,keyCode,event,getAdapterPosition());
                }
            });
        }

        void bind(T t) {
            mRecyclerViewItemLayout.setData(t);
            mRecyclerViewItemLayout.setItemAdapter(getAdapter());
            mRecyclerViewItemLayout.setHomeType(getAdapter().mAdapterPresenter.getCurrentHomeType());
            mRecyclerViewItemLayout.setParentPosition(getAdapter().getParentPosition());
        }

        void bindInVisible(){
            mRecyclerViewItemLayout.setHomeType(getAdapter().mAdapterPresenter.getCurrentHomeType());
//            mRecyclerViewItemLayout.setLayoutSize(getAdapter().mAdapterPresenter.mAbsItem.getWidth(),getAdapter().mAdapterPresenter.mAbsItem.getHeight());
        }

        void unBind(){
            mRecyclerViewItemLayout.unBind();
        }
    }

    static class FocusRunnable<T> extends BaseRunnable<AbsRecyclerViewItemAdapter> {

        WeakReference<View> mViewWeakReference;
        boolean hasFocus;
        int position;
        T t;

        FocusRunnable(AbsRecyclerViewItemAdapter weakReference) {
            super(weakReference);
        }

        void setContent(View view,boolean hasFocus,int position,T t){
            this.mViewWeakReference = new WeakReference<>(view);
            this.hasFocus = hasFocus;
            this.position = position;
            this.t = t;
        }

        View getView(){
            if (mViewWeakReference != null){
                return mViewWeakReference.get();
            }
            return null;
        }

        @Override
        protected void work() {
            if (getContent().mAbsAdapter != null && getContent().mAdapterPresenter != null){
                getContent().mAdapterPresenter.onItemFocusChangeListener(hasFocus,getView(),getContent().mAdapterPresenter.getCurrentHomeType(),t);
            }
        }
    }

}
