package id.ac.its.sikemastc.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class SikemasSyncKelasMahasiswaIntentService extends IntentService  {

    public SikemasSyncKelasMahasiswaIntentService() {
        super("SikemasSyncKelasMahasiswaIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SikemasSyncTask.syncKelasMahasiswa(this);
    }
}
