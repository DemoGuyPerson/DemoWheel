package com.nes.base.mvp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.nes.base.BaseFragment;

/**
 * @author liuqz
 */
public abstract class BaseMvpFragment<P extends MvpFragmentPresenter> extends BaseFragment implements MvpView {

    protected P presenter;
    protected abstract P createPresenter();

    /**
     * 当fragment被加入到activity时调用（在这个方法中可以获得所在的activity）。
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter = createPresenter();
//        if (presenter == null) {
//            throw new NullPointerException("Presenter is null! Do you return null in createPresenter()?");
//        }
        if (presenter != null){
            presenter.onMvpFragmentAttach(context);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (presenter != null){
            presenter.onMvpFragmentHiddenChanged(hidden);
        }
    }

    /**
     * 初始化Fragment,可通过savedInstanceState 获取之前保存的值。
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (presenter != null){
            presenter.onMvpAttachView(this, savedInstanceState);
        }
    }

//    /**
//     * 当activity要得到fragment的layout时，调用此方法，fragment在其中创建自己的layout(界面)。
//     *
//     * @param inflater
//     * @param container
//     * @param savedInstanceState
//     * @return
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }

    /**
     * 当activity的onCreated()方法返回后调用此方法.
     * 在该方法内可以进行与Activity交互的UI操作，
     * 所以在该方法之前Activity的onCreate方法并未执行完成，
     * 如果提前进行交互操作，会引发空指针异常。
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (presenter != null){
            presenter.onMvpFragmentActivityCreated(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null){
            presenter.onMvpStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null){
            presenter.onMvpResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter != null){
            presenter.onMvpPause();
        }
    }

    /**
     * 保存当前Fragment的状态。该方法会自动保存Fragment的状态，比如EditText键入的文本。
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (presenter != null){
            presenter.onMvpSaveInstanceState(outState);
        }
    }

    /**
     * 当fragment中的视图被移除的时候，调用这个方法。
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null){
            presenter.onMvpFragmentDestroyView();
        }
    }

    /**
     * 当fragment和activity分离的时候，调用这个方法。
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null){
            presenter.onMvpFragmentDetach();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onMvpDetachView(false);
            presenter.onMvpDestroy();
        }
    }
}
