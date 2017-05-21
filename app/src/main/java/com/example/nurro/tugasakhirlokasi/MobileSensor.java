package com.example.nurro.tugasakhirlokasi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by nurro on 3/26/2017.
 */

public class MobileSensor implements SensorEventListener {
    private Activity activity;
    private double deltaX, deltaY, deltaZ;
    private SensorManager sensorManager;
    private Sensor proximity, accelerometer;
    private double[][] data;
    private ArrayList<String> testData;
    private boolean checkProximity, checkAccelerometer;
    private Button button;

    public MobileSensor(Activity activity, Button button){
        this.activity = activity;
        this.button = button;
        this.deltaX = 0;
        this.deltaY = 0;
        this.deltaZ = 0;
        this.data = new double[2000][5];
        this.testData = new ArrayList<>();
        this.checkAccelerometer = false;
        this.checkProximity = false;
        ActivityCompat.requestPermissions(this.activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        sensorManager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!=null)
        {
            this.proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(this,this.proximity,sensorManager.SENSOR_DELAY_NORMAL);

        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(1);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
