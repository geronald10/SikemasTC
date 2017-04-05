package id.ac.its.sikemastc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.HashMap;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import id.ac.its.sikemastc.R;

public class DetectionView extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static String TAG = DetectionView.class.getSimpleName();

    private boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;
    private CustomCameraView mDetectionView;
    private PreProcessorFactory ppF;

    private LibraryPreference preferences;

    static {
        if (!OpenCVLoader.initDebug()) {
            // handle if initialization error
        } else {
            Log.d(TAG, "Failed to Load OpenCV Library");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_detection_view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        preferences = new LibraryPreference(this);
        preferences.createCameraSettings();
        HashMap<String, String> detectionSettings = preferences.getCameraSettings();

        mDetectionView = (CustomCameraView) findViewById(R.id.DetectionView);
        front_camera = Boolean.valueOf(detectionSettings.get(LibraryPreference.KEY_FRONT_CAMERA));
        night_portrait = Boolean.valueOf(detectionSettings.get(LibraryPreference.KEY_NIGHT_PORTRAIT_MODE));
        exposure_compensation = Integer.valueOf(detectionSettings.get(LibraryPreference.KEY_EXPOSURE));

        if (front_camera) {
            mDetectionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        } else {
            mDetectionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }
        mDetectionView.setVisibility(SurfaceView.VISIBLE);
        mDetectionView.setCvCameraViewListener(this);

        int maxCameraViewWidth = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_width", "640"));
        int maxCameraViewHeight = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_height", "480"));
        mDetectionView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        if (night_portrait) {
            mDetectionView.setNightPortrait();
        }

        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100)
            mDetectionView.setExposure(exposure_compensation);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba = inputFrame.rgba();
        Mat img = new Mat();
        imgRgba.copyTo(img);
        List<Mat> images = ppF.getCroppedImage(img);
        Rect[] faces = ppF.getFacesForRecognition();

        // Selfie (Front Camera) / Mirror mode
        if (front_camera) {
            Core.flip(imgRgba, imgRgba, 1);
        }
        if (images == null || images.size() == 0 || faces == null || faces.length == 0 || !(images.size() == faces.length)) {
            return imgRgba;
        } else {
            faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
            for (int i = 0; i<faces.length; i++) {
                MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], "", front_camera);
            }
            return imgRgba;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ppF = new PreProcessorFactory(getApplicationContext());
        mDetectionView.enableView();
    }
}
