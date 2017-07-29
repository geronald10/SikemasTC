package id.ac.its.sikemastc.activity.verifikasi_qr_code;

import com.google.android.gms.vision.barcode.Barcode;
import android.content.Context;
import com.google.android.gms.vision.Tracker;

/**
 * Created by nurro on 7/28/2017.
 */

class BarcodeTracker extends Tracker<Barcode> {
    private BarcodeGraphicTrackerCallback mListener;

    public interface BarcodeGraphicTrackerCallback {
        void onDetectedQrCode(Barcode barcode);
    }

    BarcodeTracker(Context listener) {
        mListener = (BarcodeGraphicTrackerCallback) listener;
    }

    @Override
    public void onNewItem(int id, Barcode item) {
        if (item.displayValue != null) {
            mListener.onDetectedQrCode(item);
        }
    }
}
