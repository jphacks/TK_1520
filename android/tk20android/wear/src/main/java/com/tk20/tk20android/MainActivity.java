package com.tk20.tk20android;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import java.util.Locale;

public class MainActivity extends WearableActivity {

  private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
      new SimpleDateFormat("HH:mm", Locale.US);

  private BoxInsetLayout mContainerView;
  private TextView mTextView;

  private GoogleApiClient mClient;

  private SensorEventListener mListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setAmbientEnabled();

    mContainerView = (BoxInsetLayout) findViewById(R.id.container);
    mTextView = (TextView) findViewById(R.id.text);
    mClient = new GoogleApiClient.Builder(this)
        .addApi(Wearable.API)
        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
          // TODO: Do something.
          @Override
          public void onConnected(Bundle bundle) {

          }

          @Override
          public void onConnectionSuspended(int i) {

          }
        })
        .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
          @Override
          public void onConnectionFailed(ConnectionResult connectionResult) {

          }
        })
        .build();

    findViewById(R.id.stopHeartButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendEmergencyCallRequest();
      }
    });

    SensorManager manager = (SensorManager)getSystemService(SENSOR_SERVICE);
    Sensor hSensor = manager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    mListener = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        mTextView.setText("" + event.values[0]);
        // Send notification in emergency case.
        // Wearable.MessageApi.sendMessage(...

      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }
    };
    manager.registerListener(mListener, hSensor, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  public void onEnterAmbient(Bundle ambientDetails) {
    super.onEnterAmbient(ambientDetails);
    updateDisplay();
  }

  @Override
  public void onUpdateAmbient() {
    super.onUpdateAmbient();
    updateDisplay();
  }

  @Override
  public void onExitAmbient() {
    updateDisplay();
    super.onExitAmbient();
  }

  private void updateDisplay() {
    if (isAmbient()) {
      mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
      mTextView.setTextColor(getResources().getColor(android.R.color.white));
    } else {
      mContainerView.setBackground(null);
      mTextView.setTextColor(getResources().getColor(android.R.color.black));
    }
  }

  @Override
  protected void onDestroy() {
    ((SensorManager)getSystemService(SENSOR_SERVICE)).unregisterListener(mListener);
    super.onDestroy();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mClient.disconnect();
  }

  @Override
  protected void onStart() {
    super.onStart();
    mClient.connect();
  }

  private void sendEmergencyCallRequest() {
    new SendECallRequestTask().execute();
  }

  private class SendECallRequestTask extends AsyncTask<Void, Void, Void> {
    final String MESSAGE = "EMERGENCY";
    final String PATH = "/emergency";

    @Override
    protected Void doInBackground(Void... params) {
      for (Node n : Wearable.NodeApi.getConnectedNodes(mClient).await().getNodes()) {
        //TODO : Handle request.
        Wearable.MessageApi.sendMessage(mClient, n.getId(), PATH, MESSAGE.getBytes());
      }
      return null;
    }
  }
}
