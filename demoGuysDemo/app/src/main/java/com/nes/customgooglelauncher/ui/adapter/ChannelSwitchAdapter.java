package com.nes.customgooglelauncher.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.nes.base.BaseRecyclerViewAdapter;
import com.nes.base.BaseViewHolder;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.ChannelBean;
import com.nes.customgooglelauncher.utils.ChannelProgramUtils;
import com.nes.utils.LogX;
import com.nes.utils.Util;

/**
 * 开关Custom Channel Adapter
 * @author liuqz
 */
public class ChannelSwitchAdapter extends BaseRecyclerViewAdapter<ChannelBean, ChannelSwitchAdapter.ChannelSwitchViewHolder> {


    @Override
    protected ChannelSwitchViewHolder onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cus_channel_switch, parent, false);
        return new ChannelSwitchViewHolder(view,this);
    }

    @Override
    protected void onBaseBindViewHolder(ChannelSwitchViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ChannelSwitchViewHolder extends BaseViewHolder<ChannelSwitchAdapter>{

        TextView tvName;
        TextView tvAlias;
        Switch mSwitch;
        View mMask;
        ChannelSwitchViewHolder(View itemView, ChannelSwitchAdapter channelSwitchAdapter) {
            super(itemView, channelSwitchAdapter);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAlias = itemView.findViewById(R.id.tv_alias);
            mSwitch = itemView.findViewById(R.id.mSwitch);
            mMask = itemView.findViewById(R.id.mask);
            mSwitch.setFocusable(false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isCheck = mSwitch.isChecked();
                    mSwitch.setChecked(!isCheck);
                    ChannelBean bean = getAdapter().getItem(getAdapterPosition());
                    if (bean != null && bean.getChannel() != null){
                        LogX.d("updateBrowsable isChecked : "+!isCheck+" id : "+bean.getChannel().getId());
                        ChannelProgramUtils.getInstance().updateBrowsable(bean.getChannel().getId(),!isCheck);
                        bean.setChangeState(!isCheck ? 1 : 0);
                    }
                }
            });
        }

        void bind(ChannelBean bean){
            if (bean != null){
                if (!Util.isNoEmptyList(bean.getPrograms())){
                    mMask.setVisibility(View.VISIBLE);
                    tvAlias.setText(itemView.getContext().getResources().getString(R.string.channel_is_null));
                    tvAlias.setVisibility(View.VISIBLE);
                }else{
                    tvAlias.setVisibility(View.GONE);
                    mMask.setVisibility(View.GONE);
                    if (bean.isChange()){
                        mSwitch.setChecked(bean.getChangeState() == 1);
                    }else {
                        mSwitch.setChecked(bean.getChannel() != null && bean.getChannel().isBrowsable());
                    }
                }
                tvName.setText(bean.getChannel() == null ? "" : bean.getChannel().getDisplayName());
            }
        }
    }
}
