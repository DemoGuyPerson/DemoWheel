package com.nes.customgooglelauncher.utils;

import android.content.pm.ResolveInfo;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Lund
 */

public class Constants {

    public static final String CATEGORY_LEANBACK_SETTINGS =
            "android.intent.category.LEANBACK_SETTINGS";
    public static final String HATHWAY_PACKAGENAME = "com.amlogic.DVBPlayer";
    public static final String GOOGLE_TV_RECOMMEND_PACKAGENAME = "com.google.android.tvrecommendations";

    public static final String ZEE5_PACKAGENAME = "com.graymatrix.did";
    public static final String HIDE_APPS_NESUPDATE_PACKAGE_NAME = "com.nes.update";
    public static final String PLAYSTORE_PACKAGENAME = "com.android.vending";
    public static final String GOOGLEGAME_PACKAGENAME = "com.google.android.play.games";
    public static final String GOOGLE_RECOMMEND_PACKAGENAME = "com.google.android.tvrecommendations";
    public static final String NETFLIX_PACKAGENAME = "com.netflix.ninja";
    public static final String JIO_PACKAGENAME = "com.jio.media.stb.ondemand";
    public static final String PRIME_PACKAGENAME = "com.amazon.amazonvideo.livingroom";
    public static final String SONY_PACKAGENAME = "com.tv.sonyliv";
    public static final String HOOQ_PACKAGENAME = "tv.hooq.android";
    public static final String VOOT_PACKAGENAME = "com.viacom18.tv.voot";
    public static final String HOSTSTR_PACKAGENAME = "in.startv.hotstar";
    public static final String ADD_PACKAGENAME = "com.nes.add.hathway";

    public static final String SUN_PACKAGENAME = "com.suntv.sunnxt";
    public static final String YUPPTV_PACKAGENAME = "com.yupptv.androidtv";

    public static ResolveInfo PLAYSTORE_INFO, GOOGLEGAME_INFO;

    //------------------------FLAG---------------------------

    public static final int FLAG_APP = 0x10000;
    public static final int FLAG_GAME = 0x10001;
    public static final int FLAG_OTHER = 0x10002;

    /**
     * 放大倍数
     */
    public static final float SCALE = 1.17f;

    public static final boolean HIDE_PLAY_NEXT = true;

    /**
     * notifyItemInserted(int position)
     */
    public static final int TYPE_NOTIFY_INSERTED = 11;
    /**
     * notifyItemRemoved(int position)
     */
    public static final int TYPE_NOTIFY_REMOVE = 12;
    /**
     * notifyDataSetChanged() OR notifyItemChanged(int position)
     */
    public static final int TYPE_NOTIFY_CHANGED = 13;

    /**
     * 默认类型
     */
    public static final int TYPE_NOTIFY_DEFULT = 0;


}
