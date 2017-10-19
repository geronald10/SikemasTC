package id.ac.its.sikemastc.activity.verifikasi_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.ac.its.sikemastc.activity.mahasiswa.MainPerkuliahanFragment;
import id.ac.its.sikemastc.model.Perkuliahan;

/**
 * Created by ifirf on 10/5/2017.
 */

public class BluetoothVerification {

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> deviceAddressList, deviceNameList, activeClass;
    private SweetAlertDialog mLoadDialog;
    private Context mContext;
    private TextView mLocationTextView;
    public String mLocationResult;
    private List<Perkuliahan> mListAktifPerkuliahan;
    private Map<String, String> mDeviceData;

    public BluetoothVerification(Context context, TextView location, String result){
        mContext = context;
        mLocationTextView = location;
        mLocationResult = result;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mDeviceData = new HashMap<String, String>();
        deviceAddressList = new ArrayList<String>();
        deviceNameList = new ArrayList<String>();
    }

    public void startBluetoothDiscovery(List<Perkuliahan> listAktifPerkuliahanMhs){
        mListAktifPerkuliahan = listAktifPerkuliahanMhs;

        activeClass = new ArrayList<String>();
        for(int i=0; i < mListAktifPerkuliahan.size(); i++)
            activeClass.add(mListAktifPerkuliahan.get(i).getBluetoothAddr());

        if(mBluetoothAdapter == null)
            Toast.makeText(mContext, "Perangkat Anda tidak punya Bluetooth.", Toast.LENGTH_LONG).show();
        else
            findLocation();
    }

    private void showLoadingDialog() {
        mLoadDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        mLoadDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mLoadDialog.setTitleText("Menganalisis posisimu...");
        mLoadDialog.setCancelable(false);
        mLoadDialog.show();
    }

    private void showLoadingSuccessDialog(String kelas){
        if(mLoadDialog.isShowing()){
            mLoadDialog.dismiss();
            new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Ketemu!")
                    .setContentText("Yep! Anda ada di kelas " + kelas)
                    .show();
        }
    }

    private void showLoadingErrorNotFoundDialog() {
        if (mLoadDialog.isShowing()) {
            mLoadDialog.dismiss();
            new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Tidak Ketemu!")
                    .setContentText("Anda mungkin tidak berada di jangkauan area kelas")
                    .show();
        }
    }

    private void findLocation(){
        if(!mBluetoothAdapter.isEnabled())
            Toast.makeText(mContext, "Nyalakan Bluetooth dulu!", Toast.LENGTH_LONG).show();
        else {
            showLoadingDialog();
            deviceAddressList.clear();
            mDeviceData.clear();
            mBluetoothAdapter.startDiscovery();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mContext.registerReceiver(bluetoothReceiver, intentFilter);
        }
    }


    final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceAddressList.add(device.getAddress());
                deviceNameList.add(device.getName());
                mDeviceData.put(device.getAddress(), device.getName());
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                boolean flag = false;

                deviceAddressList.retainAll(activeClass);
                activeClass.retainAll(deviceAddressList);

                for(int i=0; i < activeClass.size(); i++){
                    if(activeClass.get(i).equals(deviceAddressList.get(i))){
                        mLocationResult = activeClass.get(i);
                        mLocationTextView.setText(mDeviceData.get(activeClass.get(i)));
                        showLoadingSuccessDialog(mDeviceData.get(activeClass.get(i)));

                        flag = true;
                        break;
                    }
                }
                if(!flag) showLoadingErrorNotFoundDialog();
            }
        }
    };
}
