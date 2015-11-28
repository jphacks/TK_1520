package com.tk20.tk20android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    WebView wv = (WebView) findViewById(R.id.webView);
    wv.getSettings().setJavaScriptEnabled(true);
    wv.loadUrl("http://hamanishi.orz.hm/");
  }
}
