package id.ac.its.sikemastc.activity.verifikasi_tandatangan;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
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
    LinearLayout mContent;
    signature mSignature;  // fungsi untuk menampilkan dialog pop up
    View view;
    private Button btn_tambah_tandatangan_cancel, btn_tambah_tandatangan_clear, btn_tambah_tandatangan_simpan;
    private Context mContext;
    private TextView tvUserTerlogin;
    private TextView tvJumlahDataSet;
    private Button btnTambah;
    private Button btnSinkronisasi;
    private Button btnUploadFile;
    private Toolbar toolbar, toolbar1;
    private String DIRECTORY;
    private String StoredPath;
    private File dir;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private Bitmap bitmap;
    int clickcount = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_data_set_tandatangan);
        mContext = this;

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Kelola Dataset");
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
        userId = intent.getStringExtra("id_mahasiswa");

        //folder
        DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/signatureverification/";
        StoredPath = DIRECTORY + userTerlogin;
        Log.d("storepath->", StoredPath);
        dir = new File(StoredPath);

        tvUserTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        tvJumlahDataSet = (TextView) findViewById(R.id.tv_data_set);

        tvUserTerlogin.setText(userTerlogin);

        btnTambah = (Button) findViewById(R.id.btn_tambah_data_set_tandatangan);
        btnSinkronisasi = (Button) findViewById(R.id.btn_sinkronisasi_tandatangan);
        btnUploadFile = (Button) findViewById(R.id.btn_upload_file_tandatangan);

        btnTambah.setOnClickListener(operate);
        btnSinkronisasi.setOnClickListener(operate);
        btnSinkronisasi.setOnClickListener(operate);
        btnUploadFile.setOnClickListener(operate);

        int jml_data_set = getJumlahDataSetTandaTangan();
        tvJumlahDataSet.setText(String.valueOf(jml_data_set));

    }

    private int getJumlahDataSetTandaTangan() {
        Log.d("->", " masuk fungsi jumlah dataset tanda tangan");

        if (!dir.exists()) {
            Log.d("->", "directory kosong");
            return 0;
        } else {
            File[] files = dir.listFiles();
            int numberOfFiles = files.length;
            Log.d("->","masuk else -> " + numberOfFiles);
            return numberOfFiles;
        }

    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_tambah_data_set_tandatangan:
                    if (getJumlahDataSetTandaTangan() < 5) {
                        // Dialog Function
                        dialog = new Dialog(KelolaDataSetTandaTangan.this);
                        // Removing the features of Normal Dialogs
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.tambah_data_set_tandatangan);
                        dialog.setCancelable(false); // supaya dialog tidak close ketika click sembarang
                        dialog_action();
                    }
                    else
                    {
                        Toast.makeText(mContext, "Dataset Tanda Tangan Sudah Ada", Toast.LENGTH_LONG).show();
                    }
                    break;

                case R.id.btn_sinkronisasi_tandatangan:
                    if(dir.exists()){
                        Toast.makeText(mContext, "Dataset Tanda Tangan Sudah Ada", Toast.LENGTH_LONG).show();
                    }
                    else {
                        SinkronisasiDatasetTandaTangan(userId);
                    }
                    break;

                case R.id.btn_upload_file_tandatangan:
                    if (!dir.exists()) {
                        Toast.makeText(mContext, "Dataset Tanda Tangan Kosong", Toast.LENGTH_LONG).show();
                    }
                    else {
                        encodedImageList = new ArrayList<>();
                        encodedImageList = getAllEncodedImageFormat();
                        uploadImages(encodedImageList);
                    }
                    break;
            }
        }
    };

    //tambah dataset
    public void dialog_action() {
        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout_tambah_tandatangan);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);

        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView header = (TextView) dialog.findViewById(R.id.tv_tambah_data_set_tandatangan);
        header.setText("TANDA TANGAN SEBANYAK 5 KALI");

        btn_tambah_tandatangan_cancel = (Button) dialog.findViewById(R.id.btn_tambah_tandatangan_cancel);
        btn_tambah_tandatangan_clear = (Button) dialog.findViewById(R.id.btn_tambah_tandatangan_clear);
        btn_tambah_tandatangan_simpan = (Button) dialog.findViewById(R.id.btn_tambah_tandatangan_simpan);

        btn_tambah_tandatangan_simpan.setEnabled(false);
        view = mContent;

        btn_tambah_tandatangan_clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                btn_tambah_tandatangan_simpan.setEnabled(false);
            }
        });

        btn_tambah_tandatangan_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                File[] files = dir.listFiles();
                dialog.dismiss();
//                if (!dir.exists()) {
//                    Toast.makeText(KelolaDataSetTandaTangan.this, "Dataset belum ada, silahkan melakukan tanda tangan", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    dialog.dismiss();
//                    int numberOfFiles = files.length;
//                    if(numberOfFiles != 5){
//                        Toast.makeText(KelolaDataSetTandaTangan.this, "Dataset kurang dari 5, Dataset tersimpan -> " + numberOfFiles, Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        dialog.dismiss();
//                    }
//                }
            }
        });

        btn_tambah_tandatangan_simpan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v("log_tag", "clickcount->" + clickcount);
                clickcount=clickcount+1;
                if(clickcount<=5)
                {
                    Log.v("log_tag", "Panel Saved");
                    view.setDrawingCacheEnabled(true);
                    mSignature.save(view);
                    Toast.makeText(getApplicationContext(), "Sukses Simpan Dataset ke- " + clickcount, Toast.LENGTH_SHORT).show();
                    checkDataSetStatus();
                }
                if (clickcount == 5)
                {
                    // upload
                    encodedImageList = new ArrayList<>();
                    encodedImageList = getAllEncodedImageFormat();
                    uploadImages(encodedImageList);
                    btn_tambah_tandatangan_simpan.setEnabled(true);
                    checkDataSetStatus();
                    dialog.dismiss();
                }
                mSignature.clear();
                //recreate();
            }
        });
        dialog.show();
    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }

            Canvas canvas = new Canvas(bitmap);
            try {

                dir = new File(StoredPath);
                String filename = userTerlogin+ "-" + clickcount+ ".png";
                if(!dir.exists())
                {
                    dir.mkdirs();
                }

                // file output binary
                File myFile = new File(dir.getAbsolutePath(), filename);
                myFile.createNewFile();
                FileOutputStream mFileOutStream = new FileOutputStream(myFile);
                v.draw(canvas);

                // Mengkonversi file output ke gambar .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            btn_tambah_tandatangan_simpan.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    // debug("Ignored touch event: " + event.toString());
                    //return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }


        private void debug(String string) {
            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }

    }

    //sinkronisasi dataset
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
                                Toast.makeText(KelolaDataSetTandaTangan.this, "Dataset ditemukan",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(KelolaDataSetTandaTangan.this, "Dataset tidak ditemukan, " +
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
                                        checkDataSetStatus();
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
        Log.d("number of files", String.valueOf(numberOfFiles));

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
        progressDialog.setMessage("Mengunggah Dataset ke Server ... ");
        progressDialog.show();

        final int[] numberOfPhotos = {0};
        for (int i = 0; i < encodedImagesList.size(); i++) {
            final int index = i;
            stringRequest = new StringRequest(Request.Method.POST, NetworkUtils.UPLOAD_DATASET_TANDATANGAN_SIKEMAS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Disimissing the progress dialog
                            numberOfPhotos[0]++;
                            //Showing toast message of the response
                            if (numberOfPhotos[0] == encodedImagesList.size()) {
                                progressDialog.dismiss();
                                Toast.makeText(KelolaDataSetTandaTangan.this, "Berhasil Kirim Dataset ke Server",
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

    private void checkDataSetStatus() {
        int jumlahDataSetClient = getJumlahDataSetTandaTangan();
        tvJumlahDataSet.setText(String.valueOf(jumlahDataSetClient));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDataSetStatus();
    }
}
