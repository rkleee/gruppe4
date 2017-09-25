package com.example.l_pba.team04_app01_splashscreendesign;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Toast;

public class GraphicActivity extends AppCompatActivity {

    private WebView view;

    String chartUrl = "file:///android_asset/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);

        //get webview in layout first
        Toast.makeText(this, "GraphicActivity started.", Toast.LENGTH_SHORT).show();

        view = (WebView) findViewById(R.id.webView);
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(chartUrl);

    }
}
