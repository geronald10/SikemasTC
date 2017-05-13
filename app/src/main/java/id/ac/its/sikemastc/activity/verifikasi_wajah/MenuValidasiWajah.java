package id.ac.its.sikemastc.activity;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import id.ac.its.sikemastc.R;

public class MenuValidasiWajah extends AppCompatActivity {

    private final String TAG = MenuValidasiWajah.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_validasi_wajah);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Intent intent = getIntent();
        String training = intent.getStringExtra("training");
        if (training != null && !training.isEmpty()) {
            Toast.makeText(getApplicationContext(), training, Toast.LENGTH_SHORT).show();
            intent.removeExtra("training");
        }

        Button callAddSetWajah = (Button) findViewById(R.id.btn_add_set_wajah);
        Button callDetectionView = (Button) findViewById(R.id.btn_detection_view);
        Button callTraining = (Button) findViewById(R.id.btn_recognition_training);
        Button callRecognition = (Button) findViewById(R.id.btn_recognition_view);

        callAddSetWajah.setOnClickListener(operate);
        callDetectionView.setOnClickListener(operate);
        callTraining.setOnClickListener(operate);
        callRecognition.setOnClickListener(operate);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_set_wajah:
                    Intent intentToSetWajah = new Intent(v.getContext(), AddSetWajah.class);
                    intentToSetWajah.putExtra("identitas_mahasiswa", "112 - Ronald Gunawan R");
                    intentToSetWajah.putExtra("id_mahasiswa", "5113100112");
                    startActivity(intentToSetWajah);
                    break;
                case R.id.btn_detection_view:
                    Intent intentToDetectionView = new Intent(v.getContext(), DetectionView.class);
                    startActivity(intentToDetectionView);
                    break;
                case R.id.btn_recognition_training:
                    Intent intentToTraining = new Intent(v.getContext(), FaceTraining.class);
                    startActivity(intentToTraining);
                    break;
                case R.id.btn_recognition_view:
                    Intent intentToRecognition = new Intent(v.getContext(), FaceRecognizing.class);
                    startActivity(intentToRecognition);
                    break;
            }
        }
    };
}
