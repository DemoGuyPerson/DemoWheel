package com.nes.customgooglelauncher.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nes.base.mvp.BaseMvpFragment;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.CustomChannelBean;
import com.nes.customgooglelauncher.mvp.contract.CustomChannelFragmentContract;
import com.nes.customgooglelauncher.mvp.presenter.CustomChannelFragmentPresenterImpl;
import com.nes.customgooglelauncher.ui.activity.CustomChannelsActivity;
import com.nes.customgooglelauncher.ui.adapter.CustomChannelAdapter;
import com.nes.customgooglelauncher.ui.widget.FocusLinearLayoutManager;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.List;

/**
 * 展示大Channel
 * @author liuqz
 */
public class CustomChannelFragment extends BaseMvpFragment<CustomChannelFragmentContract.ICustomChannelFragmentPresenter>
        implements CustomChannelFragmentContract.ICustomChannelFragmentView {

    private CustomChannelAdapter mCustomChannelAdapter;

    public static CustomChannelFragment newInstance(){
        return new CustomChannelFragment();
    }

    @Override
    protected CustomChannelFragmentContract.ICustomChannelFragmentPresenter createPresenter() {
        return new CustomChannelFragmentPresenterImpl();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_custom_channel;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        RecyclerView recyclerView = findId(R.id.rcy_channel);
//        FocusLinearLayoutManager focusLinearLayoutManager = new FocusLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mCustomChannelAdapter = new CustomChannelAdapter();
        mCustomChannelAdapter.addHeaderView(getHeaderView());
//        recyclerView.setLayoutManager(focusLinearLayoutManager);
        recyclerView.setAdapter(mCustomChannelAdapter);

        mCustomChannelAdapter.setItemClickListener(new CustomChannelAdapter.MoreModeItemClickListener() {
            @Override
            public void onItemClick(View view, int position, CustomChannelBean bean) {
                LogX.d(TAG+" onItemClick position : "+position);
                if (getActivity() instanceof CustomChannelsActivity){
                    ((CustomChannelsActivity)getActivity()).switchSecondFragment(bean.getChannelBeans());
                }
            }
        });

        presenter.loadAllCustomChannel();
    }

    private View getHeaderView() {
        return View.inflate(getActivity(),R.layout.layout_cus_channel_head,null);
    }

    @Override
    public void refreshChannelList(List<CustomChannelBean> list) {
        if (Util.isNoEmptyList(list)){
            mCustomChannelAdapter.setData(list);
            mCustomChannelAdapter.notifyDataSetChanged();
        }
    }
}
