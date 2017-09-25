package com.example.l_pba.team04_app01_splashscreendesign;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StatisticActivity extends AppCompatActivity {

    private ConstraintLayout cL;
    private WebView webView;

    //Set index.html as chartURL
    String chartUrl = "file:///android_asset/index.html";

    //set Background color
    private int statisticColor = Color.parseColor("#eeeeee"); //grey

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        //set the Background
        cL = (ConstraintLayout) findViewById(R.id.constraintLayout);
        cL.setBackgroundColor(statisticColor);

        //set ScreenOrientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Testargument to send to javascript as JSON-Object
        JSONArray jarr1 = new JSONArray();
        JSONArray jarr2 = new JSONArray();
        final JSONObject obj = new JSONObject();
        try {
            jarr1.put(10);
            jarr1.put(30);
            jarr1.put(50);
            jarr1.put(70);
            jarr1.put(90);
            jarr1.put(110);
            obj.put("data1",jarr1);


            jarr2.put(110);
            jarr2.put(90);
            jarr2.put(70);
            jarr2.put(50);
            jarr2.put(30);
            jarr2.put(10);
            obj.put("data2",jarr2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //set Webview display
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); //enable Javascript
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android"); //Set JavaScriptInterface
        webView.loadUrl(chartUrl);

        //Send data to Javascript
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
               // webView.loadUrl("javascript:setx('"+1+"')");
                webView.loadUrl("javascript:makeArray('"+obj+"','"+1+"')");
            }
        });

    }

    @Override
    public void onBackPressed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent backIntent = new Intent(StatisticActivity.this,HomeActivity.class);
                startActivity(backIntent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        },50);
    }
}
