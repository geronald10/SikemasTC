package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import id.ac.its.sikemastc.R;

public class MenuVerifikasiWajah extends AppCompatActivity {

    private final String TAG = MenuVerifikasiWajah.class.getSimpleName();

    private Toolbar toolbar;
    private String nrpMahasiswa;
    private String namaMahasiswa;
    private String idPerkuliahan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_verifikasi_wajah);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Verifikasi Wajah");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // Compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        idPerkuliahan = intent.getStringExtra("id_perkuliahan");

        String training = intent.getStringExtra("training");
        if (training != null && !training.isEmpty()) {
            Toast.makeText(getApplicationContext(), training, Toast.LENGTH_SHORT).show();
            intent.removeExtra("training");
        }

        TextView tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvUserTerlogin.setText(nrpMahasiswa + " - " + namaMahasiswa);

        Button btnTrainingDataSetWajah = (Button) findViewById(R.id.btn_train_data_set_wajah);
        Button btnKelolaDataSetWajah = (Button) findViewById(R.id.btn_kelola_data_set_wajah);
        Button btnVerifikasiWajah = (Button) findViewById(R.id.btn_verification_view);

        btnKelolaDataSetWajah.setOnClickListener(operate);
        btnVerifikasiWajah.setOnClickListener(operate);
        btnTrainingDataSetWajah.setOnClickListener(operate);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_kelola_data_set_wajah:
                    Intent intentToSetWajah = new Intent(v.getContext(), KelolaDataSetWajah.class);
                    intentToSetWajah.putExtra("identitas_mahasiswa", nrpMahasiswa + " - " + namaMahasiswa);
                    intentToSetWajah.putExtra("id_mahasiswa", nrpMahasiswa);
                    startActivity(intentToSetWajah);
                    break;
                case R.id.btn_train_data_set_wajah:
                    Intent intentToTraining = new Intent(v.getContext(), TrainingWajah.class);
                    startActivity(intentToTraining);
                    break;
                case R.id.btn_verification_view:
                    Intent intentToRecognition = new Intent(v.getContext(), VerifikasiWajah.class);
                    intentToRecognition.putExtra("identitas_mahasiswa", nrpMahasiswa + " - " + namaMahasiswa);
                    intentToRecognition.putExtra("id_perkuliahan", idPerkuliahan);
                    startActivity(intentToRecognition);
                    break;
            }
        }
    };
}
