package id.ac.its.sikemastc.activity;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import id.ac.its.sikemastc.R;

public class AddSetWajahPreview extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

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
    private ImageButton btnCapture;
    private boolean capturePressed;
    private boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;

    public LibraryPreference preferences;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_set_wajah_preview);

        preferences = new LibraryPreference(this);
        preferences.createCameraSettings();
        HashMap<String, String> cameraSettings = preferences.getCameraSettings();

        Intent intent = getIntent();
        folder = intent.getStringExtra("Folder");
        if (folder.equals("Test")) {
            subfolder = intent.getStringExtra("Subfolder");
        }
        name = intent.getStringExtra("Name");
        method = intent.getIntExtra("Method", 0);
        capturePressed = false;
        if(method == MANUALLY) {
            btnCapture = (ImageButton)findViewById(R.id.btn_capture);
            btnCapture.setVisibility(View.VISIBLE);
            btnCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturePressed = true;
                }
            });
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

        if (front_camera){
            mAddSetWajahView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        } else {
            mAddSetWajahView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }
        mAddSetWajahView.setVisibility(SurfaceView.VISIBLE);
        mAddSetWajahView.setCvCameraViewListener(this);

        int maxCameraViewWidth = Integer.parseInt(cameraSettings.get(LibraryPreference.KEY_CAMERA_VIEW_WIDTH));
        int maxCameraViewHeight = Integer.parseInt(cameraSettings.get(LibraryPreference.KEY_CAMERA_VIEW_HEIGHT));
        mAddSetWajahView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);
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
        if(front_camera){
            Core.flip(imgRgba,imgRgba,1);
        }

        long time = new Date().getTime();
        if((method == MANUALLY) || (method == TIME) && (lastTime + timerDiff < time)){
            lastTime = time;

            // Check that only 1 face is found. Skip if any or more than 1 are found.
            List<Mat> images = ppF.getCroppedImage(imgCopy);
            if (images != null && images.size() == 1){
                Mat img = images.get(0);
                if(img != null){
                    Rect[] faces = ppF.getFacesForRecognition();
                    //Only proceed if 1 face has been detected, ignore if 0 or more than 1 face have been detected
                    if((faces != null) && (faces.length == 1)){
                        faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
                        if(((method == MANUALLY) && capturePressed) || (method == TIME)){
                            MatName m = new MatName(name + "_" + total, img);
                            if (folder.equals("Test")) {
                                String wholeFolderPath = fh.TEST_PATH + name + "/" + subfolder;
                                new File(wholeFolderPath).mkdirs();
                                fh.saveMatToImage(m, wholeFolderPath + "/");
                            } else {
                                String wholeFolderPath = fh.TRAINING_PATH + name;
                                new File(wholeFolderPath).mkdirs();
                                fh.saveMatToImage(m, wholeFolderPath + "/");
                            }

                            for(int i = 0; i<faces.length; i++){
                                MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], String.valueOf(total), front_camera);
                            }

                            total++;

                            // Stop after numberOfPictures (settings option)
                            if(total >= numberOfPictures){
                                Intent intent = new Intent(getApplicationContext(), MenuValidasiWajah.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            capturePressed = false;
                        } else {
                            for(int i = 0; i<faces.length; i++){
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
    public void onResume()
    {
        super.onResume();

        ppF = new PreProcessorFactory(this);
        mAddSetWajahView.enableView();
    }

    @Override
    public void onPause()
    {
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
