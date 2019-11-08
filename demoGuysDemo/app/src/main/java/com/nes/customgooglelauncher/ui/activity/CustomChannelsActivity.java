package com.nes.customgooglelauncher.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.Window;

import com.nes.base.BaseFragment;
import com.nes.base.mvp.BaseMvpActivity;
import com.nes.base.mvp.MvpPresenter;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.ui.fragment.ChannelSwitchFragment;
import com.nes.customgooglelauncher.ui.fragment.CustomChannelFragment;

import java.util.List;

/**
 * 开关各种Channel
 * @author liuqz
 * @description:
 */
public class CustomChannelsActivity extends BaseMvpActivity {

    private List<ChannelBean> mSwitchBean;

    public static void newInstance(Activity context){
        if (context != null){
            Intent intent = new Intent(context, CustomChannelsActivity.class);
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
        }
    }

    @Override
    protected void setContentViewBefore() {
        super.setContentViewBefore();
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.slide);
        //退出时使用
        getWindow().setExitTransition(explode);
        getWindow().setReturnTransition(explode);
    }

    @Override
    protected MvpPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initMvp(Bundle savedInstanceState) {
        switchFragment(0);
    }

    @Override
    protected int getFragmentLayoutResId() {
        return R.id.fl_root;
    }

    @Override
    protected int getFragmentNum() {
        return 2;
    }

    @Override
    protected BaseFragment getBaseFragmentByIndex(int index) {
        BaseFragment result = null;
        if (index == 0){
            result = CustomChannelFragment.newInstance();
        }else if(index == 1){
            result = ChannelSwitchFragment.newInstance();
        }
        return result;
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_custom_channel;
    }

    public List<ChannelBean> getSwitchBean() {
        return mSwitchBean;
    }


    public void switchSecondFragment(List<ChannelBean> list){
        mSwitchBean = list;
        switchFragment(1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mCurrentFragment != null && mCurrentFragment instanceof ChannelSwitchFragment) {
                switchFragment(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
