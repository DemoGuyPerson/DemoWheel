package com.nes.utils;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Create By liuqz
 */

public class CornerUtil {

    public static void clipViewCircle(View view) {
        view.setClipToOutline(true);
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
    }

    public static void clipViewCornerByDp(View view, final int pixel) {
        view.setClipToOutline(true);
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), pixel);
            }
        });
    }

    public static void clipViewCornerByResId(View view,int resId){
        clipViewCornerByDp(view, (int)view.getContext().getResources().getDimension(resId));
    }
}
