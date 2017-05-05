package id.ac.its.sikemastc.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;


public class SikemasSyncKelasIntentService extends IntentService {

    public SikemasSyncKelasIntentService() {
        super("SikemasSyncKelasIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SikemasSyncTask.syncKelasDosen(this);
    }
}
