package id.ac.its.sikemastc.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LibraryPreference {

    private SharedPreferences libPref;
    private Editor editor;
    private Context _context;
    int PRIVATE_MODE = 0;

    // Shared Preference file name
    private static final String PREF_NAME = "SikemasPref";

    // General Settings
    public static final String KEY_FRONT_CAMERA = "key_front_camera";
    public static final String KEY_NIGHT_PORTRAIT_MODE = "key_night_portrait";
    public static final String KEY_EXPOSURE = "key_exposure_compensation";
    public static final String KEY_CAMERA_VIEW_WIDTH = "key_maximum_camera_view_width";
    public static final String KEY_CAMERA_VIEW_HEIGHT = " key_maximum_camera_view_height";
    public static final String KEY_NUMBER_OF_PICTURES = "key_numberOfPictures"; // int 20
    public static final String KEY_TIMERDIFF = "key_timerDiff"; // int 500
    public static final String KEY_FACE_SIZE = "key_faceSize"; // int 224

    // Face Detection
    public static final String KEY_DETECTION_METHOD = "key_detection_method"; // true (OPENCV detectMultiScale)
    public static final String KEY_EYE_DETECTION = "key_eye_detection"; // false (disabled)
    public static final String KEY_OPENCV_CASCADE_FILE = "key_face_cascade_file"; // @string/haarcascade_alt2
    public static final String KEY_LEFTEYE_CASCADE_FILE = "key_lefteye_cascade_file"; // @string/haarcascade_lefteye
    public static final String KEY_RIGHTEYE_CASCADE_FILE = "key_righteye_cascade_file"; // @string/haarcascade_righteye
    public static final String KEY_SCALE_FACTOR = "key_scaleFactor"; // 1.1
    public static final String KEY_MIN_NEIGHBORS = "key_minNeighbors"; // int 3
    public static final String KEY_FLAGS = "key_flags"; // int 2

    // Preprocessing Detection
    public static final String KEY_DETECTION_STANDARD_PRE = "key_detection_standard_pre"; //"@array/detection_standard_pre_default"/>
    public static final String KEY_DETECTION_BRIGHTNESS = "key_detection_brightness"; // "@array/empty_array"/>
    public static final String KEY_DETECTION_COUNTOURS = "key_detection_contours"; // "@array/empty_array"/>
    public static final String KEY_DETECTION_CONTRAST = "key_detection_contrast"; // "@array/empty_array"/>
    public static final String KEY_DETECTION_GAMMA = "key_detection_gamma"; // "@string/gamma" decimal
    public static final String KEY_DETECTION_SIGMAS = "key_detection_sigmas"; // "@string/sigmas" decimal

    // Preprocessing Recognition
    public static final String KEY_STANDARD_PRE = "key_standard_pre"; // "@array/standard_pre_default"/>
    public static final String KEY_BRIGHTNESS = "key_brightness"; // "@array/brightness_default"/>
    public static final String KEY_COUNTOURS = "key_contours"; // "@array/empty_array"/>
    public static final String KEY_CONTRAST = "key_contrast"; // "@array/contrast_default"/>
    public static final String KEY_STANDARD_POST = "key_standard_post"; // "@array/standard_post_default"/>
    public static final String KEY_GAMMA = "key_gamma"; // "@string/gamma" decimal
    public static final String KEY_SIGMAS = "key_sigmas"; // "@string/sigmas" decimal
    public static final String KEY_N = "key_N"; // int 25

    // Recognition Algorithms
    public static final String KEY_CLASSIFICATION_METHOD = "key_classification_method"; // "@string/imageReshaping"
    public static final String KEY_CLASSIFICATION_METHOD_TFCAFFE = "key_classificationMethodTFCaffe"; // true (@string svm)
    public static final String KEY_K = "key_K"; // int 20
    public static final String KEY_PCA_THRESHOLD = "key_pca_threshold"; // 0.98 decimal
    public static final String KEY_SVMTRAINOPTIONS = "key_svmTrainOptions"; // "-t 0 "

    public LibraryPreference(Context context) {
        this._context = context;
        libPref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = libPref.edit();
    }



    // Create Camera View Preferences in general
    public void createCameraSettings() {

        // General
        editor.putString(KEY_FRONT_CAMERA, "true");
        editor.putString(KEY_NIGHT_PORTRAIT_MODE, "false");
        editor.putString(KEY_EXPOSURE, "50");
        editor.putString(KEY_CAMERA_VIEW_WIDTH, "320");
        editor.putString(KEY_CAMERA_VIEW_HEIGHT, "240");
        editor.putString(KEY_NUMBER_OF_PICTURES, "10");
        editor.putString(KEY_TIMERDIFF, "500");
        editor.putString(KEY_FACE_SIZE, "224");

        // Face Detection
        editor.putString(KEY_DETECTION_METHOD, "true");
        editor.putString(KEY_EYE_DETECTION, "false");
        editor.putString(KEY_OPENCV_CASCADE_FILE, "haarcascade_frontalface_alt2");
        editor.putString(KEY_LEFTEYE_CASCADE_FILE, "haarcascade_lefteye_2splits");
        editor.putString(KEY_RIGHTEYE_CASCADE_FILE, "haarcascade_righteye_2splits");
        editor.putString(KEY_SCALE_FACTOR, "1.1");
        editor.putString(KEY_MIN_NEIGHBORS, "3");
        editor.putString(KEY_FLAGS, "2");

        // Preprocessing Detection
        editor.putString(KEY_DETECTION_STANDARD_PRE, "1. Grayscale");
        editor.putString(KEY_DETECTION_BRIGHTNESS, null);
        editor.putString(KEY_DETECTION_COUNTOURS, null);
        editor.putString(KEY_DETECTION_CONTRAST, null);
        editor.putString(KEY_DETECTION_GAMMA, "0.2");
        editor.putString(KEY_DETECTION_SIGMAS, "0.25, 4");

        // Preprocessing Recognition
        Set<String> standardPreprocessing = new HashSet<>();
        standardPreprocessing.add("1. Grayscale");
        standardPreprocessing.add("2. Eye Alignment");

        Set<String> standardPostProcessing = new HashSet<>();
        standardPostProcessing.add("1. Resize");

        Set<String> brightnessDefault = new HashSet<>();
        brightnessDefault.add("1. Gamma Correction");

        Set<String> contrastDefault = new HashSet<>();
        contrastDefault.add("1. Histogramm Equalization");

        editor.putStringSet(KEY_STANDARD_PRE, standardPreprocessing);
        editor.putStringSet(KEY_BRIGHTNESS, brightnessDefault);
        editor.putStringSet(KEY_COUNTOURS, null);
        editor.putStringSet(KEY_CONTRAST, contrastDefault);
        editor.putStringSet(KEY_STANDARD_POST, standardPostProcessing);
        editor.putString(KEY_GAMMA, "0.2");
        editor.putString(KEY_SIGMAS, "0.25, 4");
        editor.putString(KEY_N, "25");

        // Recognition Algorithms
//        editor.putString(KEY_CLASSIFICATION_METHOD, "Image Reshaping with SVM");
        editor.putString(KEY_CLASSIFICATION_METHOD, "Image Reshaping with SVM");
        editor.putString(KEY_CLASSIFICATION_METHOD_TFCAFFE, "Support Vector Machine");
        editor.putString(KEY_K, "20");
        editor.putString(KEY_PCA_THRESHOLD, "0.98");
        editor.putString(KEY_SVMTRAINOPTIONS, "-t 0 ");

        editor.commit();
    }

    public HashMap<String, String> getCameraSettings() {
        HashMap<String, String> camera = new HashMap<>();

        camera.put(KEY_FRONT_CAMERA, libPref.getString(KEY_FRONT_CAMERA, null));
        camera.put(KEY_NIGHT_PORTRAIT_MODE, libPref.getString(KEY_NIGHT_PORTRAIT_MODE, null));
        camera.put(KEY_EXPOSURE, libPref.getString(KEY_EXPOSURE, null));
        camera.put(KEY_CAMERA_VIEW_HEIGHT, libPref.getString(KEY_CAMERA_VIEW_HEIGHT, null));
        camera.put(KEY_CAMERA_VIEW_WIDTH, libPref.getString(KEY_CAMERA_VIEW_WIDTH, null));
        camera.put(KEY_NUMBER_OF_PICTURES, libPref.getString(KEY_NUMBER_OF_PICTURES, null));
        camera.put(KEY_TIMERDIFF, libPref.getString(KEY_TIMERDIFF, null));
        camera.put(KEY_FACE_SIZE, libPref.getString(KEY_FACE_SIZE, null));

        camera.put(KEY_CLASSIFICATION_METHOD, libPref.getString(KEY_CLASSIFICATION_METHOD, null));
        return camera;
    }

    public HashMap<String, Set<String>> getPreprocessingSettings() {
        HashMap<String, Set<String>> pre = new HashMap<>();

        pre.put(KEY_STANDARD_PRE, libPref.getStringSet(KEY_STANDARD_PRE, null));

        return pre;
    }
}
