package com.nes.customgooglelauncher.ui.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.nes.base.BaseRecyclerViewAdapter;
import com.nes.base.BaseViewHolder;
import com.nes.customgooglelauncher.mvp.presenter.AdapterHomePagePresenter;
import com.nes.customgooglelauncher.ui.presenter.AbsAdapterPresenter;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.widget.AbsItemLayout;
import com.nes.customgooglelauncher.ui.inter.Loader;
import com.nes.customgooglelauncher.ui.widget.FirstRowRecyclerView;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 嵌套RecyclerView的外层RecyclerView的AbsAdapter;
 * 抽象类,不可直接使用,需继承它,具体业务逻辑写在其子类。
 * @author liuqz
 */
public abstract class AbsAdapter extends BaseRecyclerViewAdapter<AbsItem, AbsAdapter.AbsViewHolder> {

    /**
     * 小项RecyclerView load动作的具体实现者
     */
    private AdapterHomePagePresenter mPresenter;
    /**
     * 创建一个{@link RecyclerView.RecycledViewPool},
     * 根据需求看是否需要为所有小RecyclerView共用一个{@link RecyclerView.RecycledViewPool};
     * 默认开启共用
     */
    private RecyclerView.RecycledViewPool mRecycledViewPool;
    /**
     * 用于标记小RecyclerView是否处于loading状态
     */
    private Map<AbsItem, Boolean> isLoadingMap = new ConcurrentHashMap<>();
    /**
     * 用于标记每个小RecyclerView的滑动状态,
     * RecyclerView复用的时候会还原其滑动状态
     */
    private Map<AbsItem,Parcelable> mScrollStates = new HashMap<>();
    /**
     * 持有使用其自身的弱引用
     */
    private WeakReference<Activity> mReference;

    AbsAdapter(){
        super();
        mPresenter = new AdapterHomePagePresenter();
        mRecycledViewPool = new RecyclerView.RecycledViewPool();
        mRecycledViewPool.setMaxRecycledViews(getBaseItemViewType(0),50);
    }

    /**
     * 抽象方法,{@link AbsAdapter}子类必须实现该方法;
     * 对于嵌套RecyclerView来说，它应该包裹一个子RecyclerView布局
     * @return item layout id
     */
    public abstract int getItemResId();

    Handler getHandler() {
        return mHandler;
    }

    public void setActivity(Activity activity){
        mReference = new WeakReference<>(activity);
    }

    public Activity getActivity(){
        if (mReference != null){
            return mReference.get();
        }
        return null;
    }

    @Override
    public void setList(List<AbsItem> list) {
        super.setList(list);
        initMap();
    }

    /**
     * 增加一个List并刷新界面
     * @param list 数据源
     */
    public void addList(List<AbsItem> list){
        if (getRealCount()==0){
            mList = new ArrayList<>();
        }
        mList.addAll(list);
        refreshMap(list);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    /**
     * 增加一项{@link AbsItem};
     * 并刷新界面
     * @param absItem 增加的数据
     */
    void addAbsItem(AbsItem absItem){
        if (getRealCount()==0){
            mList = new ArrayList<>();
        }
        mList.add(absItem);
        isLoadingMap.put(absItem,false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemInserted(getHeadersCount()+getStartVisibleItemCount()+getRealCount()-1);
            }
        });
    }

    /**
     * 根据{@link mList} 初始化 {@link isLoadingMap}
     */
    private void initMap() {
        if (getRealCount() > 0) {
            isLoadingMap.clear();
            for (AbsItem enter : mList) {
                isLoadingMap.put(enter, false);
            }
        }
    }
    /**
     * 根据传进来的集合刷新 {@link isLoadingMap}
     */
    private void refreshMap(List<AbsItem> data) {
        if (data != null && data.size() > 0) {
            for (AbsItem enter : data) {
                isLoadingMap.put(enter, false);
            }
        }
    }

    /**
     * 数据不为空时，刷新itemView
     * @param absItem
     * @param viewHolder
     * @param position
     */
    private void refreshToAdapter(AbsItem absItem, AbsViewHolder holder, int position){
        holder.bind(true,absItem,position);
    }

    /**
     * 分步加载的方法
     * @param loader {@link AbsAdapterPresenter}
     * @param holder current holder
     * @param position current layout position
     */
    protected void load(Loader loader, AbsViewHolder holder, int position){
        loader.load(mPresenter,holder,position,isLoadingMap);
    }

    @Override
    protected AbsViewHolder onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        LogX.d(TAG+" onBaseCreateViewHolder viewType : "+viewType);
        return new AbsViewHolder(LayoutInflater.from(parent.getContext()).inflate(getItemResId(),parent,false),
                this,mRecycledViewPool,viewType);
    }

    @Override
    protected void onBaseBindViewHolder(AbsViewHolder holder, int position) {
        long startTime = System.currentTimeMillis();
        final AbsItem absItem = getItem(position);
        LogX.e(TAG+" onBaseBindViewHolder type : "+absItem.getHomeType().toString()+" positon : "+position
                +" holder.getAdapterPosition() : "+holder.getAdapterPosition()+" title : "
                +(absItem == null || absItem.getAdapterPresenter() == null ? " null ":absItem.getTitle()));
        if (absItem == null || absItem.getAdapterPresenter() == null){
            holder.setViewVisibilityGone();
            return;
        }
        holder.setAbsAdapterPresenter(absItem.getAdapterPresenter());
        if (!absItem.isClose()) {
            //该项为打开状态下
            if (absItem.getAdapterPresenter().isCanEmpty()) {
                //允许为空的情况下直接根据数据刷新itemView;
                //并且为初始化状态的时候，尝试调用load方法
                refreshToAdapter(absItem, holder, position);
                if (absItem.isInitPage()) {
                    if (isCanLoading(absItem)) {
                        load(absItem.getAdapterPresenter(), holder, position);
                    }
                }
            }else{
                //不允许为空的情况下,先判断是否为初始化状态,是的情况,才会尝试load数据
                //之后马上进行刷新itemView操作;
                //因为load是异步操作，在它完成之后，会根据具体情况是否刷新itemView
                if (absItem.isInitPage()) {
                    if (isCanLoading(absItem)) {
                        load(absItem.getAdapterPresenter(), holder, position);
                    }
                }
                boolean haveData = Util.isNoEmptyList(absItem.getSourceList());
                if (haveData) {
                    refreshToAdapter(absItem, holder, position);
                }else{
                    holder.setViewVisibilityGone();
                }
            }
        }else{
            //该项为关闭状态，直接gone掉布局
            holder.setViewVisibilityGone();
        }
        LogX.d(TAG+" onBaseBindViewHolder type : "+absItem.getHomeType().toString()+" positon : "+position
                +" holder.getAdapterPosition() : "+holder.getAdapterPosition()+" title : "
                +(absItem == null || absItem.getAdapterPresenter() == null ? " null ":absItem.getTitle())+" end time : "
                +(System.currentTimeMillis() - startTime));
    }

    @Override
    protected void onBaseViewRecycled(AbsAdapter.AbsViewHolder holder) {
        super.onBaseViewRecycled(holder);
        LogX.e(TAG+" onBaseViewRecycled posiotion : "+holder.getAdapterPosition());
        holder.unBind();
    }
    @Override
    protected void onBaseViewAttachedToWindow(AbsAdapter.AbsViewHolder holder) {
        super.onBaseViewAttachedToWindow(holder);
        LogX.d(TAG+" onBaseViewAttachedToWindow position : "+holder.getAdapterPosition());
        if(holder.isAllShow() && getItem(holder.getAdapterPosition()) != null){
            Parcelable parcelable = mScrollStates.get(getItem(holder.getAdapterPosition()));
            if (parcelable != null){
                holder.setRecyclerViewRestoreInstanceState(parcelable);
            }else{
                holder.setRecyclerViewRestoreInstanceState(null);
            }
        }
    }

    @Override
    protected void onBaseViewDetachedFromWindow(AbsAdapter.AbsViewHolder holder) {
        super.onBaseViewDetachedFromWindow(holder);
        LogX.d(TAG+" onBaseViewDetachedFromWindow position : "+holder.getAdapterPosition());
        if (holder.isAllShow()){
            AbsItem enter = getItem(holder.getAdapterPosition());
            if (enter != null){
                Parcelable parcelable = holder.getRecyclerViewParcelable();
                if (parcelable != null){
                    mScrollStates.put(enter,parcelable);
                }else{
                    mScrollStates.remove(enter);
                }
            }
        }
    }
    /**
     * 根据{@link #isLoadingMap}是否为false判断是否可以继续Loading
     * @param absItem
     * @return
     */
    private boolean isCanLoading(AbsItem absItem){
        boolean result = false;
        if (absItem!=null){
            if (isLoadingMap!=null && isLoadingMap.get(absItem)!=null && !isLoadingMap.get(absItem)){
                result = true;
            }
        }
        return result;
    }


    /**
     * 关掉某个Channel或者某行，根据类型
     * @param homeType {@link HomeType}
     * @param isClean 是否清楚掉源数据
     */
    public void closeItemByHomeType(HomeType homeType,boolean isClean){
        if (getRealCount() > 0){
            for (int i = 0; i < getItemCount(); i++){
                AbsItem enter = getItem(i);
                if (enter != null && enter.getHomeType() == homeType){
                    enter.reductionPage();
                    enter.setClose(true);
                    if (isClean){
                        enter.getSourceList().clear();
                    }
                    notifyCustomItemChange(i,enter);
                    break;
                }
            }
        }
    }

    /**
     * 根据位置重新请求某一行
     * @param position 位置
     */
    void reRequestByPosition(int position){
        if (getRealCount() > 0 && position  < getRealCount()){
            AbsItem absItem = getItem(position);
            if (absItem != null){
                absItem.reductionPage();
                absItem.setClose(false);
                notifyCustomItemChange(position,absItem);
            }
        }
    }

    /**
     * 重新加载所有数据
     */
    public void reRequestAll(){
        if (getRealCount() > 0){
            for (int i = 0; i < getItemCount(); i++){
                AbsItem absItem = getItem(i);
                if (absItem != null && isCanLoading(absItem)){
                    absItem.reductionPage();
                    absItem.setClose(false);
                }
            }
            notifyCustomDataSetChanged();
        }
    }

    /**
     * 根据类型，重新请求该行。
     * @param homeType 需重新请求的{@link HomeType}
     */
    public void reRequestByHomeType(HomeType homeType){
        LogX.d(TAG+" reRequestByHomeType homeType : "+homeType.toString());
        if (getRealCount() > 0){
            for (int i = 0; i < getItemCount(); i++){
                AbsItem absItem = getItem(i);
                if (absItem != null && absItem.getHomeType() == homeType && isCanLoading(absItem)){
                    absItem.reductionPage();
                    absItem.setClose(false);
                    notifyCustomItemChange(i,absItem);
                    break;
                }
            }
        }
    }

    /**
     * 根据若干类型，重新请求若干行。
     * @param homeType
     */
    public void reRequestByHomeTypes(List<HomeType> homeTypes){
        if (Util.isNoEmptyList(homeTypes)){
            LogX.d(TAG+" reRequestByHomeTypes homeTypes size : "+homeTypes.size());
            if (getRealCount() > 0){
                int sumNum = 0;
                int lastNum = -1;
                for (int i = 0; i < getItemCount(); i++){
                    AbsItem absItem = getItem(i);
                    if (absItem != null && homeTypes.contains(absItem.getHomeType())&& isCanLoading(absItem)){
                        absItem.reductionPage();
                        absItem.setClose(false);
                        sumNum ++;
                        lastNum = i;
                    }
                }
                LogX.d(TAG+" reRequestByHomeTypes sumNum : "+sumNum+" lastNum : "+lastNum);
                if (sumNum == 1){
                    notifyCustomItemChange(lastNum,getItem(lastNum));
                }else if(sumNum > 1){
                    notifyCustomDataSetChanged();
                }
            }
        }
    }

    /**
     * 获取到{@link FirstRowRecyclerView},默认为空
     * @return
     */
    protected FirstRowRecyclerView getCustomRecyclerView(){
        return null;
    }

    /**
     * 全局的刷新,如果{@link #getCustomRecyclerView()}返回不为空;
     * 则调用{@link FirstRowRecyclerView#notifyDataSetChanged()};
     * 否则调用正常的{@link #notifyDataSetChanged()}
     */
    public void notifyCustomDataSetChanged(){
        if (getCustomRecyclerView() != null){
            getCustomRecyclerView().notifyDataSetChanged();
        }else{
            notifyDataSetChanged();
        }
    }


    /**
     * {@link AbsAdapter}对应的ViewHolder,需要是静态内部类;
     * 不默认持有外部内对象
     */
    public static class AbsViewHolder extends BaseViewHolder<AbsAdapter>{
        private AbsItemLayout mAbsItemLayout;

        AbsViewHolder(View itemView, AbsAdapter absAdapter,RecyclerView.RecycledViewPool recycledViewPool,int viewType) {
            super(itemView, absAdapter);
            mAbsItemLayout = (AbsItemLayout)itemView;
            mAbsItemLayout.setAbsAdapter(absAdapter);
            mAbsItemLayout.setRecycledViewPool(recycledViewPool);
        }

        public AbsItemLayout getItemView(){
            return mAbsItemLayout;
        }

        public void bind(boolean isShow, AbsItem absItem, int position){
            mAbsItemLayout.bind(isShow,absItem,position);
        }

        void setAbsAdapterPresenter(AbsAdapterPresenter adapterPresenter){
            mAbsItemLayout.setAdapterPresenter(adapterPresenter);
        }

        boolean isAllShow(){
            return mAbsItemLayout.isAllShow();
        }

        void unBind(){
            mAbsItemLayout.unBind();
        }

        public void setViewVisibilityGone(){
            mAbsItemLayout.setViewVisibility(false);
        }


        Parcelable getRecyclerViewParcelable(){
            return mAbsItemLayout.getRecyclerViewParcelable();
        }

        void setRecyclerViewRestoreInstanceState(Parcelable parcelable){
            mAbsItemLayout.setRecyclerViewRestoreInstanceState(parcelable);
        }
    }


    /**
     * 改变某一项时，刷新View和置空{@link #mScrollStates}对应的数据
     * @param position layout position
     * @param absItem {@link AbsItem}
     */
    void notifyCustomItemChange(final int position,AbsItem absItem){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(position);
            }
        });
        if (absItem != null) {
            mScrollStates.put(absItem, null);
        }
    }

    /**
     * 移除某一项时，刷新View和置空{@link #mScrollStates}对应的数据
     * @param position layout position
     * @param absItem {@link AbsItem}
     */
    void notifyCustomItemReMoved(final int position){
        LogX.d(TAG+" notifyCustomItemReMoved position : "+position);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(position);
            }
        });
        AbsItem enter = getItem(position - getHeadersCount());
        if (enter != null) {
            mScrollStates.put(enter, null);
        }
    }

}
