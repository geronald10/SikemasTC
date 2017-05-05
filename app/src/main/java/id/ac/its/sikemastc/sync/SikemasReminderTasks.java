package id.ac.its.sikemastc.sync;

import android.content.Context;

import id.ac.its.sikemastc.utilities.NotificationUtils;

public class SikemasReminderTasks {
    public static final String ACTION_ACTIVATED_PERKULIAHAN = "activate-perkuliahan";
    public static final String ACTION_RESCHEDULE_NOTIFICATION = "reschedule-perkuliahan";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static void executeTask(Context context, String action) {
        if (ACTION_ACTIVATED_PERKULIAHAN.equals(action)) {
            activatePerkuliahan(context);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
//            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_RESCHEDULE_NOTIFICATION.equals(action)) {
            reschedulePerkuliahan(context);
        }
    }

    private static void activatePerkuliahan(Context context) {
//        PreferenceUtilities.incrementWaterCount(context);
//        NotificationUtils.clearAllNotifications(context);
    }

    private static void reschedulePerkuliahan(Context context) {

//        NotificationUtils.clearAllNotifications(context);
    }
}
