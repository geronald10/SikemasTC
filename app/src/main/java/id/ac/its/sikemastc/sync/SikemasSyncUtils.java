package id.ac.its.sikemastc.sync;

import android.content.Context;
import android.content.Intent;

public class SikemasSyncUtils {

    public static void startImmediateSync(Context context) {
        Intent intentToSyncImmediately = new Intent(context, SikemasSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}