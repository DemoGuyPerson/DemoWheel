package com.nes.customgooglelauncher.ui.presenter;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nes.base.BaseRecyclerViewAdapter;
import com.nes.base.BaseViewHolder;
import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.HomeDetailBean;
import com.nes.customgooglelauncher.mvp.presenter.AdapterHomePagePresenter;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.inter.AdapterContract;
import com.nes.customgooglelauncher.ui.inter.Loader;
import com.nes.customgooglelauncher.ui.adapter.AbsAdapter;
import com.nes.customgooglelauncher.ui.inter.ItemFocusChangeListener;
import com.nes.customgooglelauncher.ui.widget.AbsItemLayout;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.customgooglelauncher.ui.widget.RecyclerViewItemLayout;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.List;
import java.util.Map;

/**
 * 每一行的 小RecyclerView 的 Presenter,用于定制差异化和实现具体bind功能。
 * @author liuqz
 * @param <T> 数据类型
 */
public abstract class AbsAdapterPresenter<T> implements Loader<T> {

    protected String TAG = getClass().getSimpleName();

    AdapterContract mAdapterContract;
    private ItemFocusChangeListener mItemFocusChangeListener;
    public AbsItem<T> mAbsItem;

    public void setAdapterContract(AdapterContract adapterContract) {
        mAdapterContract = adapterContract;
    }


    public void setAbsItem(AbsItem<T> absItem) {
        mAbsItem = absItem;
    }

    public void addItem(T t) {
        mAbsItem.getSourceList().add(t);
    }

    public void setItem(int index,T t){
        mAbsItem.getSourceList().set(index,t);
    }

    public void removeItem(int index){
        mAbsItem.getSourceList().remove(index);
    }

    public void removeItem(T t){
        mAbsItem.getSourceList().remove(t);
    }

    public void setList(List<T> list){
        mAbsItem.setSourceList(list);
    }

    public void setItemFocusChangeListener(ItemFocusChangeListener itemFocusChangeListener) {
        mItemFocusChangeListener = itemFocusChangeListener;
    }

    public HomeType getCurrentHomeType(){
        return mAbsItem.getHomeType();
    }

    /**
     * bind View
     * @param t 数据源
     * @param position 位置
     * @param itemLayout view
     * @param isMoveMode 是否为移动模式,支持移动;与{@link #isCanMoveMode()}保持一致
     */
    public abstract void onBindView(T t, int position, RecyclerViewItemLayout itemLayout,boolean isMoveMode);

    /**
     *  bind empty View
     * @param t 数据源
     * @param position 位置
     * @param itemLayout view
     * @param isMoveMode 是否为移动模式,支持移动;与{@link #isCanMoveMode()}保持一致
     */
    public void onBindEmptyView(T t, int position, RecyclerViewItemLayout itemLayout,boolean isMoveMode){

    }
    /**
     *  bind InVisible View,用于固定焦点的功能，只有当{@link #getEndVisibleItemCount()}大于0时，才会回调。
     * @param position 位置
     * @param itemLayout view
     */
    public void onBindInVisibleView(int position,RecyclerViewItemLayout itemLayout){

    }

    /**
     * 数据为空时，是否显示占位图片
     * @return true 为需要显示占位图片,需重写{@link #onBindEmptyView(Object, int, RecyclerViewItemLayout, boolean)}
     */
    public boolean isCanEmpty(){
        return false;
    }

    /**
     * 是否为第三方推荐类型
     * @return
     */
    public boolean isThirdRecommend(){
        return false;
    }

    /**
     * 是否支持可移动模式
     * @return
     */
    public boolean isCanMoveMode(){
        return false;
    }

    /**
     * 返回右端"空"多少个item,用于焦点定位在左边的功能
     * @return 数目
     *
     * @see BaseRecyclerViewAdapter#getItemCount()
     * @see BaseRecyclerViewAdapter#onCreateViewHolder(ViewGroup, int)
     * @see BaseRecyclerViewAdapter#onBindViewHolder(BaseViewHolder, int)
     */
    public int getEndVisibleItemCount(){
        return 0;
    }

    /**
     * 是否打开行的title
     * @return
     *
     * @see AbsItemLayout#setAdapterPresenter(AbsAdapterPresenter)
     */
    public boolean isShowRowTitle(){
        return false;
    }

    /**
     * 是否打开小项RecyclerView item --> name
     * @return
     */
    public boolean isShowItemTitle(){
        return false;
    }


    /**
     * 是否使用正常焦点模式的RecyclerView
     * @return
     *
     * @see AbsItemLayout#setRecyclerModel(boolean)
     */
    public boolean isUseNomalRecyclerView(){
        return getEndVisibleItemCount() <= 0;
    }

    /**
     * 是否使用自定义的{@link RecyclerView.LayoutManager}
     * @return
     */
    public boolean isUseCustomLayoutManager(){
        return false;
    }

    /**
     * 返回小项RecyclerView的PaddingBottom
     * @return
     *
     * @see AbsItemLayout#setLayoutSize()
     */
    public int getItemBottomPadding(){
        return Utils.getDimension(MyApplication.getInstance(), R.dimen.home_item_recycler_padding_bottom);
    }

    /**
     * 返回额外的数据,一般可用于添加一些"加减号"
     * @return
     */
    public T getAdditionalData(){
        return null;
    }

    public void onItemFocusChangeListener(boolean hasFocus, View view, HomeType homeType, T t){

    }

    protected void onItemChange(HomeType homeType, HomeDetailBean bean, boolean hasFocus, View view){
        if (mItemFocusChangeListener != null){
            mItemFocusChangeListener.onItemChange(homeType,bean,hasFocus,view);
        }
    }

    public void onItemClickListener(View view,int position, T t,boolean isEmpty){

    }

    public boolean onItemLongClickListener(final ImageView view, int position, T t){
        return false;
    }

    public boolean onItemKeyDown(View view, int keyCode, KeyEvent event, int position, T t){
        return false;
    }

    /**
     * 是否是数据集合里面的第一个
     * @param t t
     * @return 是否是数据集合里面的第一个
     */
    public boolean isFirstPosition(T t){
        return t != null && Util.isNoEmptyList(mAbsItem.getSourceList())
                && mAbsItem.getSourceList().get(0) != null
                && mAbsItem.getSourceList().get(0).equals(t);
    }

    /**
     * 是否是数据集合里面的最后一个
     * @param t t
     * @return 是否是数据集合里面的最后一个
     */
    public boolean isLastPosition(T t){
        return t != null && Util.isNoEmptyList(mAbsItem.getSourceList())
                && mAbsItem.getSourceList().get(mAbsItem.getSourceList().size() - 1) != null
                && mAbsItem.getSourceList().get(mAbsItem.getSourceList().size() - 1).equals(t);
    }

    @Override
    public void load(AdapterHomePagePresenter pagePresenter, AbsAdapter.AbsViewHolder holder, int position, Map<AbsItem, Boolean> map) {

    }


    @Override
    public void processLoadResult(int code,List<T> list, AbsAdapter.AbsViewHolder holder, int position, Map<AbsItem, Boolean> map) {
        LogX.d(TAG+" processLoadResult type : "+mAbsItem.getHomeType().toString()+" code : "
                +code+" list size : "+(Util.isNoEmptyList(list) ? String.valueOf(list.size()) : "null"));
        map.put(mAbsItem, false);
        if (isCanEmpty()){
            mAbsItem.setSourceList(list);
            if (holder != null && holder.isRealPosition(position)) {
                //todo:这里要判断该项是否已经移除屏幕,也就是说该holder是否被其他item重用;如果没有的话才应该去刷新界面.
                holder.bind(true,mAbsItem,position);
            }
            if (code == 0) {
                mAbsItem.addPage();
            } else{
                mAbsItem.setError();
            }
        }else {
            if (code == 0) {
                mAbsItem.setSourceList(list);
                if (holder != null && holder.isRealPosition(position)) {
                    //todo:这里要判断该项是否已经移除屏幕,也就是说该holder是否被其他item重用;如果没有的话才应该去刷新界面.
                    holder.bind(true, mAbsItem, position);
                }
                mAbsItem.addPage();
            } else {
                if (holder != null && holder.isRealPosition(position)) {
                    holder.setViewVisibilityGone();
                }
                mAbsItem.setError();
            }
        }
    }
}
