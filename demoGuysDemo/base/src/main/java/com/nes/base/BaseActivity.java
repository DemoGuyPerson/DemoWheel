package com.nes.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.nes.utils.LogX;


/**
 * @author liuqz
 */
public abstract class BaseActivity extends FragmentActivity {

    protected String TAG = this.getClass().getSimpleName();

    protected BaseFragment[] mFragments;

    protected BaseFragment mCurrentFragment = null;

    protected int mCurrentIndex = -1;

    /**
     * 返回布局layout id
     * @return layout id
     */
    protected abstract int getContentViewId();

    protected abstract void init(Bundle savedInstanceState);

    protected int getFragmentNum(){
        return 0;
    }

    protected int getFragmentLayoutResId(){
        return -1;
    }

    protected BaseFragment getBaseFragmentByIndex(int index){
        return null;
    }

    protected void setContentViewBefore(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragments = new BaseFragment[getFragmentNum()];
        setContentViewBefore();
        setContentView(getContentViewId());
        init(savedInstanceState);
    }

    /**
     * 切换Fragment
     * @param index
     * @return
     */
    protected boolean switchFragment(int index){
        if (mCurrentIndex != -1 && index == mCurrentIndex){
            LogX.d(TAG+" switchFragment index == mCurrentIndex ");
            return false;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        for (int i = 0,size = mFragments.length; i < size; i++){
            if (i == index){
                if (mFragments[i] == null){
                    mFragments[i] = getBaseFragmentByIndex(i);
                    if (mFragments[i] != null) {
                        fragmentTransaction = fragmentTransaction.add(getFragmentLayoutResId(), mFragments[i]);
                    }
                }
                mCurrentFragment = mFragments[i];
                mCurrentIndex = index;
                if (mFragments[i] != null) {
                    fragmentTransaction = fragmentTransaction.show(mFragments[i]);
                    LogX.d(TAG+" switchFragment show fragment : "+mFragments[i]);
                }
            }else{
                if (mFragments[i] != null) {
                    fragmentTransaction = fragmentTransaction.hide(mFragments[i]);
                }
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
        return true;
    }

    public <T extends View> T findId(int resId) {
        return findViewById(resId);
    }

    public <T extends View> T findId(int resId, View parent) {
        return parent.findViewById(resId);
    }

}
