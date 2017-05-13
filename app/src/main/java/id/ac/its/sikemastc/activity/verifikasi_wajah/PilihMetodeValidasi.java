package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import id.ac.its.sikemastc.R;

public class PilihMetodeValidasi extends AppCompatActivity {
    private final String TAG = PilihMetodeValidasi.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_metode_validasi);

        Button btnValidasiTtd = (Button) findViewById(R.id.btn_validasi_ttd);
        Button btnValidasiWajah = (Button) findViewById(R.id.btn_validasi_wajah);

        btnValidasiTtd.setOnClickListener(validate);
        btnValidasiWajah.setOnClickListener(validate);
    }

    View.OnClickListener validate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_validasi_ttd:
                    break;
                case R.id.btn_validasi_wajah:
                    Intent intentToValidateFace = new Intent(PilihMetodeValidasi.this, MenuValidasiWajah.class);
                    startActivity(intentToValidateFace);
                    break;
                default:
                    break;
            }
        }
    };
}
