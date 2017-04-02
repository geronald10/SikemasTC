package id.ac.its.sikemastc.activity.dosen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.R;

public class PenjadwalanUlang extends BaseActivity {

    private Button btnGantiJadwalSementera;
    private Button btnGantiJadwalPermanen;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjadwalan_ulang);
        mContext = this;

        btnGantiJadwalSementera = (Button) findViewById(R.id.btn_ganti_jadwal_sementara);
        btnGantiJadwalPermanen = (Button) findViewById(R.id.btn_ganti_jadwal_permanen);

        btnGantiJadwalSementera.setOnClickListener(operate);
        btnGantiJadwalPermanen.setOnClickListener(operate);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_ganti_jadwal_sementara:
                    Intent intentToSementara = new Intent(mContext, PenjadwalanUlangSementara.class);
                    startActivity(intentToSementara);
                    break;
                case R.id.btn_ganti_jadwal_permanen:
                    Intent intentToPermanen = new Intent(mContext, PenjadwalanUlangPermanen.class);
                    startActivity(intentToPermanen);
                    break;
            }
        }
    };
}
