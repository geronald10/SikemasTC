package id.ac.its.sikemastc.activity.verifikasi_tandatangan;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.verifikasi_wajah.KelolaDataSetWajah;

/**
 * Created by novitarpl on 5/16/2017.
 */

public class MenuVerifikasiTandaTangan extends AppCompatActivity{
    private final String TAG = MenuVerifikasiTandaTangan.class.getSimpleName();

    private Toolbar toolbar;
    private String nrpMahasiswa;
    private String namaMahasiswa;
    private String idPerkuliahan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_verifikasi_tandatangan);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Verifikasi Tanda Tangan");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        // Compatibility
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10f);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        nrpMahasiswa = intent.getStringExtra("nrp_mahasiswa");
        namaMahasiswa = intent.getStringExtra("nama_mahasiswa");
        idPerkuliahan =intent.getStringExtra("id_perkuliahan");

        Log.v("log_tag", "id perkuliahan di menu verifikasi -> " + idPerkuliahan);

        TextView tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvUserTerlogin.setText(nrpMahasiswa + " - " + namaMahasiswa);
//        Button callDetectionView = (Button) findViewById(R.id.btn_detection_view);
//        Button callTraining = (Button) findViewById(R.id.btn_recognition_training);

        Button btnKelolaDataSetTandaTangan = (Button) findViewById(R.id.btn_kelola_data_set_tandatangan_view);
        Button btnVerifikasiTandaTangan = (Button) findViewById(R.id.btn_verification_ttd_view);

        btnKelolaDataSetTandaTangan.setOnClickListener(operate);
        btnVerifikasiTandaTangan.setOnClickListener(operate);
//        callDetectionView.setOnClickListener(operate);
//        callTraining.setOnClickListener(operate);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_kelola_data_set_tandatangan_view:
                    Intent intentToSetTandaTangan = new Intent(v.getContext(), KelolaDataSetTandaTangan.class);
                    intentToSetTandaTangan.putExtra("identitas_mahasiswa", nrpMahasiswa + " - " + namaMahasiswa);
                    intentToSetTandaTangan.putExtra("id_mahasiswa", nrpMahasiswa);
                    startActivity(intentToSetTandaTangan);
                    break;

                case R.id.btn_verification_ttd_view:
                    Intent intentToSignatureRecognition = new Intent(v.getContext(), VerifikasiTandaTangan.class);
                    intentToSignatureRecognition.putExtra("identitas_mahasiswa", nrpMahasiswa + " - " + namaMahasiswa);
                    intentToSignatureRecognition.putExtra("id_mahasiswa", nrpMahasiswa);
                    intentToSignatureRecognition.putExtra("nama_mahasiswa", namaMahasiswa);
                    intentToSignatureRecognition.putExtra("id_perkuliahan", idPerkuliahan);
                    startActivity(intentToSignatureRecognition);
                    break;
            }
        }
    };
}
