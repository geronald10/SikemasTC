package id.ac.its.sikemastc.activity.verifikasi_tandatangan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
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

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.utilities.InputStreamVolleyRequest;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

/**
 * Created by novitarpl on 5/16/2017.
 */

public class MenuVerifikasiTandaTangan extends AppCompatActivity{
    private final String TAG = MenuVerifikasiTandaTangan.class.getSimpleName();

    private Toolbar toolbar;
    private String userTerlogin;
    private String nrpMahasiswa;
    private String namaMahasiswa;
    private String idPerkuliahan;
    private String DIRECTORY;
    private String StoredPath;
    private ProgressDialog progressDialog;
    private ArrayList<String> imageUrlList;
    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_verifikasi_tandatangan);

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
     //   Log.v("log_tag", "id perkuliahan di menu verifikasi -> " + idPerkuliahan);
        userTerlogin= nrpMahasiswa + " - " + namaMahasiswa;

        DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/signatureverification/";
        StoredPath = DIRECTORY + userTerlogin;
        Log.d("storepath->", StoredPath);
        dir = new File(StoredPath);

        TextView tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvUserTerlogin.setText(userTerlogin);

        Button btnKelolaDataSetTandaTangan = (Button) findViewById(R.id.btn_kelola_data_set_tandatangan);
        Button btnVerifikasiTandaTangan = (Button) findViewById(R.id.btn_verifikasi_tandatangan);

        btnKelolaDataSetTandaTangan.setOnClickListener(operate);
        btnVerifikasiTandaTangan.setOnClickListener(operate);
//        callDetectionView.setOnClickListener(operate);
//        callTraining.setOnClickListener(operate);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_kelola_data_set_tandatangan:
                    if(dir.exists()){
                        Toast.makeText(MenuVerifikasiTandaTangan.this, "Dataset Tanda Tangan Sudah Ada", Toast.LENGTH_LONG).show();
                    }
                    else {
                        SinkronisasiDatasetTandaTangan(nrpMahasiswa);
                    }
                    break;

                case R.id.btn_verifikasi_tandatangan:
                    if(!dir.exists()) {
                        Toast.makeText(MenuVerifikasiTandaTangan.this, "Dataset Kosong, Silahkan Tambah Dataset", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent intentToSignatureRecognition = new Intent(v.getContext(), VerifikasiTandaTangan.class);
                        intentToSignatureRecognition.putExtra("identitas_mahasiswa", nrpMahasiswa + " - " + namaMahasiswa);
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
    private void SinkronisasiDatasetTandaTangan(final String userId) {
        //Showing the progress dialog
        imageUrlList = new ArrayList<>();
        progressDialog.setMessage("Pengecekan Dataset Server... ");
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
                                Toast.makeText(MenuVerifikasiTandaTangan.this, "Dataset ditemukan",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(MenuVerifikasiTandaTangan.this, "Dataset tidak ditemukan, " +
                                        "Anda belum mendaftarkan Tanda Tangan Anda", Toast.LENGTH_SHORT).show();
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
        progressDialog.setMessage("Unduh Dataset... ");
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
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Berhasil melakukan unduh dataset",
                                                Toast.LENGTH_SHORT).show();
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


}