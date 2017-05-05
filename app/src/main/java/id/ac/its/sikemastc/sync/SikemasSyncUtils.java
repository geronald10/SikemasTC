package id.ac.its.sikemastc.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import id.ac.its.sikemastc.data.SikemasContract;

public class SikemasSyncUtils {

    private static final int SYNC_INTERVAL_HOURS = 2;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    // Tag to identify sync job
    public static final String SIKEMAS_PERKULIAHAN_SYNC_TAG = "sikemas-perkuliahan-sync";

    static void FirebaseJobDispatcherSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        // Create the Job to periodically sync list perkuliahan.
        Job syncSikemasJob = dispatcher.newJobBuilder()
                .setService(PerkuliahanFirebaseJobService.class)
                .setTag(SIKEMAS_PERKULIAHAN_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncSikemasJob);
    }

    synchronized public static void initializePerkuliahan(@NonNull final Context context) {
        if (sInitialized) return;
        sInitialized = true;
        FirebaseJobDispatcherSync(context);
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                // URI for every row of table perkuliahan
                Uri perkuliahanQueriUri = SikemasContract.PerkuliahanEntry.CONTENT_URI;
                String[] projectionColumns = {SikemasContract.PerkuliahanEntry._ID};
                Cursor cursor = context.getContentResolver().query(
                        perkuliahanQueriUri,
                        projectionColumns,
                        null,
                        null,
                        null);
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediatePerkuliahanSync(context);
                }
                /* Make sure to close the Cursor to avoid memory leaks! */
                cursor.close();
            }
        });
        checkForEmpty.start();
    }

//    synchronized public static void initializedKelas(@NonNull final Context context) {
//        if (sInitialized) return;
//        sInitialized = true;
//        // FirebaseJobDispatcherSync(context);
//        Thread checkForEmpty = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // URI for every row of table perkuliahan
//                Uri kelasQueriUri = SikemasContract.KelasEntry.CONTENT_URI;
//                String[] projectionColumns = {SikemasContract.KelasEntry._ID};
//                Cursor cursor = context.getContentResolver().query(
//                        kelasQueriUri,
//                        projectionColumns,
//                        null,
//                        null,
//                        null);
//                if (null == cursor || cursor.getCount() == 0) {
//                    startImmediateKelasSync(context);
//                }
//                /* Make sure to close the Cursor to avoid memory leaks! */
//                cursor.close();
//            }
//        });
//        checkForEmpty.start();
//    }

    public static void startImmediatePerkuliahanSync(Context context) {
        Intent intentToSyncImmediately = new Intent(context, SikemasSyncPerkuliahanIntentService.class);
        context.startService(intentToSyncImmediately);
    }

    public static void startImmediateKelasSync(final Context context) {
        Intent intentToSyncImmediately = new Intent(context, SikemasSyncKelasIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}