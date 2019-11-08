package com.nes.customgooglelauncher.ui.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;

import com.nes.base.BaseFragment;
import com.nes.base.mvp.BaseMvpActivity;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.notifications.TvNotification;
import com.nes.customgooglelauncher.broadcast.AppBroadcastReceiver;
import com.nes.customgooglelauncher.mvp.contract.MainActivityContract;
import com.nes.customgooglelauncher.mvp.presenter.MainActivityPresenterImpl;
import com.nes.customgooglelauncher.ui.fragment.HomeFragment;
import com.nes.customgooglelauncher.ui.fragment.MyAppsFragment;
import com.nes.customgooglelauncher.ui.widget.CustomTextView;
import com.nes.customgooglelauncher.ui.widget.FocusConstraintLayout;
import com.nes.customgooglelauncher.utils.AppUtils;
import com.nes.customgooglelauncher.utils.BrowseItemFocusHighlight;
import com.nes.customgooglelauncher.utils.SetDefaultLauncher;
import com.nes.utils.CornerUtil;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.List;

/**
 * main
 * @author liuqz
 */
public class MainActivity extends BaseMvpActivity<MainActivityContract.IMainActivityPresenter>
        implements MainActivityContract.IMainActivityView,View.OnClickListener,
                   View.OnFocusChangeListener,FocusConstraintLayout.FocusCallBack {

    private FocusConstraintLayout mFocusConstraintLayout;
    private ImageView mImageHeaderIcon;
    private ImageView mImageSetting;
    private ImageView mImageAss;
    private ImageView mImageWifi;
    private CustomTextView mTvNotify;
    private CustomTextView mTvHome,mTvMyApps;
    private View mLastSelectorView = null;
    private BrowseItemFocusHighlight mBrowseItemFocusHighlight;
    private static final int SEARCH_TYPE_VOICE = 1;
    private Group mGroup;

    @Override
    protected MainActivityContract.IMainActivityPresenter createPresenter() {
        return new MainActivityPresenterImpl();
    }

    @Override
    protected void initMvp(Bundle savedInstanceState) {
        AppBroadcastReceiver.getInstance().register(this);
        SetDefaultLauncher.disableGoogleLauncher(this);
        initView();
        String brand = Build.BRAND ;
//        if (!TextUtils.isEmpty(brand) && !brand.equalsIgnoreCase("Hathway")) {
//            mGroup.setVisibility(View.GONE);
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setCancelable(false);
//            builder.setView(R.layout.show_brand);
//            builder.show();
//        }else{
            mGroup.setVisibility(View.VISIBLE);
            mBrowseItemFocusHighlight = BrowseItemFocusHighlight.newInstance();
            initData();
            switchFragment(0);
            setSelectorView(mTvHome);
            mTvHome.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mTvHome.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mTvHome.requestFocus();
                }
            });
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppBroadcastReceiver.getInstance().unRegister(this);
    }

    public CustomTextView getTvHome() {
        return mTvHome;
    }

    private void initView() {
        mFocusConstraintLayout = findId(R.id.id_main);
        mGroup = findId(R.id.group);
        mImageHeaderIcon = findId(R.id.img_icon);
        mImageSetting = findId(R.id.img_setting);
        mImageAss = findId(R.id.id_img_assistant);
        mTvHome = findId(R.id.id_title_home);
        mTvMyApps = findId(R.id.id_title_myapp);
        mImageWifi = findId(R.id.img_wifi);
        mTvNotify = findId(R.id.tv_notify);

        CornerUtil.clipViewCircle(mImageHeaderIcon);
        CornerUtil.clipViewCircle(mImageSetting);
        CornerUtil.clipViewCircle(mImageAss);
        CornerUtil.clipViewCircle(mTvNotify);

    }

    @Override
    protected int getFragmentLayoutResId() {
        return R.id.fragment;
    }

    @Override
    protected int getFragmentNum() {
        return 2;
    }

    @Override
    protected BaseFragment getBaseFragmentByIndex(int index) {
        BaseFragment result = null;
        if (index == 0){
            result = HomeFragment.newInstance();
        }else if(index == 1){
            result = MyAppsFragment.newInstance();
        }
        return result;
    }

    private void initData(){
        mFocusConstraintLayout.setFocusCallBack(this);

        mImageHeaderIcon.setOnClickListener(this);
        mImageSetting.setOnClickListener(this);
        mImageAss.setOnClickListener(this);
        mTvHome.setOnClickListener(this);
        mTvMyApps.setOnClickListener(this);
        mImageWifi.setOnClickListener(this);
        mTvNotify.setOnClickListener(this);
        mBrowseItemFocusHighlight.onInitializeView(mTvHome);
        mBrowseItemFocusHighlight.onInitializeView(mTvMyApps);
        mTvHome.setOnFocusChangeListener(this);
        mTvMyApps.setOnFocusChangeListener(this);


        presenter.registerNotification();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_setting:
                AppUtils.getInstance().openSettings(this);
                break;
            case R.id.img_icon:

                break;
            case R.id.id_img_assistant:
                AppUtils.getInstance().openAssistant(this,SEARCH_TYPE_VOICE);
                break;
            case R.id.id_title_home:
                if (switchFragment(0)){
                    setSelectorView(v);
                }
                break;
            case R.id.id_title_myapp:
                if (switchFragment(1)){
                    setSelectorView(v);
                }
                break;
            case R.id.img_wifi:
                AppUtils.getInstance().openWifiSettings(this);
                break;
            case R.id.tv_notify:
                AppUtils.getInstance().openNotification(this);
                break;
            default:

                break;
        }
    }

    private void setSelectorView(View selectorView){
        if (mLastSelectorView == null){
            mLastSelectorView = selectorView;
            mLastSelectorView.setSelected(true);
        }else{
            mLastSelectorView.setSelected(false);
            mLastSelectorView = selectorView;
            mLastSelectorView.setSelected(true);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.id_title_home:
            case R.id.id_title_myapp:
                if (mBrowseItemFocusHighlight!=null){
                    mBrowseItemFocusHighlight.onItemFocused(v,hasFocus,false);
                }
                break;
            default:

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && mCurrentFragment != null && !(mCurrentFragment instanceof HomeFragment)){
                switchFragment(0);
                mTvHome.requestFocus();
                setSelectorView(mTvHome);
            }
            return true;
        }
        if (mCurrentFragment != null) {
            if (mCurrentFragment.onKeyDown(keyCode,event)){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public LoaderManager getActivityLoaderManager() {
        return getLoaderManager();
    }

    @Override
    public void showNotifyNum(int num) {
        LogX.e(TAG+" notify showNotifyNum num : "+num);
        mTvNotify.setText(String.valueOf(num));
    }

    @Override
    public void showNotifyList(List<TvNotification> list) {
        LogX.e(TAG+" notify showNotifyList list.size : "+(Util.isNoEmptyList(list) ? String.valueOf(list.size()) : " null "));
        if (Util.isNoEmptyList(list)){
            for (TvNotification notification : list){
                LogX.d(TAG+" showNotifyList toString : "+notification.toString());
            }
        }
    }

    @Override
    public View getNextDownFocusView() {
        if (mCurrentFragment instanceof HomeFragment){
           return ((HomeFragment)mCurrentFragment).getNextDownFocusView();
        }
        return null;
    }

    @Override
    public boolean isTopFocusView(View view) {
        return mCurrentFragment instanceof HomeFragment && view != null &&
                (view.getId() == mImageHeaderIcon.getId() ||
                view.getId() == mTvHome.getId() ||
                view.getId() == mTvMyApps.getId() ||
                view.getId() == mImageAss.getId() ||
                view.getId() == mTvNotify.getId() ||
                view.getId() == mImageWifi.getId() ||
                view.getId() == mImageSetting.getId());
    }
}
