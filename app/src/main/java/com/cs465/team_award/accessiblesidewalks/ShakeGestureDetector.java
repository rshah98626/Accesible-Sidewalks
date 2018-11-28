package com.cs465.team_award.accessiblesidewalks;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Calendar;


public class ShakeGestureDetector implements SensorEventListener {
    // This is the min force to consider as a shake
    private static double SHAKE_MIN_THRESHOLD = 12.0;

    // Number of total "shakes" that must be detected for the gesture
    private static int SHAKE_NUMBER_OF_STATES = 8;

    // The time window in milliseconds in which the "shakes" must occur
    private static int SHAKE_MIN_TIME = 1000;

    private static String TAG = "DEBUGGING";




    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mAccelSensor;
    private int state;
    private long startTime;
    private Logic logic;


public ShakeGestureDetector(Context context) {

    logic = Logic.getInstance();

    mContext = context;
    mSensorManager = null;
    mAccelSensor = null;

    // Get reference to the accelerometer
    mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    if (mSensorManager != null) {
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        state = 0;
        startTime = 0;

        }

protected void registerSensorListeners() {
        if (mSensorManager != null) {
        mSensorManager.registerListener(this, mAccelSensor, SensorManager.SENSOR_DELAY_UI);
        }
        }

protected void unRegisterSensorListeners() {
        if (mSensorManager != null) {
        mSensorManager.unregisterListener(this);
        }
        }

public void onSensorChanged(SensorEvent event) {
        // Implement the state machine shown in the lecture notes
        double length = getVectorLength(event.values);
        if (state == 0) {
        // is there sufficient force to count
        if (length > SHAKE_MIN_THRESHOLD) {
        ++state;
        startTime = Calendar.getInstance().getTimeInMillis();
        } else {
        state = 0;
        }

        // there was sufficient force to detect a shake gesture
        } else if (state >= SHAKE_NUMBER_OF_STATES) {
        onShakeDetected();
        state = 0;

        // continue to track length of motion
        } else if (state > 0) {
        if ((Calendar.getInstance().getTimeInMillis() - startTime) > SHAKE_MIN_TIME) {
        // it has taken too long, reset
        state = 0;
        } else if (length > SHAKE_MIN_THRESHOLD) {
        // otherwise, check if sufficient force is present to advance
        ++state;
        //TODO: check the number of required shakes
        }
        }
        }

    private void onShakeDetected() {
        logic.getSync().updateChange();
    }

private double getVectorLength(float [] vector) {
        return Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2]);
        }


// Must implement for interface but not needed
public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        }
