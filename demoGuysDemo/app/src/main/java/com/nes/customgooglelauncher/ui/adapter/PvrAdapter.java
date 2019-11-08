package com.nes.customgooglelauncher.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nes.base.BaseRecyclerViewAdapter;
import com.nes.base.BaseViewHolder;
import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.PopBean;
import com.nes.customgooglelauncher.ui.widget.CustomTextView;

import java.util.List;

/**
 * 长按PopupWindow --> RecyclerView --> Adapter
 * @author liuqz
 */
public class PvrAdapter extends BaseRecyclerViewAdapter<PopBean, PvrAdapter.PvrViewHolder> {

    public void setPopItemClick(PopItemClick popItemClick) {
        this.popItemClick = popItemClick;
    }

    private PopItemClick popItemClick;

    public PvrAdapter(){

    }

    public PvrAdapter(List<PopBean> items) {
        setList(items);
    }

    @Override
    protected PvrViewHolder onBaseCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pvr_library, parent,false);
        return new PvrViewHolder(view,this);
    }

    @Override
    protected void onBaseBindViewHolder(PvrViewHolder holder, int position) {
        PopBean bean = getItem(position);
        holder.bind(bean);
    }


    private void callClick(int positoin){
       if (popItemClick != null){
           popItemClick.onPopItemClick(positoin,getItem(positoin));
       }
    }

    static class PvrViewHolder extends BaseViewHolder<PvrAdapter> {

        CustomTextView mTv;
        ImageView mIcon;

        PvrViewHolder(View itemView,PvrAdapter adapter) {
            super(itemView,adapter);
            mTv = itemView.findViewById(R.id.tv_title);
            mIcon = itemView.findViewById(R.id.icon);
        }

        void bind(PopBean bean){
            mTv.setText(bean.getName());
            if (!bean.isEnable()) {
                itemView.setFocusable(false);
                mTv.setTextColor(itemView.getContext().getResources().getColor(R.color.txt_uninstall));
            } else {
                itemView.setFocusable(true);
                mTv.setTextColor(itemView.getContext().getResources().getColor(R.color.color_202020));
                itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        mIcon.setSelected(hasFocus);
                        mTv.setTextColor(itemView.getContext().getResources().getColor(hasFocus ? R.color.gray_color : R.color.color_202020));
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAdapter().callClick(getAdapterPosition());
                    }
                });
            }
            if (bean.getRes() != -1) {
                mIcon.setImageResource(bean.getRes());
            }
        }
    }

    public interface PopItemClick{
        void onPopItemClick(int position, PopBean bean);
    }
}
