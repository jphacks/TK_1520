package com.tk20.tk20android.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.tk20.tk20android.R;

public class MainActivity extends Activity {
  private static final String ASSET_HTML_PATH = "";
  private MessageApi.MessageListener mListener;
  private GoogleApiClient mClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final WebView wv = (WebView) findViewById(R.id.webView);
    wv.getSettings().setJavaScriptEnabled(true);
    wv.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
      }
    });
    wv.loadUrl("file:///android_asset/www/html/index.html");
    final EditText et = (EditText) findViewById(R.id.ipEdit);

    findViewById(R.id.ipButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        wv.loadUrl("javascript:setIp(\"" + et.getText().toString() + "\");");
      }
    });

    mClient = new GoogleApiClient.Builder(this)
        .addApi(Wearable.API)
        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
          //TODO: Do something.
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

    mListener  = new MessageApi.MessageListener() {
      @Override
      public void onMessageReceived(MessageEvent messageEvent) {
        Toast.makeText(MainActivity.this, new String(messageEvent.getData()),Toast.LENGTH_LONG)
            .show();
      }
    };

    Wearable.MessageApi.addListener(mClient, mListener);
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Wearable.MessageApi.removeListener(mClient, mListener);
  }
}
