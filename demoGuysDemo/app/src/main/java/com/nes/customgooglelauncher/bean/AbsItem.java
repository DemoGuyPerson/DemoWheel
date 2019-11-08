package com.nes.customgooglelauncher.bean;

import androidx.annotation.Nullable;

import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.ui.presenter.AbsAdapterPresenter;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 大项RecyclerView item对应的实体类
 * @author liuqz
 */
public class AbsItem<T> {

    /**
     * 小项RecyclerView的数据源
     */
    private List<T> mSourceList;
    /**
     * 小项RecyclerView的item的宽,为0时表示需动态计算
     */
    private int mWidth;
    /**
     * 小项RecyclerView的item的可见高度，不包括padding
     */
    private int mHeight;
    /**
     * 小项RecyclerView的title str
     */
    private String mTitle;
    /**
     * 描述小项RecyclerView是否被关掉
     */
    private boolean mIsClose;
    /**
     * 额外的数据str,用于load或者其他验证
     */
    private String mAdditional;
    /**
     * 初始化page,用于分页，无分页需求时，为1即可
     */
    private int mInitPage = 1;
    /**
     * 当前页数，用于分页，初始状态与{@link #mInitPage}相等，load成功之后++
     */
    private int mCurrentPage = 1;
    /**
     * 当前数据类型，不同小项RecyclerView的mHomeType可以不同，但还是建议为其添加
     */
    private HomeType mHomeType;
    /**
     * {@linkplain AbsAdapterPresenter 小项RecyclerView处理者}
     */
    private AbsAdapterPresenter<T> mAdapterPresenter;

    public AbsItem(List<T> sourceList, int width, int height, String title, boolean isClose, String additional,
                   int initPage, int currentPage, HomeType homeType) {
        mSourceList = sourceList;
        mWidth = width;
        mHeight = height;
        mTitle = title;
        mIsClose = isClose;
        mAdditional = additional;
        mInitPage = initPage;
        mCurrentPage = currentPage;
        mHomeType = homeType;
    }

    public AbsItem(List<T> sourceList,String title,String additional,HomeType homeType){
        this(sourceList,0,Utils.getDimension(MyApplication.getInstance(), R.dimen.home_item_item_type3_height),
                title,false,additional,1,1,homeType);
    }

    public AbsItem(String title,String additonal,HomeType homeType,int width,int height){
        this(null,width,height,title,false,additonal,1,1,homeType);
    }

    public List<T> getSourceList() {
        if (mSourceList == null){
            mSourceList = new ArrayList<>();
        }
        return mSourceList;
    }

    public void setSourceList(List<T> sourceList) {
        mSourceList = sourceList;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isClose() {
        return mIsClose;
    }

    public void setClose(boolean close) {
        mIsClose = close;
    }

    public String getAdditional() {
        return mAdditional;
    }

    public void setAdditional(String additional) {
        mAdditional = additional;
    }

    public int getInitPage() {
        return mInitPage;
    }

    public void setInitPage(int initPage) {
        mInitPage = initPage;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }

    public HomeType getHomeType() {
        return mHomeType;
    }

    public void setHomeType(HomeType homeType) {
        mHomeType = homeType;
    }

    public AbsAdapterPresenter<T> getAdapterPresenter() {
        return mAdapterPresenter;
    }

    /**
     * 设置{@link AbsAdapterPresenter},需要注意的是，这里也要为{@link AbsAdapterPresenter},设置this : {@link AbsAdapterPresenter#setAbsItem(AbsItem)}
     * @param adapterPresenter {@link AbsAdapterPresenter}
     */
    public void setAdapterPresenter(AbsAdapterPresenter<T> adapterPresenter) {
        mAdapterPresenter = adapterPresenter;
        mAdapterPresenter.setAbsItem(this);
    }

    /**
     * 复原page为initPage
     */
    public void reductionPage() {
        this.mCurrentPage = mInitPage;
    }

    public void addPage() {
        this.mCurrentPage = this.mCurrentPage + 1;
    }

    public boolean isShow(){
        return !mIsClose && Util.isNoEmptyList(mSourceList);
    }

    /**
     * 是否为初始状态;
     * 只有为初始状态的时候，才会考虑进行{@linkplain com.nes.customgooglelauncher.ui.inter.Loader load加载}
     * @return 是否为初始状态
     */
    public boolean isInitPage(){
        return mCurrentPage == mInitPage;
    }

    public void setError(){
        mCurrentPage = -2;
    }

    @Override
    public String toString() {
        return "AbsItem{" +
                "mSourceList=" + mSourceList +
                ", mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                ", mTitle='" + mTitle + '\'' +
                ", mIsClose=" + mIsClose +
                ", mAdditional='" + mAdditional + '\'' +
                ", mInitPage=" + mInitPage +
                ", mCurrentPage=" + mCurrentPage +
                ", mHomeType=" + mHomeType +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
