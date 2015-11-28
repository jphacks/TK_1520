package com.tk20.tk20android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.tk20.tk20android.R;

public class MainActivity extends Activity {
  private static final String ASSET_HTML_PATH = "";

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
  }
}
