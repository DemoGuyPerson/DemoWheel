package com.nes.base;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于{@link RecyclerView.Adapter}的封装,扩展部分实用功能;
 * <p>
 *     <li>增加少量头部布局和尾部布局,数量最好不要过多</li>
 *     <li>支持首尾增加空的"占位"项,它们不会获取焦点;{@link #getStartVisibleItemCount()}、{@link #getEndVisibleItemCount()}</li>
 *     <li>使用它之前,ViewHolder必须使用{@link BaseViewHolder},因为{@link BaseViewHolder}消除了内部类ViewHolder默认持有Adapter的隐患</li>
 * </p>
 * @author liuqz
 */
public abstract class BaseRecyclerViewAdapter<T,VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    protected String TAG = this.getClass().getSimpleName();
    private static final int HEADER_FLAG = 0x10000;
    private static final int FOOTER_FLAG = 0x20000;
    private static final int VISIBLE_FLAG = 0x30000;
    protected RecyclerView mRecyclerView;


    private SparseArrayCompat<View> mHeadViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    /**
     * 主线程Handler
     */
    protected Handler mHandler;


    public BaseRecyclerViewAdapter(){
//        setHasStableIds(true);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 抽象方法,用于其子类返回具体的{@link BaseViewHolder}
     * @param parent ViewGroup
     * @param viewType viewType
     * @return {@link BaseViewHolder}
     */
    protected abstract VH onBaseCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * 抽象方法,用于其子类实现具体的Bind View操作
     * @param holder {@link BaseViewHolder}
     * @param position layout position
     */
    protected abstract void onBaseBindViewHolder(VH holder, int position);

    /**
     * 当有空的"占位项"时,其子类将不会被回调{@link #onBaseBindViewHolder(BaseViewHolder, int)};
     * 而是回调{@link #onBaseInVisibleBindViewHolder(BaseViewHolder, int)}
     * @param holder {@link BaseViewHolder}
     * @param position layout position
     */
    protected void onBaseInVisibleBindViewHolder(VH holder, int position){

    }

    /**
     * 默认的viewType,如果其子类想构建不一样的viewType,则不需要重写{@link RecyclerView.Adapter#getItemViewType(int)};
     * 而是需重写{@link #getBaseItemViewType(int)}
     * @param position layout position
     * @return viewType
     */
    protected int getBaseItemViewType(int position){
        return 0;
    }

    /**
     * 如果其子类想对itemView添加到屏幕时设置监听,则不需要重写{@link RecyclerView.Adapter#onViewAttachedToWindow(RecyclerView.ViewHolder)};
     * 而是需重写{@link #onBaseViewAttachedToWindow(BaseViewHolder)}
     * @param holder {@link BaseViewHolder}
     */
    protected void onBaseViewAttachedToWindow(VH holder){

    }

    /**
     * 如果其子类想对itemView移除到屏幕时设置监听,则不需要重写{@link RecyclerView.Adapter#onViewDetachedFromWindow(RecyclerView.ViewHolder)};
     * 而是需重写{@link #onBaseViewDetachedFromWindow(BaseViewHolder)}
     * @param holder {@link BaseViewHolder}
     */
    protected void onBaseViewDetachedFromWindow(VH holder){

    }

    /**
     * 如果其子类想对itemView回收时设置监听,则不需要重写{@link RecyclerView.Adapter#onViewRecycled(RecyclerView.ViewHolder)};
     * 而是需重写{@link #onBaseViewRecycled(BaseViewHolder)}
     * @param holder {@link BaseViewHolder}
     */
    protected void onBaseViewRecycled(VH holder){

    }

    protected List<T> mList;

    public List<T> getList() {
        return mList;
    }

    /**
     * 设置数据源,但不会引用之前的数据对象地址
     * @param list old data
     */
    public void setList(List<T> list){
        if (Util.isNoEmptyList(list)) {
            mList = new ArrayList<>(list);
        }else{
            mList = new ArrayList<>();
        }
    }

    /**
     * 设置数据源,会引用之前的数据对象地址
     * @param list this data
     */
    public void setData(List<T> list){
        mList = list;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    /**
     * 获取空的"占位"项的item count,用于焦点固定在左边的形式,默认为0
     * @return item count
     */
    protected int getEndVisibleItemCount(){
        return 0;
    }

    /**
     * 获取空的"占位"的item count，用于焦点固定在左边的形式，默认为0
     * @return item count
     */
    protected int getStartVisibleItemCount(){
        return 0;
    }

    /**
     * 实际显示数据的大小
     * @return item count
     */
    public int getRealCount() {
        return !Util.isNoEmptyList(mList) ? 0 : mList.size();
    }

    /**
     * 头布局的数目
     * @return item count
     */
    public int getHeadersCount() {
        return mHeadViews.size();
    }

    /**
     * 尾布局的数目
     * @return item count
     */
    public int getFootCount() {
        return mFootViews.size();
    }

    private boolean isHeadViewPosition(int position) {
        return position >= getStartVisibleItemCount() &&  position < getHeadersCount() + getStartVisibleItemCount();
    }

    private boolean isFootViewPosition(int position) {
        int before = getStartVisibleItemCount() + getHeadersCount() + getRealCount();
        return position >= before && position < before + getFootCount();
    }

    private boolean isRealPosition(int position){
        int before = getStartVisibleItemCount() + getHeadersCount();
        return position >= before && position < before + getRealCount();
    }

    private boolean isVisiblePosition(int position){
        return position < getStartVisibleItemCount() || position >= getStartVisibleItemCount() + getHeadersCount() + getRealCount() + getFootCount();
    }

    public void addHeaderView(View view) {
        mHeadViews.put(mHeadViews.size() + HEADER_FLAG, view);
    }
    public void addFootView(View view) {
        mFootViews.put(mFootViews.size() + FOOTER_FLAG, view);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeadViews.get(viewType) != null) {
            HeaderFootViewHolder headerViewHolder = new HeaderFootViewHolder(mHeadViews.get(viewType),this);
            return (VH) headerViewHolder;
        } else if (mFootViews.get(viewType) != null) {
            HeaderFootViewHolder footViewHolder = new HeaderFootViewHolder(mFootViews.get(viewType),this);
            return (VH) footViewHolder;
        }
        LogX.d(TAG+" onCreateViewHolder is OTHER viewType ："+viewType);
        return onBaseCreateViewHolder(parent,viewType);
    }

    public T getItem(int position){
        T t = null;
        if (getRealCount() > 0 && isRealPosition(position)){
            t = mList.get(position - getHeadersCount() - getStartVisibleItemCount());
        }
        return t;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        LogX.d(TAG+" onBindViewHolder position : "+position);
        if (!holder.isAdapterNoEmpty()){
            holder.setAdapter(this);
        }
        if (isHeadViewPosition(position)){
            LogX.e(TAG+" onBindViewHolder position : "+position+" isHeadViewPosition");
            return;
        }
        if (isFootViewPosition(position)){
            LogX.e(TAG+" onBindViewHolder position : "+position+" isFootViewPosition");
            return;
        }
        if (getItemViewType(position) != VISIBLE_FLAG){
            LogX.e(TAG+" onBindViewHolder position : "+position+" != VISIBLE_FLAG");
            holder.toggleVisibility(true);
            onBaseBindViewHolder(holder,position);
        }else{
            LogX.e(TAG+" onBindViewHolder position : "+position+" == VISIBLE_FLAG");
            holder.toggleVisibility(false);
            onBaseInVisibleBindViewHolder(holder,position);
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = getStartVisibleItemCount() + getRealCount()+getHeadersCount()+getFootCount()+ getEndVisibleItemCount();
        LogX.d(TAG+" getItemCount : "+itemCount);
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeadViewPosition(position)) {
            LogX.e(TAG+" getItemViewType isHeadViewPosition position : "+position);
            return mHeadViews.keyAt(position);
        } else if (isFootViewPosition(position)) {
            LogX.e(TAG+" getItemViewType isFootViewPosition position : "+position);
            return mFootViews.keyAt(position - getHeadersCount() - getRealCount());
        } else if(isVisiblePosition(position)){
            LogX.e(TAG+" getItemViewType VISIBLE_FLAG position : "+position);
            return VISIBLE_FLAG;
        }
        return getBaseItemViewType(position);
    }

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        LogX.e(TAG+" onViewRecycled position : "+ holder.getAdapterPosition());
        holder.destroy();
        if (!(holder instanceof HeaderFootViewHolder)){
            onBaseViewRecycled(holder);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VH holder) {
        super.onViewAttachedToWindow(holder);
        LogX.d(TAG+" onViewAttachedToWindow position : "+holder.getAdapterPosition()+" instanceof : "+(holder instanceof HeaderFootViewHolder));
        if (!(holder instanceof HeaderFootViewHolder)){
            onBaseViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VH holder) {
        super.onViewDetachedFromWindow(holder);
        LogX.d(TAG+" onViewDetachedFromWindow position : "+holder.getAdapterPosition()+" instanceof : "+(holder instanceof HeaderFootViewHolder));
        if (!(holder instanceof HeaderFootViewHolder)){
            onBaseViewDetachedFromWindow(holder);
        }
    }

    public static class HeaderFootViewHolder extends BaseViewHolder<BaseRecyclerViewAdapter>{

        public HeaderFootViewHolder(View itemView, BaseRecyclerViewAdapter baseRecyclerViewAdapter) {
            super(itemView, baseRecyclerViewAdapter);
        }
    }

}
