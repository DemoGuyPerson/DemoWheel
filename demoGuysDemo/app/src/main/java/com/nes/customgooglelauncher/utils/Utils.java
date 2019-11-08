package com.nes.customgooglelauncher.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;
import androidx.tvprovider.media.tv.TvContractCompat;

import com.nes.customgooglelauncher.MyApplication;
import com.nes.customgooglelauncher.bean.HomeDetailBean;
import com.nes.customgooglelauncher.ui.widget.HomeType;
import com.nes.imageloader.loader.LoaderManager;
import com.nes.utils.Util;

import java.util.Arrays;
import java.util.List;

/**
 * @author liuqz
 */
public class Utils {

    public static String getChannelShowTitle(Channel channel){
        String result = "";
        if (channel != null){
            if (channel.getPackageName().equals(Constants.GOOGLE_RECOMMEND_PACKAGENAME)){
                result = channel.getDisplayName();
            }else{
                result = AppUtils.getInstance().getAppName(MyApplication.getInstance(),channel.getPackageName()) + " - " + channel.getDisplayName();
            }
        }
        return result;
    }

    @SuppressLint("RestrictedApi")
    public static int getWidthByPreviewProgram(PreviewProgram program,int height){
        int result = -1;
        int posterArtAspectRatio = program == null ? TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9 : program.getPosterArtAspectRatio();
        if (posterArtAspectRatio == TvContractCompat.PreviewPrograms.ASPECT_RATIO_2_3) {
            result = (height * 2) / 3;
        } else if (posterArtAspectRatio == TvContractCompat.PreviewPrograms.ASPECT_RATIO_1_1) {
            result = height;
        } else if (posterArtAspectRatio == TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9) {
            result = (height * 16) / 9;
        } else if (posterArtAspectRatio == TvContractCompat.PreviewPrograms.ASPECT_RATIO_4_3) {
            result = (height * 4) / 3;
        } else {
            result = (height * 16) / 9;
        }
        return result;
    }

    public static int getDimension(Context context, int resId){
        return context == null ? -1 : (int)context.getResources().getDimension(resId);
    }

    @SuppressLint("RestrictedApi")
    private static String getSmallIcon(PreviewProgram previewProgram){
        String result = "";
        if (previewProgram != null){
            if (previewProgram.getLogoUri() != null){
                result = LoaderManager.HTTP+previewProgram.getLogoUri().toString();
            }else if(!TextUtils.isEmpty(previewProgram.getPackageName())){
                result = LoaderManager.APP_ICON+previewProgram.getPackageName();
            }
        }
        return result;
    }

    private static String getTimeByMilliseconds(int milliseconds){
        if (milliseconds <= 0){
            return "";
        }
        long hours = milliseconds / (1000 * 60 * 60);
        long minutes = (milliseconds - hours * (1000 * 60 * 60 ))/(1000* 60);
        return (hours > 0 ? (hours+" hr ") : "")+minutes + " min";
    }

    @SuppressLint({"RestrictedApi", "WrongConstant"})
    private static float getRattingFloat(PreviewProgram previewProgram){
        float result = -1F;
        if (previewProgram != null){
            //BasePreviewProgram
            if (previewProgram.getReviewRatingStyle() == 0){
                /**五星级评星风格*/
                result = (float) Util.stringToDouble(previewProgram.getReviewRating());
            }
        }
        return result;
    }

    @SuppressLint("RestrictedApi")
    private static String getBigImageUrl(PreviewProgram previewProgram){
        String result = "";
        if (previewProgram != null){
            if (previewProgram.getPosterArtUri() != null){
                result = previewProgram.getPosterArtUri().toString();
            }
        }
        return result;
    }

    @SuppressLint("RestrictedApi")
    public static HomeDetailBean getHomeDetailsBeanByPreviewProgram(PreviewProgram program){
        if (program == null){
            return null;
        }
        return new HomeDetailBean.Builder()
                .addTitle(program.getTitle())
                .addDetails(program.getDescription())
                .addYear(program.getReleaseDate())
                .addSmallIcon(Utils.getSmallIcon(program))
                .addTime(Utils.getTimeByMilliseconds(program.getDurationMillis()))
                .addRatting(Utils.getRattingFloat(program))
                .addBigIcon(Utils.getBigImageUrl(program))
                .build();
    }


    public static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<HomeType> getListByArray(HomeType ... homeTypes){
        return Arrays.asList(homeTypes);
    }

}
