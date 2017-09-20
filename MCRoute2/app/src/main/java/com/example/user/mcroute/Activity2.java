package com.example.user.mcroute;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.LinkedList;


//MAIN

/**
 * MainActivity for the Map
 */
public class Activity2 extends AppCompatActivity {
    private MapView mapView;

    /**
     * for GPS position
     */
    public LocationManager lManager;
    public LocationListener lListener;

    /**
     * drawing parameters
     */
    public LatLng[] points = new LatLng[2]; //Array of two points for route draw
    LinkedList<LatLng> allPoints = new LinkedList<>(); //List of all points for polygon draw

    /**
     * const parameters
     */
    final static int updateTime = 1000; //in ms
    final static int polyCountPoints = 25000/updateTime;
    final static int cameraSmooth = 1000; //in ms
    final static int lineWidth = 5; //for draw route
    final static String lineColor = "#ff38afea"; //color of the route
    final static String polygonColor = "#7f3bb2d0"; //color of the polygon
    final static double offset = 0.00005; //measuring inaccuracy for polygon calculating (0.00005 = 3.5m)
    private static final String TAG = MainActivity.class.getName(); //Tagging for Logging

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
        setContentView(R.layout.activity_2);

        /**
         * Initialize a mapView
         */
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        /**
         * set mapView style
         */
        mapView.setStyleUrl(Style.SATELLITE_STREETS);
        //mapView.setStyleUrl(Style.TRAFFIC_NIGHT);
        //mapView.setStyleUrl(Style.TRAFFIC_DAY);
        //mapView.setStyleUrl(Style.DARK);
        //mapView.setStyleUrl(Style.LIGHT);
        //mapView.setStyleUrl(Style.OUTDOORS);
        //mapView.setStyleUrl(Style.MAPBOX_STREETS); //Default

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
                            //CAMERAPOSITION
                            CameraPosition camPos = new CameraPosition.Builder()
                                    .target(actualPoint)
                                    .build();
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
                                    .title("Your Position")
                                    .snippet("You are here"));

                            //Draw Line
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

                            //POLYGON
                            allPoints.add(actualPoint);
                            //search through all Points

                            //temporary vars
                            int polygoncounter = 0; //number of points out of offset

                            //search through the field of points
                            for (int i = allPoints.size() - 2; i>=0; i--) {
                                LatLng temp = allPoints.get(i);
                                //Deltas
                                double dlat = Math.abs(temp.getLatitude() - actualPoint.getLatitude());
                                double dlong = Math.abs(temp.getLongitude() - actualPoint.getLongitude());

                                //Trigger
                                if((dlat > offset) || (dlong > offset)) { polygoncounter++; }


                                //Polygon found
                                if ((dlat < offset) && (dlong < offset) && (polygoncounter>polyCountPoints)) {

                                    Toast.makeText(Activity2.this, "Polygon", Toast.LENGTH_SHORT).show();
                                    //create polygon
                                    LinkedList<LatLng> polyPoints = new LinkedList<>();
                                    for (int j = allPoints.size()-1; j>=i; j--) {
                                        polyPoints.add(allPoints.get(j));
                                    }
                                    //draw Polygon
                                    mapboxMap.addPolygon(new PolygonOptions()
                                            .addAll(polyPoints)
                                            .fillColor(Color.parseColor(polygonColor)));
                                    //clear polyPoints for next polygon
                                    polyPoints.clear();
                                    //allPoints.clear();
                                    //allPoints.add(actualPoint);
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

}
