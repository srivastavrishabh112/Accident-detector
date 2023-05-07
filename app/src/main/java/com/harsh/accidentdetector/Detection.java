package com.harsh.accidentdetector;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Detection extends AppCompatActivity implements SensorEventListener {


    TextView first;
    ImageView imageView;
    Sensor accelerometer;
    int x, y, z;
    float[] accel_read;

    private SensorManager sens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);


        first = findViewById(R.id.first);


        imageView = findViewById(R.id.imageView);

        sens = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sens.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


    }


    protected void onResume() {

        super.onResume();
        sens.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

    }

    protected void onPause() {

        super.onPause();
        sens.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() ==
                Sensor.TYPE_ACCELEROMETER)
            accel_read = event.values;

        checkaccident();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void checkaccident() {


        x = (int) accel_read[0];
        y = (int) accel_read[1];
        z = (int) accel_read[2];


        int accelationSquareRoot = (int) ((x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH));

        if (accelationSquareRoot >= 27) {


            Intent intent = new Intent(Detection.this, Detected.class);
            startActivity(intent);


        }


    }


}