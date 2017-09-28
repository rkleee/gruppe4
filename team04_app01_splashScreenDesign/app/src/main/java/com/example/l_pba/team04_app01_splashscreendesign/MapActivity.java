/**
 * @author group04
 * @version 1.0
 * MapActivity for displaying the map
 */
package com.example.l_pba.team04_app01_splashscreendesign;

/**
 * Android Imports
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * MapBox Imports
 */
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

/**
 * Util Imports
 */
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


//MAIN
/**
 * MainActivity for the Map
 */
public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    private Button saveButton, playButton;
    private EditText mName;
    private CheckBox mRoute;
    private CheckBox mPolygon;
    private Button mSave;
    private Button mCancel;
    /**
     * for GPS position
     */
    public LocationManager lManager;
    public LocationListener lListener;

    /**
     * save with SharedPreferences
     */
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    /**
     * drawing parameters
     */
    private LatLng[] points = new LatLng[2]; //Array of two points for route draw
    private LinkedList<LinkedList<LatLng>> allRoutes = new LinkedList<>();      //Collection of all Routes
    private LinkedList<LinkedList<LatLng>> allPolygons = new LinkedList<>();    //Collection of all Polygons
    private LinkedList<LatLng> polyPoints = new LinkedList<>(); //List of all points for polygon draw
    private LinkedList<LatLng> routePoints = new LinkedList<>(); //List of all points

    /**
     * setting parameters
     */
    static int updateTime; //in ms
    static int polyCountPoints; // = 25000/updateTime
    static String lineColor; //color of the route
    static String polygonColor; //color of the polygon

    /**
     * const parameters
     */
    final static int cameraSmooth = 1000; //in ms
    final static int lineWidth = 5; //for draw route
    final static double offset = 0.00005; //inaccuracy for polygon calculating (0.00005 = 3.5m)
    private static final String TAG = MapActivity.class.getName(); //Tagging for Logging

    //Map with Styles
    private static final Map<String, String> styles;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("Streets", Style.MAPBOX_STREETS);
        aMap.put("Satellite", Style.SATELLITE);
        aMap.put("Dark", Style.DARK);
        aMap.put("Light", Style.LIGHT);
        aMap.put("Outdoor", Style.OUTDOORS);
        styles = Collections.unmodifiableMap(aMap);
    }

    //Map with Route Colors
    private static final Map<String, String> routeColors;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("black",       "#ff000000");
        aMap.put("red",         "#ffff0000");
        aMap.put("green",       "#ff00ff00");
        aMap.put("blue",        "#ff0000ff");
        aMap.put("yellow",      "#ffffff00");
        aMap.put("magenta",     "#ffff00ff");
        aMap.put("cyan",        "#ff00ffff");
        aMap.put("white",       "#ffffffff");
        aMap.put("grey",        "#ff7f7f7f");
        aMap.put("lightgrey",   "#ffd3d3d3");
        aMap.put("silver",      "#ffc0c0c0");
        routeColors = Collections.unmodifiableMap(aMap);
    }

    //Map with Poly Colors
    private static final Map<String, String> polygonColors;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("black",       "#7f000000");
        aMap.put("red",         "#7fff0000");
        aMap.put("green",       "#7f00ff00");
        aMap.put("blue",        "#7f0000ff");
        aMap.put("yellow",      "#7fffff00");
        aMap.put("magenta",     "#7fff00ff");
        aMap.put("cyan",        "#7f00ffff");
        aMap.put("white",       "#7fffffff");
        aMap.put("grey",        "#7f7f7f7f");
        aMap.put("lightgrey",   "#7fd3d3d3");
        aMap.put("silver",      "#7fc0c0c0");
        polygonColors = Collections.unmodifiableMap(aMap);
    }

    /**
     * other parameters
     */
    private boolean gpsPlay = false;


    @Override
    /**
     * @param savedInstanceState Bundle
     */
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * Android Shit
         */
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidGVhbTA0IiwiYSI6ImNqN3B5cTVxdTNqMnIzM3FwYmhoe" +
                "WczczgifQ._hVIhv2V3zLw8TNOCeLc7A");
        setContentView(R.layout.mapactivity);

        /**
         * Initialize a mapView
         */
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);


        /**
         * Loading Settings
         */
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //updateTime and polyCountPoints
        String prefPingKey = getString(R.string.preference_ping_key);
        String prefPingDefault = getString(R.string.preference_ping_default);
        String ping = sPrefs.getString(prefPingKey,prefPingDefault);
        updateTime = Integer.parseInt(ping);
        polyCountPoints = 25000/updateTime;
        //Style
        String prefStyleKey = getString(R.string.preference_style_key);
        String prefStyleDefault = getString(R.string.preference_style_default);
        String style = sPrefs.getString(prefStyleKey,prefStyleDefault);
        mapView.setStyleUrl(styles.get(style));
        //Route Color
        String prefRouteColorKey = getString(R.string.preference_route_color_key);
        String prefRouteColorDefault = getString(R.string.preference_route_color_default);
        lineColor = sPrefs.getString(prefRouteColorKey,prefRouteColorDefault);
        //Polygon Color
        String prefPolygonColorKey = getString(R.string.preference_polygon_color_key);
        String prefPolygonColorDefault = getString(R.string.preference_polygon_color_default);
        polygonColor = sPrefs.getString(prefPolygonColorKey,prefPolygonColorDefault);

        /**
         * set preferences
         */
        preferences = getSharedPreferences("GPSFile", Context.MODE_PRIVATE);
        editor = preferences.edit();
        /**
         * loading Routes/Polygons
         */
        //über alle keys sollen nun die latlng ausgelesen und in allRoute/allPolygon abgespeichert werden
        //Die DataActivity übergibt ein String[], indem die namen aller zu ladenden routen steht
        //HomeActivity übergibt dann ein leeres StringArray, sd. keine Route geladen wird

        //  Polygon: #name0 = ff... ; #name1 = LatLng ; ...LatLng
        //  Route:   name0 = ff.... ; ....


        String[] loading = getIntent().getExtras().getStringArray("Items");
        String[] keyArray = preferences.getAll().keySet().toArray(new String[0]);
        LinkedList<LatLng> helpList = new LinkedList<>();

        Arrays.sort(keyArray);
        boolean polygon = false;

        for (String s : loading){
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            String color = "";
            for (int i=0; i<keyArray.length; i++){
                String raw = preferences.getString(keyArray[i],"");
                if (keyArray[i].replaceAll("[0-9]","").replace("#","").equals(s)){
                    if (raw.contains(",")){
                        String[] splitString = raw.split(",");
                        polygon = keyArray[i].startsWith("#");
                        Double lat = Double.parseDouble(splitString[0].substring(17));
                        Double lon = Double.parseDouble(splitString[1].substring(11));
                        helpList.add(new LatLng(lat,lon));
                    } else {
                        color = raw;
                    }
                }

                if ((helpList.size()>0)&&(!raw.contains(",") || ((i+1)==keyArray.length))){
                    if (polygon) {allPolygons.add(helpList);}
                    else {allRoutes.add(helpList);}
                    draw(helpList, color, polygon);
                    helpList.clear();
                }
            }
        }


        /**
         * BUTTONS
         */
        //Button to start the Route record
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gpsPlay){
                    gpsPlay = true;
                    playButton.setText("Pause");
                    Toast.makeText(MapActivity.this, "Go", Toast.LENGTH_SHORT).show();
                } else {
                    gpsPlay = false;
                    playButton.setText("Play");
                    Toast.makeText(MapActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button to save the Routes and Polygons

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pause search
                playButton.performClick();


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_alert,null);

                mName = (EditText) mView.findViewById(R.id.et2);
                mRoute = (CheckBox) mView.findViewById(R.id.checkBox);
                mPolygon = (CheckBox) mView.findViewById(R.id.checkBox2);
                mSave = (Button) mView.findViewById(R.id.savebtn);
                mCancel = (Button) mView.findViewById(R.id.cancelbtn);

                if (!polyPoints.isEmpty()) {mPolygon.setChecked(true);}

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!mName.getText().toString().isEmpty()&& (mRoute.isChecked()||mPolygon.isChecked())){

                            if (mRoute.isChecked()){
                                editor.putString(mName.getText().toString()+"0000", routeColors.get(lineColor));
                                for (int i=0; i < routePoints.size(); i++) {
                                    String Key = mName.getText().toString()+String.format("%04d", i+1);
                                    String Data = routePoints.get(i).toString();
                                    editor.putString(Key, Data);
                                }
                                editor.commit();
                                Toast.makeText(MapActivity.this, "Route saved", Toast.LENGTH_SHORT).show();
                            }
                            if (mPolygon.isChecked()){
                                editor.putString("#"+mName.getText().toString()+"0000", polygonColors.get(polygonColor));
                                for (int i=0; i < polyPoints.size(); i++) {
                                    String Key = "#"+mName.getText().toString()+String.format("%04d", i+1);
                                    String Data = polyPoints.get(i).toString();
                                    editor.putString(Key, Data);
                                }
                                editor.commit();
                                Toast.makeText(MapActivity.this, "Polygon saved", Toast.LENGTH_SHORT).show();

                                //clear polyPoints for next polygon and add it
                                allPolygons.add(polyPoints);
                                polyPoints.clear();
                                polyCountPoints = 0;
                            }

                            dialog.cancel();
                        }else {
                            Toast.makeText(MapActivity.this, "please fill any empty fields", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //clear polyPoints for next polygon
                        polyPoints.clear();
                        polyCountPoints = 0;
                        Toast.makeText(MapActivity.this, "polyPoints cleared", Toast.LENGTH_SHORT).show();

                        dialog.cancel();
                    }
                });

            }
        });


        /**
         * Initialize GPS
         */
        lManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        lListener = new LocationListener() {
            @Override
            /**
             * @param location Location
             */
            public void onLocationChanged(Location location) {
                if (location != null){
                    //actual position
                    final LatLng actualPoint =
                            new LatLng(location.getLatitude(), location.getLongitude());

                    //MapBox
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(MapboxMap mapboxMap) {

                            //MARKER
                            //delete Marker, if one exists
                            if(!mapboxMap.getMarkers().isEmpty() && !mapboxMap.getMarkers().get(0).isInfoWindowShown()) {
                                mapboxMap.getMarkers().get(0).remove();
                            }
                            if (mapboxMap.getMarkers().isEmpty()){
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(actualPoint)
                                        .title(getResources().getString(R.string.marker_title))
                                        .snippet(getLocationString(actualPoint)));
                                //CAMERAPOSITION
                                CameraPosition camPos = new CameraPosition.Builder()
                                        .target(actualPoint)
                                        .build();
                                //Camera moves smooth to actual position
                                mapboxMap.easeCamera(CameraUpdateFactory.newCameraPosition(camPos),
                                        cameraSmooth);
                            }


                            //ROUTE
                            if (gpsPlay) {
                                routePoints.add(actualPoint);
                                //first point
                                if (points[1] == null) {
                                    //if points[] is empty, the first point will put in both
                                    points[0] = actualPoint;
                                } else {
                                    //old point put in 0
                                    points[0] = points[1];
                                }

                                //new point put in 1
                                points[1] = actualPoint;
                                // Draw Points on MapView
                                mapboxMap.addPolyline(new PolylineOptions()
                                        .add(points)
                                        .color(Color.parseColor(routeColors.get(lineColor))) //!!!
                                        .width(lineWidth));


                                //POLYGON
                                int polygonCounter = 0; //number of points out of offset

                                //search through the field of points
                                for (int i = routePoints.size() - 2; i >= 0; i--) {
                                    LatLng temp = routePoints.get(i);
                                    //Deltas
                                    double dLat = Math.abs(temp.getLatitude() - actualPoint.getLatitude());
                                    double dLong = Math.abs(temp.getLongitude() - actualPoint.getLongitude());

                                    //Trigger
                                    if ((dLat > offset) || (dLong > offset)) {
                                        polygonCounter++;
                                    }

                                    //Polygon found
                                    if ((dLat < offset) && (dLong < offset) && (polygonCounter > polyCountPoints)) {
                                        Toast.makeText(MapActivity.this, "Polygon", Toast.LENGTH_SHORT).show();
                                        //create polygon
                                        for (int j = routePoints.size() - 1; j >= i; j--) {
                                            polyPoints.add(routePoints.get(j));
                                        }

                                        //draw Polygon
                                        mapboxMap.addPolygon(new PolygonOptions()
                                                .addAll(polyPoints)
                                                .fillColor(Color.parseColor(polygonColors.get(lineColor))));

                                        //call save
                                        saveButton.performClick();

                                        //end the search
                                        break;
                                    }
                                }
                            }
                        }//end of onMapReady
                    });//getMapAsync
                } else {
                    Log.wtf(TAG, "Null Location");
                }
            }//end onLocationChanged

            @Override
            /**
             * @param provider  String
             * @param status    int
             * @param extras    Bundle
             */
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged");
            }

            @Override
            /**
             * @param provider String
             */
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled");
            }

            @Override
            /**
             * @param provider String
             */
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled");
            }
        };

        //RequestCode 1
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }




    private void draw(final LinkedList<LatLng> list , final String color, final Boolean poly) {
        final LinkedList<LatLng> myList = new LinkedList<>(list);
        final String myColor = new String(color);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                if (poly) {
                    mapboxMap.addPolygon(new PolygonOptions()
                            .addAll(myList)
                            .fillColor(Color.parseColor(polygonColors.get(lineColor))));
                } else {
                    mapboxMap.addPolyline(new PolylineOptions()
                            .addAll(myList)
                            .color(Color.parseColor(routeColors.get(lineColor)))
                            .width(lineWidth));
                }
            }
        });
    }




    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        try {
            //Update Loop
            lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateTime, 0, lListener);
        } catch (SecurityException e){
            Log.e(TAG, "SecurityException in onStart was thrown");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        lManager.removeUpdates(lListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    /**
     * @param outState Bundle
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    /**
     * @param requestCode   int
     * @param permissions   String[]
     * @param grantResults  int[]
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 1:
                if ( (grantResults.length > 0) &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED) ) {
                    try {
                        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                updateTime, 0, lListener);
                    } catch (SecurityException e) {
                        Log.e(TAG, "SecurityException in onRequestPermissionsResult was thrown");
                    }
                }
                break;
            default:
                Log.wtf(TAG, "No other RequestCode is implemented");
        }
    }

    @Override
    public void onBackPressed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent backIntent = new Intent(MapActivity.this,HomeActivity.class);
                startActivity(backIntent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        },50);
    }

    private String getLocationString(LatLng latlng){
        String sLat,sLon;
        String[] help = latlng.toString().split(",");
        double lat = Double.parseDouble(help[0].substring(17));
        double lon = Double.parseDouble(help[1].substring(11));
        if (lat>0) sLat=" N"; else sLat=" S";
        if (lon>0) sLon=" O"; else sLon=" W";

        return String.format("%.4f", Math.abs(lat))+sLat+"\n"+String.format("%.4f", Math.abs(lon))+sLon;
    }

}
