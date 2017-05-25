package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.mahasiswa.HalamanUtamaMahasiswa;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class HasilVerifikasi extends AppCompatActivity {

    private final String TAG = HasilVerifikasi.class.getSimpleName();

    private String idPerkuliahan;

    private ProgressBar mLoadingIndicator;
    private ConstraintLayout clLayoutHasilVerifikasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_hasil_verifikasi);

        Intent intent = getIntent();
        String identitasMahasiswa = intent.getStringExtra("identitas_mahasiswa");
        String ketKehadiran = intent.getStringExtra("ket_kehadiran");
        idPerkuliahan = intent.getStringExtra("id_perkuliahan");
        String[] identitas = identitasMahasiswa.trim().split(Pattern.quote("-"));
        String idMahasiswa = identitas[0];

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        clLayoutHasilVerifikasi = (ConstraintLayout) findViewById(R.id.cl_hasil_verifikasi);

        TextView tvUserDetil = (TextView) findViewById(R.id.tv_user_detil);
        tvUserDetil.setText(identitasMahasiswa);

        Button btnMenuUtama = (Button) findViewById(R.id.btn_menu_utama);
        Button btnCobaLagi = (Button) findViewById(R.id.btn_coba_lagi);
        btnMenuUtama.setOnClickListener(operate);
        btnCobaLagi.setOnClickListener(operate);

        sendKehadiranStatus(idPerkuliahan, idMahasiswa, ketKehadiran);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_coba_lagi:
//                    Intent intentToMenuVerifikasi = new Intent(HasilVerifikasi.this, MenuVerifikasiWajah.class);
//                    intentToMenuVerifikasi.putExtra("id_perkuliahan", idPerkuliahan);
//                    startActivity(intentToMenuVerifikasi);
                    finish();
                    break;
                case R.id.btn_menu_utama:
                    Intent intentToHome = new Intent(HasilVerifikasi.this, HalamanUtamaMahasiswa.class);
                    startActivity(intentToHome);
                    finish();
                    break;
            }
        }
    };

    public void sendKehadiranStatus(final String idPerkuliahan, final String nrpMahasiswa,
                                    final String ketKehadiran) {
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.CHANGE_STATUS_KEHADIRAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Peserta Kehadiran Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("1"))
                                showHasilVerifikasiDataView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_perkuliahan", idPerkuliahan);
                params.put("id_mahasiswa", nrpMahasiswa);
                params.put("ket_kehadiran", ketKehadiran);
                Log.d("id_perkuliahan", idPerkuliahan);
                Log.d("id_mahasiswa", nrpMahasiswa);
                Log.d("ket_kehadiran", ketKehadiran);
                return params;
            }
        };
        VolleySingleton.getmInstance(this).addToRequestQueue(stringRequest);
    }

    private void showHasilVerifikasiDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        clLayoutHasilVerifikasi.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        clLayoutHasilVerifikasi.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }
}
