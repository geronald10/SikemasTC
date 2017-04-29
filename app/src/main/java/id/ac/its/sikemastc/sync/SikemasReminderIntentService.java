package id.ac.its.sikemastc.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class SikemasReminderIntentService extends IntentService {

    public SikemasReminderIntentService() {
        super("SikemasReminderIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        ReminderTasks.executeTask(this, action);
    }
}
