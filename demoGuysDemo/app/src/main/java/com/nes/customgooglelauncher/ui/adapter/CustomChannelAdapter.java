package com.nes.customgooglelauncher.ui.adapter;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import com.nes.base.BaseRecyclerViewAdapter;
import com.nes.base.BaseViewHolder;
import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.CustomChannelBean;
import com.nes.customgooglelauncher.ui.widget.CustomTextView;
import com.nes.customgooglelauncher.utils.ChannelProgramUtils;
import com.nes.customgooglelauncher.utils.Constants;
import com.nes.customgooglelauncher.utils.ImageLoaderUtils;
import com.nes.utils.LogX;
import com.nes.utils.Util;

/**
 * 展示Channel list Adapter
 * @author liuqz
 */
public class CustomChannelAdapter extends BaseRecyclerViewAdapter<CustomChannelBean, CustomChannelAdapter.CustomChannelViewHodler> {

    private final int TYPE_ONE = 0x1111;
    private final int TYPE_MORE = 0x2222;

    @Override
    protected int getBaseItemViewType(int position) {
        CustomChannelBean bean = getItem(position);
        if (bean != null && bean.getDefBean() != null){
            if (bean.getChannelBeans() != null && bean.getChannelBeans().size() > 1) {
                return TYPE_MORE;
            } else {
                return TYPE_ONE;
            }
        }
        return TYPE_ONE;
    }

    @Override
    protected CustomChannelViewHodler onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == TYPE_MORE ?
                R.layout.item_cus_channel_more : R.layout.item_cus_channel_one, parent, false);
        return new CustomChannelViewHodler(view,this);
    }

    @Override
    protected void onBaseBindViewHolder(CustomChannelViewHodler holder, int position) {
        int viewType = getBaseItemViewType(position);
        CustomChannelBean bean  = getItem(position);
        if (bean != null) {
            if (viewType == TYPE_MORE) {
                holder.bindMore(bean);
            } else if (viewType == TYPE_ONE) {
                holder.bindOne(bean);
            }
        }
    }

    private void callBackClick(View view,int position){
        CustomChannelBean bean = getItem(position);
        if (bean != null && bean.getDefBean() != null && Util.isNoEmptyList(bean.getChannelBeans())){
            if (mItemClickListener != null){
                mItemClickListener.onItemClick(view,position,getItem(position));
            }
        }
    }

    static class CustomChannelViewHodler extends BaseViewHolder<CustomChannelAdapter>{

        ImageView logo;
        CustomTextView tvName;
        CustomTextView tvAlias;
        Switch mSwitch;
        View mMask;
        CustomChannelViewHodler(View itemView, CustomChannelAdapter customChannelAdapter) {
            super(itemView, customChannelAdapter);
            logo = itemView.findViewById(R.id.logo);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAlias = itemView.findViewById(R.id.tv_alias);
            mSwitch = itemView.findViewById(R.id.mSwitch);
            mMask = itemView.findViewById(R.id.mask);
            if (mSwitch != null){
                mSwitch.setFocusable(false);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int viewType = getAdapter().getBaseItemViewType(getAdapterPosition());
                    if (viewType == getAdapter().TYPE_ONE){
                        if (mSwitch != null) {
                            boolean isChecked = mSwitch.isChecked();
                            mSwitch.setChecked(!isChecked);
                            CustomChannelBean bean = getAdapter().getItem(getAdapterPosition());
                            long id = (bean == null || bean.getDefBean() == null || bean.getDefBean().getChannel() == null)
                                    ? -1 : bean.getDefBean().getChannel().getId();
                            if (id != -1) {
                                LogX.d("updateBrowsable isChecked : " + !isChecked + " id : " + id + " viewType : " + viewType);
                                ChannelProgramUtils.getInstance().updateBrowsable(id, !isChecked);
                                bean.getDefBean().setChangeState(!isChecked ? 1 : 0);
                            }
                        }
                    }else {
                        getAdapter().callBackClick(v, getAdapterPosition());
                    }
                }
            });
        }

        @SuppressLint("SetTextI18n")
        void bindMore(CustomChannelBean bean){
            int size = Util.isNoEmptyList(bean.getChannelBeans()) ? bean.getChannelBeans().size() : 0;
            tvAlias.setText(size + " channels");
            ImageLoaderUtils.imageLoaderDisplayImagePkg(logo,bean.getDefBean().getChannel().getPackageName());
            ApplicationInfo info = null;
            PackageManager packageManager = MyApplication.getInstance().getPackageManager();
            try {
                info = packageManager.getApplicationInfo(bean.getDefBean().getChannel().getPackageName(), 0);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if(null != info){
                tvName.setText(info.loadLabel(packageManager).toString());
            }
        }

        void bindOne(CustomChannelBean bean){
            ApplicationInfo info = null;
            PackageManager packageManager = MyApplication.getInstance().getPackageManager();
            try {
                if (bean.getDefBean() != null){
                    info = packageManager.getApplicationInfo(bean.getDefBean().getChannel().getPackageName(), 0);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (bean.getDefBean() == null || !Util.isNoEmptyList(bean.getDefBean().getPrograms())){
                if (mMask != null){
                    mMask.setVisibility(View.VISIBLE);
                }
                itemView.setFocusable(false);
                tvAlias.setText(itemView.getContext().getResources().getString(R.string.channel_is_null));
            }else{
                itemView.setFocusable(true);
                if (mMask != null){
                    mMask.setVisibility(View.GONE);
                }
                if (bean.getDefBean().getChannel().getPackageName().equals(Constants.GOOGLE_RECOMMEND_PACKAGENAME)) {
                    tvAlias.setText(bean.getDefBean().getChannel().getDisplayName());
                } else {
                    if(null != info){
                        tvAlias.setText(info.loadLabel(packageManager).toString());
                    }else{
                        LogX.e("null == info");
                        tvAlias.setText("");
                    }
                }
                if (mSwitch != null){
                    if (bean.getDefBean().isChange()){
                        mSwitch.setChecked(bean.getDefBean().getChangeState() == 1);
                    }else {
                        mSwitch.setChecked(bean.getDefBean().getChannel().isBrowsable());
                    }
                }
            }
            if (bean.getDefBean().getChannel().getPackageName().equals(Constants.GOOGLE_RECOMMEND_PACKAGENAME)) {
                tvName.setText(bean.getDefBean().getChannel().getDisplayName());
            } else {
                if (null != info) {
                    tvName.setText(info.loadLabel(packageManager).toString());
                } else {
                    tvName.setText("");
                }
            }
            ImageLoaderUtils.imageLoaderDisplayImagePkg(logo,bean.getDefBean().getChannel().getPackageName());
        }
    }

    private MoreModeItemClickListener mItemClickListener;

    public void setItemClickListener(MoreModeItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public interface MoreModeItemClickListener{
        public void onItemClick(View view, int position, CustomChannelBean bean);
    }
}
