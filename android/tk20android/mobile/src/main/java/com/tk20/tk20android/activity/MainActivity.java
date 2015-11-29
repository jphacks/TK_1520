package com.tk20.tk20android.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.tk20.tk20android.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {
  private static final String ASSET_HTML_PATH = "file:///android_asset/www/html/index.html";

  private String mHostName = "192.51.208.64";
  private int ENDPOINT_PORT = 8081;

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
    wv.loadUrl(ASSET_HTML_PATH);
    final EditText et = (EditText) findViewById(R.id.ipEdit);

    findViewById(R.id.ipButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        wv.loadUrl("javascript:setIp(\"" + et.getText().toString() + "\");");
        mHostName = et.getText().toString();
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

        new AsyncTask<Void, Void, Void>() {
          String TAG = "ApiRequestTask";

          @Override
          protected Void doInBackground(Void... params) {
            HttpURLConnection connection = null;
            InputStream is = null;
            String url = getString(R.string.endpoint, mHostName, ENDPOINT_PORT);
            try {
              connection = (HttpURLConnection) new URL(url).openConnection();
              connection.setRequestMethod("GET");
              connection.connect();
              is = connection.getInputStream();
            } catch (IOException e) {
              e.printStackTrace();
            } finally {
              try {
                if (is != null) {
                  is.close();
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
              if (connection != null) {
                connection.disconnect();
              }
            }
            return null;
          }
        }.execute();
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
