package id.ac.its.sikemastc.activity.verifikasi_wajah;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Pattern;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.Helpers.PreferencesHelper;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition;
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory;
import id.ac.its.sikemastc.R;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class TrainingWajah extends AppCompatActivity {

    private static final String TAG = "Training";
    private SharedPreferences flagStatus;
    private String idPerkuliahan;

    TextView progress;
    Thread thread;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_face_training);
        flagStatus = getSharedPreferences("flag_status", 0);

        Intent intent = getIntent();
        idPerkuliahan = intent.getStringExtra("id_perkuliahan");

        progress = (TextView) findViewById(R.id.tv_training_progress);
        progress.setMovementMethod(new ScrollingMovementMethod());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Training Data Set");
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
    }

    @Override
    public void onResume() {
        super.onResume();

        final Handler handler = new Handler(Looper.getMainLooper());
        thread = new Thread(new Runnable() {
            public void run() {
                if (!Thread.currentThread().isInterrupted()) {
                    Log.d("cek masuk handler", "masuk handler");
                    PreProcessorFactory ppF = new PreProcessorFactory(getApplicationContext());
                    PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
                    String algorithm = preferencesHelper.getClassificationMethod();
                    try {
                        int counter = 1;
                        FileHelper fileHelper = new FileHelper();
                        fileHelper.createDataFolderIfNotExsiting();
                        final File[] persons = fileHelper.getTrainingList();
                        AssetManager assetManager = getAssets();
                        final String[] assetFiles = assetManager.list("DataSetWajah");

                        if (persons.length > 0 && assetFiles.length > 0) {
                            Recognition rec = RecognitionFactory.getRecognitionAlgorithm(getApplicationContext(),
                                    Recognition.TRAINING, algorithm);

                            for (int i = 0; i < assetFiles.length; i++) {
                                InputStream inputStream;
                                OutputStream outputStream;
                                // String[] splitBy = assetFiles[i].split(Pattern.quote("."));
                                inputStream = assetManager.open("DataSetWajah/" + assetFiles[i]);
                                File outputFile = new File(FileHelper.TRAINING_PATH + "/" + "processedDataSet.png");
                                outputStream = new FileOutputStream(outputFile);
                                int read = 0;
                                byte[] bytes = new byte[1024];
                                while ((read = inputStream.read(bytes)) != -1) {
                                    outputStream.write(bytes, 0, read);
                                }
                                outputStream.close();
                                if (FileHelper.isFileAnImage(outputFile)) {
                                    Mat imgRgb = imread(outputFile.getAbsolutePath());
                                    Imgproc.cvtColor(imgRgb, imgRgb, Imgproc.COLOR_BGRA2RGBA);
                                    Mat processedDataSetImage = new Mat();
                                    imgRgb.copyTo(processedDataSetImage);
                                    List<Mat> images = ppF.getProcessedImage(processedDataSetImage,
                                            PreProcessorFactory.PreprocessingMode.RECOGNITION);
                                    if (images == null || images.size() > 1) {
                                        // More than 1 face detected --> cannot use this file for training
                                        continue;
                                    } else {
                                        processedDataSetImage = images.get(0);
                                    }
                                    if (processedDataSetImage.empty()) {
                                        continue;
                                    }

                                    MatName m = new MatName("processedImage", processedDataSetImage);
                                    fileHelper.saveMatToImage(m, FileHelper.DATA_PATH);

                                    rec.addImage(processedDataSetImage, outputFile.getParent(), false);

                                    // Update screen to show the progress
                                    final int counterPost = counter;
                                    final int filesLength = assetFiles.length;
                                    progress.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.append("Image " + counterPost + " of " + filesLength + " from " + "asset files" + " imported.\n");
                                        }
                                    });
                                    counter++;
                                }
                            }
                            for (File person : persons) {
                                if (person.isDirectory()) {
                                    File[] files = person.listFiles();
                                    for (File file : files) {
                                        if (FileHelper.isFileAnImage(file)) {
                                            Mat imgRgb = Imgcodecs.imread(file.getAbsolutePath());
                                            Imgproc.cvtColor(imgRgb, imgRgb, Imgproc.COLOR_BGRA2RGBA);
                                            Mat processedImage = new Mat();
                                            imgRgb.copyTo(processedImage);
                                            List<Mat> images = ppF.getProcessedImage(processedImage, PreProcessorFactory.PreprocessingMode.RECOGNITION);
                                            if (images == null || images.size() > 1) {
                                                // More than 1 face detected --> cannot use this file for training
                                                continue;
                                            } else {
                                                processedImage = images.get(0);
                                            }
                                            if (processedImage.empty()) {
                                                continue;
                                            }
                                            // The last token is the name --> Folder name = Person name
                                            String[] tokens = file.getParent().split("/");
                                            final String name = tokens[tokens.length - 1];

                                            MatName m = new MatName("processedImage", processedImage);
                                            fileHelper.saveMatToImage(m, FileHelper.DATA_PATH);

                                            rec.addImage(processedImage, name, false);

//                                      fileHelper.saveCroppedImage(imgRgb, ppF, file, name, counter);

                                            // Update screen to show the progress
                                            final int counterPost = counter;
                                            final int filesLength = files.length;
                                            progress.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progress.append("Image " + counterPost + " of " + filesLength + " from " + name + " imported.\n");
                                                }
                                            });
                                            counter++;
                                        }
                                    }
                                }
                            }
                            final Intent intent = new Intent(getApplicationContext(), MenuVerifikasiWajah.class);
                            intent.putExtra("id_perkuliahan", idPerkuliahan);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            if (rec.train()) {
                                intent.putExtra("training", "Training successful");
                                flagStatus.edit().putBoolean("training_flag", true).apply();
                                flagStatus.edit().putBoolean("upload_flag", true).apply();
                            } else {
                                intent.putExtra("training", "Training failed");
                                flagStatus.edit().putBoolean("training_flag", false).apply();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            Thread.currentThread().interrupt();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        thread.interrupt();
    }
}
