package id.ac.its.sikemastc.activity.verifikasi_tandatangan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.utilities.InputStreamVolleyRequest;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class MenuVerifikasiTandaTangan extends AppCompatActivity{

    private final String TAG = MenuVerifikasiTandaTangan.class.getSimpleName();

    private SharedPreferences flagStatus;
    private Context mContext;
    private Toolbar toolbar;
    private String userTerlogin;
    private String nrpMahasiswa;
    private String namaMahasiswa;
    private String idPerkuliahan;
    private String DIRECTORY;
    private String StoredPath;
    private ProgressDialog progressDialog;
    private ArrayList<String> imageUrlList;
    private SikemasSessionManager session;
    private Button btnKelolaDataSetTandaTangan;
    private Button btnVerifikasiTandaTangan;
    private ImageView ivStatusData;
    private TextView tvStatusData;
    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_menu_verifikasi_tandatangan);

        mContext = this;
        session = new SikemasSessionManager(this);
        flagStatus = getSharedPreferences("status_file_ttd_flag", 0);

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
        HashMap<String, String> userDetail = session.getUserDetails();
        nrpMahasiswa = userDetail.get(SikemasSessionManager.KEY_USER_ID);
        namaMahasiswa = userDetail.get(SikemasSessionManager.KEY_USER_NAME);
        idPerkuliahan = intent.getStringExtra("id_perkuliahan");
        userTerlogin= nrpMahasiswa + " - " + namaMahasiswa;

        DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/signatureverification/";
        StoredPath = DIRECTORY + userTerlogin;
        Log.d("storepath->", StoredPath);
        dir = new File(StoredPath);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        TextView tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvUserTerlogin.setText(userTerlogin);

        ivStatusData = (ImageView) findViewById(R.id.iv_status_data_ttd);
        tvStatusData = (TextView) findViewById(R.id.tv_status_data_ttd);
        btnKelolaDataSetTandaTangan = (Button) findViewById(R.id.btn_kelola_data_set_tandatangan);
        btnVerifikasiTandaTangan = (Button) findViewById(R.id.btn_verifikasi_tandatangan);

        btnKelolaDataSetTandaTangan.setOnClickListener(operate);
        btnVerifikasiTandaTangan.setOnClickListener(operate);

        sinkronisasiDatasetTandaTangan(nrpMahasiswa);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_kelola_data_set_tandatangan:
                    if(dir.exists()){
                        Toast.makeText(MenuVerifikasiTandaTangan.this, "Data Tanda Tangan Sudah Ada", Toast.LENGTH_LONG).show();
                    }
                    else {
                        sinkronisasiDatasetTandaTangan(nrpMahasiswa);
                    }
                    break;

                case R.id.btn_verifikasi_tandatangan:
                    if(!dir.exists()) {
                        Toast.makeText(MenuVerifikasiTandaTangan.this, "Data tandatangan tidak ditemukan, silahkan sinkronisasi terlebih dahulu", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent intentToSignatureRecognition = new Intent(mContext, VerifikasiTandaTangan.class);
                        intentToSignatureRecognition.putExtra("identitas_mahasiswa", userTerlogin);
                        intentToSignatureRecognition.putExtra("id_mahasiswa", nrpMahasiswa);
                        intentToSignatureRecognition.putExtra("nama_mahasiswa", namaMahasiswa);
                        intentToSignatureRecognition.putExtra("id_perkuliahan", idPerkuliahan);
                        startActivity(intentToSignatureRecognition);
                    }
                    break;
            }
        }
    };

    //unduh dataset
    private void sinkronisasiDatasetTandaTangan(final String userId) {
        //Showing the progress dialog
        imageUrlList = new ArrayList<>();
        progressDialog.setMessage("Pengecekan Data Server... ");
        progressDialog.show();
        Log.d(TAG, "masuk fungsi sinkronisasi");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkUtils.SINKRONISASI_DATASET_TANDATANGAN_SIKEMAS,
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
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(MenuVerifikasiTandaTangan.this, "Dataset tidak ditemukan, " +
                                        "Anda belum mendaftarkan Tandatangan Anda", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MenuVerifikasiTandaTangan.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
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

    // Download image from url
    private void downloadImageFromUrl() {
        //Showing the progress dialog
        progressDialog.setMessage("Unduh data tandatangan... ");
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
                                    //  String wholeFolderPath = fh.TRAINING_PATH + userTerlogin;
                                    String wholeFolderPath= StoredPath;
                                    new File(wholeFolderPath).mkdirs();
                                    int nomor=index+1;
                                    String name = userTerlogin + "-" + nomor + ".png";
                                    String fullpath = wholeFolderPath + "/" + name;
                                    File outputFile = new File(fullpath);

                                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                                    outputStream.write(response);
                                    outputStream.close();
                                    if ((index + 1) == imageUrlList.size()) {
                                        flagStatus.edit().putBoolean("status_file", true).apply();
                                        checkDataSetStatus();
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Berhasil melakukan unduh dataset",
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
                            Toast.makeText(MenuVerifikasiTandaTangan.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }, null);
            VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(inputStreamRequest);
        }
    }

    private void checkDataSetStatus() {
        boolean flag = flagStatus.getBoolean("status_file", false);
        if (!flag) {
            btnKelolaDataSetTandaTangan.setVisibility(View.VISIBLE);
            ivStatusData.setImageResource(R.drawable.ic_error_outline);
            tvStatusData.setText("data tandatangan tidak ditemukan");
            btnVerifikasiTandaTangan.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_error_outline), null, null, null);
            btnVerifikasiTandaTangan.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondaryText));
        } else {
            btnKelolaDataSetTandaTangan.setVisibility(View.GONE);
            ivStatusData.setImageResource(R.drawable.ic_verified_user);
            tvStatusData.setText("data tandatangan ditemukan");
            btnVerifikasiTandaTangan.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_verified_user), null, null, null);
            btnVerifikasiTandaTangan.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryText));
        }
    }
}