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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * MapBox Imports
 */
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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

    /**
     * for GPS position
     */
    public LocationManager lManager;
    public LocationListener lListener;

    /**
     * save with SharedPreferences
     */
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    /**
     * drawing parameters
     */
    public LatLng[] points = new LatLng[2]; //Array of two points for route draw
    LinkedList<LatLng> polyPoints = new LinkedList<>(); //List of all points for polygon draw
    LinkedList<LatLng> allPoints = new LinkedList<>(); //List of all points

    /**
     * const parameters
     */
    static int updateTime = 1000; //in ms
    final static int polyCountPoints = 25000/updateTime;
    final static int cameraSmooth = 1000; //in ms
    final static int lineWidth = 5; //for draw route
    final static String lineColor = "#ff38afea"; //color of the route
    final static String polygonColor = "#7f3bb2d0"; //color of the polygon
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
        //updateTime
        String prefPingKey = getString(R.string.preference_ping_key);
        String prefPingDefault = getString(R.string.preference_ping_default);
        String ping = sPrefs.getString(prefPingKey,prefPingDefault);
        updateTime = Integer.parseInt(ping);
        //Style
        String prefStyleKey = getString(R.string.preference_style_key);
        String prefStyleDefault = getString(R.string.preference_style_default);
        String style = sPrefs.getString(prefStyleKey,prefStyleDefault);
        mapView.setStyleUrl(styles.get(style));

        /**
         * set preferences
         */
        preferences = getSharedPreferences("GPSFile", Context.MODE_PRIVATE);
        editor = preferences.edit();

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
                    Toast.makeText(MapActivity.this, "gpsPlay = true", Toast.LENGTH_SHORT).show();
                } else {
                    gpsPlay = false;
                    playButton.setText("Play");
                    Toast.makeText(MapActivity.this, "gpsPlay = false", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button to save the Routes and Polygons

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 for (int i=0; i<allPoints.size(); i++) {
                 String Key = "mydefault" + Integer.toString(i);
                 String Data = allPoints.get(i).toString();
                 editor.putString(Key, Data);
                 }
                 editor.commit();


                if (!allPoints.isEmpty()) {
                    Toast.makeText(MapActivity.this, allPoints.get(0).toString(), Toast.LENGTH_LONG).show();
                }
                if (!preferences.getAll().isEmpty()){
                    Toast.makeText(MapActivity.this, "!Empty", Toast.LENGTH_SHORT).show();
                }
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
                    if (gpsPlay) {
                        allPoints.add(actualPoint);
                    }

                    //MapBox
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(MapboxMap mapboxMap) {

                            //CAMERAPOSITION
                            CameraPosition camPos = new CameraPosition.Builder()
                                    .target(actualPoint)
                                    .build();
                            //Camera moves smooth to actual position
                            mapboxMap.easeCamera(CameraUpdateFactory.newCameraPosition(camPos),
                                    cameraSmooth);

                            //MARKER
                            //delete Marker, if one exists
                            if(!mapboxMap.getMarkers().isEmpty()) {
                                mapboxMap.getMarkers().get(0).remove();
                            }

                            //create new Marker
                            mapboxMap.addMarker(new MarkerOptions()
                                    .position(actualPoint)
                                    .title(getResources().getString(R.string.marker_title))
                                    .snippet(getResources().getString(R.string.marker_snippet)));

                            //ROUTE
                            if (gpsPlay) {
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
                                        .color(Color.parseColor(lineColor))
                                        .width(lineWidth));
                            }


                            //POLYGON
                            //temporary vars
                            int polygonCounter = 0; //number of points out of offset

                            //search through the field of points
                            for (int i = allPoints.size() - 2; i>=0; i--) {
                                LatLng temp = allPoints.get(i);
                                //Deltas
                                double dLat = Math.abs(temp.getLatitude() - actualPoint.getLatitude());
                                double dLong = Math.abs(temp.getLongitude() - actualPoint.getLongitude());

                                //Trigger
                                if((dLat > offset) || (dLong > offset)) {
                                    polygonCounter++;
                                }

                                //Polygon found
                                if ((dLat < offset) && (dLong < offset) && (polygonCounter > polyCountPoints)) {
                                    Toast.makeText(MapActivity.this, "Polygon", Toast.LENGTH_SHORT).show();
                                    //create polygon
                                    for (int j = allPoints.size() - 1; j >= i; j--) {
                                        polyPoints.add(allPoints.get(j));
                                    }

                                    //draw Polygon
                                    mapboxMap.addPolygon(new PolygonOptions()
                                            .addAll(polyPoints)
                                            .fillColor(Color.parseColor(polygonColor)));

                                    //clear polyPoints for next polygon
                                    polyPoints.clear();

                                    //end the search
                                    break;
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

}
