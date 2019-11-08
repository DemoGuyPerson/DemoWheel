package com.nes.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import java.util.List;
import java.util.Map;

public class Util {

    public static String getDefultStr(String str,String defultStr){
        return TextUtils.isEmpty(str) ? defultStr : str;
    }

    public static boolean isNoEmptyList(List list){
        if (list!=null && list.size()>0){
            return true;
        }
        return false;
    }

    public static boolean isNoEmptyMap(Map map){
        if (map!=null && map.size()>0){
            return true;
        }
        return false;
    }

    public static Bitmap drawableToBitmap(Drawable drawable){
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable){
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            if (bitmapDrawable.getBitmap()!=null){
                return bitmapDrawable.getBitmap();
            }
        }

        int width = drawable.getIntrinsicWidth() <= 0 ? 1 : drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight() <= 0 ? 1 : drawable.getIntrinsicHeight();
        bitmap = Bitmap.createBitmap(width,height,drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,width,height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static double stringToDouble(String str){
        double result = 0D;
        if (str != null && !TextUtils.isEmpty(str.trim())){
            try {
                result = Double.parseDouble(str.trim());
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static int stringToInt(String string){
        int result = -1;
        if (string != null && !TextUtils.isEmpty(string.trim())){
            try {
                if (string.startsWith("0x")){
                    result = Integer.parseInt(string.substring(2),16);
                }else{
                    result = Integer.parseInt(string,10);
                }
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static long stringToLong(String str){
        long result = -1L;
        if (str != null && !TextUtils.isEmpty(str.trim())){
            try {
                result = Long.parseLong(str.trim());
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static float stringToFloat(String str){
        float result = -1F;
        if (str != null && !TextUtils.isEmpty(str.trim())){
            try {
                result = Float.parseFloat(str.trim());
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        //API 19
        return bitmap.getAllocationByteCount();
    }

    public static long ObjectToLong(Object o){
        long result = -1;
        if (o != null){
            String str = o.toString();
            result = stringChatAtToLong(str);
        }
        return result;
    }

    public static long stringChatAtToLong(String str){
        long result = -1;
        if (!TextUtils.isEmpty(str)){
            for (int i=0; i<str.length(); i++){
                result += str.charAt(i);
            }
        }
        return result;
    }
}
