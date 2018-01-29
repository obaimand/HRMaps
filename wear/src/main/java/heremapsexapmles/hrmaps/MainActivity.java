package heremapsexapmles.hrmaps;

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

public class MainActivity extends  WearableActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private TextView mTextView;

    private ImageButton btnStart;
    private ImageButton btnPause;
    //private static final int SENSOR_TYPE_HEARTRATE = 33171027;
    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    //Sensor hProximitySensor;
    private TextView mTextViewHeart;

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
        //mHeartRateSensor = mSensorManager.getDefaultSensor(33171027);
        //hProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private void startMeasure() {
        boolean sensorRegistered = mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_UI);
        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
        //logAvailableSensors();
    }
    private void stopMeasure() {
        mSensorManager.unregisterListener(this);


    }

    public void onResume(){
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
       /* float mHeartRateFloat = event.values[0];

        int mHeartRate = Math.round(mHeartRateFloat);
        mTextView.setText(Integer.toString(mHeartRate));*/

        String mHeartRate = "" + (int)event.values[0];
        //String mHeartRateY = ""+ (int)event.values[1];
        mTextView.setText(mHeartRate);

        Log.e(TAG, "HR: "+ mHeartRate);
      /*  String msg = "" + (int)event.values[0];
        mTextViewHeart.setText(msg);
        Log.d(TAG, "HR: "+ mHeartRateFloat);*/
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
    /**
     * Log all available sensors to logcat
     */
   /* private void logAvailableSensors() {
        final List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.v(TAG, "=== LIST AVAILABLE SENSORS ===");
        Log.v(TAG, String.format(Locale.getDefault(), "|%-35s|%-38s|%-6s|", "SensorName", "StringType", "Type"));
        for (Sensor sensor : sensors) {
            Log.v(TAG, String.format(Locale.getDefault(), "|%-35s|%-38s|%-6s|", sensor.getName(), sensor.getStringType(), sensor.getType()));
        }
        Log.v(TAG, "=== LIST AVAILABLE SENSORS ===");
    }*/

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy: " + accuracy);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
