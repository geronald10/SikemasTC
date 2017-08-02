package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.utilities.InputStreamVolleyRequest;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class VerifikasiWajahMenuActivity extends AppCompatActivity {

    private final String TAG = VerifikasiWajahMenuActivity.class.getSimpleName();

    private SharedPreferences flagStatus;
    private Button btnVerifikasiWajah;
    private Context mContext;
    private Toolbar toolbar;
    private String nrpMahasiswa;
    private String namaMahasiswa;
    private String idPerkuliahan;
    private String userTerlogin;
    private SikemasSessionManager session;
    private ArrayList<String> imageUrlList;
    private ProgressDialog progressDialog;
    private FileHelper fh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_menu_verifikasi_wajah);

        mContext = this;
        session = new SikemasSessionManager(this);
        flagStatus = getSharedPreferences("flag_status", 0);

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
        HashMap<String, String> userDetail = session.getUserDetails();
        nrpMahasiswa = userDetail.get(SikemasSessionManager.KEY_USER_ID);
        namaMahasiswa = userDetail.get(SikemasSessionManager.KEY_USER_NAME);
        idPerkuliahan = intent.getStringExtra("id_perkuliahan");
        userTerlogin= nrpMahasiswa + " - " + namaMahasiswa;
        String training = intent.getStringExtra("training");
        if (training != null && !training.isEmpty()) {
            Toast.makeText(getApplicationContext(), training, Toast.LENGTH_SHORT).show();
            intent.removeExtra("training");
        }

        TextView tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvUserTerlogin.setText(nrpMahasiswa + " - " + namaMahasiswa);

        Button btnKelolaDataSetWajah = (Button) findViewById(R.id.btn_kelola_data_set_wajah);
        btnVerifikasiWajah = (Button) findViewById(R.id.btn_verification_view);

        btnKelolaDataSetWajah.setOnClickListener(operate);
        btnVerifikasiWajah.setOnClickListener(operate);

        checkDataSetStatus();
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_kelola_data_set_wajah:
                    if (getJumlahDataSetClient() < 20) {
                        sinkronisasiDataSet(nrpMahasiswa);
                    } else {
                        Toast.makeText(getApplicationContext(), "Data Wajah Ditemukan Sama Dengan Data Wajah di Server",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.btn_verification_view:
                    if (!flagStatus.getBoolean("training_flag", false)) {
                        Toast.makeText(getApplication(), "Fitur Verifikasi Kehadiran tidak dapat " +
                                        "digunakan\nFitur ini aktif ketika data wajah telah ditraining sebelumnya",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Intent intentToRecognition = new Intent(v.getContext(), VerifikasiWajahActivity.class);
                        intentToRecognition.putExtra("identitas_mahasiswa", nrpMahasiswa + " - " + namaMahasiswa);
                        intentToRecognition.putExtra("id_perkuliahan", idPerkuliahan);
                        startActivity(intentToRecognition);
                        finish();
                    }
                    break;
            }
        }
    };

    // Get jumlah data set wajah di local file system
    private int getJumlahDataSetClient() {
        FileHelper imageFiles = new FileHelper(userTerlogin);
        File[] imageList = imageFiles.getTrainingList();
        return imageList.length;
    }

    // Download imageUrl from server
    private void sinkronisasiDataSet(final String userId) {
        //Showing the progress dialog
        imageUrlList = new ArrayList<>();
        progressDialog.setMessage("Pengecekan Data Wajah di Server... ");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkUtils.LIST_FOTO_MAHASISWA_IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "get image url " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray list = jsonObject.getJSONArray("list");
                            if (list.length() > 0) {
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject urlImage = list.getJSONObject(i);
                                    String fotoUrl = urlImage.getString("foto").replace(" ", "%20");
                                    imageUrlList.add(fotoUrl);
                                    Log.d("imageUrl", imageUrlList.get(i));
                                }
                                progressDialog.dismiss();
                                downloadImageFromUrl();
                                Toast.makeText(VerifikasiWajahMenuActivity.this, "Data Set ditemukan",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(VerifikasiWajahMenuActivity.this, "Data Set tidak ditemukan, " +
                                        "Anda belum mendaftarkan Foto Wajah Anda", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Dismissing the progress dialog
                        progressDialog.dismiss();
                        Log.d("VolleyErroyResponse", "Error");
                        //Showing toast
                        Toast.makeText(VerifikasiWajahMenuActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Get encoded Image
                Map<String, String> params = new HashMap<>();
                params.put("nrp", userId);
                //returning parameters
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    // Download image from imageUrl
    private void downloadImageFromUrl() {
        //Showing the progress dialog
        progressDialog.setMessage("Mengunduh Data Wajah... ");
        progressDialog.show();

        for (int i = 0; i < imageUrlList.size(); i++) {
            final int index = i;
            Log.d("image URL" + index, imageUrlList.get(i));
            InputStreamVolleyRequest inputStreamRequest = new InputStreamVolleyRequest(Request.Method.GET, imageUrlList.get(i),
                    new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            try {
                                Log.d("Sinkronisasi Volley", "response");
                                if (response != null) {
                                    String wholeFolderPath = fh.TRAINING_PATH + userTerlogin;
                                    new File(wholeFolderPath).mkdirs();
                                    String name = userTerlogin + "_" + index + ".png";
                                    String fullpath = wholeFolderPath + "/" + name;
                                    File outputFile = new File(fullpath);

                                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                                    outputStream.write(response);
                                    outputStream.close();
                                    if (index + 1 == imageUrlList.size()) {
                                        flagStatus.edit().putBoolean("training_flag", false).apply();
                                        flagStatus.edit().putBoolean("upload_flag", true).apply();
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Berhasil mengunduh data wajah",
                                                Toast.LENGTH_SHORT).show();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            // Dismissing the progress dialog
                            progressDialog.dismiss();
                            Log.d("VolleyErroyResponse", "Error");
                            //Showing toast
                            Toast.makeText(VerifikasiWajahMenuActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }, null);
            VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(inputStreamRequest);
        }
    }

    private void checkDataSetStatus() {
        boolean flag = flagStatus.getBoolean("training_flag", false);
        if (!flag) {
            btnVerifikasiWajah.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_error_outline), null, null, null);
            btnVerifikasiWajah.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondaryText));
        } else {
            btnVerifikasiWajah.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_verified_user), null, null, null);
            btnVerifikasiWajah.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryText));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDataSetStatus();
    }
}