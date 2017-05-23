package id.ac.its.sikemastc.activity.verifikasi_tandatangan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.utilities.InputStreamVolleyRequest;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

import static id.ac.its.sikemastc.R.id.toolbar;

/**
 * Created by novitarpl on 5/16/2017.
 */

public class KelolaDataSetTandaTangan extends AppCompatActivity {

    private static final String TAG = KelolaDataSetTandaTangan.class.getSimpleName();
    private String userTerlogin;
    private String userId;
    private ArrayList<String> imageUrlList;
    private ArrayList<String> encodedImageList;

    private Context mContext;
    private TextView tvUserTerlogin;
    private TextView tvJumlahDataSet;
    private Button btnTambah;
    private Button btnSinkronisasi;
    private Button btnUploadFile;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private String DIRECTORY;
    private String StoredPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_data_set_tandatangan);
        mContext = this;

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //flag status
       // flagStatus = getSharedPreferences("flag_status", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Kelola Data Set");
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
        userTerlogin = intent.getStringExtra("identitas_mahasiswa"); //nrp_nama
        userId = intent.getStringExtra("id_mahasiswa"); //nrp

//        String training = intent.getStringExtra("training");
//        if (training != null && !training.isEmpty()) {
//            Toast.makeText(getApplicationContext(), training, Toast.LENGTH_SHORT).show();
//            intent.removeExtra("training");
//        }

        tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvJumlahDataSet = (TextView) findViewById(R.id.tv_data_set);

        tvUserTerlogin.setText(userTerlogin);

        btnTambah = (Button) findViewById(R.id.btn_tambah_data_set_tandatangan);
        btnSinkronisasi = (Button) findViewById(R.id.btn_sinkronisasi_tandatangan);
        btnUploadFile = (Button) findViewById(R.id.btn_upload_file_ttd);

        btnTambah.setOnClickListener(operate);
        btnSinkronisasi.setOnClickListener(operate);
        btnSinkronisasi.setOnClickListener(operate);
        btnUploadFile.setOnClickListener(operate);

        int jml_data_set = getJumlahDataSetTandaTangan();
        tvJumlahDataSet.setText(String.valueOf(jml_data_set));
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_tambah_data_set_tandatangan:
                    Intent intentToStart = new Intent(v.getContext(), TambahDataSetTandaTangan.class);
                    intentToStart.putExtra("user_terlogin", userTerlogin);
                    Log.v("log_tag", "user terlogin di keloladatasettandatangan" + userTerlogin);

                 //   intentToStart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add dataset photos to "Training" folder
                //    FileHelper newFile = new FileHelper();

//                    if (isNameAlreadyUsed(newFile.getTrainingList(), userTerlogin)) {
//                        Log.d("TrainingList", String.valueOf(newFile.getTrainingList()));
//                        Toast.makeText(getApplicationContext(), "Data Set Tanda Tangan Ditemukan", Toast.LENGTH_SHORT).show();
//                    } else {
                //    intentToStart.putExtra("Folder", "Training");
                      startActivityForResult(intentToStart, 1);
//                    }
                    break;

                case R.id.btn_sinkronisasi_tandatangan:
                    SinkronisasiDatasetTandaTangan(userId);
                    break;

                case R.id.btn_upload_file_ttd:
                    encodedImageList = new ArrayList<>();
                    encodedImageList = getAllEncodedImageFormat();
                    uploadImages(encodedImageList);
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String resultMessage = data.getStringExtra("result_message");
                int numberOfPictures = data.getIntExtra("number_of_pictures", 0);
                Toast.makeText(mContext, resultMessage, Toast.LENGTH_SHORT).show();
                tvJumlahDataSet.setText(String.valueOf(numberOfPictures));
//                encodedImageList = new ArrayList<>();
//                encodedImageList = getAllEncodedImageFormat();
//                uploadImages(encodedImageList);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public int getJumlahDataSetTandaTangan() {
        DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/signatureverification/";
        StoredPath = DIRECTORY + userTerlogin;

        File dir = new File(StoredPath);
        if (!dir.exists()) {
            Log.d("->", "masuk if");
            return 0;
        } else {
            File[] files = dir.listFiles();
            int numberOfFiles = files.length;
            Log.d("->","masuk else -> " + numberOfFiles);
            return numberOfFiles;
        }

//        FileHelper imageFiles = new FileHelper(userTerlogin);
//        File[] imageList = imageFiles.getTrainingList();
//        return imageList.length;

    }

    private void SinkronisasiDatasetTandaTangan(final String userId) {

        //Showing the progress dialog
        imageUrlList = new ArrayList<>();
        progressDialog.setMessage("Pengecekan Data Set Server... ");
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
                                Toast.makeText(KelolaDataSetTandaTangan.this, "Data Set ditemukan",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(KelolaDataSetTandaTangan.this, "Data Set tidak ditemukan, " +
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
                        Toast.makeText(KelolaDataSetTandaTangan.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
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
        progressDialog.setMessage("Sinkronisasi Data Set... ");
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
                                    String name = userTerlogin + "-" + index + ".png";
                                    String fullpath = wholeFolderPath + "/" + name;
                                    File outputFile = new File(fullpath);

                                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                                    outputStream.write(response);
                                    outputStream.close();
                                    if (index + 1 == imageUrlList.size()) {
                          //              flagStatus.edit().putBoolean("training_flag", false).apply();
                           //             flagStatus.edit().putBoolean("upload_flag", true).apply();
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Berhasil melakukan sinkronisasi",
                                                Toast.LENGTH_SHORT).show();
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                tvJumlahDataSet.setText(String.valueOf(imageUrlList.size()));
//                                                checkDataSetStatus();
//                                            }
//                                        });
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
                            Toast.makeText(KelolaDataSetTandaTangan.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }, null);
            VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(inputStreamRequest);
        }
    }

    public ArrayList<String> getAllEncodedImageFormat() {
        ArrayList<String> encodedImage = new ArrayList<>();

        File dir = new File(StoredPath);
        File[] files = dir.listFiles();
        int numberOfFiles = files.length;

        if (files != null && numberOfFiles > 0) {
            for (File image : files) {
                String imagePath = image.getAbsolutePath();
                Log.d("imagePath", imagePath);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage.add(Base64.encodeToString(imageBytes, Base64.DEFAULT));

            }
            return encodedImage;
        } else {
            return null;
        }
    }

    // Upload image to server
    private void uploadImages(final ArrayList<String> encodedImagesList) {

        StringRequest stringRequest;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.d(TAG, "masuk fungsi upload");

        //Showing the progress dialog
        progressDialog.setMessage("Mengunggah Data Set ke Server ... ");
        progressDialog.show();

        final int[] numberOfPhotos = {0};
        for (int i = 0; i < encodedImagesList.size(); i++) {
            final int index = i;
            stringRequest = new StringRequest(Request.Method.POST, NetworkUtils.UPLOAD_DATASET_TANDATANGAN_SIKEMAS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Disimissing the progress dialog
                            progressDialog.dismiss();
                            numberOfPhotos[0]++;
                            //Showing toast message of the response
                            if (numberOfPhotos[0] == encodedImagesList.size()) {
                                Toast.makeText(KelolaDataSetTandaTangan.this, "Berhasil Kirim Data Set ke Server",
                                        Toast.LENGTH_SHORT).show();
                            }
                            Log.d("VolleyResponse", "Dapat ResponseVolley Upload Images");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            // Dismissing the progress dialog
                            progressDialog.dismiss();
                            Log.d("VolleyErroyResponse", "Error");
                            //Showing toast
                            Toast.makeText(KelolaDataSetTandaTangan.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Get encoded Image
                    String image = encodedImagesList.get(index);
                    Map<String, String> params = new HashMap<>();
                    // Adding parameters
                    params.put("image", image);
                    params.put("image_name", userTerlogin + "-" + (index+1));
                    params.put("user_id", userId);

                    return params;
                }
            };
            //Adding request to the queue
            VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }
}
