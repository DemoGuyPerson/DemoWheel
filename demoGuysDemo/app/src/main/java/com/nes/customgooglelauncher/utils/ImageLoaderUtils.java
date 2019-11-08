package com.nes.customgooglelauncher.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.nes.customgooglelauncher.R;
import com.nes.imageloader.config.DisplayConfig;
import com.nes.imageloader.config.ImageLoaderConfig;
import com.nes.imageloader.core.SimpleImageLoader;
import com.nes.imageloader.loader.LoaderManager;
import com.nes.utils.LogX;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * 加载图片的Utils
 * @author liuqz
 */
public class ImageLoaderUtils {

    private static int getLoadingResId(){
        return R.drawable.defult_1;
    }
    private static int getErrorResId(){
        return R.drawable.defult_1;
    }
    public static int getDefultResId(){
        return R.drawable.defult_1;
    }

    private static void imageLoaderDisplayImage(ImageView imageView, String pkgName,int loadingResId,int errorResId){
        int resId = getDrawbleResbyPakcage(pkgName);
        LogX.d(" imageLoaderDisplayImage pkgName : "+pkgName+" resId : "+resId);
        if (resId == -1){
            SimpleImageLoader.getInstance().displayImage(imageView, LoaderManager.APP_ICON+"://"+pkgName,
                    new DisplayConfig(loadingResId,errorResId));
        }else{
            imageView.setImageResource(resId);
        }
    }

    private static void imageLoaderDisplayImageHttp(ImageView imageView, String url, int loadingResId, int errorResId){
        imageLoaderDisplayImageHttp(imageView,url,loadingResId,errorResId,false);
    }

    public static void imageLoaderDisplayImageHttp(ImageView imageView, String url, int loadingResId, int errorResId,boolean isTransition){
        if (isTransition) {
            Glide.with(imageView.getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(loadingResId)
                    .error(errorResId).transition(withCrossFade()).into(imageView);
        }else{
            Glide.with(imageView.getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(loadingResId)
                    .error(errorResId).into(imageView);
        }
//        imageView.setImageResource(getDefultResId());
    }

    public static void imageLoaderDisplayImageUri(ImageView imageView, Uri uri, int defultImage){
        Glide.with(imageView.getContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(defultImage)
                .error(defultImage).into(imageView);
//        imageView.setImageResource(getDefultResId());
    }
    public static void imageLoaderDisplayImageUri(ImageView imageView, Uri uri){
        imageLoaderDisplayImageUri(imageView,uri,getDefultResId());
    }

    public static void imageLoaderDisplayImagePkg(ImageView imageView, String pkgName,int defultImage){
        imageLoaderDisplayImageAllType(imageView,LoaderManager.APP_ICON+pkgName,defultImage);
    }

    public static void imageLoaderDisplayImagePkg(ImageView imageView, String pkgName){
        imageLoaderDisplayImagePkg(imageView,pkgName,getDefultResId());
    }

    public static void imageLoaderDisplayImageHttp(ImageView imageView, String url,int defultImage){
        imageLoaderDisplayImageAllType(imageView,LoaderManager.HTTP+url,defultImage);
    }

    public static void imageLoaderDisplayImageHttp(ImageView imageView, String url){
        imageLoaderDisplayImageHttp(imageView,url,getDefultResId());
    }

    public static void imageLoaderDisplayImageAllType(ImageView imageView,String url,int defultImage){
        imageLoaderDisplayImageAllType(imageView,url,defultImage,defultImage);
    }

    public static void imageLoaderDisplayImageAllType(ImageView imageView,String url){
        imageLoaderDisplayImageAllType(imageView,url,getDefultResId());
    }

    public static void imageLoaderDisplayImageAllType(ImageView imageView,String url,int loadingResId, int errorResId){
        if (TextUtils.isEmpty(url)){
            imageView.setImageResource(errorResId);
            return;
        }
        if (url.startsWith(LoaderManager.APP_ICON)){
            imageLoaderDisplayImage(imageView,url.substring(LoaderManager.APP_ICON.length()),loadingResId,errorResId);
        }else if(url.startsWith(LoaderManager.HTTP)){
            imageLoaderDisplayImageHttp(imageView,url.substring(LoaderManager.HTTP.length()),loadingResId,errorResId);
        }else{
            imageLoaderDisplayImageHttp(imageView,url,loadingResId,errorResId);
        }
    }

    public static int getDrawbleResbyPakcage(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return -1;
        }
        if (Constants.SUN_PACKAGENAME.equals(packageName)) {
            return R.drawable.app_sunnxt;
        }

        if (Constants.YUPPTV_PACKAGENAME.equals(packageName)) {
            return R.drawable.app_yupptv;
        }

        if (Constants.ZEE5_PACKAGENAME.equals(packageName)) {
            return R.drawable.app_zee5;
        }

        if (Constants.SONY_PACKAGENAME.equals(packageName)) {
            return R.drawable.app_sonyliv;
        }

        if (Constants.HOOQ_PACKAGENAME.equals(packageName)) {
            return R.drawable.app_hooq;
        }

        if (Constants.VOOT_PACKAGENAME.equals(packageName)) {
            return R.drawable.app_voot;
        }

        if (Constants.HOSTSTR_PACKAGENAME.equals(packageName)) {
            return R.drawable.app_hotstar;
        }

        if (Constants.JIO_PACKAGENAME.equals(packageName)) {
            return R.drawable.app_jiocinema;
        }

        if (Constants.PRIME_PACKAGENAME.equals(packageName)) {
            return R.drawable.app_prime;
        }

        if (Constants.ADD_PACKAGENAME.equals(packageName)){
            return R.drawable.ic_app_add;
        }
        return -1;
    }
}
