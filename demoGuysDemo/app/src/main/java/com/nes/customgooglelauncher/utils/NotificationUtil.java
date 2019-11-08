package com.nes.customgooglelauncher.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.nes.utils.LogX;

/**
 *
 * @author zhurongkun
 */

public class NotificationUtil {
    private static final String PATH_NOTIFS = "notifications";
    private static final String PATH_NOTIFS_COUNT = PATH_NOTIFS + "/count";
    public static final String COLUMN_COUNT = "count";
    /**
     * Content provider for notifications
     */
    private static final String AUTHORITY =
            "com.android.tv.notifications.NotificationContentProvider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" +
            PATH_NOTIFS);
    public static final Uri NOTIFS_COUNT_URI = Uri.parse("content://" + AUTHORITY + "/" +
            PATH_NOTIFS_COUNT);


    /**
     * Title of notification (TEXT)
     */
    public static final String COLUMN_NOTIF_TITLE = "title";
    /**
     * The second line of text (TEXT)
     */
    public static final String COLUMN_NOTIF_TEXT = "text";


    public static void registerObserver(Context context, ContentObserver observer) {
        context.getContentResolver().registerContentObserver(NOTIFS_COUNT_URI, true, observer);


    }

    public static void unregisterObserver(Context context, ContentObserver observer) {
        context.getContentResolver().unregisterContentObserver(observer);
    }

    public static int getNotificationCount(Context context) {
        int c = 0;
        try {
            Cursor query = context.getContentResolver().query(NOTIFS_COUNT_URI, null, null, null, null);

            c = 0;
            if (query != null) {
                c = query.getCount();
                LogX.i("NotificationCount " + c);
            }
            query.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogX.e("NotificationCount error");
        }
        return c;
    }
}
