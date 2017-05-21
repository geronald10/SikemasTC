package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
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
import id.ac.its.sikemastc.adapter.KelasDosenAdapter;
import id.ac.its.sikemastc.utilities.InputStreamVolleyRequest;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class KelolaDataSetWajah extends AppCompatActivity {

    private static final String TAG = KelolaDataSetWajah.class.getSimpleName();
    // flag if the data has been trained or not
    private SharedPreferences flagStatus;

    private String userTerlogin;
    private String userId;
    private ArrayList<String> imageUrlList;
    private ArrayList<String> encodedImageList;
    private FileHelper fh;

    private Context mContext;
    private TextView tvUserTerlogin;
    private TextView tvJumlahDataSet;
    private Button btnTambahDataSet;
    private Button btnSinkronisasi;
    private Button btnTrainingDataSet;
    private Button btnUploadFile;

    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_data_set_wajah);
        mContext = this;

        flagStatus = getSharedPreferences("flag_status", 0);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

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
        userTerlogin = intent.getStringExtra("identitas_mahasiswa");
        userId = intent.getStringExtra("id_mahasiswa");

        tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvJumlahDataSet = (TextView) findViewById(R.id.tv_data_set);

        tvUserTerlogin.setText(userTerlogin);

        btnTambahDataSet = (Button) findViewById(R.id.btn_tambah_data_set_wajah);
        btnSinkronisasi = (Button) findViewById(R.id.btn_sinkronisasi_dataset);
        btnTrainingDataSet = (Button) findViewById(R.id.btn_train_data_set_wajah);
        btnUploadFile = (Button) findViewById(R.id.btn_upload_file);

        btnTambahDataSet.setOnClickListener(operate);
        btnSinkronisasi.setOnClickListener(operate);
        btnTrainingDataSet.setOnClickListener(operate);
        btnUploadFile.setOnClickListener(operate);

        checkDataSetStatus();
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_tambah_data_set_wajah:
                    Intent intentToStart = new Intent(v.getContext(), TambahDataSetWajah.class);
                    intentToStart.putExtra("user_terlogin", userTerlogin);
                    intentToStart.putExtra("method", TambahDataSetWajah.TIME);
                    intentToStart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Add dataset photos to "Training" folder
                    FileHelper newFile = new FileHelper();
                    if (isNameAlreadyUsed(newFile.getTrainingList(), userTerlogin)) {
                        Log.d("TrainingList", String.valueOf(newFile.getTrainingList()));
                        Toast.makeText(getApplicationContext(), "Data Set Wajah Ditemukan", Toast.LENGTH_SHORT).show();
                    } else {
                        intentToStart.putExtra("Folder", "Training");
                        startActivityForResult(intentToStart, 1);
                    }
                    break;
                case R.id.btn_sinkronisasi_dataset:
                    sinkronisasiDataSet(userId);
                    break;
                case R.id.btn_train_data_set_wajah:
                    if (!flagStatus.getBoolean("training_flag", true)) {
                        Intent intentToTraining = new Intent(v.getContext(), TrainingWajah.class);
                        startActivity(intentToTraining);
                    } else {
                        Toast.makeText(getApplication(), "Fitur Training Data Set tidak dapat " +
                                        "digunakan\nFitur ini aktif ketika sistem mendapatkan data set baru",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.btn_upload_file:
                    if (!flagStatus.getBoolean("upload_flag", true)) {
                        encodedImageList = new ArrayList<>();
                        encodedImageList = getAllEncodedImageFormat();
                        uploadImages(encodedImageList);
                    } else {
                        Toast.makeText(getApplication(), "Fitur Upload NON-AKTIF\nFitur ini aktif " +
                                        "ketika sistem gagal mengirimkan dataset ke server",
                                Toast.LENGTH_LONG).show();
                    }
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
                encodedImageList = new ArrayList<>();
                encodedImageList = getAllEncodedImageFormat();
                uploadImages(encodedImageList);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private boolean isNameAlreadyUsed(File[] list, String name) {
        boolean used = false;
        if (list != null && list.length > 0) {
            for (File person : list) {
                // The last token is the name --> Folder name = Person name
                String[] tokens = person.getAbsolutePath().split("/");
                final String foldername = tokens[tokens.length - 1];
                if (foldername.equals(name)) {
                    used = true;
                    break;
                }
            }
        }
        return used;
    }

    public int getJumlahDataSet() {
        FileHelper imageFiles = new FileHelper(userTerlogin);
        File[] imageList = imageFiles.getTrainingList();
        return imageList.length;
    }

    public ArrayList<String> getAllEncodedImageFormat() {
        ArrayList<String> encodedImage = new ArrayList<>();
        FileHelper imageFiles = new FileHelper(userTerlogin);
        File[] imageList = imageFiles.getTrainingList();
        Log.d("get All Image", String.valueOf(imageList.length));

        if (imageList.length > 0) {
            for (File image : imageList) {
                String imagePath = image.getAbsolutePath();
                Log.d("imagePath", imagePath);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage.add(Base64.encodeToString(imageBytes, Base64.DEFAULT));
            }
        } else {
            return null;
        }
        return encodedImage;
    }

    // Upload image to server
    private void uploadImages(final ArrayList<String> encodedImagesList) {

        StringRequest stringRequest;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Showing the progress dialog
        progressDialog.setMessage("Mengunggah Data Set ke Server ... ");
        progressDialog.show();

        final int[] numberOfPhotos = {0};
        for (int i = 0; i < encodedImagesList.size(); i++) {
            final int index = i;
            stringRequest = new StringRequest(Request.Method.POST, NetworkUtils.UPLOAD_DATASET_WAJAH_SIKEMAS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Disimissing the progress dialog
                            progressDialog.dismiss();
                            numberOfPhotos[0]++;
                            //Showing toast message of the response
                            if (numberOfPhotos[0] == encodedImagesList.size()) {
                                Toast.makeText(KelolaDataSetWajah.this, "Berhasil Kirim Data Set ke Server",
                                        Toast.LENGTH_SHORT).show();
                                flagStatus.edit().putBoolean("upload_flag", true).apply();
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
                            if (index == encodedImagesList.size())
                                Toast.makeText(KelolaDataSetWajah.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                            flagStatus.edit().putBoolean("upload_flag", false).apply();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Get encoded Image
                    String image = encodedImagesList.get(index);
                    Map<String, String> params = new HashMap<>();
                    // Adding parameters
                    params.put("image", image);
                    params.put("image_name", userTerlogin + "_" + index);
                    params.put("user_id", userId);

                    //returning parameters
                    return params;
                }
            };
            //Adding request to the queue
            VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    // Download imageUrl from server
    private void sinkronisasiDataSet(final String userId) {

        //Showing the progress dialog
        imageUrlList = new ArrayList<>();
        progressDialog.setMessage("Pengecekan Data Set Server... ");
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
                                Toast.makeText(KelolaDataSetWajah.this, "Data Set ditemukan",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(KelolaDataSetWajah.this, "Data Set tidak ditemukan, " +
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
                        Toast.makeText(KelolaDataSetWajah.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(getApplicationContext(), "Berhasil melakukan sinkronisasi",
                                                Toast.LENGTH_SHORT).show();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvJumlahDataSet.setText(String.valueOf(imageUrlList.size()));
                                                checkDataSetStatus();
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
                            Toast.makeText(KelolaDataSetWajah.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }, null);
            VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(inputStreamRequest);
        }
    }

    private void checkDataSetStatus() {
        int jumlahDataSet = getJumlahDataSet();
        tvJumlahDataSet.setText(String.valueOf(jumlahDataSet));
        if (jumlahDataSet > 0) {
            if (jumlahDataSet < 20) {
                btnTambahDataSet.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.drawable.ic_check_circle), null, null);
                btnTambahDataSet.setTextColor(ContextCompat.getColor(this, R.color.colorSecondaryText));
                btnSinkronisasi.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.drawable.ic_sync), null, null);
                btnTambahDataSet.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            } else {
                btnSinkronisasi.setCompoundDrawablesWithIntrinsicBounds(null,
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_circle, null), null, null);
                btnSinkronisasi.setTextColor(ContextCompat.getColor(this, R.color.colorSecondaryText));
                btnTambahDataSet.setCompoundDrawablesWithIntrinsicBounds(null,
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_circle, null), null, null);
                btnTambahDataSet.setTextColor(ContextCompat.getColor(this, R.color.colorSecondaryText));
            }

            if (!flagStatus.getBoolean("training_flag", true)) {
                btnTrainingDataSet.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.drawable.ic_recent_actors), null, null);
                btnTrainingDataSet.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            } else {
                btnTrainingDataSet.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.drawable.ic_check_circle), null, null);
                btnTrainingDataSet.setTextColor(ContextCompat.getColor(this, R.color.colorSecondaryText));
            }

            if (!flagStatus.getBoolean("upload_flag", true)) {
                btnUploadFile.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.drawable.ic_file_upload), null, null);
                btnTrainingDataSet.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
            else {
                btnUploadFile.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.drawable.ic_do_not_disturb), null, null);
                btnTrainingDataSet.setTextColor(ContextCompat.getColor(this, R.color.colorSecondaryText));
            }
        } else {
            btnTambahDataSet.setCompoundDrawablesWithIntrinsicBounds(null,
                    ContextCompat.getDrawable(this, R.drawable.ic_person_add), null, null);
            btnTambahDataSet.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            btnSinkronisasi.setCompoundDrawablesWithIntrinsicBounds(null,
                    ContextCompat.getDrawable(this, R.drawable.ic_sync), null, null);
            btnTambahDataSet.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDataSetStatus();
    }
}
