package com.nes.customgooglelauncher.bean;

import androidx.annotation.NonNull;

/**
 * item详情实体类
 * @author liuqz
 */
public class HomeDetailBean {
    /**title*/
    private String mTitle;
    /**几颗星*/
    private float mRatting;
    /**星星后面的文字*/
    private String mRattingStr;
    /**小的icon*/
    private String mSmallIcon;
    /**分数*/
    private String mScore;
    /**时间:小时/分*/
    private String mTime;
    /**年份*/
    private String mYear;
    /**详情*/
    private String mDetails;
    /**背景图*/
    private String mBigIcon;

    public HomeDetailBean(){

    }

    public HomeDetailBean(Builder builder){
        mTitle = builder.mTitle;
        mRatting = builder.mRatting;
        mRattingStr = builder.mRattingStr;
        mSmallIcon = builder.mSmallIcon;
        mScore = builder.mScore;
        mTime = builder.mTime;
        mYear = builder.mYear;
        mTitle = builder.mTitle;
        mDetails = builder.mDetails;
        mBigIcon = builder.mBigIcon;
    }


    public static class Builder{
        private String mTitle;
        private float mRatting;
        private String mRattingStr;
        private String mSmallIcon;
        private String mScore;
        private String mTime;
        private String mYear;
        private String mDetails;
        private String mBigIcon;

        public Builder(){

        }

        public HomeDetailBean build(){
            return new HomeDetailBean(this);
        }

        public Builder addTitle(String title){
            mTitle = title;
            return this;
        }

        public Builder addRatting(float ratting){
            mRatting = ratting;
            return this;
        }

        public Builder addRattingStr(String rattingStr){
            mRattingStr = rattingStr;
            return this;
        }
        public Builder addSmallIcon(String smallIcon){
            mSmallIcon = smallIcon;
            return this;
        }
        public Builder addScore(String score){
            mScore = score;
            return this;
        }
        public Builder addTime(String time){
            mTime = time;
            return this;
        }
        public Builder addYear(String year){
            mYear = year;
            return this;
        }
        public Builder addDetails(String details){
            mDetails = details;
            return this;
        }
        public Builder addBigIcon(String bigIcon){
            mBigIcon = bigIcon;
            return this;
        }

    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public float getRatting() {
        return mRatting;
    }

    public void setRatting(int ratting) {
        mRatting = ratting;
    }

    public String getRattingStr() {
        return mRattingStr;
    }

    public void setRattingStr(String rattingStr) {
        mRattingStr = rattingStr;
    }

    public String getSmallIcon() {
        return mSmallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        mSmallIcon = smallIcon;
    }

    public String getScore() {
        return mScore;
    }

    public void setScore(String score) {
        mScore = score;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public String getBigIcon() {
        return mBigIcon;
    }

    public void setBigIcon(String bigIcon) {
        mBigIcon = bigIcon;
    }

    @NonNull
    @Override
    public String toString() {
        return "HomeDetailBean{" +
                "mTitle='" + mTitle + '\'' +
                ", mRatting=" + mRatting +
                ", mRattingStr='" + mRattingStr + '\'' +
                ", mSmallIcon='" + mSmallIcon + '\'' +
                ", mScore='" + mScore + '\'' +
                ", mTime='" + mTime + '\'' +
                ", mYear='" + mYear + '\'' +
                ", mDetails='" + mDetails + '\'' +
                ", mBigIcon='" + mBigIcon + '\'' +
                '}';
    }
}
