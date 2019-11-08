package com.nes.customgooglelauncher.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nes.base.mvp.BaseMvpFragment;
import com.nes.base.mvp.MvpFragmentPresenter;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.ui.activity.CustomChannelsActivity;
import com.nes.customgooglelauncher.ui.adapter.ChannelSwitchAdapter;
import com.nes.customgooglelauncher.ui.widget.FocusLinearLayoutManager;

import java.util.List;

/**
 * 切换Channel
 * @author liuqz
 */
public class ChannelSwitchFragment extends BaseMvpFragment {

    private ChannelSwitchAdapter mChannelSwitchAdapter;
    private RecyclerView recyclerView;

    public static ChannelSwitchFragment newInstance(){
        return new ChannelSwitchFragment();
    }

    @Override
    protected MvpFragmentPresenter createPresenter() {
        return null;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_custom_channel;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        recyclerView = findId(R.id.rcy_channel);
        mChannelSwitchAdapter = new ChannelSwitchAdapter();
        mChannelSwitchAdapter.setData(getChannelList());
//        FocusLinearLayoutManager focusLinearLayoutManager = new FocusLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(focusLinearLayoutManager);
        recyclerView.setAdapter(mChannelSwitchAdapter);

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (recyclerView.getChildCount() >0){
                    recyclerView.getChildAt(0).requestFocus();
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mChannelSwitchAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    mChannelSwitchAdapter.unregisterAdapterDataObserver(this);
                    View view = recyclerView.getChildAt(0);
                    if (view != null) {
                        view.requestFocus();
                    }
                }
            });
            mChannelSwitchAdapter.setData(getChannelList());
            mChannelSwitchAdapter.notifyDataSetChanged();
        }
    }

    private List<ChannelBean> getChannelList(){
        if (getActivity() instanceof CustomChannelsActivity){
            return ((CustomChannelsActivity)getActivity()).getSwitchBean();
        }
        return null;
    }
}
