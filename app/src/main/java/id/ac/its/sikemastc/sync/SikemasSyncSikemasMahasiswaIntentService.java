package id.ac.its.sikemastc.sync;

import android.content.Intent;
import android.app.IntentService;

import id.ac.its.sikemastc.utilities.SikemasDateUtils;

public class SikemasSyncSikemasMahasiswaIntentService extends IntentService {

    public SikemasSyncSikemasMahasiswaIntentService() {
        super("SikemasSyncSikemasMahasiswaIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SikemasSyncTask.syncKelasMahasiswa(this);
        SikemasSyncTask.syncPerkuliahanMahasiswa(this);
    }
}
