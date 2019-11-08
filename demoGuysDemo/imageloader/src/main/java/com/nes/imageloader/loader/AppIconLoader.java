package com.nes.imageloader.loader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.ChannelLogoUtils;
import androidx.tvprovider.media.tv.TvContractCompat;

import com.nes.imageloader.disklrucache.IOUtil;
import com.nes.imageloader.request.BitmapRequest;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.util.List;

/**
 * app Icon类型的图片加载器
 *
 * @author liuqz
 * @date 2019/5/16
 */

public class AppIconLoader extends AbsLoader{
    private String TAG = AppIconLoader.class.getSimpleName();

    private boolean isCanLog(String pkgName){
       return !TextUtils.isEmpty(pkgName);
//               && (pkgName.equals("com.jio.media.stb.ondemand") || pkgName.equals("com.amazon.amazonvideo.livingroom"));
    }
    @Override
    protected Bitmap onLoadImage(BitmapRequest result) {
        Bitmap bitmap = null;
        String packageName = getPkgName(result.imageUri);
        LogX.d(TAG+" onLoadImage start ... pkgName : "+packageName);
        Drawable drawable = null;
        Context context = result.getImageView().getContext();
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            //获取banner图标
            drawable = packageManager.getApplicationBanner(packageName);
            if (isCanLog(packageName)){
                LogX.d(TAG+" onLoadImage packageName : "+packageName+" getApplicationBanner drawable == null : "+(drawable == null));
            }
            if (drawable==null){
            //处理Banner附着在activity上
                Intent intent1 = packageManager.getLeanbackLaunchIntentForPackage(packageName);
                drawable = packageManager.getActivityBanner(intent1);
                if (isCanLog(packageName)){
                    LogX.d(TAG+" onLoadImage packageName : "+packageName+" getLeanbackLaunchIntentForPackage getActivityBanner drawable == null : "+(drawable == null));
                }
                if (drawable == null){
                    Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                    if (intent !=null){
                        drawable = packageManager.getActivityBanner(intent);
                        if (isCanLog(packageName)){
                            LogX.d(TAG+" onLoadImage packageName : "+packageName + " getLaunchIntentForPackage getActivityBanner drawable == null : "+(drawable == null));
                        }
                    }
                }
            }

            if (drawable == null) {
                Intent AppsIntent = new Intent(Intent.ACTION_MAIN, null);
                AppsIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
                List<ResolveInfo> apps = packageManager.queryIntentActivities(AppsIntent, 0);
                for (ResolveInfo app : apps) {
                    if (app.activityInfo.packageName.equals(packageName)) {
                        drawable = app.activityInfo.loadBanner(packageManager);
                        break;
                    }
                }
                if (isCanLog(packageName)){
                    LogX.d(TAG+" onLoadImage packageName : "+packageName+" loadBanner drawable == null : "+(drawable == null));
                }
            }
            //获取Logo图标
            if (drawable == null) {
                drawable = packageManager.getApplicationLogo(packageName);
                if (isCanLog(packageName)){
                    LogX.d(TAG+" onLoadImage packageName : "+packageName +" getApplicationLogo drawable == null : "+(drawable == null));
                }
            }
            //获取Icon
            if (drawable == null) {
                ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
                drawable = info.loadIcon(packageManager);
                if (isCanLog(packageName)){
                    LogX.d(TAG+" onLoadImage packageName : " +packageName+ " loadIcon drawable == null : "+(drawable == null));
                }
            } //获取推荐APK图标
        } catch (Exception e) {
            e.printStackTrace();
            LogX.e("PlayNext logo error:" + e.getMessage());
        }
        if (drawable == null) {
            bitmap = ChannelLogoUtils.loadChannelLogo(context, getChannelIdByPackageName(context,packageName));
            if (isCanLog(packageName)){
                LogX.d(TAG+" onLoadImage packageName : "+packageName+" loadChannelLogo bitmap == null : "+(bitmap == null));
            }
        }else{
            bitmap = Util.drawableToBitmap(drawable);
            if (isCanLog(packageName)){
                LogX.d(TAG+" onLoadImage packageName : "+packageName+" drawableToBitmap bitmap == null : "+(bitmap == null));
            }
        }
        if (bitmap != null){
            LogX.e(TAG+" onLoadImage packageName : "+packageName+" width : "
                    +bitmap.getWidth()+" height : "+bitmap.getHeight()+" getAllocationByteCount : "
                    +Util.getBitmapSize(bitmap)+" getByteCount : "+bitmap.getByteCount()+" * : "+(bitmap.getRowBytes() * bitmap.getHeight()));
        }else{
            LogX.e(TAG+" onLoadImage packageName : "+packageName+" width : "+0+" height : "+0);
        }
        return bitmap;
    }


    private String getPkgName(String uri){
        String result = "";
        if (!TextUtils.isEmpty(uri) && uri.contains("://")){
            String[] strings = uri.split("://");
            if (strings.length >= 2) {
                result = strings[1];
            }
        }
        return result;
    }

    private long getChannelIdByPackageName(Context context, String packageName) {
        long channelid = -1;
        Cursor cursor = null;
        //从数据库拿
        try {
            cursor = context.getContentResolver().query(TvContractCompat.Channels.CONTENT_URI, null, "package_name=?", new String[]{packageName}, null);
            while (cursor != null && cursor.moveToNext()) {
                Channel channel = Channel.fromCursor(cursor);
                channelid = channel.getId();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(cursor);
        }
        return channelid;
    }
}
