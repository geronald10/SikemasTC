package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition;
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.utilities.LibraryPreference;

public class VerifikasiWajah extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CustomCameraView mRecognitionView;
    private static final String TAG = "Recognition";
    private FileHelper fh;
    private Recognition rec;
    private PreProcessorFactory ppF;
    private ProgressBar progressBar;
    private boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;

    private String idPerkuliahan;
    private String identitasMahasiswa;
    private String identitasTerdeteksi;
    private LibraryPreference preferences;

    private TextView tvUserDetil;
    private TextView tvUserDetected;
    private ProgressBar pbVerificationLoading;
    private int progress;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_face_verification);

        preferences = new LibraryPreference(this);
        preferences.createCameraSettings();
        HashMap<String, String> cameraSettings = preferences.getCameraSettings();

        Intent intent = getIntent();
        identitasMahasiswa = intent.getStringExtra("identitas_mahasiswa");
        idPerkuliahan = intent.getStringExtra("id_perkuliahan");

        tvUserDetil = (TextView) findViewById(R.id.tv_user_detail);
        tvUserDetected = (TextView) findViewById(R.id.tv_identitas_dikenali);
        pbVerificationLoading = (ProgressBar) findViewById(R.id.pb_verification_process);

        tvUserDetil.setText(identitasMahasiswa);
        tvUserDetected.setText("-");

        fh = new FileHelper();
        File folder = new File(fh.getFolderPath());
        if (folder.mkdir() || folder.isDirectory()) {
            Log.i(TAG, "New directory for photos created");
        } else {
            Log.i(TAG, "Photos directory already existing");
        }

        mRecognitionView = (CustomCameraView) findViewById(R.id.verifikasi_wajah);
        // Use camera which is selected in settings
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        front_camera = Boolean.valueOf(cameraSettings.get(LibraryPreference.KEY_FRONT_CAMERA));
        night_portrait = Boolean.valueOf(cameraSettings.get(LibraryPreference.KEY_NIGHT_PORTRAIT_MODE));
        exposure_compensation = Integer.valueOf(cameraSettings.get(LibraryPreference.KEY_EXPOSURE));

        if (front_camera) {
            mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        } else {
            mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }
        mRecognitionView.setVisibility(SurfaceView.VISIBLE);
        mRecognitionView.setCvCameraViewListener(this);

        int maxCameraViewWidth = Integer.parseInt(cameraSettings.get(LibraryPreference.KEY_CAMERA_VIEW_WIDTH));
        int maxCameraViewHeight = Integer.parseInt(cameraSettings.get(LibraryPreference.KEY_CAMERA_VIEW_HEIGHT));
        mRecognitionView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecognitionView != null)
            mRecognitionView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mRecognitionView != null)
            mRecognitionView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        if (night_portrait) {
            mRecognitionView.setNightPortrait();
        }
        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100)
            mRecognitionView.setExposure(exposure_compensation);
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba = inputFrame.rgba();
        Mat img = new Mat();
        imgRgba.copyTo(img);
        List<Mat> images = ppF.getProcessedImage(img, PreProcessorFactory.PreprocessingMode.RECOGNITION);
        Rect[] faces = ppF.getFacesForRecognition();

        // Selfie / Mirror mode
        if (front_camera) {
            Core.flip(imgRgba, imgRgba, 1);
        }
        if (images == null || images.size() == 0 || faces == null || faces.length == 0 || !(images.size() == faces.length)) {
            // skip
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvUserDetected.setText("Wajah tidak ditemukan");
                    progress = 0;
                    pbVerificationLoading.setProgress(progress);
                }
            });
            return imgRgba;
        } else {
            faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
            for (int i = 0; i < faces.length; i++) {
                MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], rec.recognize(images.get(i), ""), front_camera);
                identitasTerdeteksi = rec.recognize(images.get(i), "");
                if (identitasTerdeteksi.equals(identitasMahasiswa))
                    progress += 10;
                else
                    progress = 0;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (identitasTerdeteksi.equals(identitasMahasiswa)) {
                        tvUserDetected.setText(identitasTerdeteksi);
                        pbVerificationLoading.setProgress(progress);
                        if (progress == 100) {
                            showAlertDialog(idPerkuliahan);
                        }
                    } else {
                        progress = 0;
                        pbVerificationLoading.setProgress(progress);
                        tvUserDetected.setText("Tidak Cocok");
                    }
                }
            });
            return imgRgba;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ppF = new PreProcessorFactory(getApplicationContext());

        final android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
        Thread t = new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String algorithm = sharedPref.getString("key_classification_method", getResources().getString(R.string.imageReshaping));
                rec = RecognitionFactory.getRecognitionAlgorithm(getApplicationContext(), Recognition.RECOGNITION, algorithm);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        t.start();

        // Wait until Eigenfaces loading thread has finished
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRecognitionView.enableView();
    }

    private void showAlertDialog(final String idPerkuliahan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi")
                .setMessage("Kirimkan status verifikasi kehadiran?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intentToSuccess = new Intent(VerifikasiWajah.this, HasilVerifikasi.class);
                        intentToSuccess.putExtra("id_perkuliahan", idPerkuliahan);
                        intentToSuccess.putExtra("identitas_mahasiswa", identitasMahasiswa);
                        intentToSuccess.putExtra("ket_kehadiran", "M");
                        startActivity(intentToSuccess);
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
