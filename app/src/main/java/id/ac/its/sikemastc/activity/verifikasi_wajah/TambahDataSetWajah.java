package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.helper.SwitchPreference;
import id.ac.its.sikemastc.utilities.LibraryPreference;

public class TambahDataSetWajah extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    public static final int TIME = 0;
    public static final int MANUALLY = 1;
    private CustomCameraView mAddSetWajahView;
    // The timerDiff defines after how many milliseconds a picture is taken
    private long timerDiff;
    private long lastTime;
    private PreProcessorFactory ppF;
    private FileHelper fh;
    private String folder;
    private String subfolder;
    private String name;
    private int total;
    private int numberOfPictures;
    private int method;
    private boolean capturePressed;
    private boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;

//    private Switch switchCaptureMode;
//    private ImageButton btnCapture;
    private ImageView capturedImagePreview;
    private TextView counter;
    private TextView userTerlogin;
    private LibraryPreference preferences;
    private SharedPreferences flagStatus;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_tambah_data_set_wajah);

        flagStatus = getSharedPreferences("flag_status", 0);

        preferences = new LibraryPreference(this);
        preferences.createCameraSettings();
        HashMap<String, String> cameraSettings = preferences.getCameraSettings();

        Intent intent = getIntent();
        folder = intent.getStringExtra("Folder");
        if (folder.equals("Test")) {
            subfolder = intent.getStringExtra("Subfolder");
        }
        name = intent.getStringExtra("user_terlogin");
        method = intent.getIntExtra("method", 0);
//        switchCaptureMode = (Switch) findViewById(R.id.switch_capture_mode);
//        btnCapture = (ImageButton) findViewById(R.id.btn_capture);

//        switchCaptureMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    method = TIME;
//                    Toast.makeText(getApplicationContext(), "Menambahkan gambar secara OTOMATIS",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    method = MANUALLY;
//                    Toast.makeText(getApplicationContext(), "Menambahkan gambar secara MANUAL",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        if (method == MANUALLY) {
//            btnCapture.setVisibility(View.VISIBLE);
//            btnCapture.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    capturePressed = true;
//                }
//            });
//        } else {
//            btnCapture.setVisibility(View.GONE);
//            capturePressed = false;
//        }
        if (method == MANUALLY) {
            capturePressed = true;
        } else {
            capturePressed = false;
        }

        fh = new FileHelper();
        total = 0;
        lastTime = new Date().getTime();

        timerDiff = Integer.valueOf(cameraSettings.get(LibraryPreference.KEY_TIMERDIFF));
        mAddSetWajahView = (CustomCameraView) findViewById(R.id.AddSetWajahPreview);
        front_camera = Boolean.valueOf(cameraSettings.get(LibraryPreference.KEY_FRONT_CAMERA));
        numberOfPictures = Integer.valueOf(cameraSettings.get(LibraryPreference.KEY_NUMBER_OF_PICTURES));
        night_portrait = Boolean.valueOf(cameraSettings.get(LibraryPreference.KEY_NIGHT_PORTRAIT_MODE));
        exposure_compensation = Integer.valueOf(cameraSettings.get(LibraryPreference.KEY_EXPOSURE));

        if (front_camera) {
            mAddSetWajahView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        } else {
            mAddSetWajahView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }
        mAddSetWajahView.setVisibility(SurfaceView.VISIBLE);
        mAddSetWajahView.setCvCameraViewListener(this);

        int maxCameraViewWidth = Integer.parseInt(cameraSettings.get(LibraryPreference.KEY_CAMERA_VIEW_WIDTH));
        int maxCameraViewHeight = Integer.parseInt(cameraSettings.get(LibraryPreference.KEY_CAMERA_VIEW_HEIGHT));
        mAddSetWajahView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);

        userTerlogin = (TextView) findViewById(R.id.tv_user_detail);
        capturedImagePreview = (ImageView) findViewById(R.id.iv_captured_image);
        counter = (TextView) findViewById(R.id.tv_counter);
        counter.setText("0");
        userTerlogin.setText(name);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        if (night_portrait) {
            mAddSetWajahView.setNightPortrait();
        }

        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100)
            mAddSetWajahView.setExposure(exposure_compensation);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba = inputFrame.rgba();
        Mat imgCopy = new Mat();
        imgRgba.copyTo(imgCopy);
        // Selfie / Mirror mode
        if (front_camera) {
            Core.flip(imgRgba, imgRgba, 1);
        }

        long time = new Date().getTime();
        if ((method == MANUALLY) || (method == TIME) && (lastTime + timerDiff < time)) {
            lastTime = time;
            // Check that only 1 face is found. Skip if any or more than 1 are found.
            final List<Mat> images = ppF.getCroppedImage(imgCopy);
            if (images != null && images.size() == 1) {
                Mat img = images.get(0);
                if (img != null) {
                    Rect[] faces = ppF.getFacesForRecognition();
                    //Only proceed if 1 face has been detected, ignore if 0 or more than 1 face have been detected
                    if ((faces != null) && (faces.length == 1)) {
                        faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
                        if (((method == MANUALLY) && capturePressed) || (method == TIME)) {
                            final MatName m = new MatName(name + "_" + total, img);
                            if (folder.equals("Test")) {
                                String wholeFolderPath = fh.TEST_PATH + name + "/" + subfolder;
                                new File(wholeFolderPath).mkdirs();
                                fh.saveMatToImage(m, wholeFolderPath + "/");
                            } else {
                                String wholeFolderPath = fh.TRAINING_PATH + name;
                                new File(wholeFolderPath).mkdirs();
                                fh.saveMatToImage(m, wholeFolderPath + "/");
                            }

                            for (int i = 0; i < faces.length; i++) {
                                MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], String.valueOf(total + 1), front_camera);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Mat mat = m.getMat();
                                        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                                        Utils.matToBitmap(mat, bitmap);
                                        capturedImagePreview.setImageBitmap(bitmap);
                                    }
                                });
                            }
                            total++;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    counter.setText(String.valueOf(total));
                                }
                            });

                            // Stop after numberOfPictures (settings option)
                            if (total >= numberOfPictures) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result_message", "Berhasil menambahkan data set wajah");
                                returnIntent.putExtra("number_of_pictures", total);
                                setResult(Activity.RESULT_OK, returnIntent);
                                flagStatus.edit().putBoolean("upload_flag", true).apply();
                                flagStatus.edit().putBoolean("training_flag", false).apply();
                                finish();
                            }
                            capturePressed = false;
                        } else {
                            for (int i = 0; i < faces.length; i++) {
                                MatOperation.drawRectangleOnPreview(imgRgba, faces[i], front_camera);
                            }
                        }
                    }
                }
            }
        }
        return imgRgba;
    }

    @Override
    public void onResume() {
        super.onResume();

        ppF = new PreProcessorFactory(this);
        mAddSetWajahView.enableView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAddSetWajahView != null)
            mAddSetWajahView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mAddSetWajahView != null)
            mAddSetWajahView.disableView();
    }
}
