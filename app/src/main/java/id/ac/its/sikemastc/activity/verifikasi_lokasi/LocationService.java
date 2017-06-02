package id.ac.its.sikemastc.activity.verifikasi_lokasi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by nurro on 6/2/2017.
 */

public class LocationService extends Service {
    private GoogleAPITracker googleAPI;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.googleAPI.stopUsingAPI();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Memulai Service Lokasi", intent.getStringExtra("NRP"));
        googleAPI = new GoogleAPITracker(this, intent.getStringExtra("NRP"));
        if (!googleAPI.isGoogleApiClientConnected()) {
            googleAPI.getLocation();
        }
        if(googleAPI == null)
            stopSelf();
        return Service.START_NOT_STICKY;
    }
}
