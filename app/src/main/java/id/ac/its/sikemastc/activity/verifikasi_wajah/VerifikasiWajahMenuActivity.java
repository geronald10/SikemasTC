package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.utilities.InputStreamVolleyRequest;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

import static ch.zhaw.facerecognitionlibrary.Helpers.FileHelper.DATA_PATH;
import static ch.zhaw.facerecognitionlibrary.Helpers.FileHelper.SVM_PATH;

public class VerifikasiWajahMenuActivity extends AppCompatActivity {

    private final String TAG = VerifikasiWajahMenuActivity.class.getSimpleName();

    private SharedPreferences flagStatus;
    private Button btnVerifikasiWajah;
    private Button btnKelolaDataSetWajah;
    private ImageView ivStatusData;
    private TextView tvStatusData;
    private TextView tvCekUpdate;
    private Context mContext;
    private Toolbar toolbar;
    private String nrpMahasiswa;
    private String namaMahasiswa;
    private String idPerkuliahan;
    private String userTerlogin;
    private SikemasSessionManager session;
    private Set<String> fileNameSet;
    private Set<String> fileTimestampSet;
    private ArrayList<String> fileUrlList;
    private ProgressDialog progressDialog;
    private FileHelper fh;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_menu_verifikasi_wajah);

        mContext = this;
        session = new SikemasSessionManager(this);
        flagStatus = getApplicationContext().getSharedPreferences("status_file_flag", 0);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Verifikasi Wajah");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

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
        userTerlogin = nrpMahasiswa + " - " + namaMahasiswa;

        String training = intent.getStringExtra("training");
        if (training != null && !training.isEmpty()) {
            Toast.makeText(getApplicationContext(), training, Toast.LENGTH_SHORT).show();
            intent.removeExtra("training");
        }

        TextView tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvUserTerlogin.setText(userTerlogin);

        btnKelolaDataSetWajah = (Button) findViewById(R.id.btn_kelola_data_set_wajah);
        btnVerifikasiWajah = (Button) findViewById(R.id.btn_verification_view);
        ivStatusData = (ImageView) findViewById(R.id.iv_status_data_wajah);
        tvStatusData = (TextView) findViewById(R.id.tv_status_data_wajah);
        tvCekUpdate = (TextView) findViewById(R.id.tv_update_terakhir);

        btnKelolaDataSetWajah.setOnClickListener(operate);
        btnVerifikasiWajah.setOnClickListener(operate);

        File dataDirectory = new File(DATA_PATH);
        if(!dataDirectory.exists()) {
            downloadFileFromUrl();
        } else {
            checkDataSetStatus();
        }
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_kelola_data_set_wajah:
                    checkTrainingFileFromServer();
                    break;
                case R.id.btn_verification_view:
                    if (!flagStatus.getBoolean("status_file", false)) {
                        Toast.makeText(getApplication(), "Fitur Verifikasi Kehadiran tidak dapat " +
                                        "digunakan\nFitur ini aktif ketika data telah berhasil diunduh",
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

    private void checkDataSetStatus() {
        boolean flag = flagStatus.getBoolean("status_file", false);
        if (!flag) {
            tvCekUpdate.setText(flagStatus.getString("cek_update_time", "-"));
            ivStatusData.setImageResource(R.drawable.ic_error_outline);
            tvStatusData.setText("data training tidak ditemukan");
            tvStatusData.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondaryText));
            btnVerifikasiWajah.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_error_outline), null, null, null);
            btnVerifikasiWajah.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondaryText));
        } else {
            tvCekUpdate.setText(flagStatus.getString("cek_update_time", "-"));
            ivStatusData.setImageResource(R.drawable.ic_verified_user);
            tvStatusData.setText("data training ditemukan");
            tvStatusData.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryText));
            btnVerifikasiWajah.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.ic_verified_user), null, null, null);
            btnVerifikasiWajah.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryText));
        }
    }

    // Download file from fileUrl
    private void downloadFileFromUrl() {
        //Showing the progress dialog
        progressDialog.setMessage("Mengunduh Data ... ");
        progressDialog.show();

        fileUrlList = new ArrayList<>();
        fileUrlList.add("http://absensi.if.its.ac.id/uploads/data/labelMap_train");
        fileUrlList.add("http://absensi.if.its.ac.id/uploads/data/svm_train");
        fileUrlList.add("http://absensi.if.its.ac.id/uploads/data/svm_train_model");

        for (int i = 0; i < fileUrlList.size(); i++) {
            final int index = i;
            Log.d("image URL" + index, fileUrlList.get(i));
            InputStreamVolleyRequest inputStreamRequest = new InputStreamVolleyRequest(Request.Method.GET, fileUrlList.get(i),
                    new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            try {
                                Log.d("Sinkronisasi Volley", "response");
                                if (response != null) {
                                    String wholeFolderPath = fh.SVM_PATH;
                                    new File(wholeFolderPath).mkdirs();

                                    String url = fileUrlList.get(index);
                                    String[] urlArray = url.split("/");
                                    String name = urlArray[urlArray.length - 1];
                                    Log.d("name file", name);
                                    String fullpath = wholeFolderPath + "/" + name;
                                    File outputFile = new File(fullpath);

                                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                                    outputStream.write(response);
                                    outputStream.close();

                                    if (index + 1 == fileUrlList.size()) {
                                        flagStatus.edit().putBoolean("status_file", true).apply();
                                        session.setFileProperty(fileNameSet, fileTimestampSet);
                                        checkDataSetStatus();
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Berhasil mengunduh data training",
                                                Toast.LENGTH_SHORT).show();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                            }
                                        });
                                    } else
                                        flagStatus.edit().putBoolean("status_file", false).apply();
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

    private void checkTrainingFileFromServer() {
        //Showing the progress dialog
        progressDialog.setMessage("Cek training data ... ");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                NetworkUtils.CEK_DATA_TRAINING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "File Response: " + response);
                        try {
                            List<String> fileNameList = new ArrayList<>();
                            List<String> fileTimestampList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray files = jsonObject.getJSONArray("filenya");
                            for (int i = 0; i < files.length(); i++) {
                                JSONObject file = files.getJSONObject(i);
                                String namaFile = file.getString("nama");
                                String timeFile = file.getString("updated_at");
                                fileNameList.add(namaFile);
                                fileTimestampList.add(timeFile);
                            }
                            fileNameSet = new HashSet<>(fileNameList);
                            fileTimestampSet = new HashSet<>(fileTimestampList);

                            Log.d("cek file", String.valueOf(session.getFileProperty().get("file_name")));
                            Log.d("cek file", String.valueOf(fileNameSet));
                            Log.d("cek file", String.valueOf(session.getFileProperty().get("file_timestamp")));
                            Log.d("cek file", String.valueOf(fileTimestampSet));

                            progressDialog.dismiss();
                            flagStatus.edit().putString("cek_update_time", SikemasDateUtils.getCurrentTimeStamp(mContext)).apply();

                            // cek training data existence
                            File dataDirectory = new File(DATA_PATH);
                            File svmDirectory = new File(SVM_PATH);
                            if(dataDirectory.exists() && svmDirectory.exists() && svmDirectory.listFiles().length >= 3){
                                if (session.getFileProperty().get("file_name") == null ||
                                        session.getFileProperty().get("file_timestamp") == null)
                                    downloadFileFromUrl();
                                else if (session.getFileProperty().get("file_name").equals(fileNameSet) &&
                                        session.getFileProperty().get("file_timestamp").equals(fileTimestampSet)) {
                                    checkDataSetStatus();
                                    Toast.makeText(getApplicationContext(), "Aplikasi telah memiliki data training terbaru",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    downloadFileFromUrl();
                                    Toast.makeText(getApplicationContext(), "Data training telah diperbarui",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                downloadFileFromUrl();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}