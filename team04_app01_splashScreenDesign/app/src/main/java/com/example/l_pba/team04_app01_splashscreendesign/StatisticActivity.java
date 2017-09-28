package com.example.l_pba.team04_app01_splashscreendesign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class StatisticActivity extends AppCompatActivity {

    private ConstraintLayout cL;
    private WebView webView;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private HashMap<String,LinkedList<LatLng>> map = new HashMap<>();

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
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //getData from shared Preferences
        preferences = getSharedPreferences("GPSFile", Context.MODE_PRIVATE);

        final LinkedList<String> keyArray = new LinkedList<>();
        LinkedList<String> helpArray = new LinkedList<>();

        final JSONObject obj = new JSONObject();

        //store all preferencepoints as Strings
        if(!preferences.getAll().isEmpty()) {
            String[] s = preferences.getAll().keySet().toArray(new String[0]);
            //in String s are all datasetnames

            int x=0;
            //store all datanames in keyArray-List
            for(int i=0; i < s.length; i++) {
                if(s[i].charAt(0) == '#') continue;
                helpArray.add(s[i].replaceAll("[0-9]","")); //remove numbers
                if(!keyArray.contains(helpArray.get(x))) {
                    keyArray.add(helpArray.get(x));
                }
                x++;
            }

            //fill Hashmap with Stringname and LatLng-List
            HashMap<String,String> colorMap = new HashMap<>();

            Map<String,?> helper = new HashMap<>();
            helper = preferences.getAll();
            String name="";
            String val = "";
            for(int j=0; j < s.length; j++) {
                name = s[j].replaceAll("[0-9]", ""); //datasetname
                val = String.valueOf(helper.get(s[j])); //datavalue
                if(name.charAt(0) == '#') continue;; //name is polygon
                if(val.charAt(0)=='#') {    //value is color
                    colorMap.put(name,val);
                }else{
                    //save LatLng as value and datasetname as key in Map
                    if(!map.containsKey(name)) {
                        LinkedList<LatLng> l = new LinkedList<>();
                        l.add(StringtoLatLng(val));
                        map.put(name,l);
                    }else{
                        map.get(name).add(StringtoLatLng(val));
                    }
                }
            }


            JSONArray arr;
            //Create JSONArrays and store in JSONobject
            for (int index = 0; index < keyArray.size(); index++) {
                arr = new JSONArray();
                for (int l = 0; l < map.get(keyArray.get(index)).size(); l++) {
                    try {
                        arr.put(map.get(keyArray.get(index)).get(l).getLongitude());
                        arr.put(map.get(keyArray.get(index)).get(l).getLatitude());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    obj.put(keyArray.get(index), arr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            Toast.makeText(this,R.string.no_stat_data, Toast.LENGTH_LONG).show();
        }
        //set Webview display
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); //enable Javascript
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android"); //Set JavaScriptInterface
        webView.loadUrl(chartUrl);

        //Send data to Javascript
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
               webView.loadUrl("javascript:makeArray('"+obj+"','"+keyArray.size()+"','"+2+"')");
            }
        });

    }

    /**
     * convert Stringconverted LatLng back to LatLng
     * @param s : String
     * @return LatLang
     */
    public LatLng StringtoLatLng(String s) {

        String latstr = "";
        String lngstr = "";

        int c=0;
        int count = 0;


        while(s.charAt(c) != 'a' || s.charAt(c+1) != 'l') {
            if(s.charAt(c) == '=') {
                count++;
                c++;
                while(s.charAt(c) != ',') {
                    if(count == 1) {
                        latstr+=s.charAt(c);
                    }else {
                        lngstr += s.charAt(c);
                    }
                    c++;
                }
            }
            c++;
        }

        double lat = Double.valueOf(latstr);
        double lng = Double.valueOf(lngstr);

        return new LatLng(lat,lng);
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
