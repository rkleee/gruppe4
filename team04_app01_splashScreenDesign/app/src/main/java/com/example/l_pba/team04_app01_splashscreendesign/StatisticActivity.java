/**
 * @author group04
 * @version 1.0
 * StatisticActivity, displays statistics
 */
package com.example.l_pba.team04_app01_splashscreendesign;

/**
 * Android Imports
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * MapBox Imports
 */
import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * JSON Imports
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Java util Imports
 */
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * StatisticActivity to display the Statistics of the App
 */
public class StatisticActivity extends AppCompatActivity {

    /**Declarate xml-Reading**/
    private ConstraintLayout cL;
    private WebView webView;
    private ListView listView;
    private Button secretButton;

    /**Declarate SharedPreferences**/
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    /**Declarate storing map**/
    private HashMap<String,LinkedList<LatLng>> map = new HashMap<>();

    //Set index.html as chartURL
    String chartUrl = "file:///android_asset/index.html";

    //set Background color
    private int statisticColor = Color.parseColor("#eeeeee"); //grey

    private boolean pressed = false;

    @Override
    /**
     * @param savedInstanceState : Bundle
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        //set the Background
        cL = (ConstraintLayout) findViewById(R.id.constraintLayout);
        cL.setBackgroundColor(statisticColor);

        //getData from shared Preferences
        preferences = getSharedPreferences("GPSFile", Context.MODE_PRIVATE);

        /******************THIS IS LAYOUT01***************************/

        //Initialize needed Lists
        final LinkedList<String> keyArray = new LinkedList<>();
        LinkedList<String> helpArray = new LinkedList<>();

        //Initialize needed Maps
        HashMap<String,String> colorMap = new HashMap<>();
        Map<String,?> helper;

        //Initialize needed JSON-Object and JSON-Array
        final JSONObject obj = new JSONObject();
        JSONArray arr;

        //store all preferencepoints as Strings
        if(!preferences.getAll().isEmpty()) {
            String[] s = preferences.getAll().keySet().toArray(new String[0]);
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
            //Display when no data is found
            Toast.makeText(this,R.string.no_stat_data, Toast.LENGTH_LONG).show();
        }
        //set Webview display
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); //enable Javascript
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android"); //Set JavaScriptInterface
        webView.loadUrl(chartUrl);


        secretButton = (Button) findViewById(R.id.secretbutton);
        secretButton.getBackground().setColorFilter(0x00000000, PorterDuff.Mode.MULTIPLY); //Set Button Backgroundcolor transparent

        secretButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pressed) {
                    webView.loadUrl("javascript:secretfunction('"+obj+"','"+keyArray.size()+"','"+2+"')");
                }else pressed = false;

            }
        });

        //Send data to Javascript
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
               webView.loadUrl("javascript:makeArray('"+obj+"','"+keyArray.size()+"','"+2+"')");
            }
        });


        /******************THIS IS SLIDINGUP-LAYOUT***************************/

        //Declarate needed Map, List and Array
        HashMap<String,Double> distance = new HashMap<>();
        String[] arr2 = new String[keyArray.size()];
        LinkedList<LatLng> ll;

        double temp = 0; //temporary route calculate

        //Calculate distance from routes
        for(int ind = 0; ind < keyArray.size(); ind++) {
            ll = map.get(keyArray.get(ind));
            for(int ind2 =0; ind2 < ll.size()-1; ind2++) {
                temp += calculate(ll.get(ind2),ll.get(ind2+1));
            }
            distance.put(keyArray.get(ind),temp);
            temp=0;
        }

        //Build String to display on Information-Screen
        String s;
        for(int index = 0; index < keyArray.size(); index++) {
            s = "Route '" + keyArray.get(index) +"': Distance in: \n -> metres: "
                    + distance.get(keyArray.get(index))*1000 +"\n -> kilometres: "
                    + distance.get(keyArray.get(index));

            arr2[index] = s;
        }

        //Fill ListView with data
        listView = (ListView) findViewById(R.id.listviewst);
        listView.setScrollContainer(true); //make ListView scrollable

        ListAdapter adapter = new ArrayAdapter<String>(this,R.layout.textview2,arr2);
        listView.setAdapter(adapter);

    }

    /**
     * calculate distance between two LatLng and return as double value
     * @param l1 : LatLng
     * @param l2 : LatLng
     * @return double
     */
    public double calculate(LatLng l1, LatLng l2) {
        double lat1=l1.getLatitude();
        double lat2=l2.getLatitude();
        double lon1=l1.getLongitude();
        double lon2=l2.getLongitude();

        double lat = ((lat1+lat2)/2)*0.01745;
        double dx = 111.3*Math.cos(lat)*(lon1-lon2);
        double dy = 111.3*(lat1-lat2);

        return Math.sqrt(dx*dx+dy*dy);
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

    /**
     * onBackPressed to return to HomeActivity
     */
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
