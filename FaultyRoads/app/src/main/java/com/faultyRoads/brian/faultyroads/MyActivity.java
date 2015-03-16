package com.faultyRoads.brian.faultyroads;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class MyActivity extends Activity implements SensorEventListener {

    private float mLastY;
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 6.0;
    Firebase myFireBaseReference;
    GPSTracker gps;
    private static double latitude;
    private static double longitude;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Firebase.setAndroidContext(this);
        myFireBaseReference = new Firebase("https://faulty-roads.firebaseio.com/");
        gps = new GPSTracker(this);
        gps.getLocation();
        if(!gps.canGetLocation()){
            gps.showSettingsAlert();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        TextView tvY= (TextView)findViewById(R.id.y_axis);
        ImageView iv = (ImageView)findViewById(R.id.image);
        TextView lon = (TextView)findViewById(R.id.lo);
        TextView lat = (TextView)findViewById(R.id.lat);
        float y = event.values[2];
        if (!mInitialized) {
            mLastY = y;
            tvY.setText("0.0");
            mInitialized = true;
        } else {
            float deltaY = Math.abs(mLastY - y);
            if (deltaY < NOISE)
            {
                deltaY = (float)0.0;
            }
            mLastY = y;

            if (deltaY > 3) {
                iv.setImageResource(R.drawable.redcircle);


                //gps.updateGPSCoordinates();

                latitude = gps.getLatitude(latitude);
                longitude = gps.getLongitude(longitude);
                lat.setText(Double.toString(latitude));
                lon.setText(Double.toString(longitude));

                Firebase postRef;
                postRef = myFireBaseReference.push();
                postRef.child("latitude").setValue(latitude);
                postRef.child("longitude").setValue(longitude);

                tvY.setText(Float.toString(y));

                iv.setVisibility(View.VISIBLE);

            }
            else {
                iv.setVisibility(View.INVISIBLE);
            }
        }
    }
}