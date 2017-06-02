package id.ac.its.sikemastc.activity.verifikasi_lokasi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by nurro on 6/2/2017.
 */

public class BroadcastResult extends BroadcastReceiver {

    private TextView location;
    public BroadcastResult(TextView location) {
        this.location = location;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Broadcast", intent.getStringExtra("FinalResult"));
        this.location.setText(intent.getStringExtra("FinalResult"));
    }
}
