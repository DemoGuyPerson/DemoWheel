package com.nes.customgooglelauncher.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import androidx.tvprovider.media.tv.Channel;
import androidx.tvprovider.media.tv.PreviewProgram;
import com.nes.utils.LogX;
import com.nes.utils.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * item Click utils
 * @author liuqz
 */
public class ClickUtils {

    public static void clickResolveInfo(Context context, ResolveInfo resolveInfo){
        if (resolveInfo == null){
            LogX.e(" onClick ALL_APP resolveInfo is NULL");
        }else{
            AppUtils.getInstance().launchApp(context,resolveInfo);
        }
    }

    @SuppressLint("RestrictedApi")
    public static void clickPreviewProgram(Context context, PreviewProgram previewProgram){
       if (previewProgram == null){
           LogX.e(" onClick NETFLIX|JIO|OTHER_RECOMMEND previewProgram is NULL");
       }else{
           String pkgName = previewProgram.getPackageName();
           String intentStr = previewProgram.getIntentUri() != null ? previewProgram.getIntentUri().toString() : "";
           if (!TextUtils.isEmpty(pkgName) && !TextUtils.isEmpty(intentStr)){
               Intent intent = null;
               if (pkgName.contains("youtube")){
                   intent = getIntentByYoutube(intentStr);
               }else if(pkgName.equals(Constants.NETFLIX_PACKAGENAME)){
//                   intent = getIntentByNetflix(intentStr);
                   intent = getIntentByAll(intentStr);
               }else if(pkgName.equals(Constants.PRIME_PACKAGENAME)){
                   intent = getIntentByAll(intentStr);
               }else if(pkgName.equals(Constants.JIO_PACKAGENAME)){
                   intent = getIntentByAll(intentStr);
               }else if(pkgName.equals(Constants.GOOGLE_RECOMMEND_PACKAGENAME)){
                   Channel channel = ChannelProgramUtils.getInstance().getChannelByChannelId(previewProgram.getChannelId());
                   if (channel != null){
                       byte[] bytes = channel.getInternalProviderDataByteArray();
                       String detail = new String(bytes);
                       AppUtils.getInstance().launchApp(context, detail);
                   }
               }else{
                   intent = new Intent();
                   intent.setData(previewProgram.getIntentUri());
               }
               if (intent != null){
                   try {
                       context.startActivity(intent);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
               }
           }
       }
    }


     /**
     * intent://XqZsoesa55w?launch=launcher#Intent;scheme=vnd.youtube;package=com.google.android.youtube.tv;end
     * @return
     */
    private static Intent getIntentByYoutube(String intStr){
        Intent result = null;
        if (!TextUtils.isEmpty(intStr)){
            String[] strings = intStr.split(";");
            if (strings.length > 2){
                String pkgName = strings[2].replace("package=", "");
                String scheme = strings[1].replace("scheme=", "");
                if(strings[0].contains("intent") && !TextUtils.isEmpty(pkgName) && !TextUtils.isEmpty(scheme)){
                    if (strings[0].contains("#")){
                        strings[0] = strings[0].split("#")[0];
                    }
                    String uri = strings[0].replaceFirst("intent", scheme);
                    result = new Intent();
                    result.setPackage(pkgName);
                    result.setData(Uri.parse(uri));
                }
            }
        }
        return result;
    }


    private static Intent getIntentByAll(String intStr){
        Intent intent = null;
        if (!TextUtils.isEmpty(intStr)){
            String[] strings = intStr.split(";");
            if (strings.length>0){
                for (int i=0;i<strings.length;i++){
                    if (!TextUtils.isEmpty(strings[i])){
                        if (strings[i].endsWith("#Intent")){
                            /**设置uri*/
                           String[] strs = strings[i].split("#");
                           if (!TextUtils.isEmpty(strs[0])){
                               String uri = strs[0];
                               if (strs[0].startsWith("intent:")){
                                   uri = uri.replaceAll("intent:","");
                               }
                               if (!TextUtils.isEmpty(uri.trim())){
                                   if (intent == null) {
                                       intent = new Intent(Intent.ACTION_VIEW);
                                   }
                                   LogX.d("getIntentByAll uri : " + uri);
                                   intent.setData(Uri.parse(uri.trim()));
                               }
                           }
                        }else if (strings[i].contains("=")) {
                            String[] strings1 = strings[i].split("=");
                            if (strings1.length > 1 && !TextUtils.isEmpty(strings1[0])) {
                                if (strings1[0].equals("component")) {
                                    /**设置component*/
                                    if (strings1[1].contains("/")) {
                                        String[] strings2 = strings1[1].split("/");
                                        if (strings2.length > 1) {
                                            if (intent == null) {
                                                intent = new Intent(Intent.ACTION_VIEW);
                                            }
                                            String classPath = strings2[1].startsWith(".") ? (strings2[0] + strings2[1]) : strings2[1];
                                            LogX.d("getIntentByAll pkgName : " + strings2[0] + " classPath : " + classPath);
                                            intent.setComponent(new ComponentName(strings2[0], classPath));
                                        }
                                    }
                                } else if (strings1[0].equals("launchFlags")) {
                                    /**设置flag*/
                                    LogX.d("getIntentByAll flag : " + strings1[1]);
                                    if (intent == null) {
                                        intent = new Intent(Intent.ACTION_VIEW);
                                    }
                                    intent.setFlags(Util.stringToInt(strings1[1]));
                                } else if (strings1[0].contains(".")) {
                                    /**设置Extra*/
                                    String[] _strings = strings1[0].split("\\.");
                                    if (!TextUtils.isEmpty(_strings[0]) && !TextUtils.isEmpty(_strings[1])) {
                                        String key = urlDecodeStr(_strings[1]);
                                        if (intent == null) {
                                            intent = new Intent(Intent.ACTION_VIEW);
                                        }
                                        switch (_strings[0]) {
                                            case "i":
                                                LogX.d("getIntentByAll Extra key : " + key + " int value : " + Util.stringToInt(strings1[1]));
                                                intent.putExtra(key, Util.stringToInt(strings1[1]));
                                                break;
                                            case "S":
                                                LogX.d("getIntentByAll Extra key : " + key + " string value : " + urlDecodeStr(strings1[1]));
                                                intent.putExtra(key, urlDecodeStr(strings1[1]));
                                                break;
                                            case "B":
                                                LogX.d("getIntentByAll Extra key : " + key + " boolean value : " + Boolean.parseBoolean(strings1[1]));
                                                intent.putExtra(key, Boolean.parseBoolean(strings1[1]));
                                                break;
                                            case "D":
                                            case "d":
                                                LogX.d("getIntentByAll Extra key : " + key + " double value : " + Util.stringToDouble(strings1[1]));
                                                intent.putExtra(key, Util.stringToDouble(strings1[1]));
                                                break;
                                            case "L":
                                            case "l":
                                                LogX.d("getIntentByAll Extra key : " + key + " long value : " + Util.stringToLong(strings1[1]));
                                                intent.putExtra(key, Util.stringToLong(strings1[1]));
                                                break;
                                            case "F":
                                            case "f":
                                                LogX.d("getIntentByAll Extra key : " + key + " float value : " + Util.stringToFloat(strings1[1]));
                                                intent.putExtra(key, Util.stringToFloat(strings1[1]));
                                                break;
                                            default:

                                                break;
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return intent;
    }

    private static String urlDecodeStr(String str){
        String result = "";
        if (!TextUtils.isEmpty(str)){
            try {
                result = URLDecoder.decode(str,"UTF-8");
                System.out.println(" result : "+result);
                if (result.contains("%")){
                    result = urlDecodeStr(result);
                }
            }catch (IllegalArgumentException | UnsupportedEncodingException e){
                e.printStackTrace();
                result = str;
            }
        }
        return result;
    }
}
