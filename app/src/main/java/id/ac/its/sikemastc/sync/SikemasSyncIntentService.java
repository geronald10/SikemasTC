package id.ac.its.sikemastc.sync;

import android.app.IntentService;
import android.content.Intent;

public class SikemasSyncIntentService extends IntentService {

    public SikemasSyncIntentService() {
        super("SikemasSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SikemasSyncTask.syncJadwalPerkuliahan(this);
    }
}
