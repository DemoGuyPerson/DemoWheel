package com.nes.customgooglelauncher.ui.fragment;

import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nes.base.mvp.BaseMvpFragment;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.mvp.contract.AppsFragmentContract;
import com.nes.customgooglelauncher.mvp.presenter.AppsFragmentPresenterImpl;
import com.nes.customgooglelauncher.bean.AbsItem;
import com.nes.customgooglelauncher.ui.presenter.AllAppAndGameAdapterPresenter;
import com.nes.customgooglelauncher.ui.adapter.AppsAdapter;
import com.nes.customgooglelauncher.ui.widget.FocusLinearLayoutManager;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.customgooglelauncher.utils.AppUtils;
import com.nes.customgooglelauncher.utils.BrowseItemFocusHighlight;
import com.nes.customgooglelauncher.utils.Constants;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.CornerUtil;
import com.nes.utils.LogX;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看设备所有App and game
 * @author liuqz
 */
public class MyAppsFragment extends BaseMvpFragment<AppsFragmentContract.IAppsFragmentPresenter>
        implements AppsFragmentContract.IAppsFragmentView,View.OnFocusChangeListener,View.OnClickListener{

    private RecyclerView mRecyclerView;
    private AppsAdapter mAdapter;
    private BrowseItemFocusHighlight mBrowseItemFocusHighlight;


    public static MyAppsFragment newInstance(){
        return new MyAppsFragment();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_myapp;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBrowseItemFocusHighlight = BrowseItemFocusHighlight.newInstance();
        mRecyclerView = findId(R.id.verticalGridView);
        mAdapter = new AppsAdapter();
        mAdapter.addHeaderView(getHeaderView());
        mRecyclerView.setLayoutManager(new FocusLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        initAbsItem();
        presenter.addAppBroadcast();
    }

    private void initAbsItem(){
        List<AbsItem> absItems = new ArrayList<>();
        AbsItem<ResolveInfo> appItem = new AbsItem<ResolveInfo>(getString(R.string.apps_title),
                "", HomeType.ALL_APP, 0,Utils.getDimension(getActivity(),R.dimen.home_item_item_type4_height));
        appItem.setAdapterPresenter(new AllAppAndGameAdapterPresenter());

        AbsItem<ResolveInfo> gameItem = new AbsItem<ResolveInfo>(getString(R.string.apps_game_title),
                "", HomeType.ALL_GAME, 0,Utils.getDimension(getActivity(),R.dimen.home_item_item_type4_height));
        gameItem.setAdapterPresenter(new AllAppAndGameAdapterPresenter());
        absItems.add(appItem);
        absItems.add(gameItem);
        mAdapter.setList(absItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected AppsFragmentContract.IAppsFragmentPresenter createPresenter() {
        return new AppsFragmentPresenterImpl();
    }


    private View getHeaderView(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_apps_add_header2, null, false);
        ConstraintLayout tvApp = view.findViewById(R.id.tv_add_app);
        ConstraintLayout tvGame = view.findViewById(R.id.tv_add_game);
        TextView tvAppBg = view.findViewById(R.id.tv_bg_app);
        TextView tvAppGame = view.findViewById(R.id.tv_bg_game);
        CornerUtil.clipViewCornerByResId(tvApp, R.dimen.home_item_item_image_rate);
        CornerUtil.clipViewCornerByResId(tvGame, R.dimen.home_item_item_image_rate);
        CornerUtil.clipViewCornerByResId(tvAppBg, R.dimen.home_item_item_image_rate);
        CornerUtil.clipViewCornerByResId(tvAppGame, R.dimen.home_item_item_image_rate);
        mBrowseItemFocusHighlight.onInitializeView(tvApp);
        mBrowseItemFocusHighlight.onInitializeView(tvGame);
        tvApp.setOnFocusChangeListener(this);
        tvGame.setOnFocusChangeListener(this);
        tvApp.setOnClickListener(this);
        tvGame.setOnClickListener(this);
        return view;
    }



    @Override
    public void installApk(String pkgName) {
        LogX.e(TAG+" installApk pkgName : "+pkgName);
        mAdapter.reRequestAll();
    }

    @Override
    public void unInstallApk(String pkgName) {
        LogX.e(TAG+" unInstallApk pkgName : "+pkgName);
        boolean removeApp = mAdapter.removeApp(pkgName);
        if (!removeApp) {
            mAdapter.reRequestAll();
        }
    }

    @Override
    public void onClick(View v) {
       if (v.getId() == R.id.tv_add_app){
           AppUtils.getInstance().launchApp(getActivity(), Constants.PLAYSTORE_INFO);
       }else if(v.getId() == R.id.tv_add_game){
           AppUtils.getInstance().launchApp(getActivity(), Constants.GOOGLEGAME_INFO);
       }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mBrowseItemFocusHighlight != null){
            mBrowseItemFocusHighlight.onItemFocused(v,hasFocus);
        }
    }

}
