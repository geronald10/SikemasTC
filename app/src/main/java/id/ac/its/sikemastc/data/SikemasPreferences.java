package id.ac.its.sikemastc.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import id.ac.its.sikemastc.R;

public final class SikemasPreferences {
    public static boolean areNotificationsEnabled(Context context) {
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean shouldDisplayNotificationsByDefault = true;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean shouldDisplayNotifications = sp
                .getBoolean(displayNotificationsKey, shouldDisplayNotificationsByDefault);

        return shouldDisplayNotifications;
    }
}
