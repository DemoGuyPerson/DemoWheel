package com.nes.customgooglelauncher.ui.fragment;


import android.content.Context;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;

import com.bumptech.glide.Glide;
import com.nes.base.mvp.BaseMvpFragment;
import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.bean.HomeDetailBean;
import com.nes.customgooglelauncher.mvp.contract.HomeFragmentContract;
import com.nes.customgooglelauncher.mvp.presenter.HomeFragmentPresenterImpl;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.activity.CustomChannelsActivity;
import com.nes.customgooglelauncher.ui.activity.MainActivity;
import com.nes.customgooglelauncher.ui.adapter.HomeAdapter;
import com.nes.customgooglelauncher.ui.inter.HRecyclerViewCallback;
import com.nes.customgooglelauncher.ui.inter.ItemFocusChangeListener;
import com.nes.customgooglelauncher.ui.presenter.AllAppAdapterPresenter;
import com.nes.customgooglelauncher.ui.widget.FirstRowRecyclerView;
import com.nes.customgooglelauncher.ui.widget.CustomTextView;
import com.nes.customgooglelauncher.ui.widget.FirstColumnRecyclerView;
import com.nes.customgooglelauncher.ui.widget.HomeDetailsLayout;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.customgooglelauncher.utils.BrowseItemFocusHighlight;
import com.nes.customgooglelauncher.utils.Constants;
import com.nes.customgooglelauncher.utils.ImageLoaderUtils;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.imageloader.request.BitmapRequest;
import com.nes.utils.CornerUtil;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseMvpFragment<HomeFragmentContract.IHomeFragmentPresenter>
        implements HomeFragmentContract.IHomeFragmentView, FirstColumnRecyclerView.FocusCallBack {

    private FirstRowRecyclerView mVerticalGridView;
    private HomeAdapter mHomeAdapter;
    private HomeDetailsLayout mHomeDetailsLayout;
    private ImageView mImageView,mImageViewBg;
    private BrowseItemFocusHighlight mBrowseItemFocusHighlight;
    private MainActivity mActivity;
    private CustomTextView mCustomTextView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected HomeFragmentContract.IHomeFragmentPresenter createPresenter() {
        return new HomeFragmentPresenterImpl();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity){
            mActivity = (MainActivity)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mActivity != null){
            mActivity = null;
        }
    }

    public View getNextDownFocusView(){
        View view = mVerticalGridView.getChildAt(0);
        if (view instanceof HRecyclerViewCallback){
            HRecyclerViewCallback hRecyclerViewCallback = (HRecyclerViewCallback)view;
            View firstView = hRecyclerViewCallback.getStartView();
            if (firstView != null){
                mVerticalGridView.initLastFocusIndex();
                return firstView;
            }
            return null;
        }
        return null;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mVerticalGridView != null){
            mVerticalGridView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVerticalGridView != null){
            mVerticalGridView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView(){
        mBrowseItemFocusHighlight = BrowseItemFocusHighlight.newInstance();
        mVerticalGridView = findId(R.id.recyclerview);
        mImageView = findId(R.id.img_src);
        mHomeDetailsLayout = findId(R.id.home_details);
        mImageViewBg = findId(R.id.img_bg);
        CornerUtil.clipViewCircle(mImageView);

        setRightImageVisible(false);

        mVerticalGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        Glide.with(MyApplication.getInstance()).resumeRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Glide.with(MyApplication.getInstance()).pauseRequests();
                        break;
                    default:

                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initData(){
        mHomeAdapter = new HomeAdapter();
        mHomeAdapter.setActivity(mActivity);
        mHomeAdapter.setFocusCallBack(this);
        mHomeAdapter.addFootView(getFootView());
        initAbsItems();

        presenter.addAppBroadcast();
    }

    private void setRightImageVisible(boolean visible){
        if (visible){
            mImageViewBg.setBackgroundResource(R.drawable.home_img_bg_cent);
        }else{
            mImageViewBg.setBackgroundColor(getResources().getColor(R.color.home_bg_color));
        }
    }

    private ItemFocusChangeListener mItemFocusChangeListener = new ItemFocusChangeListener() {
        @Override
        public void onItemChange(HomeType homeType, HomeDetailBean bean, boolean hasFocus, View view) {
            if (hasFocus && bean != null){
                LogX.d(TAG+" initData onItemChange homeType : "+homeType.toString()+" bean : "+bean.toString());
                setRightImageVisible(true);
                mHomeDetailsLayout.bind(bean);
                ImageLoaderUtils.imageLoaderDisplayImageHttp(mImageView,bean.getBigIcon()
                        ,ImageLoaderUtils.getDefultResId(),ImageLoaderUtils.getDefultResId(),true);
//                    ImageLoaderUtils.imageLoaderDisplayImageHttp(mImageView,bean.getBigIcon());
            }
        }
    };

    private View getFootView(){
        View view = View.inflate(getActivity(),R.layout.layout_channel_foot,null);
        mCustomTextView = view.findViewById(R.id.btn_ctm_channel);
        mBrowseItemFocusHighlight.onInitializeView(mCustomTextView);
        mCustomTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomChannelsActivity.newInstance(getActivity());
            }
        });
        mCustomTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mBrowseItemFocusHighlight.onItemFocused(v,hasFocus);
            }
        });
        CornerUtil.clipViewCornerByResId(mCustomTextView, R.dimen.home_item_item_image_rate);
        mCustomTextView.setTag(BitmapRequest.IMAGE_TAG_KEY,HomeFragment.class.getSimpleName());
        return view;
    }

    private void initAbsItems() {
        List<AbsItem> list = new ArrayList<>();

        AbsItem<ResolveInfo> allapp = new AbsItem<>(getString(R.string.apps_title),
                "",HomeType.ALL_APP, Utils.getDimension(getActivity(),R.dimen.home_item_item_type3_width)
                ,Utils.getDimension(getActivity(),R.dimen.home_item_item_type3_height));
        allapp.setAdapterPresenter(new AllAppAdapterPresenter(mItemFocusChangeListener));
        list.add(allapp);
        mHomeAdapter.setList(list);
//        mHomeAdapter.setList(null);
        mVerticalGridView.setAdapter(mHomeAdapter);

        presenter.loadAllShowChannel();
        presenter.monitordb();
        presenter.addAppBroadcast();
    }


    @Override
    public void showAllShowChannel(List<AbsItem> list) {
        if (mHomeAdapter != null && Util.isNoEmptyList(list)){
            for (AbsItem absItem : list){
                absItem.getAdapterPresenter().setItemFocusChangeListener(mItemFocusChangeListener);
            }
            mHomeAdapter.addList(list);
            mHomeAdapter.firstPreLoad();
        }
    }

    @Override
    public void notifyAllData() {
        LogX.d(TAG+" notifyAllData");
        if (mHomeAdapter != null){
            mHomeAdapter.notifyAllData();
        }
    }

    @Override
    public void notifyChannelOne(int action, ChannelBean channelBean) {
        LogX.d(TAG+" notifyChannelOne action : "+action+" channelBean : "+channelBean.toString());
        if (mHomeAdapter != null){
            mHomeAdapter.notifyChannelOne(action,channelBean);
        }
    }

    @Override
    public void notifyProgram(int action, Channel channel, PreviewProgram previewProgram,long programId) {
        LogX.d(TAG+" notifyProgram action : "+action
                +" channel : "+(channel == null ? " null " : channel.toString())+
                " previewProgram : "+(previewProgram == null ? " null " : previewProgram.toString())+ " programId : "+programId);
        if (mHomeAdapter != null){
            mHomeAdapter.notifyProgram(action,channel,previewProgram,programId);
        }
    }

    @Override
    public void notifyChannels(List<ChannelBean> channelBeans) {
        LogX.d(TAG+" notifyChannels channelBeans size : "+(Util.isNoEmptyList(channelBeans) ? String.valueOf(channelBeans.size()) : " null "));
        if (mHomeAdapter != null){
            mHomeAdapter.notifyChannels(channelBeans);
        }
    }

    @Override
    public int getActionByProgram(PreviewProgram previewProgram) {
        return mHomeAdapter != null ? mHomeAdapter.getActionByProgram(previewProgram) : Constants.TYPE_NOTIFY_DEFULT;
    }

    @Override
    public void unInstallApk(String pkgName) {
        LogX.e(TAG+" unInstallApk pkgName : "+pkgName);
        mHomeAdapter.checkUnInstallApk(pkgName);
    }


    @Override
    public View getNextFocusView() {
        View view = mActivity != null ? mActivity.getTvHome() : null;
        mVerticalGridView.setEmptyLastFocusIndex();
        if (view != null){
            setRightImageVisible(false);
        }
        return view;
    }
}
