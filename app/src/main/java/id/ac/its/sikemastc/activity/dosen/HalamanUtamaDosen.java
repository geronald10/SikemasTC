package id.ac.its.sikemastc.activity.dosen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.R;

public class HalamanUtamaDosen extends BaseActivity {

    private final String TAG = ListKelasDiampu.class.getSimpleName();

    private Context mContext;
    private Button btnAktif;
    private Button btnLihatKehadiran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama_dosen);
        mContext = this;

        btnAktif = (Button) findViewById(R.id.btn_aktifkan_kelas);
        btnLihatKehadiran = (Button) findViewById(R.id.btn_lihat_kehadiran);

        btnAktif.setOnClickListener(operate);
        btnLihatKehadiran.setOnClickListener(operate);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_aktifkan_kelas:
                    Toast.makeText(mContext, "Kelas Diaktifkan", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_lihat_kehadiran:
                    Intent intentToLihatKehadiran = new Intent(mContext, LihatKehadiran.class);
                    startActivity(intentToLihatKehadiran);
                    break;
            }
        }
    };
}
