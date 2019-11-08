package com.nes.base;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * @author liuqz
 */
public abstract class BaseFragment extends Fragment {

    protected String TAG = this.getClass().getSimpleName();

    protected View mRootView;

    protected abstract int getContentViewId();

    protected abstract void init(Bundle savedInstanceState);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getContentViewId(), container, false);
        }
        try {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        init(savedInstanceState);
        return mRootView;
    }

    public <T extends View> T findId(int resId) {
        return mRootView.findViewById(resId);
    }

    public <T extends View> T findId(int resId, View parent) {
        return parent.findViewById(resId);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
