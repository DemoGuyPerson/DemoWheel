package com.nes.base.mvp;

import android.content.Context;
import android.os.Bundle;

/**
 * 相对于Fragment来说的{@link MvpPresenter}
 * @author liuqz
 */
public interface MvpFragmentPresenter<V extends MvpView> extends MvpPresenter<V> {

    void onMvpFragmentAttach(Context context);

    void onMvpFragmentActivityCreated(Bundle savedInstanceState);

    void onMvpFragmentDestroyView();

    void onMvpFragmentDetach();

    void onMvpFragmentHiddenChanged(boolean hidden);
}
