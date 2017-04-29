package id.ac.its.sikemastc.utilities;

public class NotificationUtils {

//    private static final int SIKEMAS_REMINDER_NOTIFICATION_ID = 1138;
//    private static final int SIKEMAS_REMINDER_PENDING_INTENT_ID = 3417;
//    private static final int ACTION_ACTIVATE_PENDING_INTENT_ID = 1;
//    private static final int ACTION_RESCHEDULE_PENDING_INTENT_ID = 14;
//    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 30;
//
//    public static void clearAllNotifications(Context context) {
//        NotificationManager notificationManager = (NotificationManager)
//                context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancelAll();
//    }
//
//    public static void remindUserJadwalPerkuliahan(Context context) {
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
//                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
//                .setSmallIcon(R.drawable.ic_drink_notification)
//                .setLargeIcon(largeIcon(context))
//                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
//                .setContentText(context.getString(R.string.charging_reminder_notification_body))
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(
//                        context.getString(R.string.charging_reminder_notification_body)))
//                .setDefaults(Notification.DEFAULT_VIBRATE)
//                .setContentIntent(contentIntent(context))
//                // COMPLETED (17) Add the two new actions using the addAction method and your helper methods
//                .addAction(drinkWaterAction(context))
//                .addAction(ignoreReminderAction(context))
//                .setAutoCancel(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
//        }
//
//        NotificationManager notificationManager = (NotificationManager)
//                context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        /* WATER_REMINDER_NOTIFICATION_ID allows you to update or cancel the notification later on */
//        notificationManager.notify(SIKEMAS_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
//    }
//    //  COMPLETED (5) Add a static method called ignoreReminderAction
//    private static NotificationCompat.Action ignoreReminderAction(Context context) {
//        // COMPLETED (6) Create an Intent to launch WaterReminderIntentService
//        Intent ignoreReminderIntent = new Intent(context, SikemasReminderIntentService.class);
//        // COMPLETED (7) Set the action of the intent to designate you want to dismiss the notification
//        ignoreReminderIntent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION);
//        // COMPLETED (8) Create a PendingIntent from the intent to launch WaterReminderIntentService
//        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
//                context,
//                ACTION_IGNORE_PENDING_INTENT_ID,
//                ignoreReminderIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        // COMPLETED (9) Create an Action for the user to ignore the notification (and dismiss it)
//        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_cancel_black_24px,
//                "No, thanks.",
//                ignoreReminderPendingIntent);
//        // COMPLETED (10) Return the action
//        return ignoreReminderAction;
//    }
//
//    //  COMPLETED (11) Add a static method called drinkWaterAction
//    private static NotificationCompat.Action drinkWaterAction(Context context) {
//        // COMPLETED (12) Create an Intent to launch WaterReminderIntentService
//        Intent incrementWaterCountIntent = new Intent(context, SikemasReminderIntentService.class);
//        // COMPLETED (13) Set the action of the intent to designate you want to increment the water count
//        incrementWaterCountIntent.setAction(ReminderTasks.ACTION_ACTIVATED_PERKULIAHAN);
//        // COMPLETED (14) Create a PendingIntent from the intent to launch WaterReminderIntentService
//        PendingIntent incrementWaterPendingIntent = PendingIntent.getService(
//                context,
//                ACTION_ACTIVATE_PENDING_INTENT_ID,
//                incrementWaterCountIntent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//        // COMPLETED (15) Create an Action for the user to tell us they've had a glass of water
//        NotificationCompat.Action drinkWaterAction = new NotificationCompat.Action(R.drawable.ic_local_drink_black_24px,
//                "I did it!",
//                incrementWaterPendingIntent);
//        // COMPLETED (16) Return the action
//        return drinkWaterAction;
//    }
//
//    private static PendingIntent contentIntent(Context context) {
//        Intent startActivityIntent = new Intent(context, HalamanUtamaDosen.class);
//        return PendingIntent.getActivity(
//                context,
//                SIKEMAS_REMINDER_PENDING_INTENT_ID,
//                startActivityIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    private static Bitmap largeIcon(Context context) {
//        Resources res = context.getResources();
//        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_local_drink_black_24px);
//        return largeIcon;
//    }
}
