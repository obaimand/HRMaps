package heremapsexapmles.watchr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;



public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private TextView mTextView;
    private ImageButton btnStart;
    private ImageButton btnPause;
    //private static final int SENSOR_TYPE_HEARTRATE = 65562;
    SensorManager mSensorManager;
    Sensor mHeartRateSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.heartRateText);
        btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnPause = (ImageButton) findViewById(R.id.btnPause);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setVisibility(ImageButton.GONE);
                btnPause.setVisibility(ImageButton.VISIBLE);
                mTextView.setText("Please wait...");
                startMeasure();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPause.setVisibility(ImageButton.GONE);
                btnStart.setVisibility(ImageButton.VISIBLE);
                mTextView.setText("--");
                stopMeasure();
            }
        });
        /*mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i(TAG, "LISTENER REGISTERED.");
        mTextView.setText("HR:--");
        mSensorManager.registerListener(this, mHeartRateSensor, mSensorManager.SENSOR_DELAY_FASTEST);*/


        // Enables Always-on
        setAmbientEnabled();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
       // mHeartRateSensor = mSensorManager.getDefaultSensor(65562);
    }

    private void startMeasure() {
        boolean sensorRegistered = mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
    }
    private void stopMeasure() {
        mSensorManager.unregisterListener(this);
    }

    /*public void onResume(){
        super.onResume();
    }*/

    @Override
    public void onSensorChanged(SensorEvent event) {
        float mHeartRateFloat = event.values[0];

        int mHeartRate = Math.round(mHeartRateFloat);

        mTextView.setText(Integer.toString(mHeartRate));
    }


   /* @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "..." + (int)event.values[0];
            mTextView.setText(msg);
            Log.d(TAG, msg);
        }
        else
            Log.d(TAG, "Unknown sensor type");
    }*/



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
