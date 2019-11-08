package com.nes.customgooglelauncher.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nes.customgooglelauncher.R;
import com.nes.customgooglelauncher.bean.HomeDetailBean;
import com.nes.customgooglelauncher.utils.ImageLoaderUtils;
import com.nes.customgooglelauncher.utils.Utils;
import com.nes.utils.Util;

/**
 * item详情ConstraintLayout
 * @author liuqz
 */
public class HomeDetailsLayout extends ConstraintLayout {

    private CustomTextView mTvName;
    private CustomTextView mYear1,mYear2;
    private RatingBar mRatingBar;
    private ImageView mImageType;
    private CustomTextView mTvTime,mTvDetails,mTvFraction;
    public HomeDetailsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_home_details, this, true);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        init();
    }

    private void init(){
        mTvName = findViewById(R.id.tv_name);
        mYear1 = findViewById(R.id.tv_year);
        mYear2 = findViewById(R.id.tv_year1);
        mRatingBar = findViewById(R.id.ratingBar);
        mImageType = findViewById(R.id.img_type);
        mTvTime = findViewById(R.id.tv_time);
        mTvDetails = findViewById(R.id.tv_detail);
        mTvFraction = findViewById(R.id.tv_fraction);
    }

    public void bind(HomeDetailBean bean){
        if (bean != null) {
            setTitle(Util.getDefultStr(bean.getTitle(), "NULL"));
            setRatingBarNum(bean.getRatting());
            setContent(mTvTime, bean.getTime());
            setContent(mYear1, !TextUtils.isEmpty(bean.getRattingStr()) ? ("("+bean.getRattingStr()+")") : "","    ");
            setContent(mYear2, bean.getYear());
            setContent(mTvFraction, bean.getScore());
            setContent(mTvDetails, bean.getDetails());
            setImage(bean.getSmallIcon());
        }
    }

    private void setImage(String url){
        if (!TextUtils.isEmpty(url)){
            mImageType.setVisibility(View.VISIBLE);
            ImageLoaderUtils.imageLoaderDisplayImageAllType(mImageType,url);
        }else{
            mImageType.setVisibility(View.GONE);
        }
    }

    private void setContent(TextView textView,String content,String defultStr){
//        textView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.INVISIBLE);
        textView.setText(Util.getDefultStr(content,defultStr));
    }

    private void setContent(TextView textView,String content){
//        textView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.INVISIBLE);
        textView.setText(Util.getDefultStr(content,""));
    }


    public void setTitle(String title){
        if (mTvName != null){
            mTvName.setText(title);
        }
    }

    public void setTime(String time){
        if (mTvTime != null){
            mTvTime.setText(time);
        }
    }

    public void setDetail(String detail){
        if (mTvDetails != null){
            mTvDetails.setText(detail);
        }
    }

    public void setRatingBarNum(float ratingBarNum){
        if (mRatingBar != null){
            if (ratingBarNum < 0){
                mRatingBar.setVisibility(View.INVISIBLE);
            }else{
                mRatingBar.setVisibility(View.VISIBLE);
                mRatingBar.setRating(ratingBarNum);
            }
        }
    }
}
