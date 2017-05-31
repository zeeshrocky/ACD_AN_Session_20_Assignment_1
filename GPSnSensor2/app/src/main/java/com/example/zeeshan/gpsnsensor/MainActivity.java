package com.example.zeeshan.gpsnsensor;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mySensorManager;
    private float curX, curY, curZ;
    private float lastX, lastY, lastZ;

    private boolean firstUpdate = true;

    private final float shakeThreshold = 1.5f;

    private boolean shakeInitiated = false;


    EditText etMobNo;
    CheckBox chkStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);

        etMobNo = (EditText) findViewById(R.id.etMobileNo);

        chkStart = (CheckBox) findViewById(R.id.chkBoxStartService);

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            updateAccelParameters(sensorEvent.values[0], sensorEvent.values[1],
                    sensorEvent.values[2]);
            if ((!shakeInitiated) && isAccelarationChanged()) {
                shakeInitiated = true;
            } else if (shakeInitiated && isAccelarationChanged()) {
                executeShakeAction();
            } else if (shakeInitiated && (!isAccelarationChanged())) {
                shakeInitiated = false;
            }


        }
    }

    private void executeShakeAction() {

        if (chkStart.isChecked()) {
            String uri = "tel:" + etMobNo.getText().toString().trim();


            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            //
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
            chkStart.setChecked(false);


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mySensorManager.registerListener(this, mySensorManager.getDefaultSensor
                (Sensor.TYPE_ACCELEROMETER), mySensorManager.SENSOR_DELAY_NORMAL);

    }

    private void updateAccelParameters(float newX, float newY, float newZ) {
        if (firstUpdate) {
            lastX = newX;
            lastY = newY;
            lastZ = newZ;
            firstUpdate = false;
        } else {
            lastX = curX;
            lastY = curY;
            lastZ = curZ;
        }
        curX = newX;
        curY = newY;
        curZ = newZ;
    }

    private boolean isAccelarationChanged() {

        float diffX = Math.abs(lastX - curX);
        float diffY = Math.abs(lastY - curY);
        float diffZ = Math.abs(lastZ - curZ);


        return (diffX > shakeThreshold && diffY > shakeThreshold)
                || (diffX > shakeThreshold && diffZ > shakeThreshold)
                || (diffY > shakeThreshold && diffZ > shakeThreshold);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySensorManager.unregisterListener(this);
    }
}