package id.ac.its.sikemastc.sync;

import android.content.Intent;
import android.app.IntentService;

public class SikemasSyncPerkuliahanMahasiswaIntentService extends IntentService {

    public SikemasSyncPerkuliahanMahasiswaIntentService() {
        super("SikemasSyncPerkuliahanMahasiswaIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SikemasSyncTask.syncPerkuliahanMahasiswa(this);
    }
}
