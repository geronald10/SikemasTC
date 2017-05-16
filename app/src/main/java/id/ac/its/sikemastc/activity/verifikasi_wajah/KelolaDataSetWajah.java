package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class KelolaDataSetWajah extends AppCompatActivity {

    private String userTerlogin;
    private String userId;
    private ArrayList<String> encodedImageList;

    private Context mContext;
    private TextView tvUserTerlogin;
    private TextView tvJumlahDataSet;
    private Button btnStart;
    private Button btnSinkronisasi;
    private Button btnUploadFile;
    private Button btnTraining;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_data_set_wajah);
        mContext = this;

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Kelola Data Set");
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
        userTerlogin = intent.getStringExtra("identitas_mahasiswa");
        userId = intent.getStringExtra("id_mahasiswa");
        String training = intent.getStringExtra("training");
        if (training != null && !training.isEmpty()) {
            Toast.makeText(getApplicationContext(), training, Toast.LENGTH_SHORT).show();
            intent.removeExtra("training");
        }

        tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvJumlahDataSet = (TextView) findViewById(R.id.tv_data_set);

        tvUserTerlogin.setText(userTerlogin);

        btnStart = (Button) findViewById(R.id.btn_tambah_data_set_wajah);
        btnSinkronisasi = (Button) findViewById(R.id.btn_sinkronisasi_dataset);
        btnUploadFile = (Button) findViewById(R.id.btn_upload_file);
        btnTraining = (Button) findViewById(R.id.btn_train_data_set);

        btnStart.setOnClickListener(operate);
        btnSinkronisasi.setOnClickListener(operate);
        btnUploadFile.setOnClickListener(operate);
        btnTraining.setOnClickListener(operate);

        if (getJumlahDataSet() > 0) {
            tvJumlahDataSet.setText(String.valueOf(getJumlahDataSet()));
        }
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
                    break;
                case R.id.btn_upload_file:
                    encodedImageList = new ArrayList<>();
                    encodedImageList = getAllEncodedImageFormat();
                    uploadImages(encodedImageList);
                    break;
                case R.id.btn_train_data_set:
                    Intent intentToTraining = new Intent(v.getContext(), TrainingWajah.class);
                    startActivity(intentToTraining);;
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
                            Toast.makeText(KelolaDataSetWajah.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // Get encoded Image
                    String image = encodedImagesList.get(index);
                    Map<String, String> params = new HashMap<>();
                    // Adding parameters
                    params.put("image", image);
                    params.put("image_name", userTerlogin + "_" + index+1);
                    params.put("user_id", userId);

                    //returning parameters
                    return params;
                }
            };
            //Adding request to the queue
            VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }
}
