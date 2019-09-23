package com.arifcemal.sensorapp;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private SensorManager mSensorManager;
    private TextToSpeech mTts;
    private Sensor mProximity;
    private Sensor magnetic;
    private String mesaj = "";
    private StringBuilder msg = new StringBuilder(2048);

    @Override
    public void onInit(int arg0) {
        mesaj = "Merhaba, bu uygulama cihazınızın ekranı herhangi bir cisme yaklaştığında veya ondan uzaklaştığında sesli uyarılar verir. Ayrıca bu cisim demir gibi bir metal ise cihazınız titrer. Uyarı: Telefon ekranını yere doğru çevirirseniz manyetik alan sensörü yerin manyetik alanından etkileneceğinden cihazınız devamlı olarak titrer.";
        speakOut(mesaj);
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        /*Sensors*/
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /*Sensor Events*/
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_PROXIMITY) {

            if (mProximity == null) {
                speakOut("Cihazınızda yakınlık sensörü bulunamadı.");
            } else {
                if (event.values[0] == 0) {
                    mesaj = "Yakın";
                } else if (event.values[0] == mProximity.getMaximumRange()) {
                    mesaj = "Uzak";
                }
                speakOut(mesaj);
                mesaj = "";
            }
        } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            if (magnetic == null) {
                speakOut("Cihazınızda manyetik alan sensörü bulunamadı.");
            } else if (event.values[2] > 20) {
                Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mVibrator.vibrate(100);
            }

        }
    }

    public final void onCreate(Bundle savedInstanceState) {
        mTts = new TextToSpeech(this, this);
        setContentView(R.layout.activity_list_item);
        super.onCreate(savedInstanceState);
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /*Speaking function */
    private void speakOut(String text) {
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}